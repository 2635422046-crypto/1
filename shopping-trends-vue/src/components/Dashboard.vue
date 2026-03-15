<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover" class="welcome-card">
          <template #header>
            <div class="card-header">
              <h2>电商交易数据分析仪表盘</h2>
            </div>
          </template>
          <div class="welcome-content">
            <div class="stats-summary">
              <el-row :gutter="20">
                <el-col :xs="24" :sm="12" :md="6">
                  <el-card shadow="hover" class="stat-card">
                    <el-statistic title="总销售额" :value="totalSales" :precision="2">
                      <template #prefix>
                        <el-icon><el-icon-money /></el-icon>
                      </template>
                      <template #suffix>
                        <span>元</span>
                      </template>
                    </el-statistic>
                  </el-card>
                </el-col>
                <el-col :xs="24" :sm="12" :md="6">
                  <el-card shadow="hover" class="stat-card">
                    <el-statistic title="订单数量" :value="orderCount">
                      <template #prefix>
                        <el-icon><el-icon-shopping-cart /></el-icon>
                      </template>
                    </el-statistic>
                  </el-card>
                </el-col>
                <el-col :xs="24" :sm="12" :md="6">
                  <el-card shadow="hover" class="stat-card">
                    <el-statistic title="客户数量" :value="customerCount">
                      <template #prefix>
                        <el-icon><el-icon-user /></el-icon>
                      </template>
                    </el-statistic>
                  </el-card>
                </el-col>
                <el-col :xs="24" :sm="12" :md="6">
                  <el-card shadow="hover" class="stat-card">
                    <el-statistic title="平均订单金额" :value="avgOrderAmount" :precision="2">
                      <template #prefix>
                        <el-icon><el-icon-data-analysis /></el-icon>
                      </template>
                      <template #suffix>
                        <span>元</span>
                      </template>
                    </el-statistic>
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 词云图：品类=category、种类=item_purchased，同一行并排 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="word-cloud-card">
          <template #header>
            <div class="card-header">
              <h3>商品品类词云图</h3>
            </div>
          </template>
          <WordCloudChart data-source="category" />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="word-cloud-card">
          <template #header>
            <div class="card-header">
              <h3>商品种类词云图</h3>
            </div>
          </template>
          <WordCloudChart data-source="itemPurchased" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表网格 -->
    <el-row :gutter="20" class="chart-row">
      <el-col v-for="(chart, index) in charts" :key="index" :xs="24" :sm="24" :md="12" :lg="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>{{ chart.title }}</h3>
              <el-button v-if="chart.loading" :loading="true" circle></el-button>
              <el-button v-else type="primary" size="small" circle @click="refreshChart(index)">
                <el-icon><el-icon-refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-loading="chart.loading" class="chart-container">
            <div v-if="chart.error" class="chart-error">
              <el-empty description="加载数据失败">
                <el-button type="primary" @click="refreshChart(index)">重试</el-button>
              </el-empty>
            </div>
            <div v-else :id="`chart-${index}`" class="chart-instance"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
// 统一使用 apiClient（baseURL 已配置为 http://localhost:8080，带响应拦截器）
import apiClient from '@/utils/apiClient';
import WordCloudChart from './WordCloudChart.vue';
import { Money, ShoppingCart, User, DataAnalysis, Refresh } from '@element-plus/icons-vue';
import { getCategoryLabel, getLocationLabel, getGenderLabel, getSeasonLabel } from '@/utils/displayLabels';

// 统计数据
const totalSales = ref(0);
const orderCount = ref(0);
const customerCount = ref(0);
const avgOrderAmount = ref(0);
const dateRange = ref([]);

// 图表实例存储
const chartInstances = ref([]);

// 柱形图专用颜色：每个柱形图一种颜色，图表之间颜色不同
const barChartColors = [
  '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272'
];

// 图表配置
const charts = reactive([
  {
    url: '/api/shopping-records/stats/gender',
    title: '性别购买分布',
    type: 'pie',
    xAxis: 'gender',
    yAxis: 'total_amount',
    loading: false,
    error: false
  },
  {
    url: '/api/shopping-records/stats/age-group',
    title: '年龄段购买分析',
    type: 'bar',
    xAxis: 'age_group',
    yAxis: 'total_amount',
    loading: false,
    error: false
  },
  {
    url: '/api/shopping-records/stats/category',
    title: '商品品类销售额',
    type: 'bar',
    xAxis: 'category',
    yAxis: 'total_amount',
    loading: false,
    error: false
  },
  {
    url: '/api/shopping-records/stats/season',
    title: '季节销售趋势',
    type: 'bar',
    xAxis: 'season',
    yAxis: 'total_amount',
    loading: false,
    error: false
  },
  {
    url: '/api/shopping-records/stats/location',
    title: '地区销售分布',
    type: 'bar',
    xAxis: 'location',
    yAxis: 'total_amount',
    loading: false,
    error: false
  },
  {
    url: '/api/shopping-records/stats/date-range',
    title: '销售时间趋势',
    type: 'line',
    xAxis: 'date',
    yAxis: 'total_amount',
    loading: false,
    error: false
  }
]);

// 加载仪表盘顶部四个统计卡片的汇总数据
const loadSummaryData = async () => {
  try {
    // apiClient 响应拦截器已将 response.data 解包，此处直接拿到后端 Result 对象
    const res = await apiClient.get('/api/shopping-records/stats/summary');
    const d = res?.data ?? res ?? {};
    totalSales.value     = d.total_sales      || 0;
    orderCount.value     = d.order_count      || 0;
    customerCount.value  = d.customer_count   || 0;
    avgOrderAmount.value = d.avg_order_amount || 0;
  } catch (error) {
    console.error('加载统计数据失败:', error);
  }
};

// 加载单个图表数据
const loadChartData = async (index) => {
  const chart = charts[index];
  chart.loading = true;
  chart.error = false;
  
  try {
    // 构造请求参数：有日期范围时附带 start_date/end_date 查询参数
    const params = {};
    if (dateRange.value && dateRange.value.length === 2) {
      params.start_date = dateRange.value[0].toISOString().split('T')[0];
      params.end_date   = dateRange.value[1].toISOString().split('T')[0];
    }
    // 使用 apiClient 统一发起请求，baseURL 已配置，无需拼 http://localhost:8080
    const res = await apiClient.get(chart.url, { params });
    const rawList = Array.isArray(res?.data) ? res.data : (Array.isArray(res) ? res : []);
    
    // 展示用：将 category/location/gender/season 转为中文（不改接口与数据库）
    const toDisplayLabel = (raw) => {
      if (raw == null || raw === '') return '';
      if (chart.xAxis === 'category') return getCategoryLabel(raw);
      if (chart.xAxis === 'location') return getLocationLabel(raw);
      if (chart.xAxis === 'gender') return getGenderLabel(raw);
      if (chart.xAxis === 'season') return getSeasonLabel(raw);
      return String(raw);
    };
    const displayData = rawList.map(item => ({
      ...item,
      _displayLabel: toDisplayLabel(item[chart.xAxis])
    }));

    // 确保DOM元素存在
    const chartDom = document.getElementById(`chart-${index}`);
    if (!chartDom) {
      chart.loading = false;
      return;
    }
    
    // 初始化或获取图表实例
    let chartInstance = chartInstances.value[index];
    if (!chartInstance) {
      chartInstance = echarts.init(chartDom);
      chartInstances.value[index] = chartInstance;
    }
    
    // 地区销售分布类别多，X 轴横排斜标、缩小字号并留足底部空间，减少重叠
    const isLocationChart = chart.xAxis === 'location';
    const gridBottom = isLocationChart ? '22%' : '3%';
    const xAxisLabel = {
      interval: 0,
      rotate: 30,
      ...(isLocationChart && { fontSize: 10, margin: 10 })
    };

    // 配置图表选项
    const option = {
      title: { text: chart.title, left: 'center' },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: gridBottom, containLabel: true },
      legend: { orient: 'horizontal', bottom: 0 },
      toolbox: {
        feature: {
          saveAsImage: {}
        }
      },
      xAxis: chart.type !== 'pie' ? { 
        type: 'category',
        data: displayData.map(item => item._displayLabel),
        axisLabel: xAxisLabel
      } : undefined,
      yAxis: chart.type !== 'pie' ? {
        type: 'value',
        name: '金额 (元)'
      } : undefined,
      series: [
        {
          name: chart.title,
          type: chart.type,
          data: chart.type !== 'pie' 
            ? displayData.map(item => item[chart.yAxis])
            : displayData.map(item => ({ 
                value: item[chart.yAxis], 
                name: item._displayLabel 
              })),
          ...(chart.type === 'bar' && {
            itemStyle: {
              color: barChartColors[index % barChartColors.length]
            }
          }),
          ...(chart.type === 'pie' && {
            radius: '60%',
            center: ['50%', '50%'],
            label: {
              formatter: '{b}: {c} ({d}%)'
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          })
        }
      ]
    };
    
    // 设置图表选项
    chartInstance.setOption(option);
    
    // 响应窗口大小变化
    window.addEventListener('resize', () => {
      chartInstance.resize();
    });
    
  } catch (error) {
    console.error(`加载图表 ${chart.title} 数据失败:`, error);
    chart.error = true;
  } finally {
    chart.loading = false;
  }
};

// 刷新单个图表
const refreshChart = (index) => {
  loadChartData(index);
};

// 日期范围变化处理
const handleDateChange = () => {
  loadSummaryData();
  charts.forEach((_, index) => {
    loadChartData(index);
  });
};

// 组件挂载时加载数据
onMounted(() => {
  loadSummaryData();
  charts.forEach((_, index) => {
    loadChartData(index);
  });
});

// 组件卸载前清理图表实例
onBeforeUnmount(() => {
  chartInstances.value.forEach(instance => {
    if (instance) {
      instance.dispose();
    }
  });
});
</script>

<style scoped>
.dashboard {
  padding: 10px;
}

.chart-row {
  margin-top: 20px;
}

.word-cloud-card {
  height: 100%;
  margin-bottom: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2, .card-header h3 {
  margin: 0;
  font-weight: 500;
}

.welcome-content {
  padding: 10px 0;
}

.stats-summary {
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