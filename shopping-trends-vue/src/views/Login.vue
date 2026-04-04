<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="title">电商交易数据分析系统</h1>
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
          <el-input v-model="form.loginId" placeholder="请输入登录ID" size="large" clearable />
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
        <el-form-item label="验证码" prop="captchaText">
          <div class="captcha-row">
            <el-input
              v-model="form.captchaText"
              placeholder="请输入图中 4 位字母"
              size="large"
              maxlength="4"
              clearable
              class="captcha-input"
              @keyup.enter="handleLogin"
            />
            <div class="captcha-img-wrap" title="点击换一张" @click="loadCaptcha">
              <img v-if="captchaDataUrl" :src="captchaDataUrl" alt="验证码" class="captcha-img" />
              <span v-else class="captcha-placeholder">加载中…</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
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
import { AUTH_LOGIN_ID_KEY, AUTH_SESSION_KEY } from '@/constants/auth';

const router = useRouter();
const formRef = ref(null);
const loading = ref(false);
const captchaId = ref('');
const captchaDataUrl = ref('');

const form = reactive({
  loginId: '',
  password: '',
  captchaText: ''
});

const rules = {
  loginId: [{ required: true, message: '请输入登录ID', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaText: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 4, message: '验证码为 4 位字母', trigger: 'blur' },
    {
      pattern: /^[A-Za-z]{4}$/,
      message: '仅允许 4 位英文字母',
      trigger: 'blur'
    }
  ]
};

async function loadCaptcha() {
  captchaDataUrl.value = '';
  captchaId.value = '';
  form.captchaText = '';
  try {
    const res = await apiClient.get('/api/admin/captcha');
    const d = res?.data;
    if (res && res.success && d && d.captchaId && d.imageBase64) {
      captchaId.value = d.captchaId;
      captchaDataUrl.value = 'data:image/png;base64,' + d.imageBase64;
    } else {
      ElMessage.error('获取验证码失败');
    }
  } catch (e) {
    console.error(e);
    ElMessage.error('无法获取验证码，请检查网络或后端服务');
  }
}

async function handleLogin() {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    if (!captchaId.value) {
      ElMessage.warning('请先获取验证码');
      await loadCaptcha();
      return;
    }
    loading.value = true;
    try {
      const res = await apiClient.post('/api/admin/login/password', {
        loginId: form.loginId,
        password: form.password,
        captchaId: captchaId.value,
        captchaText: form.captchaText.trim()
      });
      if (res && res.success && res.data && res.data.sessionToken && res.data.loginId) {
        localStorage.setItem(AUTH_LOGIN_ID_KEY, res.data.loginId);
        localStorage.setItem(AUTH_SESSION_KEY, res.data.sessionToken);
        ElMessage.success('登录成功');
        router.replace('/');
      } else {
        ElMessage.error(res?.message || '登录ID或密码错误');
        await loadCaptcha();
      }
    } catch (e) {
      console.error(e);
      ElMessage.error(e.response?.data?.message || '无法连接服务器，请稍后重试');
      await loadCaptcha();
    } finally {
      loading.value = false;
    }
  });
}

onMounted(async () => {
  if (localStorage.getItem(AUTH_LOGIN_ID_KEY) && localStorage.getItem(AUTH_SESSION_KEY)) {
    router.replace('/');
    return;
  }
  await loadCaptcha();
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

.captcha-row {
  display: flex;
  align-items: stretch;
  gap: 12px;
  width: 100%;
}

.captcha-input {
  flex: 1;
  min-width: 0;
}

.captcha-img-wrap {
  flex-shrink: 0;
  width: 120px;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  user-select: none;
}

.captcha-img-wrap:hover {
  border-color: #409eff;
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.captcha-placeholder {
  font-size: 12px;
  color: #909399;
}

.login-btn {
  width: 100%;
}
</style>
