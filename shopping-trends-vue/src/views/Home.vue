<template>
  <div class="home-page">
    <h1 class="home-title">电商交易分析系统</h1>
    <p class="home-desc">请选择功能模块进入</p>
    <div class="module-list">
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/profile')">
            <div class="module-icon">
              <el-icon :size="48"><el-icon-user /></el-icon>
            </div>
            <h3>用户个人资料</h3>
            <p>查看与修改账号信息</p>
          </el-card>
        </el-col>
      </el-row>
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/dashboard')">
            <div class="module-icon">
              <el-icon :size="48"><el-icon-menu /></el-icon>
            </div>
            <h3>仪表盘</h3>
            <p>总览与词云分析</p>
          </el-card>
        </el-col>
      </el-row>
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/user-analysis')">
            <div class="module-icon">
              <el-icon :size="48"><el-icon-user /></el-icon>
            </div>
            <h3>用户分析</h3>
            <p>用户行为与分布</p>
          </el-card>
        </el-col>
      </el-row>
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/product-analysis')">
            <div class="module-icon">
              <el-icon :size="48"><el-icon-shopping-cart /></el-icon>
            </div>
            <h3>产品分析</h3>
            <p>产品品类分布</p>
          </el-card>
        </el-col>
      </el-row>
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/comprehensive-analysis')">
            <div class="module-icon">
              <el-icon :size="48"><el-icon-data-analysis /></el-icon>
            </div>
            <h3>综合分析</h3>
            <p>分析1 / 分析2 / 分析3</p>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const go = (path) => {
  router.push(path);
};
</script>

<style scoped>
.home-page {
  padding: 40px 20px;
  text-align: center;
}

.home-title {
  font-size: 28px;
  margin-bottom: 8px;
  color: #303133;
}

.home-desc {
  color: #909399;
  margin-bottom: 40px;
}

.module-list {
  max-width: 640px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  gap: 24px;
}

.module-list .el-row {
  margin: 0;
}

.module-card {
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
}

.module-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.module-icon {
  color: #409eff;
  margin-bottom: 16px;
}

.module-card h3 {
  font-size: 18px;
  margin: 0 0 8px;
  color: #303133;
}

.module-card p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}
</style>
