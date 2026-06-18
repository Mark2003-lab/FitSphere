<template>
  <div class="plan-management">
    <div class="page-header">
      <h1>我的计划</h1>
      <button class="btn-add" @click="handleAdd">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        新建计划
      </button>
    </div>

    <div class="filter-bar">
      <el-input v-model="searchKeyword" placeholder="搜索计划名称或描述" style="width: 260px" clearable />
    </div>

    <div class="table-card">
      <div v-if="loading" class="loading-mask">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>

      <table v-if="filteredPlans.length > 0" class="native-table">
        <thead>
          <tr>
            <th>计划名称</th>
            <th>描述</th>
            <th class="th-date">创建时间</th>
            <th class="th-action">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredPlans" :key="row.id" class="table-row">
            <td>
              <div class="cell-plan">
                <span class="plan-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                </span>
                <span class="cell-name">{{ row.planName }}</span>
              </div>
            </td>
            <td><span class="cell-desc">{{ row.description || '-' }}</span></td>
            <td class="td-center"><span class="cell-mono">{{ row.createTime }}</span></td>
            <td class="td-center">
              <div class="action-btns">
                <button class="btn-view" @click="handleView(row)">查看</button>
                <button class="btn-edit" @click="handleEdit(row)">编辑</button>
                <button class="btn-delete" @click="handleDelete(row)">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="!loading" class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
        <p>暂无健身计划</p>
        <p class="hint">您可以在AI健身顾问中生成计划并保存到这里</p>
      </div>

      <div class="pagination" v-if="total > pageSize">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--; loadPlans()">上一页</button>
        <span class="page-info">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="currentPage++; loadPlans()">下一页</button>
      </div>
    </div>

    <!-- 添加/编辑弹窗 -->
    <Teleport to="body">
      <div v-if="dialogVisible" class="modal-overlay" @click.self="dialogVisible = false">
        <div class="modal-card">
          <div class="modal-header">
            <h2>{{ isEdit ? '编辑计划' : '新建计划' }}</h2>
            <button class="modal-close" @click="dialogVisible = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>计划名称</label>
              <el-input v-model="form.planName" placeholder="请输入计划名称" />
            </div>
            <div class="form-group">
              <label>描述</label>
              <el-input v-model="form.description" placeholder="请输入计划描述" type="textarea" :rows="2" />
            </div>
            <div class="form-group">
              <label>计划内容</label>
              <el-input v-model="form.content" placeholder="请输入计划内容" type="textarea" :rows="6" />
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="dialogVisible = false">取消</button>
            <button class="btn-confirm" @click="handleSubmit">确定</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 查看详情弹窗 -->
    <Teleport to="body">
      <div v-if="viewVisible" class="modal-overlay" @click.self="viewVisible = false">
        <div class="modal-card">
          <div class="modal-header">
            <h2>计划详情</h2>
            <button class="modal-close" @click="viewVisible = false">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body" v-if="viewPlan">
            <h3 class="plan-view-title">{{ viewPlan.planName }}</h3>
            <p v-if="viewPlan.description" class="plan-view-desc">{{ viewPlan.description }}</p>
            <div class="plan-view-content">{{ viewPlan.content }}</div>
            <div class="plan-view-meta">创建时间：{{ viewPlan.createTime }}</div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="viewVisible = false">关闭</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createPlan, updatePlan, deletePlan, getPlans } from '../../api/plan'

const searchKeyword = ref('')
const plans = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const viewVisible = ref(false)
const isEdit = ref(false)
const form = ref({ id: null, planName: '', description: '', content: '' })
const viewPlan = ref(null)

const filteredPlans = computed(() => {
  if (!searchKeyword.value) return plans.value
  return plans.value.filter(p => p.planName?.includes(searchKeyword.value) || p.description?.includes(searchKeyword.value))
})

async function loadPlans() {
  loading.value = true
  try {
    const res = await getPlans(currentPage.value, pageSize.value)
    if (res.code === 200) { plans.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch { ElMessage.error('加载数据失败') } finally { loading.value = false }
}

function handleAdd() { isEdit.value = false; form.value = { id: null, planName: '', description: '', content: '' }; dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; form.value = { ...row }; dialogVisible.value = true }
function handleView(row) { viewPlan.value = row; viewVisible.value = true }

async function handleSubmit() {
  if (!form.value.planName) { ElMessage.warning('请填写计划名称'); return }
  try {
    const api = isEdit.value ? updatePlan : createPlan
    await api(isEdit.value ? form.value.id : undefined, form.value)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false; loadPlans()
  } catch { ElMessage.error('操作失败') }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该计划吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await deletePlan(row.id); ElMessage.success('删除成功'); loadPlans()
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => loadPlans())
</script>

<style scoped>
.plan-management { padding: 24px; min-height: 100%; background: transparent; }

.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; }
.page-header h1 { margin: 0; font-size: 22px; font-weight: 700; color: #1e293b; }

.btn-add {
  display: flex; align-items: center; gap: 6px; padding: 9px 20px;
  border-radius: 12px; border: none; font-size: 14px; font-weight: 600; cursor: pointer;
  background: linear-gradient(135deg, #0891b2, #06b6d4); color: #fff;
  box-shadow: 0 2px 10px rgba(8,145,178,0.3); transition: all 0.25s ease;
}
.btn-add:hover { box-shadow: 0 4px 16px rgba(8,145,178,0.4); transform: translateY(-1px); }

.filter-bar { margin-bottom: 16px; }

/* ========== TABLE CARD ========== */
.table-card {
  background: rgba(255,255,255,0.85); backdrop-filter: blur(10px); border-radius: 16px; border: 1px solid rgba(8,145,178,0.1);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04), 0 4px 16px rgba(8,145,178,0.08);
  overflow-x: auto; position: relative;
}

/* ========== NATIVE TABLE ========== */
.native-table { width: 100%; border-collapse: collapse; font-size: 13px; }

.native-table thead {
  background: linear-gradient(135deg, #ecfeff 0%, #cffafe 40%, #f0fdfa 100%);
  border-bottom: 2px solid #22d3ee;
}

.native-table th {
  padding: 10px 12px; text-align: left; font-weight: 600; font-size: 12px;
  color: #0e7490; letter-spacing: 0.01em; user-select: none; vertical-align: middle;
}

.native-table td {
  padding: 10px 12px; color: #374151; border-bottom: 1px solid #f1f5f9;
  vertical-align: middle; font-size: 13px;
}

.native-table th.th-date, .native-table th.th-action,
.native-table td.td-center { text-align: center; }

.table-row { transition: background 0.2s ease, box-shadow 0.2s ease; }
.table-row:hover { background: #f0fdfa; box-shadow: inset 3px 0 0 #22d3ee; }

/* ========== CELL STYLES ========== */
.cell-plan { display: flex; align-items: center; gap: 10px; }
.plan-icon {
  width: 32px; height: 32px; border-radius: 10px;
  background: linear-gradient(135deg, #0891b2, #22d3ee); color: #fff;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.cell-name { font-weight: 600; color: #1e293b; }
.cell-desc { color: #64748b; font-size: 12px; }
.cell-mono { font-family: 'SF Mono', 'Cascadia Code', monospace; font-size: 12px; color: #6b7280; }

/* ========== ACTION BUTTONS ========== */
.action-btns { display: flex; gap: 6px; justify-content: center; flex-wrap: nowrap; }
.btn-view, .btn-edit, .btn-delete {
  padding: 5px 12px; border-radius: 8px; border: none; font-size: 12px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease; white-space: nowrap;
}
.btn-view { background: #cffafe; color: #0891b2; }
.btn-view:hover { background: #0891b2; color: #fff; box-shadow: 0 2px 8px rgba(8,145,178,0.3); }
.btn-edit { background: #dcfce7; color: #16a34a; }
.btn-edit:hover { background: #16a34a; color: #fff; box-shadow: 0 2px 8px rgba(22,163,74,0.3); }
.btn-delete { background: #fee2e2; color: #dc2626; }
.btn-delete:hover { background: #dc2626; color: #fff; box-shadow: 0 2px 8px rgba(220,38,38,0.3); }

/* ========== PAGINATION ========== */
.pagination { display: flex; align-items: center; justify-content: center; gap: 14px; padding: 16px 24px; border-top: 1px solid #f1f5f9; }
.page-btn { padding: 7px 18px; border: 1px solid #cbd5e1; background: #fff; border-radius: 10px; font-size: 13px; font-weight: 500; color: #475569; cursor: pointer; transition: all 0.2s ease; }
.page-btn:hover:not(:disabled) { background: #f8fafc; border-color: #94a3b8; }
.page-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.page-info { font-size: 13px; color: #64748b; font-weight: 500; }

/* ========== LOADING & EMPTY ========== */
.loading-mask {
  position: absolute; inset: 0; background: rgba(255,255,255,0.8); backdrop-filter: blur(4px);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 12px; z-index: 10; border-radius: 16px; color: #0891b2; font-size: 14px;
}
.spinner { width: 32px; height: 32px; border: 3px solid #cffafe; border-top-color: #0891b2; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 48px 24px; gap: 12px; color: #94a3b8; font-size: 14px; }
.empty-state p { margin: 0; }
.empty-state .hint { font-size: 13px; color: #cbd5e1; }

/* ========== MODAL DIALOG ========== */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(15,23,42,0.5); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center; z-index: 2000;
  animation: fadeIn 0.2s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-card {
  background: #fff; border-radius: 20px; width: 520px; max-height: 85vh; overflow-y: auto;
  box-shadow: 0 24px 60px rgba(0,0,0,0.18), 0 8px 24px rgba(8,145,178,0.12);
  animation: slideUp 0.3s cubic-bezier(0.34,1.56,0.64,1);
}
@keyframes slideUp { from { opacity: 0; transform: translateY(30px) scale(0.96); } to { opacity: 1; transform: translateY(0) scale(1); } }

.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 22px 28px 16px; border-bottom: 1px solid #f1f5f9; }
.modal-header h2 { margin: 0; font-size: 18px; font-weight: 700; background: linear-gradient(135deg, #0891b2, #22d3ee); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }

.modal-close { display: flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: 10px; border: 1px solid #e2e8f0; background: #f8fafc; color: #64748b; cursor: pointer; transition: all 0.2s ease; }
.modal-close:hover { background: #fee2e2; color: #dc2626; border-color: #fecaca; }

.modal-body { padding: 20px 28px 8px; display: flex; flex-direction: column; gap: 16px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 13px; font-weight: 600; color: #475569; letter-spacing: 0.01em; }

.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 28px 22px; }
.btn-cancel, .btn-confirm { padding: 9px 24px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; transition: all 0.2s ease; border: none; }
.btn-cancel { background: #f1f5f9; color: #475569; }
.btn-cancel:hover { background: #e2e8f0; }
.btn-confirm { background: linear-gradient(135deg, #0891b2, #06b6d4); color: #fff; box-shadow: 0 2px 10px rgba(8,145,178,0.3); }
.btn-confirm:hover { box-shadow: 0 4px 16px rgba(8,145,178,0.45); transform: translateY(-1px); }

/* ========== VIEW DETAIL ========== */
.plan-view-title { font-size: 20px; font-weight: 700; color: #1e293b; margin: 0; }
.plan-view-desc { color: #64748b; margin: 8px 0 16px; padding-bottom: 16px; border-bottom: 1px solid #f1f5f9; }
.plan-view-content { white-space: pre-wrap; line-height: 1.8; color: #334155; max-height: 350px; overflow-y: auto; background: #f8fafc; border-radius: 10px; padding: 16px; }
.plan-view-meta { margin-top: 16px; font-size: 13px; color: #94a3b8; text-align: right; }
</style>