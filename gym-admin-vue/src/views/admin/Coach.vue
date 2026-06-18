<template>
  <div class="coach-management">
    <div class="page-header">
      <h1>教练管理</h1>
      <button class="btn-add" @click="handleAdd">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        添加教练
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="filter-bar">
      <el-input v-model="searchKeyword" placeholder="搜索教练姓名或手机号" style="width: 260px" clearable />
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <table v-if="filteredCoaches.length > 0" class="native-table">
        <thead>
          <tr>
            <th>教练</th>
            <th>手机号</th>
            <th class="th-tags">专长</th>
            <th class="th-price">课时费</th>
            <th class="th-rating">评分</th>
            <th class="th-num">从业年限</th>
            <th>认证</th>
            <th>登录状态</th>
            <th class="th-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredCoaches" :key="row.id" class="table-row">
            <td>
              <div class="cell-user">
                <span class="user-avatar" :style="getAvatarStyle(row)">{{ row.name?.charAt(0) || '?' }}</span>
                <span class="cell-name">{{ row.name }}</span>
              </div>
            </td>
            <td><span class="cell-mono">{{ row.phone }}</span></td>
            <td class="td-center">
              <div class="speciality-tags">
                <span v-for="tag in splitTags(row.speciality)" :key="tag" class="tag-item">{{ tag }}</span>
              </div>
            </td>
            <td class="td-center">
              <span class="cell-price">¥{{ row.price || 0 }}</span>
            </td>
            <td class="td-center">
              <div class="rating-display">
                <span class="rating-stars">
                  <svg v-for="i in 5" :key="i" width="14" height="14" viewBox="0 0 24 24" :fill="i <= Math.round(row.rating || 5) ? '#f59e0b' : '#e2e8f0'" stroke="none"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                </span>
                <span class="rating-num">{{ (row.rating || 5).toFixed(1) }}</span>
              </div>
            </td>
            <td class="td-center">
              <span class="cell-exp">{{ row.experienceYears || 0 }} 年</span>
            </td>
            <td>
              <span class="cell-cert" :title="row.certification">{{ row.certification || '-' }}</span>
            </td>
            <td class="td-center">
              <span v-if="row.isLocked" class="lock-badge locked">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                已锁定
              </span>
              <span v-else class="lock-badge normal">正常</span>
            </td>
            <td class="td-center">
              <div class="action-btns">
                <button v-if="row.isLocked" class="btn-unlock" @click="handleUnlock(row)">解锁</button>
                <button class="btn-edit" @click="handleEdit(row)">编辑</button>
                <button class="btn-delete" @click="handleDelete(row)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="!loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        <p>{{ searchKeyword ? '未找到匹配的教练' : '暂无教练数据' }}</p>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--; loadCoaches()">上一页</button>
        <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="currentPage++; loadCoaches()">下一页</button>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <Teleport to="body">
      <div v-if="dialogVisible" class="modal-overlay" @click.self="dialogVisible = false">
        <div class="modal-card">
          <div class="modal-header">
            <h2>{{ isEdit ? '编辑教练' : '添加教练' }}</h2>
            <button class="modal-close" @click="dialogVisible = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>姓名</label>
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </div>
            <div class="form-group">
              <label>手机号</label>
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </div>
            <div class="form-group">
              <label>专长</label>
              <el-input v-model="form.speciality" placeholder="如：瑜伽、普拉提、力量增肌（多个用顿号分隔）" />
            </div>
            <div class="form-row">
              <div class="form-group" style="flex:1">
                <label>课时费</label>
                <el-input-number v-model="form.price" :min="0" :max="9999" :step="10" :precision="2" style="width:100%" />
              </div>
              <div class="form-group" style="flex:1">
                <label>从业年限</label>
                <el-input-number v-model="form.experienceYears" :min="0" :max="50" style="width:100%" />
              </div>
            </div>
            <div class="form-group">
              <label>认证信息</label>
              <el-input v-model="form.certification" placeholder="如：国家健身教练资格证" />
            </div>
            <div class="form-group">
              <label>个人描述</label>
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="教练简介，将展示给会员" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCoaches, addCoach, updateCoach, deleteCoach } from '../../api/coach'
import { getLoginSecurityInfo, unlockAccount } from '../../api/user'

const searchKeyword = ref('')
const coaches = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  name: '',
  phone: '',
  speciality: '',
  price: 0,
  certification: '',
  experienceYears: 0,
  description: ''
})

const filteredCoaches = computed(() => {
  if (!searchKeyword.value) return coaches.value
  return coaches.value.filter(c =>
    c.name?.includes(searchKeyword.value) || c.phone?.includes(searchKeyword.value)
  )
})

// 专长标签拆分（支持顿号、逗号、斜杠分隔）
function splitTags(speciality) {
  if (!speciality) return []
  return speciality.split(/[、，,/]/).map(s => s.trim()).filter(Boolean)
}

// 头像渐变颜色
const avatarGradients = [
  'linear-gradient(135deg, #6366f1, #4f46e5)',
  'linear-gradient(135deg, #06b6d4, #0891b2)',
  'linear-gradient(135deg, #8b5cf6, #7c3aed)',
  'linear-gradient(135deg, #f59e0b, #d97706)',
  'linear-gradient(135deg, #10b981, #059669)',
  'linear-gradient(135deg, #ec4899, #db2777)',
]

function getAvatarStyle(row) {
  const hash = (row.name || '').split('').reduce((acc, c) => acc + c.charCodeAt(0), 0)
  const idx = hash % avatarGradients.length
  return { background: avatarGradients[idx] }
}

async function loadCoaches() {
  loading.value = true
  try {
    const res = await getCoaches(currentPage.value, pageSize.value)
    if (res.code === 200) {
      coaches.value = res.data.records || []
      total.value = res.data.total || 0

      // 获取每个教练的登录安全状态
      for (const coach of coaches.value) {
        try {
          const securityRes = await getLoginSecurityInfo(coach.name)
          if (securityRes.code === 200 && securityRes.data) {
            coach.isLocked = securityRes.data.locked
          }
        } catch (e) {
          coach.isLocked = false
        }
      }
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

async function handleUnlock(row) {
  try {
    await ElMessageBox.confirm('确定要解锁该账号吗？解锁后用户可正常登录。', '提示', {
      confirmButtonText: '确定解锁',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await unlockAccount(row.name)
    ElMessage.success('解锁成功')
    row.isLocked = false
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('解锁失败')
    }
  }
}

function handleAdd() {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    phone: '',
    speciality: '',
    price: 0,
    certification: '',
    experienceYears: 0,
    description: ''
  }
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.value.name || !form.value.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (isEdit.value) {
      await updateCoach(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await addCoach(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadCoaches()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该教练吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCoach(row.id)
    ElMessage.success('删除成功')
    loadCoaches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadCoaches()
})
</script>

<style scoped>
.coach-management {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.page-header h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}

.btn-add {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 9px 20px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #6366f1, #4f46e5);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 2px 10px rgba(99, 102, 241, 0.3);
}

.btn-add:hover {
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.4);
  transform: translateY(-1px);
}

/* ========== FILTER BAR ========== */
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

/* ========== NATIVE TABLE ========== */
.native-table {
  width: 100%;
  min-width: 950px;
  border-collapse: collapse;
  font-size: 13px;
}

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
.native-table th.th-rate,
.native-table th.th-date,
.native-table th.th-status,
.native-table th.th-action {
  white-space: nowrap;
}

.native-table td {
  padding: 10px 8px;
  color: #374151;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
  font-size: 13px;
}

/* 列宽 */
.native-table th:nth-child(1),
.native-table td:nth-child(1) { min-width: 140px; }

.native-table th:nth-child(2),
.native-table td:nth-child(2) { min-width: 110px; }

.native-table th:nth-child(3),
.native-table td:nth-child(3) { min-width: 170px; }

.native-table th:nth-child(4),
.native-table td:nth-child(4) { min-width: 80px; }

.native-table th:nth-child(5),
.native-table td:nth-child(5) { min-width: 120px; }

.native-table th:nth-child(6),
.native-table td:nth-child(6) { min-width: 80px; }

.native-table th:nth-child(7),
.native-table td:nth-child(7) { min-width: 110px; }

.native-table th:nth-child(8),
.native-table td:nth-child(8) { min-width: 130px; }

/* 居中对齐 */
.native-table th.th-price,
.native-table th.th-rating,
.native-table th.th-num,
.native-table th.th-tags,
.native-table th.th-action,
.native-table td.td-center {
  text-align: center;
}

/* 行 hover */
.table-row { transition: background 0.2s ease, box-shadow 0.2s ease; }

.table-row:hover {
  background: #f8faff;
  box-shadow: inset 3px 0 0 #818cf8;
}

/* ========== CELL STYLES ========== */
.cell-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 700;
  flex-shrink: 0;
}

.cell-name { font-weight: 600; color: #1e293b; }

.cell-mono {
  font-family: 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  font-size: 13px;
  color: #64748b;
}

/* ========== SPECIALITY TAGS ========== */
.speciality-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.tag-item {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
  background: #ede9fe;
  color: #6d28d9;
  border: 1px solid #c4b5fd;
  white-space: nowrap;
}

/* ========== PRICE ========== */
.cell-price {
  font-weight: 600;
  color: #059669;
  font-size: 13px;
}

/* ========== RATING ========== */
.rating-display {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.rating-stars {
  display: flex;
  gap: 1px;
}

.rating-num {
  font-weight: 600;
  color: #1e293b;
  font-size: 12px;
  min-width: 24px;
}

/* ========== EXPERIENCE ========== */
.cell-exp {
  display: inline-flex;
  align-items: center;
  padding: 3px 10px;
  border-radius: 10px;
  background: #e0f2fe;
  color: #0369a1;
  font-size: 12px;
  font-weight: 600;
  border: 1px solid #bae6fd;
  white-space: nowrap;
}

/* ========== CERTIFICATION ========== */
.cell-cert {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: #475569;
}

/* ========== ACTION BUTTONS ========== */
.action-btns {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: nowrap;
}

.btn-edit, .btn-delete {
  padding: 5px 12px;
  border-radius: 8px;
  border: none;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-edit { background: #ede9fe; color: #6d28d9; }
.btn-edit:hover { background: #6d28d9; color: #fff; box-shadow: 0 2px 8px rgba(109, 40, 217, 0.3); }

.btn-delete { background: #fee2e2; color: #dc2626; }
.btn-delete:hover { background: #dc2626; color: #fff; box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3); }

/* ========== LOCK BADGE ========== */
.lock-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.lock-badge.normal {
  background: #dcfce7;
  color: #15803d;
  border: 1px solid #bbf7d0;
}

.lock-badge.locked {
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fecaca;
  font-weight: 600;
}

/* ========== UNLOCK BUTTON ========== */
.btn-unlock {
  padding: 5px 12px;
  border-radius: 8px;
  border: none;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  background: #fef3c7;
  color: #b45309;
  border: 1px solid #fcd34d;
}
.btn-unlock:hover { background: #f59e0b; color: #fff; box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3); }

/* ========== PAGINATION ========== */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  padding: 18px 24px;
  border-top: 1px solid #f1f5f9;
}

.page-btn {
  padding: 7px 18px;
  border: 1px solid #cbd5e1;
  background: #fff;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) { background: #f8fafc; border-color: #94a3b8; }
.page-btn:disabled { opacity: 0.35; cursor: not-allowed; }

.page-info { font-size: 13px; color: #64748b; font-weight: 500; }

/* ========== LOADING ========== */
.loading-mask {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  z-index: 10;
  border-radius: 16px;
  color: #6366f1;
  font-size: 14px;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e0e7ff;
  border-top-color: #6366f1;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* ========== EMPTY STATE ========== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  gap: 12px;
  color: #94a3b8;
  font-size: 14px;
}

.empty-state p { margin: 0; }

/* ========== MODAL DIALOG ========== */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.5);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-card {
  background: #ffffff;
  border-radius: 20px;
  width: 520px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.18), 0 8px 24px rgba(99, 102, 241, 0.12);
  animation: slideUp 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22px 28px 16px;
  border-bottom: 1px solid #f1f5f9;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.modal-close:hover { background: #fee2e2; color: #dc2626; border-color: #fecaca; }

.modal-body {
  padding: 20px 28px 8px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  letter-spacing: 0.01em;
}

.form-row {
  display: flex;
  gap: 14px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 28px 22px;
}

.btn-cancel, .btn-confirm {
  padding: 9px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-cancel {
  background: #f1f5f9;
  color: #475569;
}
.btn-cancel:hover { background: #e2e8f0; }

.btn-confirm {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
  color: #fff;
  box-shadow: 0 2px 10px rgba(99, 102, 241, 0.3);
}
.btn-confirm:hover { box-shadow: 0 4px 16px rgba(99, 102, 241, 0.45); transform: translateY(-1px); }
</style>