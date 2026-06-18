<template>
  <div class="test-container">
    <h1>粒子背景测试页面</h1>
    <p>如果粒子背景正常工作，你应该能看到紫色的粒子</p>
    <ParticlesBackground />
  </div>
</template>

<script setup>
import ParticlesBackground from '../components/ParticlesBackground.vue'
</script>

<style scoped>
.test-container {
  position: relative;
  width: 100%;
  height: 100vh;
  background: #ffffff;
}

h1 {
  position: relative;
  z-index: 10;
  text-align: center;
  padding-top: 50px;
  color: #333;
}

p {
  position: relative;
  z-index: 10;
  text-align: center;
  color: #666;
}
</style>
