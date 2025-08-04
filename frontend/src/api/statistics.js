import request from './request'

/**
 * 统计分析相关API
 */

// 多维度统计分析
export const getMultiDimensionStats = (params) => {
  return request({
    url: '/statistics/multi-dimension/analysis',
    method: 'get',
    params
  })
}

// 获取多维度统计数据
export const getMultiDimensionData = (params) => {
  return request({
    url: '/statistics/multi-dimension/data',
    method: 'get',
    params
  })
}

// 导出多维度统计报表
export const exportMultiDimensionReport = (params) => {
  return request({
    url: '/statistics/multi-dimension/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 医院运营评分相关API
 */

// 获取运营评分数据
export const getOperationScore = (params) => {
  return request({
    url: '/statistics/operation-score/data',
    method: 'get',
    params
  })
}

// 获取运营评分趋势
export const getOperationScoreTrend = (params) => {
  return request({
    url: '/statistics/operation-score/trend',
    method: 'get',
    params
  })
}

// 获取运营评分排名
export const getOperationScoreRanking = (params) => {
  return request({
    url: '/statistics/operation-score/ranking',
    method: 'get',
    params
  })
}

// 导出运营评分报表
export const exportOperationScoreReport = (params) => {
  return request({
    url: '/statistics/operation-score/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 通用统计API
 */

// 获取统计概览
export const getStatisticsOverview = (params) => {
  return request({
    url: '/statistics/overview',
    method: 'get',
    params
  })
}

// 获取时间范围统计
export const getTimeRangeStats = (params) => {
  return request({
    url: '/statistics/time-range',
    method: 'get',
    params
  })
}

// 获取部门统计
export const getDepartmentStats = (params) => {
  return request({
    url: '/statistics/department',
    method: 'get',
    params
  })
}