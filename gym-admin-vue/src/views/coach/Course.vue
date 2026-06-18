<template>
  <div class="course-root">
    <div class="page-header">
      <div class="header-left">
        <div class="header-icon">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        </div>
        <div>
          <h1>我教的课</h1>
          <p class="header-sub">管理你的课程与签到</p>
        </div>
      </div>
      <button class="btn-create" @click="handleCreateCourse">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        创建课程
      </button>
    </div>

    <!-- 课程表格 -->
    <div class="table-card">
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <table v-if="courses.length > 0" class="native-table course-list-table">
        <thead>
          <tr>
            <th>课程名称</th>
            <th class="th-date">上课时间</th>
            <th>上课地点</th>
            <th class="th-price">价格</th>
            <th class="th-num">容量</th>
            <th class="th-num">已预约</th>
            <th class="th-status">审核状态</th>
            <th class="th-status">签到状态</th>
            <th class="th-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in courses" :key="row.id" class="table-row">
            <td><span class="cell-course">{{ row.courseName }}</span></td>
            <td class="td-center"><span class="cell-mono">{{ formatDateTime(row.courseTime) }}</span></td>
            <td>{{ row.location }}</td>
            <td class="td-center"><span class="cell-price">¥{{ row.price || 0 }}</span></td>
            <td class="td-center">{{ row.capacity }}</td>
            <td class="td-center">{{ row.currentReservations }}</td>
            <td class="td-center">
              <span class="status-badge" :class="'status-' + (row.auditStatus || 'PENDING')">
                {{ getAuditStatusText(row.auditStatus) }}
              </span>
            </td>
            <td class="td-center">
              <span v-if="row.checkinEnabled" class="status-badge" :class="isCheckinTime(row) ? 'checkin-active' : 'checkin-published'">
                {{ isCheckinTime(row) ? '签到中' : '签到已发布' }}
              </span>
              <span v-else class="status-badge status-CANCELLED">未发布</span>
            </td>
            <td class="td-center">
              <div class="action-btns">
                <button
                  v-if="row.auditStatus === 'APPROVED' && !row.checkinEnabled"
                  class="btn-publish"
                  @click="handlePublishCheckin(row)"
                >发布签到</button>
                <button
                  v-if="row.auditStatus === 'APPROVED' && row.checkinEnabled"
                  class="btn-stop"
                  @click="handleStopCheckin(row)"
                >停止签到</button>
                <button
                  v-if="row.auditStatus === 'PENDING'"
                  class="btn-pending"
                  disabled
                >审核中</button>
                <button class="btn-view" @click="handleViewReservations(row)">查看预约</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="!loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#fdba74" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
        <p>暂无课程，点击上方按钮创建</p>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--; loadCourses()">上一页</button>
        <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="currentPage++; loadCourses()">下一页</button>
      </div>
    </div>

    <!-- 创建课程对话框 -->
    <div class="modal-overlay" v-if="createDialogVisible" @click.self="createDialogVisible = false">
      <div class="modal-card modal-form">
        <div class="modal-header">
          <h3>{{ isEdit ? '编辑课程' : '创建课程' }}</h3>
          <button class="modal-close" @click="createDialogVisible = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>课程名称 <span class="required">*</span></label>
            <input v-model="form.courseName" class="form-input" placeholder="请输入课程名称" />
          </div>
          <div class="form-group">
            <label>上课时间 <span class="required">*</span></label>
            <input v-model="form.courseTime" type="datetime-local" class="form-input" />
          </div>
          <div class="form-group">
            <label>上课地点 <span class="required">*</span></label>
            <input v-model="form.location" class="form-input" placeholder="请输入上课地点" />
          </div>
          <div class="form-row">
            <div class="form-group flex-1">
              <label>课程价格 <span class="required">*</span></label>
              <div class="input-with-suffix">
                <input v-model.number="form.price" type="number" class="form-input" placeholder="请输入课程价格" />
                <span class="input-suffix">元</span>
              </div>
            </div>
            <div class="form-group flex-1">
              <label>课程容量 <span class="required">*</span></label>
              <input v-model.number="form.capacity" type="number" min="1" max="100" class="form-input" />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="createDialogVisible = false">取消</button>
          <button class="btn-confirm" @click="handleSubmit">确定</button>
        </div>
      </div>
    </div>

    <!-- 预约详情对话框 -->
    <div class="modal-overlay" v-if="dialogVisible" @click.self="dialogVisible = false">
      <div class="modal-card modal-table">
        <div class="modal-header">
          <h3>课程预约详情</h3>
          <button class="modal-close" @click="dialogVisible = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="reservations.length > 0" class="reserve-cards">
            <div v-for="row in reservations" :key="row.id" class="reserve-card">
              <div class="rc-avatar">
                <span>{{ row.memberName?.charAt(0) || '?' }}</span>
              </div>
              <div class="rc-name">{{ row.memberName }}</div>
              <div class="rc-phone">{{ row.memberPhone }}</div>
              <div class="rc-badges">
                <span class="status-badge" :class="'status-' + row.status">{{ getStatusText(row.status) }}</span>
                <span class="status-badge" :class="'pay-' + row.paymentStatus">{{ getPaymentStatusText(row.paymentStatus) }}</span>
              </div>
              <div class="rc-time">{{ formatDateTime(row.reservationTime) }}</div>
            </div>
          </div>
          <div v-else class="empty-state-sm">
            <p>暂无预约记录</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="dialogVisible = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- 发布签到对话框 -->
    <div class="modal-overlay" v-if="checkinDialogVisible" @click.self="checkinDialogVisible = false">
      <div class="modal-card modal-form">
        <div class="modal-header">
          <h3>发布签到</h3>
          <button class="modal-close" @click="checkinDialogVisible = false">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>课程名称</label>
            <p class="form-static">{{ currentCourse?.courseName }}</p>
          </div>
          <div class="form-group">
            <label>签到持续时长</label>
            <div class="input-with-suffix">
              <input v-model.number="checkinDuration" type="number" min="5" max="120" class="form-input form-input-sm" />
              <span class="input-suffix">分钟</span>
            </div>
          </div>
          <div class="form-hint">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
            发布后会员可在签到时间内自行签到，超时后仍可代签。
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="checkinDialogVisible = false">取消</button>
          <button class="btn-confirm" @click="confirmPublishCheckin">确认发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCoachReservations } from '../../api/reservation'
import { getCoachCourses, publishCourseCheckin, stopCourseCheckin, coachCreateCourse } from '../../api/course'

const courses = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const reservations = ref([])

const createDialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  courseName: '',
  courseTime: null,
  location: '',
  price: null,
  capacity: 20
})

const checkinDialogVisible = ref(false)
const checkinDuration = ref(30)
const currentCourse = ref(null)

function getStatusText(status) {
  return { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝', CANCELLED: '已取消' }[status] || status
}

function getAuditStatusText(status) {
  return { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝' }[status] || status || '待审核'
}

function getPaymentStatusText(status) {
  return { UNPAID: '未支付', PAID: '已支付' }[status] || status
}

function formatDateTime(dateTime) {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

async function loadCourses() {
  loading.value = true
  try {
    const res = await getCoachCourses(currentPage.value, pageSize.value)
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

function handleCreateCourse() {
  isEdit.value = false
  form.value = { courseName: '', courseTime: null, location: '', price: null, capacity: 20 }
  createDialogVisible.value = true
}

async function handleSubmit() {
  if (!form.value.courseName) { ElMessage.warning('请填写课程名称'); return }
  if (!form.value.courseTime) { ElMessage.warning('请选择上课时间'); return }
  if (!form.value.location) { ElMessage.warning('请填写上课地点'); return }
  if (!form.value.price || form.value.price <= 0) { ElMessage.warning('请输入有效的课程价格'); return }
  try {
    const payload = { ...form.value, courseTime: new Date(form.value.courseTime).toISOString() }
    const res = await coachCreateCourse(payload)
    if (res.code === 200) {
      ElMessage.success(res.message || '课程创建成功')
      createDialogVisible.value = false
      loadCourses()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '创建课程失败')
  }
}

async function handleViewReservations(row) {
  try {
    const res = await getCoachReservations(1, 1000)
    if (res.code === 200) {
      reservations.value = res.data.records.filter(r => r.courseId === row.id)
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('加载预约详情失败')
  }
}

function handlePublishCheckin(row) {
  currentCourse.value = row
  checkinDuration.value = 30
  checkinDialogVisible.value = true
}

async function confirmPublishCheckin() {
  try {
    const res = await publishCourseCheckin(currentCourse.value.id, { durationMinutes: checkinDuration.value })
    if (res.code === 200) {
      ElMessage.success('签到已发布')
      checkinDialogVisible.value = false
      loadCourses()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发布签到失败')
  }
}

async function handleStopCheckin(row) {
  try {
    const res = await stopCourseCheckin(row.id)
    if (res.code === 200) {
      ElMessage.success('签到已停止')
      loadCourses()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '停止签到失败')
  }
}

function isCheckinTime(row) {
  if (!row.checkinStartTime || !row.checkinEndTime) return false
  const now = new Date().getTime()
  const start = new Date(row.checkinStartTime).getTime()
  const end = new Date(row.checkinEndTime).getTime()
  return now >= start && now <= end
}

onMounted(() => { loadCourses() })
</script>

<style scoped>
/* ========== ROOT ========== */
.course-root { padding: 24px; min-height: 100%; background: transparent; }

/* ========== PAGE HEADER ========== */
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 22px; }
.header-left { display: flex; align-items: center; gap: 16px; }
.header-icon {
  width: 50px; height: 50px; border-radius: 14px;
  background: linear-gradient(135deg, #ea580c, #f97316); color: #fff;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 18px rgba(234,88,12,0.3);
}
.header-left h1 { margin: 0; font-size: 22px; font-weight: 700; color: #1e293b; }
.header-sub { margin: 3px 0 0; font-size: 13px; color: #94a3b8; }

.btn-create {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 22px;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: #fff; border: none; border-radius: 12px;
  font-size: 14px; font-weight: 600; cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 14px rgba(249,115,22,0.3);
}
.btn-create:hover { box-shadow: 0 6px 20px rgba(249,115,22,0.45); transform: translateY(-2px); }

/* ========== TABLE CARD ========== */
.table-card {
  background: rgba(255,255,255,0.88); backdrop-filter: blur(10px);
  border-radius: 16px; border: 1px solid rgba(234,88,12,0.08);
  box-shadow: 0 2px 12px rgba(0,0,0,0.03), 0 1px 4px rgba(0,0,0,0.02);
  overflow-x: auto; position: relative;
}

/* ========== NATIVE TABLE ========== */
.native-table { width: 100%; min-width: 1050px; border-collapse: collapse; font-size: 13px; }

.native-table thead {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
  border-bottom: 2px solid #fdba74;
}

.native-table th {
  padding: 11px 8px; text-align: left; font-weight: 600; font-size: 12px;
  color: #9a3412; letter-spacing: 0.01em; user-select: none; vertical-align: middle;
}

.native-table th.th-date, .native-table th.th-time, .native-table th.th-price,
.native-table th.th-num, .native-table th.th-status, .native-table th.th-action { white-space: nowrap; }

.native-table td {
  padding: 10px 8px; color: #374151; border-bottom: 1px solid #fff7ed;
  vertical-align: middle; font-size: 13px;
}

.native-table th.th-date, .native-table th.th-time, .native-table th.th-price,
.native-table th.th-num, .native-table th.th-status, .native-table th.th-action,
.native-table th.th-id, .native-table th.th-type, .native-table th.th-rate,
.native-table td.td-center { text-align: center; }

/* 列宽 */
.course-list-table th:nth-child(1), .course-list-table td:nth-child(1) { min-width: 100px; }
.course-list-table th:nth-child(2), .course-list-table td:nth-child(2) { min-width: 130px; }
.course-list-table th:nth-child(3), .course-list-table td:nth-child(3) { min-width: 90px; }
.course-list-table th:nth-child(4), .course-list-table td:nth-child(4) { min-width: 130px; }
.course-list-table th:nth-child(5), .course-list-table td:nth-child(5) { min-width: 100px; }
.course-list-table th:nth-child(6), .course-list-table td:nth-child(6) { min-width: 80px; }
.course-list-table th:nth-child(7), .course-list-table td:nth-child(7) { min-width: 100px; }
.course-list-table th:nth-child(8), .course-list-table td:nth-child(8) { min-width: 100px; }
.course-list-table th:nth-child(9), .course-list-table td:nth-child(9) { min-width: 180px; }

/* 行 hover */
.table-row { transition: background 0.2s ease, box-shadow 0.2s ease; }
.table-row:hover { background: #fff7ed; box-shadow: inset 3px 0 0 #f97316; }

/* ========== CELL STYLES ========== */
.cell-user { display: flex; align-items: center; gap: 10px; }
.user-avatar-sm {
  width: 28px; height: 28px; border-radius: 50%;
  background: linear-gradient(135deg, #fb923c, #f97316);
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600; flex-shrink: 0;
}
.cell-course { font-weight: 500; color: #c2410c; }
.cell-mono { font-family: 'SF Mono','Cascadia Code','Consolas',monospace; font-size: 13px; color: #6b7280; }
.cell-price { font-weight: 600; color: #059669; font-family: 'SF Mono','Cascadia Code',monospace; }

/* ========== STATUS BADGES ========== */
.status-badge {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600;
  letter-spacing: 0.01em; white-space: nowrap;
}
.status-PENDING { background: #fef3c7; color: #d97706; border: 1px solid #fde68a; }
.status-APPROVED { background: #d1fae5; color: #059669; border: 1px solid #a7f3d0; }
.status-REJECTED { background: #fee2e2; color: #dc2626; border: 1px solid #fecaca; }
.status-CANCELLED { background: #f3f4f6; color: #6b7280; border: 1px solid #e5e7eb; }
.checkin-active { background: #d1fae5; color: #059669; border: 1px solid #a7f3d0; }
.checkin-published { background: #dbeafe; color: #2563eb; border: 1px solid #bfdbfe; }
.pay-PAID { background: #dbeafe; color: #2563eb; border: 1px solid #bfdbfe; }
.pay-UNPAID { background: #fef3c7; color: #d97706; border: 1px solid #fde68a; }

/* ========== ACTION BUTTONS ========== */
.action-btns { display: flex; gap: 6px; justify-content: center; flex-wrap: nowrap; }
.action-btns button {
  padding: 5px 12px; border-radius: 7px; border: none;
  font-size: 12px; font-weight: 500; cursor: pointer;
  transition: all 0.2s ease; white-space: nowrap;
}
.btn-publish { background: #d1fae5; color: #059669; }
.btn-publish:hover { background: #059669; color: #fff; box-shadow: 0 2px 8px rgba(5,150,105,0.3); }
.btn-stop { background: #fef3c7; color: #d97706; }
.btn-stop:hover { background: #d97706; color: #fff; box-shadow: 0 2px 8px rgba(217,119,6,0.3); }
.btn-view { background: #fff7ed; color: #c2410c; }
.btn-view:hover { background: #f97316; color: #fff; box-shadow: 0 2px 8px rgba(249,115,22,0.3); }
.btn-pending { background: #f3f4f6; color: #9ca3af; cursor: not-allowed; }
.btn-pending:disabled { opacity: 0.5; }

/* ========== PAGINATION ========== */
.pagination {
  display: flex; align-items: center; justify-content: center;
  gap: 14px; padding: 18px 24px; border-top: 1px solid #fff7ed;
}
.page-btn {
  padding: 7px 18px; border: 1px solid #fdba74; background: #fff;
  border-radius: 10px; font-size: 13px; font-weight: 500;
  color: #c2410c; cursor: pointer; transition: all 0.2s ease;
}
.page-btn:hover:not(:disabled) { background: #fff7ed; border-color: #f97316; }
.page-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.page-info { font-size: 13px; color: #6b7280; font-weight: 500; }

/* ========== LOADING ========== */
.loading-mask {
  position: absolute; inset: 0;
  background: rgba(255,255,255,0.8); backdrop-filter: blur(4px);
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  gap: 12px; z-index: 10; border-radius: 16px;
  color: #c2410c; font-size: 14px;
}
.spinner {
  width: 32px; height: 32px;
  border: 3px solid #ffedd5;
  border-top-color: #f97316;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ========== EMPTY STATE ========== */
.empty-state {
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  padding: 60px 20px; color: #fdba74;
}
.empty-state p { margin-top: 12px; font-size: 14px; color: #9ca3af; }
.empty-state-sm { display: flex; align-items: center; justify-content: center; padding: 30px 20px; }
.empty-state-sm p { color: #9ca3af; font-size: 14px; }

/* ========== RESERVE CARDS ========== */
.reserve-cards {
  display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 10px;
}
.reserve-card {
  background: #f8fafc; border: 1px solid #f1f5f9;
  border-radius: 12px; padding: 14px 12px;
  text-align: center; transition: all 0.2s ease;
}
.reserve-card:hover { border-color: #fdba74; background: #fff7ed; transform: translateY(-1px); }
.rc-avatar {
  width: 38px; height: 38px; border-radius: 50%;
  background: linear-gradient(135deg, #fb923c, #f97316);
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 15px; font-weight: 600; margin: 0 auto 8px;
}
.rc-name { font-size: 14px; font-weight: 600; color: #1e293b; margin-bottom: 2px; }
.rc-phone { font-size: 12px; color: #64748b; font-family: 'SF Mono','Cascadia Code','Consolas',monospace; margin-bottom: 8px; }
.rc-badges { display: flex; gap: 6px; justify-content: center; margin-bottom: 8px; flex-wrap: wrap; }
.rc-badges .status-badge { padding: 2px 7px; font-size: 11px; }
.rc-time { font-size: 11px; color: #94a3b8; font-family: 'SF Mono','Cascadia Code','Consolas',monospace; }

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

/* ========== FORM ========== */
.form-item { margin-bottom: 16px; }
</style>