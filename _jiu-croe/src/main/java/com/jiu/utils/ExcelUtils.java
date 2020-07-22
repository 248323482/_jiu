package com.jiu.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExcelUtils {

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response) {
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);

    }

    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }

    public static void exportExcel(List <?> list, Class <?> pojoClass, String fileName, ExportParams exportParams, HttpServletResponse response) {
        defaultExport(list, pojoClass, fileName, response, exportParams);
    }

    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        defaultExport(list, fileName, response);
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        if (workbook != null) ;
        downLoadExcel(fileName, response, workbook);
    }

    public static Workbook getWorkbook(List<?> list, Class<?> pojoClass, String title, String sheetName, ExportParams exportParams) {
        return ExcelExportUtil.exportExcel(new ExportParams(title, sheetName), pojoClass, list);
    }

    public static void exportExcelZip(Map<String, Workbook> map, String fileDir, HttpServletResponse response) throws IOException {

        if (MapUtils.isEmpty(map)) {
            return;
        }

        // 多个文件
        List<File> files = new ArrayList<>();

        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String fileName = iterator.next();
            Workbook workbook = map.get(fileName);

            //创建文件夹
            File file = new File(fileDir);
            if (!file.exists()) {
                file.mkdirs();
            }

            String filePath = fileDir + File.separator + fileName;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            workbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            files.add(new File(filePath));
        }

        String zipName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".zip";
        String zipFilePath = fileDir + File.separator + zipName;
        //压缩多个excel文件为.zip格式并删除
        zipFiles(files, zipFilePath);
        deleteFiles(files);
        //下载.zip格式文件并删除
        downFile(response, zipFilePath, zipName);
        deleteZip(new File(zipFilePath));

    }

    /**
     * 将多个Excel打包成zip文件
     *
     * @param srcfile
     * @param zipFilePath
     */
    private static void zipFiles(List<File> srcfile, String zipFilePath) {
        byte[] buf = new byte[1024];
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFilePath));
            for (int i = 0; i < srcfile.size(); i++) {
                File file = srcfile.get(i);
                FileInputStream in = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除多个文件方法
     *
     * @param srcfile
     */
    private static void deleteFiles(List<File> srcfile) {
        for (File file : srcfile) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private static void downFile(HttpServletResponse response, String filePath, String fileName) {
        try {
            if (new File(filePath).exists()) {
                InputStream ins = new FileInputStream(filePath);
                BufferedInputStream bins = new BufferedInputStream(ins);// 放到缓冲流里面
                OutputStream outs = response.getOutputStream();// 获取文件输出IO流
                BufferedOutputStream bouts = new BufferedOutputStream(outs);
                response.setContentType("application/x-download");// 设置response内容的类型
                response.setHeader(
                        "Content-disposition",
                        "attachment;filename="
                                + URLEncoder.encode(fileName, "UTF-8"));
                // 设置头部信息
                response.addHeader("Access-Control-Expose-Headers", "ajax-mimeType, ajax-filename");
                //response.setHeader("Content-Disposition", "attachment; filename="+fName);
                response.setHeader("ajax-mimeType", "");
                response.setHeader("ajax-filename", URLEncoder.encode(fileName, "UTF-8"));
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                //开始向网络传输文件流
                while ((bytesRead = bins.read(buffer, 0, 8192)) != -1) {
                    bouts.write(buffer, 0, bytesRead);
                }
                bouts.flush();
                ins.close();
                bins.close();
                outs.close();
                bouts.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除zip
     */
    private static void deleteZip(File path) {
        if (path.exists()) {
            path.delete();
        }
    }

    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.addHeader("Access-Control-Expose-Headers", "ajax-mimeType, ajax-filename");
            //response.setHeader("Content-Disposition", "attachment; filename="+fName);
            response.setHeader("ajax-mimeType", "");
            response.setHeader("ajax-filename", URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/octet-stream; charset=utf-8");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if (workbook != null) ;
        downLoadExcel(fileName, response, workbook);
    }

    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("模板不能为空");
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        }
        return list;
    }

    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("excel文件不能为空");
        } catch (Exception e) {
            throw new NoSuchElementException(e.getMessage());
        }
        return list;
    }
}
