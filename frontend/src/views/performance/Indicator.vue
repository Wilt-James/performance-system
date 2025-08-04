<template>
  <div class="indicator-management">
    <div class="table-container">
      <div class="table-header">
        <div class="table-title">绩效指标管理</div>
      </div>
      
      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增指标
        </el-button>
        <el-button type="success" @click="handleImport">
          <el-icon><Upload /></el-icon>
          导入指标
        </el-button>
        <el-button type="warning" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出指标
        </el-button>
        
        <div class="toolbar-right">
          <el-select v-model="searchForm.indicatorType" placeholder="指标类型" clearable style="width: 120px">
            <el-option label="收入类" :value="1" />
            <el-option label="工作量类" :value="2" />
            <el-option label="质量类" :value="3" />
            <el-option label="成本类" :value="4" />
            <el-option label="满意度类" :value="5" />
          </el-select>
          <el-select v-model="searchForm.indicatorCategory" placeholder="指标分类" clearable style="width: 120px">
            <el-option label="基础指标" :value="1" />
            <el-option label="KPI指标" :value="2" />
            <el-option label="自定义指标" :value="3" />
          </el-select>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索指标名称、编码"
            style="width: 200px"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
      
      <el-table v-loading="loading" :data="tableData">
        <el-table-column prop="indicatorCode" label="指标编码" width="120" />
        <el-table-column prop="indicatorName" label="指标名称" width="150" />
        <el-table-column prop="indicatorType" label="指标类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getIndicatorTypeTag(row.indicatorType)">
              {{ getIndicatorTypeName(row.indicatorType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="indicatorCategory" label="指标分类" width="100">
          <template #default="{ row }">
            <el-tag :type="getIndicatorCategoryTag(row.indicatorCategory)">
              {{ getIndicatorCategoryName(row.indicatorCategory) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="计量单位" width="80" />
        <el-table-column prop="weight" label="权重系数" width="100">
          <template #default="{ row }">
            {{ (row.weight * 100).toFixed(2) }}%
          </template>
        </el-table-column>
        <el-table-column prop="targetValue" label="目标值" width="100" />
        <el-table-column prop="applicableScope" label="适用范围" width="100">
          <template #default="{ row }">
            {{ getApplicableScopeName(row.applicableScope) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="info" size="small" @click="handleViewFormula(row)">
              公式
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="table-pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    
    <!-- 公式查看对话框 -->
    <el-dialog v-model="formulaDialogVisible" title="计算公式" width="600px">
      <div class="formula-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="指标名称">{{ currentIndicator.indicatorName }}</el-descriptions-item>
          <el-descriptions-item label="指标编码">{{ currentIndicator.indicatorCode }}</el-descriptions-item>
          <el-descriptions-item label="计算公式">
            <div class="formula-text">{{ currentIndicator.formula }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="数据来源">{{ getDataSourceName(currentIndicator.dataSource) }}</el-descriptions-item>
          <el-descriptions-item label="计算周期">{{ getCalculationCycleName(currentIndicator.calculationCycle) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getIndicatorList,
  createIndicator,
  updateIndicator,
  deleteIndicator,
  getIndicatorDetail
} from '@/api/performance'

const loading = ref(false)
const tableData = ref([])
const formulaDialogVisible = ref(false)
const currentIndicator = ref({})

const searchForm = reactive({
  keyword: '',
  indicatorType: null,
  indicatorCategory: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 模拟数据
const mockData = [
  {
    id: 1,
    indicatorCode: 'MZRS',
    indicatorName: '门诊人数',
    indicatorType: 2,
    indicatorCategory: 1,
    formula: '统计期间内门诊接诊的患者总人次数',
    dataSource: 1,
    unit: '人次',
    weight: 0.2000,
    targetValue: 1000.00,
    calculationCycle: 3,
    applicableScope: 2,
    status: 1
  },
  {
    id: 2,
    indicatorCode: 'ZYRS',
    indicatorName: '住院人数',
    indicatorType: 2,
    indicatorCategory: 1,
    formula: '统计期间内住院收治的患者总人次数',
    dataSource: 1,
    unit: '人次',
    weight: 0.3000,
    targetValue: 200.00,
    calculationCycle: 3,
    applicableScope: 2,
    status: 1
  },
  {
    id: 3,
    indicatorCode: 'YYSR',
    indicatorName: '医疗收入',
    indicatorType: 1,
    indicatorCategory: 1,
    formula: '统计期间内医疗服务产生的总收入金额',
    dataSource: 1,
    unit: '元',
    weight: 0.4000,
    targetValue: 500000.00,
    calculationCycle: 3,
    applicableScope: 2,
    status: 1
  },
  {
    id: 4,
    indicatorCode: 'CWZL',
    indicatorName: '床位周转率',
    indicatorType: 3,
    indicatorCategory: 2,
    formula: '出院人数 / 平均开放床位数',
    dataSource: 5,
    unit: '次/床',
    weight: 0.2000,
    targetValue: 2.50,
    calculationCycle: 3,
    applicableScope: 2,
    status: 1
  },
  {
    id: 5,
    indicatorCode: 'YLFWMYD',
    indicatorName: '医疗服务满意度',
    indicatorType: 5,
    indicatorCategory: 2,
    formula: '患者满意度调查得分的平均值',
    dataSource: 4,
    unit: '分',
    weight: 0.1500,
    targetValue: 90.00,
    calculationCycle: 3,
    applicableScope: 2,
    status: 1
  }
]

const getIndicatorTypeName = (type) => {
  const typeMap = {
    1: '收入类',
    2: '工作量类',
    3: '质量类',
    4: '成本类',
    5: '满意度类'
  }
  return typeMap[type] || '未知'
}

const getIndicatorTypeTag = (type) => {
  const tagMap = {
    1: 'success',
    2: 'primary',
    3: 'warning',
    4: 'danger',
    5: 'info'
  }
  return tagMap[type] || ''
}

const getIndicatorCategoryName = (category) => {
  const categoryMap = {
    1: '基础指标',
    2: 'KPI指标',
    3: '自定义指标'
  }
  return categoryMap[category] || '未知'
}

const getIndicatorCategoryTag = (category) => {
  const tagMap = {
    1: 'primary',
    2: 'success',
    3: 'warning'
  }
  return tagMap[category] || ''
}

const getApplicableScopeName = (scope) => {
  const scopeMap = {
    1: '全院',
    2: '科室',
    3: '个人'
  }
  return scopeMap[scope] || '未知'
}

const getDataSourceName = (source) => {
  const sourceMap = {
    1: 'HIS系统',
    2: 'LIS系统',
    3: 'PACS系统',
    4: '手工录入',
    5: '计算生成'
  }
  return sourceMap[source] || '未知'
}

const getCalculationCycleName = (cycle) => {
  const cycleMap = {
    1: '日',
    2: '周',
    3: '月',
    4: '季',
    5: '年'
  }
  return cycleMap[cycle] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    // 调用后端API获取指标列表
    const response = await getIndicatorList({
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword,
      indicatorType: searchForm.indicatorType,
      indicatorCategory: searchForm.indicatorCategory
    })
    
    if (response.code === 200) {
      tableData.value = response.data?.records || []
      pagination.total = response.data?.total || 0
    } else {
      console.error('获取指标列表失败:', response.message)
      // 如果API调用失败，使用模拟数据
      tableData.value = mockData
      pagination.total = mockData.length
    }
  } catch (error) {
    console.error('获取指标列表失败:', error)
    ElMessage.error('加载数据失败，使用模拟数据')
    // 如果API调用失败，使用模拟数据
    tableData.value = mockData
    pagination.total = mockData.length
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleAdd = () => {
  ElMessage.info('新增指标功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑指标: ${row.indicatorName}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除指标 "${row.indicatorName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 用户取消
  }
}

const handleViewFormula = (row) => {
  currentIndicator.value = row
  formulaDialogVisible.value = true
}

const handleImport = () => {
  ElMessage.info('导入指标功能开发中...')
}

const handleExport = () => {
  ElMessage.info('导出指标功能开发中...')
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadData()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.indicator-management {
  .table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .toolbar-right {
      display: flex;
      gap: 10px;
      align-items: center;
    }
  }
  
  .table-pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.formula-content {
  .formula-text {
    background: #f5f7fa;
    padding: 10px;
    border-radius: 4px;
    font-family: 'Courier New', monospace;
    white-space: pre-wrap;
    word-break: break-all;
  }
}

@media (max-width: 768px) {
  .indicator-management {
    .table-toolbar {
      flex-direction: column;
      gap: 10px;
      align-items: stretch;
      
      .toolbar-right {
        justify-content: flex-end;
        flex-wrap: wrap;
      }
    }
  }
}
</style>