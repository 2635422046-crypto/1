<template>
  <div class="product-analysis">
    <h2>产品分析</h2>

    <!-- 顶端居中：商品品类数量、商品种类数量 -->
    <el-row :gutter="20" class="stat-row" justify="center">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="商品品类数量" :value="categoryCount">
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="商品种类数量" :value="itemTypeCount">
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <div class="filter-section">
      <!-- 筛选条件组件 -->
    </div>

    <div class="chart-container">
      <!-- 产品品类分布（品类=category） -->
      <div class="chart-card">
        <h3>产品品类分布</h3>
        <div class="chart-wrapper" ref="categoryChart"></div>
      </div>
      <!-- 产品种类分布（种类=item_purchased） -->
      <div class="chart-card">
        <h3>产品种类分布</h3>
        <div class="chart-wrapper" ref="itemChart"></div>
      </div>
      <!-- 颜色分布（数据库 color） -->
      <div class="chart-card">
        <h3>颜色分布</h3>
        <div class="chart-wrapper" ref="colorChart"></div>
      </div>
      <!-- 尺码分布（数据库 size） -->
      <div class="chart-card">
        <h3>尺码分布</h3>
        <div class="chart-wrapper" ref="sizeChart"></div>
      </div>
    </div>

    <!-- 品类包含的种类说明 -->
    <div class="chart-card list-section">
      <h3>品类包含的种类说明</h3>
      <div class="category-list">
        <div
          v-for="cat in categoryContainsList"
          :key="cat.categoryEn"
          class="category-item"
        >
          <div class="category-name">{{ cat.categoryName }}</div>
          <div class="category-items">
            <el-tag
              v-for="item in cat.items"
              :key="item"
              size="small"
              class="item-tag"
            >
              {{ item }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import * as echarts from 'echarts';
import apiClient from '@/utils/apiClient';
import { getCategoryLabel, getItemPurchasedLabel, categoryToZh, itemPurchasedToZh } from '@/utils/displayLabels';
import { toolboxWithExportLog } from '@/utils/chartToolbox';

// 图表实例
const categoryChart = ref(null);
const itemChart = ref(null);
const colorChart = ref(null);
const sizeChart = ref(null);

// 图表实例引用
let categoryChartInstance = null;
let itemChartInstance = null;
let colorChartInstance = null;
let sizeChartInstance = null;
let resizeHandler = null;

// 饼图配色
const chartColors = [
  '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
  '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#5ad8a6'
];

// 渲染品类分布图（品类=category，展示用：英文转中文，不改数据库）
const renderCategoryChart = () => {
  if (!categoryChartInstance) return;
  const raw = categoryDistributionData.value || [];
  const data = raw.map((item, i) => ({
    name: getCategoryLabel(item.name) || item.name,
    value: item.value,
    itemStyle: { color: chartColors[i % chartColors.length] }
  }));
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    toolbox: toolboxWithExportLog('商品品类分布'),
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: data.map(item => item.name)
    },
    series: [{
      name: '品类分布',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {d}%'
      },
      labelLine: {
        show: true
      },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' },
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' }
      },
      data: data.length ? data : [{ name: '暂无数据', value: 1, itemStyle: { color: '#ddd' } }]
    }]
  };
  categoryChartInstance.setOption(option, true);
  categoryChartInstance.resize();
};

// 产品种类分布：种类=item_purchased，数据来自 /stats/item-purchased
const itemDistributionData = ref(null);
const renderItemChart = () => {
  if (!itemChartInstance) return;
  const raw = itemDistributionData.value || [];
  const data = raw.map((item, i) => ({
    name: getItemPurchasedLabel(item.name) || item.name,
    value: item.value,
    itemStyle: { color: chartColors[i % chartColors.length] }
  }));
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    toolbox: toolboxWithExportLog('商品种类分布'),
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: data.map(item => item.name)
    },
    series: [{
      name: '种类分布',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: true, formatter: '{b}: {d}%' },
      labelLine: { show: true },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' },
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' }
      },
      data: data.length ? data : [{ name: '暂无数据', value: 1, itemStyle: { color: '#ddd' } }]
    }]
  };
  itemChartInstance.setOption(option, true);
  itemChartInstance.resize();
};

// 颜色分布图（数据来自数据库 color 列）
const colorDistributionData = ref(null);
const renderColorChart = () => {
  if (!colorChartInstance) return;
  const raw = colorDistributionData.value || [];
  const data = raw.map((item, i) => ({
    name: item.name,
    value: item.value,
    itemStyle: { color: chartColors[i % chartColors.length] }
  }));
  const option = {
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
    toolbox: toolboxWithExportLog('颜色分布'),
    legend: { orient: 'vertical', right: 10, top: 'center', data: data.map(item => item.name) },
    series: [{
      name: '颜色分布',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}: {d}%' },
      labelLine: { show: true },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' },
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' }
      },
      data: data.length ? data : [{ name: '暂无数据', value: 1, itemStyle: { color: '#ddd' } }]
    }]
  };
  colorChartInstance.setOption(option, true);
  colorChartInstance.resize();
};

// 尺码分布图（数据来自数据库 size 列）
const sizeDistributionData = ref(null);
const renderSizeChart = () => {
  if (!sizeChartInstance) return;
  const raw = sizeDistributionData.value || [];
  const data = raw.map((item, i) => ({
    name: item.name,
    value: item.value,
    itemStyle: { color: chartColors[i % chartColors.length] }
  }));
  const option = {
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
    toolbox: toolboxWithExportLog('尺码分布'),
    legend: { orient: 'vertical', right: 10, top: 'center', data: data.map(item => item.name) },
    series: [{
      name: '尺码分布',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}: {d}%' },
      labelLine: { show: true },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' },
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' }
      },
      data: data.length ? data : [{ name: '暂无数据', value: 1, itemStyle: { color: '#ddd' } }]
    }]
  };
  sizeChartInstance.setOption(option, true);
  sizeChartInstance.resize();
};

const initCharts = () => {
  disposeCharts();
  if (categoryChart.value) {
    categoryChartInstance = echarts.init(categoryChart.value);
  }
  if (itemChart.value) {
    itemChartInstance = echarts.init(itemChart.value);
  }
  if (colorChart.value) {
    colorChartInstance = echarts.init(colorChart.value);
  }
  if (sizeChart.value) {
    sizeChartInstance = echarts.init(sizeChart.value);
  }
};

// 销毁图表实例
const disposeCharts = () => {
  if (categoryChartInstance) {
    categoryChartInstance.dispose();
    categoryChartInstance = null;
  }
  if (itemChartInstance) {
    itemChartInstance.dispose();
    itemChartInstance = null;
  }
  if (colorChartInstance) {
    colorChartInstance.dispose();
    colorChartInstance = null;
  }
  if (sizeChartInstance) {
    sizeChartInstance.dispose();
    sizeChartInstance = null;
  }
};

// 数据状态
const categoryDistributionData = ref(null);
const itemTypeCount = ref(0);
const categoryCount = ref(0);
// 品类包含的种类说明：{ categoryName, categoryEn, items: [ 种类中文名 ] }
const categoryContainsList = ref([]);

// 产品品类分布：品类=category，从后端 /stats/category 读取（不修改数据库）
const loadCategoryDistribution = async () => {
  try {
    const res = await apiClient.get('/api/shopping-records/stats/category');
    const raw = res?.data ?? res;
    const list = Array.isArray(raw) ? raw : [];
    categoryDistributionData.value = list
      .map(item => {
        const name = item?.category ?? item?.name ?? '';
        const val = Number(item?.purchase_count ?? item?.value ?? 0);
        return { name: String(name), value: val };
      })
      .filter(item => item.name && item.value > 0);
  } catch (e) {
    console.error('加载产品品类分布失败:', e);
    categoryDistributionData.value = [];
  }
};

// 加载品类数量(categories)、种类数量(item-purchased)（用于顶端卡片）
const loadCounts = async () => {
  try {
    const [itemRes, catRes] = await Promise.all([
      apiClient.get('/api/shopping-records/stats/item-purchased'),
      apiClient.get('/api/shopping-records/categories')
    ]);
    const itemList = itemRes?.data ?? itemRes;
    const catList = catRes?.data ?? catRes;
    itemTypeCount.value = Array.isArray(itemList) ? itemList.length : 0;
    categoryCount.value = Array.isArray(catList) ? catList.length : 0;
  } catch (e) {
    console.error('加载种类/品类数量失败:', e);
  }
};

// 产品种类分布：从 /stats/item-purchased 读取
const loadItemDistribution = async () => {
  try {
    const res = await apiClient.get('/api/shopping-records/stats/item-purchased');
    const raw = res?.data ?? res;
    const list = Array.isArray(raw) ? raw : [];
    itemDistributionData.value = list
      .map(item => {
        const name = item?.name ?? item?.item_purchased ?? '';
        const val = Number(item?.value ?? item?.purchase_count ?? 0);
        return { name: String(name), value: val };
      })
      .filter(item => item.name && item.value > 0);
  } catch (e) {
    console.error('加载产品种类分布失败:', e);
    itemDistributionData.value = [];
  }
};

// 颜色分布：从 /stats/color 读取（shopping_data.color 列），与 loadCategoryDistribution 完全相同模式
const loadColorDistribution = async () => {
  try {
    const res = await apiClient.get('/api/shopping-records/stats/color');
    const raw = res?.data ?? res;
    const list = Array.isArray(raw) ? raw : [];
    colorDistributionData.value = list
      .map(item => {
        const name = item?.color ?? item?.name ?? '';
        const val = Number(item?.purchase_count ?? item?.value ?? 0);
        return { name: String(name), value: val };
      })
      .filter(item => item.name && item.value > 0);
  } catch (e) {
    console.error('加载颜色分布失败:', e);
    colorDistributionData.value = [];
  }
};

// 尺码分布：从 /stats/size 读取（shopping_data.size 列），与 loadCategoryDistribution 完全相同模式
const loadSizeDistribution = async () => {
  try {
    const res = await apiClient.get('/api/shopping-records/stats/size');
    const raw = res?.data ?? res;
    const list = Array.isArray(raw) ? raw : [];
    sizeDistributionData.value = list
      .map(item => {
        const name = item?.size ?? item?.name ?? '';
        const val = Number(item?.purchase_count ?? item?.value ?? 0);
        return { name: String(name), value: val };
      })
      .filter(item => item.name && item.value > 0);
  } catch (e) {
    console.error('加载尺码分布失败:', e);
    sizeDistributionData.value = [];
  }
};

// 品类包含的种类说明：从 /stats/product-distribution 按 category 分组得到种类列表（不修改数据库）
const loadCategoryContainsList = async () => {
  try {
    const res = await apiClient.get('/api/shopping-records/stats/product-distribution');
    const raw = res?.data ?? res?.list ?? res;
    const list = Array.isArray(raw) ? raw : (raw && Array.isArray(raw.data) ? raw.data : []);
    const byCategory = new Map();
    for (const r of list) {
      const cat = (r && (r.category ?? r.CATEGORY ?? r.categoryName ?? r.name)) ?? '';
      const itemEn = (r && (r.item_purchased ?? r.itemPurchased ?? r.ITEM_PURCHASED ?? r.item ?? r.name)) ?? '';
      if (!cat) continue;
      const label = getItemPurchasedLabel(itemEn) || itemEn;
      if (!label) continue;
      if (!byCategory.has(cat)) byCategory.set(cat, new Set());
      byCategory.get(cat).add(label);
    }
    categoryContainsList.value = Array.from(byCategory.entries()).map(([categoryEn, itemSet]) => ({
      categoryName: getCategoryLabel(categoryEn) || categoryEn,
      categoryEn,
      items: [...itemSet].filter(Boolean)
    }));
    if (categoryContainsList.value.length === 0) {
      buildCategoryContainsFromDisplayLabels();
    }
  } catch (e) {
    console.error('加载品类包含种类说明失败:', e);
    categoryContainsList.value = [];
    buildCategoryContainsFromDisplayLabels();
  }
};

// 当接口无数据时，用 displayLabels 中的品类与种类生成静态说明（不修改数据库，仅展示）
function buildCategoryContainsFromDisplayLabels() {
  const categoryOrder = ['Clothing', 'Footwear', 'Outerwear', 'Accessories'];
  const categoryToItems = {
    Clothing: ['Blouse', 'Sweater', 'Jeans', 'Shirt', 'Shorts', 'Pants', 'Dress', 'T-shirt', 'Skirt', 'Hoodie'],
    Footwear: ['Sandals', 'Sneakers', 'Shoes', 'Boots', 'Socks'],
    Outerwear: ['Coat', 'Jacket'],
    Accessories: ['Handbag', 'Scarf', 'Hat', 'Backpack', 'Jewelry', 'Belt', 'Sunglasses', 'Gloves']
  };
  categoryContainsList.value = categoryOrder
    .filter(cat => categoryToZh[cat])
    .map(categoryEn => ({
      categoryName: categoryToZh[categoryEn],
      categoryEn,
      items: (categoryToItems[categoryEn] || [])
        .map(en => itemPurchasedToZh[en] || en)
        .filter(Boolean)
    }));
}

// 加载产品数据
const loadProductData = async () => {
  try {
    categoryDistributionData.value = [];
    itemDistributionData.value = [];
    colorDistributionData.value = [];
    sizeDistributionData.value = [];
    categoryContainsList.value = [];
    await loadCounts();
    await nextTick();
    initCharts();
    await Promise.all([
      loadCategoryDistribution(),
      loadItemDistribution(),
      loadColorDistribution(),
      loadSizeDistribution(),
      loadCategoryContainsList()
    ]);
    await nextTick();
    renderCategoryChart();
    renderItemChart();
    renderColorChart();
    renderSizeChart();
  } catch (error) {
    console.error('加载产品数据失败:', error);
  }
};

onMounted(() => {
  loadProductData();
  resizeHandler = () => {
    categoryChartInstance?.resize();
    itemChartInstance?.resize();
    colorChartInstance?.resize();
    sizeChartInstance?.resize();
  };
  window.addEventListener('resize', resizeHandler);
});

onBeforeUnmount(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler);
    resizeHandler = null;
  }
  disposeCharts();
});
</script>

<style scoped>
.product-analysis {
  padding: 20px;
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

.chart-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-wrapper {
  height: 300px;
}

.list-section {
  margin-top: 20px;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.category-item {
  flex: 1 1 280px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.category-name {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid #e2e8f0;
}

.category-items {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.item-tag {
  margin: 0;
}
</style>
