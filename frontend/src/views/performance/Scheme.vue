<template>
  <div class="scheme-management">
    <!-- 查询条件 -->
    <div class="search-container">
      <el-form :model="searchForm" inline>
        <el-form-item label="方案名称">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入方案名称或编码"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="方案类型">
          <el-select v-model="searchForm.schemeType" placeholder="请选择方案类型" clearable style="width: 150px">
            <el-option label="工作量法" :value="1" />
            <el-option label="KPI方法" :value="2" />
            <el-option label="成本核算法" :value="3" />
            <el-option label="混合方法" :value="4" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="loadSchemeList">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 操作按钮 -->
    <div class="table-container">
      <div class="table-header">
        <div class="table-title">绩效方案管理</div>
        <div>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新增方案
          </el-button>
          <el-button type="success" @click="batchSetDefault" :disabled="!selectedSchemes.length">
            <el-icon><Star /></el-icon>
            批量设为默认
          </el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table 
        v-loading="loading" 
        :data="schemeList" 
        @selection-change="handleSelectionChange"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="schemeCode" label="方案编码" width="120" />
        <el-table-column prop="schemeName" label="方案名称" width="200" />
        <el-table-column prop="schemeType" label="方案类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getSchemeTypeTag(row.schemeType)">
              {{ getSchemeTypeName(row.schemeType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicableDeptType" label="适用范围" width="120">
          <template #default="{ row }">
            {{ getDeptTypeName(row.applicableDeptType) }}
          </template>
        </el-table-column>
        <el-table-column prop="isDefault" label="默认方案" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="success">是</el-tag>
            <span v-else>否</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewScheme(row)">
              查看
            </el-button>
            <el-button type="info" size="small" @click="editScheme(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.isDefault !== 1"
              type="success" 
              size="small" 
              @click="setDefault(row)"
            >
              设为默认
            </el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              v-if="row.isDefault !== 1"
              type="danger" 
              size="small" 
              @click="deleteScheme(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadSchemeList"
          @current-change="loadSchemeList"
        />
      </div>
    </div>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="60%" 
      :close-on-click-modal="false"
    >
      <el-form 
        :model="schemeForm" 
        :rules="schemeRules" 
        ref="schemeFormRef" 
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="方案编码" prop="schemeCode">
              <el-input 
                v-model="schemeForm.schemeCode" 
                placeholder="请输入方案编码"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="方案名称" prop="schemeName">
              <el-input v-model="schemeForm.schemeName" placeholder="请输入方案名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="方案类型" prop="schemeType">
              <el-select v-model="schemeForm.schemeType" placeholder="请选择方案类型" style="width: 100%">
                <el-option label="工作量法" :value="1" />
                <el-option label="KPI方法" :value="2" />
                <el-option label="成本核算法" :value="3" />
                <el-option label="混合方法" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="适用部门类型" prop="applicableDeptType">
              <el-select v-model="schemeForm.applicableDeptType" placeholder="请选择适用部门类型" style="width: 100%">
                <el-option label="临床科室" :value="1" />
                <el-option label="医技科室" :value="2" />
                <el-option label="护理单元" :value="3" />
                <el-option label="行政科室" :value="4" />
                <el-option label="全院适用" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计算周期" prop="calculationCycle">
              <el-select v-model="schemeForm.calculationCycle" placeholder="请选择计算周期" style="width: 100%">
                <el-option label="月度" :value="1" />
                <el-option label="季度" :value="2" />
                <el-option label="半年度" :value="3" />
                <el-option label="年度" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="schemeForm.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="方案描述" prop="description">
          <el-input 
            v-model="schemeForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入方案描述"
          />
        </el-form-item>
        
        <el-form-item label="计算规则" prop="calculationRules">
          <el-input 
            v-model="schemeForm.calculationRules" 
            type="textarea" 
            :rows="4"
            placeholder="请输入计算规则说明"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveScheme" :loading="saving">
          {{ saving ? '保存中...' : '确定' }}
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="方案详情" width="50%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="方案编码">{{ currentScheme.schemeCode }}</el-descriptions-item>
        <el-descriptions-item label="方案名称">{{ currentScheme.schemeName }}</el-descriptions-item>
        <el-descriptions-item label="方案类型">{{ getSchemeTypeName(currentScheme.schemeType) }}</el-descriptions-item>
        <el-descriptions-item label="适用范围">{{ getDeptTypeName(currentScheme.applicableDeptType) }}</el-descriptions-item>
        <el-descriptions-item label="计算周期">{{ getCalculationCycleName(currentScheme.calculationCycle) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentScheme.status === 1 ? 'success' : 'danger'">
            {{ currentScheme.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="默认方案">
          <el-tag v-if="currentScheme.isDefault === 1" type="success">是</el-tag>
          <span v-else>否</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentScheme.createTime }}</el-descriptions-item>
        <el-descriptions-item label="方案描述" span="2">{{ currentScheme.description }}</el-descriptions-item>
        <el-descriptions-item label="计算规则" span="2">{{ currentScheme.calculationRules }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const schemeFormRef = ref()

const searchForm = reactive({
  keyword: '',
  schemeType: null,
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const schemeForm = reactive({
  id: null,
  schemeCode: '',
  schemeName: '',
  schemeType: null,
  applicableDeptType: null,
  calculationCycle: 1,
  status: 1,
  description: '',
  calculationRules: ''
})

const schemeRules = {
  schemeCode: [
    { required: true, message: '请输入方案编码', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  schemeName: [
    { required: true, message: '请输入方案名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  schemeType: [
    { required: true, message: '请选择方案类型', trigger: 'change' }
  ],
  applicableDeptType: [
    { required: true, message: '请选择适用部门类型', trigger: 'change' }
  ]
}

const schemeList = ref([])
const selectedSchemes = ref([])
const currentScheme = ref({})

// 模拟数据
const mockSchemes = [
  {
    id: 1,
    schemeCode: 'LCKSZX',
    schemeName: '临床科室默认方案',
    schemeType: 1,
    applicableDeptType: 1,
    calculationCycle: 1,
    status: 1,
    isDefault: 1,
    description: '适用于临床科室的工作量法绩效考核方案',
    calculationRules: '基于门诊量、住院量、手术量等工作量指标进行绩效计算',
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    schemeCode: 'YJKSZX',
    schemeName: '医技科室默认方案',
    schemeType: 2,
    applicableDeptType: 2,
    calculationCycle: 1,
    status: 1,
    isDefault: 1,
    description: '适用于医技科室的KPI方法绩效考核方案',
    calculationRules: '基于检查量、设备利用率、服务质量等KPI指标进行绩效计算',
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 3,
    schemeCode: 'XZKSZX',
    schemeName: '行政科室默认方案',
    schemeType: 3,
    applicableDeptType: 4,
    calculationCycle: 1,
    status: 1,
    isDefault: 1,
    description: '适用于行政科室的成本核算法绩效考核方案',
    calculationRules: '基于成本控制、工作效率、服务质量等指标进行绩效计算',
    createTime: '2024-01-01 10:00:00'
  }
]

const dialogTitle = computed(() => isEdit.value ? '编辑方案' : '新增方案')

const loadSchemeList = () => {
  loading.value = true
  setTimeout(() => {
    schemeList.value = mockSchemes
    pagination.total = mockSchemes.length
    loading.value = false
  }, 500)
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.schemeType = null
  searchForm.status = null
  loadSchemeList()
}

const handleSelectionChange = (selection) => {
  selectedSchemes.value = selection
}

const showAddDialog = () => {
  isEdit.value = false
  resetSchemeForm()
  dialogVisible.value = true
}

const editScheme = (row) => {
  isEdit.value = true
  Object.assign(schemeForm, row)
  dialogVisible.value = true
}

const viewScheme = (row) => {
  currentScheme.value = row
  viewDialogVisible.value = true
}

const resetSchemeForm = () => {
  Object.assign(schemeForm, {
    id: null,
    schemeCode: '',
    schemeName: '',
    schemeType: null,
    applicableDeptType: null,
    calculationCycle: 1,
    status: 1,
    description: '',
    calculationRules: ''
  })
  schemeFormRef.value?.clearValidate()
}

const saveScheme = async () => {
  if (!schemeFormRef.value) return
  
  try {
    const valid = await schemeFormRef.value.validate()
    if (!valid) return
    
    saving.value = true
    
    // 模拟保存过程
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success(isEdit.value ? '方案更新成功' : '方案创建成功')
    dialogVisible.value = false
    loadSchemeList()
    
  } catch (error) {
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const setDefault = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要将"${row.schemeName}"设为默认方案吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('设置成功')
    loadSchemeList()
  } catch {
    // 用户取消
  }
}

const batchSetDefault = async () => {
  if (selectedSchemes.value.length === 0) {
    ElMessage.warning('请先选择要设置的方案')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要批量设置 ${selectedSchemes.value.length} 个方案为默认方案吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('批量设置成功')
    loadSchemeList()
  } catch {
    // 用户取消
  }
}

const toggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}"${row.schemeName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success(`${action}成功`)
    loadSchemeList()
  } catch {
    // 用户取消
  }
}

const deleteScheme = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除"${row.schemeName}"吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    ElMessage.success('删除成功')
    loadSchemeList()
  } catch {
    // 用户取消
  }
}

const getSchemeTypeName = (type) => {
  const typeMap = {
    1: '工作量法',
    2: 'KPI方法',
    3: '成本核算法',
    4: '混合方法'
  }
  return typeMap[type] || '未知'
}

const getSchemeTypeTag = (type) => {
  const tagMap = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'info'
  }
  return tagMap[type] || ''
}

const getDeptTypeName = (type) => {
  const typeMap = {
    1: '临床科室',
    2: '医技科室',
    3: '护理单元',
    4: '行政科室',
    5: '全院适用'
  }
  return typeMap[type] || '未知'
}

const getCalculationCycleName = (cycle) => {
  const cycleMap = {
    1: '月度',
    2: '季度',
    3: '半年度',
    4: '年度'
  }
  return cycleMap[cycle] || '未知'
}

onMounted(() => {
  loadSchemeList()
})
</script>

<style lang="scss" scoped>
.scheme-management {
  .search-container {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}
</style>