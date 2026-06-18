<template>
  <div class="equipment-page">
    <div class="page-header">
      <h2>器材管理</h2>
      <el-button type="primary" @click="openAddModal">添加器材</el-button>
    </div>
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="name" label="器材名称" />
      <el-table-column prop="quantity" label="数量" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" link @click="openEditModal(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="currentPage" :page-sizes="[10, 20, 30]" :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>
    
    <el-dialog :title="isEdit ? '编辑器材' : '添加器材'" :visible.sync="modalVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="器材名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="form.quantity" type="number" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="正常" value="NORMAL" />
            <el-option label="维修中" value="REPAIRING" />
            <el-option label="已报废" value="SCRAPPED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="modalVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getEquipments, addEquipment, updateEquipment, deleteEquipment } from '../api/equipment'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const modalVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '', quantity: 1, status: 'NORMAL' })

onMounted(() => {
  loadData()
})

async function loadData() {
  const response = await getEquipments(currentPage.value, pageSize.value)
  if (response.code === 200) {
    tableData.value = response.data.records
    total.value = response.data.total
  }
}

function handleSizeChange(val) {
  pageSize.value = val
  loadData()
}

function handleCurrentChange(val) {
  currentPage.value = val
  loadData()
}

function getStatusType(status) {
  switch (status) {
    case 'NORMAL': return 'success'
    case 'REPAIRING': return 'warning'
    case 'SCRAPPED': return 'danger'
    default: return 'info'
  }
}

function getStatusText(status) {
  switch (status) {
    case 'NORMAL': return '正常'
    case 'REPAIRING': return '维修中'
    case 'SCRAPPED': return '已报废'
    default: return status
  }
}

function openAddModal() {
  isEdit.value = false
  form.value = { name: '', quantity: 1, status: 'NORMAL' }
  modalVisible.value = true
}

function openEditModal(row) {
  isEdit.value = true
  form.value = { id: row.id, name: row.name, quantity: row.quantity, status: row.status }
  modalVisible.value = true
}

async function handleSubmit() {
  try {
    if (isEdit.value) {
      await updateEquipment(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await addEquipment(form.value)
      ElMessage.success('添加成功')
    }
    modalVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(id) {
  if (confirm('确定要删除吗？')) {
    try {
      await deleteEquipment(id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.equipment-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
