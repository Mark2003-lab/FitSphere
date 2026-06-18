<template>
  <div class="member-course">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-title">
        <h1>课程预约</h1>
        <p class="header-subtitle">浏览并预约您感兴趣的健身课程</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ total }}</span>
          <span class="stat-label">课程总数</span>
        </div>
      </div>
    </div>

    <!-- 课程卡片列表 -->
    <div class="course-grid">
      <!-- Loading -->
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <!-- 课程卡片 -->
      <div
        v-for="course in courses"
        :key="course.id"
        class="course-card"
        :class="{ 'card-disabled': course.availableSlots <= 0 || course.status !== 'AVAILABLE', [`theme-${getCourseTheme(course.courseName)}`]: true }"
      >
        <div class="card-header">
          <div class="course-icon" :class="`icon-${getCourseTheme(course.courseName)}`">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path :d="getIconPath(course.courseName)" />
          </svg>
        </div>
          <div class="course-info">
            <h3 class="course-name">{{ course.courseName }}</h3>
            <p class="course-coach">教练: {{ course.coachName }}</p>
          </div>
          <span class="course-status" :class="getStatusClass(course)">
            {{ getStatusText(course.status) }}
          </span>
        </div>

        <div class="card-body">
          <div class="info-row">
            <div class="info-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><calendar/></svg>
              <span>{{ formatDate(course.courseTime) }}</span>
            </div>
            <div class="info-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><clock/></svg>
              <span>{{ formatTime(course.courseTime) }}</span>
            </div>
          </div>

          <div class="info-row">
            <div class="info-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><map-pin/></svg>
              <span>{{ course.location }}</span>
            </div>
            <div class="info-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><users/></svg>
              <span>{{ course.currentReservations }}/{{ course.capacity }}</span>
            </div>
          </div>

          <div class="price-section">
            <span class="price-label">课程费用</span>
            <span class="price-value">¥{{ course.price || 0 }}</span>
          </div>

          <!-- 剩余名额进度条 -->
          <div class="capacity-bar">
            <div class="bar-label">
              <span>剩余名额</span>
              <span class="remaining-count">{{ course.availableSlots }} 个</span>
            </div>
            <div class="bar-track">
              <div 
                class="bar-fill" 
                :style="{ width: getCapacityPercent(course) + '%' }"
                :class="{ 'bar-low': course.availableSlots <= 3 }"
              ></div>
            </div>
          </div>
        </div>

        <div class="card-footer">
          <button
            class="btn-reserve"
            :class="{ 'btn-disabled': course.availableSlots <= 0 || course.status !== 'AVAILABLE' }"
            :disabled="course.availableSlots <= 0 || course.status !== 'AVAILABLE'"
            @click="handleReserve(course)"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><calendar-check/></svg>
            <span>{{ course.availableSlots <= 0 || course.status !== 'AVAILABLE' ? '已满' : '立即预约' }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[6, 12, 24]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadCourses"
        @size-change="loadCourses"
      />
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && courses.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><dumbbell/></svg>
      </div>
      <h3 class="empty-title">暂无课程</h3>
      <p class="empty-hint">当前没有可预约的课程，请稍后再来</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCourses } from '../../api/course'
import { createReservation } from '../../api/reservation'

const courses = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

async function loadCourses() {
  loading.value = true
  try {
    const res = await getCourses(currentPage.value, pageSize.value)
    if (res.code === 200) {
      courses.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

async function handleReserve(course) {
  // 检查课程是否过期
  const courseTime = new Date(course.courseTime)
  const now = new Date()
  
  if (courseTime < now) {
    ElMessage.warning('该课程已过期，无法预约')
    return
  }
  
  try {
    const res = await createReservation(course.id)
    if (res.code === 200) {
      ElMessage.success('预约成功，请等待管理员审核')
      loadCourses()
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message || '预约失败'
    
    // 根据后端返回的错误信息显示相应提示
    if (message.includes('预约过')) {
      ElMessage.warning('您已预约过该课程，无需再次预约')
    } else if (message.includes('过期')) {
      ElMessage.warning('该课程已过期，无法预约')
    } else {
      ElMessage.error(message)
    }
  }
}

function formatDate(dateTime) {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return `${year}-${month}-${day} ${weekDays[date.getDay()]}`
}

function formatTime(dateTime) {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

function getStatusText(status) {
  const texts = { AVAILABLE: '可预约', FULL: '已满', CANCELLED: '已取消' }
  return texts[status] || status
}

function getStatusClass(course) {
  if (course.availableSlots <= 0 || course.status !== 'AVAILABLE') {
    return 'status-unavailable'
  }
  if (course.availableSlots <= 3) {
    return 'status-low'
  }
  return 'status-available'
}

function getCapacityPercent(course) {
  if (!course.capacity) return 0
  return Math.round((course.availableSlots / course.capacity) * 100)
}

// 根据课程名称获取图标类型
function getCourseIconType(courseName) {
  if (!courseName) return 'dumbbell'
  
  const name = courseName.toLowerCase()
  if (name.includes('力量') || name.includes('器械') || name.includes('基础')) return 'dumbbell'
  if (name.includes('瑜伽') || name.includes('冥想')) return 'flower2'
  if (name.includes('hiit') || name.includes('高强度') || name.includes('燃脂')) return 'flame'
  if (name.includes('普拉提') || name.includes('核心') || name.includes('体态')) return 'activity'
  if (name.includes('动感单车') || name.includes('单车')) return 'bike'
  if (name.includes('有氧') || name.includes('跑步')) return 'heartPulse'
  if (name.includes('搏击') || name.includes('格斗')) return 'target'
  if (name.includes('舞蹈') || name.includes('舞')) return 'sparkles'
  if (name.includes('游泳')) return 'sparkles'
  if (name.includes('拉伸') || name.includes('放松')) return 'sparkles'
  return 'dumbbell'
}

// 根据课程名称获取主题颜色
function getCourseTheme(courseName) {
  if (!courseName) return 'default'
  
  const name = courseName.toLowerCase()
  if (name.includes('力量') || name.includes('器械')) return 'strength'
  if (name.includes('瑜伽') || name.includes('冥想')) return 'yoga'
  if (name.includes('hiit') || name.includes('高强度') || name.includes('燃脂')) return 'hiit'
  if (name.includes('普拉提') || name.includes('核心')) return 'pilates'
  if (name.includes('动感单车') || name.includes('单车')) return 'bike'
  if (name.includes('有氧') || name.includes('跑步')) return 'cardio'
  if (name.includes('搏击') || name.includes('格斗')) return 'combat'
  if (name.includes('舞蹈')) return 'dance'
  if (name.includes('游泳') || name.includes('拉伸') || name.includes('放松')) return 'recovery'
  return 'default'
}

// 获取图标路径
function getIconPath(courseName) {
  const iconType = getCourseIconType(courseName)
  const icons = {
    // Dumbbell - 力量训练
    dumbbell: 'M6.5 6.5h11M6.5 17.5h11M6.5 6.5v11M17.5 6.5v11M2 12h20M2 12l5-5M2 12l5 5M22 12l-5-5M22 12l-5 5',
    // Flame - HIIT 高强度
    flame: 'M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.1.2-2.2.5-3.3.3.9.8 1.6 1.5 2.3.5.5 1 1 1.5 1.5z',
    // Flower2 - 瑜伽
    flower2: 'M12 7.5a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5zM12 22a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5zM5.5 14.5a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0zM23.5 14.5a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5zM12 12a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5z',
    // Activity - 普拉提
    activity: 'M22 12h-4l-3 9L9 3l-3 9H2',
    // Bike - 动感单车
    bike: 'M18 10a4 4 0 1 0-8 0c0 2.2 1.8 4 4 4h2v2h-2v2h2v-2h2a4 4 0 0 0 4-4c0-1.1-.4-2.1-1-2.9M10 10a2 2 0 1 1 4 0 2 2 0 0 1-4 0m8 6a2 2 0 1 1 0-4 2 2 0 0 1 0 4z',
    // Target - 核心训练
    target: 'M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm0 16a6 6 0 1 1 6-6 6 6 0 0 1-6 6zm0-8a2 2 0 1 0 2 2 2 2 0 0 0-2-2z',
    // Sparkles - 拉伸恢复
    sparkles: 'M12 2l2.4 7.2h7.6l-6 4.8 2.4 7.2-6-4.8-6 4.8 2.4-7.2-6-4.8h7.6z',
    // HeartPulse - 有氧燃脂
    heartPulse: 'M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35zM9 10h2v2H9zm4 0h2v2h-2z'
  }
  return icons[iconType] || icons.dumbbell
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
/* ========== ROOT ========== */
.member-course {
  padding: 24px;
  min-height: 100%;
  background: transparent;
}

/* ========== PAGE HEADER ========== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
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

/* ========== COURSE GRID ========== */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

/* ========== COURSE CARD ========== */
.course-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(8, 145, 178, 0.08);
  border: 1px solid rgba(8, 145, 178, 0.08);
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.course-card::before {
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

.course-card:hover {
  box-shadow: 0 8px 32px rgba(8, 145, 178, 0.16);
  transform: translateY(-3px);
}

.course-card:hover::before {
  opacity: 1;
}

.course-card.card-disabled {
  opacity: 0.7;
}

/* ========== CARD HEADER ========== */
.card-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.course-icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
}

.course-icon:hover {
  transform: scale(1.08);
}

/* 图标主题颜色 - 彩色渐变 */
.course-icon.icon-default {
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
}

.course-icon.icon-strength {
  background: linear-gradient(135deg, #ff8a65 0%, #ff5722 100%);
}

.course-icon.icon-yoga {
  background: linear-gradient(135deg, #ce93d8 0%, #ab47bc 100%);
}

.course-icon.icon-hiit {
  background: linear-gradient(135deg, #ff6b6b 0%, #ef4444 100%);
}

.course-icon.icon-pilates {
  background: linear-gradient(135deg, #4dd0e1 0%, #00acc1 100%);
}

.course-icon.icon-bike {
  background: linear-gradient(135deg, #60a5fa 0%, #2563eb 100%);
}

.course-icon.icon-cardio {
  background: linear-gradient(135deg, #f472b6 0%, #ec4899 100%);
}

.course-icon.icon-combat {
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
}

.course-icon.icon-dance {
  background: linear-gradient(135deg, #fb7185 0%, #f43f5e 100%);
}

.course-icon.icon-recovery {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
}

/* 卡片顶部颜色条 */
.course-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.course-card:hover::before {
  opacity: 1;
}

/* 主题颜色条 */
.course-card.theme-default::before {
  background: linear-gradient(90deg, #0891B2, #06B6D4, #22d3ee);
}

.course-card.theme-strength::before {
  background: linear-gradient(90deg, #ff8a65, #ff5722, #ff7043);
}

.course-card.theme-yoga::before {
  background: linear-gradient(90deg, #ce93d8, #ab47bc, #ba68c8);
}

.course-card.theme-hiit::before {
  background: linear-gradient(90deg, #ff6b6b, #ef4444, #f87171);
}

.course-card.theme-pilates::before {
  background: linear-gradient(90deg, #4dd0e1, #00acc1, #26c6da);
}

.course-card.theme-bike::before {
  background: linear-gradient(90deg, #60a5fa, #2563eb, #3b82f6);
}

.course-card.theme-cardio::before {
  background: linear-gradient(90deg, #f472b6, #ec4899, #f43f5e);
}

.course-card.theme-combat::before {
  background: linear-gradient(90deg, #a78bfa, #8b5cf6, #7c3aed);
}

.course-card.theme-dance::before {
  background: linear-gradient(90deg, #fb7185, #f43f5e, #e11d48);
}

.course-card.theme-recovery::before {
  background: linear-gradient(90deg, #34d399, #10b981, #059669);
}

.course-info {
  flex: 1;
  min-width: 0;
}

.course-name {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-coach {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.course-status {
  padding: 4px 10px;
  border-radius: 16px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.course-status.status-available {
  background: #d1fae5;
  color: #059669;
}

.course-status.status-low {
  background: #fef3c7;
  color: #d97706;
}

.course-status.status-unavailable {
  background: #f3f4f6;
  color: #6b7280;
}

/* ========== CARD BODY ========== */
.card-body {
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.info-row:last-of-type {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.info-item svg {
  color: #0891B2;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 12px;
  background: linear-gradient(135deg, #f0fdfa 0%, #ecfeff 100%);
  border-radius: 12px;
  margin-bottom: 16px;
}

.price-label {
  font-size: 13px;
  color: #64748b;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #0891B2;
}

.capacity-bar {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.bar-label {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #64748b;
}

.remaining-count {
  font-weight: 600;
  color: #0891B2;
}

.bar-track {
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #0891B2 0%, #06B6D4 100%);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.bar-fill.bar-low {
  background: linear-gradient(90deg, #f59e0b 0%, #d97706 100%);
}

/* ========== CARD FOOTER ========== */
.card-footer {
  display: flex;
  justify-content: flex-end;
}

.btn-reserve {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 12px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  color: #ffffff;
  box-shadow: 0 4px 14px rgba(8, 145, 178, 0.35);
}

.btn-reserve:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(8, 145, 178, 0.45);
}

.btn-reserve.btn-disabled {
  background: #e2e8f0;
  color: #94a3b8;
  box-shadow: none;
  cursor: not-allowed;
}

/* ========== PAGINATION ========== */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.pagination-wrapper :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-wrapper :deep(.el-pager li) {
  border-radius: 8px;
  margin: 0 4px;
  min-width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-wrapper :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  color: #ffffff;
}

/* ========== LOADING ========== */
.loading-mask {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #0891B2;
  font-size: 14px;
  gap: 12px;
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

/* ========== EMPTY STATE ========== */
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
