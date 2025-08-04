<template>
  <div class="user-management">
    <div class="table-container">
      <div class="table-header">
        <div class="table-title">用户管理</div>
      </div>
      
      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        
        <div class="toolbar-right">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户名、姓名、工号"
            style="width: 250px"
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
      
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" @click="handleResetPassword(row)">
              重置密码
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const multipleSelection = ref([])

const searchForm = reactive({
  keyword: '',
  deptId: null,
  status: null
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
    username: 'admin',
    realName: '系统管理员',
    employeeNo: 'ADMIN001',
    phone: '13800138000',
    email: 'admin@hospital.com',
    position: '系统管理员',
    status: 1,
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    username: 'doctor01',
    realName: '张医生',
    employeeNo: 'DOC001',
    phone: '13800138001',
    email: 'doctor01@hospital.com',
    position: '主治医师',
    status: 1,
    createTime: '2024-01-02 10:00:00'
  },
  {
    id: 3,
    username: 'nurse01',
    realName: '李护士',
    employeeNo: 'NUR001',
    phone: '13800138002',
    email: 'nurse01@hospital.com',
    position: '护士长',
    status: 1,
    createTime: '2024-01-03 10:00:00'
  }
]

const loadData = async () => {
  loading.value = true
  try {
    // 这里应该调用实际的API
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = mockData
    pagination.total = mockData.length
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleAdd = () => {
  ElMessage.info('新增用户功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑用户: ${row.realName}`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.realName}" 吗？`, '提示', {
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

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${multipleSelection.value.length} 个用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    ElMessage.success('批量删除成功')
    loadData()
  } catch {
    // 用户取消
  }
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 "${row.realName}" 的密码吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    ElMessage.success('密码重置成功，新密码为：123456')
  } catch {
    // 用户取消
  }
}

const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
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
.user-management {
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

@media (max-width: 768px) {
  .user-management {
    .table-toolbar {
      flex-direction: column;
      gap: 10px;
      align-items: stretch;
      
      .toolbar-right {
        justify-content: flex-end;
      }
    }
  }
}
</style>