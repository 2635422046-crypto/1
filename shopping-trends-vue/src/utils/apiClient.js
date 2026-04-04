import axios from 'axios';
import { AUTH_SESSION_KEY } from '@/constants/auth';

// 创建axios实例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器：携带管理员会话（与后端 X-Admin-Session 一致）
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(AUTH_SESSION_KEY);
    if (token) {
      config.headers = config.headers || {};
      config.headers['X-Admin-Session'] = token;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

export default apiClient;
