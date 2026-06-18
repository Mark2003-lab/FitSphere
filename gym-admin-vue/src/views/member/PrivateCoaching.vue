<template>
  <div class="private-coaching">
    <!-- ========== 页面头部 ========== -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <div>
          <h1>私教课程</h1>
          <p class="header-subtitle">专业教练一对一指导，定制专属健身计划</p>
        </div>
      </div>
    </div>

    <!-- ========== 选项卡导航 ========== -->
    <div class="custom-tabs">
      <button class="tab-btn" :class="{ active: activeTab === 'coaches' }" @click="activeTab = 'coaches'">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        教练列表
      </button>
      <button class="tab-btn" :class="{ active: activeTab === 'bookings' }" @click="activeTab = 'bookings'">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        我的私教预约
      </button>
    </div>

    <!-- ========== 教练列表 ========== -->
    <div v-show="activeTab === 'coaches'" class="tab-content">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <div v-else-if="coaches.length > 0" class="coach-grid">
        <div v-for="coach in coaches" :key="coach.id" class="coach-card">
          <div class="card-accent"></div>
          <div class="card-body">
            <div class="coach-avatar-wrap">
              <img v-if="coach.avatar" :src="'http://localhost:8080' + coach.avatar" :alt="coach.name" class="coach-avatar-img" />
              <div v-else class="coach-avatar-fallback">
                <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </div>
            </div>
            <div class="coach-detail">
              <div class="coach-header-row">
                <h3>{{ coach.name }}</h3>
                <span class="coach-badge">资深教练</span>
              </div>
              <div class="coach-tags">
                <span class="coach-tag" v-if="coach.speciality">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                  {{ coach.speciality }}
                </span>
                <span class="coach-tag" v-if="coach.certification">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                  {{ coach.certification }}
                </span>
                <span class="coach-tag exp-tag" v-if="coach.experienceYears">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                  {{ coach.experienceYears }}年经验
                </span>
              </div>
              <div class="coach-rating-row">
                <div class="stars">
                  <svg v-for="i in 5" :key="i" width="14" height="14" viewBox="0 0 24 24" :fill="i <= Math.round(coach.rating || 5) ? '#f59e0b' : '#e2e8f0'" stroke="none"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                </div>
                <span class="review-count">{{ coach.rating || 5 }} ({{ coach.totalReviews || 0 }}条评价)</span>
              </div>
              <p class="desc" v-if="coach.description">{{ coach.description }}</p>
              <div class="coach-footer">
                <span class="price">¥<strong>{{ coach.price || 0 }}</strong>/课时</span>
                <button class="btn-book" @click="openBookingDialog(coach)">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                  立即预约
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        <p class="empty-title">暂无教练信息</p>
        <p class="empty-hint">教练信息暂未录入，请联系管理员</p>
      </div>
    </div>

    <!-- ========== 我的私教预约 ========== -->
    <div v-show="activeTab === 'bookings'" class="tab-content">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <div v-else-if="bookings.length > 0" class="table-card">
        <table class="native-table">
          <thead>
            <tr>
              <th>课程名称</th>
              <th>教练</th>
              <th>预约时间</th>
              <th class="th-center">时长</th>
              <th class="th-center">价格</th>
              <th class="th-center">支付方式</th>
              <th class="th-center">状态</th>
              <th class="th-center">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in bookings" :key="row.id" class="table-row">
              <td><span class="cell-course">{{ row.courseName }}</span></td>
              <td><span class="cell-coach">{{ row.coachName }}</span></td>
              <td><span class="cell-time">{{ formatDateTime(row.scheduledTime) }}</span></td>
              <td class="td-center"><span class="cell-mono">{{ row.duration }}分钟</span></td>
              <td class="td-center"><span class="cell-price">¥{{ row.price }}</span></td>
              <td class="td-center">
                <span class="pay-tag" :class="row.couponId ? 'tag-coupon' : (row.paymentStatus === 'PAID' ? 'tag-paid' : 'tag-unpaid')">
                  {{ row.couponId ? '兑换券' : (row.paymentStatus === 'PAID' ? '已支付' : '未支付') }}
                </span>
              </td>
              <td class="td-center">
                <span class="status-tag" :class="'status-' + getStatusType(row.status)">{{ getStatusText(row.status) }}</span>
              </td>
              <td class="td-center">
                <div class="action-btns">
                  <button v-if="['APPROVED','CONFIRMED'].includes(row.status) && row.paymentStatus === 'UNPAID' && !row.couponId"
                    class="btn-pay" @click="handlePay(row)">支付</button>
                  <button v-if="!['COMPLETED','CANCELLED','REJECTED'].includes(row.status)"
                    class="btn-cancel" @click="handleCancel(row)">取消</button>
                  <button v-if="row.status === 'COMPLETED' && !row.rating"
                    class="btn-review" @click="openReviewDialog(row)">评价</button>
                  <span v-if="row.rating" class="rating-display">
                    <svg v-for="i in 5" :key="i" width="12" height="12" viewBox="0 0 24 24" :fill="i <= row.rating ? '#f59e0b' : '#e2e8f0'" stroke="none"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                  </span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="empty-state">
        <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        <p class="empty-title">暂无预约记录</p>
        <p class="empty-hint">预约一位教练，开启您的健身之旅</p>
      </div>
    </div>

    <!-- ========== 预约对话框 ========== -->
    <el-dialog v-model="bookingDialogVisible" title="预约私教课程" width="520px">
      <el-form :model="bookingForm" label-width="90px">
        <el-form-item label="教练">
          <el-input :model-value="selectedCoach?.name" disabled />
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="bookingForm.courseName" placeholder="如：减脂训练、增肌训练" />
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="bookingForm.courseType" placeholder="请选择">
            <el-option label="减脂训练" value="FAT_LOSS" />
            <el-option label="增肌训练" value="MUSCLE_GAIN" />
            <el-option label="塑形训练" value="BODY_SHAPING" />
            <el-option label="力量训练" value="STRENGTH" />
            <el-option label="瑜伽普拉提" value="YOGA" />
            <el-option label="康复训练" value="REHAB" />
            <el-option label="综合健身" value="GENERAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker v-model="bookingForm.scheduledTime" type="datetime"
            placeholder="选择预约时间" :disabled-date="disablePastDate" />
        </el-form-item>
        <el-form-item label="时长(分)">
          <el-input-number v-model="bookingForm.duration" :min="30" :max="120" :step="15" />
        </el-form-item>
        <el-form-item label="上课地点">
          <el-input v-model="bookingForm.location" placeholder="默认：健身房私教区" />
        </el-form-item>
        <el-form-item label="需求备注">
          <el-input v-model="bookingForm.notes" type="textarea" :rows="3" placeholder="请描述您的健身需求和目标" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="paymentType">
            <el-radio :label="'normal'">正常支付</el-radio>
            <el-radio :label="'coupon'" :disabled="availableCoupons.length === 0">使用兑换券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="paymentType === 'coupon'" label="选择兑换券">
          <el-select v-model="selectedCouponId" placeholder="请选择兑换券">
            <el-option v-for="coupon in availableCoupons" :key="coupon.id" :label="`兑换券 ${coupon.couponCode} (有效期至${formatDate(coupon.expireTime)})`" :value="coupon.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用">
          <span class="price-display" :class="{ 'free': paymentType === 'coupon' }">
            {{ paymentType === 'coupon' ? '免费（兑换券抵扣）' : '¥' + currentPrice }}
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBooking" :loading="submitting">提交预约</el-button>
      </template>
    </el-dialog>

    <!-- ========== 评价对话框 ========== -->
    <el-dialog v-model="reviewDialogVisible" title="评价教练" width="420px">
      <el-form label-width="80px">
        <el-form-item label="教练">
          <span>{{ reviewingBooking?.coachName }}</span>
        </el-form-item>
        <el-form-item label="课程">
          <span>{{ reviewingBooking?.courseName }}</span>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :max="5" show-text
            :texts="['很差', '较差', '一般', '满意', '非常满意']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="reviewForm.review" type="textarea" :rows="4" placeholder="请分享您的上课体验" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="submitting">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllCoaches, createPrivateCoaching, getMyPrivateCoaching, cancelPrivateCoaching, payPrivateCoaching, reviewPrivateCoaching, createBookingWithCoupon } from '../../api/privateCoaching'
import { getMyAvailableCoupons } from '../../api/coupon'

const activeTab = ref('coaches')
const coaches = ref([])
const bookings = ref([])
const loading = ref(false)
const submitting = ref(false)

// 预约对话框
const bookingDialogVisible = ref(false)
const selectedCoach = ref(null)
const bookingForm = ref({
  courseName: '',
  courseType: 'GENERAL',
  scheduledTime: null,
  duration: 60,
  location: '健身房私教区',
  notes: ''
})

// 支付方式
const paymentType = ref('normal')
const selectedCouponId = ref(null)
const availableCoupons = ref([])

// 评价对话框
const reviewDialogVisible = ref(false)
const reviewingBooking = ref(null)
const reviewForm = ref({ rating: 5, review: '' })

// 当前选择的教练价格
const currentPrice = computed(() => {
  return selectedCoach.value?.price || 0
})

function disablePastDate(date) {
  return date.getTime() < Date.now() - 86400000
}

function getStatusType(status) {
  const map = { PENDING: 'warning', APPROVED: '', REJECTED: 'danger', CONFIRMED: 'primary', COMPLETED: 'success', CANCELLED: 'info' }
  return map[status] || 'info'
}

function getStatusText(status) {
  const map = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝', CONFIRMED: '已确认', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}

function formatDateTime(dt) {
  if (!dt) return ''
  const d = new Date(dt)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}

function formatDate(dt) {
  if (!dt) return ''
  const d = new Date(dt)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}

async function openBookingDialog(coach) {
  selectedCoach.value = coach
  bookingForm.value = {
    courseName: '',
    courseType: 'GENERAL',
    scheduledTime: null,
    duration: 60,
    location: '健身房私教区',
    notes: ''
  }
  paymentType.value = 'normal'
  selectedCouponId.value = null
  
  // 加载可用兑换券
  try {
    const res = await getMyAvailableCoupons()
    if (res.code === 200) {
      availableCoupons.value = res.data || []
    }
  } catch (e) {
    availableCoupons.value = []
  }
  
  bookingDialogVisible.value = true
}

function openReviewDialog(row) {
  reviewingBooking.value = row
  reviewForm.value = { rating: 5, review: '' }
  reviewDialogVisible.value = true
}

async function loadCoaches() {
  loading.value = true
  try {
    const res = await getAllCoaches()
    if (res.code === 200) {
      coaches.value = (res.data || []).map(c => ({ ...c, rating: c.rating ? Number(c.rating) : 5 }))
    }
  } catch (e) {
    ElMessage.error('加载教练列表失败')
  } finally {
    loading.value = false
  }
}

async function loadBookings() {
  loading.value = true
  try {
    const res = await getMyPrivateCoaching(1, 100)
    if (res.code === 200) {
      bookings.value = res.data.records || []
    }
  } catch (e) {
    ElMessage.error('加载预约记录失败')
  } finally {
    loading.value = false
  }
}

async function submitBooking() {
  if (!bookingForm.value.scheduledTime) {
    ElMessage.warning('请选择预约时间')
    return
  }
  if (!bookingForm.value.courseName) {
    ElMessage.warning('请输入课程名称')
    return
  }
  
  // 使用兑换券时必须选择兑换券
  if (paymentType.value === 'coupon' && !selectedCouponId.value) {
    ElMessage.warning('请选择兑换券')
    return
  }
  
  submitting.value = true
  try {
    let res
    const bookingData = {
      coachId: selectedCoach.value.id,
      courseName: bookingForm.value.courseName,
      courseType: bookingForm.value.courseType,
      scheduledTime: bookingForm.value.scheduledTime,
      duration: bookingForm.value.duration,
      location: bookingForm.value.location,
      notes: bookingForm.value.notes
    }
    
    if (paymentType.value === 'coupon') {
      // 使用兑换券预约
      res = await createBookingWithCoupon(bookingData, selectedCouponId.value)
    } else {
      // 正常预约
      res = await createPrivateCoaching(bookingData)
    }
    
    if (res.code === 200) {
      const msg = paymentType.value === 'coupon' ? '使用兑换券预约成功，等待管理员审核' : '预约提交成功，等待管理员审核'
      ElMessage.success(msg)
      bookingDialogVisible.value = false
      loadBookings()
    } else {
      ElMessage.error(res.message || '预约失败')
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '预约失败')
  } finally {
    submitting.value = false
  }
}

async function handleCancel(row) {
  try {
    await ElMessageBox.confirm('确定取消这个私教预约吗？', '取消确认', { type: 'warning' })
    const res = await cancelPrivateCoaching(row.id)
    if (res.code === 200) {
      ElMessage.success('取消成功')
      loadBookings()
    }
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.response?.data?.message || '取消失败')
  }
}

async function handlePay(row) {
  try {
    await ElMessageBox.confirm(`确认支付 ¥${row.price} 吗？`, '支付确认', { type: 'info' })
    const res = await payPrivateCoaching(row.id)
    if (res.code === 200) {
      ElMessage.success('支付成功')
      loadBookings()
    }
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.response?.data?.message || '支付失败')
  }
}

async function submitReview() {
  if (!reviewForm.value.rating || reviewForm.value.rating < 1) {
    ElMessage.warning('请给教练评分')
    return
  }
  submitting.value = true
  try {
    const res = await reviewPrivateCoaching(reviewingBooking.value.id, reviewForm.value.rating, reviewForm.value.review)
    if (res.code === 200) {
      ElMessage.success('评价成功')
      reviewDialogVisible.value = false
      loadBookings()
      loadCoaches()
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '评价失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCoaches()
  loadBookings()
})
</script>

<style scoped>
/* ========== ROOT ========== */
.private-coaching { padding: 24px; min-height: 100%; background: transparent; }

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

/* ========== CUSTOM TABS ========== */
.custom-tabs { display: flex; gap: 8px; margin-bottom: 24px; padding: 4px; background: rgba(255,255,255,0.6); backdrop-filter: blur(10px); border-radius: 14px; width: fit-content; }
.tab-btn {
  display: flex; align-items: center; gap: 8px; padding: 10px 20px;
  border-radius: 11px; border: none; font-size: 14px; font-weight: 500;
  color: #64748b; background: transparent; cursor: pointer; transition: all 0.25s ease;
}
.tab-btn:hover { color: #0891b2; background: rgba(8,145,178,0.06); }
.tab-btn.active { background: #fff; color: #0891b2; font-weight: 600; box-shadow: 0 2px 8px rgba(8,145,178,0.12); }

.tab-content { min-height: 200px; }

/* ========== LOADING & EMPTY ========== */
.loading-state {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60px; gap: 12px; color: #0891b2; font-size: 14px;
}
.spinner { width: 36px; height: 36px; border: 3px solid #cffafe; border-top-color: #0891b2; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 48px 24px; gap: 12px; }
.empty-title { font-size: 15px; color: #94a3b8; margin: 0; }
.empty-hint { font-size: 13px; color: #cbd5e1; margin: 0; }

/* ========== COACH CARDS ========== */
.coach-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(420px, 1fr)); gap: 20px; }

.coach-card {
  background: rgba(255,255,255,0.85); backdrop-filter: blur(10px);
  border-radius: 18px; border: 1px solid rgba(8,145,178,0.08);
  box-shadow: 0 2px 12px rgba(0,0,0,0.04), 0 8px 24px rgba(8,145,178,0.06);
  overflow: hidden; transition: all 0.3s ease; cursor: default;
}
.coach-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.06), 0 12px 32px rgba(8,145,178,0.1);
}

.card-accent {
  height: 4px; background: linear-gradient(90deg, #0891b2, #22d3ee, #67e8f9);
}

.card-body { display: flex; gap: 20px; padding: 20px 24px 24px; }

/* ========== AVATAR ========== */
.coach-avatar-wrap { flex-shrink: 0; }
.coach-avatar-img {
  width: 80px; height: 80px; border-radius: 18px; object-fit: cover;
  box-shadow: 0 4px 12px rgba(8,145,178,0.15);
}
.coach-avatar-fallback {
  width: 80px; height: 80px; border-radius: 18px;
  background: linear-gradient(135deg, #cffafe, #06b6d4);
  display: flex; align-items: center; justify-content: center;
  color: #fff; box-shadow: 0 4px 12px rgba(8,145,178,0.15);
}

/* ========== COACH DETAIL ========== */
.coach-detail { flex: 1; min-width: 0; }

.coach-header-row { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.coach-header-row h3 { margin: 0; font-size: 17px; font-weight: 700; color: #1e293b; }
.coach-badge {
  padding: 2px 10px; border-radius: 20px; font-size: 11px; font-weight: 600;
  background: linear-gradient(135deg, #ecfeff, #cffafe); color: #0891b2;
  white-space: nowrap;
}

.coach-tags { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 8px; }
.coach-tag {
  display: flex; align-items: center; gap: 4px; padding: 3px 10px;
  border-radius: 8px; font-size: 12px; color: #475569;
  background: #f1f5f9; white-space: nowrap;
}
.exp-tag { background: #fef3c7; color: #92400e; }

.coach-rating-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.stars { display: flex; gap: 2px; }
.review-count { font-size: 12px; color: #94a3b8; }

.desc { color: #94a3b8; font-size: 12px; line-height: 1.6; margin: 0 0 14px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

/* ========== COACH FOOTER ========== */
.coach-footer { display: flex; align-items: center; justify-content: space-between; }
.price { font-size: 15px; color: #64748b; }
.price strong { font-size: 22px; color: #e6a23c; font-weight: 800; }

.btn-book {
  display: flex; align-items: center; gap: 6px; padding: 9px 22px;
  border-radius: 12px; border: none; font-size: 14px; font-weight: 600; cursor: pointer;
  background: linear-gradient(135deg, #0891b2, #06b6d4); color: #fff;
  box-shadow: 0 2px 10px rgba(8,145,178,0.3); transition: all 0.25s ease;
}
.btn-book:hover { box-shadow: 0 4px 16px rgba(8,145,178,0.45); transform: translateY(-1px); }

/* ========== BOOKING TABLE ========== */
.table-card {
  background: rgba(255,255,255,0.85); backdrop-filter: blur(10px);
  border-radius: 16px; border: 1px solid rgba(8,145,178,0.08);
  box-shadow: 0 2px 12px rgba(0,0,0,0.04), 0 8px 24px rgba(8,145,178,0.06);
  overflow-x: auto;
}

.native-table { width: 100%; border-collapse: collapse; font-size: 13px; }
.native-table thead {
  background: linear-gradient(135deg, #ecfeff 0%, #cffafe 40%, #f0fdfa 100%);
  border-bottom: 2px solid #22d3ee;
}
.native-table th {
  padding: 11px 14px; text-align: left; font-weight: 600; font-size: 12px;
  color: #0e7490; user-select: none; vertical-align: middle;
}
.native-table td { padding: 11px 14px; color: #374151; border-bottom: 1px solid #f1f5f9; vertical-align: middle; font-size: 13px; }
.native-table .th-center, .native-table .td-center { text-align: center; }

.table-row { transition: background 0.2s ease, box-shadow 0.2s ease; }
.table-row:hover { background: #f0fdfa; box-shadow: inset 3px 0 0 #22d3ee; }

.cell-course { font-weight: 600; color: #1e293b; }
.cell-coach { color: #475569; }
.cell-time { font-family: 'SF Mono', 'Cascadia Code', monospace; font-size: 12px; color: #6b7280; }
.cell-mono { font-family: 'SF Mono', 'Cascadia Code', monospace; font-size: 12px; color: #6b7280; }
.cell-price { font-weight: 700; color: #e6a23c; font-size: 14px; }

/* ========== PAYMENT & STATUS TAGS ========== */
.pay-tag {
  display: inline-block; padding: 3px 10px; border-radius: 8px; font-size: 12px; font-weight: 500;
}
.tag-coupon { background: #dcfce7; color: #16a34a; }
.tag-paid { background: #dbeafe; color: #2563eb; }
.tag-unpaid { background: #fef3c7; color: #92400e; }

.status-tag {
  display: inline-block; padding: 4px 12px; border-radius: 8px; font-size: 12px; font-weight: 500;
}
.status-warning { background: #fef3c7; color: #92400e; }
.status- { background: #dbeafe; color: #2563eb; }
.status-danger { background: #fee2e2; color: #dc2626; }
.status-primary { background: #cffafe; color: #0891b2; }
.status-success { background: #dcfce7; color: #16a34a; }
.status-info { background: #f1f5f9; color: #64748b; }

/* ========== ACTION BUTTONS ========== */
.action-btns { display: flex; gap: 6px; justify-content: center; flex-wrap: nowrap; }
.btn-pay, .btn-cancel, .btn-review {
  padding: 5px 14px; border-radius: 8px; border: none; font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease; white-space: nowrap;
}
.btn-pay { background: #cffafe; color: #0891b2; }
.btn-pay:hover { background: #0891b2; color: #fff; box-shadow: 0 2px 8px rgba(8,145,178,0.3); }
.btn-cancel { background: #fee2e2; color: #dc2626; }
.btn-cancel:hover { background: #dc2626; color: #fff; box-shadow: 0 2px 8px rgba(220,38,38,0.3); }
.btn-review { background: #dcfce7; color: #16a34a; }
.btn-review:hover { background: #16a34a; color: #fff; box-shadow: 0 2px 8px rgba(22,163,74,0.3); }

.rating-display { display: inline-flex; gap: 1px; align-items: center; }

/* ========== DIALOG OVERRIDES ========== */
:deep(.el-dialog) {
  border-radius: 20px !important;
  box-shadow: 0 24px 60px rgba(0,0,0,0.18), 0 8px 24px rgba(8,145,178,0.12) !important;
}
:deep(.el-dialog__header) {
  padding: 22px 28px 16px !important; border-bottom: 1px solid #f1f5f9;
}
:deep(.el-dialog__title) {
  font-size: 18px !important; font-weight: 700 !important;
  background: linear-gradient(135deg, #0891b2, #22d3ee) !important;
  -webkit-background-clip: text !important; -webkit-text-fill-color: transparent !important;
  background-clip: text !important;
}
:deep(.el-dialog__body) { padding: 20px 28px 8px !important; }
:deep(.el-dialog__footer) { padding: 16px 28px 22px !important; }
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #0891b2, #06b6d4) !important;
  border: none !important; box-shadow: 0 2px 10px rgba(8,145,178,0.3) !important;
}
:deep(.el-button--primary:hover) { box-shadow: 0 4px 16px rgba(8,145,178,0.45) !important; }

.price-display { font-size: 22px; color: #e6a23c; font-weight: bold; }
.price-display.free { color: #16a34a; }
</style>
