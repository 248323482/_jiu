package com.jiu.database.sync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Administrator
 * @create 2020/10/29 9:59
 */
@Slf4j
public class BinlogClientRunner implements CommandLineRunner {
    @Value("${binlog.host:budongi.club}")
    private String host;

    @Value("${binlog.port:3306}")
    private int port;

    @Value("${binlog.user:root}")
    private String user;

    @Value("${binlog.password:123456}")
    private String password;
    @Value("${server.id:1}")
    private long serverId;
    // 指定监听的数据表
    @Value("${binlog.database.table:jiu.f_file}")
    private String database_table;

    @Override
    @Async
    public void run(String... args) throws Exception {
        log.info("binlog数据获取异步启动中.......");
        // 创建binlog监听客户端
        BinaryLogClient client = new BinaryLogClient(host, port, user, password);
        // 获取监听数据表数组
        List<String> databaseList = Arrays.asList(database_table.split(","));

        HashMap<Long, String> tableMap = new HashMap<Long, String>();
        client.setServerId(serverId);
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
        );
        client.registerEventListener((event -> {
            //注冊监听binlog事件
            EventData data = event.getData();
            //获得事件
            if (data != null) {
                if (data instanceof TableMapEventData) {
                    TableMapEventData tableMapEventData = (TableMapEventData) data;
                    tableMap.put(tableMapEventData.getTableId(), tableMapEventData.getDatabase() + "." + tableMapEventData.getTable());
                } else if (data instanceof UpdateRowsEventData) {
                    //更新事件
                    UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) data;
                    String tableName = tableMap.get(updateRowsEventData.getTableId());
                    if (tableName != null && databaseList.contains(tableName)) {
                        //需要同步的表
                        System.err.println(JSONObject.toJSONString(updateRowsEventData.getIncludedColumns()));
                        for (Map.Entry<Serializable[], Serializable[]> row : updateRowsEventData.getRows()) {
                            String eventKey = tableName + ".update";
                            String msg = JSON.toJSONString(new BinlogDto(eventKey, row.getValue()));
                            log.info("binlog数据{}", msg);
                            log.info("binlog数据{}", getESObject(JSON.toJSONString(row.getValue())));
                        }
                    }
                } else if (data instanceof WriteRowsEventData) {
                    //新增操作
                    WriteRowsEventData writeRowsEventData = (WriteRowsEventData) data;
                    String tableName = tableMap.get(writeRowsEventData.getTableId());
                    if (tableName != null && databaseList.contains(tableName)) {
                        String eventKey = tableName + ".insert";
                        for (Serializable[] row : writeRowsEventData.getRows()) {
                            String msg = JSON.toJSONString(new BinlogDto(eventKey, row));
                            log.info("binlog数据{}", msg);
                        }
                    }
                } else if (data instanceof DeleteRowsEventData) {
                    //删除操作
                    DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) data;
                    String tableName = tableMap.get(deleteRowsEventData.getTableId());
                    if (tableName != null && databaseList.contains(tableName)) {
                        String eventKey = tableName + ".delete";
                        for (Serializable[] row : deleteRowsEventData.getRows()) {
                            String msg = JSON.toJSONString(new BinlogDto(eventKey, row));
                            log.info("binlog数据{}", msg);
                        }
                    }
                }
            }

        }));
        client.connect();
    }


    private JSONObject getESObject(String obj) {
        JSONArray message = (JSONArray) JSONObject.parse(obj);
        JSONObject resultObject = new JSONObject();
        String format = "{\"id\":\"0\",\"name\":\"1\"}";
        if (!format.isEmpty()) {
            JSONObject jsonFormatObject = JSON.parseObject(format);

            for (String key : jsonFormatObject.keySet()) {
                String[] formatValues = jsonFormatObject.getString(key).split(",");
                if (formatValues.length < 2) {
                    resultObject.put(key, message.get(jsonFormatObject.getInteger(key)));
                } else {
                    Object object = message.get(Integer.parseInt(formatValues[0]));
                    if (object == null) {
                        String[] array = {};
                        resultObject.put(key, array);
                    } else {
                        String objectStr = message.get(Integer.parseInt(formatValues[0])).toString();
                        String[] result = objectStr.split(formatValues[1]);
                        resultObject.put(key, result);
                    }
                }
            }
        }
        return resultObject;
    }

}
