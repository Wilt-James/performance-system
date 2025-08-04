<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <div class="stats-card">
          <div class="stats-icon">
            <el-icon size="32"><User /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-title">总用户数</div>
            <div class="stats-value">{{ stats.totalUsers }}</div>
            <div class="stats-trend">
              <el-icon><TrendCharts /></el-icon>
              较上月 +12%
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <div class="stats-card">
          <div class="stats-icon">
            <el-icon size="32"><OfficeBuilding /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-title">部门数量</div>
            <div class="stats-value">{{ stats.totalDepts }}</div>
            <div class="stats-trend">
              <el-icon><TrendCharts /></el-icon>
              较上月 +3%
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <div class="stats-card">
          <div class="stats-icon">
            <el-icon size="32"><DataAnalysis /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-title">绩效指标</div>
            <div class="stats-value">{{ stats.totalIndicators }}</div>
            <div class="stats-trend">
              <el-icon><TrendCharts /></el-icon>
              较上月 +8%
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <div class="stats-card">
          <div class="stats-icon">
            <el-icon size="32"><Money /></el-icon>
          </div>
          <div class="stats-content">
            <div class="stats-title">本月绩效</div>
            <div class="stats-value">{{ formatMoney(stats.monthlyPerformance) }}</div>
            <div class="stats-trend">
              <el-icon><TrendCharts /></el-icon>
              较上月 +15%
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>月度绩效趋势</h3>
            <el-select v-model="selectedYear" size="small" style="width: 100px">
              <el-option label="2024" value="2024" />
              <el-option label="2023" value="2023" />
            </el-select>
          </div>
          <div class="chart-content">
            <v-chart :option="performanceTrendOption" style="height: 300px" />
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>部门绩效分布</h3>
          </div>
          <div class="chart-content">
            <v-chart :option="deptPerformanceOption" style="height: 300px" />
          </div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 运营概览 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <div class="overview-card">
          <div class="overview-header">
            <h3>最新运营评分</h3>
            <el-button type="text" @click="goToOperationScore">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="score-display">
            <div class="score-circle">
              <div class="score-value">{{ operationScore.totalScore }}</div>
              <div class="score-label">综合得分</div>
            </div>
            <div class="score-details">
              <div class="score-item">
                <span class="item-label">市场占有率</span>
                <el-progress :percentage="operationScore.marketShare" :show-text="false" />
                <span class="item-value">{{ operationScore.marketShare }}分</span>
              </div>
              <div class="score-item">
                <span class="item-label">人力资源效率</span>
                <el-progress :percentage="operationScore.hrEfficiency" :show-text="false" />
                <span class="item-value">{{ operationScore.hrEfficiency }}分</span>
              </div>
              <div class="score-item">
                <span class="item-label">设备效率</span>
                <el-progress :percentage="operationScore.equipmentEfficiency" :show-text="false" />
                <span class="item-value">{{ operationScore.equipmentEfficiency }}分</span>
              </div>
              <div class="score-item">
                <span class="item-label">收入结构</span>
                <el-progress :percentage="operationScore.revenueStructure" :show-text="false" />
                <span class="item-value">{{ operationScore.revenueStructure }}分</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <div class="overview-card">
          <div class="overview-header">
            <h3>最近计算记录</h3>
            <el-button type="text" @click="goToCalculation">
              查看更多 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="calculation-list">
            <div v-for="record in recentCalculations" :key="record.id" class="calculation-item">
              <div class="calculation-info">
                <div class="calculation-title">{{ record.schemeName }}</div>
                <div class="calculation-meta">
                  <span>{{ record.period }}</span>
                  <span>{{ record.deptCount }}个部门</span>
                  <span>{{ formatMoney(record.totalAmount) }}</span>
                </div>
              </div>
              <div class="calculation-status">
                <el-tag :type="getStatusTag(record.status)">{{ getStatusName(record.status) }}</el-tag>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="actions-row">
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <div class="action-card calculation-action" @click="goToCalculation">
          <div class="action-icon">
            <el-icon size="48"><Calculator /></el-icon>
          </div>
          <div class="action-content">
            <h4>绩效计算</h4>
            <p>执行月度绩效计算任务</p>
            <div class="action-badge">核心功能</div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <div class="action-card stats-action" @click="goToMultiDimension">
          <div class="action-icon">
            <el-icon size="48"><DataBoard /></el-icon>
          </div>
          <div class="action-content">
            <h4>多口径统计</h4>
            <p>查看多维度统计分析</p>
            <div class="action-badge">分析工具</div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <div class="action-card score-action" @click="goToOperationScore">
          <div class="action-icon">
            <el-icon size="48"><Medal /></el-icon>
          </div>
          <div class="action-content">
            <h4>运营评分</h4>
            <p>查看医院运营评分</p>
            <div class="action-badge">评估报告</div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <div class="action-card config-action" @click="goToIndicator">
          <div class="action-icon">
            <el-icon size="48"><Setting /></el-icon>
          </div>
          <div class="action-content">
            <h4>指标管理</h4>
            <p>管理绩效指标配置</p>
            <div class="action-badge">配置管理</div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import VChart from 'vue-echarts'

const router = useRouter()

const selectedYear = ref('2024')

// 统计数据
const stats = reactive({
  totalUsers: 156,
  totalDepts: 28,
  totalIndicators: 45,
  monthlyPerformance: 2580000
})

// 运营评分数据
const operationScore = reactive({
  totalScore: 85.6,
  marketShare: 82.3,
  hrEfficiency: 88.9,
  equipmentEfficiency: 85.2,
  revenueStructure: 86.0
})

// 最近计算记录
const recentCalculations = ref([
  {
    id: 1,
    schemeName: '临床科室默认方案',
    period: '2024-01',
    deptCount: 12,
    totalAmount: 580000,
    status: 2
  },
  {
    id: 2,
    schemeName: '医技科室默认方案',
    period: '2024-01',
    deptCount: 8,
    totalAmount: 320000,
    status: 2
  },
  {
    id: 3,
    schemeName: '行政科室默认方案',
    period: '2024-01',
    deptCount: 6,
    totalAmount: 180000,
    status: 1
  }
])

// 绩效趋势图配置
const performanceTrendOption = ref({
  tooltip: {
    trigger: 'axis'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      formatter: '{value}万'
    }
  },
  series: [
    {
      name: '绩效金额',
      type: 'line',
      smooth: true,
      data: [220, 182, 191, 234, 290, 330, 310, 258, 280, 320, 350, 380],
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ]
        }
      }
    }
  ]
})

// 部门绩效分布图配置
const deptPerformanceOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c}万 ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '部门绩效',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 335, name: '心血管内科' },
        { value: 310, name: '心血管外科' },
        { value: 234, name: '普通外科' },
        { value: 135, name: '骨科' },
        { value: 148, name: '医学检验科' },
        { value: 120, name: '医学影像科' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

// 格式化金额
const formatMoney = (amount) => {
  return (amount / 10000).toFixed(1) + '万'
}

// 跳转到绩效计算
const goToCalculation = () => {
  router.push('/performance/calculation')
}

// 跳转到多口径统计
const goToMultiDimension = () => {
  router.push('/statistics/multi-dimension')
}

// 跳转到运营评分
const goToOperationScore = () => {
  router.push('/statistics/operation-score')
}

// 跳转到指标管理
const goToIndicator = () => {
  router.push('/performance/indicator')
}

// 获取状态名称
const getStatusName = (status) => {
  const statusMap = {
    1: '计算中',
    2: '已完成',
    3: '失败'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusTag = (status) => {
  const tagMap = {
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return tagMap[status] || ''
}

onMounted(() => {
  // 这里可以加载实际的统计数据
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -50%;
    width: 100px;
    height: 100px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }
  
  .stats-icon {
    flex-shrink: 0;
    opacity: 0.8;
  }
  
  .stats-content {
    flex: 1;
    
    .stats-title {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 8px;
    }
    
    .stats-value {
      font-size: 28px;
      font-weight: bold;
      margin-bottom: 4px;
    }
    
    .stats-trend {
      font-size: 12px;
      opacity: 0.8;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

.charts-row {
  margin-bottom: 20px;
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

.overview-row {
  margin-bottom: 20px;
}

.overview-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  
  .overview-header {
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
  
  .score-display {
    display: flex;
    gap: 30px;
    align-items: center;
    
    .score-circle {
      text-align: center;
      
      .score-value {
        font-size: 36px;
        font-weight: bold;
        color: #409EFF;
        display: block;
      }
      
      .score-label {
        font-size: 14px;
        color: #606266;
        margin-top: 4px;
      }
    }
    
    .score-details {
      flex: 1;
      
      .score-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        gap: 12px;
        
        .item-label {
          width: 100px;
          font-size: 14px;
          color: #606266;
        }
        
        .el-progress {
          flex: 1;
        }
        
        .item-value {
          width: 50px;
          text-align: right;
          font-size: 14px;
          color: #303133;
          font-weight: 600;
        }
      }
    }
  }
  
  .calculation-list {
    .calculation-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .calculation-info {
        .calculation-title {
          font-size: 16px;
          color: #303133;
          font-weight: 600;
          margin-bottom: 4px;
        }
        
        .calculation-meta {
          font-size: 14px;
          color: #606266;
          
          span {
            margin-right: 16px;
          }
        }
      }
    }
  }
}

.actions-row {
  .action-card {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }
    
    &.calculation-action:hover { 
      background: linear-gradient(135deg, #409EFF, #36a3f7); 
      color: white; 
      
      .action-icon, .action-content h4, .action-content p {
        color: white;
      }
    }
    
    &.stats-action:hover { 
      background: linear-gradient(135deg, #67C23A, #5daf34); 
      color: white; 
      
      .action-icon, .action-content h4, .action-content p {
        color: white;
      }
    }
    
    &.score-action:hover { 
      background: linear-gradient(135deg, #E6A23C, #d09a2b); 
      color: white; 
      
      .action-icon, .action-content h4, .action-content p {
        color: white;
      }
    }
    
    &.config-action:hover { 
      background: linear-gradient(135deg, #909399, #82848a); 
      color: white; 
      
      .action-icon, .action-content h4, .action-content p {
        color: white;
      }
    }
    
    .action-icon {
      margin-bottom: 16px;
      color: #409EFF;
      transition: color 0.3s;
    }
    
    .action-content {
      h4 {
        margin: 0 0 8px 0;
        font-size: 18px;
        color: #303133;
        transition: color 0.3s;
      }
      
      p {
        margin: 0 0 16px 0;
        color: #606266;
        font-size: 14px;
        transition: color 0.3s;
      }
      
      .action-badge {
        position: absolute;
        top: 12px;
        right: 12px;
        background: rgba(64, 158, 255, 0.1);
        color: #409EFF;
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 600;
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 10px;
  }
  
  .stats-card {
    margin-bottom: 10px;
    
    .stats-value {
      font-size: 24px;
    }
  }
  
  .chart-card {
    margin-bottom: 10px;
  }
  
  .action-card {
    margin-bottom: 10px;
  }
}
</style>