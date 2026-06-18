<template>
  <div class="coupon-page">
    <!-- ========== 页面头部 ========== -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 12 20 22 4 22 4 12"/><rect x="2" y="7" width="20" height="5"/><line x1="12" y1="22" x2="12" y2="7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg>
        </div>
        <div>
          <h1>我的兑换券</h1>
          <p class="header-subtitle">私教课程兑换券管理</p>
        </div>
      </div>
    </div>

    <!-- ========== 统计概览 ========== -->
    <div class="stats-row">
      <div class="stat-card stat-available">
        <div class="stat-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 12 20 22 4 22 4 12"/><rect x="2" y="7" width="20" height="5"/><line x1="12" y1="22" x2="12" y2="7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ availableCoupons.length }}</span>
          <span class="stat-label">可用兑换券</span>
        </div>
      </div>
      <div class="stat-card stat-used">
        <div class="stat-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ usedCoupons.length }}</span>
          <span class="stat-label">已使用</span>
        </div>
      </div>
    </div>

    <!-- ========== 可用兑换券 ========== -->
    <div class="section">
      <div class="section-header">
        <h2>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 12 20 22 4 22 4 12"/><rect x="2" y="7" width="20" height="5"/><line x1="12" y1="22" x2="12" y2="7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg>
          可用兑换券
        </h2>
        <span class="section-count">{{ availableCoupons.length }} 张</span>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <div v-else-if="availableCoupons.length === 0" class="empty-state">
        <svg width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 12 20 22 4 22 4 12"/><rect x="2" y="7" width="20" height="5"/><line x1="12" y1="22" x2="12" y2="7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg>
        <p class="empty-title">暂无可用兑换券</p>
        <p class="empty-hint">购买指定健身卡可获得私教兑换券</p>
      </div>

      <div v-else class="coupon-grid">
        <div v-for="coupon in availableCoupons" :key="coupon.id" class="coupon-card">
          <div class="coupon-left">
            <div class="coupon-icon-wrap">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 12 20 22 4 22 4 12"/><rect x="2" y="7" width="20" height="5"/><line x1="12" y1="22" x2="12" y2="7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg>
            </div>
            <div class="coupon-core">
              <span class="coupon-code">{{ coupon.couponCode }}</span>
              <span class="coupon-type">私教兑换券</span>
            </div>
          </div>
          <div class="coupon-middle">
            <div class="coupon-date-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
              <span>有效期至 {{ formatDate(coupon.expireTime) }}</span>
            </div>
            <div class="coupon-date-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              <span>领取于 {{ formatDate(coupon.createTime) }}</span>
            </div>
          </div>
          <div class="coupon-right">
            <span class="status-badge status-available">可使用</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 已使用兑换券 ========== -->
    <div class="section">
      <div class="section-header">
        <h2>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
          已使用兑换券
        </h2>
        <span class="section-count used-count">{{ usedCoupons.length }} 张</span>
      </div>

      <div v-if="usedCoupons.length === 0" class="empty-state">
        <svg width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
        <p class="empty-title">暂无已使用的兑换券</p>
        <p class="empty-hint">去预约一位私教教练使用兑换券吧</p>
      </div>

      <div v-else class="coupon-grid">
        <div v-for="coupon in usedCoupons" :key="coupon.id" class="coupon-card used">
          <div class="coupon-left">
            <div class="coupon-icon-wrap used-icon">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            </div>
            <div class="coupon-core">
              <span class="coupon-code dimmed">{{ coupon.couponCode }}</span>
              <span class="coupon-type">私教兑换券</span>
            </div>
          </div>
          <div class="coupon-middle">
            <div class="coupon-date-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              <span>使用于 {{ formatDate(coupon.useTime) }}</span>
            </div>
            <div class="coupon-date-row">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              <span>领取于 {{ formatDate(coupon.createTime) }}</span>
            </div>
          </div>
          <div class="coupon-right">
            <span class="status-badge status-used">已使用</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserCoupons } from '../../api/coupon'

const coupons = ref([])
const loading = ref(false)

const availableCoupons = computed(() => coupons.value.filter(c => c.status === 0))

const usedCoupons = computed(() => coupons.value.filter(c => c.status === 1))

function formatDate(dateStr) {
  if (!dateStr) return '-'
  if (dateStr.includes('T')) {
    return dateStr.replace('T', ' ').substring(0, 19)
  }
  return dateStr
}

async function loadCoupons() {
  loading.value = true
  try {
    const res = await getUserCoupons()
    if (res.code === 200) {
      coupons.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载兑换券失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadCoupons())
</script>

<style scoped>
/* ========== ROOT ========== */
.coupon-page { padding: 24px; min-height: 100%; background: transparent; }

/* ========== PAGE HEADER ========== */
.page-header { margin-bottom: 24px; }
.header-content { display: flex; align-items: center; gap: 16px; }
.header-icon {
  width: 52px; height: 52px; border-radius: 16px;
  background: linear-gradient(135deg, #0891b2, #06b6d4); color: #fff;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 18px rgba(8,145,178,0.25);
}
.header-content h1 { margin: 0; font-size: 24px; font-weight: 700; color: #1e293b; }
.header-subtitle { margin: 4px 0 0; font-size: 14px; color: #64748b; }

/* ========== STATS ROW ========== */
.stats-row { display: flex; gap: 16px; margin-bottom: 24px; }
.stat-card {
  flex: 1; display: flex; align-items: center; gap: 14px; padding: 18px 22px;
  background: rgba(255,255,255,0.85); backdrop-filter: blur(10px);
  border-radius: 16px; border: 1px solid rgba(8,145,178,0.08);
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}
.stat-icon {
  width: 44px; height: 44px; border-radius: 14px; display: flex;
  align-items: center; justify-content: center; flex-shrink: 0;
}
.stat-available .stat-icon {
  background: linear-gradient(135deg, #cffafe, #22d3ee); color: #0e7490;
}
.stat-used .stat-icon {
  background: linear-gradient(135deg, #dcfce7, #4ade80); color: #166534;
}
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 26px; font-weight: 800; color: #1e293b; line-height: 1.1; }
.stat-label { font-size: 13px; color: #94a3b8; margin-top: 2px; }

/* ========== SECTION ========== */
.section { margin-bottom: 24px; }
.section-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 16px;
}
.section-header h2 {
  margin: 0; font-size: 16px; font-weight: 700; color: #1e293b;
  display: flex; align-items: center; gap: 8px;
}
.section-header h2 svg { color: #0891b2; }
.section-count {
  font-size: 13px; font-weight: 600; color: #0891b2;
  background: #ecfeff; padding: 4px 14px; border-radius: 20px;
}
.used-count { color: #16a34a; background: #dcfce7; }

/* ========== LOADING & EMPTY ========== */
.loading-state {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60px; gap: 12px; color: #0891b2; font-size: 14px;
}
.spinner { width: 36px; height: 36px; border: 3px solid #cffafe; border-top-color: #0891b2; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 48px 24px; gap: 10px; }
.empty-title { font-size: 15px; color: #94a3b8; margin: 0; }
.empty-hint { font-size: 13px; color: #cbd5e1; margin: 0; }

/* ========== COUPON CARDS ========== */
.coupon-grid { display: flex; flex-direction: column; gap: 12px; }

.coupon-card {
  display: flex; align-items: center;
  background: rgba(255,255,255,0.85); backdrop-filter: blur(10px);
  border-radius: 16px; border: 1px solid rgba(8,145,178,0.08);
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  overflow: hidden; transition: all 0.25s ease;
}
.coupon-card:not(.used):hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(8,145,178,0.1), 0 2px 8px rgba(0,0,0,0.04);
}
.coupon-card.used { opacity: 0.75; }

/* ========== COUPON LEFT ========== */
.coupon-left {
  display: flex; align-items: center; gap: 16px; padding: 20px 24px;
  flex: 0 0 280px; border-right: 2px dashed #e2e8f0;
}
.coupon-icon-wrap {
  width: 52px; height: 52px; border-radius: 14px; display: flex;
  align-items: center; justify-content: center; flex-shrink: 0;
  background: linear-gradient(135deg, #cffafe, #06b6d4); color: #fff;
  box-shadow: 0 4px 14px rgba(8,145,178,0.18);
}
.used-icon { background: linear-gradient(135deg, #dcfce7, #4ade80); box-shadow: 0 4px 14px rgba(22,163,74,0.15); }

.coupon-core { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.coupon-code {
  font-family: 'SF Mono', 'Cascadia Code', 'Courier New', monospace;
  font-size: 17px; font-weight: 700; color: #0e7490; letter-spacing: 0.02em;
}
.dimmed { color: #94a3b8; }
.coupon-type { font-size: 12px; color: #94a3b8; }

/* ========== COUPON MIDDLE ========== */
.coupon-middle {
  flex: 1; padding: 16px 24px; display: flex; flex-direction: column; gap: 8px;
}
.coupon-date-row { display: flex; align-items: center; gap: 8px; font-size: 13px; color: #64748b; }

/* ========== COUPON RIGHT ========== */
.coupon-right { padding: 20px 24px; flex-shrink: 0; }
.status-badge {
  display: inline-block; padding: 6px 18px; border-radius: 10px;
  font-size: 13px; font-weight: 600;
}
.status-available { background: #cffafe; color: #0891b2; }
.status-used { background: #dcfce7; color: #16a34a; }
</style>