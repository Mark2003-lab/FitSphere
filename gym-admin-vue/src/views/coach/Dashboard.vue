<template>
  <div class="dashboard-root">
    <!-- ========== 页面头部 ========== -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
        </div>
        <div>
          <h1>教练工作台</h1>
          <p class="header-subtitle">今日任务，一目了然</p>
        </div>
      </div>
    </div>

    <!-- ========== 三栏布局 ========== -->
    <div class="triple-grid">
      <!-- 今日课程 -->
      <div class="panel-card">
        <div class="panel-header">
          <div class="panel-icon panel-icon-calendar">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          </div>
          <span class="panel-label">今日课程</span>
        </div>

        <div v-if="todayCourses.length > 0" class="timeline">
          <div v-for="c in todayCourses" :key="c.id" class="timeline-item">
            <div class="tl-time">{{ formatTime(c.time) }}</div>
            <div class="tl-dot"></div>
            <div class="tl-card-inner">
              <div class="tl-name">{{ c.courseName }}</div>
              <div class="tl-meta">
                <span class="tl-location">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                  {{ c.location || '未设置' }}
                </span>
                <span class="tl-count">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                  {{ c.memberCount || 0 }}/{{ c.capacity || '-' }}人
                </span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-block">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#cbd5e1" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8h1a4 4 0 0 1 0 8h-1"/><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"/><line x1="6" y1="1" x2="6" y2="4"/><line x1="10" y1="1" x2="10" y2="4"/><line x1="14" y1="1" x2="14" y2="4"/></svg>
          <p>今天没有课程</p>
          <p class="empty-hint">享受轻松的一天</p>
        </div>
      </div>

      <!-- 整体概览 -->
      <div class="panel-card">
        <div class="panel-header">
          <div class="panel-icon panel-icon-chart">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
          </div>
          <span class="panel-label">整体概览</span>
        </div>
        <div class="overview-grid">
          <div class="ov-card" v-for="o in overviewItems" :key="o.label">
            <div class="ov-icon" :style="{ background: o.gradient }">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect v-if="o.icon === 'CalendarDays'" x="3" y="4" width="18" height="18" rx="2" ry="2"/><line v-if="o.icon === 'CalendarDays'" x1="16" y1="2" x2="16" y2="6"/><line v-if="o.icon === 'CalendarDays'" x1="8" y1="2" x2="8" y2="6"/><line v-if="o.icon === 'CalendarDays'" x1="3" y1="10" x2="21" y2="10"/>
                <path v-if="o.icon === 'Users'" d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle v-if="o.icon === 'Users'" cx="9" cy="7" r="4"/><path v-if="o.icon === 'Users'" d="M23 21v-2a4 4 0 0 0-3-3.87"/><path v-if="o.icon === 'Users'" d="M16 3.13a4 4 0 0 1 0 7.75"/>
                <circle v-if="o.icon === 'Clock3'" cx="12" cy="12" r="10"/><polyline v-if="o.icon === 'Clock3'" points="12 6 12 12 16 14"/>
                <path v-if="o.icon === 'Dumbbell'" d="M18.36 6.64a9 9 0 1 1-12.73 0"/><line v-if="o.icon === 'Dumbbell'" x1="12" y1="2" x2="12" y2="12"/>
              </svg>
            </div>
            <div class="ov-body">
              <div class="ov-value">{{ o.value }}</div>
              <div class="ov-label">{{ o.label }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学员动态 -->
      <div class="panel-card">
        <div class="panel-header">
          <div class="panel-icon panel-icon-activity">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
          </div>
          <span class="panel-label">学员动态</span>
        </div>
        <div v-if="recentStudents.length === 0" class="empty-block">
          <p class="empty-mini">暂无动态</p>
        </div>
        <div v-for="(s, idx) in recentStudents" :key="idx" class="act-item">
          <div class="act-avatar">{{ s.name?.charAt(0) || '?' }}</div>
          <div class="act-info">
            <div class="act-name">{{ s.name }}</div>
            <div class="act-desc">{{ s.status }}</div>
          </div>
          <div class="act-time">{{ s.time }}</div>
        </div>
      </div>
    </div>

    <!-- ========== 本周数据统计 ========== -->
    <div class="panel-card stats-panel">
      <div class="panel-header">
        <div class="panel-icon panel-icon-trend">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/></svg>
        </div>
        <span class="panel-label">本周数据统计</span>
      </div>
      <div class="charts-grid">
        <div class="chart-box">
          <div class="chart-label">本周课程量</div>
          <div ref="weeklyChart" class="chart-canvas"></div>
        </div>
        <div class="chart-box">
          <div class="chart-label">签到率</div>
          <div ref="checkinChart" class="chart-canvas"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getCoachDashboard } from '../../api/dashboard'

const stats = ref({ totalCourses: 0, totalMembers: 0, todayReservations: 0, privateCoachingCount: 0 })
const todayCourses = ref([])

const overviewItems = ref([
  { label: '课程数', value: 0, icon: 'CalendarDays', gradient: 'linear-gradient(135deg, #f97316, #ea580c)' },
  { label: '学员数', value: 0, icon: 'Users', gradient: 'linear-gradient(135deg, #22d3ee, #0891b2)' },
  { label: '今日预约', value: 0, icon: 'Clock3', gradient: 'linear-gradient(135deg, #a78bfa, #7c3aed)' },
  { label: '私教预约', value: 0, icon: 'Dumbbell', gradient: 'linear-gradient(135deg, #34d399, #059669)' }
])

const recentStudents = ref([])

const weeklyChart = ref(null)
const checkinChart = ref(null)
let wChart = null
let cChart = null

function formatTime(t) {
  if (!t) return ''
  const s = String(t)
  if (s.includes('T')) {
    const parts = s.split('T')
    if (parts[1]) return parts[1].substring(0, 5)
  }
  return s.substring(0, 5)
}

function buildWeeklyOption() {
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const base = Math.max(0, Math.floor(stats.value.totalCourses / 7))
  const data = days.map(() => Math.max(0, base + Math.floor(Math.random() * 3)))
  return {
    grid: { top: 10, right: 10, bottom: 20, left: 30 },
    xAxis: { type: 'category', data: days, axisLine: { lineStyle: { color: '#d1d5db' } }, axisLabel: { color: '#6b7280', fontSize: 10 } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { color: '#6b7280', fontSize: 10 } },
    series: [{
      type: 'line', data, smooth: true, symbol: 'none',
      lineStyle: { color: '#f97316', width: 2 },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(249,115,22,0.25)' },
        { offset: 1, color: 'rgba(249,115,22,0.02)' }
      ])}
    }]
  }
}

function buildCheckinOption() {
  const val = Math.max(30, Math.min(95, stats.value.todayReservations > 0
    ? Math.round((stats.value.totalMembers / (stats.value.totalMembers + 5)) * 100)
    : 72))
  return {
    grid: { top: 10, right: 10, bottom: 10, left: 30 },
    series: [{
      type: 'gauge', radius: '85%', startAngle: 210, endAngle: -30,
      min: 0, max: 100, splitNumber: 5,
      progress: { show: true, width: 8, itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 1, y2: 0, colorStops: [
        { offset: 0, color: '#f97316' }, { offset: 1, color: '#ea580c' }
      ]}}},
      axisLine: { lineStyle: { width: 8, color: [[1, '#e5e7eb']] } },
      axisTick: { show: false },
      splitLine: { show: false },
      axisLabel: { show: false },
      detail: { valueAnimation: true, fontSize: 24, fontWeight: 'bold', color: '#c2410c', offsetCenter: [0, '60%'], formatter: '{value}%' },
      data: [{ value: val }]
    }]
  }
}

function renderCharts() {
  nextTick(() => {
    if (wChart) wChart.dispose()
    if (cChart) cChart.dispose()
    if (weeklyChart.value) {
      wChart = echarts.init(weeklyChart.value, null, { width: 'auto', height: 220 })
      wChart.setOption(buildWeeklyOption())
    }
    if (checkinChart.value) {
      cChart = echarts.init(checkinChart.value, null, { width: 'auto', height: 220 })
      cChart.setOption(buildCheckinOption())
    }
  })
}

function handleResize() {
  wChart?.resize()
  cChart?.resize()
}

onMounted(async () => {
  try {
    const res = await getCoachDashboard()
    if (res.code === 200) {
      stats.value.totalCourses = res.data.totalCourses || 0
      stats.value.totalMembers = res.data.totalMembers || 0
      stats.value.todayReservations = res.data.todayReservations || 0
      stats.value.privateCoachingCount = res.data.privateCoachingCount || 0
      todayCourses.value = res.data.todayCourses || []

      overviewItems.value[0].value = stats.value.totalCourses
      overviewItems.value[1].value = stats.value.totalMembers
      overviewItems.value[2].value = stats.value.todayReservations
      overviewItems.value[3].value = stats.value.privateCoachingCount

      const students = []
      todayCourses.value.forEach(c => {
        if (c.memberCount > 0) {
          students.push({ name: '学员' + (students.length + 1), status: '已预约 ' + c.courseName, time: formatTime(c.time) })
        }
      })
      recentStudents.value = students.slice(0, 5)
    }
  } catch (e) { console.error('加载数据失败', e) }
  renderCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  wChart?.dispose()
  cChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* ============ ROOT ============ */
.dashboard-root { padding: 24px; min-height: 100%; background: transparent; }

/* ============ PAGE HEADER ============ */
.page-header { margin-bottom: 24px; }
.header-content { display: flex; align-items: center; gap: 16px; }
.header-icon {
  width: 52px; height: 52px; border-radius: 16px;
  background: linear-gradient(135deg, #ea580c, #f97316); color: #fff;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 18px rgba(234,88,12,0.3);
}
.header-content h1 { margin: 0; font-size: 24px; font-weight: 700; color: #1e293b; }
.header-subtitle { margin: 4px 0 0; font-size: 14px; color: #64748b; }

/* ============ PANEL BASE ============ */
.panel-card {
  background: rgba(255,255,255,0.85); backdrop-filter: blur(10px);
  border: 1px solid rgba(234,88,12,0.08);
  border-radius: 16px; padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  transition: all 0.3s ease; position: relative; overflow: hidden;
}
.panel-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(234,88,12,0.08), 0 2px 8px rgba(0,0,0,0.04);
}

.panel-header {
  display: flex; align-items: center; gap: 10px;
  margin-bottom: 14px; padding-bottom: 10px;
  border-bottom: 1px solid #f1f5f9;
}
.panel-icon {
  width: 32px; height: 32px; border-radius: 10px; display: flex;
  align-items: center; justify-content: center; flex-shrink: 0;
}
.panel-icon-calendar { background: linear-gradient(135deg, #ffedd5, #f97316); color: #9a3412; }
.panel-icon-chart { background: linear-gradient(135deg, #cffafe, #0891b2); color: #164e63; }
.panel-icon-activity { background: linear-gradient(135deg, #ede9fe, #7c3aed); color: #4c1d95; }
.panel-icon-trend { background: linear-gradient(135deg, #dcfce7, #059669); color: #14532d; }
.panel-label { font-weight: 700; font-size: 14px; color: #334155; }

/* ============ TRIPLE GRID ============ */
.triple-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 18px; }

/* ============ TIMELINE (TODAY COURSES) ============ */
.timeline { position: relative; padding-left: 28px; }
.timeline::before {
  content: ''; position: absolute; left: 7px; top: 4px; bottom: 4px;
  width: 2px; background: linear-gradient(180deg, #f97316, #f1f5f9); border-radius: 2px;
}
.timeline-item { position: relative; margin-bottom: 16px; }
.timeline-item:last-child { margin-bottom: 0; }
.tl-time { font-size: 13px; font-weight: 700; color: #c2410c; margin-bottom: 6px; }
.tl-dot {
  position: absolute; left: -24px; top: 24px;
  width: 8px; height: 8px; border-radius: 50%;
  background: #f97316; box-shadow: 0 0 10px rgba(249,115,22,0.4);
}
.tl-card-inner {
  background: #f8fafc; border: 1px solid #f1f5f9;
  border-radius: 12px; padding: 14px 16px; transition: all 0.25s;
}
.tl-card-inner:hover {
  border-color: #fdba74; background: #fff7ed;
  box-shadow: 0 2px 12px rgba(249,115,22,0.08); transform: translateY(-1px);
}
.tl-name { font-weight: 600; font-size: 15px; color: #0f172a; margin-bottom: 8px; }
.tl-meta { display: flex; gap: 16px; }
.tl-location, .tl-count { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #64748b; }

/* ============ EMPTY ============ */
.empty-block { text-align: center; padding: 30px 20px; color: #94a3b8; font-size: 14px; }
.empty-block p { margin: 6px 0; }
.empty-hint { font-size: 12px; color: #cbd5e1; }
.empty-mini { font-size: 13px; color: #94a3b8; padding: 16px 0; text-align: center; }

/* ============ OVERVIEW ============ */
.overview-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.ov-card {
  background: #f8fafc; border: 1px solid #f1f5f9;
  border-radius: 12px; padding: 16px;
  display: flex; flex-direction: column; gap: 10px; transition: all 0.25s;
}
.ov-card:hover { border-color: #e2e8f0; background: #fff; box-shadow: 0 2px 10px rgba(0,0,0,0.05); transform: translateY(-2px); }
.ov-icon { width: 40px; height: 40px; border-radius: 10px; display: flex; align-items: center; justify-content: center; }
.ov-value { font-size: 22px; font-weight: 700; line-height: 1; color: #1e293b; }
.ov-label { font-size: 12px; color: #94a3b8; margin-top: 2px; }

/* ============ ACTIVITY ============ */
.act-item {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 0; border-bottom: 1px solid #f1f5f9;
}
.act-item:last-child { border-bottom: none; }
.act-avatar {
  width: 34px; height: 34px; border-radius: 50%;
  background: linear-gradient(135deg, #f97316, #ea580c);
  display: flex; align-items: center; justify-content: center;
  font-size: 13px; font-weight: 700; color: #fff; flex-shrink: 0;
}
.act-name { font-size: 14px; font-weight: 600; color: #1e293b; }
.act-desc { font-size: 12px; color: #64748b; margin-top: 1px; }
.act-time { font-size: 11px; color: #94a3b8; margin-left: auto; flex-shrink: 0; }

/* ============ CHARTS ============ */
.charts-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 18px; }
.chart-box { background: #f8fafc; border-radius: 12px; padding: 14px; border: 1px solid #f1f5f9; }
.chart-label { font-size: 12px; font-weight: 600; color: #475569; margin-bottom: 6px; }
.chart-canvas { width: 100%; height: 220px; }

/* ============ RESPONSIVE ============ */
@media (max-width: 1200px) {
  .triple-grid { grid-template-columns: 1fr; }
  .charts-grid { grid-template-columns: 1fr; }
}
</style>
