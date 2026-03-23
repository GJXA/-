import { defineStore } from 'pinia'
import type { Product } from '@/types/product'

// 购物车商品项
export interface CartItem {
  productId: number
  quantity: number
  product: Product // 商品详情（可缓存）
  selected: boolean // 是否选中
  addedAt: string // 加入时间
}

// 购物车状态
interface CartState {
  items: CartItem[]
}

export const useCartStore = defineStore('cart', {
  state: (): CartState => ({
    items: JSON.parse(localStorage.getItem('cart') || '[]')
  }),

  getters: {
    // 获取购物车商品总数
    totalItems: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),

    // 获取选中的商品
    selectedItems: (state) => state.items.filter(item => item.selected),

    // 计算总价
    totalPrice: (state) => state.items
      .filter(item => item.selected)
      .reduce((sum, item) => sum + (item.product.price * item.quantity), 0),

    // 是否为空
    isEmpty: (state) => state.items.length === 0,

    // 获取商品数量
    getItemQuantity: (state) => (productId: number) => {
      const item = state.items.find(item => item.productId === productId)
      return item ? item.quantity : 0
    },

    // 是否在购物车中
    isInCart: (state) => (productId: number) => {
      return state.items.some(item => item.productId === productId)
    }
  },

  actions: {
    // 保存到本地存储
    saveToLocalStorage() {
      localStorage.setItem('cart', JSON.stringify(this.items))
    },

    // 添加商品到购物车
    addItem(product: Product, quantity: number = 1) {
      const existingItem = this.items.find(item => item.productId === product.id)

      if (existingItem) {
        // 如果已存在，增加数量
        existingItem.quantity += quantity
      } else {
        // 新商品
        this.items.push({
          productId: product.id,
          quantity,
          product,
          selected: true,
          addedAt: new Date().toISOString()
        })
      }

      this.saveToLocalStorage()
    },

    // 更新商品数量
    updateQuantity(productId: number, quantity: number) {
      const item = this.items.find(item => item.productId === productId)
      if (item) {
        if (quantity <= 0) {
          // 数量为0则移除
          this.removeItem(productId)
        } else {
          item.quantity = quantity
          this.saveToLocalStorage()
        }
      }
    },

    // 移除商品
    removeItem(productId: number) {
      const index = this.items.findIndex(item => item.productId === productId)
      if (index !== -1) {
        this.items.splice(index, 1)
        this.saveToLocalStorage()
      }
    },

    // 切换选中状态
    toggleSelection(productId: number) {
      const item = this.items.find(item => item.productId === productId)
      if (item) {
        item.selected = !item.selected
        this.saveToLocalStorage()
      }
    },

    // 全选/全不选
    toggleSelectAll(selected: boolean) {
      this.items.forEach(item => {
        item.selected = selected
      })
      this.saveToLocalStorage()
    },

    // 清空购物车
    clearCart() {
      this.items = []
      localStorage.removeItem('cart')
    },

    // 清空选中的商品
    clearSelected() {
      this.items = this.items.filter(item => !item.selected)
      this.saveToLocalStorage()
    },

    // 获取购物车商品（用于结算）
    getItemsForCheckout(): CartItem[] {
      return this.items.filter(item => item.selected)
    }
  }
})