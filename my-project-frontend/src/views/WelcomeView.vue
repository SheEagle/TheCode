<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1">
      <el-image style="width: 100%;height: 100%" fit="cover"
                src="/src/assets/cover.jpg"/>
    </div>
    <div class="welcome-title">
      <div style="font-family: Arial, sans-serif; font-size: 40px; font-weight: bold">欢迎来到The Code</div>
      <div style="font-family: Arial, sans-serif; margin-top: 10px; font-size: 20px">
        一个你可以结识STEM爱好者、从业者的论坛。
      </div>
      <div style="font-family: Arial, sans-serif; margin-top: 10px">探索科技前沿、交流学习心得，并与行业专家进行有意义的讨论。
      </div>
    </div>
    <div class="right-card">
      <router-view v-slot="{ Component }" :key="$route.fullPath">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component" style="height: 100%"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.right-card {
  width: 430px;
  z-index: 1;
  background-color: var(--el-bg-color);
}



.welcome-title {
  position: absolute;
  bottom: 50px; /* 增加底部间距 */
  left: 50px; /* 增加左侧间距 */
  color: white;
  text-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
}

/* 添加背景渐变 */
body, html {
  height: 100%;
  background: linear-gradient(to right, #6a11cb 0%, #2575fc 100%);
}
</style>
