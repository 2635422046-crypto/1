<template>
  <div class="profile-page">
    <el-card class="profile-card">
      <template #header>
        <span class="card-title">个人资料</span>
      </template>
      <div class="sections">
        <div class="section">
          <h3>账号信息</h3>
          <el-descriptions :column="1" border class="desc-block">
            <el-descriptions-item label="登录ID">{{ loginId }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="section">
          <h3>修改密码</h3>
          <el-form
            ref="pwdFormRef"
            :model="pwdForm"
            :rules="pwdRules"
            label-width="120px"
            class="pwd-form"
          >
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input
                v-model="pwdForm.oldPassword"
                type="password"
                placeholder="请输入旧密码"
                show-password
                clearable
                size="large"
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="pwdForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
                clearable
                size="large"
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="pwdForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
                clearable
                size="large"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="pwdLoading" size="large" @click="handleChangePassword">
                确认修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="section">
          <h3>注销账号</h3>
          <p class="tip">注销后将从系统中移除该管理员，需重新录入才能再登录。</p>
          <el-button type="danger" :loading="deregisterLoading" size="large" @click="handleDeregister">
            注销
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import apiClient from '@/utils/apiClient';

const AUTH_KEY = 'shopping_trends_admin_id';
const router = useRouter();
const pwdFormRef = ref(null);
const pwdLoading = ref(false);
const deregisterLoading = ref(false);

const loginId = computed(() => localStorage.getItem(AUTH_KEY) || '');

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的新密码不一致'));
  } else {
    callback();
  }
};

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
};

async function handleChangePassword() {
  if (!pwdFormRef.value) return;
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return;
    pwdLoading.value = true;
    try {
      const res = await apiClient.post('/api/admin/change-password', {
        loginId: loginId.value,
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      });
      if (res && res.success) {
        ElMessage.success(res.message || '修改成功');
        pwdForm.oldPassword = '';
        pwdForm.newPassword = '';
        pwdForm.confirmPassword = '';
        pwdFormRef.value?.resetFields();
      } else {
        ElMessage.error(res?.message || '修改失败');
      }
    } catch (e) {
      const msg = e.response?.data?.message || e.message || '修改失败';
      ElMessage.error(msg);
    } finally {
      pwdLoading.value = false;
    }
  });
}

async function handleDeregister() {
  try {
    await ElMessageBox.confirm(
      '注销后将从电商交易分析系统中删除该账号，确定要注销吗？',
      '确认注销',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
  } catch {
    return;
  }
  const pwd = await ElMessageBox.prompt('请输入当前密码以确认注销', '验证密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'password',
    inputPlaceholder: '当前密码',
    inputValidator: (v) => (v && v.trim() ? true : '请输入密码')
  }).catch(() => null);
  if (!pwd || !pwd.value) return;
  deregisterLoading.value = true;
  try {
    const res = await apiClient.post('/api/admin/deregister', {
      loginId: loginId.value,
      password: pwd.value.trim()
    });
    if (res && res.success) {
      localStorage.removeItem(AUTH_KEY);
      ElMessage.success('已注销');
      router.replace('/login');
    } else {
      ElMessage.error(res?.message || '账号或密码错误');
    }
  } catch (e) {
    const msg = e.response?.data?.message || e.message || '注销失败';
    ElMessage.error(msg);
  } finally {
    deregisterLoading.value = false;
  }
}

onMounted(() => {
  if (!loginId.value) router.replace('/login');
});
</script>

<style scoped>
.profile-page {
  padding: 32px 24px;
  min-height: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.profile-card {
  width: 100%;
  max-width: 880px;
}

.profile-card :deep(.el-card__header) {
  font-size: 20px;
  font-weight: 600;
  padding: 20px 24px;
}

.card-title {
  font-size: 20px;
}

.sections {
  display: flex;
  flex-direction: column;
  gap: 40px;
  padding: 8px 0;
}

.section {
  flex: 1;
  min-height: 0;
}

.section h3 {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.desc-block {
  font-size: 16px;
}

.desc-block :deep(.el-descriptions__label),
.desc-block :deep(.el-descriptions__content) {
  font-size: 16px;
  padding: 14px 16px;
}

.pwd-form {
  max-width: 480px;
}

.pwd-form :deep(.el-form-item__label) {
  font-size: 15px;
}

.pwd-form :deep(.el-input__wrapper) {
  font-size: 15px;
  padding: 4px 12px;
}

.tip {
  margin: 0 0 16px;
  font-size: 15px;
  color: #606266;
  line-height: 1.6;
}
</style>
