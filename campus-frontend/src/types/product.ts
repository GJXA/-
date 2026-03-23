/**
 * 商品相关类型定义
 */
import type { PageResult, PageParams } from './common'

/**
 * 商品基本信息
 */
export interface Product {
  id: number
  title: string
  description: string
  price: number
  originalPrice?: number
  categoryId: number
  categoryName?: string
  condition: 'new' | 'used' | 'like_new'
  status: 'pending' | 'published' | 'sold' | 'deleted' | 'hidden'
  location: string
  images: string[]
  stock: number
  viewCount: number
  likeCount: number
  commentCount: number
  contactPhone?: string
  contactWechat?: string
  sellerId: number
  sellerName?: string
  sellerAvatar?: string
  sellerRating?: number
  createTime: string
  updateTime: string
  publishTime?: string
  expireTime?: string
  // 前端特有字段
  name?: string // 商品名称别名
  marketPrice?: number // 市场价
  imageUrls?: string[] // 图片URL别名
  category?: string // 分类名称
  userId?: number // 用户ID别名
  username?: string // 用户名
  avatarUrl?: string // 头像URL
  rating?: number // 评分
  address?: string // 地址
  productCount?: number // 商品数量
  positiveReviews?: number // 好评数
  tags?: string[] // 标签
  keywords?: string[] // 关键词
  specifications?: string // 规格
  attributes?: string // 属性
  shippingInfo?: string // 配送信息
  returnPolicy?: string // 退货政策
}

/**
 * 商品分类
 */
export interface ProductCategory {
  id: number
  name: string
  description?: string
  parentId?: number
  sortOrder: number
  icon?: string
  status: number
  createTime: string
}

/**
 * 商品查询参数
 */
export interface ProductQueryParams extends PageParams {
  keyword?: string
  categoryId?: number
  minPrice?: number
  maxPrice?: number
  condition?: string
  location?: string
  status?: string
  sellerId?: number
  sortField?: 'price' | 'view_count' | 'like_count' | 'create_time' | 'publish_time'
  sortDirection?: 'asc' | 'desc'
}

/**
 * 商品创建/更新请求
 */
export interface ProductCreateRequest {
  title: string
  description: string
  price: number
  originalPrice?: number
  categoryId: number
  condition: 'new' | 'used' | 'like_new'
  location: string
  images: string[]
  stock?: number
  contactPhone?: string
  contactWechat?: string
}

export interface ProductUpdateRequest extends Partial<ProductCreateRequest> {
  id: number
}

/**
 * 商品收藏
 */
export interface ProductLike {
  id: number
  userId: number
  productId: number
  createTime: string
}

/**
 * 商品评论
 */
export interface ProductComment {
  id: number
  productId: number
  userId: number
  userName?: string
  userAvatar?: string
  content: string
  rating: number
  parentId?: number
  status: number
  createTime: string
  updateTime: string
}

/**
 * 商品搜索响应
 */
export type ProductSearchResult = PageResult<Product>

/**
 * 热门商品
 */
export interface HotProduct {
  product: Product
  score: number
  rank: number
}

/**
 * 商品统计
 */
export interface ProductStats {
  totalProducts: number
  publishedProducts: number
  soldProducts: number
  totalViews: number
  totalLikes: number
  avgPrice: number
  categoryDistribution: Array<{
    categoryId: number
    categoryName: string
    count: number
    percentage: number
  }>
}