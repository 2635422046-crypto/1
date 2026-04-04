<template>
  <div class="comprehensive-analysis">
    <el-alert
      v-if="apiError"
      :title="apiError"
      type="warning"
      show-icon
      closable
      class="mb-16"
      @close="apiError = null"
    />
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 分析1：客户相关 -->
      <el-tab-pane label="分析1" name="summary">
        <div class="chart-grid">
          <el-card shadow="hover" class="chart-card">
            <template #header>客户价格敏感度</template>
            <div v-loading="loadState.summaryPriceSensitivity" class="chart-wrap" ref="summaryPriceSensitivityRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>客户细分（年龄×性别×价值）</template>
            <div v-loading="loadState.summarySegmentation" class="chart-wrap" ref="summarySegmentationRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>会员 vs 非会员</template>
            <div v-loading="loadState.hiveSubscription" class="chart-wrap" ref="hiveSubscriptionRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>年龄段消费</template>
            <div v-loading="loadState.hiveAgeGroup" class="chart-wrap" ref="hiveAgeGroupRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>高价值客户分段</template>
            <div v-loading="loadState.hiveHighValue" class="chart-wrap" ref="hiveHighValueRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>复购 vs 一次性客户</template>
            <div v-loading="loadState.hiveRepeatOnetime" class="chart-wrap" ref="hiveRepeatOnetimeRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>RFM 分段</template>
            <div v-loading="loadState.hiveRfm" class="chart-wrap" ref="hiveRfmRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>客户生命周期阶段</template>
            <div v-loading="loadState.advLifecycle" class="chart-wrap" ref="advLifecycleRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>客户状态（活跃/风险/流失）</template>
            <div v-loading="loadState.advCustomerStatus" class="chart-wrap" ref="advCustomerStatusRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>行为聚类</template>
            <div v-loading="loadState.advBehaviorCluster" class="chart-wrap" ref="advBehaviorClusterRef"></div>
          </el-card>
        </div>
      </el-tab-pane>
      <!-- 分析2：销量和金额相关 -->
      <el-tab-pane label="分析2" name="hive">
        <div class="chart-grid">
          <el-card shadow="hover" class="chart-card">
            <template #header>购买频率与金额关系</template>
            <div v-loading="loadState.summaryFrequencyAmount" class="chart-wrap" ref="summaryFrequencyAmountRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>季节性购买</template>
            <div v-loading="loadState.hiveSeason" class="chart-wrap" ref="hiveSeasonRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>月度销售</template>
            <div v-loading="loadState.hiveMonthly" class="chart-wrap" ref="hiveMonthlyRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>优惠码对购买商品种类的影响</template>
            <div v-loading="loadState.hivePromoItemEffect" class="chart-wrap" ref="hivePromoItemEffectRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>折扣和销售的关系</template>
            <div v-loading="loadState.hiveDiscountSales" class="chart-wrap" ref="hiveDiscountSalesRef"></div>
          </el-card>
        </div>
      </el-tab-pane>
      <!-- 分析3：其他 -->
      <el-tab-pane label="分析3" name="advanced">
        <div class="chart-grid">
          <el-card shadow="hover" class="chart-card">
            <template #header>评分分布</template>
            <div v-loading="loadState.hiveRatingDist" class="chart-wrap" ref="hiveRatingDistRef"></div>
          </el-card>
          <el-card shadow="hover" class="chart-card">
            <template #header>品类关联（一起购买）</template>
            <div v-loading="loadState.advCategoryAssoc" class="chart-wrap" ref="advCategoryAssocRef"></div>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue';
import * as echarts from 'echarts';
import apiClient from '@/utils/apiClient';
import { getCategoryLabel, getSeasonLabel, getItemPurchasedLabel } from '@/utils/displayLabels';
import { toolboxWithExportLog } from '@/utils/chartToolbox';

const API_BASE = '/api/shopping-records/stats/comprehensive';

const activeTab = ref('summary');
const apiError = ref('');
const loadState = reactive({
  summaryFrequencyAmount: false,
  summaryPriceSensitivity: false,
  summarySegmentation: false,
  hiveSubscription: false,
  hiveAgeGroup: false,
  hiveSeason: false,
  hiveMonthly: false,
  hiveHighValue: false,
  hiveRepeatOnetime: false,
  hiveRfm: false,
  hiveRatingDist: false,
  hivePromoItemEffect: false,
  hiveDiscountSales: false,
  advLifecycle: false,
  advCustomerStatus: false,
  advBehaviorCluster: false,
  advCategoryAssoc: false
});

const summaryFrequencyAmountRef = ref(null);
const summaryPriceSensitivityRef = ref(null);
const summarySegmentationRef = ref(null);
const hiveSubscriptionRef = ref(null);
const hiveAgeGroupRef = ref(null);
const hiveSeasonRef = ref(null);
const hiveMonthlyRef = ref(null);
const hiveHighValueRef = ref(null);
const hiveRepeatOnetimeRef = ref(null);
const hiveRfmRef = ref(null);
const hiveRatingDistRef = ref(null);
const hivePromoItemEffectRef = ref(null);
const hiveDiscountSalesRef = ref(null);
const advLifecycleRef = ref(null);
const advCustomerStatusRef = ref(null);
const advBehaviorClusterRef = ref(null);
const advCategoryAssocRef = ref(null);

const chartInstances = {};

/** 仅当容器存在且有宽高时才 init 或返回实例，避免 0 尺寸下绘图 */
function getChart(refObj, key) {
  const el = refObj && refObj.value;
  if (!el || !(el instanceof HTMLElement)) return null;
  if (el.offsetWidth < 1 || el.offsetHeight < 1) return null;
  if (!chartInstances[key]) {
    chartInstances[key] = echarts.init(el);
  }
  return chartInstances[key];
}

/** 等容器有宽高后再 setOption，并 resize；Tab 显示后由 watch 统一 resize */
function setOptionWithResize(refObj, key, option) {
  const trySet = (n = 0) => {
    const chart = getChart(refObj, key);
    if (chart) {
      chart.setOption(option, true);
      nextTick(() => chart.resize());
      setTimeout(() => chart.resize(), 300);
      return;
    }
    if (n < 35) setTimeout(() => trySet(n + 1), 80);
  };
  nextTick(() => trySet(0));
}

async function loadSummaryFrequencyAmount() {
  loadState.summaryFrequencyAmount = true;
  try {
    const res = await apiClient.get(`${API_BASE}/summary/frequency-amount`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: toolboxWithExportLog('购买频率与金额'),
      xAxis: { type: 'category', data: list.map(d => d.frequency_group) },
      yAxis: [{ type: 'value', name: '客户数' }, { type: 'value', name: '金额' }],
      series: [
        { name: '客户数', type: 'bar', data: list.map(d => d.customer_count) },
        { name: '平均消费', type: 'line', yAxisIndex: 1, data: list.map(d => d.avg_total_spent) }
      ]
    };
    setOptionWithResize(summaryFrequencyAmountRef, 'summaryFrequencyAmount', option);
  } catch (e) {
    console.error('loadSummaryFrequencyAmount', e);
    apiError.value = '综合分析接口请求失败（如 404），请确认后端已启动并包含综合分析模块。';
  } finally {
    loadState.summaryFrequencyAmount = false;
  }
}

const priceSensitivityToZh = { Low: '低', Medium: '中', High: '高' };
function getPriceSensitivityLabel(en) {
  if (en == null || en === '') return '';
  return priceSensitivityToZh[en] ?? String(en);
}

async function loadSummaryPriceSensitivity() {
  loadState.summaryPriceSensitivity = true;
  try {
    const res = await apiClient.get(`${API_BASE}/summary/price-sensitivity`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'item' },
      toolbox: toolboxWithExportLog('价格敏感度'),
      series: [{ type: 'pie', radius: '60%', data: list.map(d => ({ name: getPriceSensitivityLabel(d.price_sensitivity), value: d.customer_count })) }]
    };
    setOptionWithResize(summaryPriceSensitivityRef, 'summaryPriceSensitivity', option);
  } catch (e) {
    console.error('loadSummaryPriceSensitivity', e);
  } finally {
    loadState.summaryPriceSensitivity = false;
  }
}

const valueSegmentToZh = { 'High Value': '高价值', 'Medium Value': '中价值', 'Low Value': '低价值', High: '高', Medium: '中', Low: '低' };
function getValueSegmentLabel(en) {
  if (en == null || en === '') return '';
  return valueSegmentToZh[en] ?? String(en);
}

async function loadSummarySegmentation() {
  loadState.summarySegmentation = true;
  try {
    const res = await apiClient.get(`${API_BASE}/summary/customer-segmentation`);
    const list = (res && res.data) ? res.data : [];
    const bySeg = {};
    list.forEach(d => {
      const key = getValueSegmentLabel(d.value_segment);
      bySeg[key] = (bySeg[key] || 0) + Number(d.customer_count || 0);
    });
    const segs = Object.keys(bySeg);
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      xAxis: { type: 'category', data: segs },
      yAxis: { type: 'value', name: '客户数' },
      series: [{ name: '客户数', type: 'bar', data: segs.map(s => bySeg[s]) }]
    };
    setOptionWithResize(summarySegmentationRef, 'summarySegmentation', option);
  } catch (e) {
    console.error('loadSummarySegmentation', e);
  } finally {
    loadState.summarySegmentation = false;
  }
}

async function loadHiveSubscription() {
  loadState.hiveSubscription = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/subscription`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'item' },
      toolbox: { feature: { saveAsImage: {} } },
      series: [{ type: 'pie', radius: '60%', data: list.map(d => ({ name: d.subscription_status === 'Yes' ? '会员' : '非会员', value: d.total_purchases })) }]
    };
    setOptionWithResize(hiveSubscriptionRef, 'hiveSubscription', option);
  } catch (e) {
    console.error('loadHiveSubscription', e);
  } finally {
    loadState.hiveSubscription = false;
  }
}

async function loadHiveAgeGroup() {
  loadState.hiveAgeGroup = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/age-group`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      xAxis: { type: 'category', data: list.map(d => d.age_group) },
      yAxis: [{ type: 'value', name: '订单数' }, { type: 'value', name: '均价' }],
      series: [
        { name: '订单数', type: 'bar', data: list.map(d => d.purchase_count) },
        { name: '均价', type: 'line', yAxisIndex: 1, data: list.map(d => d.avg_amount) }
      ]
    };
    setOptionWithResize(hiveAgeGroupRef, 'hiveAgeGroup', option);
  } catch (e) {
    console.error('loadHiveAgeGroup', e);
  } finally {
    loadState.hiveAgeGroup = false;
  }
}

async function loadHiveSeason() {
  loadState.hiveSeason = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/season`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      xAxis: { type: 'category', data: list.map(d => getSeasonLabel(d.season)) },
      yAxis: { type: 'value', name: '销量' },
      series: [{ name: '销量', type: 'bar', data: list.map(d => d.total_sales) }]
    };
    setOptionWithResize(hiveSeasonRef, 'hiveSeason', option);
  } catch (e) {
    console.error('loadHiveSeason', e);
  } finally {
    loadState.hiveSeason = false;
  }
}

async function loadHiveMonthly() {
  loadState.hiveMonthly = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/monthly`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      xAxis: { type: 'category', data: list.map(d => d.month) },
      yAxis: { type: 'value', name: '金额' },
      series: [{ name: '月销售额', type: 'line', data: list.map(d => d.total_amount), smooth: true }]
    };
    setOptionWithResize(hiveMonthlyRef, 'hiveMonthly', option);
  } catch (e) {
    console.error('loadHiveMonthly', e);
  } finally {
    loadState.hiveMonthly = false;
  }
}

async function loadHivePromoItemEffect() {
  loadState.hivePromoItemEffect = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/promo-item-effect`);
    const list = (res && res.data) ? res.data : [];
    const items = list.map(d => getItemPurchasedLabel(d.item_purchased));
    const option = {
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          const d = list[params[0].dataIndex];
          const nameZh = getItemPurchasedLabel(d.item_purchased);
          return `${nameZh}<br/>使用优惠码：${d.promo_count} 笔<br/>未使用优惠码：${d.no_promo_count} 笔<br/>优惠码使用率：${d.promo_rate}%`;
        }
      },
      toolbox: { feature: { saveAsImage: {} } },
      legend: { data: ['使用优惠码', '未使用优惠码'] },
      grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
      xAxis: {
        type: 'category',
        name: '商品种类',
        nameLocation: 'middle',
        nameGap: 50,
        data: items,
        axisLabel: { rotate: 35, interval: 0, fontSize: 11 }
      },
      yAxis: { type: 'value', name: '订单数', nameLocation: 'end' },
      series: [
        {
          name: '使用优惠码',
          type: 'bar',
          stack: 'promo',
          data: list.map(d => d.promo_count),
          itemStyle: { color: '#91cc75' }
        },
        {
          name: '未使用优惠码',
          type: 'bar',
          stack: 'promo',
          data: list.map(d => d.no_promo_count),
          itemStyle: { color: '#fac858' }
        }
      ]
    };
    setOptionWithResize(hivePromoItemEffectRef, 'hivePromoItemEffect', option);
  } catch (e) {
    console.error('loadHivePromoItemEffect', e);
  } finally {
    loadState.hivePromoItemEffect = false;
  }
}

async function loadHiveDiscountSales() {
  loadState.hiveDiscountSales = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/discount-sales`);
    const list = (res && res.data) ? res.data : [];
    const labels = list.map(d => d.discount_applied === 'Yes' ? '有折扣' : '无折扣');
    const option = {
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          const d = list[params[0].dataIndex];
          return `${d.discount_applied === 'Yes' ? '有折扣' : '无折扣'}<br/>订单数：${d.order_count}<br/>销售总额：¥${d.total_amount}<br/>平均金额：¥${d.avg_amount}`;
        }
      },
      toolbox: { feature: { saveAsImage: {} } },
      legend: { data: ['订单数', '销售总额'] },
      xAxis: { type: 'category', name: '是否使用折扣', data: labels },
      yAxis: [
        { type: 'value', name: '订单数' },
        { type: 'value', name: '销售总额（元）' }
      ],
      series: [
        { name: '订单数', type: 'bar', data: list.map(d => d.order_count), itemStyle: { color: '#5470c6' } },
        { name: '销售总额', type: 'bar', yAxisIndex: 1, data: list.map(d => d.total_amount), itemStyle: { color: '#91cc75' } }
      ]
    };
    setOptionWithResize(hiveDiscountSalesRef, 'hiveDiscountSales', option);
  } catch (e) {
    console.error('loadHiveDiscountSales', e);
  } finally {
    loadState.hiveDiscountSales = false;
  }
}

const customerSegmentToZh = { 'High Value': '高价值', 'Medium Value': '中价值', 'Low Value': '低价值', Champions: '冠军', Loyal: '忠诚', Potential: '潜力', 'At Risk': '风险', 'Cannot Lose': '不可流失', 'Need Attention': '需关注', 'need attention': '需关注', New: '新客', Promising: '潜力', Hibernating: '沉睡' };
function getCustomerSegmentLabel(en) {
  if (en == null || en === '') return '';
  return customerSegmentToZh[en] ?? getValueSegmentLabel(en) ?? String(en);
}

async function loadHiveHighValue() {
  loadState.hiveHighValue = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/high-value`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      xAxis: { type: 'category', data: list.map(d => getCustomerSegmentLabel(d.customer_segment)) },
      yAxis: [{ type: 'value', name: '客户数' }, { type: 'value', name: '平均消费' }],
      series: [
        { name: '客户数', type: 'bar', data: list.map(d => d.customer_count) },
        { name: '平均消费', type: 'line', yAxisIndex: 1, data: list.map(d => d.avg_total_spent) }
      ]
    };
    setOptionWithResize(hiveHighValueRef, 'hiveHighValue', option);
  } catch (e) {
    console.error('loadHiveHighValue', e);
  } finally {
    loadState.hiveHighValue = false;
  }
}

const customerTypeToZh = { Repeat: '复购', 'One-time': '一次性', Onetime: '一次性' };
function getCustomerTypeLabel(en) {
  if (en == null || en === '') return '';
  return customerTypeToZh[en] ?? String(en);
}

async function loadHiveRepeatOnetime() {
  loadState.hiveRepeatOnetime = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/repeat-vs-onetime`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'item' },
      toolbox: { feature: { saveAsImage: {} } },
      series: [{ type: 'pie', radius: '60%', data: list.map(d => ({ name: getCustomerTypeLabel(d.customer_type), value: d.customer_count })) }]
    };
    setOptionWithResize(hiveRepeatOnetimeRef, 'hiveRepeatOnetime', option);
  } catch (e) {
    console.error('loadHiveRepeatOnetime', e);
  } finally {
    loadState.hiveRepeatOnetime = false;
  }
}

async function loadHiveRfm() {
  loadState.hiveRfm = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/rfm`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      xAxis: { type: 'category', data: list.map(d => getCustomerSegmentLabel(d.customer_segment)) },
      yAxis: { type: 'value', name: '客户数' },
      series: [{ name: '客户数', type: 'bar', data: list.map(d => d.customer_count) }]
    };
    setOptionWithResize(hiveRfmRef, 'hiveRfm', option);
  } catch (e) {
    console.error('loadHiveRfm', e);
  } finally {
    loadState.hiveRfm = false;
  }
}

async function loadHiveRatingDist() {
  loadState.hiveRatingDist = true;
  try {
    const res = await apiClient.get(`${API_BASE}/hive/rating-dist`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      xAxis: { type: 'category', data: list.map(d => `${d.rating_bucket}分`) },
      yAxis: { type: 'value' },
      series: [{ name: '评价数', type: 'bar', data: list.map(d => d.rating_count) }]
    };
    setOptionWithResize(hiveRatingDistRef, 'hiveRatingDist', option);
  } catch (e) {
    console.error('loadHiveRatingDist', e);
  } finally {
    loadState.hiveRatingDist = false;
  }
}

const lifecycleStageToZh = { New: '新客', Active: '活跃', Loyal: '忠诚', 'At Risk': '风险', Churned: '流失', Dormant: '沉睡' };
function getLifecycleStageLabel(en) {
  if (en == null || en === '') return '';
  return lifecycleStageToZh[en] ?? String(en);
}

async function loadAdvLifecycle() {
  loadState.advLifecycle = true;
  try {
    const res = await apiClient.get(`${API_BASE}/advanced/lifecycle-stage`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      xAxis: { type: 'category', data: list.map(d => getLifecycleStageLabel(d.lifecycle_stage)) },
      yAxis: [{ type: 'value', name: '客户数' }, { type: 'value', name: '金额' }],
      series: [
        { name: '客户数', type: 'bar', data: list.map(d => d.customer_count) },
        { name: '平均消费', type: 'line', yAxisIndex: 1, data: list.map(d => d.avg_total_spent) }
      ]
    };
    setOptionWithResize(advLifecycleRef, 'advLifecycle', option);
  } catch (e) {
    console.error('loadAdvLifecycle', e);
  } finally {
    loadState.advLifecycle = false;
  }
}

const customerStatusToZh = { Active: '活跃', 'At Risk': '风险', Churned: '流失' };
function getCustomerStatusLabel(en) {
  if (en == null || en === '') return '';
  return customerStatusToZh[en] ?? String(en);
}

async function loadAdvCustomerStatus() {
  loadState.advCustomerStatus = true;
  try {
    const res = await apiClient.get(`${API_BASE}/advanced/customer-status`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'item' },
      toolbox: { feature: { saveAsImage: {} } },
      series: [{ type: 'pie', radius: '60%', data: list.map(d => ({ name: getCustomerStatusLabel(d.customer_status), value: d.customer_count })) }]
    };
    setOptionWithResize(advCustomerStatusRef, 'advCustomerStatus', option);
  } catch (e) {
    console.error('loadAdvCustomerStatus', e);
  } finally {
    loadState.advCustomerStatus = false;
  }
}

const behaviorClusterToZh = { 'High Spender': '高消费', 'Frequent Buyer': '高频购买', 'Occasional': '偶尔购买', 'New Customer': '新客', 'At Risk': '风险' };
function getBehaviorClusterLabel(en) {
  if (en == null || en === '') return '';
  return behaviorClusterToZh[en] ?? String(en);
}

async function loadAdvBehaviorCluster() {
  loadState.advBehaviorCluster = true;
  try {
    const res = await apiClient.get(`${API_BASE}/advanced/behavior-cluster`);
    const list = (res && res.data) ? res.data : [];
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      xAxis: { type: 'category', data: list.map(d => getBehaviorClusterLabel(d.behavior_cluster)) },
      yAxis: { type: 'value', name: '客户数' },
      series: [{ name: '客户数', type: 'bar', data: list.map(d => d.customer_count) }]
    };
    setOptionWithResize(advBehaviorClusterRef, 'advBehaviorCluster', option);
  } catch (e) {
    console.error('loadAdvBehaviorCluster', e);
  } finally {
    loadState.advBehaviorCluster = false;
  }
}

async function loadAdvCategoryAssoc() {
  loadState.advCategoryAssoc = true;
  try {
    const res = await apiClient.get(`${API_BASE}/advanced/category-association`);
    const list = ((res && res.data) ? res.data : []).slice(0, 15);
    const option = {
      tooltip: { trigger: 'axis' },
      toolbox: { feature: { saveAsImage: {} } },
      grid: { left: '20%' },
      xAxis: { type: 'value', name: '一起购买次数' },
      yAxis: { type: 'category', data: list.map(d => `${getCategoryLabel(d.category1)} + ${getCategoryLabel(d.category2)}`).reverse() },
      series: [{ name: '一起购买次数', type: 'bar', data: list.map(d => d.purchased_together).reverse() }]
    };
    setOptionWithResize(advCategoryAssocRef, 'advCategoryAssoc', option);
  } catch (e) {
    console.error('loadAdvCategoryAssoc', e);
  } finally {
    loadState.advCategoryAssoc = false;
  }
}

/** 分析1：客户相关图表 */
function loadTab1Charts() {
  loadSummaryPriceSensitivity();
  loadSummarySegmentation();
  loadHiveSubscription();
  loadHiveAgeGroup();
  loadHiveHighValue();
  loadHiveRepeatOnetime();
  loadHiveRfm();
  loadAdvLifecycle();
  loadAdvCustomerStatus();
  loadAdvBehaviorCluster();
}

/** 分析2：销量和金额相关图表 */
function loadTab2Charts() {
  loadSummaryFrequencyAmount();
  loadHiveSeason();
  loadHiveMonthly();
  loadHivePromoItemEffect();
  loadHiveDiscountSales();
}

/** 分析3：其他图表 */
function loadTab3Charts() {
  loadHiveRatingDist();
  loadAdvCategoryAssoc();
}

onMounted(() => {
  nextTick(() => {
    setTimeout(() => {
      loadTab1Charts();
      setTimeout(handleResize, 600);
    }, 120);
  });
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  Object.values(chartInstances).forEach(chart => chart && chart.dispose());
});

function handleResize() {
  nextTick(() => {
    Object.values(chartInstances).forEach(chart => chart && chart.resize());
  });
}

watch(activeTab, (tab) => {
  nextTick(() => {
    setTimeout(() => {
      if (tab === 'summary') loadTab1Charts();
      else if (tab === 'hive') loadTab2Charts();
      else if (tab === 'advanced') loadTab3Charts();
      setTimeout(handleResize, 500);
    }, 80);
  });
});
</script>

<style scoped>
.comprehensive-analysis {
  padding: 16px;
}
.chart-grid {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.mb-16 { margin-bottom: 16px; }
.chart-card {
  min-height: 460px;
}
.chart-wrap {
  height: 420px;
  width: 100%;
  min-height: 420px;
}
</style>
