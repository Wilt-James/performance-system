import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: '/system',
        name: 'System',
        component: () => import('@/views/ParentView.vue'),
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'Setting' },
        children: [
          {
            path: '/system/user',
            name: 'SystemUser',
            component: () => import('@/views/system/User.vue'),
            meta: { title: '用户管理', icon: 'User' }
          },
          {
            path: '/system/department',
            name: 'SystemDepartment',
            component: () => import('@/views/system/Department.vue'),
            meta: { title: '部门管理', icon: 'OfficeBuilding' }
          }
        ]
      },
      {
        path: '/performance',
        name: 'Performance',
        component: () => import('@/views/ParentView.vue'),
        redirect: '/performance/indicator',
        meta: { title: '绩效管理', icon: 'TrendCharts' },
        children: [
          {
            path: '/performance/indicator',
            name: 'PerformanceIndicator',
            component: () => import('@/views/performance/Indicator.vue'),
            meta: { title: '指标管理', icon: 'DataAnalysis' }
          },
          {
            path: '/performance/scheme',
            name: 'PerformanceScheme',
            component: () => import('@/views/performance/Scheme.vue'),
            meta: { title: '方案管理', icon: 'Document' }
          },
          {
            path: '/performance/calculation',
            name: 'PerformanceCalculation',
            component: () => import('@/views/performance/Calculation.vue'),
            meta: { title: '绩效计算', icon: 'Calculator' }
          },
          {
            path: '/performance/report',
            name: 'PerformanceReport',
            component: () => import('@/views/performance/Report.vue'),
            meta: { title: '绩效报表', icon: 'Histogram' }
          }
        ]
      },
      {
        path: '/statistics',
        name: 'Statistics',
        component: () => import('@/views/ParentView.vue'),
        redirect: '/statistics/multi-dimension',
        meta: { title: '统计分析', icon: 'PieChart' },
        children: [
          {
            path: '/statistics/multi-dimension',
            name: 'MultiDimensionStats',
            component: () => import('@/views/statistics/MultiDimension.vue'),
            meta: { title: '多口径统计', icon: 'DataBoard' }
          },
          {
            path: '/statistics/operation-score',
            name: 'OperationScore',
            component: () => import('@/views/statistics/OperationScore.vue'),
            meta: { title: '运营评分', icon: 'Medal' }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/404.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 院内绩效考核系统` : '院内绩效考核系统'
  
  // 检查是否需要认证
  if (to.meta.requiresAuth !== false) {
    if (!userStore.token) {
      next('/login')
      return
    }
  }
  
  // 如果已登录且访问登录页，重定向到首页
  if (to.path === '/login' && userStore.token) {
    next('/')
    return
  }
  
  next()
})

export default router