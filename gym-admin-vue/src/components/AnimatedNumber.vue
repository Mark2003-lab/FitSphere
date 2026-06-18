<template>
  <span class="animated-number" :class="{ 'prefix': prefix }">
    <span v-if="prefix" class="number-prefix">{{ prefix }}</span>
    <span class="number-value">{{ displayValue }}</span>
  </span>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  value: {
    type: [Number, String],
    default: 0
  },
  duration: {
    type: Number,
    default: 1500
  },
  decimals: {
    type: Number,
    default: 2
  },
  prefix: {
    type: String,
    default: ''
  }
})

const displayValue = ref('0.00')
let animationFrame = null

function animateValue(start, end, duration) {
  const startTime = performance.now()
  const diff = end - start

  function update(currentTime) {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    
    const easeOutQuart = 1 - Math.pow(1 - progress, 4)
    const current = start + diff * easeOutQuart
    
    displayValue.value = current.toFixed(props.decimals)
    
    if (progress < 1) {
      animationFrame = requestAnimationFrame(update)
    }
  }

  if (animationFrame) {
    cancelAnimationFrame(animationFrame)
  }
  animationFrame = requestAnimationFrame(update)
}

function parseValue(val) {
  if (typeof val === 'string') {
    const num = parseFloat(val)
    return isNaN(num) ? 0 : num
  }
  return Number(val) || 0
}

watch(() => props.value, (newVal) => {
  const currentNum = parseFloat(displayValue.value) || 0
  const targetNum = parseValue(newVal)
  animateValue(currentNum, targetNum, props.duration)
})

onMounted(() => {
  const targetNum = parseValue(props.value)
  animateValue(0, targetNum, props.duration)
})
</script>

<style scoped>
.animated-number {
  font-variant-numeric: tabular-nums;
}
.number-prefix {
  margin-right: 2px;
}
.number-value {
  font-weight: bold;
}
</style>