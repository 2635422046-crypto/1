/**
 * 仅用于前端展示：将接口返回的商品类别(英文)、地区(英文/拼音)、季节等转为中文显示，不修改数据库。
 */

// 商品类别：英文 -> 中文
export const categoryToZh = {
  Clothing: '服装',
  Footwear: '鞋类',
  Outerwear: '外套',
  Accessories: '配饰'
};

// 购买商品名称 item_purchased：英文 -> 中文（用于产品品类分布饼图等）
export const itemPurchasedToZh = {
  Blouse: '女式衬衫',
  Sweater: '毛衣',
  Jeans: '牛仔裤',
  Sandals: '凉鞋',
  Sneakers: '运动鞋',
  Shirt: '衬衫',
  Shorts: '短裤',
  Coat: '外套',
  Handbag: '手袋',
  Shoes: '鞋',
  'T-shirt': 'T恤',
  Pants: '裤子',
  Jacket: '夹克',
  Scarf: '围巾',
  Dress: '连衣裙',
  Hat: '帽子',
  Backpack: '背包',
  Socks: '袜子',
  Jewelry: '珠宝',
  Boots: '靴子',
  Belt: '腰带',
  Sunglasses: '太阳镜',
  Skirt: '裙子',
  Gloves: '手套',
  Hoodie: '连帽衫',

};

// 性别：英文 -> 中文
export const genderToZh = {
  Male: '男',
  Female: '女'
};

// 季节：英文 -> 中文
export const seasonToZh = {
  Spring: '春季',
  Summer: '夏季',
  Fall: '秋季',
  Winter: '冬季'
};

// 地区：英文/拼音 -> 中文
export const locationToZh = {
  Zhejiang: '浙江',
  Shandong: '山东',
  Hebei: '河北',
  Heilongjiang: '黑龙江',
  Liaoning: '辽宁',
  Hunan: '湖南',
  Jiangsu: '江苏',
  Chongqing: '重庆',
  Sichuan: '四川',
  Shanghai: '上海',
  Guangdong: '广东',
  Gansu: '甘肃',
  Anhui: '安徽',
  Shanxi: '山西',
  Shaanxi: '陕西',
  Henan: '河南',
  Guangxi: '广西',
  Hubei: '湖北',
  Beijing: '北京',
  Jilin: '吉林',
  Qinghai: '青海',
  Hainan: '海南',
  Ningxia: '宁夏',
  Tibet: '西藏',
  Xinjiang: '新疆',
  Tianjin: '天津',
  Fujian: '福建',
  Jiangxi: '江西',
  Guizhou: '贵州',
  Yunnan: '云南',
  InnerMongolia: '内蒙古',
  Guizhou: '贵州'
};

/**
 * 将商品类别转为中文显示（空值返回空字符串，避免图表不显示）
 */
export function getCategoryLabel(en) {
  if (en == null || en === '') return '';
  return categoryToZh[en] ?? String(en);
}

/**
 * 将 item_purchased（购买商品名称）转为中文显示，用于产品品类分布等
 */
export function getItemPurchasedLabel(en) {
  if (en == null || en === '') return '';
  return itemPurchasedToZh[en] ?? String(en);
}

/**
 * 将性别转为中文显示
 */
export function getGenderLabel(en) {
  if (en == null || en === '') return '';
  return genderToZh[en] ?? String(en);
}

/**
 * 将季节转为中文显示
 */
export function getSeasonLabel(en) {
  if (en == null || en === '') return '';
  return seasonToZh[en] ?? String(en);
}

/**
 * 将地区转为中文显示
 */
export function getLocationLabel(en) {
  if (en == null || en === '') return '';
  return locationToZh[en] ?? String(en);
}

/**
 * 将 GeoJSON 中的省/市/区全称转为与 locationToZh 一致的短名称，用于地图数据匹配
 * 例如：浙江省 -> 浙江，北京市 -> 北京，内蒙古自治区 -> 内蒙古
 */
export function normalizeProvinceNameForMap(fullName) {
  if (fullName == null || fullName === '') return '';
  return String(fullName)
    .replace(/省$/, '')
    .replace(/市$/, '')
    .replace(/壮族自治区$/, '')
    .replace(/回族自治区$/, '')
    .replace(/维吾尔自治区$/, '')
    .replace(/自治区$/, '')
    .replace(/特别行政区$/, '')
    .trim();
}
