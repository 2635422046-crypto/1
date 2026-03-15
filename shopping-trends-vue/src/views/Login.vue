<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="title">电商交易分析系统</h1>
      <p class="subtitle">管理员登录</p>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="登录ID" prop="loginId">
          <el-input
            v-model="form.loginId"
            placeholder="请输入登录ID"
            size="large"
            clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import apiClient from '@/utils/apiClient';

const router = useRouter();
const formRef = ref(null);
const loading = ref(false);

const form = reactive({
  loginId: '',
  password: ''
});

const rules = {
  loginId: [{ required: true, message: '请输入登录ID', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const AUTH_KEY = 'shopping_trends_admin_id';

function parseAdminsText(text) {
  const list = [];
  const lines = (text || '').trim().split(/\r?\n/);
  for (const line of lines) {
    const t = line.trim();
    if (!t) continue;
    const idx = t.indexOf(',');
    if (idx === -1) continue;
    const id = t.slice(0, idx).trim();
    const pwd = t.slice(idx + 1).trim();
    if (id && pwd) list.push({ id, password: pwd });
  }
  return list;
}

async function handleLogin() {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    loading.value = true;
    try {
      const res = await apiClient.get('/api/admins');
      const lines = Array.isArray(res?.data) ? res.data : [];
      const admins = parseAdminsText(lines.join('\n'));
      const found = admins.find(
        (a) => a.id === form.loginId && a.password === form.password
      );
      if (found) {
        localStorage.setItem(AUTH_KEY, form.loginId);
        ElMessage.success('登录成功');
        router.replace('/');
      } else {
        ElMessage.error('登录ID或密码错误');
      }
    } catch (e) {
      console.error(e);
      ElMessage.error('无法连接服务器或读取管理员列表，请稍后重试');
    } finally {
      loading.value = false;
    }
  });
}

onMounted(() => {
  if (localStorage.getItem(AUTH_KEY)) {
    router.replace('/');
  }
});
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.title {
  margin: 0 0 8px;
  font-size: 22px;
  text-align: center;
  color: #303133;
}

.subtitle {
  margin: 0 0 28px;
  font-size: 14px;
  text-align: center;
  color: #909399;
}

.login-form {
  margin-top: 8px;
}

.login-btn {
  width: 100%;
}
</style>
