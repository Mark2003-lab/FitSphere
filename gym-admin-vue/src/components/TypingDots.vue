<template>
  <div class="typing-dots" aria-hidden="true">
    <span></span><span></span><span></span>
  </div>
</template>

<style scoped>
.typing-dots {
  display: inline-block;
  width: 40px;
  text-align: center;
}
.typing-dots span {
  display: inline-block;
  width: 8px;
  height: 8px;
  margin: 0 3px;
  background: rgba(0,0,0,0.25);
  border-radius: 50%;
  opacity: 0.6;
  transform: translateY(0);
  animation: td 1s infinite ease-in-out;
}
.typing-dots span:nth-child(2) { animation-delay: 0.15s }
.typing-dots span:nth-child(3) { animation-delay: 0.3s }

@keyframes td {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.6 }
  30% { transform: translateY(-6px); opacity: 1 }
}
</style>

