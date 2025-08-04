package com.hospital.performance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.performance.entity.HospitalOperationScore;

import java.util.List;
import java.util.Map;

/**
 * 医院运营评分服务接口
 */
public interface HospitalOperationScoreService extends IService<HospitalOperationScore> {

    /**
     * 计算医院运营评分
     * 
     * @param period 评分期间
     * @return 评分结果
     */
    HospitalOperationScore calculateOperationScore(String period);

    /**
     * 获取评分历史
     * 
     * @param startPeriod 开始期间
     * @param endPeriod 结束期间
     * @return 评分历史列表
     */
    List<HospitalOperationScore> getScoreHistory(String startPeriod, String endPeriod);

    /**
     * 获取评分数据
     * 
     * @param period 期间
     * @return 评分数据列表
     */
    List<HospitalOperationScore> getScoreData(String period);

    /**
     * 分页查询评分记录
     * 
     * @param page 分页参数
     * @param startPeriod 开始期间
     * @param endPeriod 结束期间
     * @return 分页结果
     */
    Page<HospitalOperationScore> pageScores(Page<HospitalOperationScore> page, String startPeriod, String endPeriod);

    /**
     * 获取评分趋势数据
     * 
     * @param startPeriod 开始期间
     * @param endPeriod 结束期间
     * @return 趋势数据
     */
    Map<String, Object> getScoreTrend(String startPeriod, String endPeriod);

    /**
     * 获取评分详细分析
     * 
     * @param period 评分期间
     * @return 详细分析结果
     */
    Map<String, Object> getScoreAnalysis(String period);

    /**
     * 获取评分对比分析
     * 
     * @param currentPeriod 当前期间
     * @param comparePeriod 对比期间
     * @return 对比分析结果
     */
    Map<String, Object> getScoreComparison(String currentPeriod, String comparePeriod);

    /**
     * 获取改进建议
     * 
     * @param period 评分期间
     * @return 改进建议列表
     */
    List<Map<String, Object>> getImprovementSuggestions(String period);

    /**
     * 导出评分报告
     * 
     * @param period 评分期间
     * @return 报告文件路径
     */
    String exportScoreReport(String period);

    /**
     * 重新计算评分
     * 
     * @param period 评分期间
     * @return 是否成功
     */
    boolean recalculateScore(String period);
}