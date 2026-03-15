import apiClient from '@/utils/apiClient'

/**
 * 获取产品品类分布数据
 * @returns {Promise<{success: boolean, data: Array<{name: string, value: number}>}>}
 */
export const getProductCategory = async () => {
  try {
    const response = await apiClient.get('/api/product/category')
    return {
      success: true,
      data: response.data.data || []
    }
  } catch (error) {
    console.error('获取产品品类数据失败:', error)
    return {
      success: false,
      data: []
    }
  }
}

/**
 * 获取热销产品TOP10数据
 * @returns {Promise<{success: boolean, data: Array<{name: string, value: number}>}>}
 */
export const getTopProducts = async () => {
  try {
    const response = await apiClient.get('/api/product/top')
    return {
      success: true,
      data: response.data.data || []
    }
  } catch (error) {
    console.error('获取热销产品数据失败:', error)
    return {
      success: false,
      data: []
    }
  }
}