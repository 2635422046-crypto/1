<template>
  <div class="home-page">
    <h1 class="home-title">电商交易数据分析系统</h1>
    <p class="home-desc">请选择功能模块进入</p>
    <div class="module-list">
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/profile')">
            <div class="module-icon">
              <el-icon :size="48"><User /></el-icon>
            </div>
            <h3>管理员账户管理</h3>
            <p>账号信息、修改密码、注销、操作日志记录登录、退出、修改密码、图表下载功能</p>
          </el-card>
        </el-col>
      </el-row>
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/dashboard')">
            <div class="module-icon">
              <el-icon :size="48"><Menu /></el-icon>
            </div>
            <h3>经营数据概览</h3>
            <p>经营指标概览与词云分析</p>
          </el-card>
        </el-col>
      </el-row>
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/user-analysis')">
            <div class="module-icon">
              <el-icon :size="48"><User /></el-icon>
            </div>
            <h3>客户分析</h3>
            <p>客户行为与分布</p>
          </el-card>
        </el-col>
      </el-row>
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/product-analysis')">
            <div class="module-icon">
              <el-icon :size="48"><ShoppingCart /></el-icon>
            </div>
            <h3>商品结构分析</h3>
            <p>品类、种类与属性结构</p>
          </el-card>
        </el-col>
      </el-row>
      <el-row justify="center">
        <el-col :span="24">
          <el-card shadow="hover" class="module-card" @click="go('/comprehensive-analysis')">
            <div class="module-icon">
              <el-icon :size="48"><DataAnalysis /></el-icon>
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
import { User, Menu, ShoppingCart, DataAnalysis } from '@element-plus/icons-vue';

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
  font-weight: 600;
}

.home-desc {
  color: #909399;
  margin-bottom: 40px;
  font-size: 14px;
}

.module-list {
  max-width: 640px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  gap: 20px;
}

.module-list .el-row {
  margin: 0;
}

.module-card {
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
  padding: 20px;
}

.module-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.module-icon {
  color: #409eff;
  margin-bottom: 12px;
  display: flex;
  justify-content: center;
}

.module-card h3 {
  font-size: 16px;
  margin: 0 0 8px;
  color: #303133;
  font-weight: 600;
}

.module-card p {
  font-size: 12px;
  color: #909399;
  margin: 0;
  line-height: 1.5;
}
</style>
