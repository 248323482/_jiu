package com.jiu.datasource.rout.core;

import com.jiu.datasource.rout.entity.database.AbstractDataBase;
import com.jiu.datasource.rout.entity.database.DataBaseMaster;
import com.jiu.datasource.rout.entity.database.DataBaseSlave;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public abstract class DynamicDataSource<T extends DataSource> extends AbstractRoutingDataSource {
	private static final ThreadLocal<DatabaseType> contextHolder = new ThreadLocal<DatabaseType>();
	private static final String  SEPARATOR ="_";

	/**
	 * atomicInteger 用于记录轮询选择数据库
	 */
	private final Map<String, DatabaseIndex> mapIndex = new HashMap<>();
	/** 数据源KEY-VALUE键值对 */
	private Map<Object, Object> targetDataSources;
	private DataBaseMaster dataBaseMaster;
	private DataBaseSlave dataBaseSlave;

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		log.info(targetDataSources.toString());
		this.targetDataSources = targetDataSources;
		super.setTargetDataSources(targetDataSources);
		super.afterPropertiesSet();
	}
	public Map<Object, Object> getTargetDataSources() {
		return this.targetDataSources;
	}
	
	/**
	 * 创建数据源
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public abstract T createDataSource(String driverClassName, String url, String username, String password);

	@Override
	protected Object determineCurrentLookupKey() {
		DatabaseType databaseType = contextHolder.get();
		if(databaseType==null) {
			databaseType = DatabaseType.Master;
		}


		String dbkey = "";
		Integer sub_dbIndex=0;
		dbkey = dbkey+SEPARATOR+sub_dbIndex;
		DatabaseIndex databaseIndex = mapIndex.get(databaseType+SEPARATOR+sub_dbIndex);
		synchronized (mapIndex) {
			Integer index = databaseIndex.getIndex();
			index++;
			index = index % databaseIndex.length;
			databaseIndex.setIndex(index);
			dbkey=dbkey+SEPARATOR+index;
		}


		Object verifyAndInitDataSource = this.verifyAndInitDataSource(dbkey,databaseType);
		log.info("当前数据源为[{}]库", verifyAndInitDataSource);
		 return verifyAndInitDataSource;
	}

	
	
	/**
	 *检查并初始化数据源 
	 */
	private Object verifyAndInitDataSource(String dbkey, DatabaseType databaseType) {
		String name= databaseType.name()+"_"+dbkey;
		Object obj = this.targetDataSources.get(name);
		if (obj != null) {
			return name; 
		}else{
			AbstractDataBase.DataBase dataBase = chooseDataBase(databaseType,dbkey);
			//创建数据源
			T dataSource = createDataSource("",dataBase.getUrl() ,dataBase.getUsername() ,dataBase.getPassword() );
			this.addTargetDataSource(name, dataSource);
		}
		return name;
	}

	/**
	 *
	 * @param databaseType   主/从  类型
	 * @param dbkey
	 * @return
	 */
	private AbstractDataBase.DataBase chooseDataBase(DatabaseType databaseType, String dbkey) {
		// 数据库名称_分库下表_轮训下标
		String[] dx = dbkey.split(SEPARATOR);
		//从 [分库 1][ ]
		String db =  dx[0]; // 数据库
		Integer index = Integer.valueOf(dx[1]);//第几个分库
		Integer train_index = Integer.valueOf(dx[2]);//轮训下标，读库有效
		
		
		//数据源不存在创建数据源
		if(databaseType==DatabaseType.Master){
			//主库
			AbstractDataBase.DataBase[][] database = dataBaseMaster.getDatabase();
			return  database[index][0];

		}else{
			//从库
			AbstractDataBase.DataBase[][] database = dataBaseSlave.getDatabase();
			return  database[index][train_index];
		}

	}

	/**
	 * 添加数据源
	 */
	public void addTargetDataSource(String key, T dataSource) {
		this.targetDataSources.put(key, dataSource);
		super.setTargetDataSources(this.targetDataSources);
		super.afterPropertiesSet();
	}




	/**
	 * 读写
	 * @By        九
	 * @Date   2019年8月20日
	 * @Time   上午11:23:37
	 * @Email budongilt@gmail.com
	 */
	public static enum DatabaseType {
		Master, Slave
	}

	public static void master() {
		contextHolder.set(DatabaseType.Master);
	}

	public static void slave() {
		contextHolder.set(DatabaseType.Slave);
	}

	public static void setDatabaseType(DatabaseType type) {
		contextHolder.set(type);
	}

	public static DatabaseType getType() {
		return contextHolder.get();
	}

	@Autowired
	public void setDataBaseMaster(DataBaseMaster dataBaseMaster) {
		this.dataBaseMaster = dataBaseMaster;
		AbstractDataBase.DataBase[][] database = dataBaseMaster.getDatabase();
		for(int i=0;i<database.length;i++){
			DatabaseIndex databaseIndex = new DatabaseIndex();
			databaseIndex.setIndex(0);
			databaseIndex.setLength(database[i].length);
			mapIndex.put(DatabaseType.Master+SEPARATOR+i,databaseIndex);
		}
	}
	@Autowired
	public void setDataBaseSlave(DataBaseSlave dataBaseSlave) {
		this.dataBaseSlave = dataBaseSlave;
		AbstractDataBase.DataBase[][] database = dataBaseSlave.getDatabase();
		for(int i=0;i<database.length;i++){
			DatabaseIndex databaseIndex = new DatabaseIndex();
			databaseIndex.setIndex(0);
			databaseIndex.setLength(database[i].length);
			mapIndex.put(DatabaseType.Slave+SEPARATOR+i,databaseIndex);
		}
	}

	@Data
	private final class DatabaseIndex {
		Integer index = 0;
		Integer length = 0;
	}
	
	
}
