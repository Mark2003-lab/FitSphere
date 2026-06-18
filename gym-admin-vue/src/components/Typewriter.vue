<template>
  <span class="typewriter" v-html="displayHtml"></span>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
const props = defineProps({
  text: { type: String, required: true },
  speed: { type: Number, default: 24 } // chars per second
})

const displayHtml = ref('')

function escapeHtml(str: string) {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

function typeText(full: string) {
  displayHtml.value = ''
  let i = 0
  const interval = 1000 / props.speed
  const timer = setInterval(() => {
    i++
    displayHtml.value = escapeHtml(full.slice(0, i)).replace(/\n/g, '<br/>')
    if (i >= full.length) clearInterval(timer)
  }, interval)
}

watch(() => props.text, (n) => {
  if (n == null) return
  typeText(n)
})

onMounted(() => { if (props.text) typeText(props.text) })
</script>

<style scoped>
.typewriter { white-space: pre-wrap; }
</style>

