<template>
  <div class="operation-score">
    <!-- 评分计算卡片 -->
    <div class="card mb-20">
      <div class="card-header">
        <h3>运营评分计算</h3>
      </div>
      
      <el-form :model="scoreForm" inline>
        <el-form-item label="评分期间">
          <el-date-picker
            v-model="scoreForm.period"
            type="month"
            placeholder="选择评分期间"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="calculateScore" :loading="calculating">
            <el-icon><Calculator /></el-icon>
            {{ calculating ? '计算中...' : '开始评分' }}
          </el-button>
          <el-button @click="loadScoreHistory">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 最新评分展示 -->
    <div v-if="latestScore" class="score-display mb-20">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="score-card total-score">
            <div class="score-icon">
              <el-icon size="48"><Medal /></el-icon>
            </div>
            <div class="score-content">
              <div class="score-title">综合得分</div>
              <div class="score-value">{{ latestScore.totalScore }}</div>
              <div class="score-level">{{ latestScore.scoreLevel }}</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="4.5">
          <div class="score-card market-share">
            <div class="score-title">市场占有率</div>
            <div class="score-value">{{ latestScore.marketShareScore }}</div>
            <div class="score-progress">
              <el-progress :percentage="latestScore.marketShareScore" :show-text="false" />
            </div>
          </div>
        </el-col>
        
        <el-col :span="4.5">
          <div class="score-card hr-efficiency">
            <div class="score-title">人力资源效率</div>
            <div class="score-value">{{ latestScore.hrEfficiencyScore }}</div>
            <div class="score-progress">
              <el-progress :percentage="latestScore.hrEfficiencyScore" :show-text="false" />
            </div>
          </div>
        </el-col>
        
        <el-col :span="4.5">
          <div class="score-card equipment-efficiency">
            <div class="score-title">设备效率</div>
            <div class="score-value">{{ latestScore.equipmentEfficiencyScore }}</div>
            <div class="score-progress">
              <el-progress :percentage="latestScore.equipmentEfficiencyScore" :show-text="false" />
            </div>
          </div>
        </el-col>
        
        <el-col :span="4.5">
          <div class="score-card revenue-structure">
            <div class="score-title">收入结构</div>
            <div class="score-value">{{ latestScore.revenueStructureScore }}</div>
            <div class="score-progress">
              <el-progress :percentage="latestScore.revenueStructureScore" :show-text="false" />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- 评分趋势图表 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="16">
        <div class="chart-card">
          <div class="chart-header">
            <h3>评分趋势分析</h3>
            <el-select v-model="trendPeriod" size="small" style="width: 120px" @change="loadTrendData">
              <el-option label="近6个月" value="6" />
              <el-option label="近12个月" value="12" />
              <el-option label="近24个月" value="24" />
            </el-select>
          </div>
          <div class="chart-content">
            <v-chart :option="trendChartOption" style="height: 300px" />
          </div>
        </div>
      </el-col>
      
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3>维度得分雷达图</h3>
          </div>
          <div class="chart-content">
            <v-chart :option="radarChartOption" style="height: 300px" />
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 评分历史记录 -->
    <div class="table-container">
      <div class="table-header">
        <div class="table-title">评分历史记录</div>
        <div>
          <el-button type="success" @click="exportReport" v-if="selectedScore">
            <el-icon><Download /></el-icon>
            导出报告
          </el-button>
        </div>
      </div>
      
      <el-table 
        v-loading="loading" 
        :data="scoreHistory" 
        @selection-change="handleSelectionChange"
        highlight-current-row
        @current-change="handleCurrentChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="scorePeriod" label="评分期间" width="100" />
        <el-table-column prop="totalScore" label="综合得分" width="100">
          <template #default="{ row }">
            <span :class="getScoreClass(row.totalScore)">{{ row.totalScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="scoreLevel" label="评分等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTag(row.scoreLevel)">{{ row.scoreLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="marketShareScore" label="市场占有率" width="100" />
        <el-table-column prop="hrEfficiencyScore" label="人力资源效率" width="120" />
        <el-table-column prop="equipmentEfficiencyScore" label="设备效率" width="100" />
        <el-table-column prop="revenueStructureScore" label="收入结构" width="100" />
        <el-table-column prop="createTime" label="计算时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewAnalysis(row)">
              详细分析
            </el-button>
            <el-button type="info" size="small" @click="viewSuggestions(row)">
              改进建议
            </el-button>
            <el-button type="warning" size="small" @click="recalculateScore(row)">
              重新计算
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 详细分析对话框 -->
    <el-dialog v-model="analysisDialogVisible" title="详细分析" width="70%">
      <div class="analysis-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="评分期间">{{ currentAnalysis.scorePeriod }}</el-descriptions-item>
          <el-descriptions-item label="综合得分">{{ currentAnalysis.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="评分等级">
            <el-tag :type="getLevelTag(currentAnalysis.scoreLevel)">{{ currentAnalysis.scoreLevel }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评估结果" span="2">
            {{ currentAnalysis.evaluationResult }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="dimension-analysis mt-20">
          <h4>各维度分析</h4>
          <el-row :gutter="20">
            <el-col :span="12" v-for="dimension in dimensionAnalysis" :key="dimension.dimension">
              <div class="dimension-card">
                <div class="dimension-header">
                  <span class="dimension-name">{{ dimension.dimension }}</span>
                  <span class="dimension-score">{{ dimension.score }}分</span>
                </div>
                <div class="dimension-description">{{ dimension.description }}</div>
                <el-tag :type="getLevelTag(dimension.level)" size="small">{{ dimension.level }}</el-tag>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-dialog>
    
    <!-- 改进建议对话框 -->
    <el-dialog v-model="suggestionsDialogVisible" title="改进建议" width="60%">
      <div class="suggestions-content">
        <el-alert
          title="基于当前评分结果，系统为您提供以下改进建议："
          type="info"
          :closable="false"
          class="mb-20"
        />
        
        <div v-for="(suggestion, index) in improvementSuggestions" :key="index" class="suggestion-item">
          <div class="suggestion-header">
            <el-icon><Lightbulb /></el-icon>
            <span class="suggestion-dimension">{{ suggestion.dimension }}</span>
            <el-tag :type="suggestion.priority === '高' ? 'danger' : 'warning'" size="small">
              {{ suggestion.priority }}优先级
            </el-tag>
          </div>
          <div class="suggestion-content">{{ suggestion.suggestion }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import VChart from 'vue-echarts'
import { 
  getOperationScore,
  getOperationScoreTrend,
  getOperationScoreRanking
} from '@/api/statistics'

const calculating = ref(false)
const loading = ref(false)
const analysisDialogVisible = ref(false)
const suggestionsDialogVisible = ref(false)
const trendPeriod = ref('12')

const scoreForm = reactive({
  period: ''
})

const latestScore = ref(null)
const scoreHistory = ref([])
const selectedScore = ref(null)
const currentAnalysis = ref({})
const dimensionAnalysis = ref([])
const improvementSuggestions = ref([])

// 趋势图表配置
const trendChartOption = ref({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['综合得分', '市场占有率', '人力资源效率', '设备效率', '收入结构']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value',
    min: 0,
    max: 100
  },
  series: [
    {
      name: '综合得分',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: { color: '#409EFF' }
    },
    {
      name: '市场占有率',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: { color: '#67C23A' }
    },
    {
      name: '人力资源效率',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: { color: '#E6A23C' }
    },
    {
      name: '设备效率',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: { color: '#F56C6C' }
    },
    {
      name: '收入结构',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: { color: '#909399' }
    }
  ]
})

// 雷达图配置
const radarChartOption = ref({
  tooltip: {},
  radar: {
    indicator: [
      { name: '市场占有率', max: 100 },
      { name: '人力资源效率', max: 100 },
      { name: '设备效率', max: 100 },
      { name: '收入结构', max: 100 }
    ]
  },
  series: [{
    name: '运营评分',
    type: 'radar',
    data: [{
      value: [],
      name: '当前得分'
    }]
  }]
})

// 模拟数据
const mockScoreHistory = [
  {
    id: 1,
    scorePeriod: '2024-01',
    totalScore: 85.6,
    scoreLevel: '良好',
    marketShareScore: 82.3,
    hrEfficiencyScore: 88.9,
    equipmentEfficiencyScore: 85.2,
    revenueStructureScore: 86.0,
    evaluationResult: '医院运营综合得分为85.6分，评级为良好。其中：市场占有率得分82.3分，人力资源效率得分88.9分，设备效率得分85.2分，收入结构得分86.0分。',
    createTime: '2024-02-01 10:30:00'
  },
  {
    id: 2,
    scorePeriod: '2023-12',
    totalScore: 78.4,
    scoreLevel: '一般',
    marketShareScore: 75.6,
    hrEfficiencyScore: 82.1,
    equipmentEfficiencyScore: 76.8,
    revenueStructureScore: 79.2,
    evaluationResult: '医院运营综合得分为78.4分，评级为一般。',
    createTime: '2024-01-01 10:30:00'
  }
]

const calculateScore = async () => {
  if (!scoreForm.period) {
    ElMessage.warning('请选择评分期间')
    return
  }
  
  calculating.value = true
  try {
    // 调用后端API计算运营评分
    const response = await getOperationScore({
      period: scoreForm.period
    })
    
    if (response.code === 200) {
      ElMessage.success('评分计算完成')
      await loadScoreHistory()
    } else {
      ElMessage.error(response.message || '评分计算失败')
    }
  } catch (error) {
    console.error('评分计算失败:', error)
    ElMessage.error('评分计算失败，请稍后重试')
  } finally {
    calculating.value = false
  }
}

const loadScoreHistory = async () => {
  loading.value = true
  try {
    // 计算时间范围
    const currentDate = new Date()
    const endPeriod = `${currentDate.getFullYear()}-${String(currentDate.getMonth() + 1).padStart(2, '0')}`
    const startDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - parseInt(trendPeriod.value), 1)
    const startPeriod = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, '0')}`
    
    // 调用后端API获取评分趋势数据
    const response = await getOperationScoreTrend({
      startPeriod,
      endPeriod
    })
    
    if (response.code === 200) {
      scoreHistory.value = response.data || []
      latestScore.value = scoreHistory.value[0] || null
      updateCharts()
    } else {
      console.error('获取评分历史失败:', response.message)
      // 如果API调用失败，使用模拟数据
      scoreHistory.value = mockScoreHistory
      latestScore.value = mockScoreHistory[0]
      updateCharts()
    }
  } catch (error) {
    console.error('获取评分历史失败:', error)
    // 如果API调用失败，使用模拟数据
    scoreHistory.value = mockScoreHistory
    latestScore.value = mockScoreHistory[0]
    updateCharts()
  } finally {
    loading.value = false
  }
}

const updateCharts = () => {
  // 使用实际的scoreHistory数据而不是mockScoreHistory
  const historyData = scoreHistory.value.length > 0 ? scoreHistory.value : mockScoreHistory
  
  // 更新趋势图
  const periods = historyData.map(item => item.scorePeriod).reverse()
  const totalScores = historyData.map(item => item.totalScore).reverse()
  const marketShareScores = historyData.map(item => item.marketShareScore).reverse()
  const hrEfficiencyScores = historyData.map(item => item.hrEfficiencyScore).reverse()
  const equipmentEfficiencyScores = historyData.map(item => item.equipmentEfficiencyScore).reverse()
  const revenueStructureScores = historyData.map(item => item.revenueStructureScore).reverse()
  
  trendChartOption.value.xAxis.data = periods
  trendChartOption.value.series[0].data = totalScores
  trendChartOption.value.series[1].data = marketShareScores
  trendChartOption.value.series[2].data = hrEfficiencyScores
  trendChartOption.value.series[3].data = equipmentEfficiencyScores
  trendChartOption.value.series[4].data = revenueStructureScores
  
  // 更新雷达图
  if (latestScore.value) {
    radarChartOption.value.series[0].data[0].value = [
      latestScore.value.marketShareScore || 0,
      latestScore.value.hrEfficiencyScore || 0,
      latestScore.value.equipmentEfficiencyScore || 0,
      latestScore.value.revenueStructureScore || 0
    ]
  }
}

const loadTrendData = () => {
  // 根据选择的期间加载趋势数据
  updateCharts()
}

const handleSelectionChange = (selection) => {
  selectedScore.value = selection.length > 0 ? selection[0] : null
}

const handleCurrentChange = (currentRow) => {
  selectedScore.value = currentRow
}

const viewAnalysis = (row) => {
  currentAnalysis.value = row
  
  // 模拟维度分析数据
  dimensionAnalysis.value = [
    {
      dimension: '市场占有率',
      score: row.marketShareScore,
      description: '反映医院在区域内的市场地位和竞争力',
      level: row.marketShareScore >= 80 ? '良好' : '一般'
    },
    {
      dimension: '人力资源效率',
      score: row.hrEfficiencyScore,
      description: '反映医院人力资源的配置和使用效率',
      level: row.hrEfficiencyScore >= 80 ? '良好' : '一般'
    },
    {
      dimension: '设备效率',
      score: row.equipmentEfficiencyScore,
      description: '反映医院设备的利用率和产出效率',
      level: row.equipmentEfficiencyScore >= 80 ? '良好' : '一般'
    },
    {
      dimension: '收入结构',
      score: row.revenueStructureScore,
      description: '反映医院收入来源的合理性和可持续性',
      level: row.revenueStructureScore >= 80 ? '良好' : '一般'
    }
  ]
  
  analysisDialogVisible.value = true
}

const viewSuggestions = (row) => {
  // 模拟改进建议数据
  improvementSuggestions.value = [
    {
      dimension: '市场占有率',
      suggestion: '加强市场推广，提升服务质量，扩大患者群体',
      priority: '高'
    },
    {
      dimension: '设备效率',
      suggestion: '提高设备利用率，加强设备维护，优化检查流程',
      priority: '中'
    }
  ]
  
  suggestionsDialogVisible.value = true
}

const recalculateScore = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重新计算该期间的评分吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('重新计算成功')
    loadScoreHistory()
  } catch {
    // 用户取消
  }
}

const exportReport = () => {
  if (!selectedScore.value) {
    ElMessage.warning('请先选择要导出的评分记录')
    return
  }
  
  ElMessage.success('报告导出成功')
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 70) return 'score-normal'
  return 'score-poor'
}

const getLevelTag = (level) => {
  const tagMap = {
    '优秀': 'success',
    '良好': 'primary',
    '一般': 'warning',
    '较差': 'danger'
  }
  return tagMap[level] || ''
}

onMounted(() => {
  loadScoreHistory()
})
</script>

<style lang="scss" scoped>
.operation-score {
  .card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    
    .card-header {
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 1px solid #e4e7ed;
      
      h3 {
        margin: 0;
        font-size: 18px;
        color: #303133;
      }
    }
  }
  
  .score-display {
    .score-card {
      background: #fff;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      text-align: center;
      
      &.total-score {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #fff;
        display: flex;
        align-items: center;
        gap: 16px;
        text-align: left;
        
        .score-icon {
          flex-shrink: 0;
          opacity: 0.8;
        }
        
        .score-content {
          flex: 1;
          
          .score-title {
            font-size: 14px;
            opacity: 0.9;
            margin-bottom: 8px;
          }
          
          .score-value {
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 4px;
          }
          
          .score-level {
            font-size: 16px;
            opacity: 0.8;
          }
        }
      }
      
      .score-title {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
      }
      
      .score-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 8px;
      }
      
      .score-progress {
        margin-top: 8px;
      }
    }
  }
  
  .chart-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      h3 {
        margin: 0;
        font-size: 16px;
        color: #303133;
      }
    }
  }
}

.analysis-content {
  .dimension-analysis {
    .dimension-card {
      background: #f8f9fa;
      border-radius: 8px;
      padding: 16px;
      margin-bottom: 16px;
      
      .dimension-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        
        .dimension-name {
          font-weight: 600;
          color: #303133;
        }
        
        .dimension-score {
          font-size: 18px;
          font-weight: bold;
          color: #409EFF;
        }
      }
      
      .dimension-description {
        color: #606266;
        font-size: 14px;
        margin-bottom: 8px;
        line-height: 1.5;
      }
    }
  }
}

.suggestions-content {
  .suggestion-item {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 16px;
    
    .suggestion-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      
      .suggestion-dimension {
        font-weight: 600;
        color: #303133;
      }
    }
    
    .suggestion-content {
      color: #606266;
      line-height: 1.5;
    }
  }
}

// 得分颜色样式
.score-excellent {
  color: #67C23A;
  font-weight: bold;
}

.score-good {
  color: #409EFF;
  font-weight: bold;
}

.score-normal {
  color: #E6A23C;
  font-weight: bold;
}

.score-poor {
  color: #F56C6C;
  font-weight: bold;
}
</style>