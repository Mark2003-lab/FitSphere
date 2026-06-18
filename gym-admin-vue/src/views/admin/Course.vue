<template>
  <div class="course-management">
    <div class="page-header">
      <h1>课程管理</h1>
      <div class="header-actions">
        <button class="btn-pending" @click="showPending = true">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="12" y1="18" x2="12" y2="12"/><line x1="9" y1="15" x2="15" y2="15"/></svg>
          待审核 ({{ pendingCount }})
        </button>
        <button class="btn-add" @click="handleAdd">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          添加课程
        </button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="filter-bar">
      <el-input v-model="searchKeyword" placeholder="搜索课程名称或教练" style="width: 260px" clearable />
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <table v-if="filteredCourses.length > 0" class="native-table">
        <thead>
          <tr>
            <th>课程名称</th>
            <th>教练</th>
            <th class="th-date">上课时间</th>
            <th class="th-num">容量</th>
            <th class="th-num">已预约</th>
            <th class="th-num">剩余</th>
            <th>地点</th>
            <th class="th-price">价格</th>
            <th class="th-status">审核</th>
            <th class="th-status">状态</th>
            <th class="th-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredCourses" :key="row.id" class="table-row">
            <td><span class="cell-name">{{ row.courseName }}</span></td>
            <td>
              <div class="cell-user">
                <span class="coach-avatar" :style="getAvatarStyle(row.coachName || '')">{{ (row.coachName || '教').charAt(0) }}</span>
                <span>{{ row.coachName }}</span>
              </div>
            </td>
            <td class="td-center"><span class="cell-mono">{{ formatDateTime(row.courseTime) }}</span></td>
            <td class="td-center">{{ row.capacity }}</td>
            <td class="td-center">
              <span class="cell-num" :class="{ 'cell-full': row.currentReservations >= row.capacity }">
                {{ row.currentReservations || 0 }}
              </span>
            </td>
            <td class="td-center">
              <span class="cell-remaining" :class="{ 'cell-none': (row.availableSlots || 0) <= 0 }">
                {{ row.availableSlots || 0 }}
              </span>
            </td>
            <td><span class="cell-location">{{ row.location }}</span></td>
            <td class="td-center"><span class="cell-price">¥{{ row.price || 0 }}</span></td>
            <td class="td-center">
              <span class="status-badge" :class="'audit-' + getAuditBadgeClass(row.auditStatus)">
                {{ getAuditStatusText(row.auditStatus) }}
              </span>
            </td>
            <td class="td-center">
              <span class="status-badge" :class="'status-' + getStatusBadgeClass(row.status)">
                {{ getStatusText(row.status) }}
              </span>
            </td>
            <td class="td-center">
              <div class="action-btns">
                <button class="btn-edit" @click="handleEdit(row)">编辑</button>
                <button class="btn-delete" @click="handleDelete(row)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="!loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
        <p>{{ searchKeyword ? '未找到匹配的课程' : '暂无课程数据' }}</p>
      </div>

      <div class="pagination" v-if="total > pageSize">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--; loadCourses()">上一页</button>
        <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="currentPage++; loadCourses()">下一页</button>
      </div>
    </div>

    <!-- 待审核课程弹窗 -->
    <Teleport to="body">
      <div v-if="showPending" class="modal-overlay" @click.self="showPending = false">
        <div class="modal-card modal-wide">
          <div class="modal-header">
            <h2>待审核课程</h2>
            <button class="modal-close" @click="showPending = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body" style="padding-top:8px">
            <div v-if="pendingLoading" class="loading-mask" style="position:relative;min-height:200px">
              <div class="spinner"></div>
              <span>加载中...</span>
            </div>
            <table v-else-if="pendingCourses.length > 0" class="native-table" style="min-width:700px">
              <thead>
                <tr>
                  <th>课程名称</th>
                  <th>教练</th>
                  <th class="th-date">上课时间</th>
                  <th class="th-num">容量</th>
                  <th>地点</th>
                  <th class="th-price">价格</th>
                  <th class="th-action">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="row in pendingCourses" :key="row.id" class="table-row">
                  <td><span class="cell-name">{{ row.courseName }}</span></td>
                  <td>{{ row.coachName }}</td>
                  <td class="td-center"><span class="cell-mono">{{ formatDateTime(row.courseTime) }}</span></td>
                  <td class="td-center">{{ row.capacity }}</td>
                  <td>{{ row.location }}</td>
                  <td class="td-center"><span class="cell-price">¥{{ row.price || 0 }}</span></td>
                  <td class="td-center">
                    <div class="action-btns">
                      <button class="btn-approve" @click="handleAudit(row, 'APPROVED')">通过</button>
                      <button class="btn-reject" @click="handleAudit(row, 'REJECTED')">拒绝</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-else class="empty-state" style="padding:32px">
              <p>暂无待审核课程</p>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 添加/编辑课程弹窗 -->
    <Teleport to="body">
      <div v-if="dialogVisible" class="modal-overlay" @click.self="dialogVisible = false">
        <div class="modal-card">
          <div class="modal-header">
            <h2>{{ isEdit ? '编辑课程' : '添加课程' }}</h2>
            <button class="modal-close" @click="dialogVisible = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>课程名称</label>
              <el-input v-model="form.courseName" placeholder="请输入课程名称" />
            </div>
            <div class="form-group">
              <label>教练</label>
              <el-select v-model="form.coachId" placeholder="请选择教练" style="width: 100%" clearable filterable>
                <el-option v-for="coach in coachList" :key="coach.id" :label="coach.name" :value="coach.id" />
              </el-select>
            </div>
            <div class="form-group">
              <label>上课时间</label>
              <el-date-picker v-model="form.courseTime" type="datetime" placeholder="选择上课时间" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
            </div>
            <div class="form-row">
              <div class="form-group" style="flex:1">
                <label>容量</label>
                <el-input-number v-model="form.capacity" :min="1" :max="100" style="width:100%" />
              </div>
              <div class="form-group" style="flex:1">
                <label>价格</label>
                <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
              </div>
            </div>
            <div class="form-group">
              <label>课程地点</label>
              <el-input v-model="form.location" placeholder="请输入课程地点" />
            </div>
            <div class="form-group">
              <label>状态</label>
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="可预约" value="AVAILABLE" />
                <el-option label="不可预约" value="UNAVAILABLE" />
              </el-select>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="dialogVisible = false">取消</button>
            <button class="btn-confirm" @click="handleSubmit">确定</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourses, addCourse, updateCourse, deleteCourse, getPendingCourses, auditCourse } from '../../api/course'
import { getCoaches } from '../../api/coach'

const searchKeyword = ref('')
const courses = ref([])
const coachList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showPending = ref(false)
const pendingCourses = ref([])
const pendingLoading = ref(false)
const pendingCount = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  courseName: '',
  coachId: null,
  coachName: '',
  courseTime: null,
  capacity: 20,
  location: '',
  price: 0,
  status: 'AVAILABLE'
})

watch(showPending, (val) => { if (val) loadPendingCourses() })

const filteredCourses = computed(() => {
  if (!searchKeyword.value) return courses.value
  return courses.value.filter(c =>
    c.courseName?.includes(searchKeyword.value) || c.coachName?.includes(searchKeyword.value)
  )
})

async function loadCourses() {
  loading.value = true
  try {
    const res = await getCourses(currentPage.value, pageSize.value)
    if (res.code === 200) {
      courses.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) { ElMessage.error('加载数据失败') } finally { loading.value = false }
}

async function loadCoachList() {
  try {
    const res = await getCoaches(1, 100)
    if (res.code === 200) { coachList.value = res.data.records || [] }
  } catch (error) { console.error('加载教练列表失败', error) }
}

function handleAdd() {
  isEdit.value = false
  form.value = { id: null, courseName: '', coachId: null, coachName: '', courseTime: null, capacity: 20, location: '', price: 0, status: 'AVAILABLE' }
  dialogVisible.value = true
}

function getStatusText(status) {
  const map = { AVAILABLE: '可预约', UNAVAILABLE: '不可预约' }
  return map[status] || status
}

function getStatusBadgeClass(status) {
  return status === 'AVAILABLE' ? 'available' : 'unavailable'
}

function getAuditStatusText(status) {
  const map = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝' }
  return map[status] || (status || '已通过')
}

function getAuditBadgeClass(status) {
  const map = { PENDING: 'pending', APPROVED: 'approved', REJECTED: 'rejected' }
  return map[status] || 'approved'
}

function formatDateTime(dateTime) {
  if (!dateTime) return '-'
  const d = new Date(dateTime)
  const y = d.getFullYear()
  const mo = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const mi = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${mo}-${day} ${h}:${mi}`
}

const avatarGradients = [
  'linear-gradient(135deg, #6366f1, #4f46e5)',
  'linear-gradient(135deg, #06b6d4, #0891b2)',
  'linear-gradient(135deg, #8b5cf6, #7c3aed)',
  'linear-gradient(135deg, #f59e0b, #d97706)',
  'linear-gradient(135deg, #10b981, #059669)',
  'linear-gradient(135deg, #ec4899, #db2777)',
]

function getAvatarStyle(name) {
  const hash = (name || '').split('').reduce((acc, c) => acc + c.charCodeAt(0), 0)
  return { background: avatarGradients[hash % avatarGradients.length] }
}

function handleEdit(row) { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

async function handleSubmit() {
  if (!form.value.courseName || !form.value.coachId) { ElMessage.warning('请填写完整信息'); return }
  try {
    const coach = coachList.value.find(c => c.id === form.value.coachId)
    form.value.coachName = coach?.name || ''
    if (isEdit.value) { await updateCourse(form.value.id, form.value); ElMessage.success('更新成功') }
    else { await addCourse(form.value); ElMessage.success('添加成功') }
    dialogVisible.value = false; loadCourses()
  } catch (error) { ElMessage.error('操作失败') }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await deleteCourse(row.id); ElMessage.success('删除成功'); loadCourses()
  } catch (error) { if (error !== 'cancel') ElMessage.error('删除失败') }
}

async function loadPendingCourses() {
  pendingLoading.value = true
  try {
    const res = await getPendingCourses(1, 100)
    if (res.code === 200) {
      const records = res.data.records || []
      records.forEach(c => { c.coachName = (coachList.value.find(co => co.id === c.coachId) || {}).name || '未知教练' })
      pendingCourses.value = records; pendingCount.value = res.data.total || 0
    }
  } catch (error) { ElMessage.error('加载待审核课程失败') } finally { pendingLoading.value = false }
}

async function handleAudit(row, auditStatus) {
  const action = auditStatus === 'APPROVED' ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定要${action}该课程吗？`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: auditStatus === 'APPROVED' ? 'success' : 'warning' })
    const res = await auditCourse(row.id, auditStatus)
    if (res.code === 200) { ElMessage.success(`课程已${action}`); loadPendingCourses(); loadCourses() }
  } catch (error) { if (error !== 'cancel') ElMessage.error('审核失败') }
}

onMounted(() => { loadCourses(); loadCoachList(); loadPendingCourses() })
</script>

<style scoped>
.course-management { padding: 20px; }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.page-header h1 { margin: 0; font-size: 22px; font-weight: 700; color: #1e293b; }

.header-actions { display: flex; gap: 10px; align-items: center; }

.btn-add, .btn-pending {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 9px 20px;
  border-radius: 12px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}

.btn-add {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
  color: #fff;
  box-shadow: 0 2px 10px rgba(99, 102, 241, 0.3);
}
.btn-add:hover { box-shadow: 0 4px 16px rgba(99, 102, 241, 0.4); transform: translateY(-1px); }

.btn-pending {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fcd34d;
}
.btn-pending:hover { background: #fde68a; }

.filter-bar { margin-bottom: 16px; }

/* ========== TABLE CARD ========== */
.table-card {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 4px 16px rgba(99, 102, 241, 0.06);
  overflow-x: auto;
  position: relative;
}

.native-table { width: 100%; min-width: 1050px; border-collapse: collapse; font-size: 13px; }

.native-table thead {
  background: linear-gradient(135deg, #eef2ff 0%, #e0e7ff 40%, #f0e6ff 100%);
  border-bottom: 2px solid #c7d2fe;
}

.native-table th {
  padding: 10px 8px;
  text-align: left;
  font-weight: 600;
  font-size: 12px;
  color: #4338ca;
  letter-spacing: 0.01em;
  user-select: none;
  vertical-align: middle;
}

.native-table th.th-num,
.native-table th.th-price,
.native-table th.th-status,
.native-table th.th-action {
  white-space: nowrap;
}

.native-table td { padding: 10px 8px; color: #374151; border-bottom: 1px solid #f1f5f9; vertical-align: middle; font-size: 13px; }

/* 列宽 */
.native-table th:nth-child(1), .native-table td:nth-child(1) { min-width: 120px; }
.native-table th:nth-child(2), .native-table td:nth-child(2) { min-width: 100px; }
.native-table th:nth-child(3), .native-table td:nth-child(3) { min-width: 120px; }
.native-table th:nth-child(4), .native-table td:nth-child(4) { min-width: 55px; }
.native-table th:nth-child(5), .native-table td:nth-child(5) { min-width: 60px; }
.native-table th:nth-child(6), .native-table td:nth-child(6) { min-width: 50px; }
.native-table th:nth-child(7), .native-table td:nth-child(7) { min-width: 90px; }
.native-table th:nth-child(8), .native-table td:nth-child(8) { min-width: 75px; }
.native-table th:nth-child(9), .native-table td:nth-child(9) { min-width: 75px; }
.native-table th:nth-child(10), .native-table td:nth-child(10) { min-width: 75px; }
.native-table th:nth-child(11), .native-table td:nth-child(11) { min-width: 130px; }

.native-table th.th-date, .native-table th.th-price, .native-table th.th-num,
.native-table th.th-status, .native-table th.th-action, .native-table td.td-center { text-align: center; }

.table-row { transition: background 0.2s ease, box-shadow 0.2s ease; }
.table-row:hover { background: #f8faff; box-shadow: inset 3px 0 0 #818cf8; }

/* ========== CELL STYLES ========== */
.cell-name { font-weight: 500; color: #1e293b; }

.cell-user { display: flex; align-items: center; gap: 8px; }

.coach-avatar {
  width: 30px; height: 30px; border-radius: 50%;
  color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 600; flex-shrink: 0;
}

.cell-mono { font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace; font-size: 11px; color: #64748b; }

.cell-num { font-weight: 500; font-size: 13px; }
.cell-num.cell-full { color: #dc2626; font-weight: 600; }

.cell-remaining { font-weight: 600; color: #059669; font-size: 13px; }
.cell-remaining.cell-none { color: #dc2626; }

.cell-location { color: #475569; font-size: 12px; }

.cell-price { font-weight: 600; color: #059669; font-size: 13px; }

/* ========== STATUS BADGES ========== */
.status-badge {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 3px 8px; border-radius: 16px; font-size: 11px; font-weight: 600;
  white-space: nowrap; border: 1px solid transparent;
}

.status-available { background: #d1fae5; color: #065f46; border-color: #a7f3d0; }
.status-unavailable { background: #fee2e2; color: #991b1b; border-color: #fecaca; }

.audit-pending { background: #fef3c7; color: #92400e; border-color: #fcd34d; }
.audit-approved { background: #d1fae5; color: #065f46; border-color: #a7f3d0; }
.audit-rejected { background: #fee2e2; color: #991b1b; border-color: #fecaca; }

/* ========== ACTION BUTTONS ========== */
.action-btns { display: flex; gap: 8px; justify-content: center; flex-wrap: nowrap; }

.btn-edit, .btn-delete, .btn-approve, .btn-reject {
  padding: 5px 12px; border-radius: 8px; border: none; font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease; white-space: nowrap;
}

.btn-edit { background: #ede9fe; color: #6d28d9; }
.btn-edit:hover { background: #6d28d9; color: #fff; box-shadow: 0 2px 8px rgba(109, 40, 217, 0.3); }

.btn-delete { background: #fee2e2; color: #dc2626; }
.btn-delete:hover { background: #dc2626; color: #fff; box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3); }

.btn-approve { background: #d1fae5; color: #065f46; }
.btn-approve:hover { background: #059669; color: #fff; box-shadow: 0 2px 8px rgba(5, 150, 105, 0.3); }

.btn-reject { background: #fee2e2; color: #dc2626; }
.btn-reject:hover { background: #dc2626; color: #fff; box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3); }

/* ========== PAGINATION ========== */
.pagination { display: flex; align-items: center; justify-content: center; gap: 14px; padding: 18px 24px; border-top: 1px solid #f1f5f9; }
.page-btn { padding: 7px 18px; border: 1px solid #cbd5e1; background: #fff; border-radius: 10px; font-size: 13px; font-weight: 500; color: #475569; cursor: pointer; transition: all 0.2s ease; }
.page-btn:hover:not(:disabled) { background: #f8fafc; border-color: #94a3b8; }
.page-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.page-info { font-size: 13px; color: #64748b; font-weight: 500; }

/* ========== LOADING & EMPTY ========== */
.loading-mask {
  position: absolute; inset: 0; background: rgba(255,255,255,0.8); backdrop-filter: blur(4px);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 12px; z-index: 10; border-radius: 16px; color: #6366f1; font-size: 14px;
}
.spinner { width: 32px; height: 32px; border: 3px solid #e0e7ff; border-top-color: #6366f1; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 48px 24px; gap: 12px; color: #94a3b8; font-size: 14px; }
.empty-state p { margin: 0; }

/* ========== MODAL DIALOG ========== */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(15,23,42,0.5); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center; z-index: 2000;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-card {
  background: #fff; border-radius: 20px; width: 500px; max-height: 90vh; overflow-y: auto;
  box-shadow: 0 24px 60px rgba(0,0,0,0.18), 0 8px 24px rgba(99,102,241,0.12);
  animation: slideUp 0.3s cubic-bezier(0.34,1.56,0.64,1);
}
.modal-wide { width: 800px; }

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 22px 28px 16px; border-bottom: 1px solid #f1f5f9; }
.modal-header h2 { margin: 0; font-size: 18px; font-weight: 700; background: linear-gradient(135deg, #6366f1, #8b5cf6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }

.modal-close { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 10px; border: 1px solid #e2e8f0; background: #f8fafc; color: #64748b; cursor: pointer; transition: all 0.2s ease; }
.modal-close:hover { background: #fee2e2; color: #dc2626; border-color: #fecaca; }

.modal-body { padding: 20px 28px 8px; display: flex; flex-direction: column; gap: 16px; }

.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 13px; font-weight: 600; color: #475569; letter-spacing: 0.01em; }
.form-row { display: flex; gap: 14px; }

.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 28px 22px; }

.btn-cancel, .btn-confirm { padding: 9px 24px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; border: none; }
.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-cancel:hover { background: #e2e8f0; }
.btn-confirm { background: linear-gradient(135deg, #6366f1, #4f46e5); color: #fff; box-shadow: 0 2px 10px rgba(99,102,241,0.3); }
.btn-confirm:hover { box-shadow: 0 4px 16px rgba(99,102,241,0.45); transform: translateY(-1px); }
</style>