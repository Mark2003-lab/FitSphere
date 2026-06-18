<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">数据概览</h1>
      <el-button type="primary" size="small" @click="exportExcel" class="export-btn">
        <el-icon><Download /></el-icon>导出数据
      </el-button>
    </div>
    <el-row :gutter="12" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon member"><User /></div>
            <div class="stat-info">
              <div class="stat-value"><AnimatedNumber :value="stats.memberCount" :decimals="0" /></div>
              <div class="stat-label">会员总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon coach"><UserFilled /></div>
            <div class="stat-info">
              <div class="stat-value"><AnimatedNumber :value="stats.coachCount" :decimals="0" /></div>
              <div class="stat-label">教练总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon course"><Tickets /></div>
            <div class="stat-info">
              <div class="stat-value"><AnimatedNumber :value="stats.courseCount" :decimals="0" /></div>
              <div class="stat-label">课程总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon checkin"><Clock /></div>
            <div class="stat-info">
              <div class="stat-value"><AnimatedNumber :value="stats.todayCheckinCount" :decimals="0" /></div>
              <div class="stat-label">今日签到</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="12" class="stats-row">
      <el-col :span="24">
        <el-card class="stat-card finance-card">
          <div class="finance-header">
            <Wallet class="finance-icon" />
            <span class="finance-title">资金流水概览</span>
          </div>
          <div class="finance-content">
            <div class="finance-section">
              <div class="section-title">累计数据</div>
              <div class="finance-grid">
                <div class="finance-item">
                  <AnimatedNumber :value="financeStats.courseRevenue" prefix="¥" class="finance-value" />
                  <div class="finance-label">课程流水</div>
                </div>
                <div class="finance-item">
                  <AnimatedNumber :value="financeStats.privateCoachingRevenue" prefix="¥" class="finance-value" />
                  <div class="finance-label">私教流水</div>
                </div>
                <div class="finance-item highlight">
                  <AnimatedNumber :value="financeStats.totalRevenue" prefix="¥" class="finance-value total" />
                  <div class="finance-label">累计总收入</div>
                </div>
              </div>
            </div>
            <div class="finance-section">
              <div class="section-title">本月数据</div>
              <div class="finance-grid">
                <div class="finance-item">
                  <AnimatedNumber :value="financeStats.monthlyCourseRevenue" prefix="¥" class="finance-value" />
                  <div class="finance-label">课程流水</div>
                </div>
                <div class="finance-item">
                  <AnimatedNumber :value="financeStats.monthlyPrivateRevenue" prefix="¥" class="finance-value" />
                  <div class="finance-label">私教流水</div>
                </div>
                <div class="finance-item highlight">
                  <AnimatedNumber :value="financeStats.monthlyTotalRevenue" prefix="¥" class="finance-value total" />
                  <div class="finance-label">本月总收入</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="12" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header><span>本周签到趋势</span></template>
          <div ref="weeklyCheckinChart" class="chart-content"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header><span>课程预约统计</span></template>
          <div ref="courseReservationChart" class="chart-content"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { User, UserFilled, Tickets, Clock, Download, Wallet, TrendCharts } from '@element-plus/icons-vue'
import { getAdminDashboard } from '../../api/dashboard'
import AnimatedNumber from '../../components/AnimatedNumber.vue'

const stats = ref({ memberCount: 0, coachCount: 0, courseCount: 0, todayCheckinCount: 0 })
const financeStats = ref({
  courseRevenue: 0,
  privateCoachingRevenue: 0,
  totalRevenue: 0,
  monthlyCourseRevenue: 0,
  monthlyPrivateRevenue: 0,
  monthlyTotalRevenue: 0
})
const weeklyCheckinChart = ref(null)
const courseReservationChart = ref(null)
let weeklyChart = null
let courseChart = null

const chartData = ref({ weeklyCheckin: [], courseReservations: [] })

function formatMoney(val) {
  if (!val) return '0.00'
  const num = typeof val === 'number' ? val : parseFloat(val)
  return num.toFixed(2)
}

function initChart(chartRef, option) {
  if (!chartRef) return
  const instance = echarts.init(chartRef)
  instance.setOption(option)
  return instance
}

function buildWeeklyOption(data) {
  return {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(d => d.name) },
    yAxis: { type: 'value' },
    series: [{
      name: '签到人数', type: 'line', data: data.map(d => d.value), smooth: true,
      itemStyle: { color: '#409eff' },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(64,158,255,0.3)' },
        { offset: 1, color: 'rgba(64,158,255,0.05)' }
      ])}
    }]
  }
}

function buildPieOption(data) {
  const colors = ['#67c23a', '#409eff', '#e6a23c', '#f56c6c', '#909399', '#8b5cf6', '#06b6d4', '#f97316']
  return {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      name: '预约人数', type: 'pie', radius: ['40%', '70%'],
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}: {c}人' },
      data: data.map((d, i) => ({ name: d.name, value: d.value, itemStyle: { color: colors[i % colors.length] } }))
    }]
  }
}

async function loadData() {
  try {
    const res = await getAdminDashboard()
    if (res.code === 200) {
      const d = res.data
      stats.value.memberCount = d.memberCount || 0
      stats.value.coachCount = d.coachCount || 0
      stats.value.courseCount = d.courseCount || 0
      stats.value.todayCheckinCount = d.todayCheckinCount || 0
      chartData.value.weeklyCheckin = d.weeklyCheckin || []
      chartData.value.courseReservations = d.courseReservations || []
      
      const fs = d.financeStats || {}
      financeStats.value.courseRevenue = fs.courseRevenue || 0
      financeStats.value.privateCoachingRevenue = fs.privateCoachingRevenue || 0
      financeStats.value.totalRevenue = fs.totalRevenue || 0
      financeStats.value.monthlyCourseRevenue = fs.monthlyCourseRevenue || 0
      financeStats.value.monthlyPrivateRevenue = fs.monthlyPrivateRevenue || 0
      financeStats.value.monthlyTotalRevenue = fs.monthlyTotalRevenue || 0
      
      renderCharts()
    }
  } catch (e) {
    console.error('加载看板数据失败', e)
  }
}

function renderCharts() {
  if (weeklyChart) weeklyChart.dispose()
  if (courseChart) courseChart.dispose()

  if (weeklyCheckinChart.value) {
    weeklyChart = initChart(weeklyCheckinChart.value, buildWeeklyOption(chartData.value.weeklyCheckin))
  }
  if (courseReservationChart.value) {
    courseChart = initChart(courseReservationChart.value, buildPieOption(chartData.value.courseReservations))
  }
}

function handleResize() {
  weeklyChart?.resize()
  courseChart?.resize()
}

async function exportExcel() {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch('http://localhost:8080/api/dashboard/admin/export', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    if (!response.ok) throw new Error('导出失败')
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '数据看板.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (e) {
    console.error('导出失败', e)
    alert('导出失败，请重试')
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  weeklyChart?.dispose()
  courseChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard { 
  padding: 16px; 
  height: 100%; 
  overflow: auto; 
  background: #f5f7fa;
}
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.dashboard-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}
.export-btn {
  display: flex;
  align-items: center;
  gap: 4px;
}
.stats-row { 
  margin-bottom: 12px; 
}
.stat-card {
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: none;
}
.stat-content { 
  display: flex; 
  align-items: center; 
  height: 60px;
}
.stat-icon { 
  width: 44px; 
  height: 44px; 
  border-radius: 10px; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  margin-right: 12px; 
  flex-shrink: 0;
}
.stat-icon.member { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.coach { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-icon.course { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.checkin { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.stat-icon.revenue { background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); }
.stat-icon :deep(svg) { width: 22px; height: 22px; color: #fff; }
.stat-info { 
  flex: 1; 
  display: flex; 
  flex-direction: column;
  justify-content: center;
}
.stat-value { 
  font-size: 22px; 
  font-weight: 700; 
  color: #1f2937; 
  line-height: 1.2;
}
.stat-label { 
  font-size: 12px; 
  color: #6b7280; 
  margin-top: 2px; 
  line-height: 1.2;
}
.stat-detail { 
  display: flex; 
  flex-wrap: wrap; 
  gap: 16px; 
  margin-top: 10px; 
  padding-top: 10px; 
  border-top: 1px solid #e5e7eb; 
}
.detail-item { 
  font-size: 12px; 
  color: #6b7280; 
}
.revenue-card {
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
}
.charts-row { 
  margin-top: 12px; 
}
.chart-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: none;
}
.chart-content {
  height: 240px;
}

/* 资金流水卡片样式 */
.finance-card {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(249, 115, 22, 0.15);
  padding: 20px;
}
.finance-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px dashed rgba(249, 115, 22, 0.25);
}
.finance-icon {
  width: 32px;
  height: 32px;
  color: #d97706;
  margin-right: 10px;
}
.finance-title {
  font-size: 16px;
  font-weight: 600;
  color: #92400e;
}
.finance-content {
  padding-top: 4px;
  display: flex;
  gap: 24px;
}
.finance-section {
  flex: 1;
}
.section-title {
  font-size: 13px;
  font-weight: 500;
  color: #92400e;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid #f97316;
}
.finance-grid {
  display: flex;
  gap: 16px;
}
.finance-item {
  flex: 1;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px 8px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 8px;
}
.finance-item.highlight {
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(249, 115, 22, 0.3);
}
.finance-value {
  font-size: 18px;
  font-weight: 700;
  color: #dc2626;
  line-height: 1.3;
}
.finance-value.total {
  font-size: 22px;
  color: #b45309;
}
.finance-label {
  font-size: 12px;
  color: #78350f;
  margin-top: 4px;
  line-height: 1.2;
}
.finance-item.highlight .finance-label {
  color: #92400e;
  font-weight: 500;
}

/* 数字动画组件样式 */
:deep(.animated-number) {
  font-variant-numeric: tabular-nums;
}
:deep(.number-prefix) {
  margin-right: 2px;
}
:deep(.number-value) {
  font-weight: inherit;
}
</style>
