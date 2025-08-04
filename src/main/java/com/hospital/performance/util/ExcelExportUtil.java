package com.hospital.performance.util;

import com.hospital.performance.entity.PerformanceData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Excel导出工具类
 */
@Slf4j
@Component
public class ExcelExportUtil {

    /**
     * 导出绩效数据到Excel
     */
    public byte[] exportPerformanceData(List<PerformanceData> dataList, String title) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("绩效数据");
            
            // 创建标题样式
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            
            int rowNum = 0;
            
            // 创建标题行
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);
            
            // 合并标题单元格
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 11));
            
            // 空行
            rowNum++;
            
            // 创建表头
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                "数据期间", "部门名称", "姓名", "指标编码", "指标名称", 
                "指标值", "目标值", "完成率", "得分", "绩效金额", 
                "统计口径", "状态"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据
            for (PerformanceData data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                
                dataRow.createCell(0).setCellValue(data.getDataPeriod());
                dataRow.createCell(1).setCellValue(data.getDeptName());
                dataRow.createCell(2).setCellValue(data.getUserName() != null ? data.getUserName() : "");
                dataRow.createCell(3).setCellValue(data.getIndicatorCode());
                dataRow.createCell(4).setCellValue(data.getIndicatorName());
                
                // 数值类型的单元格
                setCellNumericValue(dataRow.createCell(5), data.getIndicatorValue(), numberStyle);
                setCellNumericValue(dataRow.createCell(6), data.getTargetValue(), numberStyle);
                setCellNumericValue(dataRow.createCell(7), data.getCompletionRate(), numberStyle);
                setCellNumericValue(dataRow.createCell(8), data.getScore(), numberStyle);
                setCellNumericValue(dataRow.createCell(9), data.getPerformanceAmount(), numberStyle);
                
                dataRow.createCell(10).setCellValue(getStatisticsTypeName(data.getStatisticsType()));
                dataRow.createCell(11).setCellValue(getStatusName(data.getStatus()));
                
                // 应用数据样式
                for (int i = 0; i < 12; i++) {
                    if (i >= 5 && i <= 9) {
                        dataRow.getCell(i).setCellStyle(numberStyle);
                    } else {
                        dataRow.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 设置最小列宽
                if (sheet.getColumnWidth(i) < 2000) {
                    sheet.setColumnWidth(i, 2000);
                }
            }
            
            // 添加导出信息
            Row infoRow = sheet.createRow(rowNum + 1);
            infoRow.createCell(0).setCellValue("导出时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            infoRow.createCell(4).setCellValue("总记录数：" + dataList.size());
            
            // 转换为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 导出多口径统计数据到Excel
     */
    public byte[] exportMultiDimensionStats(List<Map<String, Object>> dataList, String title) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("多口径统计");
            
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            
            int rowNum = 0;
            
            // 创建标题行
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);
            
            // 合并标题单元格
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7));
            
            // 空行
            rowNum++;
            
            // 创建表头
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                "统计期间", "统计口径", "部门名称", "指标名称", 
                "指标值", "目标值", "完成率", "绩效金额"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据
            for (Map<String, Object> data : dataList) {
                Row dataRow = sheet.createRow(rowNum++);
                
                dataRow.createCell(0).setCellValue(getString(data, "period"));
                dataRow.createCell(1).setCellValue(getStatisticsTypeName(getInteger(data, "statisticsType")));
                dataRow.createCell(2).setCellValue(getString(data, "deptName"));
                dataRow.createCell(3).setCellValue(getString(data, "indicatorName"));
                
                setCellNumericValue(dataRow.createCell(4), getBigDecimal(data, "indicatorValue"), numberStyle);
                setCellNumericValue(dataRow.createCell(5), getBigDecimal(data, "targetValue"), numberStyle);
                setCellNumericValue(dataRow.createCell(6), getBigDecimal(data, "completionRate"), numberStyle);
                setCellNumericValue(dataRow.createCell(7), getBigDecimal(data, "performanceAmount"), numberStyle);
                
                // 应用样式
                for (int i = 0; i < 8; i++) {
                    if (i >= 4 && i <= 7) {
                        dataRow.getCell(i).setCellStyle(numberStyle);
                    } else {
                        dataRow.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < 2000) {
                    sheet.setColumnWidth(i, 2000);
                }
            }
            
            // 添加导出信息
            Row infoRow = sheet.createRow(rowNum + 1);
            infoRow.createCell(0).setCellValue("导出时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            infoRow.createCell(3).setCellValue("总记录数：" + dataList.size());
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 创建标题样式
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }

    /**
     * 创建数据样式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }

    /**
     * 创建数字样式
     */
    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置数字格式
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        
        return style;
    }

    /**
     * 设置单元格数值
     */
    private void setCellNumericValue(Cell cell, BigDecimal value, CellStyle style) {
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }

    /**
     * 获取统计口径名称
     */
    private String getStatisticsTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "开单医生所在科";
            case 2: return "执行医生所在科";
            case 3: return "开单科室对应护理单元";
            case 4: return "患者所在科室";
            case 5: return "收费科室";
            default: return "未知";
        }
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "草稿";
            case 2: return "已确认";
            case 3: return "已发布";
            default: return "未知";
        }
    }

    /**
     * 从Map中获取字符串值
     */
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * 从Map中获取整数值
     */
    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    /**
     * 从Map中获取BigDecimal值
     */
    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return null;
    }
}