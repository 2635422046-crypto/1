import { reportClientOperation } from '@/utils/operationLog';

/**
 * ECharts toolbox：保存为图片时先记入操作日志（图表下载），再触发浏览器下载（PNG）。
 *
 * 说明：ECharts 内置 saveAsImage 的 onclick 不会被触发，此处改用自定义 feature key
 * （mySaveImage），ECharts 对自定义 feature 完整暴露 onclick 回调。
 */

// ECharts 内置保存图片的图标 path（下载箭头）
const SAVE_ICON = 'path://M4.7,22.9L29.3,45.5L54.7,23.4M4.6,43.6L4.6,61.3L53.8,61.2L54.7,43.6M29.2,45.1L29.2,0';

export function toolboxWithExportLog(chartLabel) {
  const label = chartLabel || 'chart';
  return {
    feature: {
      mySaveImage: {
        show: true,
        title: '保存为图片',
        icon: SAVE_ICON,
        async onclick(_, api) {
          await reportClientOperation('DATA_EXPORT', `图表下载: ${label}`);
          const title = String(label).replace(/\s+/g, '_') || 'echarts';
          const isSvg = api.getZr().painter.getType() === 'svg';
          const type = isSvg ? 'svg' : 'png';
          const url = api.getConnectedDataURL({
            type,
            backgroundColor: '#fff',
            excludeComponents: ['toolbox'],
            pixelRatio: window.devicePixelRatio || 2
          });
          const a = document.createElement('a');
          a.download = title + '.' + type;
          a.href = url;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
        }
      }
    }
  };
}
