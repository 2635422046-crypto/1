import apiClient from '@/utils/apiClient';
import { AUTH_SESSION_KEY } from '@/constants/auth';

/**
 * 前端上报（需已登录且带会话），用于将图表下载等记入操作日志
 */
export async function reportClientOperation(action, detail) {
  const token = localStorage.getItem(AUTH_SESSION_KEY);
  if (!token || !action) return;
  try {
    await apiClient.post('/api/admin/operation-log', {
      action,
      detail: detail != null ? String(detail) : ''
    });
  } catch (e) {
    /* 不影响本地下载；失败时打日志便于排查（如会话过期、跨域、后端未启动） */
    console.warn('[operation-log] 上报失败:', e?.response?.data?.message || e?.message || e);
  }
}
