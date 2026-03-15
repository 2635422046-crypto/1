<template>
  <div class="word-cloud-container">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">词云加载失败</div>
    <div ref="chartRef" class="chart" :style="{ width: '100%', height: '400px' }"></div>
  </div>
</template>

<script>
import { defineComponent, onMounted, onBeforeUnmount, ref, watch } from 'vue';
import * as echarts from 'echarts';
import 'echarts-wordcloud';
// 统一使用 apiClient，避免硬编码 localhost:8080
import apiClient from '@/utils/apiClient';
import { getCategoryLabel, getItemPurchasedLabel } from '@/utils/displayLabels';

export default defineComponent({
  name: 'WordCloudChart',
  props: {
    /** 'category' 商品品类（按 category 统计） | 'itemPurchased' 商品种类（按 item_purchased 统计） */
    dataSource: {
      type: String,
      default: 'category',
      validator: (v) => ['category', 'itemPurchased'].includes(v)
    }
  },
  setup(props) {
    const chartRef = ref(null);
    const loading = ref(false);
    const error = ref(false);
    let chartInstance = null;
    let resizeHandler = null;

    // 颜色集合
    const colors = [
      '#c23531', '#2f4554', '#61a0a8', 
      '#d48265', '#91c7ae', '#749f83', 
      '#ca8622', '#bda29a', '#6e7074'
    ];

    // 加载词云数据
    const loadData = async () => {
      try {
        loading.value = true;
        error.value = false;
        
        const isCategory = props.dataSource === 'category';
        // 根据 dataSource 决定请求哪个统计接口
        const url = isCategory
          ? '/api/shopping-records/stats/category'
          : '/api/shopping-records/stats/item-purchased';
        // apiClient 的 baseURL 已配置为 http://localhost:8080，此处只需传相对路径
        const response = await apiClient.get(url);
        
        if (!chartRef.value) return;
        
        if (!chartInstance) {
          chartInstance = echarts.init(chartRef.value);
          resizeHandler = () => chartInstance?.resize();
          window.addEventListener('resize', resizeHandler);
        }

        let data;
        // apiClient 响应拦截器已解包一层 response.data，
        // 后端返回 Result<List> 结构，data 字段即为真实列表
        if (isCategory) {
          const rawList = Array.isArray(response?.data) ? response.data : (Array.isArray(response) ? response : []);
          data = rawList.map(item => {
            const displayName = getCategoryLabel(item.category) || String(item.category ?? '');
            const count = item.purchase_count;
            if (typeof count !== 'number' || !Number.isInteger(count) || count <= 0) {
              return {
                name: displayName,
                value: 1,
                textStyle: { color: colors[Math.floor(Math.random() * colors.length)] },
                totalAmount: item.total_amount ?? 0,
                purchaseCount: count ?? 0
              };
            }
            return {
              name: displayName,
              value: Math.max(1, count),
              textStyle: { color: colors[Math.floor(Math.random() * colors.length)] },
              totalAmount: item.total_amount ?? 0,
              purchaseCount: count
            };
          }).filter(d => d.name !== '');
        } else {
          const rawList = Array.isArray(response?.data) ? response.data : (Array.isArray(response) ? response : []);
          data = rawList.map(item => {
            const rawName = item?.name ?? item?.item_purchased ?? '';
            const displayName = getItemPurchasedLabel(rawName) || String(rawName);
            const val = Number(item?.value ?? item?.purchase_count ?? 0) || 1;
            return {
              name: displayName,
              value: val,
              textStyle: { color: colors[Math.floor(Math.random() * colors.length)] },
              totalAmount: val,
              purchaseCount: val
            };
          }).filter(d => d.name !== '');
        }

        // 设置词云选项（isCategory 已在上方定义）
        chartInstance.setOption({
          backgroundColor: 'transparent',
          tooltip: {
            show: true,
            formatter: params => {
              if (isCategory) {
                return `<div><strong>${params.name}</strong><br/>购买次数: ${params.data.purchaseCount}<br/>总金额: ${params.data.totalAmount}</div>`;
              }
              return `<div><strong>${params.name}</strong><br/>购买笔数: ${params.data.purchaseCount}</div>`;
            }
          },
          series: [{
            type: 'wordCloud',
            shape: 'circle',
            left: 'center',
            top: 'center',
            width: '100%',
            height: '100%',
            left: 0,
            top: 0,
            right: null,
            bottom: null,
            sizeRange: [12, 60],
            rotationRange: [-45, 45],
            rotationStep: 15,
            gridSize: 8,
            drawOutOfBound: false,
            textStyle: {
              fontFamily: 'sans-serif',
              fontWeight: 'bold',
              color: function () {
                return colors[Math.floor(Math.random() * colors.length)];
              }
            },
            emphasis: {
              textStyle: {
                shadowBlur: 10,
                shadowColor: '#333'
              }
            },
            data: data
          }]
        });
      } catch (err) {
        console.error('加载词云数据失败:', err);
        error.value = true;
      } finally {
        loading.value = false;
      }
    };

    onMounted(() => {
      loadData();
    });

    watch(() => props.dataSource, () => {
      loadData();
    });

    // 组件卸载时清理资源
    onBeforeUnmount(() => {
      if (resizeHandler) {
        window.removeEventListener('resize', resizeHandler);
        resizeHandler = null;
      }
      if (chartInstance) {
        chartInstance.dispose();
        chartInstance = null;
      }
    });

    return {
      chartRef,
      loading,
      error
    };
  }
});
</script>

<style scoped>
.word-cloud-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.chart {
  width: 100%;
  height: 400px;
}

.loading, .error {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 16px;
  color: #666;
}

.error {
  color: #f56c6c;
}
</style>