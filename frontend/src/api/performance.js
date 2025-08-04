import request from './request'

/**
 * 绩效计算相关API
 */

// 执行绩效计算
export const executeCalculation = (data) => {
  return request({
    url: '/performance/calculation/execute',
    method: 'post',
    data
  })
}

// 获取计算结果
export const getCalculationResult = (calculationRecordId) => {
  return request({
    url: `/performance/calculation/result/${calculationRecordId}`,
    method: 'get'
  })
}

// 获取计算历史记录
export const getCalculationHistory = (params) => {
  return request({
    url: '/performance/calculation/history',
    method: 'get',
    params
  })
}

// 获取计算状态
export const getCalculationStatus = (calculationRecordId) => {
  return request({
    url: `/performance/calculation/status/${calculationRecordId}`,
    method: 'get'
  })
}

// 获取绩效方案列表
export const getPerformanceSchemes = () => {
  return request({
    url: '/performance/scheme/list',
    method: 'get'
  })
}

// 获取部门列表
export const getDepartments = () => {
  return request({
    url: '/system/dept/list',
    method: 'get'
  })
}

/**
 * 绩效指标相关API
 */

// 获取指标列表
export const getIndicatorList = (params) => {
  return request({
    url: '/performance/indicator/list',
    method: 'get',
    params
  })
}

// 创建指标
export const createIndicator = (data) => {
  return request({
    url: '/performance/indicator/create',
    method: 'post',
    data
  })
}

// 更新指标
export const updateIndicator = (id, data) => {
  return request({
    url: `/performance/indicator/update/${id}`,
    method: 'put',
    data
  })
}

// 删除指标
export const deleteIndicator = (id) => {
  return request({
    url: `/performance/indicator/delete/${id}`,
    method: 'delete'
  })
}

// 获取指标详情
export const getIndicatorDetail = (id) => {
  return request({
    url: `/performance/indicator/detail/${id}`,
    method: 'get'
  })
}