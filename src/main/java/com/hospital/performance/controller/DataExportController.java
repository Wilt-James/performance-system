package com.hospital.performance.controller;

import com.hospital.performance.common.Result;
import com.hospital.performance.entity.PerformanceData;
import com.hospital.performance.service.PerformanceDataService;
import com.hospital.performance.service.MultiDimensionStatsService;
import com.hospital.performance.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 数据导出控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class DataExportController {

    private final PerformanceDataService performanceDataService;
    private final MultiDimensionStatsService multiDimensionStatsService;
    private final ExcelExportUtil excelExportUtil;

    /**
     * 导出绩效数据
     */
    @PostMapping("/performance-data")
    public ResponseEntity<byte[]> exportPerformanceData(
            @RequestParam String period,
            @RequestParam(required = false) List<Long> deptIds,
            @RequestParam(required = false) List<Long> userIds,
            @RequestParam(required = false) List<Long> indicatorIds) {
        
        try {
            log.info("开始导出绩效数据: period={}, deptIds={}, userIds={}, indicatorIds={}", 
                    period, deptIds, userIds, indicatorIds);
            
            // 查询数据
            List<PerformanceData> dataList = performanceDataService.exportPerformanceData(
                    period, deptIds, userIds, indicatorIds);
            
            if (dataList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            // 生成Excel文件
            String title = String.format("绩效数据报表 - %s", period);
            byte[] excelData = excelExportUtil.exportPerformanceData(dataList, title);
            
            // 生成文件名
            String fileName = String.format("绩效数据_%s_%s.xlsx", 
                    period, 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                    URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            headers.setContentLength(excelData.length);
            
            log.info("绩效数据导出完成: 记录数={}, 文件大小={}字节", dataList.size(), excelData.length);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            log.error("导出绩效数据失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("导出绩效数据异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 导出多口径统计数据
     */
    @PostMapping("/multi-dimension-stats")
    public ResponseEntity<byte[]> exportMultiDimensionStats(
            @RequestParam String period,
            @RequestParam Integer statisticsType,
            @RequestParam(required = false) List<Long> deptIds,
            @RequestParam(required = false) List<Long> indicatorIds,
            @RequestParam(required = false) List<Long> userIds) {
        
        try {
            log.info("开始导出多口径统计数据: period={}, statisticsType={}, deptIds={}, indicatorIds={}", 
                    period, statisticsType, deptIds, indicatorIds);
            
            // 查询统计数据
            List<PerformanceData> statsData = multiDimensionStatsService.queryMultiDimensionStats(
                    period, statisticsType, deptIds, indicatorIds, userIds);
            
            if (statsData.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            // 转换为导出格式
            List<Map<String, Object>> exportData = convertToExportFormat(statsData, period, statisticsType);
            
            // 生成Excel文件
            String statisticsTypeName = getStatisticsTypeName(statisticsType);
            String title = String.format("多口径统计报表 - %s - %s", period, statisticsTypeName);
            byte[] excelData = excelExportUtil.exportMultiDimensionStats(exportData, title);
            
            // 生成文件名
            String fileName = String.format("多口径统计_%s_%s_%s.xlsx", 
                    period, 
                    statisticsTypeName,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                    URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            headers.setContentLength(excelData.length);
            
            log.info("多口径统计数据导出完成: 记录数={}, 文件大小={}字节", exportData.size(), excelData.length);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            log.error("导出多口径统计数据失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("导出多口径统计数据异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 导出部门绩效分布数据
     */
    @PostMapping("/dept-performance-distribution")
    public ResponseEntity<byte[]> exportDeptPerformanceDistribution(
            @RequestParam String period,
            @RequestParam(required = false) Integer statisticsType,
            @RequestParam(required = false) Integer deptType) {
        
        try {
            log.info("开始导出部门绩效分布数据: period={}, statisticsType={}, deptType={}", 
                    period, statisticsType, deptType);
            
            // 查询部门绩效分布数据
            List<Map<String, Object>> distributionData = multiDimensionStatsService
                    .getDeptPerformanceDistribution(period, statisticsType, deptType);
            
            if (distributionData.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            
            // 生成Excel文件
            String title = String.format("部门绩效分布报表 - %s", period);
            byte[] excelData = excelExportUtil.exportMultiDimensionStats(distributionData, title);
            
            // 生成文件名
            String fileName = String.format("部门绩效分布_%s_%s.xlsx", 
                    period, 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                    URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            headers.setContentLength(excelData.length);
            
            log.info("部门绩效分布数据导出完成: 记录数={}, 文件大小={}字节", distributionData.size(), excelData.length);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            log.error("导出部门绩效分布数据失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("导出部门绩效分布数据异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取导出任务状态
     */
    @GetMapping("/task-status/{taskId}")
    public Result<Map<String, Object>> getExportTaskStatus(@PathVariable String taskId) {
        // 这里可以实现异步导出任务的状态查询
        // 为了演示，返回模拟状态
        Map<String, Object> status = Map.of(
                "taskId", taskId,
                "status", "completed",
                "progress", 100,
                "message", "导出完成",
                "downloadUrl", "/export/download/" + taskId
        );
        
        return Result.success(status);
    }

    /**
     * 下载导出文件
     */
    @GetMapping("/download/{taskId}")
    public ResponseEntity<byte[]> downloadExportFile(@PathVariable String taskId) {
        // 这里可以实现文件下载逻辑
        // 为了演示，返回空响应
        return ResponseEntity.notFound().build();
    }

    /**
     * 转换为导出格式
     */
    private List<Map<String, Object>> convertToExportFormat(List<PerformanceData> statsData, 
                                                           String period, Integer statisticsType) {
        return statsData.stream().map(data -> {
            Map<String, Object> exportItem = new java.util.HashMap<>();
            exportItem.put("period", period);
            exportItem.put("statisticsType", statisticsType);
            exportItem.put("deptName", data.getDeptName());
            exportItem.put("indicatorName", data.getIndicatorName());
            exportItem.put("indicatorValue", data.getIndicatorValue());
            exportItem.put("targetValue", data.getTargetValue());
            exportItem.put("completionRate", data.getCompletionRate());
            exportItem.put("performanceAmount", data.getPerformanceAmount());
            return exportItem;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取统计口径名称
     */
    private String getStatisticsTypeName(Integer type) {
        switch (type) {
            case 1: return "开单医生所在科";
            case 2: return "执行医生所在科";
            case 3: return "开单科室对应护理单元";
            case 4: return "患者所在科室";
            case 5: return "收费科室";
            default: return "未知口径";
        }
    }
}