package com.hospital.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.performance.entity.PerformanceScheme;
import com.hospital.performance.mapper.PerformanceSchemeMapper;
import com.hospital.performance.service.PerformanceSchemeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 绩效方案服务实现类
 */
@Service
@RequiredArgsConstructor
public class PerformanceSchemeServiceImpl extends ServiceImpl<PerformanceSchemeMapper, PerformanceScheme> 
        implements PerformanceSchemeService {

    @Override
    public PerformanceScheme getBySchemeCode(String schemeCode) {
        LambdaQueryWrapper<PerformanceScheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceScheme::getSchemeCode, schemeCode);
        return this.getOne(wrapper);
    }

    @Override
    public List<PerformanceScheme> getBySchemeType(Integer schemeType) {
        LambdaQueryWrapper<PerformanceScheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceScheme::getSchemeType, schemeType)
                .eq(PerformanceScheme::getStatus, 1)
                .orderByDesc(PerformanceScheme::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<PerformanceScheme> getByApplicableDeptType(Integer applicableDeptType) {
        LambdaQueryWrapper<PerformanceScheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceScheme::getApplicableDeptType, applicableDeptType)
                .eq(PerformanceScheme::getStatus, 1)
                .orderByDesc(PerformanceScheme::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Page<PerformanceScheme> pageSchemes(Page<PerformanceScheme> page, String keyword, 
                                             Integer schemeType, Integer status) {
        LambdaQueryWrapper<PerformanceScheme> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(PerformanceScheme::getSchemeName, keyword)
                    .or().like(PerformanceScheme::getSchemeCode, keyword)
                    .or().like(PerformanceScheme::getDescription, keyword));
        }
        
        if (schemeType != null) {
            wrapper.eq(PerformanceScheme::getSchemeType, schemeType);
        }
        
        if (status != null) {
            wrapper.eq(PerformanceScheme::getStatus, status);
        }
        
        wrapper.orderByDesc(PerformanceScheme::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public List<PerformanceScheme> getSchemeList(String keyword, Integer schemeType, Integer status) {
        LambdaQueryWrapper<PerformanceScheme> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(PerformanceScheme::getSchemeName, keyword)
                    .or().like(PerformanceScheme::getSchemeCode, keyword)
                    .or().like(PerformanceScheme::getDescription, keyword));
        }
        
        if (schemeType != null) {
            wrapper.eq(PerformanceScheme::getSchemeType, schemeType);
        }
        
        if (status != null) {
            wrapper.eq(PerformanceScheme::getStatus, status);
        }
        
        wrapper.orderByDesc(PerformanceScheme::getCreateTime);
        
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createScheme(PerformanceScheme scheme) {
        // 检查方案编码是否已存在
        if (existsBySchemeCode(scheme.getSchemeCode(), null)) {
            throw new RuntimeException("方案编码已存在");
        }
        
        return this.save(scheme);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateScheme(PerformanceScheme scheme) {
        PerformanceScheme existingScheme = this.getById(scheme.getId());
        if (existingScheme == null) {
            throw new RuntimeException("方案不存在");
        }
        
        // 检查方案编码是否被其他方案使用
        if (existsBySchemeCode(scheme.getSchemeCode(), scheme.getId())) {
            throw new RuntimeException("方案编码已被其他方案使用");
        }
        
        return this.updateById(scheme);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteScheme(Long id) {
        PerformanceScheme scheme = this.getById(id);
        if (scheme == null) {
            throw new RuntimeException("方案不存在");
        }
        
        // 检查是否为默认方案
        if (scheme.getIsDefault() == 1) {
            throw new RuntimeException("默认方案不能删除");
        }
        
        // 这里可以添加检查是否被计算记录使用的逻辑
        
        return this.removeById(id);
    }

    @Override
    public boolean existsBySchemeCode(String schemeCode, Long excludeId) {
        LambdaQueryWrapper<PerformanceScheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceScheme::getSchemeCode, schemeCode);
        if (excludeId != null) {
            wrapper.ne(PerformanceScheme::getId, excludeId);
        }
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleSchemeStatus(Long id, Integer status) {
        PerformanceScheme scheme = this.getById(id);
        if (scheme == null) {
            throw new RuntimeException("方案不存在");
        }
        
        scheme.setStatus(status);
        return this.updateById(scheme);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultScheme(Long id) {
        PerformanceScheme scheme = this.getById(id);
        if (scheme == null) {
            throw new RuntimeException("方案不存在");
        }
        
        // 先将同类型的其他方案设为非默认
        LambdaQueryWrapper<PerformanceScheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceScheme::getApplicableDeptType, scheme.getApplicableDeptType())
                .eq(PerformanceScheme::getIsDefault, 1);
        
        List<PerformanceScheme> defaultSchemes = this.list(wrapper);
        for (PerformanceScheme defaultScheme : defaultSchemes) {
            defaultScheme.setIsDefault(0);
        }
        this.updateBatchById(defaultSchemes);
        
        // 设置当前方案为默认
        scheme.setIsDefault(1);
        return this.updateById(scheme);
    }

    @Override
    public PerformanceScheme getDefaultScheme(Integer applicableDeptType) {
        LambdaQueryWrapper<PerformanceScheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceScheme::getApplicableDeptType, applicableDeptType)
                .eq(PerformanceScheme::getIsDefault, 1)
                .eq(PerformanceScheme::getStatus, 1);
        return this.getOne(wrapper);
    }
}