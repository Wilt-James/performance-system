package com.hospital.performance.service.impl;

import com.hospital.performance.engine.PerformanceCalculationEngine;
import com.hospital.performance.entity.*;
import com.hospital.performance.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 绩效计算服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceCalculationServiceImpl implements PerformanceCalculationService {

    private final ApplicationContext applicationContext;
    private final PerformanceSchemeService schemeService;
    private final DepartmentService departmentService;
    private final UserService userService;
    private final PerformanceDataService performanceDataService;
    private final PerformanceCalculationRecordService calculationRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long executeCalculation(Long schemeId, String period, List<Long> deptIds, Integer calculationType) {
        log.info("开始执行绩效计算: 方案ID={}, 期间={}, 计算类型={}", schemeId, period, calculationType);
        
        // 获取绩效方案
        PerformanceScheme scheme = schemeService.getById(schemeId);
        if (scheme == null) {
            throw new RuntimeException("绩效方案不存在");
        }
        
        // 创建计算记录
        PerformanceCalculationRecord record = new PerformanceCalculationRecord();
        record.setCalculationPeriod(period);
        record.setSchemeId(schemeId);
        record.setSchemeName(scheme.getSchemeName());
        record.setCalculationType(calculationType);
        record.setCalculationStatus(1); // 计算中
        record.setStartTime(LocalDateTime.now());
        
        calculationRecordService.save(record);
        Long recordId = record.getId();
        
        try {
            // 获取计算引擎
            PerformanceCalculationEngine engine = getCalculationEngine(scheme.getSchemeType());
            
            // 准备基础数据
            Map<String, Object> baseData = prepareBaseData(period, deptIds);
            
            List<PerformanceData> allResults = new ArrayList<>();
            
            if (calculationType == 1) {
                // 科室绩效计算
                List<Department> departments = getDepartmentsForCalculation(deptIds, scheme.getApplicableDeptType());
                
                for (Department department : departments) {
                    List<PerformanceData> deptResults = engine.calculateDepartmentPerformance(
                            scheme, department, period, baseData);
                    allResults.addAll(deptResults);
                }
                
                record.setDeptCount(departments.size());
                
            } else if (calculationType == 2) {
                // 个人绩效计算
                List<User> users = getUsersForCalculation(deptIds);
                
                for (User user : users) {
                    List<PerformanceData> userResults = engine.calculateUserPerformance(
                            scheme, user, period, baseData);
                    allResults.addAll(userResults);
                }
                
                record.setUserCount(users.size());
            }
            
            // 验证计算结果
            if (!engine.validateCalculationResult(allResults)) {
                throw new RuntimeException("计算结果验证失败");
            }
            
            // 保存计算结果
            for (PerformanceData data : allResults) {
                data.setCreateBy(1L); // 这里应该从上下文获取当前用户
                data.setUpdateBy(1L);
            }
            performanceDataService.saveBatch(allResults);
            
            // 更新计算记录
            record.setCalculationStatus(2); // 计算完成
            record.setEndTime(LocalDateTime.now());
            record.setTotalAmount(allResults.stream()
                    .map(PerformanceData::getPerformanceAmount)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
            
            calculationRecordService.updateById(record);
            
            log.info("绩效计算完成: 记录ID={}, 数据条数={}", recordId, allResults.size());
            return recordId;
            
        } catch (Exception e) {
            log.error("绩效计算失败: 记录ID={}", recordId, e);
            
            // 更新计算记录为失败状态
            record.setCalculationStatus(3); // 计算失败
            record.setEndTime(LocalDateTime.now());
            record.setErrorMessage(e.getMessage());
            calculationRecordService.updateById(record);
            
            throw new RuntimeException("绩效计算失败: " + e.getMessage());
        }
    }

    @Override
    public List<PerformanceData> getCalculationResult(Long calculationRecordId) {
        PerformanceCalculationRecord record = calculationRecordService.getById(calculationRecordId);
        if (record == null) {
            throw new RuntimeException("计算记录不存在");
        }
        
        return performanceDataService.getByCalculationRecord(calculationRecordId);
    }

    @Override
    public Map<String, Object> getCalculationSteps(Long schemeId, Long targetId, String period) {
        PerformanceScheme scheme = schemeService.getById(schemeId);
        if (scheme == null) {
            throw new RuntimeException("绩效方案不存在");
        }
        
        PerformanceCalculationEngine engine = getCalculationEngine(scheme.getSchemeType());
        return engine.getCalculationSteps(scheme, targetId, period);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recalculate(Long calculationRecordId) {
        PerformanceCalculationRecord record = calculationRecordService.getById(calculationRecordId);
        if (record == null) {
            throw new RuntimeException("计算记录不存在");
        }
        
        // 删除原有计算结果
        performanceDataService.deleteByCalculationRecord(calculationRecordId);
        
        // 重新执行计算
        Long newRecordId = executeCalculation(
                record.getSchemeId(),
                record.getCalculationPeriod(),
                null, // 重新计算所有部门
                record.getCalculationType()
        );
        
        return newRecordId != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishCalculationResult(Long calculationRecordId) {
        PerformanceCalculationRecord record = calculationRecordService.getById(calculationRecordId);
        if (record == null) {
            throw new RuntimeException("计算记录不存在");
        }
        
        if (record.getCalculationStatus() != 2) {
            throw new RuntimeException("只有计算完成的记录才能发布");
        }
        
        // 更新绩效数据状态为已发布
        List<PerformanceData> dataList = performanceDataService.getByCalculationRecord(calculationRecordId);
        for (PerformanceData data : dataList) {
            data.setStatus(3); // 已发布
        }
        
        return performanceDataService.updateBatchById(dataList);
    }

    @Override
    public List<Map<String, Object>> getCalculationHistory(String period, Long schemeId) {
        return calculationRecordService.getCalculationHistory(period, schemeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCalculationRecord(Long calculationRecordId) {
        // 删除计算结果
        performanceDataService.deleteByCalculationRecord(calculationRecordId);
        
        // 删除计算记录
        return calculationRecordService.removeById(calculationRecordId);
    }

    /**
     * 获取计算引擎
     */
    private PerformanceCalculationEngine getCalculationEngine(Integer schemeType) {
        String engineBeanName;
        
        switch (schemeType) {
            case 1:
                engineBeanName = "workloadCalculationEngine";
                break;
            case 2:
                engineBeanName = "kpiCalculationEngine";
                break;
            case 3:
                engineBeanName = "costCalculationEngine";
                break;
            case 4:
                engineBeanName = "hybridCalculationEngine";
                break;
            default:
                throw new RuntimeException("不支持的绩效方案类型: " + schemeType);
        }
        
        return applicationContext.getBean(engineBeanName, PerformanceCalculationEngine.class);
    }

    /**
     * 准备基础数据
     */
    private Map<String, Object> prepareBaseData(String period, List<Long> deptIds) {
        Map<String, Object> baseData = new HashMap<>();
        
        // 这里应该从各个业务系统获取基础数据
        // 为了演示，使用模拟数据
        
        // 模拟门诊数据
        baseData.put("MZRS_3", 950); // 心血管内科门诊人数
        baseData.put("MZRS_4", 720); // 心血管外科门诊人数
        baseData.put("MZRS_6", 880); // 普通外科门诊人数
        
        // 模拟住院数据
        baseData.put("ZYRS_3", 180); // 心血管内科住院人数
        baseData.put("ZYRS_4", 160); // 心血管外科住院人数
        baseData.put("ZYRS_6", 190); // 普通外科住院人数
        
        // 模拟收入数据
        baseData.put("YYSR_3", 520000); // 心血管内科医疗收入
        baseData.put("YYSR_4", 480000); // 心血管外科医疗收入
        baseData.put("YYSR_6", 510000); // 普通外科医疗收入
        
        // 模拟KPI数据
        baseData.put("CWZL_3", 2.3); // 心血管内科床位周转率
        baseData.put("CWZL_4", 2.1); // 心血管外科床位周转率
        baseData.put("CWZL_6", 2.5); // 普通外科床位周转率
        
        baseData.put("YLFWMYD_3", 92.5); // 心血管内科满意度
        baseData.put("YLFWMYD_4", 89.8); // 心血管外科满意度
        baseData.put("YLFWMYD_6", 91.2); // 普通外科满意度
        
        return baseData;
    }

    /**
     * 获取需要计算的部门列表
     */
    private List<Department> getDepartmentsForCalculation(List<Long> deptIds, Integer applicableDeptType) {
        if (deptIds != null && !deptIds.isEmpty()) {
            return deptIds.stream()
                    .map(departmentService::getById)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        
        if (applicableDeptType == 5) {
            // 全院适用
            return departmentService.list();
        } else {
            // 按部门类型筛选
            return departmentService.getByDeptType(applicableDeptType);
        }
    }

    /**
     * 获取需要计算的用户列表
     */
    private List<User> getUsersForCalculation(List<Long> deptIds) {
        if (deptIds != null && !deptIds.isEmpty()) {
            return deptIds.stream()
                    .flatMap(deptId -> userService.list(
                            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                                    .eq(User::getDeptId, deptId)
                                    .eq(User::getStatus, 1)
                    ).stream())
                    .collect(Collectors.toList());
        }
        
        // 获取所有启用的用户
        return userService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getStatus, 1)
        );
    }
}