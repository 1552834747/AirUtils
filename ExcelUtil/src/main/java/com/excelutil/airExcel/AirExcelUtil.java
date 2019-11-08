package com.excelutil.airExcel;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class AirExcelUtil<T> {

    private Class<T> clazz;

    private Workbook workbook;

    private DataFormat dataFormat;

    private Sheet sheet;

    //标题样式
    private CellStyle titleCellStyle;

    public AirExcelUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Excel导入
     */
    public List<T> importExcel(String sheetName, String filename, InputStream input) {
        List<T> list = new ArrayList<>();
        try {
            Workbook workbook = isExcel2003(filename) ? new HSSFWorkbook(input) : new XSSFWorkbook(input);
            Sheet sheet = getSheet(workbook, sheetName);
            List<Field> fields = getFieldsByAnnotation(AirCell.class);
            TitleStyle titleStyle = clazz.getAnnotation(TitleStyle.class);
            int titleRow = titleStyle != null ? titleStyle.title().length : 0;
            int nowRow = 1 + titleRow;        //开始的行数 从0开始
            int rows = sheet.getPhysicalNumberOfRows();
            for (int i = nowRow; i < rows; i++) {
                T entity = clazz.newInstance();
                Row row = sheet.getRow(i);
                if (row != null) {
                    setEntity(row, entity, fields);
                    list.add(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 导出
     */
    public boolean exportExcel(List<T> list, String sheetName, String filename, OutputStream output) throws IllegalAccessException {
        //创建excel对象
        workbook = isExcel2003(filename) ? new HSSFWorkbook() : new XSSFWorkbook();
        dataFormat = workbook.getCreationHelper().createDataFormat();
        sheet = workbook.createSheet(sheetName);      //工作表
        titleCellStyle = createTitleStyle();
        int nowRow = 0;        //开始的行数 从0开始
        List<Field> fields = getFieldsByAnnotation(AirCell.class);
        int listMaxSize = getListMaxSize(fields, list);
        //创建列头名称
        nowRow = setCellTitle(nowRow, fields, listMaxSize);
        //填充数据
        for (T entity : list) {
            Row row = sheet.createRow(nowRow++);
            setExcel(row, entity, fields);
        }
        try {
            output.flush();
            workbook.write(output);
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("导出   失败");
            return false;
        }
    }

    /**
     * 创建列头
     */
    private int setCellTitle(int nowRow, List<Field> fields, int listMaxSize) {
        TitleStyle titleStyle = clazz.getAnnotation(TitleStyle.class);
        if (titleStyle != null) {
            String[] titles = titleStyle.title();
            for (int i = 0; i < titles.length; i++) {
                Row row = sheet.createRow(nowRow);
                CellRangeAddress region = new CellRangeAddress(nowRow, nowRow++, 0, fields.size()); //设置合并单元格范围对象
                sheet.addMergedRegion(region);//合并单元格
                Cell cell = row.createCell(0);
                cell.setCellValue(titles[i]);
                cell.setCellStyle(titleCellStyle);
            }
        }
        Row row = sheet.createRow(nowRow++);
        row.setHeight(titleStyle != null ? titleStyle.titleHeight() : -1);
        for (Field field : fields) {
            AirCell airCell = field.getAnnotation(AirCell.class);
            int column = airCell.column();
            String[] names = airCell.names();
            int[] titleWidths = airCell.titleWidths();
            if (!airCell.isList()) {
                Cell cell = row.createCell(column);
                cell.setCellValue(names[0]);
                sheet.setColumnWidth(column, titleWidths[0]);
                cell.setCellStyle(titleCellStyle);
            } else {
                for (int i = 0; i < listMaxSize; i++) {
                    for (int n = 0; n < names.length; n++) {
                        Cell cell = row.createCell(column);
                        cell.setCellValue(names[n]);
                        sheet.setColumnWidth(column++, titleWidths[n]);
                        cell.setCellStyle(titleCellStyle);
                    }
                }
            }
        }
        return nowRow;
    }

    /**
     * 导出：数据处理
     */
    private void setExcel(Row row, T entity, List<Field> fields) throws IllegalAccessException {
        for (Field field : fields) {
            AirCell airCell = field.getAnnotation(AirCell.class);
            String dateFormat = airCell.dateFormat();
            int column = airCell.column();
            if (!airCell.isList()) {
                Cell cell = row.createCell(column);
                Object fieldValue = field.get(entity);
                setExcelData(cell, fieldValue, dateFormat);
            } else {
                List<Map<String, Object>> maps = (List<Map<String, Object>>) field.get(entity);
                String[] keys = airCell.keys();
                for (Map<String, Object> map : maps) {
                    for (String key : keys) {
                        Cell cell = row.createCell(column++);
                        setExcelData(cell, map.get(key), dateFormat);
                    }
                }
            }
        }
    }

    /**
     * 导出：按照数据格式写出数据
     */
    private void setExcelData(Cell cell, Object fieldValue, String dateFormat) {
        CellStyle style = createDataStyle();
        if (fieldValue instanceof Date) {
            style.setDataFormat(dataFormat.getFormat(dateFormat));
            cell.setCellValue((Date) fieldValue);
        } else if (fieldValue instanceof String) {
            cell.setCellValue(String.valueOf(fieldValue));
        } else {
            cell.setCellValue(Double.valueOf(String.valueOf(fieldValue)));
        }
        cell.setCellStyle(style);
    }

    /**
     * 导入：封装数据
     */
    private void setEntity(Row row, T entity, List<Field> fields) throws IllegalAccessException {
        for (Field field : fields) {
            AirCell airCell = field.getAnnotation(AirCell.class);
            boolean isList = airCell.isList();
            int column = airCell.column();
            if (!isList) {
                setEntityData(field, entity, row.getCell(column));
            } else {
                String[] keys = airCell.keys();
                List<Map> maps = setEntityData(row, column, keys);
                field.set(entity, maps);
            }
        }
    }

    /**
     * 导入：封装普通参数
     */
    private void setEntityData(Field field, T entity, Cell cell) throws IllegalAccessException {
        if (cell != null) {
            Class<?> type = field.getType();
            if (type == Integer.class || type == int.class) {
                field.set(entity, Integer.valueOf((int) cell.getNumericCellValue()));
                return;
            }
            if (type == Byte.class || type == byte.class) {
                field.set(entity, Byte.valueOf((byte) cell.getNumericCellValue()));
                return;
            }
            if (type == Short.class || type == short.class) {
                field.set(entity, Short.valueOf((short) cell.getNumericCellValue()));
                return;
            }
            if (type == Long.class || type == long.class) {
                field.set(entity, Long.valueOf((long) cell.getNumericCellValue()));
                return;
            }
            if (type == Double.class || type == double.class) {
                field.set(entity, cell.getNumericCellValue());
                return;
            }
            if (type == Float.class || type == float.class) {
                field.set(entity, Float.valueOf((float) cell.getNumericCellValue()));
                return;
            }
            if (type == String.class) {
                field.set(entity, cell.getStringCellValue());
                return;
            }
            if (type == Date.class) {
                field.set(entity, cell.getDateCellValue());
            }
        }
    }

    /**
     * 导入：封装List参数
     */
    private List<Map> setEntityData(Row row, int column, String[] keys) {
        List<Map> maps = new ArrayList<>();     //记录总数据
        Map<String, Object> map = new HashMap<>();
        int key = 1;
        while (true) {
            Cell cell = row.getCell(column++);
            if (cell == null || "".equals(cell.toString().trim())) {
                break;
            }
            map.put(keys[key - 1], cell);
            key++;
            if (key > keys.length) {
                maps.add(map);
                map = new HashMap<>();
                key = 1;
            }
        }
        return maps;
    }

    /**
     * 导出：创建数据样式
     */
    private CellStyle createDataStyle() {
        CellStyle style = workbook.createCellStyle();
        DataStyle dataStyle = clazz.getAnnotation(DataStyle.class);
        if (dataStyle != null) {
            style.setAlignment(dataStyle.alignment());                    //设置水平对齐
            style.setVerticalAlignment(dataStyle.verticalAlignment());    //设置垂直对齐
            style.setWrapText(dataStyle.wrapText());                      //自动换行
        }
        return style;
    }

    /**
     * 导出：创建标题样式
     */
    private CellStyle createTitleStyle() {
        CellStyle style = workbook.createCellStyle();
        TitleStyle titleStyle = clazz.getAnnotation(TitleStyle.class);
        if (titleStyle != null) {
            style.setAlignment(titleStyle.alignment());                    //设置水平对齐
            style.setVerticalAlignment(titleStyle.verticalAlignment());    //设置垂直对齐
            style.setWrapText(titleStyle.wrapText());                      //自动换行
            style.setFillForegroundColor(titleStyle.fillForegroundColor().getIndex());     //前景色
            style.setFillPattern(titleStyle.fillPattern());                    //设置单元格填充样式
            style.setBorderBottom(titleStyle.border());                        //下边框
            style.setBorderLeft(titleStyle.border());                          //左边框
            style.setBorderTop(titleStyle.border());                           //上边框
            style.setBorderRight(titleStyle.border());                         //右边框
            style.setBottomBorderColor(titleStyle.borderColor().getIndex());   //设置边框颜色
            style.setLeftBorderColor(titleStyle.borderColor().getIndex());     //设置边框颜色
            style.setRightBorderColor(titleStyle.borderColor().getIndex());    //设置边框颜色
            style.setTopBorderColor(titleStyle.borderColor().getIndex());      //设置边框颜色
        }
        return style;
    }

    /**
     * 获取带有指定注解的字段
     */
    private List<Field> getFieldsByAnnotation(Class<? extends Annotation> annotationClass) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> collect = Arrays.stream(fields).filter(e -> e.isAnnotationPresent(annotationClass)).peek(e -> e.setAccessible(true)).collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取list中最大的长度值
     */
    private int getListMaxSize(List<Field> fields, List<T> list) throws IllegalAccessException {
        List<Field> collect = fields.stream().filter(e -> e.getAnnotation(AirCell.class).isList()).collect(Collectors.toList());
        int max = 0;
        if (collect != null && collect.size() > 0) {
            Field field = collect.get(0);
            for (T entity : list) {
                List<Map<String, Object>> maps = (List<Map<String, Object>>) field.get(entity);
                max = Math.max(max, maps.size());
            }
        }
        return max;
    }

    /**
     * 根据文件名判断excel的版本
     */
    private static boolean isExcel2003(String filename) {
        return filename.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 得到sheet
     */
    private static Sheet getSheet(Workbook workbook, String sheetName) {
        Sheet sheet;
        if (sheetName != null && !sheetName.trim().equals("")) {
            sheet = workbook.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
        } else {
            sheet = workbook.getSheetAt(0); // 如果传入的sheet名不存在则默认指向第1个sheet.
        }
        return sheet;
    }
}
