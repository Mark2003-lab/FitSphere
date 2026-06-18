<template>
  <div class="coach-checkin">
    <div class="header-section">
      <h1>课程签到管理</h1>
      <p class="header-sub">管理您的课程签到与会员出勤</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card stat-course">
        <div class="stat-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ myCourses.length }}</div>
          <div class="stat-label">我的课程</div>
        </div>
      </div>
      <div class="stat-card stat-published">
        <div class="stat-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="9 11 12 14 22 4"></polyline>
            <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ publishedCount }}</div>
          <div class="stat-label">已发布签到</div>
        </div>
      </div>
      <div class="stat-card stat-active">
        <div class="stat-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ activeCount }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </div>
      <div class="stat-card stat-checkin">
        <div class="stat-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ totalCheckedIn }}/{{ totalExpected }}</div>
          <div class="stat-label">已签/应签</div>
        </div>
      </div>
    </div>

    <!-- 课程列表（带签到状态） -->
    <div class="section-card">
      <div class="section-header">
        <h2>课程签到列表</h2>
        <button class="btn-refresh" @click="loadMyCourses">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
            <path d="M3 3v5h5"></path>
            <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16"></path>
            <path d="M16 16h5v5"></path>
          </svg>
          刷新
        </button>
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th class="th-name">课程名称</th>
              <th class="th-time">上课时间</th>
              <th class="th-reserved">预约人数</th>
              <th class="th-status">签到状态</th>
              <th class="th-window">签到时间窗口</th>
              <th class="th-action">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="row in myCourses" 
              :key="row.id" 
              class="table-row"
              :class="{ 'row-selected': selectedCourse?.id === row.id }"
              @click="handleCourseSelect(row)"
            >
              <td class="td-name">{{ row.courseName }}</td>
              <td class="td-time">{{ formatDateTime(row.courseTime) }}</td>
              <td class="td-center">{{ row.currentReservations }}</td>
              <td class="td-center">
                <span v-if="!row.checkinEnabled && !row.checkinStartTime" class="status-badge status-disabled">未发布</span>
                <span v-else-if="isCheckinActive(row)" class="status-badge status-active">签到中</span>
                <span v-else-if="row.checkinEnabled && !isCheckinActive(row)" class="status-badge status-expired">已过期</span>
                <span v-else class="status-badge status-finished">已结束</span>
              </td>
              <td class="td-window">
                <template v-if="row.checkinStartTime && row.checkinEndTime">
                  <span class="time-range">
                    {{ formatDateTime(row.checkinStartTime) }} ~ {{ formatTime(row.checkinEndTime) }}
                  </span>
                </template>
                <span v-else class="time-placeholder">—</span>
              </td>
              <td class="td-center">
                <div class="action-btns">
                  <button
                    v-if="!isCheckinActive(row)"
                    class="btn-publish"
                    @click.stop="openPublishDialog(row)"
                  >
                    发布签到
                  </button>
                  <button
                    v-else
                    class="btn-stop"
                    @click.stop="handleStopCheckin(row)"
                  >
                    停止签到
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="myCourses.length === 0" class="table-row">
              <td colspan="6" class="td-empty">
                <div class="empty-state">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                  </svg>
                  <p>暂无课程数据</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 选中课程的会员签到列表 -->
    <div v-if="selectedCourse" class="section-card">
      <div class="section-header">
        <div class="course-info">
          <h2>{{ selectedCourse.courseName }}</h2>
          <div class="course-tags">
            <span v-if="isCheckinActive(selectedCourse)" class="status-badge status-active">会员可自助签到</span>
            <span v-else-if="selectedCourse.checkinStartTime" class="status-badge status-warning">仅教练代签</span>
            <span v-else class="status-badge status-disabled">未发布签到</span>
          </div>
        </div>
        <div class="course-actions">
          <span v-if="isCheckinActive(selectedCourse)" class="checkin-timer">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            签到剩余：{{ remainingMinutes }} 分钟
          </span>
          <button class="btn-refresh" @click="loadMemberCheckins">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"></path>
              <path d="M3 3v5h5"></path>
              <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16"></path>
              <path d="M16 16h5v5"></path>
            </svg>
            刷新
          </button>
        </div>
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th class="th-name">会员姓名</th>
              <th class="th-phone">手机号</th>
              <th class="th-status">预约状态</th>
              <th class="th-status">支付状态</th>
              <th class="th-status">签到状态</th>
              <th class="th-time">签到时间</th>
              <th class="th-action">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in memberCheckins" :key="row.id" class="table-row">
              <td class="td-name">{{ row.memberName }}</td>
              <td class="td-phone">{{ row.memberPhone }}</td>
              <td class="td-center">
                <span :class="'status-badge ' + (row.reservationStatus === 'APPROVED' ? 'status-approved' : 'status-pending')">
                  {{ row.reservationStatus === 'APPROVED' ? '已通过' : '待审核' }}
                </span>
              </td>
              <td class="td-center">
                <span :class="'status-badge ' + (row.paymentStatus === 'PAID' ? 'status-paid' : 'status-unpaid')">
                  {{ row.paymentStatus === 'PAID' ? '已支付' : '未支付' }}
                </span>
              </td>
              <td class="td-center">
                <span :class="'status-badge ' + (row.checkinStatus === 'CHECKED_IN' ? 'status-checked' : 'status-unchecked')">
                  {{ row.checkinStatus === 'CHECKED_IN' ? '已签到' : '未签到' }}
                </span>
              </td>
              <td class="td-time">{{ row.checkinTime ? formatDateTime(row.checkinTime) : '—' }}</td>
              <td class="td-center">
                <div class="action-btns">
                  <template v-if="row.reservationStatus === 'APPROVED'">
                    <button
                      v-if="row.checkinStatus !== 'CHECKED_IN'"
                      class="btn-proxy"
                      @click="handleProxyCheckin(row)"
                    >
                      代签
                    </button>
                    <button
                      v-else
                      class="btn-cancel"
                      @click="handleCancelCheckin(row)"
                    >
                      取消签到
                    </button>
                  </template>
                  <span v-else class="status-badge status-pending">待审核</span>
                </div>
              </td>
            </tr>
            <tr v-if="memberCheckins.length === 0" class="table-row">
              <td colspan="7" class="td-empty">
                <div class="empty-state empty-state-sm">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="9" cy="7" r="4"></circle>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                  </svg>
                  <p>暂无预约会员</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="memberCheckins.length > 0" class="checkin-summary">
        已签到 <strong>{{ memberCheckins.filter(m => m.checkinStatus === 'CHECKED_IN').length }}</strong> / {{ memberCheckins.length }} 人
      </div>
    </div>

    <div v-else class="section-card">
      <div class="empty-state">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
          <circle cx="9" cy="7" r="4"></circle>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          <path d="M8 12h6"></path>
        </svg>
        <p>请点击上方课程查看会员签到情况</p>
      </div>
    </div>

    <!-- 发布签到对话框 -->
    <div class="modal-overlay" v-if="publishDialogVisible" @click.self="publishDialogVisible = false">
      <div class="modal-card modal-form">
        <div class="modal-header">
          <h3>发布签到</h3>
          <button class="modal-close" @click="publishDialogVisible = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>课程名称</label>
            <div class="form-static">{{ publishCourse?.courseName }}</div>
          </div>
          <div class="form-group">
            <label>上课时间</label>
            <div class="form-static">{{ formatDateTime(publishCourse?.courseTime) }}</div>
          </div>
          <div class="form-group">
            <label>签到持续时长</label>
            <div class="input-with-suffix">
              <input v-model.number="checkinDuration" type="number" min="5" max="180" class="form-input form-input-sm" />
              <span class="input-suffix">分钟</span>
            </div>
          </div>
          <div class="form-hint">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
            发布后会员可在签到时间窗口内自行签到；超时后仅教练可代签。
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="publishDialogVisible = false">取消</button>
          <button class="btn-confirm" @click="confirmPublishCheckin" :disabled="publishing">
            <span v-if="publishing" class="btn-loading">处理中...</span>
            <span v-else>确认发布</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCoachCourses, publishCourseCheckin, stopCourseCheckin } from '../../api/course'
import { getCourseCheckins, coachCheckinForMember, cancelCourseCheckin } from '../../api/checkin'

const myCourses = ref([])
const selectedCourse = ref(null)
const memberCheckins = ref([])
const coursesLoading = ref(false)
const checkinsLoading = ref(false)

// 发布签到对话框
const publishDialogVisible = ref(false)
const publishCourse = ref(null)
const checkinDuration = ref(30)
const publishing = ref(false)

// 定时器，用于刷新签到剩余时间
const now = ref(Date.now())
let timer = null

// 统计
const publishedCount = computed(() => myCourses.value.filter(c => c.checkinEnabled).length)
const activeCount = computed(() => myCourses.value.filter(c => isCheckinActive(c)).length)
const totalExpected = computed(() => memberCheckins.value.length)
const totalCheckedIn = computed(() => memberCheckins.value.filter(m => m.checkinStatus === 'CHECKED_IN').length)

const remainingMinutes = computed(() => {
  if (!selectedCourse.value || !selectedCourse.value.checkinEndTime) return 0
  const end = new Date(selectedCourse.value.checkinEndTime).getTime()
  const diff = end - now.value
  return diff > 0 ? Math.ceil(diff / 60000) : 0
})

function isCheckinActive(row) {
  if (!row.checkinEnabled || !row.checkinStartTime || !row.checkinEndTime) return false
  const n = now.value
  const start = new Date(row.checkinStartTime).getTime()
  const end = new Date(row.checkinEndTime).getTime()
  return n >= start && n <= end
}

async function loadMyCourses() {
  coursesLoading.value = true
  try {
    const res = await getCoachCourses(1, 100)
    if (res.code === 200) {
      myCourses.value = res.data?.records || []
      // 如果当前已选课程，刷新其状态
      if (selectedCourse.value) {
        const updated = myCourses.value.find(c => c.id === selectedCourse.value.id)
        if (updated) selectedCourse.value = updated
      }
    }
  } catch (error) {
    ElMessage.error('加载课程列表失败')
  } finally {
    coursesLoading.value = false
  }
}

async function handleCourseSelect(row) {
  selectedCourse.value = row
  if (row) {
    await loadMemberCheckins()
  } else {
    memberCheckins.value = []
  }
}

async function loadMemberCheckins() {
  if (!selectedCourse.value) return
  checkinsLoading.value = true
  try {
    const res = await getCourseCheckins(selectedCourse.value.id)
    if (res.code === 200) {
      memberCheckins.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载会员签到数据失败')
  } finally {
    checkinsLoading.value = false
  }
}

function openPublishDialog(row) {
  publishCourse.value = row
  checkinDuration.value = 30
  publishDialogVisible.value = true
}

async function confirmPublishCheckin() {
  if (!publishCourse.value) return
  publishing.value = true
  try {
    const res = await publishCourseCheckin(publishCourse.value.id, { durationMinutes: checkinDuration.value })
    if (res.code === 200) {
      ElMessage.success('签到已发布，会员现在可以自行签到')
      publishDialogVisible.value = false
      await loadMyCourses()
      if (selectedCourse.value?.id === publishCourse.value.id) {
        await loadMemberCheckins()
      }
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发布签到失败')
  } finally {
    publishing.value = false
  }
}

async function handleStopCheckin(row) {
  try {
    await ElMessageBox.confirm(
      '停止签到后，会员将无法自行签到，仅教练可代签。确定要停止吗？',
      '确认停止签到',
      { confirmButtonText: '确定停止', cancelButtonText: '取消', type: 'warning' }
    )
    const res = await stopCourseCheckin(row.id)
    if (res.code === 200) {
      ElMessage.success('签到已停止')
      await loadMyCourses()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '停止签到失败')
    }
  }
}

async function handleProxyCheckin(row) {
  try {
    await ElMessageBox.confirm(
      `确认为会员【${row.memberName}】代签？`,
      '教练代签',
      { confirmButtonText: '确认代签', cancelButtonText: '取消', type: 'info' }
    )
    const res = await coachCheckinForMember({
      memberId: row.memberId,
      courseId: selectedCourse.value.id,
      type: 'COURSE'
    })
    if (res.code === 200) {
      ElMessage.success(`已为【${row.memberName}】签到成功`)
      await loadMemberCheckins()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || error.message || '代签失败')
    }
  }
}

async function handleCancelCheckin(row) {
  try {
    await ElMessageBox.confirm(
      `确定要取消会员【${row.memberName}】的签到吗？`,
      '取消签到',
      { confirmButtonText: '确定取消', cancelButtonText: '返回', type: 'warning' }
    )
    const res = await cancelCourseCheckin(selectedCourse.value.id, row.memberId)
    if (res.code === 200) {
      ElMessage.success(`已取消【${row.memberName}】的签到`)
      await loadMemberCheckins()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || error.message || '取消签到失败')
    }
  }
}

function formatDateTime(value) {
  if (!value) return ''
  const d = new Date(value)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function formatTime(value) {
  if (!value) return ''
  const d = new Date(value)
  return `${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function pad(v) { return String(v).padStart(2, '0') }

onMounted(() => {
  loadMyCourses()
  timer = setInterval(() => { now.value = Date.now() }, 30000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
/* ========== GLOBAL FONTS & ANIMATIONS ========== */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.coach-checkin {
  padding: 24px;
  background: linear-gradient(135deg, #fff8f1 0%, #fefefe 100%);
  min-height: 100vh;
  font-family: "Inter", "PingFang SC", "Microsoft YaHei", "SimHei", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* ========== HEADER ========== */
.header-section {
  margin-bottom: 24px;
}
.header-section h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #f97316, #ea580c);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.header-sub {
  font-size: 15px;
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

/* ========== SECTION CARD ========== */
.section-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.08);
  margin-bottom: 24px;
  overflow: hidden;
  transition: all 0.3s ease;
}
.section-card:hover {
  box-shadow: 0 6px 30px rgba(249, 115, 22, 0.15);
  transform: translateY(-2px);
}

/* ========== SECTION HEADER ========== */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(241, 245, 249, 0.6);
  background: linear-gradient(to right, rgba(249, 115, 22, 0.03), transparent);
}
.section-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}
.course-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.course-tags {
  display: flex;
  gap: 8px;
}

/* ========== BUTTONS ========== */
.btn-refresh {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 10px;
  border: 1px solid #f1f5f9;
  background: #fff;
  color: #475569;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-refresh:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #1e293b;
}
.btn-refresh:active {
  transform: translateY(1px);
}

.course-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.checkin-timer {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #fffbf0;
  border: 1px solid #fde68a;
  border-radius: 8px;
  color: #92400e;
  font-size: 13px;
  font-weight: 500;
}

/* ========== STATS GRID ========== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.1);
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  cursor: default;
}
.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.15);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-course .stat-icon { background: linear-gradient(135deg, #ffedd5, #fed7aa); color: #c2410c; }
.stat-published .stat-icon { background: linear-gradient(135deg, #bbf7d0, #86efac); color: #166534; }
.stat-active .stat-icon { background: linear-gradient(135deg, #dbeafe, #bfdbfe); color: #1e40af; }
.stat-checkin .stat-icon { background: linear-gradient(135deg, #ede9fe, #ddd6fe); color: #7c3aed; }

.stat-content {
  flex: 1;
}
.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}
.stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

/* ========== TABLE ========== */
.table-container {
  overflow-x: auto;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
}
.data-table th,
.data-table td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #f1f5f9;
}
.data-table th {
  background: #f8fafc;
  font-weight: 600;
  color: #334155;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  position: sticky;
  top: 0;
}
.data-table tbody tr:last-child td {
  border-bottom: none;
}
.data-table tbody tr:hover {
  background: #fffbeb;
}
.table-row.row-selected {
  background: #fffbeb !important;
  box-shadow: inset 0 0 0 1px #f97316;
}

.th-name { min-width: 180px; }
.th-time { min-width: 160px; }
.th-reserved { min-width: 100px; text-align: center; }
.th-status { min-width: 120px; text-align: center; }
.th-window { min-width: 220px; }
.th-phone { min-width: 140px; }
.th-time { min-width: 160px; }
.th-action { min-width: 140px; text-align: center; }

.td-name,
.td-phone { font-weight: 500; color: #1e293b; }
.td-time,
.td-window { color: #64748b; font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace; }
.td-center { text-align: center; }
.td-empty { text-align: center; padding: 40px !important; }

.time-range { font-size: 13px; }
.time-placeholder { color: #94a3b8; }

/* ========== STATUS BADGES ========== */
.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  text-transform: none;
}
.status-active { background: #fef7ee; color: #c2410c; border: 1px solid #fdba74; }
.status-disabled { background: #f1f5f9; color: #64748b; border: 1px solid #e2e8f0; }
.status-expired { background: #fefce8; color: #854d0e; border: 1px solid #f7e9a0; }
.status-finished { background: #eff6ff; color: #1e40af; border: 1px solid #bfdbfe; }
.status-approved { background: #ecfdf5; color: #047857; border: 1px solid #a7f3d0; }
.status-pending { background: #fffbeb; color: #b45309; border: 1px solid #fde68a; }
.status-paid { background: #ecfdf5; color: #047857; border: 1px solid #a7f3d0; }
.status-unpaid { background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; }
.status-checked { background: #dcfce7; color: #166534; border: 1px solid #86efac; }
.status-unchecked { background: #eff6ff; color: #1e40af; border: 1px solid #bfdbfe; }
.status-warning { background: #fffbeb; color: #b45309; border: 1px solid #fde68a; }

/* ========== ACTION BUTTONS ========== */
.action-btns {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}
.btn-publish {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid #d1fae5;
  background: #ecfdf5;
  color: #047857;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-publish:hover {
  background: #d1fae5;
  border-color: #a7f3d0;
}
.btn-stop {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid #fde68a;
  background: #fffbeb;
  color: #b45309;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-stop:hover {
  background: #fef3c7;
  border-color: #fcd34d;
}
.btn-proxy {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid #bfdbfe;
  background: #e0f2fe;
  color: #0369a1;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-proxy:hover {
  background: #bae6fd;
  border-color: #7dd3fc;
}
.btn-cancel {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid #fecaca;
  background: #fef2f2;
  color: #b91c1c;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-cancel:hover {
  background: #fecaca;
  border-color: #f87171;
}

/* ========== CHECKIN SUMMARY ========== */
.checkin-summary {
  padding: 16px 24px;
  background: #fffbeb;
  border-top: 1px solid #fde68a;
  text-align: right;
  color: #92400e;
  font-size: 14px;
  font-weight: 500;
}

/* ========== EMPTY STATE ========== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #fdba74;
  text-align: center;
}
.empty-state p { margin-top: 12px; font-size: 14px; color: #9ca3af; }
.empty-state-sm { display: flex; align-items: center; justify-content: center; padding: 30px 20px; }
.empty-state-sm p { color: #9ca3af; font-size: 14px; }

/* ========== MODAL ========== */
.modal-overlay {
  position: fixed; inset: 0; z-index: 1000;
  background: rgba(15,23,42,0.4); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-card {
  background: #fff; border-radius: 18px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15), 0 4px 16px rgba(234,88,12,0.1);
  max-height: 85vh; display: flex; flex-direction: column;
  animation: slideUp 0.25s ease;
  width: 520px;
}
@keyframes slideUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }

.modal-form { width: 520px; }
.modal-table { width: fit-content; min-width: 600px; max-width: 95vw; max-height: 92vh; }
.modal-table .modal-body { overflow-y: visible; padding: 12px 16px 16px; }
.modal-table .modal-header { padding: 12px 20px; }
.modal-table .modal-header h3 { font-size: 15px; }
.modal-table .modal-footer { padding: 10px 20px; }

.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px; border-bottom: 1px solid #f1f5f9;
}
.modal-header h3 { margin: 0; font-size: 17px; font-weight: 700; color: #1e293b; }
.modal-close {
  width: 32px; height: 32px; border-radius: 8px; border: none; background: #f1f5f9;
  color: #64748b; cursor: pointer; display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.modal-close:hover { background: #fee2e2; color: #dc2626; }

.modal-body { padding: 24px; overflow-y: auto; flex: 1; }

.modal-footer {
  display: flex; justify-content: flex-end; gap: 10px;
  padding: 16px 24px; border-top: 1px solid #f1f5f9;
}

.btn-cancel {
  padding: 8px 20px; border-radius: 10px; border: 1px solid #e2e8f0;
  background: #fff; color: #475569; font-size: 14px; font-weight: 500;
  cursor: pointer; transition: all 0.2s;
}
.btn-cancel:hover { background: #f8fafc; border-color: #cbd5e1; }

.btn-confirm {
  padding: 8px 20px; border-radius: 10px; border: none;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff; font-size: 14px; font-weight: 600;
  cursor: pointer; transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(249,115,22,0.3);
}
.btn-confirm:hover { box-shadow: 0 4px 14px rgba(249,115,22,0.45); transform: translateY(-1px); }
.btn-confirm:disabled { opacity: 0.6; cursor: not-allowed; transform: none; box-shadow: 0 2px 8px rgba(249,115,22,0.3); }

.btn-loading {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  vertical-align: sub;
  margin-right: 6px;
}
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }

/* ========== FORM ========== */
.form-group { margin-bottom: 16px; }
.form-group label {
  display: block; font-size: 13px; font-weight: 600; color: #475569;
  margin-bottom: 6px;
}
.required { color: #f97316; margin-left: 2px; }
.form-input {
  width: 100%; padding: 10px 14px; border: 1px solid #e2e8f0; border-radius: 10px;
  font-size: 14px; color: #1e293b; background: #f8fafc;
  transition: all 0.2s; outline: none; box-sizing: border-box;
}
.form-input:focus { border-color: #f97316; background: #fff; box-shadow: 0 0 0 3px rgba(249,115,22,0.1); }
.form-input::placeholder { color: #94a3b8; }
.form-input-sm { width: 100px; }

.form-row { display: flex; gap: 16px; }
.flex-1 { flex: 1; }

.input-with-suffix { display: flex; align-items: center; gap: 8px; }
.input-with-suffix .form-input { flex: 1; }
.input-suffix { font-size: 14px; color: #64748b; white-space: nowrap; }

.form-static { margin: 0; padding: 10px 14px; background: #f8fafc; border-radius: 10px; font-size: 14px; color: #334155; font-weight: 500; }
.form-hint {
  display: flex; align-items: center; gap: 8px; margin-top: 16px;
  padding: 12px 16px; background: #fff7ed; border-radius: 10px;
  font-size: 13px; color: #9a3412; border: 1px solid #fdba74;
}

/* ========== RESPONSIVE ========== */
@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .section-header { flex-direction: column; align-items: flex-start; gap: 12px; }
  .course-actions { width: 100%; justify-content: space-between; }
}
@media (max-width: 768px) {
  .coach-checkin { padding: 16px; }
  .stats-grid { grid-template-columns: 1fr; }
  .data-table, .data-table thead, .data-table tbody, .data-table th, .data-table td, .data-table tr {
    display: block;
  }
  .data-table thead tr {
    position: absolute;
    top: -9999px;
    left: -9999px;
  }
  .data-table tr {
    border: 1px solid #ccc;
    margin-bottom: 10px;
    padding: 10px;
    border-radius: 8px;
    background: white;
  }
  .data-table td {
    border: none;
    position: relative;
    padding-left: 50% !important;
    text-align: right;
  }
  .data-table td:before {
    content: attr(data-label);
    position: absolute;
    left: 16px;
    width: 45%;
    text-align: left;
    font-weight: 600;
    color: #475569;
  }
}
</style>
