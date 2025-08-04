package com.hospital.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.performance.entity.PerformanceIndicator;
import com.hospital.performance.mapper.PerformanceIndicatorMapper;
import com.hospital.performance.service.PerformanceIndicatorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 绩效指标服务实现类
 */
@Service
@RequiredArgsConstructor
public class PerformanceIndicatorServiceImpl extends ServiceImpl<PerformanceIndicatorMapper, PerformanceIndicator> 
        implements PerformanceIndicatorService {

    @Override
    public PerformanceIndicator getByIndicatorCode(String indicatorCode) {
        return baseMapper.selectByIndicatorCode(indicatorCode);
    }

    @Override
    public List<PerformanceIndicator> getByIndicatorType(Integer indicatorType) {
        return baseMapper.selectByIndicatorType(indicatorType);
    }

    @Override
    public List<PerformanceIndicator> getByIndicatorCategory(Integer indicatorCategory) {
        return baseMapper.selectByIndicatorCategory(indicatorCategory);
    }

    @Override
    public List<PerformanceIndicator> getByApplicableScope(Integer applicableScope) {
        return baseMapper.selectByApplicableScope(applicableScope);
    }

    @Override
    public Page<PerformanceIndicator> pageIndicators(Page<PerformanceIndicator> page, String keyword, 
                                                   Integer indicatorType, Integer indicatorCategory, Integer status) {
        LambdaQueryWrapper<PerformanceIndicator> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(PerformanceIndicator::getIndicatorName, keyword)
                    .or().like(PerformanceIndicator::getIndicatorCode, keyword)
                    .or().like(PerformanceIndicator::getDescription, keyword));
        }
        
        if (indicatorType != null) {
            wrapper.eq(PerformanceIndicator::getIndicatorType, indicatorType);
        }
        
        if (indicatorCategory != null) {
            wrapper.eq(PerformanceIndicator::getIndicatorCategory, indicatorCategory);
        }
        
        if (status != null) {
            wrapper.eq(PerformanceIndicator::getStatus, status);
        }
        
        wrapper.orderByAsc(PerformanceIndicator::getSort)
                .orderByDesc(PerformanceIndicator::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public List<PerformanceIndicator> getIndicatorList(String keyword, Integer indicatorType, 
                                                     Integer indicatorCategory, Integer status) {
        LambdaQueryWrapper<PerformanceIndicator> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(PerformanceIndicator::getIndicatorName, keyword)
                    .or().like(PerformanceIndicator::getIndicatorCode, keyword)
                    .or().like(PerformanceIndicator::getDescription, keyword));
        }
        
        if (indicatorType != null) {
            wrapper.eq(PerformanceIndicator::getIndicatorType, indicatorType);
        }
        
        if (indicatorCategory != null) {
            wrapper.eq(PerformanceIndicator::getIndicatorCategory, indicatorCategory);
        }
        
        if (status != null) {
            wrapper.eq(PerformanceIndicator::getStatus, status);
        }
        
        wrapper.orderByAsc(PerformanceIndicator::getSort)
                .orderByDesc(PerformanceIndicator::getCreateTime);
        
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createIndicator(PerformanceIndicator indicator) {
        // 检查指标编码是否已存在
        if (existsByIndicatorCode(indicator.getIndicatorCode(), null)) {
            throw new RuntimeException("指标编码已存在");
        }
        
        // 验证公式
        if (StringUtils.isNotBlank(indicator.getFormula()) && !validateFormula(indicator.getFormula())) {
            throw new RuntimeException("指标公式格式不正确");
        }
        
        return this.save(indicator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateIndicator(PerformanceIndicator indicator) {
        PerformanceIndicator existingIndicator = this.getById(indicator.getId());
        if (existingIndicator == null) {
            throw new RuntimeException("指标不存在");
        }
        
        // 检查指标编码是否被其他指标使用
        if (existsByIndicatorCode(indicator.getIndicatorCode(), indicator.getId())) {
            throw new RuntimeException("指标编码已被其他指标使用");
        }
        
        // 验证公式
        if (StringUtils.isNotBlank(indicator.getFormula()) && !validateFormula(indicator.getFormula())) {
            throw new RuntimeException("指标公式格式不正确");
        }
        
        return this.updateById(indicator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteIndicator(Long id) {
        PerformanceIndicator indicator = this.getById(id);
        if (indicator == null) {
            throw new RuntimeException("指标不存在");
        }
        
        // 这里可以添加检查是否被绩效方案使用的逻辑
        
        return this.removeById(id);
    }

    @Override
    public boolean existsByIndicatorCode(String indicatorCode, Long excludeId) {
        LambdaQueryWrapper<PerformanceIndicator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceIndicator::getIndicatorCode, indicatorCode);
        if (excludeId != null) {
            wrapper.ne(PerformanceIndicator::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchImportIndicators(List<PerformanceIndicator> indicators) {
        if (indicators == null || indicators.isEmpty()) {
            throw new RuntimeException("导入数据不能为空");
        }
        
        // 验证数据
        for (PerformanceIndicator indicator : indicators) {
            if (StringUtils.isBlank(indicator.getIndicatorCode())) {
                throw new RuntimeException("指标编码不能为空");
            }
            if (StringUtils.isBlank(indicator.getIndicatorName())) {
                throw new RuntimeException("指标名称不能为空");
            }
            if (existsByIndicatorCode(indicator.getIndicatorCode(), null)) {
                throw new RuntimeException("指标编码 " + indicator.getIndicatorCode() + " 已存在");
            }
        }
        
        return this.saveBatch(indicators);
    }

    @Override
    public boolean validateFormula(String formula) {
        if (StringUtils.isBlank(formula)) {
            return true; // 空公式认为是有效的
        }
        
        // 这里可以实现更复杂的公式验证逻辑
        // 例如检查括号匹配、运算符合法性等
        
        // 简单验证：检查是否包含基本的数学运算符
        return formula.matches(".*[+\\-*/()\\d\\w\\s]+.*");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleIndicatorStatus(Long id, Integer status) {
        PerformanceIndicator indicator = this.getById(id);
        if (indicator == null) {
            throw new RuntimeException("指标不存在");
        }
        
        indicator.setStatus(status);
        return this.updateById(indicator);
    }
}