<template>
  <div class="coach-page">
    <div class="page-header">
      <h2>教练管理</h2>
      <el-button type="primary" @click="openAddModal">添加教练</el-button>
    </div>
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="联系方式" />
      <el-table-column prop="speciality" label="专长" />
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
    
    <el-dialog :title="isEdit ? '编辑教练' : '添加教练'" :visible.sync="modalVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="专长">
          <el-input v-model="form.speciality" />
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
import { getCoaches, addCoach, updateCoach, deleteCoach } from '../api/coach'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const modalVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '', phone: '', speciality: '' })

onMounted(() => {
  loadData()
})

async function loadData() {
  const response = await getCoaches(currentPage.value, pageSize.value)
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

function openAddModal() {
  isEdit.value = false
  form.value = { name: '', phone: '', speciality: '' }
  modalVisible.value = true
}

function openEditModal(row) {
  isEdit.value = true
  form.value = { id: row.id, name: row.name, phone: row.phone, speciality: row.speciality }
  modalVisible.value = true
}

async function handleSubmit() {
  try {
    if (isEdit.value) {
      await updateCoach(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await addCoach(form.value)
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
      await deleteCoach(id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.coach-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
