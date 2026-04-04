<template>
  <!-- 全局错误提示 -->
  <el-alert
    v-if="globalError"
    :title="globalError"
    type="error"
    show-icon
    closable
    @close="globalError = null"
  />

  <!-- 全局加载状态 -->
  <div v-if="globalLoading" class="loading-overlay">
    <el-progress type="circle" :percentage="50" :show-text="false" />
  </div>
  <div class="user-analysis-container">
    <!-- 用户统计卡片 -->
    <el-row :gutter="20" class="stat-row" justify="center">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="活跃用户数" :value="userStats.activeUsers">
            <template #prefix>
              <el-icon><el-icon-user /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="平均购买频率" :value="userStats.avgFrequency" :precision="2">
            <template #suffix>
              <span>次/月</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 用户年龄分布 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>用户年龄分布</h3>
              <el-button v-if="charts.ageDistribution.loading" :loading="true" circle></el-button>
              <el-button v-else type="primary" size="small" circle @click="refreshChart('ageDistribution')">
                <el-icon><el-icon-refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-loading="charts.ageDistribution.loading" class="chart-container">
            <div v-if="charts.ageDistribution.error" class="chart-error">
              <el-empty description="加载数据失败">
                <el-button type="primary" @click="refreshChart('ageDistribution')">重试</el-button>
              </el-empty>
            </div>
            <div v-else id="age-distribution-chart" class="chart-instance"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 用户性别分布 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>用户性别分布</h3>
              <el-button v-if="charts.genderDistribution.loading" :loading="true" circle></el-button>
              <el-button v-else type="primary" size="small" circle @click="refreshChart('genderDistribution')">
                <el-icon><el-icon-refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-loading="charts.genderDistribution.loading" class="chart-container">
            <div v-if="charts.genderDistribution.error" class="chart-error">
              <el-empty description="加载数据失败">
                <el-button type="primary" @click="refreshChart('genderDistribution')">重试</el-button>
              </el-empty>
            </div>
            <div v-else id="gender-distribution-chart" class="chart-instance"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 用户购买频率 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>用户购买频率分布</h3>
              <el-button v-if="charts.frequencyDistribution.loading" :loading="true" circle></el-button>
              <el-button v-else type="primary" size="small" circle @click="refreshChart('frequencyDistribution')">
                <el-icon><el-icon-refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-loading="charts.frequencyDistribution.loading" class="chart-container">
            <div v-if="charts.frequencyDistribution.error" class="chart-error">
              <el-empty description="加载数据失败">
                <el-button type="primary" @click="refreshChart('frequencyDistribution')">重试</el-button>
              </el-empty>
            </div>
            <div v-else id="frequency-distribution-chart" class="chart-instance"></div>
          </div>
        </el-card>
      </el-col>

      <!-- 用户地区分布 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>用户地区分布</h3>
              <el-button v-if="charts.locationDistribution.loading" :loading="true" circle></el-button>
              <el-button v-else type="primary" size="small" circle @click="refreshChart('locationDistribution')">
                <el-icon><el-icon-refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-loading="charts.locationDistribution.loading" class="chart-container">
            <div v-if="charts.locationDistribution.error" class="chart-error">
              <el-empty description="加载数据失败">
                <el-button type="primary" @click="refreshChart('locationDistribution')">重试</el-button>
              </el-empty>
            </div>
            <div v-else id="location-distribution-chart" class="chart-instance"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户购买趋势 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>用户购买趋势</h3>
              <el-button v-if="charts.purchaseTrend.loading" :loading="true" circle></el-button>
              <el-button v-else type="primary" size="small" circle @click="refreshChart('purchaseTrend')">
                <el-icon><el-icon-refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-loading="charts.purchaseTrend.loading" class="chart-container">
            <div v-if="charts.purchaseTrend.error" class="chart-error">
              <el-empty description="加载数据失败">
                <el-button type="primary" @click="refreshChart('purchaseTrend')">重试</el-button>
              </el-empty>
            </div>
            <div v-else id="purchase-trend-chart" class="chart-instance"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
// 统一使用 apiClient，baseURL 已配置，无需在每个请求中拼写 http://localhost:8080
import apiClient from '@/utils/apiClient';
import { getLocationLabel, getGenderLabel } from '@/utils/displayLabels';
import { toolboxWithExportLog } from '@/utils/chartToolbox';

// 全局状态
const globalLoading = ref(false);
const globalError = ref(null);

// 筛选条件
const dateRange = ref([]);
const filters = reactive({
  ageGroup: '',
  gender: '',
  location: ''
});
const locationOptions = ref([]);

// 数据缓存
const dataCache = reactive({
  userStats: null,
  ageDistribution: null,
  genderDistribution: null,
  frequencyDistribution: null,
  locationDistribution: null,
  purchaseTrend: null
});

// 用户统计数据
const userStats = reactive({
  activeUsers: 0,
  avgFrequency: 0,
  avgOrderValue: 0,
  retentionRate: 0
});

// 图表配置
const charts = reactive({
  ageDistribution: {
    loading: false,
    error: false,
    instance: null
  },
  genderDistribution: {
    loading: false,
    error: false,
    instance: null
  },
  frequencyDistribution: {
    loading: false,
    error: false,
    instance: null
  },
  locationDistribution: {
    loading: false,
    error: false,
    instance: null
  },
  purchaseTrend: {
    loading: false,
    error: false,
    instance: null
  }
});

// 获取地区下拉选项（用于筛选表单）
const fetchLocationOptions = async () => {
  try {
    // apiClient 响应拦截器已解包一层 response.data，res 即后端 Result 对象
    const res = await apiClient.get('/api/shopping-records/locations');
    const list = Array.isArray(res?.data) ? res.data : (Array.isArray(res) ? res : []);
    locationOptions.value = list.map(location => ({
      value: location,
      label: getLocationLabel(location)
    }));
  } catch (error) {
    console.error('获取地区选项失败:', error);
  }
};

// 加载用户统计数据
const loadUserStats = async () => {
  try {
    globalLoading.value = true;
    globalError.value = null;
    
    // 构建查询参数
    const params = {};
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.start_date = dateRange.value[0].toISOString().split('T')[0];
      params.end_date = dateRange.value[1].toISOString().split('T')[0];
    }
    
    if (filters.ageGroup) params.age_group = filters.ageGroup;
    if (filters.gender) params.gender = filters.gender;
    if (filters.location) params.location = filters.location;
    
    // 检查缓存
    const cacheKey = JSON.stringify(params);
    if (dataCache.userStats && dataCache.userStats.key === cacheKey) {
      Object.assign(userStats, dataCache.userStats.data);
      return;
    }
    
    const res = await apiClient.get('/api/shopping-records/stats/user-summary', { params });
    // apiClient 拦截器已解包一层，res 即后端 Result 对象；.data 为业务数据
    const payload = res?.data ?? res ?? {};
    if (!payload || Object.keys(payload).length === 0) {
      throw new Error('Invalid response data structure');
    }

    // 更新顶部统计卡片数据
    userStats.activeUsers  = Number(payload.activeUserCount)     || 0;
    userStats.avgFrequency = Number(payload.avgPurchaseFrequency) || 0;

    // 缓存分布数据，供各图表加载函数复用，避免重复请求
    dataCache.ageDistribution      = payload.ageGroupDistribution;
    dataCache.genderDistribution   = payload.genderDistribution;
    dataCache.locationDistribution = payload.locationDistribution;
    
    // 更新缓存
    dataCache.userStats = {
      key: cacheKey,
      data: {...userStats}
    };
  } catch (error) {
    console.error('加载用户统计数据失败:', error);
    globalError.value = '加载用户统计数据失败，请稍后重试';
  } finally {
    globalLoading.value = false;
  }
};

// 初始化图表
const initCharts = () => {
  // 年龄分布图表
  charts.ageDistribution.instance = echarts.init(document.getElementById('age-distribution-chart'));
  
  // 性别分布图表
  charts.genderDistribution.instance = echarts.init(document.getElementById('gender-distribution-chart'));
  
  // 购买频率分布图表
  charts.frequencyDistribution.instance = echarts.init(document.getElementById('frequency-distribution-chart'));
  
  // 地区分布图表
  charts.locationDistribution.instance = echarts.init(document.getElementById('location-distribution-chart'));
  
  // 购买趋势图表
  charts.purchaseTrend.instance = echarts.init(document.getElementById('purchase-trend-chart'));

  // 响应窗口大小变化
  const resizeHandler = () => {
    Object.values(charts).forEach(chart => {
      if (chart.instance) {
        chart.instance.resize();
      }
    });
  };
  
  window.addEventListener('resize', resizeHandler);
  
  // 组件卸载时清理
  onBeforeUnmount(() => {
    window.removeEventListener('resize', resizeHandler);
    Object.values(charts).forEach(chart => {
      if (chart.instance) {
        chart.instance.dispose();
        chart.instance = null;
      }
    });
  });
};

// 加载年龄分布数据
const loadAgeDistribution = async () => {
  const chart = charts.ageDistribution;
  chart.loading = true;
  chart.error = false;
  
  try {
    // 构建查询参数
    const params = {};
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.start_date = dateRange.value[0].toISOString().split('T')[0];
      params.end_date = dateRange.value[1].toISOString().split('T')[0];
    }
    
    if (filters.gender) params.gender = filters.gender;
    if (filters.location) params.location = filters.location;
    
    // 如果缓存中没有数据，重新加载用户统计数据
    if (!dataCache.ageDistribution) {
      await loadUserStats();
      if (!dataCache.ageDistribution) {
        throw new Error('年龄分布数据不可用');
      }
    }
    
    // 转换年龄数据为数组并按年龄段排序
    const ageGroups = ['18-24', '25-34', '35-44', '45-54', '55+'];
    const ageData = ageGroups.map(group => ({
      ageGroup: group,
      count: dataCache.ageDistribution[group] || 0
    }));
    
    const option = {
      title: {
        text: '用户年龄分布',
        left: 'center',
        subtext: '按年龄段统计',
        subtextStyle: {
          fontSize: 14
        }
      },
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          const data = params[0];
          return `
            <div style="font-weight:bold">${data.name}</div>
            <div>用户数: ${data.value}人</div>
          `;
        }
      },
      toolbox: toolboxWithExportLog('客户年龄分布'),
      grid: {
        top: '20%',
        left: '3%',
        right: '3%',
        bottom: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ageData.map(item => item.ageGroup),
        axisLabel: {
          interval: 0
        }
      },
      yAxis: {
        type: 'value',
        name: '用户数',
        minInterval: 1
      },
      series: [
        {
          name: '用户数',
          type: 'bar',
          barWidth: '40%',
          data: ageData.map(item => item.count),
          itemStyle: {
            color: (params) => {
              const colorList = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de'];
              return colorList[params.dataIndex % colorList.length];
            }
          },
          label: {
            show: true,
            position: 'top'
          }
        }
      ]
    };
    
    chart.instance.setOption(option);
  } catch (error) {
    console.error('加载年龄分布数据失败:', error);
    chart.error = true;
  } finally {
    chart.loading = false;
  }
};

// 加载性别分布数据
const loadGenderDistribution = async () => {
  const chart = charts.genderDistribution;
  chart.loading = true;
  chart.error = false;
  
  try {
    // 构建查询参数
    const params = {};
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.start_date = dateRange.value[0].toISOString().split('T')[0];
      params.end_date = dateRange.value[1].toISOString().split('T')[0];
    }
    
    if (filters.ageGroup) params.age_group = filters.ageGroup;
    if (filters.location) params.location = filters.location;
    
    // 如果缓存中没有数据，重新加载用户统计数据
    if (!dataCache.genderDistribution) {
      await loadUserStats();
      if (!dataCache.genderDistribution) {
        throw new Error('性别分布数据不可用');
      }
    }
    
    // 转换性别数据为数组，展示用中文（女/男）
    const genderData = [
      { name: getGenderLabel('Female'), value: dataCache.genderDistribution.Female || 0 },
      { name: getGenderLabel('Male'), value: dataCache.genderDistribution.Male || 0 }
    ];
    
    const option = {
      title: {
        text: '用户性别分布',
        left: 'center',
        subtext: '按性别统计',
        subtextStyle: {
          fontSize: 14
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          return `
            <div style="font-weight:bold">${params.name}</div>
            <div>用户数: ${params.value}人</div>
            <div>占比: ${params.percent}%</div>
          `;
        }
      },
      toolbox: toolboxWithExportLog('客户性别分布'),
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center'
      },
      series: [
        {
          name: '用户性别',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}: {c} ({d}%)'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: true
          },
          data: genderData,
          color: ['#ff7f0e', '#1f77b4'] // 橙色代表女性，蓝色代表男性
        }
      ]
    };
    
    chart.instance.setOption(option);
  } catch (error) {
    console.error('加载性别分布数据失败:', error);
    chart.error = true;
  } finally {
    chart.loading = false;
  }
};

// 加载购买频率分布数据
const loadFrequencyDistribution = async () => {
  const chart = charts.frequencyDistribution;
  chart.loading = true;
  chart.error = false;
  
  try {
    // 构建查询参数
    const params = {};
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.start_date = dateRange.value[0].toISOString().split('T')[0];
      params.end_date = dateRange.value[1].toISOString().split('T')[0];
    }
    
    if (filters.ageGroup) params.age_group = filters.ageGroup;
    if (filters.gender) params.gender = filters.gender;
    if (filters.location) params.location = filters.location;
    
    const res = await apiClient.get('/api/shopping-records/stats/frequency', { params });
    const freqPayload = res?.data ?? res ?? {};

    // 取月频率数据并按月份升序排列，用于绘制折线图（排除 2024-12）
    const monthlyData = Object.entries(freqPayload.monthlyFrequency ?? {})
      .filter(([month]) => month !== '2024-12')
      .map(([month, count]) => ({
        month,
        count
      }))
      .sort((a, b) => {
        const [aYear, aMonth] = a.month.split('-').map(Number);
        const [bYear, bMonth] = b.month.split('-').map(Number);
        return aYear - bYear || aMonth - bMonth;
      });
    
    const option = {
      title: {
        text: '用户购买频率分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          const data = params[0];
          return `
            <div style="font-weight:bold">${data.name}</div>
            <div>购买次数: ${data.value}次</div>
          `;
        }
      },
      toolbox: toolboxWithExportLog('客户购买频率分布'),
      xAxis: {
        type: 'category',
        data: monthlyData.map(item => item.month),
        axisLabel: {
          interval: 0,
          rotate: 30
        }
      },
      yAxis: {
        type: 'value',
        name: '购买次数'
      },
      series: [
        {
          name: '购买次数',
          type: 'bar',
          data: monthlyData.map(item => item.count),
          itemStyle: {
            color: '#91cc75'
          }
        }
      ]
    };
    
    chart.instance.setOption(option);
  } catch (error) {
    console.error('加载购买频率分布数据失败:', error);
    chart.error = true;
  } finally {
    chart.loading = false;
  }
};

// 安全转为数字，避免 NaN（接口可能返回字符串或 undefined）
const toNum = (v) => {
  const n = Number(v);
  return Number.isFinite(n) ? n : 0;
};

// 加载地区分布数据（饼图，按购买次数统计）
const loadLocationDistribution = async () => {
  const chart = charts.locationDistribution;
  chart.loading = true;
  chart.error = false;
  
  try {
    const res = await apiClient.get('/api/shopping-records/stats/location');
    const rawList = Array.isArray(res?.data) ? res.data : (Array.isArray(res) ? res : []);
    const sortedData = [...rawList].sort((a, b) => toNum(b.purchase_count ?? b.count) - toNum(a.purchase_count ?? a.count));
    
    const option = {
      title: {
        text: '用户地区分布',
        left: 'center',
        subtext: '按购买次数统计',
        subtextStyle: { fontSize: 14 }
      },
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          const { name, value, percent } = params;
          return `
            <div style="font-weight:bold">${name}</div>
            <div>购买次数: ${toNum(value)}次</div>
            <div>占比: ${percent}%</div>
          `;
        }
      },
      toolbox: toolboxWithExportLog('客户地区分布'),
      legend: {
        type: 'scroll',
        orient: 'vertical',
        right: 10,
        top: 'center',
        data: sortedData.map(item => getLocationLabel(item.location))
      },
      series: [
        {
          name: '购买次数',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '18',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: sortedData.map(item => ({
            name: getLocationLabel(item.location),
            value: toNum(item.purchase_count ?? item.count)
          }))
        }
      ]
    };
    
    chart.instance.setOption(option);
  } catch (error) {
    console.error('加载地区分布数据失败:', error);
    chart.error = true;
  } finally {
    chart.loading = false;
  }
};

// 加载购买趋势数据
const loadPurchaseTrend = async () => {
  const chart = charts.purchaseTrend;
  chart.loading = true;
  chart.error = false;
  
  try {
    // 构建查询参数
    const params = {};
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.start_date = dateRange.value[0].toISOString().split('T')[0];
      params.end_date = dateRange.value[1].toISOString().split('T')[0];
    }
    
    if (filters.ageGroup) params.age_group = filters.ageGroup;
    if (filters.gender) params.gender = filters.gender;
    if (filters.location) params.location = filters.location;
    
    const res = await apiClient.get('/api/shopping-records/stats/date-range', { params });
    const trendList = Array.isArray(res?.data) ? res.data : (Array.isArray(res) ? res : []);

    const option = {
      title: {
        text: '用户购买趋势',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      toolbox: toolboxWithExportLog('客户购买趋势'),
      xAxis: {
        type: 'category',
        data: trendList.map(item => item.date),
        axisLabel: {
          interval: 'auto',
          rotate: 30
        }
      },
      yAxis: {
        type: 'value',
        name: '购买金额'
      },
      series: [
        {
          name: '购买金额',
          type: 'line',
          data: trendList.map(item => item.total_amount),
          smooth: true,
          lineStyle: {
            width: 3
          },
          areaStyle: {
            opacity: 0.2
          },
          markPoint: {
            data: [
              { type: 'max', name: '最大值' },
              { type: 'min', name: '最小值' }
            ]
          }
        }
      ]
    };
    
    chart.instance.setOption(option);
  } catch (error) {
    console.error('加载购买趋势数据失败:', error);
    chart.error = true;
  } finally {
    chart.loading = false;
  }
};

// 刷新单个图表
const refreshChart = (chartName) => {
  switch (chartName) {
    case 'ageDistribution':
      loadAgeDistribution();
      break;
    case 'genderDistribution':
      loadGenderDistribution();
      break;
    case 'frequencyDistribution':
      loadFrequencyDistribution();
      break;
    case 'locationDistribution':
      loadLocationDistribution();
      break;
    case 'purchaseTrend':
      loadPurchaseTrend();
      break;
  }
};

// 日期范围变化处理
const handleDateChange = () => {
  loadUserStats();
  loadAllCharts();
};

// 应用筛选
const applyFilters = () => {
  loadUserStats();
  loadAllCharts();
};

// 加载所有图表
const loadAllCharts = () => {
  loadAgeDistribution();
  loadGenderDistribution();
  loadFrequencyDistribution();
  loadLocationDistribution();
  loadPurchaseTrend();
};

// 组件挂载时加载数据
onMounted(async () => {
  try {
    await fetchLocationOptions();
    await loadUserStats();
    initCharts();
    loadAllCharts();
  } catch (error) {
    console.error('初始化失败:', error);
    globalError.value = '初始化失败，请刷新页面重试';
  }
});
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999;
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
}

.user-analysis-container {
  padding: 10px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-section {
  margin-top: 10px;
}

.filter-section .el-col {
  margin-bottom: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}

.card-header h2, .card-header h3 {
  margin: 0;
  font-weight: 500;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 100%;
  text-align: center;
  transition: all 0.3s;
  background-color: #2563eb;
  color: #fff;
  border-color: #1d4ed8;
}

.stat-card :deep(.el-card__body),
.stat-card :deep(.el-statistic__head),
.stat-card :deep(.el-statistic__content),
.stat-card :deep(.el-statistic__number) {
  color: #fff !important;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 100%;
  margin-bottom: 20px;
}

.chart-container {
  height: 350px;
  position: relative;
}

.chart-instance {
  width: 100%;
  height: 100%;
}

.chart-error {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .card-header > *:not(:first-child) {
    margin-top: 10px;
  }
  
  .chart-container {
    height: 300px;
  }
}
</style>