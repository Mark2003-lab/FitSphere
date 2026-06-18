<template>
  <div class="coach-private-coaching">
    <div class="header-section">
      <h1>私教课程管理</h1>
      <p class="header-sub">管理您的私教课程预约与进度</p>
    </div>

    <div class="tabs-container">
      <div class="tab-nav">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'pending' }"
          @click="activeTab = 'pending'"
        >
          <span class="tab-count">{{ pendingList.length }}</span>
          待确认课程
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'confirmed' }"
          @click="activeTab = 'confirmed'"
        >
          <span class="tab-count">{{ confirmedList.length }}</span>
          已确认课程
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'completed' }"
          @click="activeTab = 'completed'"
        >
          <span class="tab-count">{{ completedList.length }}</span>
          已完成课程
        </button>
      </div>

      <!-- ========== 待处理 ========== -->
      <div class="tab-content" v-show="activeTab === 'pending'">
        <div v-if="loading && pendingList.length === 0" class="loading-state">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>
        
        <div v-else-if="pendingList.length > 0" class="booking-grid">
          <div 
            v-for="row in pendingList" 
            :key="row.id" 
            class="booking-card pending-card"
          >
            <div class="card-header">
              <div class="member-info">
                <div class="avatar">
                  <span>{{ row.memberName?.charAt(0) || '?' }}</span>
                </div>
                <div class="member-details">
                  <h3>{{ row.memberName }}</h3>
                  <p class="member-phone">{{ row.memberPhone }}</p>
                </div>
              </div>
              <div class="status-badge status-pending">待确认</div>
            </div>
            
            <div class="card-body">
              <div class="course-info">
                <div class="info-item">
                  <label>课程</label>
                  <span>{{ row.courseName }}</span>
                </div>
                <div class="info-item">
                  <label>类型</label>
                  <span>{{ getCourseTypeText(row.courseType) }}</span>
                </div>
                <div class="info-item">
                  <label>时间</label>
                  <span class="time-info">{{ formatDateTime(row.scheduledTime) }}</span>
                </div>
                <div class="info-item">
                  <label>时长</label>
                  <span>{{ row.duration }} 分钟</span>
                </div>
                <div class="info-item">
                  <label>价格</label>
                  <span class="price">¥{{ row.price }}</span>
                </div>
                <div class="info-item full-width">
                  <label>备注</label>
                  <span class="notes">{{ row.notes || '无备注' }}</span>
                </div>
              </div>
            </div>
            
            <div class="card-footer">
              <button class="btn-confirm" @click="handleConfirm(row)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
                确认接受
              </button>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
          <h3>暂无待确认课程</h3>
          <p>当前没有需要您确认的私教课程预约</p>
        </div>
      </div>

      <!-- ========== 已确认/进行中 ========== -->
      <div class="tab-content" v-show="activeTab === 'confirmed'">
        <div v-if="loading && confirmedList.length === 0" class="loading-state">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>
        
        <div v-else-if="confirmedList.length > 0" class="booking-grid">
          <div 
            v-for="row in confirmedList" 
            :key="row.id" 
            class="booking-card confirmed-card"
          >
            <div class="card-header">
              <div class="member-info">
                <div class="avatar">
                  <span>{{ row.memberName?.charAt(0) || '?' }}</span>
                </div>
                <div class="member-details">
                  <h3>{{ row.memberName }}</h3>
                  <p class="member-phone">{{ row.memberPhone }}</p>
                </div>
              </div>
              <div class="status-badge status-confirmed">进行中</div>
            </div>
            
            <div class="card-body">
              <div class="course-info">
                <div class="info-item">
                  <label>课程</label>
                  <span>{{ row.courseName }}</span>
                </div>
                <div class="info-item">
                  <label>时间</label>
                  <span class="time-info">{{ formatDateTime(row.scheduledTime) }}</span>
                </div>
                <div class="info-item">
                  <label>时长</label>
                  <span>{{ row.duration }} 分钟</span>
                </div>
                <div class="info-item">
                  <label>地点</label>
                  <span>{{ row.location || '未指定' }}</span>
                </div>
                <div class="info-item full-width">
                  <label>备注</label>
                  <span class="notes">{{ row.notes || '无备注' }}</span>
                </div>
              </div>
            </div>
            
            <div class="card-footer">
              <button class="btn-complete" @click="handleComplete(row)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="9 11 12 14 22 4"></polyline>
                  <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path>
                </svg>
                标记完成
              </button>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
          <h3>暂无已确认课程</h3>
          <p>当前没有正在进行的私教课程</p>
        </div>
      </div>

      <!-- ========== 已完成 ========== -->
      <div class="tab-content" v-show="activeTab === 'completed'">
        <div v-if="loading && completedList.length === 0" class="loading-state">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>
        
        <div v-else-if="completedList.length > 0" class="booking-grid">
          <div 
            v-for="row in completedList" 
            :key="row.id" 
            class="booking-card completed-card"
          >
            <div class="card-header">
              <div class="member-info">
                <div class="avatar">
                  <span>{{ row.memberName?.charAt(0) || '?' }}</span>
                </div>
                <div class="member-details">
                  <h3>{{ row.memberName }}</h3>
                  <p class="member-phone">{{ row.memberPhone }}</p>
                </div>
              </div>
              <div class="status-badge status-completed">已完成</div>
            </div>
            
            <div class="card-body">
              <div class="course-info">
                <div class="info-item">
                  <label>课程</label>
                  <span>{{ row.courseName }}</span>
                </div>
                <div class="info-item">
                  <label>预约时间</label>
                  <span class="time-info">{{ formatDateTime(row.scheduledTime) }}</span>
                </div>
                <div class="info-item">
                  <label>完成时间</label>
                  <span class="time-info">{{ formatDateTime(row.completeTime) }}</span>
                </div>
                <div class="info-item">
                  <label>价格</label>
                  <span class="price">¥{{ row.price }}</span>
                </div>
                <div class="info-item full-width">
                  <label>学员评价</label>
                  <div class="rating-section" v-if="row.rating">
                    <div class="rating-stars">
                      <svg v-for="n in 5" :key="n" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="n <= row.rating ? 'star-filled' : 'star-empty'">
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                      </svg>
                    </div>
                    <p class="review-text">{{ row.review || '无评价内容' }}</p>
                  </div>
                  <span v-else class="no-rating">待评价</span>
                </div>
              </div>
            </div>
            
            <div class="card-footer">
              <div class="completed-info">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="9 11 12 14 22 4"></polyline>
                  <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"></path>
                </svg>
                课程已完成
              </div>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
          <h3>暂无已完成课程</h3>
          <p>暂无已完成的私教课程记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCoachPrivateCoaching, confirmPrivateCoaching, completePrivateCoaching } from '../../api/privateCoaching'

const activeTab = ref('pending')
const allBookings = ref([])
const loading = ref(false)

const pendingList = computed(() => allBookings.value.filter(b => b.status === 'APPROVED'))
const confirmedList = computed(() => allBookings.value.filter(b => b.status === 'CONFIRMED'))
const completedList = computed(() => allBookings.value.filter(b => b.status === 'COMPLETED'))

function getCourseTypeText(t) {
  return { FAT_LOSS:'减脂训练', MUSCLE_GAIN:'增肌训练', BODY_SHAPING:'塑形训练', STRENGTH:'力量训练', YOGA:'瑜伽普拉提', REHAB:'康复训练', GENERAL:'综合健身' }[t] || t || '未设置'
}
function formatDateTime(dt) {
  if (!dt) return ''
  const d = new Date(dt)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}

async function loadBookings() {
  loading.value = true
  try {
    const res = await getCoachPrivateCoaching(1, 200)
    if (res.code === 200) allBookings.value = res.data.records || []
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

async function handleConfirm(row) {
  try {
    await ElMessageBox.confirm(`确认接受学员"${row.memberName}"的私教课程？`, '确认')
    const res = await confirmPrivateCoaching(row.id)
    if (res.code === 200) { ElMessage.success('已确认'); loadBookings() }
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

async function handleComplete(row) {
  try {
    await ElMessageBox.confirm(`确认学员"${row.memberName}"的课程已完成？`, '标记完成')
    const res = await completePrivateCoaching(row.id)
    if (res.code === 200) { ElMessage.success('已标记完成'); loadBookings() }
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

onMounted(() => loadBookings())
</script>

<style scoped>
/* ========== GLOBAL FONTS & ANIMATIONS ========== */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

.coach-private-coaching {
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

/* ========== TABS ========== */
.tabs-container {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.08);
  overflow: hidden;
}

.tab-nav {
  display: flex;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to right, rgba(249, 115, 22, 0.02), transparent);
}

.tab-btn {
  flex: 1;
  padding: 16px 20px;
  border: none;
  background: transparent;
  font-size: 15px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.tab-btn.active {
  color: #f97316;
  background: rgba(253, 230, 138, 0.2);
}
.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 3px;
  background: #f97316;
}
.tab-btn:hover:not(.active) {
  color: #1e293b;
  background: #f8fafc;
}

.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}
.tab-btn.active .tab-count {
  background: #f97316;
  color: white;
}

.tab-content {
  padding: 24px;
}

/* ========== BOOKING GRID & CARDS ========== */
.booking-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.booking-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  padding: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.booking-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.15);
  border-color: #f9731620;
}

.pending-card {
  border-left: 4px solid #f59e0b;
}
.confirmed-card {
  border-left: 4px solid #10b981;
}
.completed-card {
  border-left: 4px solid #8b5cf6;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  flex-shrink: 0;
}

.member-details h3 {
  margin: 0 0 2px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}
.member-details .member-phone {
  margin: 0;
  font-size: 13px;
  color: #64748b;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
}
.status-pending {
  background: #fffbf0;
  color: #b45309;
  border: 1px solid #fde68a;
}
.status-confirmed {
  background: #ecfdf5;
  color: #047857;
  border: 1px solid #a7f3d0;
}
.status-completed {
  background: #f3e8ff;
  color: #7c2d12;
  border: 1px solid #ddd6fe;
}

.card-body {
  margin-bottom: 16px;
}

.course-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
}
.info-item.full-width {
  grid-column: 1 / -1;
}
.info-item label {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 4px;
  font-weight: 500;
}
.info-item span {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}
.info-item .time-info {
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  font-size: 13px;
  color: #64748b;
}
.info-item .price {
  color: #ef4444;
  font-weight: 600;
}
.info-item .notes {
  color: #64748b;
  font-style: italic;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

/* ========== BUTTONS ========== */
.btn-confirm, .btn-complete {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 10px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-confirm {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}
.btn-confirm:hover {
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.4);
  transform: translateY(-1px);
}
.btn-complete {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}
.btn-complete:hover {
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
  transform: translateY(-1px);
}

.completed-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f3e8ff;
  color: #7c2d12;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
}

/* ========== RATING STARS ========== */
.rating-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.rating-stars {
  display: flex;
  gap: 4px;
}
.star-filled {
  fill: #f59e0b;
  stroke: #f59e0b;
}
.star-empty {
  fill: none;
  stroke: #d1d5db;
}
.review-text {
  font-size: 13px;
  color: #374151;
  line-height: 1.5;
  margin: 0;
}
.no-rating {
  color: #9ca3af;
  font-style: italic;
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

/* ========== LOADING STATE ========== */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
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

/* ========== RESPONSIVE DESIGN ========== */
@media (max-width: 1200px) {
  .booking-grid {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  }
}

@media (max-width: 768px) {
  .coach-private-coaching {
    padding: 16px;
  }
  
  .tab-nav {
    flex-direction: column;
  }
  
  .tab-btn {
    justify-content: flex-start;
  }
  
  .course-info {
    grid-template-columns: 1fr;
  }
  
  .booking-grid {
    grid-template-columns: 1fr;
  }
}
</style>
