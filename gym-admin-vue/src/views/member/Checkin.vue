<template>
  <div class="member-checkin">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <div class="header-text">
          <h1>签到打卡</h1>
          <p class="header-subtitle">记录您的每一次努力</p>
        </div>
      </div>
      <div class="header-date">
        <span class="date-day">{{ currentDate.day }}</span>
        <span class="date-info">{{ currentDate.month }}月{{ currentDate.date }}日</span>
      </div>
    </div>

    <!-- 签到操作卡片 -->
    <div class="checkin-action-card" :class="{ 'checked-in': checkedInToday }">
      <div class="action-card-bg"></div>
      <div class="action-content">
        <div class="action-icon">
          <svg v-if="checkedInToday" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"/>
            <polyline points="16 10 10 16 8 14"/>
          </svg>
          <svg v-else width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <div class="action-info">
          <h2 class="action-title">{{ checkedInToday ? '今日已签到' : '到店签到' }}</h2>
          <p class="action-desc">{{ checkedInToday ? '坚持就是胜利，明天继续加油！' : '到店后点击签到，记录您的到店时间' }}</p>
        </div>
        <button
          class="btn-checkin"
          :class="{ 'btn-disabled': checkedInToday }"
          :disabled="checkedInToday || submitting"
          :loading="submitting"
          @click="handleCheckin"
        >
          <svg v-if="!checkedInToday" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
          <span>{{ checkedInToday ? '已完成' : '立即签到' }}</span>
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon blue">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
            <line x1="16" y1="2" x2="16" y2="6"/>
            <line x1="8" y1="2" x2="8" y2="6"/>
            <line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ weekCheckins }}</div>
          <div class="stat-label">本周签到</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon purple">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
            <line x1="16" y1="2" x2="16" y2="6"/>
            <line x1="8" y1="2" x2="8" y2="6"/>
            <line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ monthCheckins }}</div>
          <div class="stat-label">本月签到</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon green">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-label">累计签到</div>
        </div>
      </div>
    </div>

    <!-- 课程签到区域 -->
    <div v-if="availableCourses.length > 0" class="course-section">
      <div class="section-header">
        <div class="section-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
            <line x1="16" y1="17" x2="8" y2="17"/>
            <polyline points="10 9 9 9 8 9"/>
          </svg>
        </div>
        <h3>课程签到</h3>
      </div>
      
      <div class="course-checkin-list">
        <div
          v-for="course in availableCourses"
          :key="course.id"
          class="course-checkin-item"
          :class="{ 'checked': hasCheckedIn(course.id), 'can-checkin': canCheckin(course) && !hasCheckedIn(course.id) }"
        >
          <div class="course-header">
            <div class="course-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M6.5 6.5h11M6.5 17.5h11M6.5 6.5v11M17.5 6.5v11M2 12h20M2 12l5-5M2 12l5 5M22 12l-5-5M22 12l-5 5"/>
              </svg>
            </div>
            <div class="course-info">
              <h4 class="course-name">{{ course.courseName }}</h4>
              <p class="course-time">{{ formatDateTime(course.courseTime) }}</p>
            </div>
            <span class="course-status" :class="getStatusClass(course)">
              {{ getCourseCheckinStatus(course).text }}
            </span>
          </div>
          
          <div class="course-footer">
            <div class="checkin-time-info">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
              </svg>
              <span>{{ course.checkinStartTime ? `${formatTime(course.checkinStartTime)} - ${formatTime(course.checkinEndTime)}` : '-' }}</span>
            </div>
            <button
              class="btn-course-checkin"
              :class="{ 'btn-disabled': !canCheckin(course) || hasCheckedIn(course.id) }"
              :disabled="!canCheckin(course) || hasCheckedIn(course.id)"
              @click="handleCourseCheckin(course)"
            >
              {{ hasCheckedIn(course.id) ? '已签到' : (canCheckin(course) ? '立即签到' : '未开始') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 签到记录 -->
    <div class="history-section">
      <div class="section-header">
        <div class="section-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
            <line x1="16" y1="2" x2="16" y2="6"/>
            <line x1="8" y1="2" x2="8" y2="6"/>
            <line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
        </div>
        <h3>签到记录</h3>
      </div>

      <div class="history-list" v-loading="loading">
        <div
          v-for="(record, index) in records"
          :key="index"
          class="history-item"
        >
          <div class="history-icon" :class="record.type === 'COURSE' ? 'course' : 'gym'">
            <svg v-if="record.type === 'COURSE'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M6.5 6.5h11M6.5 17.5h11M6.5 6.5v11M17.5 6.5v11M2 12h20M2 12l5-5M2 12l5 5M22 12l-5-5M22 12l-5 5"/>
            </svg>
            <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
          </div>
          <div class="history-info">
            <div class="history-title">
              <span class="type-badge" :class="record.type === 'COURSE' ? 'course' : 'gym'">
                {{ record.type === 'COURSE' ? '课程' : '到店' }}
              </span>
              <span class="record-name">{{ record.courseName || '到店签到' }}</span>
            </div>
            <div class="history-time">{{ formatDateTime(record.checkinTime) }}</div>
          </div>
          <div class="history-status">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="16 10 10 16 8 14"/>
            </svg>
          </div>
        </div>

        <div v-if="!loading && records.length === 0" class="empty-history">
          <div class="empty-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
          </div>
          <p>暂无签到记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyCheckins, memberCheckin, memberCourseCheckin } from '../../api/checkin'
import { getMemberCourses } from '../../api/course'

const records = ref([])
const loading = ref(false)
const submitting = ref(false)
const availableCourses = ref([])

const currentDate = computed(() => {
  const now = new Date()
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return {
    day: days[now.getDay()],
    date: now.getDate(),
    month: now.getMonth() + 1
  }
})

const weekCheckins = computed(() => {
  const weekStart = new Date()
  weekStart.setDate(weekStart.getDate() - weekStart.getDay() + 1)
  weekStart.setHours(0, 0, 0, 0)
  return records.value.filter(item => new Date(item.checkinTime) >= weekStart).length
})

const monthCheckins = computed(() => {
  const now = new Date()
  return records.value.filter(item => {
    const date = new Date(item.checkinTime)
    return date.getFullYear() === now.getFullYear() && date.getMonth() === now.getMonth()
  }).length
})

const total = computed(() => records.value.length)

const checkedInToday = computed(() => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return records.value.some(r => {
    if (r.type !== 'GYM') return false
    const checkinDate = new Date(r.checkinTime)
    checkinDate.setHours(0, 0, 0, 0)
    return checkinDate.getTime() === today.getTime()
  })
})

async function loadRecords() {
  loading.value = true
  try {
    const res = await getMyCheckins()
    if (res.code === 200) {
      records.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载签到记录失败')
  } finally {
    loading.value = false
  }
}

async function handleCheckin() {
  if (checkedInToday.value) {
    ElMessage.warning('今天已经签到过了，无需重复签到')
    return
  }
  submitting.value = true
  try {
    await memberCheckin()
    ElMessage.success('签到成功')
    await loadRecords()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '签到失败')
  } finally {
    submitting.value = false
  }
}

async function loadMemberCourses() {
  try {
    const res = await getMemberCourses()
    if (res.code === 200) {
      availableCourses.value = res.data || []
    }
  } catch (error) {
    console.error('加载课程失败', error)
  }
}

function canCheckin(course) {
  if (!course.checkinEnabled) return false
  if (!course.checkinStartTime || !course.checkinEndTime) return false
  const now = new Date().getTime()
  const start = new Date(course.checkinStartTime).getTime()
  const end = new Date(course.checkinEndTime).getTime()
  return now >= start && now <= end
}

function hasCheckedIn(courseId) {
  return records.value.some(r => r.courseId === courseId && r.type === 'COURSE' && r.status === 'CHECKED_IN')
}

function getCourseCheckinStatus(course) {
  if (hasCheckedIn(course.id)) {
    return { type: 'success', text: '已签到' }
  }
  if (!course.checkinEnabled) {
    return { type: 'info', text: '未发布' }
  }
  if (canCheckin(course)) {
    return { type: 'warning', text: '签到中' }
  }
  return { type: 'info', text: '未开始' }
}

function getStatusClass(course) {
  if (hasCheckedIn(course.id)) return 'status-success'
  if (!course.checkinEnabled) return 'status-info'
  if (canCheckin(course)) return 'status-warning'
  return 'status-info'
}

async function handleCourseCheckin(course) {
  submitting.value = true
  try {
    await memberCourseCheckin(course.id)
    ElMessage.success('课程签到成功')
    await loadRecords()
    await loadMemberCourses()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || '课程签到失败')
  } finally {
    submitting.value = false
  }
}

function formatDateTime(value) {
  if (!value) return ''
  const date = new Date(value)
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function formatTime(value) {
  if (!value) return ''
  const date = new Date(value)
  return `${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function pad(value) {
  return String(value).padStart(2, '0')
}

let refreshTimer = null

onMounted(() => {
  loadRecords()
  loadMemberCourses()
  refreshTimer = setInterval(() => {
    loadRecords()
    loadMemberCourses()
  }, 10000)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})
</script>

<style scoped>
/* ========== ROOT ========== */
.member-checkin {
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

.header-content {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  box-shadow: 0 4px 16px rgba(8, 145, 178, 0.3);
}

.header-text h1 {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 4px 0;
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

.header-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 20px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(8, 145, 178, 0.08);
}

.date-day {
  font-size: 18px;
  font-weight: 700;
  color: #0891B2;
}

.date-info {
  font-size: 12px;
  color: #64748b;
}

/* ========== CHECKIN ACTION CARD ========== */
.checkin-action-card {
  position: relative;
  background: #ffffff;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(8, 145, 178, 0.12);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.checkin-action-card:hover {
  box-shadow: 0 12px 40px rgba(8, 145, 178, 0.16);
}

.checkin-action-card.checked-in {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
}

.action-card-bg {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(8, 145, 178, 0.08) 0%, transparent 70%);
  border-radius: 50%;
}

.action-content {
  position: relative;
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.action-icon {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  box-shadow: 0 8px 24px rgba(8, 145, 178, 0.4);
  flex-shrink: 0;
}

.checkin-action-card.checked-in .action-icon {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.4);
}

.action-info {
  flex: 1;
  min-width: 200px;
}

.action-title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px 0;
}

.action-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.btn-checkin {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border-radius: 16px;
  border: none;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  color: #ffffff;
  box-shadow: 0 6px 20px rgba(8, 145, 178, 0.4);
  flex-shrink: 0;
}

.btn-checkin:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(8, 145, 178, 0.5);
}

.btn-checkin.btn-disabled {
  background: #d1d5db;
  color: #9ca3af;
  box-shadow: none;
  cursor: not-allowed;
}

/* ========== STATS GRID ========== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(8, 145, 178, 0.08);
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(8, 145, 178, 0.12);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
}

.stat-icon.blue {
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
}

.stat-icon.purple {
  background: linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%);
}

.stat-icon.green {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
}

/* ========== SECTION HEADER ========== */
.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.section-icon {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

/* ========== COURSE CHECKIN SECTION ========== */
.course-section {
  background: #ffffff;
  border-radius: 20px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(8, 145, 178, 0.08);
}

.course-checkin-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-checkin-item {
  padding: 16px;
  border-radius: 16px;
  background: #f8fafc;
  border: 2px solid transparent;
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.course-checkin-item:hover {
  background: #f1f5f9;
}

.course-checkin-item.checked {
  background: #f0fdf4;
  border-color: rgba(16, 185, 129, 0.3);
}

.course-checkin-item.can-checkin {
  border-color: rgba(8, 145, 178, 0.3);
  background: #ecfeff;
}

.course-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
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
}

.course-info {
  flex: 1;
}

.course-name {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 4px 0;
}

.course-time {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.course-status {
  padding: 5px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
}

.course-status.status-success {
  background: #d1fae5;
  color: #059669;
}

.course-status.status-warning {
  background: #fef3c7;
  color: #d97706;
}

.course-status.status-info {
  background: #f1f5f9;
  color: #64748b;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkin-time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #94a3b8;
}

.btn-course-checkin {
  padding: 8px 18px;
  border-radius: 10px;
  border: none;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
  color: #ffffff;
}

.btn-course-checkin:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 14px rgba(8, 145, 178, 0.3);
}

.btn-course-checkin.btn-disabled {
  background: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
}

/* ========== HISTORY SECTION ========== */
.history-section {
  background: #ffffff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(8, 145, 178, 0.08);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  border-radius: 14px;
  background: #f8fafc;
  transition: all 0.25s ease;
}

.history-item:hover {
  background: #f1f5f9;
}

.history-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
}

.history-icon.course {
  background: linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%);
}

.history-icon.gym {
  background: linear-gradient(135deg, #0891B2 0%, #06B6D4 100%);
}

.history-info {
  flex: 1;
}

.history-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.type-badge {
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
}

.type-badge.course {
  background: #ede9fe;
  color: #7C3AED;
}

.type-badge.gym {
  background: #d1fae5;
  color: #059669;
}

.record-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.history-time {
  font-size: 12px;
  color: #94a3b8;
}

.history-status {
  color: #10B981;
}

.empty-history {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  color: #94a3b8;
}

.empty-icon {
  margin-bottom: 12px;
  opacity: 0.5;
}

.empty-history p {
  margin: 0;
  font-size: 14px;
}
</style>
