<template>
  <div class="reservation-page">
    <div class="page-header">
      <h2>预约管理</h2>
      <el-button type="primary" @click="openAddModal">预约课程</el-button>
    </div>
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="memberName" label="会员" />
      <el-table-column prop="courseName" label="课程" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'warning'">
            {{ scope.row.status === 'ACTIVE' ? '已预约' : '已取消' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'ACTIVE'" type="danger" link @click="handleCancel(scope.row.id)">取消预约</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="currentPage" :page-sizes="[10, 20, 30]" :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>
    
    <el-dialog title="预约课程" :visible.sync="modalVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="会员">
          <el-select v-model="form.memberId">
            <el-option v-for="member in members" :key="member.id" :label="member.name" :value="member.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="form.courseId">
            <el-option v-for="course in courses" :key="course.id" :label="course.courseName" :value="course.id" />
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
import { getReservations, createReservation, cancelReservation } from '../api/reservation'
import { getMembers } from '../api/member'
import { getCourses } from '../api/course'

const tableData = ref([])
const members = ref([])
const courses = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const modalVisible = ref(false)
const form = ref({ memberId: '', courseId: '' })

onMounted(() => {
  loadData()
  loadMembers()
  loadCourses()
})

async function loadData() {
  const response = await getReservations(currentPage.value, pageSize.value)
  if (response.code === 200) {
    tableData.value = response.data.records.map(res => ({
      ...res,
      memberName: members.value.find(m => m.id === res.memberId)?.name || '未知',
      courseName: courses.value.find(c => c.id === res.courseId)?.courseName || '未知'
    }))
    total.value = response.data.total
  }
}

async function loadMembers() {
  const response = await getMembers(1, 100)
  if (response.code === 200) {
    members.value = response.data.records
  }
}

async function loadCourses() {
  const response = await getCourses(1, 100)
  if (response.code === 200) {
    courses.value = response.data.records
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
  form.value = { memberId: '', courseId: '' }
  modalVisible.value = true
}

async function handleSubmit() {
  try {
    await createReservation(form.value.memberId, form.value.courseId)
    ElMessage.success('预约成功')
    modalVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('预约失败')
  }
}

async function handleCancel(id) {
  if (confirm('确定要取消预约吗？')) {
    try {
      await cancelReservation(id)
      ElMessage.success('取消成功')
      loadData()
    } catch (error) {
      ElMessage.error('取消失败')
    }
  }
}
</script>

<style scoped>
.reservation-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
