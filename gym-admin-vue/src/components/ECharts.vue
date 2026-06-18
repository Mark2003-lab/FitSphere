<template>
  <div ref="container" class="echarts-container"><slot/></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  options: { type: Object, required: true },
  theme: { type: [Object, String], default: null },
  autoResize: { type: Boolean, default: true }
})

const container = ref<HTMLElement | null>(null)
let chart: echarts.ECharts | null = null

function resize() {
  if (chart) chart.resize()
}

onMounted(() => {
  if (!container.value) return
  chart = echarts.init(container.value as HTMLElement, props.theme || undefined)
  chart.setOption(props.options)
  if (props.autoResize) window.addEventListener('resize', resize)
})

watch(() => props.options, (opt) => {
  if (chart && opt) chart.setOption(opt, true)
}, { deep: true })

onBeforeUnmount(() => {
  if (props.autoResize) window.removeEventListener('resize', resize)
  if (chart) { chart.dispose(); chart = null }
})
</script>

<style scoped>
.echarts-container {
  width: 100%;
  height: 100%;
}
</style>

