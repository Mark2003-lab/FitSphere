<template>
  <div class="coach-schedule">
    <div class="header-section">
      <h1>可用时间管理</h1>
      <p class="header-sub">设置您的可预约时间段，方便学员安排课程</p>
    </div>

    <div class="layout-grid">
      <!-- 左侧：创建时间段 -->
      <div class="section-card form-panel">
        <div class="section-header">
          <h2>添加可用时间</h2>
        </div>
        <div class="section-body">
          <div class="form-group">
            <label>日期 <span class="required">*</span></label>
            <input 
              type="date" 
              v-model="form.availableDate" 
              class="form-input"
              :min="todayStr"
            />
          </div>

          <div v-if="batchMode" class="form-group">
            <label>结束日期</label>
            <input 
              type="date" 
              v-model="batchEndDate" 
              class="form-input"
              :min="form.availableDate || todayStr"
            />
            <p class="form-hint-text">从开始日期到结束日期每天都会创建该时间段</p>
          </div>

          <div class="form-row">
            <div class="form-group flex-1">
              <label>开始时间</label>
              <input 
                type="time" 
                v-model="form.startTime" 
                class="form-input"
              />
            </div>
            <div class="form-group flex-1">
              <label>结束时间</label>
              <input 
                type="time" 
                v-model="form.endTime" 
                class="form-input"
              />
            </div>
          </div>

          <div class="form-group">
            <label>最大可预约课数</label>
            <div class="counter-group">
              <button class="counter-btn" @click="form.maxSessions > 1 && (form.maxSessions--)">−</button>
              <span class="counter-value">{{ form.maxSessions }}</span>
              <button class="counter-btn" @click="form.maxSessions < 10 && (form.maxSessions++)">+</button>
            </div>
          </div>

          <div class="form-group">
            <label class="checkbox-label" @click="batchMode = !batchMode">
              <span class="checkbox-box" :class="{ checked: batchMode }">
                <svg v-if="batchMode" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
              </span>
              <span>批量模式（按日期范围）</span>
            </label>
          </div>

          <button class="btn-submit" @click="handleSubmit" :disabled="submitting">
            <span v-if="submitting" class="btn-loading"></span>
            {{ submitting ? (batchMode ? '批量创建中...' : '创建中...') : (batchMode ? '批量创建' : '创建时间段') }}
          </button>
        </div>
      </div>

      <!-- 右侧：已有时间段列表 -->
      <div class="section-card list-panel">
        <div class="section-header">
          <h2>我的可用时间</h2>
          <button class="btn-refresh" @click="loadSchedule">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
              <path d="M3 3v5h5"/>
              <path d="M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16"/>
              <path d="M16 16h5v5"/>
            </svg>
            刷新
          </button>
        </div>

        <div class="section-body">
          <div v-if="loading" class="loading-state">
            <div class="spinner"></div>
            <span>加载中...</span>
          </div>

          <div v-else-if="schedules.length > 0" class="schedule-list">
            <div 
              v-for="row in schedules" 
              :key="row.id" 
              class="schedule-card"
              :class="{ 'schedule-full': row.bookedSessions >= row.maxSessions, 'schedule-disabled': !row.isAvailable }"
            >
              <div class="sc-date">
                <div class="sc-date-top">{{ formatDate(row.availableDate) }}</div>
                <div class="sc-date-bottom">{{ getWeekday(row.availableDate) }}</div>
              </div>
              <div class="sc-time">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                </svg>
                <span>{{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}</span>
              </div>
              <div class="sc-sessions" :class="{ danger: row.bookedSessions >= row.maxSessions }">
                <span class="sc-booked">{{ row.bookedSessions }}</span>
                <span class="sc-sep">/</span>
                <span class="sc-max">{{ row.maxSessions }}</span>
                <span class="sc-label">已约</span>
              </div>
              <div class="sc-status">
                <button 
                  class="toggle-switch" 
                  :class="{ on: row.isAvailable, off: !row.isAvailable }"
                  @click="toggleAvailability(row)"
                >
                  <span class="toggle-dot"></span>
                </button>
              </div>
              <div class="sc-actions">
                <button class="btn-delete" @click="handleDelete(row)">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 16 14"/>
            </svg>
            <h3>暂无可用时间段</h3>
            <p>请在左侧添加您的可用时间</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCoachSchedule, batchCreateCoachSchedule, getMyCoachSchedule, deleteCoachSchedule, setCoachScheduleAvailability } from '../../api/privateCoaching'

const form = ref({
  availableDate: '',
  startTime: '09:00',
  endTime: '18:00',
  maxSessions: 4
})
const batchMode = ref(false)
const batchEndDate = ref('')
const schedules = ref([])
const loading = ref(false)
const submitting = ref(false)

const todayStr = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
})

function formatTime(t) {
  if (!t) return ''
  return t.substring(0, 5)
}

function formatDate(d) {
  if (!d) return ''
  const parts = d.split('-')
  return `${parts[1]}-${parts[2]}`
}

function getWeekday(d) {
  if (!d) return ''
  const days = ['周日','周一','周二','周三','周四','周五','周六']
  const date = new Date(d)
  return days[date.getDay()]
}

async function loadSchedule() {
  loading.value = true
  try {
    const res = await getMyCoachSchedule()
    if (res.code === 200) schedules.value = res.data || []
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!form.value.availableDate) { ElMessage.warning('请选择日期'); return }
  if (!form.value.startTime || !form.value.endTime) { ElMessage.warning('请选择时间'); return }
  const startSec = timeToSeconds(form.value.startTime)
  const endSec = timeToSeconds(form.value.endTime)
  if (endSec <= startSec) { ElMessage.warning('结束时间必须晚于开始时间'); return }

  submitting.value = true
  try {
    let res
    if (batchMode.value && batchEndDate.value) {
      res = await batchCreateCoachSchedule({
        availableDate: form.value.availableDate,
        startTime: form.value.startTime + ':00',
        endTime: form.value.endTime + ':00',
        maxSessions: form.value.maxSessions
      }, batchEndDate.value)
    } else {
      res = await createCoachSchedule({
        availableDate: form.value.availableDate,
        startTime: form.value.startTime + ':00',
        endTime: form.value.endTime + ':00',
        maxSessions: form.value.maxSessions
      })
    }
    if (res.code === 200) {
      ElMessage.success('创建成功')
      loadSchedule()
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '创建失败')
  } finally {
    submitting.value = false
  }
}

function timeToSeconds(t) {
  if (!t) return 0
  const parts = t.split(':')
  return parseInt(parts[0]) * 3600 + parseInt(parts[1]) * 60 + (parseInt(parts[2]) || 0)
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除这个时间段吗？', '删除确认', { type: 'warning' })
    const res = await deleteCoachSchedule(row.id)
    if (res.code === 200) { ElMessage.success('删除成功'); loadSchedule() }
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error('删除失败')
  }
}

async function toggleAvailability(row) {
  try {
    const res = await setCoachScheduleAvailability(row.id, !row.isAvailable)
    if (res.code === 200) { ElMessage.success('更新成功'); loadSchedule() }
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

onMounted(() => loadSchedule())
</script>

<style scoped>
/* ========== GLOBAL FONTS & ANIMATIONS ========== */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.coach-schedule {
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
  margin: 0 0 4px 0;
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

/* ========== LAYOUT ========== */
.layout-grid {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 24px;
  align-items: start;
}

/* ========== SECTION CARD ========== */
.section-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}
.section-card:hover {
  box-shadow: 0 6px 30px rgba(249, 115, 22, 0.15);
  transform: translateY(-2px);
}

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

.section-body {
  padding: 24px;
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

.btn-submit {
  width: 100%;
  padding: 12px 24px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
  margin-top: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.btn-submit:hover {
  box-shadow: 0 6px 16px rgba(249, 115, 22, 0.4);
  transform: translateY(-1px);
}
.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}
.btn-submit:active:not(:disabled) {
  transform: translateY(0);
}

/* ========== FORM ========== */
.form-group {
  margin-bottom: 18px;
}
.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 6px;
}
.required {
  color: #f97316;
}
.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  color: #1e293b;
  background: #f8fafc;
  transition: all 0.2s;
  outline: none;
  box-sizing: border-box;
}
.form-input:focus {
  border-color: #f97316;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}

.form-row {
  display: flex;
  gap: 16px;
}
.flex-1 {
  flex: 1;
}
.form-hint-text {
  margin: 4px 0 0 0;
  font-size: 12px;
  color: #94a3b8;
}

/* ========== COUNTER ========== */
.counter-group {
  display: flex;
  align-items: center;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
  width: fit-content;
  background: #f8fafc;
}
.counter-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  color: #f97316;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.counter-btn:hover {
  background: #fff7ed;
}
.counter-value {
  min-width: 48px;
  text-align: center;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  padding: 0 8px;
  border-left: 1px solid #e2e8f0;
  border-right: 1px solid #e2e8f0;
}

/* ========== CHECKBOX ========== */
.checkbox-label {
  display: flex !important;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 14px !important;
  font-weight: 500 !important;
  color: #475569 !important;
}
.checkbox-box {
  width: 20px;
  height: 20px;
  border-radius: 6px;
  border: 2px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}
.checkbox-box.checked {
  background: #f97316;
  border-color: #f97316;
  color: white;
}

/* ========== LOADING ========== */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #f97316;
}
.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #fde68a;
  border-top-color: #f97316;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.btn-loading {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  vertical-align: sub;
}

/* ========== SCHEDULE LIST ========== */
.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.schedule-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}
.schedule-card:hover {
  border-color: #fde68a;
  box-shadow: 0 4px 16px rgba(249, 115, 22, 0.12);
  transform: translateY(-1px);
}
.schedule-card.schedule-full {
  border-left: 4px solid #ef4444;
}
.schedule-card.schedule-disabled {
  opacity: 0.5;
}

/* Date */
.sc-date {
  min-width: 80px;
  text-align: center;
}
.sc-date-top {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}
.sc-date-bottom {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

/* Time */
.sc-time {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 140px;
  font-size: 14px;
  color: #475569;
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
}

/* Sessions */
.sc-sessions {
  display: flex;
  align-items: baseline;
  gap: 2px;
  min-width: 80px;
}
.sc-booked {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}
.sc-sessions.danger .sc-booked {
  color: #ef4444;
}
.sc-sep {
  font-size: 14px;
  color: #94a3b8;
  margin: 0 1px;
}
.sc-max {
  font-size: 14px;
  color: #64748b;
}
.sc-label {
  font-size: 12px;
  color: #94a3b8;
  margin-left: 6px;
}

/* Status Toggle */
.sc-status {
  margin-left: auto;
}

.toggle-switch {
  position: relative;
  width: 44px;
  height: 24px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
  outline: none;
}
.toggle-switch.on {
  background: #10b981;
}
.toggle-switch.off {
  background: #e2e8f0;
}
.toggle-dot {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}
.toggle-switch.on .toggle-dot {
  left: 22px;
}

/* Actions */
.sc-actions {
  display: flex;
  gap: 4px;
}
.btn-delete {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: 1px solid #fecaca;
  background: #fef2f2;
  color: #b91c1c;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-delete:hover {
  background: #fecaca;
  border-color: #f87171;
}

/* ========== EMPTY STATE ========== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #fdba74;
}
.empty-state h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 16px 0 8px 0;
}
.empty-state p {
  font-size: 15px;
  color: #64748b;
  margin: 4px 0;
}

/* ========== RESPONSIVE ========== */
@media (max-width: 960px) {
  .layout-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .coach-schedule {
    padding: 16px;
  }
  .schedule-card {
    flex-wrap: wrap;
    gap: 10px;
  }
  .sc-status {
    margin-left: 0;
  }
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>
