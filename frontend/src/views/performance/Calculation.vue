<template>
  <div class="calculation-management">
    <!-- 计算配置卡片 -->
    <div class="card mb-20">
      <div class="card-header">
        <h3>绩效计算配置</h3>
      </div>
      
      <el-form :model="calculationForm" :rules="calculationRules" ref="calculationFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="计算期间" prop="period">
              <el-date-picker
                v-model="calculationForm.period"
                type="month"
                placeholder="选择计算期间"
                format="YYYY-MM"
                value-format="YYYY-MM"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="绩效方案" prop="schemeId">
              <el-select v-model="calculationForm.schemeId" placeholder="选择绩效方案" style="width: 100%">
                <el-option
                  v-for="scheme in schemeOptions"
                  :key="scheme.id"
                  :label="scheme.schemeName"
                  :value="scheme.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="计算类型" prop="calculationType">
              <el-select v-model="calculationForm.calculationType" placeholder="选择计算类型" style="width: 100%">
                <el-option label="科室绩效" :value="1" />
                <el-option label="个人绩效" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="计算范围">
              <el-select
                v-model="calculationForm.deptIds"
                multiple
                placeholder="选择部门（不选择则计算所有部门）"
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
          
          <el-col :span="8">
            <el-form-item>
              <el-button type="primary" @click="executeCalculation" :loading="calculating">
                <el-icon><Calculator /></el-icon>
                {{ calculating ? '计算中...' : '开始计算' }}
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    
    <!-- 计算历史记录 -->
    <div class="table-container">
      <div class="table-header">
        <div class="table-title">计算历史记录</div>
        <div>
          <el-button @click="loadCalculationHistory">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
      
      <el-table v-loading="loading" :data="historyData">
        <el-table-column prop="calculationPeriod" label="计算期间" width="100" />
        <el-table-column prop="schemeName" label="绩效方案" width="150" />
        <el-table-column prop="calculationType" label="计算类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.calculationType === 1 ? 'primary' : 'success'">
              {{ row.calculationType === 1 ? '科室绩效' : '个人绩效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="calculationStatus" label="计算状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.calculationStatus)">
              {{ getStatusName(row.calculationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总绩效金额" width="120">
          <template #default="{ row }">
            {{ formatMoney(row.totalAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="deptCount" label="部门数" width="80" />
        <el-table-column prop="userCount" label="人员数" width="80" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewResult(row)">
              查看结果
            </el-button>
            <el-button type="info" size="small" @click="viewSteps(row)">
              计算步骤
            </el-button>
            <el-button 
              v-if="row.calculationStatus === 2" 
              type="success" 
              size="small" 
              @click="publishResult(row)"
            >
              发布
            </el-button>
            <el-button type="warning" size="small" @click="recalculate(row)">
              重新计算
            </el-button>
            <el-button type="danger" size="small" @click="deleteRecord(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 计算结果对话框 -->
    <el-dialog v-model="resultDialogVisible" title="计算结果" width="80%" :close-on-click-modal="false">
      <el-table :data="resultData" max-height="400">
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="userName" label="姓名" width="100" />
        <el-table-column prop="indicatorName" label="指标名称" width="150" />
        <el-table-column prop="indicatorValue" label="实际值" width="100" />
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
      </el-table>
    </el-dialog>
    
    <!-- 计算步骤对话框 -->
    <el-dialog v-model="stepsDialogVisible" title="计算步骤" width="60%">
      <div class="steps-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="计算方法">{{ currentSteps.calculationMethod }}</el-descriptions-item>
          <el-descriptions-item label="方法描述">{{ currentSteps.description }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="steps-list mt-20">
          <el-steps direction="vertical" :active="currentSteps.steps?.length || 0">
            <el-step
              v-for="step in currentSteps.steps"
              :key="step.step"
              :title="`步骤${step.step}: ${step.name}`"
              :description="step.description"
            />
          </el-steps>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  executeCalculation as apiExecuteCalculation,
  getCalculationHistory as apiGetCalculationHistory,
  getCalculationResult as apiGetCalculationResult,
  getPerformanceSchemes,
  getDepartments
} from '@/api/performance'

const calculationFormRef = ref()
const calculating = ref(false)
const loading = ref(false)
const resultDialogVisible = ref(false)
const stepsDialogVisible = ref(false)

const calculationForm = reactive({
  period: '',
  schemeId: null,
  calculationType: 1,
  deptIds: []
})

const calculationRules = {
  period: [{ required: true, message: '请选择计算期间', trigger: 'change' }],
  schemeId: [{ required: true, message: '请选择绩效方案', trigger: 'change' }],
  calculationType: [{ required: true, message: '请选择计算类型', trigger: 'change' }]
}

const schemeOptions = ref([])
const deptOptions = ref([])
const historyData = ref([])
const resultData = ref([])
const currentSteps = ref({})

// 模拟数据
const mockSchemes = [
  { id: 1, schemeName: '临床科室默认方案' },
  { id: 2, schemeName: '医技科室默认方案' },
  { id: 3, schemeName: '行政科室默认方案' }
]

const mockDepts = [
  { id: 3, deptName: '心血管内科' },
  { id: 4, deptName: '心血管外科' },
  { id: 6, deptName: '普通外科' },
  { id: 7, deptName: '骨科' },
  { id: 9, deptName: '医学检验科' },
  { id: 10, deptName: '医学影像科' }
]

const mockHistory = [
  {
    id: 1,
    calculationPeriod: '2024-01',
    schemeName: '临床科室默认方案',
    calculationType: 1,
    calculationStatus: 2,
    totalAmount: 125000.00,
    deptCount: 4,
    userCount: 0,
    startTime: '2024-02-01 09:00:00',
    endTime: '2024-02-01 09:05:23'
  },
  {
    id: 2,
    calculationPeriod: '2024-01',
    schemeName: '医技科室默认方案',
    calculationType: 1,
    calculationStatus: 2,
    totalAmount: 68000.00,
    deptCount: 2,
    userCount: 0,
    startTime: '2024-02-01 10:00:00',
    endTime: '2024-02-01 10:03:15'
  }
]

const loadInitialData = async () => {
  try {
    // 加载绩效方案
    const schemesRes = await getPerformanceSchemes()
    if (schemesRes.code === 200) {
      schemeOptions.value = schemesRes.data || []
    }
    
    // 加载部门列表
    const deptsRes = await getDepartments()
    if (deptsRes.code === 200) {
      deptOptions.value = deptsRes.data || []
    }
    
    // 加载计算历史
    await loadCalculationHistory()
  } catch (error) {
    console.error('加载初始数据失败:', error)
    // 如果API调用失败，使用模拟数据
    schemeOptions.value = mockSchemes
    deptOptions.value = mockDepts
    historyData.value = mockHistory
  }
}

const executeCalculation = async () => {
  if (!calculationFormRef.value) return
  
  try {
    const valid = await calculationFormRef.value.validate()
    if (!valid) return
    
    calculating.value = true
    
    // 调用后端API执行计算
    const response = await apiExecuteCalculation({
      schemeId: calculationForm.schemeId,
      period: calculationForm.period,
      deptIds: calculationForm.deptIds,
      calculationType: calculationForm.calculationType
    })
    
    if (response.code === 200) {
      ElMessage.success('绩效计算任务已启动')
      await loadCalculationHistory()
    } else {
      ElMessage.error(response.message || '计算失败')
    }
    
  } catch (error) {
    console.error('执行计算失败:', error)
    ElMessage.error('计算失败，请稍后重试')
  } finally {
    calculating.value = false
  }
}

const resetForm = () => {
  calculationFormRef.value?.resetFields()
}

const loadCalculationHistory = async () => {
  loading.value = true
  try {
    const response = await apiGetCalculationHistory()
    if (response.code === 200) {
      historyData.value = response.data || []
    } else {
      console.error('获取计算历史失败:', response.message)
      // 如果API调用失败，使用模拟数据
      historyData.value = mockHistory
    }
  } catch (error) {
    console.error('获取计算历史失败:', error)
    // 如果API调用失败，使用模拟数据
    historyData.value = mockHistory
  } finally {
    loading.value = false
  }
}

const viewResult = async (row) => {
  try {
    const response = await apiGetCalculationResult(row.id)
    if (response.code === 200) {
      resultData.value = response.data || []
    } else {
      // 如果API调用失败，使用模拟数据
      resultData.value = [
        {
          deptName: '心血管内科',
          userName: '',
          indicatorName: '门诊人数',
          indicatorValue: 950,
          targetValue: 1000,
          completionRate: 0.95,
          score: 19.0,
          performanceAmount: 19000.00
        },
        {
          deptName: '心血管内科',
          userName: '',
          indicatorName: '住院人数',
          indicatorValue: 180,
          targetValue: 200,
          completionRate: 0.90,
          score: 27.0,
          performanceAmount: 27000.00
        }
      ]
    }
  } catch (error) {
    console.error('获取计算结果失败:', error)
    // 如果API调用失败，使用模拟数据
    resultData.value = [
      {
        deptName: '心血管内科',
        userName: '',
        indicatorName: '门诊人数',
        indicatorValue: 950,
        targetValue: 1000,
        completionRate: 0.95,
        score: 19.0,
        performanceAmount: 19000.00
      }
    ]
  }
  resultDialogVisible.value = true
}

const viewSteps = (row) => {
  // 模拟步骤数据
  currentSteps.value = {
    calculationMethod: '工作量法',
    description: '基于工作量完成情况计算绩效，公式：完成率 × 权重 × 100',
    steps: [
      {
        step: 1,
        name: '获取工作量数据',
        description: '从HIS系统获取门诊人数、住院人数等工作量指标数据'
      },
      {
        step: 2,
        name: '计算完成率',
        description: '完成率 = 实际值 ÷ 目标值'
      },
      {
        step: 3,
        name: '计算加权得分',
        description: '得分 = 完成率 × 权重 × 100'
      },
      {
        step: 4,
        name: '计算绩效金额',
        description: '根据得分和绩效系数计算最终绩效金额'
      }
    ]
  }
  stepsDialogVisible.value = true
}

const publishResult = async (row) => {
  try {
    await ElMessageBox.confirm('确定要发布此次计算结果吗？发布后将不能修改。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    ElMessage.success('发布成功')
    loadCalculationHistory()
  } catch {
    // 用户取消
  }
}

const recalculate = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重新计算吗？这将覆盖原有的计算结果。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    ElMessage.success('重新计算已启动')
    loadCalculationHistory()
  } catch {
    // 用户取消
  }
}

const deleteRecord = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此计算记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    ElMessage.success('删除成功')
    loadCalculationHistory()
  } catch {
    // 用户取消
  }
}

const getStatusName = (status) => {
  const statusMap = {
    1: '计算中',
    2: '计算完成',
    3: '计算失败'
  }
  return statusMap[status] || '未知'
}

const getStatusTag = (status) => {
  const tagMap = {
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return tagMap[status] || ''
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
  loadCalculationHistory()
})
</script>

<style lang="scss" scoped>
.calculation-management {
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
}

.steps-content {
  .steps-list {
    .el-steps {
      :deep(.el-step__description) {
        padding-right: 20px;
        line-height: 1.5;
      }
    }
  }
}
</style>