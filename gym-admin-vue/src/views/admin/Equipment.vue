<template>
  <div class="equipment-management">
    <div class="page-header">
      <h1>器材管理</h1>
      <button class="btn-add" @click="handleAdd">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        添加器材
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="filter-bar">
      <el-input v-model="searchKeyword" placeholder="搜索器材名称" style="width: 260px" clearable />
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <table v-if="filteredEquipment.length > 0" class="native-table">
        <thead>
          <tr>
            <th>器材名称</th>
            <th class="th-num">数量</th>
            <th class="th-status">状态</th>
            <th class="th-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredEquipment" :key="row.id" class="table-row">
            <td>
              <div class="cell-user">
                <span class="equip-icon" :style="getEquipStyle(row.name || '')">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6.5 6.5h11M6.5 10.5h11M6.5 14.5h7"/><rect x="4" y="3" width="16" height="18" rx="2"/></svg>
                </span>
                <span class="cell-name">{{ row.name }}</span>
              </div>
            </td>
            <td class="td-center">
              <span class="cell-qty">{{ row.quantity }}</span>
            </td>
            <td class="td-center">
              <span class="status-badge" :class="'status-' + getStatusBadgeClass(row.status)">
                {{ getStatusText(row.status) }}
              </span>
            </td>
            <td class="td-center">
              <div class="action-btns">
                <button class="btn-edit" @click="handleEdit(row)">编辑</button>
                <button class="btn-repair" :disabled="row.status === 'REPAIRING'" @click="handleRepair(row)">报修</button>
                <button class="btn-delete" @click="handleDelete(row)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="!loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="4" y="3" width="16" height="18" rx="2"/><path d="M6.5 6.5h11M6.5 10.5h11M6.5 14.5h7"/></svg>
        <p>{{ searchKeyword ? '未找到匹配的器材' : '暂无器材数据' }}</p>
      </div>

      <div class="pagination" v-if="total > pageSize">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--; loadEquipment()">上一页</button>
        <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="currentPage++; loadEquipment()">下一页</button>
      </div>
    </div>

    <!-- 添加/编辑弹窗 -->
    <Teleport to="body">
      <div v-if="dialogVisible" class="modal-overlay" @click.self="dialogVisible = false">
        <div class="modal-card">
          <div class="modal-header">
            <h2>{{ isEdit ? '编辑器材' : '添加器材' }}</h2>
            <button class="modal-close" @click="dialogVisible = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>器材名称</label>
              <el-input v-model="form.name" placeholder="请输入器材名称" />
            </div>
            <div class="form-group">
              <label>数量</label>
              <el-input-number v-model="form.quantity" :min="1" :max="1000" style="width:100%" />
            </div>
            <div class="form-group">
              <label>状态</label>
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="正常" value="NORMAL" />
                <el-option label="维修中" value="REPAIRING" />
                <el-option label="已报废" value="SCRAPPED" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEquipments, addEquipment, updateEquipment, deleteEquipment } from '../../api/equipment'

const searchKeyword = ref('')
const equipmentList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: null, name: '', quantity: 1, status: 'NORMAL' })

const filteredEquipment = computed(() => {
  if (!searchKeyword.value) return equipmentList.value
  return equipmentList.value.filter(e => e.name?.includes(searchKeyword.value))
})

function getStatusText(status) {
  const texts = { NORMAL: '正常', REPAIRING: '维修中', SCRAPPED: '已报废' }
  return texts[status] || status
}

function getStatusBadgeClass(status) {
  const map = { NORMAL: 'normal', REPAIRING: 'repairing', SCRAPPED: 'scrapped' }
  return map[status] || 'normal'
}

const equipGradients = [
  'linear-gradient(135deg, #6366f1, #4f46e5)',
  'linear-gradient(135deg, #06b6d4, #0891b2)',
  'linear-gradient(135deg, #8b5cf6, #7c3aed)',
  'linear-gradient(135deg, #10b981, #059669)',
  'linear-gradient(135deg, #f59e0b, #d97706)',
  'linear-gradient(135deg, #ec4899, #db2777)',
]

function getEquipStyle(name) {
  const hash = (name || '').split('').reduce((acc, c) => acc + c.charCodeAt(0), 0)
  return { background: equipGradients[hash % equipGradients.length] }
}

async function loadEquipment() {
  loading.value = true
  try {
    const res = await getEquipments(currentPage.value, pageSize.value)
    if (res.code === 200) { equipmentList.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch (error) { ElMessage.error('加载数据失败') } finally { loading.value = false }
}

function handleAdd() {
  isEdit.value = false
  form.value = { id: null, name: '', quantity: 1, status: 'NORMAL' }
  dialogVisible.value = true
}

function handleEdit(row) { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }

function handleRepair(row) {
  ElMessageBox.confirm('确定要将此器材设置为维修中吗？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      await updateEquipment(row.id, { ...row, status: 'REPAIRING' })
      ElMessage.success('已提交报修'); loadEquipment()
    } catch (error) { ElMessage.error('操作失败') }
  }).catch(() => {})
}

async function handleSubmit() {
  if (!form.value.name) { ElMessage.warning('请填写器材名称'); return }
  try {
    if (isEdit.value) { await updateEquipment(form.value.id, form.value); ElMessage.success('更新成功') }
    else { await addEquipment(form.value); ElMessage.success('添加成功') }
    dialogVisible.value = false; loadEquipment()
  } catch (error) { ElMessage.error('操作失败') }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该器材吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await deleteEquipment(row.id); ElMessage.success('删除成功'); loadEquipment()
  } catch (error) { if (error !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => { loadEquipment() })
</script>

<style scoped>
.equipment-management { padding: 20px; }

.page-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 18px;
}

.page-header h1 { margin: 0; font-size: 22px; font-weight: 700; color: #1e293b; }

.btn-add {
  display: flex; align-items: center; gap: 6px;
  padding: 9px 20px; border-radius: 12px; border: none;
  font-size: 14px; font-weight: 600; cursor: pointer;
  background: linear-gradient(135deg, #6366f1, #4f46e5); color: #fff;
  box-shadow: 0 2px 10px rgba(99, 102, 241, 0.3);
  transition: all 0.25s ease;
}
.btn-add:hover { box-shadow: 0 4px 16px rgba(99, 102, 241, 0.4); transform: translateY(-1px); }

.filter-bar { margin-bottom: 16px; }

/* ========== TABLE CARD ========== */
.table-card {
  background: #ffffff; border-radius: 16px; border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 16px rgba(99,102,241,0.06);
  overflow-x: auto; position: relative;
}

/* ========== NATIVE TABLE ========== */
.native-table { width: 100%; min-width: 600px; border-collapse: collapse; font-size: 13px; }

.native-table thead {
  background: linear-gradient(135deg, #eef2ff 0%, #e0e7ff 40%, #f0e6ff 100%);
  border-bottom: 2px solid #c7d2fe;
}

.native-table th {
  padding: 10px 8px; text-align: left; font-weight: 600; font-size: 12px;
  color: #4338ca; letter-spacing: 0.01em; user-select: none; vertical-align: middle;
}

.native-table th.th-num, .native-table th.th-status, .native-table th.th-action { white-space: nowrap; }

.native-table td { padding: 10px 8px; color: #374151; border-bottom: 1px solid #f1f5f9; vertical-align: middle; font-size: 13px; }

/* 列宽 */
.native-table th:nth-child(1), .native-table td:nth-child(1) { min-width: 200px; }
.native-table th:nth-child(2), .native-table td:nth-child(2) { min-width: 80px; }
.native-table th:nth-child(3), .native-table td:nth-child(3) { min-width: 100px; }
.native-table th:nth-child(4), .native-table td:nth-child(4) { min-width: 210px; }

.native-table th.th-num, .native-table th.th-status, .native-table th.th-action,
.native-table td.td-center { text-align: center; }

.table-row { transition: background 0.2s ease, box-shadow 0.2s ease; }
.table-row:hover { background: #f8faff; box-shadow: inset 3px 0 0 #818cf8; }

/* ========== CELL STYLES ========== */
.cell-user { display: flex; align-items: center; gap: 10px; }

.cell-name { font-weight: 500; color: #1e293b; }

.equip-icon {
  width: 32px; height: 32px; border-radius: 10px; color: #fff;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.cell-qty {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 36px; padding: 2px 10px; border-radius: 12px;
  font-weight: 700; font-size: 14px; color: #4338ca;
  background: #e0e7ff; border: 1px solid #c7d2fe;
}

/* ========== STATUS BADGES ========== */
.status-badge {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 3px 10px; border-radius: 16px; font-size: 11px; font-weight: 600;
  white-space: nowrap; border: 1px solid transparent;
}

.status-normal { background: #d1fae5; color: #065f46; border-color: #a7f3d0; }
.status-repairing { background: #fef3c7; color: #92400e; border-color: #fcd34d; }
.status-scrapped { background: #fee2e2; color: #991b1b; border-color: #fecaca; }

/* ========== ACTION BUTTONS ========== */
.action-btns { display: flex; gap: 8px; justify-content: center; flex-wrap: nowrap; }

.btn-edit, .btn-repair, .btn-delete {
  padding: 5px 12px; border-radius: 8px; border: none; font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease; white-space: nowrap;
}

.btn-edit { background: #ede9fe; color: #6d28d9; }
.btn-edit:hover { background: #6d28d9; color: #fff; box-shadow: 0 2px 8px rgba(109,40,217,0.3); }

.btn-repair { background: #fef3c7; color: #92400e; }
.btn-repair:hover:not(:disabled) { background: #f59e0b; color: #fff; box-shadow: 0 2px 8px rgba(245,158,11,0.3); }
.btn-repair:disabled { opacity: 0.35; cursor: not-allowed; }

.btn-delete { background: #fee2e2; color: #dc2626; }
.btn-delete:hover { background: #dc2626; color: #fff; box-shadow: 0 2px 8px rgba(220,38,38,0.3); }

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
  background: #fff; border-radius: 20px; width: 480px; max-height: 90vh; overflow-y: auto;
  box-shadow: 0 24px 60px rgba(0,0,0,0.18), 0 8px 24px rgba(99,102,241,0.12);
  animation: slideUp 0.3s cubic-bezier(0.34,1.56,0.64,1);
}
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

.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 28px 22px; }
.btn-cancel, .btn-confirm { padding: 9px 24px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; border: none; }
.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-cancel:hover { background: #e2e8f0; }
.btn-confirm { background: linear-gradient(135deg, #6366f1, #4f46e5); color: #fff; box-shadow: 0 2px 10px rgba(99,102,241,0.3); }
.btn-confirm:hover { box-shadow: 0 4px 16px rgba(99,102,241,0.45); transform: translateY(-1px); }
</style>