<template>
  <div class="member-reservation">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <h1>我的预约</h1>
          <p class="header-subtitle">管理您的健身课程预约</p>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-value">{{ upcomingReservations.length }}</span>
            <span class="stat-label">待上课</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ pastReservations.length }}</span>
            <span class="stat-label">已完成</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 原生 Tab 切换 -->
    <div class="native-tabs">
      <button
        class="native-tab"
        :class="{ active: activeTab === 'upcoming' }"
        @click="activeTab = 'upcoming'"
      >
        <span class="tab-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
        </span>
        <span class="tab-text">待上课</span>
        <span v-if="upcomingReservations.length > 0" class="tab-badge">{{ upcomingReservations.length }}</span>
      </button>
      <button
        class="native-tab"
        :class="{ active: activeTab === 'past' }"
        @click="activeTab = 'past'"
      >
        <span class="tab-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 8 14"/></svg>
        </span>
        <span class="tab-text">已结束</span>
        <span v-if="pastReservations.length > 0" class="tab-badge">{{ pastReservations.length }}</span>
      </button>
    </div>

    <!-- 预约卡片列表 -->
    <div class="reservation-grid">
      <!-- Loading -->
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <!-- 待上课卡片 -->
      <template v-if="activeTab === 'upcoming'">
        <div
          v-for="item in upcomingReservations"
          :key="item.id"
          class="reservation-card"
          :class="{ 'card-highlight': item.status === 'APPROVED' && item.paymentStatus === 'PAID' }"
        >
          <div class="card-header">
            <div class="course-info">
              <div class="course-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><diamond/></svg>
              </div>
              <div class="course-detail">
                <h3 class="course-name">{{ item.courseName }}</h3>
                <p class="course-coach">教练: {{ item.coachName }}</p>
              </div>
            </div>
            <span class="status-badge" :class="'status-' + item.status">
              {{ getStatusText(item.status) }}
            </span>
          </div>

          <div class="card-body">
            <div class="time-section">
              <div class="time-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><calendar/></svg>
              </div>
              <div class="time-info">
                <span class="time-date">{{ formatDate(item.courseTime) }}</span>
                <span class="time-separator">·</span>
                <span class="time-hour">{{ formatTime(item.courseTime) }}</span>
              </div>
            </div>

            <div class="capacity-section" v-if="item.capacity">
              <span class="capacity-label">课程容量:</span>
              <span class="capacity-value">{{ item.currentReservations || 0 }}/{{ item.capacity }}</span>
              <span class="capacity-label">(已预约/总容量)</span>
            </div>

            <div class="payment-section" v-if="item.paymentStatus">
              <span class="payment-badge" :class="'pay-' + item.paymentStatus">
                {{ getPaymentStatusText(item.paymentStatus) }}
              </span>
              <div 
                v-if="item.status === 'APPROVED' && item.paymentStatus === 'UNPAID'" 
                class="countdown-section"
                :class="{ 'countdown-expired': isPaymentExpired(item) }"
              >
                <span class="countdown-label">支付剩余时间:</span>
                <span class="countdown-time">{{ formatRemainingTime(getRemainingPaymentSeconds(item)) }}</span>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <div class="action-btns">
              <button
                v-if="item.status === 'APPROVED' && item.paymentStatus === 'UNPAID'"
                class="btn-pay"
                :disabled="isPaymentExpired(item)"
                @click="handlePay(item)"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><credit-card/></svg>
                <span>{{ isPaymentExpired(item) ? '已超时' : '支付' }}</span>
              </button>
              <button
                class="btn-cancel"
                :disabled="item.status === 'CANCELLED' || item.status === 'REJECTED'"
                @click="handleCancel(item)"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><x/></svg>
                <span>取消</span>
              </button>
              <button
                class="btn-delete"
                @click="handleDelete(item)"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><trash-2/></svg>
                <span>删除</span>
              </button>
            </div>
          </div>
        </div>
      </template>

      <!-- 已结束卡片 -->
      <template v-else-if="activeTab === 'past'">
        <div
          v-for="item in pastReservations"
          :key="item.id"
          class="reservation-card reservation-card-past"
        >
          <div class="card-header">
            <div class="course-info">
              <div class="course-icon course-icon-past">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><diamond/></svg>
              </div>
              <div class="course-detail">
                <h3 class="course-name">{{ item.courseName }}</h3>
                <p class="course-coach">教练: {{ item.coachName }}</p>
              </div>
            </div>
            <span class="status-badge" :class="'status-' + item.status">
              {{ getStatusText(item.status) }}
            </span>
          </div>

          <div class="card-body">
            <div class="time-section">
              <div class="time-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><calendar/></svg>
              </div>
              <div class="time-info">
                <span class="time-date">{{ formatDate(item.courseTime) }}</span>
                <span class="time-separator">·</span>
                <span class="time-hour">{{ formatTime(item.courseTime) }}</span>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <div class="action-btns">
              <button
                class="btn-delete"
                @click="handleDelete(item)"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><trash-2/></svg>
                <span>删除</span>
              </button>
            </div>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <div v-if="!loading && ((activeTab === 'upcoming' && upcomingReservations.length === 0) || (activeTab === 'past' && pastReservations.length === 0))" class="empty-state">
        <div class="empty-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        </div>
        <h3 class="empty-title">{{ activeTab === 'upcoming' ? '暂无待上课的预约' : '暂无已结束的预约' }}</h3>
        <p class="empty-hint">{{ activeTab === 'upcoming' ? '快去预约一节课程开始您的健身之旅吧！' : '完成的课程会在这里显示' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyReservations, cancelReservation, deleteReservation, payReservation } from '../../api/reservation'

const activeTab = ref('upcoming')
const reservations = ref([])
const loading = ref(false)
const currentTime = ref(new Date())
let timer = null

const PAYMENT_TIMEOUT_MINUTES = 5

onMounted(() => {
  timer = setInterval(() => {
    currentTime.value = new Date()
  }, 1000)
  loadReservations()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

function getRemainingPaymentSeconds(item) {
  if (!item.approvedTime) return 0
  const approvedTime = parseDateTime(item.approvedTime)
  if (!approvedTime) return 0
  const timeoutTime = new Date(approvedTime.getTime() + PAYMENT_TIMEOUT_MINUTES * 60 * 1000)
  const now = currentTime.value
  const remainingSeconds = Math.max(0, Math.floor((timeoutTime.getTime() - now.getTime()) / 1000))
  return remainingSeconds
}

function formatRemainingTime(seconds) {
  if (seconds <= 0) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

function isPaymentExpired(item) {
  return getRemainingPaymentSeconds(item) <= 0
}

function parseDateTime(dateTimeStr) {
  if (!dateTimeStr) return null
  try {
    let cleanStr = dateTimeStr.replace(/(周[一至日]|星期[一至日])/g, '').trim()
    let date = new Date(cleanStr)
    if (!isNaN(date.getTime())) {
      return date
    }
    const parts = cleanStr.split(' ')
    if (parts.length >= 2) {
      const dateParts = parts[0].split('-')
      const timeParts = parts[1].split(':')
      if (dateParts.length === 3 && timeParts.length >= 2) {
        date = new Date(
          parseInt(dateParts[0]),
          parseInt(dateParts[1]) - 1,
          parseInt(dateParts[2]),
          parseInt(timeParts[0]),
          parseInt(timeParts[1]),
          timeParts.length > 2 ? parseInt(timeParts[2]) : 0
        )
      }
    }
    return isNaN(date.getTime()) ? null : date
  } catch (e) {
    return null
  }
}

const upcomingReservations = computed(() => {
  return reservations.value.filter(r => {
    const courseTime = parseDateTime(r.courseTime)
    if (!courseTime) return false
    return courseTime > currentTime.value
  })
})

const pastReservations = computed(() => {
  return reservations.value.filter(r => {
    const courseTime = parseDateTime(r.courseTime)
    if (!courseTime) return false
    return courseTime <= currentTime.value
  })
})

function getStatusText(status) {
  const texts = { 
    PENDING: '待审核', 
    APPROVED: '已通过', 
    REJECTED: '已拒绝', 
    CANCELLED: '已取消',
    EXPIRED: '已失效'
  }
  return texts[status] || status
}

function getPaymentStatusText(status) {
  const texts = { UNPAID: '未支付', PAID: '已支付' }
  return texts[status] || status
}

function formatDate(dateTime) {
  if (!dateTime) return ''
  const date = new Date(dateTime.replace(/(周[一至日]|星期[一至日])/g, '').trim())
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${year}-${month}-${day} ${weekDays[date.getDay()]}`
}

function formatTime(dateTime) {
  if (!dateTime) return ''
  const date = new Date(dateTime.replace(/(周[一至日]|星期[一至日])/g, '').trim())
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

async function loadReservations() {
  loading.value = true
  try {
    const res = await getMyReservations(1, 100)
    if (res.code === 200) {
      reservations.value = res.data.records || []
    }
  } catch (error) {
    ElMessage.error('加载预约失败')
  } finally {
    loading.value = false
  }
}

async function handleCancel(row) {
  try {
    const res = await cancelReservation(row.id)
    if (res.code === 200) {
      ElMessage.success('取消成功')
      loadReservations()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '取消失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这条预约记录吗？删除后不可恢复。', '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteReservation(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadReservations()
    }
  } catch (error) {
    if (error === 'cancel' || error === 'close') return
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

async function handlePay(row) {
  try {
    const res = await payReservation(row.id)
    if (res.code === 200) {
      ElMessage.success('支付成功')
      loadReservations()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '支付失败')
  }
}
</script>

<style scoped>
.member-reservation {
  padding: 24px;
  min-height: 100%;
  background: transparent;
}

.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.header-title h1 {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 6px 0;
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.header-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 20px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(8, 145, 178, 0.08);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #0891B2;
}

.stat-label {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #e2e8f0;
}

.native-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.native-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  background: #ffffff;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  position: relative;
}

.native-tab:hover {
  color: #0891B2;
  box-shadow: 0 4px 16px rgba(8, 145, 178, 0.12);
  transform: translateY(-1px);
}

.native-tab.active {
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  color: #ffffff;
  box-shadow: 0 6px 24px rgba(8, 145, 178, 0.35), 0 2px 8px rgba(6, 182, 212, 0.2);
  transform: translateY(-2px);
}

.native-tab.active .tab-badge {
  background: rgba(255, 255, 255, 0.3);
}

.tab-icon {
  display: flex;
  align-items: center;
}

.tab-text {
  position: relative;
}

.tab-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(8, 145, 178, 0.1);
  color: #0891B2;
}

.reservation-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.reservation-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(8, 145, 178, 0.08);
  border: 1px solid rgba(8, 145, 178, 0.08);
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.reservation-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #0891B2, #06B6D4, #22d3ee);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.reservation-card:hover {
  box-shadow: 0 8px 32px rgba(8, 145, 178, 0.16);
  transform: translateY(-3px);
}

.reservation-card:hover::before {
  opacity: 1;
}

.reservation-card.card-highlight {
  border-color: rgba(8, 145, 178, 0.2);
  box-shadow: 0 4px 24px rgba(8, 145, 178, 0.14);
}

.reservation-card-past {
  opacity: 0.85;
}

.reservation-card-past .course-icon {
  opacity: 0.6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.course-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.course-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(8, 145, 178, 0.3);
}

.course-icon-past {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
  box-shadow: none;
}

.course-detail {
  display: flex;
  flex-direction: column;
}

.course-name {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 4px 0;
}

.course-coach {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.card-body {
  margin-bottom: 16px;
}

.time-section {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: linear-gradient(135deg, #f0fdfa 0%, #ecfeff 100%);
  border-radius: 12px;
}

.time-icon {
  color: #0891B2;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time-date {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.time-separator {
  color: #94a3b8;
}

.time-hour {
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  font-size: 14px;
  font-weight: 600;
  color: #0891B2;
}

.payment-section {
  margin-top: 12px;
}

.capacity-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding: 6px 10px;
  background: linear-gradient(135deg, #ecfeff 0%, #f0fdfa 100%);
  border-radius: 6px;
  border: 1px solid #a5f3fc;
}

.capacity-label {
  font-size: 12px;
  color: #0e7490;
  font-weight: 500;
}

.capacity-value {
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  font-size: 13px;
  font-weight: 600;
  color: #0891B2;
}

.countdown-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 8px;
  border: 1px solid #fcd34d;
}

.countdown-section.countdown-expired {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border-color: #fca5a5;
}

.countdown-label {
  font-size: 12px;
  color: #92400e;
  font-weight: 500;
}

.countdown-expired .countdown-label {
  color: #991b1b;
}

.countdown-time {
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  font-size: 16px;
  font-weight: 700;
  color: #b45309;
  letter-spacing: 1px;
}

.countdown-expired .countdown-time {
  color: #dc2626;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-badge, .payment-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.01em;
  white-space: nowrap;
}

.status-PENDING {
  background: #fef3c7;
  color: #d97706;
  border: 1px solid #fde68a;
}
.status-APPROVED {
  background: #d1fae5;
  color: #059669;
  border: 1px solid #a7f3d0;
}
.status-REJECTED {
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fecaca;
}
.status-CANCELLED {
  background: #f3f4f6;
  color: #6b7280;
  border: 1px solid #e5e7eb;
}
.status-EXPIRED {
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.pay-PAID {
  background: #d1fae5;
  color: #059669;
  border: 1px solid #a7f3d0;
}
.pay-UNPAID {
  background: #fef3c7;
  color: #d97706;
  border: 1px solid #fde68a;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.btn-pay, .btn-cancel, .btn-delete {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 16px;
  border-radius: 10px;
  border: none;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
  white-space: nowrap;
}

.btn-pay {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}
.btn-pay:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  color: #fff;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.4);
  transform: translateY(-1px);
}

.btn-cancel {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #d97706;
}
.btn-cancel:hover:not(:disabled) {
  background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
  color: #fff;
  box-shadow: 0 4px 14px rgba(217, 119, 6, 0.4);
  transform: translateY(-1px);
}
.btn-cancel:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-delete {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #dc2626;
}
.btn-delete:hover {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: #fff;
  box-shadow: 0 4px 14px rgba(220, 38, 38, 0.4);
  transform: translateY(-1px);
}

.loading-mask {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  z-index: 10;
  border-radius: 20px;
  color: #0891B2;
  font-size: 14px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #a5f3fc;
  border-top-color: #0891B2;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  color: #22d3ee;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #334155;
  margin: 0 0 8px 0;
}

.empty-hint {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}
</style>