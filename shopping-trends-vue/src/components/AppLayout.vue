<template>
  <div class="app-container">
    <el-container class="layout-container">
      <!-- 顶端栏：Logo + 导航菜单 + 用户 -->
      <el-header class="topbar">
        <div class="topbar-left">
          <div class="logo-container">
            <img src="../assets/logo.png" alt="Logo" class="logo" />
          </div>
          <el-menu
            :default-active="activeMenu"
            class="topbar-menu"
            mode="horizontal"
            router
            background-color="transparent"
            text-color="#fff"
            active-text-color="#409EFF"
          >
            <el-menu-item v-for="route in routes" :key="route.path" :index="route.path">
              <el-icon>
                <component :is="route.meta.icon" />
              </el-icon>
              <template #title>{{ route.meta.title }}</template>
            </el-menu-item>
          </el-menu>
        </div>
        <div class="topbar-right">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta && currentRoute.meta.title && currentRoute.path !== '/'">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
              <span class="username">管理员</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">管理员账户管理</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>

      <!-- 页脚 -->
      <el-footer class="footer">
        <div class="footer-content">
          <span>电商交易数据分析系统 &copy; {{ currentYear }}</span>
          <span>版本 v1.0.0</span>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import apiClient from '@/utils/apiClient';
import { AUTH_LOGIN_ID_KEY, AUTH_SESSION_KEY } from '@/constants/auth';

const router = useRouter();
const route = useRoute();

// 当前年份
const currentYear = new Date().getFullYear();

// 获取路由列表（用于菜单，仅显示当前布局下的子路由）
const routes = computed(() => {
  const parent = route.matched.find(r => r.children && r.children.length);
  if (!parent || !parent.children) return [];
  const base = parent.path === '/' ? '' : parent.path.replace(/\/$/, '');
  return parent.children
    .filter(r => r.meta && r.meta.title && !r.meta.hidden)
    .map(r => ({
      path: (base + '/' + (r.path || '')).replace(/\/+/g, '/') || '/',
      meta: r.meta
    }));
});

// 当前激活的菜单项
const activeMenu = computed(() => {
  return route.path;
});

// 当前路由信息
const currentRoute = computed(() => {
  return route;
});

// 退出登录（通知后端作废会话，操作日志记「退出」）
const handleLogout = async () => {
  const token = localStorage.getItem(AUTH_SESSION_KEY);
  if (token) {
    try {
      await apiClient.post('/api/admin/logout');
    } catch {
      /* 仍清除本地态 */
    }
  }
  localStorage.removeItem(AUTH_LOGIN_ID_KEY);
  localStorage.removeItem(AUTH_SESSION_KEY);
  router.push('/login');
};
</script>

<style scoped>
.app-container {
  height: 100vh;
  width: 100%;
}

.layout-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 0 !important;
}

.layout-container :deep(.el-container) {
  gap: 0 !important;
}

/* 去掉顶端栏与内容区之间的白线/阴影，保证与内容区对齐 */
.layout-container :deep(.el-header) {
  border-bottom: none !important;
  box-shadow: none !important;
  overflow: hidden;
  margin-bottom: 0 !important;
}

.layout-container :deep(.el-main) {
  border: none !important;
  border-top: none !important;
  box-shadow: none !important;
  margin: 0 !important;
  margin-top: -1px !important; /* 向上贴齐，盖住可能残留的 1px 线 */
  padding-top: 0 !important;
  outline: none !important;
}

/* 顶端栏样式 */
.topbar {
  background-color: #000000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  flex-shrink: 0;
  border: none !important;
  border-bottom: none !important;
  box-shadow: none !important;
  overflow: hidden;
}

.topbar-left {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
}

.logo-container {
  display: flex;
  align-items: center;
  margin-right: 24px;
  flex-shrink: 0;
}

.logo {
  height: 36px;
  max-width: 140px;
  object-fit: contain;
}

.topbar-menu {
  border-bottom: none;
  flex: 1;
  min-width: 0;
  /* 让菜单区域撑满顶端栏高度，避免白线露在栏中间 */
  align-self: stretch;
  display: flex;
}

.topbar-menu :deep(.el-menu--horizontal) {
  border-bottom: none !important;
  border: none !important;
  height: 100% !important;
  flex: 1;
}

.topbar-menu :deep(.el-menu--horizontal .el-menu) {
  border: none !important;
  height: 100% !important;
}

/* 去掉水平菜单底部白线（Element 默认下边框/伪元素） */
.topbar-menu :deep(.el-menu--horizontal::after),
.topbar-menu :deep(.el-menu::after) {
  display: none !important;
}

.topbar-menu :deep(.el-menu--horizontal > .el-menu) {
  border-bottom: none !important;
}

.topbar-menu :deep(.el-menu-item) {
  font-size: 14px;
  height: 60px;
  line-height: 60px;
  border-bottom: none !important;
}

.topbar-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 6px;
}

.topbar-menu :deep(.el-menu--horizontal > .el-menu-item) {
  margin: 0 4px;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
}

.topbar-right .breadcrumb :deep(.el-breadcrumb__item .el-breadcrumb__inner) {
  color: rgba(255, 255, 255, 0.8);
}

.topbar-right .breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
}

.username {
  margin-left: 8px;
  font-size: 14px;
}

/* 主内容区域样式（内边距只留左右下，顶与顶端栏贴齐） */
.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  flex: 1;
  overflow-y: auto;
  box-shadow: none;
  margin: 0;
  margin-top: -1px; /* 与 el-main 一起上移，盖住缝隙/白线 */
}

/* 页脚样式 */
.footer {
  background-color: #fff;
  padding: 15px 20px;
  text-align: center;
  color: #666;
  font-size: 14px;
  border-top: 1px solid #e8e8e8;
  flex-shrink: 0;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .topbar {
    padding: 0 12px;
    flex-wrap: wrap;
    height: auto;
    min-height: 60px;
  }

  .logo {
    max-width: 100px;
    height: 28px;
  }

  .topbar-menu :deep(.el-menu-item) {
    padding: 0 8px;
    font-size: 13px;
  }

  .topbar-menu :deep(.el-menu-item .el-icon) {
    display: none;
  }

  .username {
    display: none;
  }

  .breadcrumb {
    display: none;
  }

  .main-content {
    padding: 10px;
  }
}
</style>
