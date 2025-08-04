<template>
  <div class="department-management">
    <div class="table-container">
      <div class="table-header">
        <div class="table-title">部门管理</div>
      </div>
      
      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增部门
        </el-button>
        <el-button @click="expandAll">
          <el-icon><Expand /></el-icon>
          展开全部
        </el-button>
        <el-button @click="collapseAll">
          <el-icon><Fold /></el-icon>
          折叠全部
        </el-button>
      </div>
      
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="tableData"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
      >
        <el-table-column prop="deptName" label="部门名称" width="200">
          <template #default="{ row }">
            <span>{{ row.deptName }}</span>
            <el-tag v-if="row.isAccountingUnit" type="success" size="small" style="margin-left: 8px">
              核算单元
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deptCode" label="部门编码" width="120" />
        <el-table-column prop="deptType" label="部门类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getDeptTypeTag(row.deptType)">
              {{ getDeptTypeName(row.deptType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="leaderName" label="负责人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="bedCount" label="床位数" width="80" />
        <el-table-column prop="staffCount" label="编制数" width="80" />
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
            <el-button type="success" size="small" @click="handleAddChild(row)">
              新增子部门
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const tableRef = ref()

// 模拟部门树数据
const mockData = [
  {
    id: 1,
    deptCode: 'ROOT',
    deptName: '医院',
    parentId: 0,
    level: 1,
    deptType: 3,
    leaderName: '院长',
    phone: '0571-12345678',
    status: 1,
    isAccountingUnit: 1,
    bedCount: 0,
    staffCount: 0,
    children: [
      {
        id: 2,
        deptCode: 'NKXS',
        deptName: '内科系',
        parentId: 1,
        level: 2,
        deptType: 1,
        leaderName: '内科主任',
        phone: '0571-12345679',
        status: 1,
        isAccountingUnit: 1,
        bedCount: 0,
        staffCount: 0,
        children: [
          {
            id: 3,
            deptCode: 'XHNK',
            deptName: '心血管内科',
            parentId: 2,
            level: 3,
            deptType: 1,
            leaderName: '张主任',
            phone: '0571-12345680',
            status: 1,
            isAccountingUnit: 1,
            bedCount: 45,
            staffCount: 25
          },
          {
            id: 4,
            deptCode: 'XHWK',
            deptName: '心血管外科',
            parentId: 2,
            level: 3,
            deptType: 1,
            leaderName: '李主任',
            phone: '0571-12345681',
            status: 1,
            isAccountingUnit: 1,
            bedCount: 30,
            staffCount: 20
          }
        ]
      },
      {
        id: 5,
        deptCode: 'WKXS',
        deptName: '外科系',
        parentId: 1,
        level: 2,
        deptType: 1,
        leaderName: '外科主任',
        phone: '0571-12345682',
        status: 1,
        isAccountingUnit: 1,
        bedCount: 0,
        staffCount: 0,
        children: [
          {
            id: 6,
            deptCode: 'PTWK',
            deptName: '普通外科',
            parentId: 5,
            level: 3,
            deptType: 1,
            leaderName: '王主任',
            phone: '0571-12345683',
            status: 1,
            isAccountingUnit: 1,
            bedCount: 40,
            staffCount: 22
          },
          {
            id: 7,
            deptCode: 'GUKWK',
            deptName: '骨科',
            parentId: 5,
            level: 3,
            deptType: 1,
            leaderName: '赵主任',
            phone: '0571-12345684',
            status: 1,
            isAccountingUnit: 1,
            bedCount: 35,
            staffCount: 18
          }
        ]
      },
      {
        id: 8,
        deptCode: 'YJXS',
        deptName: '医技系',
        parentId: 1,
        level: 2,
        deptType: 2,
        leaderName: '医技主任',
        phone: '0571-12345685',
        status: 1,
        isAccountingUnit: 1,
        bedCount: 0,
        staffCount: 0,
        children: [
          {
            id: 9,
            deptCode: 'YXKX',
            deptName: '医学检验科',
            parentId: 8,
            level: 3,
            deptType: 2,
            leaderName: '检验科主任',
            phone: '0571-12345686',
            status: 1,
            isAccountingUnit: 1,
            bedCount: 0,
            staffCount: 15
          },
          {
            id: 10,
            deptCode: 'YXYX',
            deptName: '医学影像科',
            parentId: 8,
            level: 3,
            deptType: 2,
            leaderName: '影像科主任',
            phone: '0571-12345687',
            status: 1,
            isAccountingUnit: 1,
            bedCount: 0,
            staffCount: 12
          }
        ]
      }
    ]
  }
]

const getDeptTypeName = (type) => {
  const typeMap = {
    1: '临床科室',
    2: '医技科室',
    3: '行政科室',
    4: '护理单元'
  }
  return typeMap[type] || '未知'
}

const getDeptTypeTag = (type) => {
  const tagMap = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    4: 'info'
  }
  return tagMap[type] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    // 这里应该调用实际的API
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = mockData
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  ElMessage.info('新增部门功能开发中...')
}

const handleEdit = (row) => {
  ElMessage.info(`编辑部门: ${row.deptName}`)
}

const handleAddChild = (row) => {
  ElMessage.info(`为 "${row.deptName}" 新增子部门`)
}

const handleDelete = async (row) => {
  if (row.children && row.children.length > 0) {
    ElMessage.warning('该部门下有子部门，无法删除')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除部门 "${row.deptName}" 吗？`, '提示', {
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

const expandAll = () => {
  // 展开所有节点
  const expandAllNodes = (data) => {
    data.forEach(node => {
      tableRef.value.toggleRowExpansion(node, true)
      if (node.children && node.children.length > 0) {
        expandAllNodes(node.children)
      }
    })
  }
  expandAllNodes(tableData.value)
}

const collapseAll = () => {
  // 折叠所有节点
  const collapseAllNodes = (data) => {
    data.forEach(node => {
      tableRef.value.toggleRowExpansion(node, false)
      if (node.children && node.children.length > 0) {
        collapseAllNodes(node.children)
      }
    })
  }
  collapseAllNodes(tableData.value)
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.department-management {
  .table-toolbar {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }
}
</style>