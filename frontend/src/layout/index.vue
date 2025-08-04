<template>
  <div class="layout-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '240px'" class="layout-sidebar">
        <div class="sidebar-header">
          <div v-if="!isCollapse" class="logo">
            <h2>绩效系统</h2>
          </div>
          <div v-else class="logo-mini">
            <span>绩</span>
          </div>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          :default-openeds="defaultOpeneds"
          :collapse="isCollapse"
          :unique-opened="false"
          class="sidebar-menu"
          @select="handleMenuSelect"
          mode="vertical"
        >
          <template v-for="route in menuRoutes" :key="route.path">
            <el-sub-menu v-if="route.children && route.children.length > 0" :index="route.path">
              <template #title>
                <el-icon><component :is="route.meta.icon" /></el-icon>
                <span>{{ route.meta.title }}</span>
              </template>
              <el-menu-item
                v-for="child in route.children"
                :key="child.path"
                :index="child.path"
              >
                <el-icon><component :is="child.meta.icon" /></el-icon>
                <span>{{ child.meta.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            
            <el-menu-item v-else :index="route.path">
              <el-icon><component :is="route.meta.icon" /></el-icon>
              <span>{{ route.meta.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="layout-header">
          <div class="header-left">
            <el-button
              type="text"
              size="large"
              @click="toggleCollapse"
            >
              <el-icon><Expand v-if="isCollapse" /><Fold v-else /></el-icon>
            </el-button>
            
            <el-breadcrumb separator="/">
              <el-breadcrumb-item
                v-for="item in breadcrumbs"
                :key="item.path"
                :to="item.path"
              >
                {{ item.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="32" :src="userStore.avatar">
                  {{ userStore.realName?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="username">{{ userStore.realName || userStore.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="password">修改密码</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 主内容 -->
        <el-main class="layout-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

// 菜单路由
const menuRoutes = computed(() => {
  return router.options.routes
    .find(r => r.path === '/')
    ?.children?.filter(child => child.meta?.title && !child.meta?.hidden) || []
})

// 当前激活的菜单
const activeMenu = computed(() => {
  const currentPath = route.path
  console.log('Computing activeMenu for path:', currentPath)
  
  // 直接返回当前路径，Element Plus会自动处理父子菜单的激活状态
  return currentPath
})

// 默认展开的菜单
const defaultOpeneds = computed(() => {
  const currentPath = route.path
  const openeds = []
  
  // 查找当前路径对应的父菜单
  for (const parentRoute of menuRoutes.value) {
    if (parentRoute.children) {
      const hasActiveChild = parentRoute.children.some(child => child.path === currentPath)
      if (hasActiveChild) {
        openeds.push(parentRoute.path)
        console.log('Auto-opening parent menu:', parentRoute.path, 'for current path:', currentPath)
      }
    }
  }
  
  return openeds
})

// 面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  const breadcrumbs = []
  
  matched.forEach(item => {
    if (item.path !== '/') {
      breadcrumbs.push({
        path: item.path,
        title: item.meta.title
      })
    }
  })
  
  return breadcrumbs
})

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 处理菜单选择
const handleMenuSelect = (index) => {
  console.log('Menu selected:', index, 'Current route:', route.path, 'menuRoutes:', menuRoutes.value)
  
  // 查找选中的路由（可能是父菜单或子菜单）
  let selectedRoute = menuRoutes.value.find(route => route.path === index)
  
  // 如果没找到，可能是子菜单，需要在父菜单的children中查找
  if (!selectedRoute) {
    for (const parentRoute of menuRoutes.value) {
      if (parentRoute.children) {
        const childRoute = parentRoute.children.find(child => child.path === index)
        if (childRoute) {
          selectedRoute = childRoute
          break
        }
      }
    }
  }
  
  console.log('Found selectedRoute:', selectedRoute)
  
  // 如果是父菜单（有子菜单），不进行路由跳转，让Element Plus处理展开/收起
  if (selectedRoute?.children?.length > 0) {
    console.log('Parent menu clicked, letting Element Plus handle expansion')
    return
  }
  
  // 叶子菜单或子菜单项进行路由跳转
  if (index && index !== route.path) {
    console.log('Navigating to:', index)
    router.push(index).catch(err => {
      console.error('Navigation error:', err)
    })
  } else {
    console.log('Same route or invalid index, no navigation needed')
  }
}

// 处理用户下拉菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人信息功能开发中...')
      break
    case 'password':
      ElMessage.info('修改密码功能开发中...')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        userStore.logout()
        router.push('/login')
        ElMessage.success('退出登录成功')
      } catch {
        // 用户取消
      }
      break
  }
}

// 监听路由变化，自动展开对应的菜单
watch(
  () => route.path,
  (newPath) => {
    console.log('=== Route changed to:', newPath, '===')
    console.log('Current menuRoutes:', menuRoutes.value)
    console.log('ActiveMenu:', activeMenu.value)
    console.log('DefaultOpeneds:', defaultOpeneds.value)
    
    // 查找当前路由对应的父菜单
    const parentRoute = menuRoutes.value.find(route => 
      route.children?.some(child => child.path === newPath)
    )
    
    if (parentRoute) {
      console.log('Found parent route for current path:', parentRoute.path)
    } else {
      console.log('No parent route found - this is a top-level menu item')
    }
    
    // 检查菜单数据完整性
    console.log('Menu structure check:')
    menuRoutes.value.forEach(route => {
      console.log(`- ${route.meta?.title} (${route.path})`, route.children ? `has ${route.children.length} children` : 'no children')
    })
  },
  { immediate: true }
)

// 添加菜单数据调试
watch(
  menuRoutes,
  (newRoutes) => {
    console.log('MenuRoutes updated:', newRoutes)
    newRoutes.forEach(route => {
      if (route.children?.length > 0) {
        console.log(`Parent menu: ${route.meta?.title} (${route.path}) has ${route.children.length} children:`)
        route.children.forEach(child => {
          console.log(`  - Child: ${child.meta?.title} (${child.path})`)
        })
      }
    })
  },
  { immediate: true, deep: true }
)
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  
  .el-container {
    height: 100%;
  }
}

.layout-sidebar {
  background: #304156;
  transition: width 0.3s;
  
  .sidebar-header {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #263445;
    
    .logo {
      color: #fff;
      font-weight: 600;
      font-size: 18px;
    }
    
    .logo-mini {
      color: #fff;
      font-weight: 600;
      font-size: 20px;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #409eff;
      border-radius: 6px;
    }
  }
  
  .sidebar-menu {
    border: none;
    background: #304156;
    
    :deep(.el-menu-item) {
      color: #bfcbd9;
      
      &:hover {
        background: #263445;
        color: #fff;
      }
      
      &.is-active {
        background: #409eff;
        color: #fff;
      }
    }
    
    :deep(.el-sub-menu) {
      .el-sub-menu__title {
        color: #bfcbd9;
        
        &:hover {
          background: #263445;
          color: #fff;
        }
      }
      
      &.is-opened {
        .el-sub-menu__title {
          background: #263445;
        }
      }
      
      .el-menu {
        background: #1f2d3d;
        
        .el-menu-item {
          color: #bfcbd9;
          padding-left: 50px !important;
          
          &:hover {
            background: #001528;
            color: #fff;
          }
          
          &.is-active {
            background: #409eff;
            color: #fff;
          }
        }
      }
    }
    
    // 确保子菜单展开动画正常
    :deep(.el-menu--collapse) {
      .el-sub-menu {
        .el-sub-menu__title {
          .el-sub-menu__icon-arrow {
            display: none;
          }
        }
      }
    }
  }
}

.layout-header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .el-button {
      color: #606266;
    }
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 8px 12px;
      border-radius: 6px;
      transition: background-color 0.3s;
      
      &:hover {
        background: #f5f7fa;
      }
      
      .username {
        font-size: 14px;
        color: #303133;
      }
    }
  }
}

.layout-content {
  background: #f0f2f5;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .layout-sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1000;
  }
  
  .layout-header {
    padding: 0 15px;
    
    .header-left {
      gap: 10px;
    }
  }
}
</style>