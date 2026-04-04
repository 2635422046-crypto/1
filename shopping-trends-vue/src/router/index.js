import { createRouter, createWebHistory } from 'vue-router';
import AppLayout from '../components/AppLayout.vue';
import Home from '../views/Home.vue';
import Dashboard from '../components/Dashboard.vue';
import UserAnalysis from '../views/UserAnalysis.vue';
import ProductAnalysis from '../views/ProductAnalysis.vue';
import ComprehensiveAnalysis from '../views/ComprehensiveAnalysis.vue';
import Login from '../views/Login.vue';
import Profile from '../views/Profile.vue';
import { AUTH_LOGIN_ID_KEY, AUTH_SESSION_KEY } from '@/constants/auth';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/',
    component: AppLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Home',
        component: Home,
        meta: { title: '首页', icon: 'el-icon-house' }
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '经营数据概览', icon: 'el-icon-menu' }
      },
      {
        path: 'user-analysis',
        name: 'UserAnalysis',
        component: UserAnalysis,
        meta: { title: '客户分析', icon: 'el-icon-user' }
      },
      {
        path: 'product-analysis',
        name: 'ProductAnalysis',
        component: ProductAnalysis,
        meta: { title: '商品结构分析', icon: 'el-icon-shopping-cart' }
      },
      {
        path: 'comprehensive-analysis',
        name: 'ComprehensiveAnalysis',
        component: ComprehensiveAnalysis,
        meta: { title: '综合分析', icon: 'el-icon-data-analysis' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { title: '管理员账户', hidden: true }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const loggedIn =
    !!localStorage.getItem(AUTH_LOGIN_ID_KEY) && !!localStorage.getItem(AUTH_SESSION_KEY);
  if (to.meta.requiresAuth && !loggedIn) {
    next({ path: '/login' });
    return;
  }
  if (to.path === '/login' && loggedIn) {
    next({ path: '/' });
    return;
  }
  document.title = to.meta.title ? `${to.meta.title} - 电商交易数据分析系统` : '电商交易数据分析系统';
  next();
});

export default router;
