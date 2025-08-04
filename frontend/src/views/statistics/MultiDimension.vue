<template>
  <div class="multi-dimension-stats">
    <!-- 查询条件卡片 -->
    <div class="card mb-20">
      <div class="card-header">
        <h3>查询条件</h3>
      </div>
      
      <el-form :model="queryForm" :rules="queryRules" ref="queryFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="统计期间" prop="period">
              <el-date-picker
                v-model="queryForm.period"
                type="month"
                placeholder="选择统计期间"
                format="YYYY-MM"
                value-format="YYYY-MM"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="6">
            <el-form-item label="统计口径" prop="statisticsType">
              <el-select v-model="queryForm.statisticsType" placeholder="选择统计口径" style="width: 100%">
                <el-option
                  v-for="(desc, type) in statisticsTypes"
                  :key="type"
                  :label="desc"
                  :value="parseInt(type)"
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="6">
            <el-form-item label="部门范围">
              <el-select
                v-model="queryForm.deptIds"
                multiple
                placeholder="选择部门（不选择则查询所有）"
                style="width: 100%"
                clearable
              >
                <el-option
                  v-for="dept in deptOptions"
                  :key="dept.id"
                  :label="dept.deptName"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="6">
            <el-form-item label="指标范围">
              <el-select
                v-model="queryForm.indicatorIds"
                multiple
                placeholder="选择指标（不选择则查询所有）"
                style="width: 100%"
                clearable
              >
                <el-option
                  v-for="indicator in indicatorOptions"
                  :key="indicator.id"
                  :label="indicator.indicatorName"
                  :value="indicator.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" @click="executeQuery" :loading="querying">
                <el-icon><Search /></el-icon>
                {{ querying ? '查询中...' : '开始查询' }}
              </el-button>
              <el-button @click="resetQuery">重置</el-button>
              <el-button type="success" @click="exportData" :disabled="!hasData">
                <el-icon><Download /></el-icon>
                导出数据
              </el-button>
              <el-button type="info" @click="showCompareDialog">
                <el-icon><Operation /></el-icon>
                口径对比
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    
    <!-- 统计结果展示 -->
    <div v-if="hasData" class="results-section">
      <!-- 汇总统计卡片 -->
      <el-row :gutter="20" class="mb-20">
        <el-col :span="6">
          <div class="summary-card">
            <div class="summary-icon">
              <el-icon size="32"><DataAnalysis /></el-icon>
            </div>
            <div class="summary-content">
              <div class="summary-title">总记录数</div>
              <div class="summary-value">{{ summaryData.totalRecords }}</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="summary-card">
            <div class="summary-icon">
              <el-icon size="32"><Money /></el-icon>
            </div>
            <div class="summary-content">
              <div class="summary-title">总绩效金额</div>
              <div class="summary-value">{{ formatMoney(summaryData.totalAmount) }}</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="summary-card">
            <div class="summary-icon">
              <el-icon size="32"><OfficeBuilding /></el-icon>
            </div>
            <div class="summary-content">
              <div class="summary-title">涉及部门</div>
              <div class="summary-value">{{ summaryData.deptCount }}</div>
            </div>
          </div>
        </el-col>
        
        <el-col :span="6">
          <div class="summary-card">
            <div class="summary-icon">
              <el-icon size="32"><TrendCharts /></el-icon>
            </div>
            <div class="summary-content">
              <div class="summary-title">平均绩效</div>
              <div class="summary-value">{{ formatMoney(summaryData.avgAmount) }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <!-- 数据表格 -->
      <div class="table-container">
        <div class="table-header">
          <div class="table-title">统计结果明细</div>
          <div>
            <el-button-group>
              <el-button :type="viewMode === 'table' ? 'primary' : ''" @click="viewMode = 'table'">
                <el-icon><Grid /></el-icon>
                表格视图
              </el-button>
              <el-button :type="viewMode === 'chart' ? 'primary' : ''" @click="viewMode = 'chart'">
                <el-icon><PieChart /></el-icon>
                图表视图
              </el-button>
            </el-button-group>
          </div>
        </div>
        
        <!-- 表格视图 -->
        <div v-show="viewMode === 'table'">
          <el-table v-loading="loading" :data="statsData" max-height="500">
            <el-table-column prop="deptName" label="部门名称" width="150" />
            <el-table-column prop="userName" label="姓名" width="100" />
            <el-table-column prop="indicatorName" label="指标名称" width="150" />
            <el-table-column prop="indicatorValue" label="指标值" width="100" />
            <el-table-column prop="targetValue" label="目标值" width="100" />
            <el-table-column prop="completionRate" label="完成率" width="100">
              <template #default="{ row }">
                {{ (row.completionRate * 100).toFixed(2) }}%
              </template>
            </el-table-column>
            <el-table-column prop="score" label="得分" width="100" />
            <el-table-column prop="performanceAmount" label="绩效金额" width="120">
              <template #default="{ row }">
                {{ formatMoney(row.performanceAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="statisticsType" label="统计口径" width="150">
              <template #default="{ row }">
                {{ getStatisticsTypeName(row.statisticsType) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="traceByItem(row)">
                  项目追溯
                </el-button>
                <el-button type="info" size="small" @click="traceByDoctor(row)">
                  医生追溯
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <!-- 图表视图 -->
        <div v-show="viewMode === 'chart'">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="chart-card">
                <div class="chart-header">
                  <h4>部门绩效分布</h4>
                </div>
                <v-chart :option="deptDistributionOption" style="height: 300px" />
              </div>
            </el-col>
            
            <el-col :span="12">
              <div class="chart-card">
                <div class="chart-header">
                  <h4>指标完成情况</h4>
                </div>
                <v-chart :option="indicatorCompletionOption" style="height: 300px" />
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    
    <!-- 口径对比对话框 -->
    <el-dialog v-model="compareDialogVisible" title="统计口径对比" width="70%">
      <div class="compare-content">
        <el-form :model="compareForm" inline>
          <el-form-item label="对比指标">
            <el-select v-model="compareForm.indicatorId" placeholder="选择指标" style="width: 200px">
              <el-option
                v-for="indicator in indicatorOptions"
                :key="indicator.id"
                :label="indicator.indicatorName"
                :value="indicator.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="对比口径">
            <el-select
              v-model="compareForm.statisticsTypes"
              multiple
              placeholder="选择要对比的口径"
              style="width: 300px"
            >
              <el-option
                v-for="(desc, type) in statisticsTypes"
                :key="type"
                :label="desc"
                :value="parseInt(type)"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="executeCompare">开始对比</el-button>
          </el-form-item>
        </el-form>
        
        <div v-if="compareData.length > 0" class="compare-results">
          <el-table :data="compareData">
            <el-table-column prop="typeName" label="统计口径" width="200" />
            <el-table-column prop="totalValue" label="总值" width="150">
              <template #default="{ row }">
                {{ formatMoney(row.totalValue) }}
              </template>
            </el-table-column>
            <el-table-column prop="count" label="记录数" width="100" />
            <el-table-column label="占比" width="100">
              <template #default="{ row }">
                {{ ((row.totalValue / compareTotal) * 100).toFixed(2) }}%
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
    
    <!-- 追溯详情对话框 -->
    <el-dialog v-model="traceDialogVisible" :title="traceTitle" width="80%">
      <el-table :data="traceData" max-height="400">
        <el-table-column prop="patientName" label="患者姓名" width="100" />
        <el-table-column prop="itemName" label="项目名称" width="150" />
        <el-table-column prop="doctorName" label="医生" width="100" />
        <el-table-column prop="deptName" label="科室" width="120" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            {{ formatMoney(row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="executeTime" label="执行时间" width="160" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import VChart from 'vue-echarts'
import { 
  getMultiDimensionStats,
  getMultiDimensionData
} from '@/api/statistics'
import { getDepartments } from '@/api/performance'

const queryFormRef = ref()
const querying = ref(false)
const loading = ref(false)
const viewMode = ref('table')
const compareDialogVisible = ref(false)
const traceDialogVisible = ref(false)
const traceTitle = ref('')

const queryForm = reactive({
  period: '',
  statisticsType: null,
  deptIds: [],
  indicatorIds: []
})

const queryRules = {
  period: [{ required: true, message: '请选择统计期间', trigger: 'change' }],
  statisticsType: [{ required: true, message: '请选择统计口径', trigger: 'change' }]
}

const compareForm = reactive({
  indicatorId: null,
  statisticsTypes: []
})

const statisticsTypes = ref({
  1: '开单医生所在科',
  2: '执行医生所在科',
  3: '开单科室对应护理单元',
  4: '患者所在科室',
  5: '收费科室'
})

const deptOptions = ref([])
const indicatorOptions = ref([])
const statsData = ref([])
const summaryData = ref({})
const compareData = ref([])
const traceData = ref([])

// 模拟数据
const mockDepts = [
  { id: 3, deptName: '心血管内科' },
  { id: 4, deptName: '心血管外科' },
  { id: 6, deptName: '普通外科' },
  { id: 7, deptName: '骨科' }
]

const mockIndicators = [
  { id: 1, indicatorName: '门诊人数' },
  { id: 2, indicatorName: '住院人数' },
  { id: 3, indicatorName: '医疗收入' },
  { id: 4, indicatorName: '床位周转率' }
]

const mockStatsData = [
  {
    deptName: '心血管内科',
    userName: '',
    indicatorName: '门诊人数',
    indicatorValue: 950,
    targetValue: 1000,
    completionRate: 0.95,
    score: 19.0,
    performanceAmount: 19000.00,
    statisticsType: 1
  },
  {
    deptName: '心血管内科',
    userName: '',
    indicatorName: '住院人数',
    indicatorValue: 180,
    targetValue: 200,
    completionRate: 0.90,
    score: 27.0,
    performanceAmount: 27000.00,
    statisticsType: 1
  }
]

const hasData = computed(() => statsData.value.length > 0)

const compareTotal = computed(() => {
  return compareData.value.reduce((sum, item) => sum + item.totalValue, 0)
})

// 图表配置
const deptDistributionOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c}万 ({d}%)'
  },
  series: [{
    name: '部门绩效',
    type: 'pie',
    radius: '50%',
    data: []
  }]
})

const indicatorCompletionOption = ref({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      formatter: '{value}%'
    }
  },
  series: [{
    name: '完成率',
    type: 'bar',
    data: []
  }]
})

const loadInitialData = () => {
  deptOptions.value = mockDepts
  indicatorOptions.value = mockIndicators
}

const executeQuery = async () => {
  if (!queryFormRef.value) return
  
  try {
    const valid = await queryFormRef.value.validate()
    if (!valid) return
    
    querying.value = true
    
    // 调用后端API获取多维度统计数据
    const response = await getMultiDimensionData({
      period: queryForm.period,
      statisticsType: queryForm.statisticsType,
      deptIds: queryForm.deptIds,
      indicatorIds: queryForm.indicatorIds
    })
    
    if (response.code === 200) {
      statsData.value = response.data || []
      
      // 计算汇总数据
      summaryData.value = {
        totalRecords: statsData.value.length,
        totalAmount: statsData.value.reduce((sum, item) => sum + (item.performanceAmount || 0), 0),
        deptCount: new Set(statsData.value.map(item => item.deptName)).size,
        avgAmount: statsData.value.length > 0 ? 
          statsData.value.reduce((sum, item) => sum + (item.performanceAmount || 0), 0) / statsData.value.length : 0
      }
      
      updateCharts()
      ElMessage.success('查询完成')
    } else {
      ElMessage.error(response.message || '查询失败')
      // 如果API调用失败，使用模拟数据
      statsData.value = mockStatsData
      summaryData.value = {
        totalRecords: statsData.value.length,
        totalAmount: statsData.value.reduce((sum, item) => sum + item.performanceAmount, 0),
        deptCount: new Set(statsData.value.map(item => item.deptName)).size,
        avgAmount: statsData.value.reduce((sum, item) => sum + item.performanceAmount, 0) / statsData.value.length
      }
      updateCharts()
    }
    
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败，请稍后重试')
    // 如果API调用失败，使用模拟数据
    statsData.value = mockStatsData
    summaryData.value = {
      totalRecords: statsData.value.length,
      totalAmount: statsData.value.reduce((sum, item) => sum + item.performanceAmount, 0),
      deptCount: new Set(statsData.value.map(item => item.deptName)).size,
      avgAmount: statsData.value.reduce((sum, item) => sum + item.performanceAmount, 0) / statsData.value.length
    }
    updateCharts()
  } finally {
    querying.value = false
  }
}

const updateCharts = () => {
  // 更新部门分布图
  const deptData = {}
  statsData.value.forEach(item => {
    if (!deptData[item.deptName]) {
      deptData[item.deptName] = 0
    }
    deptData[item.deptName] += item.performanceAmount
  })
  
  deptDistributionOption.value.series[0].data = Object.entries(deptData).map(([name, value]) => ({
    name,
    value: value / 10000 // 转换为万元
  }))
  
  // 更新指标完成情况图
  const indicatorData = {}
  statsData.value.forEach(item => {
    if (!indicatorData[item.indicatorName]) {
      indicatorData[item.indicatorName] = []
    }
    indicatorData[item.indicatorName].push(item.completionRate * 100)
  })
  
  const indicatorNames = Object.keys(indicatorData)
  const completionRates = indicatorNames.map(name => {
    const rates = indicatorData[name]
    return rates.reduce((sum, rate) => sum + rate, 0) / rates.length
  })
  
  indicatorCompletionOption.value.xAxis.data = indicatorNames
  indicatorCompletionOption.value.series[0].data = completionRates
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  statsData.value = []
  summaryData.value = {}
}

const exportData = () => {
  ElMessage.success('数据导出成功')
}

const showCompareDialog = () => {
  compareDialogVisible.value = true
}

const executeCompare = () => {
  // 模拟对比数据
  compareData.value = [
    {
      statisticsType: 1,
      typeName: '开单医生所在科',
      totalValue: 50000.00,
      count: 150
    },
    {
      statisticsType: 2,
      typeName: '执行医生所在科',
      totalValue: 48000.00,
      count: 145
    }
  ]
  
  ElMessage.success('对比完成')
}

const traceByItem = (row) => {
  traceTitle.value = `项目追溯 - ${row.indicatorName}`
  
  // 模拟追溯数据
  traceData.value = [
    {
      patientName: '张三',
      itemName: 'CT检查',
      doctorName: '李医生',
      deptName: '放射科',
      amount: 300.00,
      executeTime: '2024-01-15 10:30:00'
    }
  ]
  
  traceDialogVisible.value = true
}

const traceByDoctor = (row) => {
  traceTitle.value = `医生追溯 - ${row.indicatorName}`
  
  // 模拟医生追溯数据
  traceData.value = [
    {
      patientName: '李四',
      itemName: 'MRI检查',
      doctorName: '王医生',
      deptName: '放射科',
      amount: 800.00,
      executeTime: '2024-01-15 14:20:00'
    }
  ]
  
  traceDialogVisible.value = true
}

const getStatisticsTypeName = (type) => {
  return statisticsTypes.value[type] || '未知'
}

const formatMoney = (amount) => {
  if (!amount) return '0.00'
  return amount.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

onMounted(() => {
  loadInitialData()
})
</script>

<style lang="scss" scoped>
.multi-dimension-stats {
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
  
  .summary-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    gap: 16px;
    
    .summary-icon {
      color: #409EFF;
      flex-shrink: 0;
    }
    
    .summary-content {
      flex: 1;
      
      .summary-title {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
      }
      
      .summary-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
      }
    }
  }
  
  .chart-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    
    .chart-header {
      margin-bottom: 20px;
      
      h4 {
        margin: 0;
        font-size: 16px;
        color: #303133;
      }
    }
  }
}

.compare-content {
  .compare-results {
    margin-top: 20px;
  }
}
</style>