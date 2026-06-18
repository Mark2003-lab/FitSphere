<template>
  <div class="course-page">
    <div class="page-header">
      <h2>课程管理</h2>
      <el-button type="primary" @click="openAddModal">添加课程</el-button>
    </div>
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="courseName" label="课程名称" />
      <el-table-column prop="coachName" label="教练" />
      <el-table-column prop="courseTime" label="上课时间" />
      <el-table-column prop="capacity" label="容量" />
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
    
    <el-dialog :title="isEdit ? '编辑课程' : '添加课程'" :visible.sync="modalVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="课程名称">
          <el-input v-model="form.courseName" />
        </el-form-item>
        <el-form-item label="教练">
          <el-select v-model="form.coachId">
            <el-option v-for="coach in coaches" :key="coach.id" :label="coach.name" :value="coach.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="上课时间">
          <el-date-picker v-model="form.courseTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input v-model="form.capacity" type="number" />
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
import { getCourses, addCourse, updateCourse, deleteCourse } from '../api/course'
import { getCoaches } from '../api/coach'

const tableData = ref([])
const coaches = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const modalVisible = ref(false)
const isEdit = ref(false)
const form = ref({ courseName: '', coachId: '', courseTime: '', capacity: 20 })

onMounted(() => {
  loadData()
  loadCoaches()
})

async function loadData() {
  const response = await getCourses(currentPage.value, pageSize.value)
  if (response.code === 200) {
    tableData.value = response.data.records.map(course => ({
      ...course,
      coachName: coaches.value.find(c => c.id === course.coachId)?.name || '未知'
    }))
    total.value = response.data.total
  }
}

async function loadCoaches() {
  const response = await getCoaches(1, 100)
  if (response.code === 200) {
    coaches.value = response.data.records
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
  form.value = { courseName: '', coachId: '', courseTime: '', capacity: 20 }
  modalVisible.value = true
}

function openEditModal(row) {
  isEdit.value = true
  form.value = { id: row.id, courseName: row.courseName, coachId: row.coachId, courseTime: row.courseTime, capacity: row.capacity }
  modalVisible.value = true
}

async function handleSubmit() {
  try {
    if (isEdit.value) {
      await updateCourse(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await addCourse(form.value)
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
      await deleteCourse(id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.course-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
