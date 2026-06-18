<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon member-icon">👥</div>
        <div class="stat-info">
          <div class="stat-value">128</div>
          <div class="stat-label">会员总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon coach-icon">👨‍🏫</div>
        <div class="stat-info">
          <div class="stat-value">15</div>
          <div class="stat-label">教练数量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon course-icon">📅</div>
        <div class="stat-info">
          <div class="stat-value">24</div>
          <div class="stat-label">本周课程</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon checkin-icon">✅</div>
        <div class="stat-info">
          <div class="stat-value">86</div>
          <div class="stat-label">今日签到</div>
        </div>
      </div>
    </div>
    <div class="charts-row">
      <div class="chart-card">
        <h3>会员增长趋势</h3>
        <ECharts :options="memberOption" class="chart" />
      </div>
      <div class="chart-card">
        <h3>热门课程排行</h3>
        <ECharts :options="courseOption" class="chart" />
      </div>
    </div>
    <div class="charts-row">
      <div class="chart-card">
        <h3>签到统计</h3>
        <ECharts :options="checkinOption" class="chart" />
      </div>
      <div class="chart-card">
        <h3>收入分析</h3>
        <ECharts :options="revenueOption" class="chart" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ECharts from '../components/ECharts.vue'

const memberOption = ref({
  xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
  yAxis: { type: 'value' },
  series: [{ data: [80, 95, 108, 115, 120, 128], type: 'line', smooth: true }]
})

const courseOption = ref({
  xAxis: { type: 'value' },
  yAxis: { type: 'category', data: ['瑜伽', 'HIIT', '力量训练', '普拉提', '有氧操'] },
  series: [{ data: [45, 38, 32, 28, 25], type: 'bar' }]
})

const checkinOption = ref({
  xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
  yAxis: { type: 'value' },
  series: [{ data: [65, 72, 68, 78, 82, 95, 86], type: 'bar', color: '#52c41a' }]
})

const revenueOption = ref({
  xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
  yAxis: { type: 'value' },
  series: [{ data: [12, 15, 18, 22, 20, 25], type: 'line', smooth: true, color: '#722ed1' }]
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.member-icon { background: #667eea; }
.coach-icon { background: #f093fb; }
.course-icon { background: #4ade80; }
.checkin-icon { background: #fbbf24; }

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-card h3 {
  margin-bottom: 15px;
  color: #333;
}

.chart {
  height: 250px;
}
</style>
