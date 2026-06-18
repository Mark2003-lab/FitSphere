<template>
  <div class="dashboard-root">
    <!-- 核心数据区 Bento Grid -->
    <div class="stats-grid">
      <div v-for="s in statsItems" :key="s.label" class="stat-card" :class="s.accent">
        <div class="stat-card-shine"></div>
        <div class="stat-icon-wrap">
          <component :is="getIcon(s.icon)" class="stat-icon" />
          <div class="stat-icon-blur"></div>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
        </div>
        <div class="stat-bg-pattern"></div>
      </div>
    </div>

    <!-- 3. 下方双栏 -->
    <div class="bottom-grid">
      <!-- 左侧：最近活动 -->
      <div class="activity-panel">
        <!-- 最近预约 -->
        <div class="glass-panel">
          <div class="panel-head">
            <component :is="getIcon('CalendarDays')" class="panel-head-icon" />
            <span>最近预约</span>
          </div>
          <div v-if="recentReservations.length === 0" class="empty-state">
            <component :is="getIcon('CalendarOff')" class="empty-icon" />
            <p>暂无预约记录</p>
          </div>
          <div v-for="r in recentReservations" :key="r.id" class="activity-item">
            <div class="act-info">
              <div class="act-title">{{ r.courseName || '课程' + r.courseId }}</div>
              <div class="act-time">{{ r.time || r.reservationTime || '' }}</div>
            </div>
            <span class="act-status" :class="r.status === 'APPROVED' ? 'approved' : 'pending'">
              <span class="status-dot"></span>
              {{ r.status === 'APPROVED' ? '已通过' : r.status === 'RESERVED' ? '待确认' : r.status }}
            </span>
          </div>
        </div>
        <!-- 最近签到 -->
        <div class="glass-panel">
          <div class="panel-head">
            <component :is="getIcon('CheckCircle')" class="panel-head-icon" />
            <span>最近签到</span>
          </div>
          <div v-if="recentCheckins.length === 0" class="empty-state">
            <component :is="getIcon('CalendarOff')" class="empty-icon" />
            <p>暂无签到记录</p>
          </div>
          <div v-for="c in recentCheckins" :key="c.id" class="activity-item">
            <div class="act-info">
              <div class="act-title">{{ formatDate(c.checkinTime) }}</div>
            </div>
            <span class="act-status checked-in">
              <span class="status-dot"></span>
              已签到
            </span>
          </div>
        </div>
      </div>

      <!-- 右侧：我的会员卡 -->
      <div class="card-panel">
        <div v-if="currentCard" class="membership-card">
          <div class="mcard-shine"></div>
          <div class="mcard-glow"></div>
          <div class="mcard-content">
            <div class="mcard-chip">
              <div class="chip-line"></div>
              <div class="chip-line"></div>
            </div>
            <div class="mcard-type" :class="levelClass">{{ membershipLevel }}</div>
            <div class="mcard-name">{{ currentCard.cardName }}</div>
            <div class="mcard-info">
              <div class="mcard-row">
                <span class="mcard-label">有效期至</span>
                <span class="mcard-value">{{ formatDate(currentCard.expireTime) }}</span>
              </div>
              <div v-if="remainingDays > 0" class="mcard-row">
                <span class="mcard-label">剩余天数</span>
                <span class="mcard-value highlight">{{ remainingDays }} 天</span>
              </div>
            </div>
            <div class="mcard-footer">
              <span>FitSphere</span>
              <component :is="getIcon('Sparkles')" class="sparkle" />
            </div>
          </div>
        </div>
        <div v-else class="glass-panel empty-card">
          <div class="empty-state">
            <component :is="getIcon('CreditCard')" class="empty-icon" />
            <p>暂无会员卡</p>
            <p class="empty-hint">前往购卡中心选购</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import * as LucideIcons from 'lucide-vue-next'
import { getCurrentCard } from '../../api/userCard'
import { getMemberDashboard } from '../../api/dashboard'

const userId = localStorage.getItem('userId') || 1

const stats = ref({ totalReservations: 0, totalCheckins: 0, privateCoachingCount: 0 })
const currentCard = ref(null)
const recentReservations = ref([])
const recentCheckins = ref([])

const statsItems = computed(() => [
  { label: '课程预约', value: stats.value.totalReservations, icon: 'CalendarDays', accent: 'accent-purple' },
  { label: '累计签到', value: stats.value.totalCheckins, icon: 'CheckCircle', accent: 'accent-green' },
  { label: '私教预约', value: stats.value.privateCoachingCount, icon: 'UserPlus', accent: 'accent-pink' },
  { label: '会员等级', value: membershipLevel.value, icon: 'Award', accent: 'accent-blue' }
])

const membershipLevel = computed(() => {
  if (!currentCard.value) return '普通用户'
  const map = { 'MONTHLY': '月卡会员', 'QUARTERLY': '季卡会员', 'YEARLY': '年卡会员' }
  return map[currentCard.value.cardType] || '普通用户'
})

const levelClass = computed(() => {
  if (!currentCard.value) return ''
  const map = { 'MONTHLY': 'lvl-monthly', 'QUARTERLY': 'lvl-quarterly', 'YEARLY': 'lvl-yearly' }
  return map[currentCard.value.cardType] || ''
})

const remainingDays = computed(() => {
  if (!currentCard.value?.expireTime) return 0
  const exp = new Date(currentCard.value.expireTime)
  const now = new Date()
  const diff = Math.ceil((exp - now) / (1000 * 60 * 60 * 24))
  return Math.max(0, diff)
})

function getIcon(name) { return LucideIcons[name] || LucideIcons['Circle'] }

function formatDate(d) {
  if (!d) return '-'
  if (typeof d === 'string') {
    if (d.includes('T')) return d.substring(0, 10)
    return d
  }
  return '-'
}

async function loadData() {
  try {
    const [res, cardRes] = await Promise.all([getMemberDashboard(), getCurrentCard(userId)])
    if (res.code === 200) {
      stats.value.totalReservations = res.data.totalReservations || 0
      stats.value.totalCheckins = res.data.totalCheckins || 0
      stats.value.privateCoachingCount = res.data.privateCoachingCount || 0
      recentReservations.value = res.data.recentReservations || []
      recentCheckins.value = res.data.recentCheckins || []
    }
    if (cardRes.code === 200 && cardRes.data) {
      currentCard.value = cardRes.data
    }
  } catch (e) { console.error('加载首页数据失败', e) }
}

onMounted(loadData)
</script>

<style scoped>
/* ============ ROOT ============ */
.dashboard-root {
  padding: 20px 24px;
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: radial-gradient(ellipse at 20% 0%, rgba(139,92,246,0.04), transparent 60%),
              radial-gradient(ellipse at 80% 50%, rgba(6,182,212,0.04), transparent 60%);
}

/* ============ STATS GRID ============ */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  position: relative;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(255,255,255,0.5);
  box-shadow: 0 4px 20px rgba(15,23,42,0.04);
  display: flex;
  align-items: center;
  gap: 14px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: default;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(15,23,42,0.1);
}

.stat-card-shine {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at var(--mouse-x, 50%) var(--mouse-y, 50%), rgba(255,255,255,0.3), transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.stat-card:hover .stat-card-shine {
  opacity: 1;
}

.stat-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
}

.stat-icon {
  width: 24px;
  height: 24px;
  color: #fff;
  position: relative;
  z-index: 1;
}

.stat-icon-blur {
  position: absolute;
  inset: -4px;
  border-radius: 18px;
  opacity: 0.4;
  filter: blur(8px);
}

/* accent colors */
.accent-purple .stat-icon-wrap { background: linear-gradient(135deg, #a78bfa, #7c3aed); }
.accent-purple .stat-icon-blur { background: #7c3aed; }
.accent-purple .stat-value { color: #5b21b6; }

.accent-green .stat-icon-wrap { background: linear-gradient(135deg, #6ee7b7, #10b981); }
.accent-green .stat-icon-blur { background: #10b981; }
.accent-green .stat-value { color: #047857; }

.accent-pink .stat-icon-wrap { background: linear-gradient(135deg, #f9a8d4, #ec4899); }
.accent-pink .stat-icon-blur { background: #ec4899; }
.accent-pink .stat-value { color: #be185d; }

.accent-blue .stat-icon-wrap { background: linear-gradient(135deg, #93c5fd, #3b82f6); }
.accent-blue .stat-icon-blur { background: #3b82f6; }
.accent-blue .stat-value { color: #1d4ed8; }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 2px;
}

.stat-bg-pattern {
  position: absolute;
  right: -20px;
  bottom: -20px;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  opacity: 0.06;
  pointer-events: none;
}

.accent-purple .stat-bg-pattern { background: #7c3aed; }
.accent-green .stat-bg-pattern { background: #10b981; }
.accent-pink .stat-bg-pattern { background: #ec4899; }
.accent-blue .stat-bg-pattern { background: #3b82f6; }

/* ============ BOTTOM GRID ============ */
.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 16px;
}

/* ============ GLASS PANELS ============ */
.glass-panel {
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(16px);
  border-radius: 16px;
  padding: 18px;
  border: 1px solid rgba(255,255,255,0.5);
  box-shadow: 0 4px 20px rgba(15,23,42,0.04);
}

.activity-panel {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.panel-head {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  font-size: 15px;
  color: #1e293b;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(0,0,0,0.06);
}

.panel-head-icon {
  width: 18px;
  height: 18px;
  color: #8b5cf6;
}

/* ============ ACTIVITY ITEMS ============ */
.activity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 10px;
  transition: all 0.2s ease;
  margin-bottom: 2px;
}

.activity-item:hover {
  background: rgba(139,92,246,0.04);
  transform: translateX(4px);
}

.act-title {
  font-weight: 600;
  font-size: 14px;
  color: #334155;
}

.act-time {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.act-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 20px;
  flex-shrink: 0;
}

.act-status.approved {
  background: rgba(16,185,129,0.1);
  color: #059669;
}

.act-status.pending {
  background: rgba(245,158,11,0.1);
  color: #d97706;
}

.act-status.checked-in {
  background: rgba(59,130,246,0.1);
  color: #2563eb;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  animation: dot-pulse 2s ease-in-out infinite;
}

@keyframes dot-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

/* ============ EMPTY STATE ============ */
.empty-state {
  text-align: center;
  padding: 24px 0;
  color: #94a3b8;
  font-size: 14px;
}

.empty-icon {
  width: 36px;
  height: 36px;
  color: #cbd5e1;
  margin: 0 auto 8px;
}

.empty-hint {
  font-size: 12px;
  color: #a78bfa;
  margin-top: 4px;
}

/* ============ MEMBERSHIP CARD ============ */
.membership-card {
  position: relative;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 50%, #1e293b 100%);
  border-radius: 20px;
  padding: 28px 24px;
  overflow: hidden;
  color: #fff;
  box-shadow: 0 12px 40px rgba(15,23,42,0.2);
  height: 100%;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.mcard-shine {
  position: absolute;
  top: -60%;
  left: -60%;
  width: 220%;
  height: 220%;
  background: conic-gradient(from 0deg, transparent, rgba(139,92,246,0.08), transparent, rgba(6,182,212,0.08), transparent);
  animation: shine-rotate 10s linear infinite;
}

.mcard-glow {
  position: absolute;
  top: -30%;
  right: -20%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(139,92,246,0.3), transparent 70%);
  filter: blur(40px);
}

.mcard-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.mcard-chip {
  display: flex;
  gap: 4px;
  margin-bottom: 16px;
}

.chip-line {
  width: 24px;
  height: 3px;
  border-radius: 2px;
  background: rgba(255,255,255,0.3);
}

.mcard-type {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 4px;
}

.lvl-monthly { background: linear-gradient(135deg, #a78bfa, #7c3aed); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.lvl-quarterly { background: linear-gradient(135deg, #fbbf24, #f59e0b); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }
.lvl-yearly { background: linear-gradient(135deg, #22d3ee, #0ea5e9); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }

.mcard-name {
  font-size: 13px;
  color: rgba(255,255,255,0.6);
  margin-bottom: 16px;
  font-family: monospace;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.mcard-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.mcard-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mcard-label {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
}

.mcard-value {
  font-size: 14px;
  font-weight: 600;
}

.mcard-value.highlight {
  background: linear-gradient(90deg, #a78bfa, #06b6d4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.mcard-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid rgba(255,255,255,0.08);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 3px;
  color: rgba(255,255,255,0.4);
}

.sparkle {
  width: 16px;
  height: 16px;
  opacity: 0.6;
}

.empty-card {
  height: 100%;
}

/* ============ RESPONSIVE ============ */
@media (max-width: 1100px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .bottom-grid { grid-template-columns: 1fr; }
  .welcome-inner { flex-direction: column; align-items: flex-start; }
  .welcome-right { text-align: left; }
  .badge-row { justify-content: flex-start; }
  .progress-section { justify-content: flex-start; }
}
</style>
