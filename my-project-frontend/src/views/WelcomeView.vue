<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1">
      <el-image style="width: 100%;height: 100%" fit="cover"
                src="https://dogefs.s3.ladydaily.com/~/source/unsplash/photo-1609270460854-e8ea8c0b591f?ixid=M3w0MjI2NjN8MHwxfHRvcGljfHxhZXU2ckwtajZld3x8fHx8Mnx8MTcxNjMyMTA0Nnw&ixlib=rb-4.0.3&w=2560&h=1440&fmt=webp"/>
    </div>
    <div class="welcome-title">
      <div style="font-family: Arial, sans-serif; font-size: 40px; font-weight: bold">Welcome to Ultraviolet</div>
      <div style="font-family: Arial, sans-serif; margin-top: 10px; font-size: 20px">
        A place where you can connect with
        other women who share
        interests in STEM, arts, and more.
      </div>
      <div style="font-family: Arial, sans-serif; margin-top: 10px">Explore Java
        programming, website
        development, and
        have meaningful
        discussions with industry experts.
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
  width: 400px;
  z-index: 1;
  background-color: var(--el-bg-color);
}

/*.welcome-title {*/
/*  position: absolute;*/
/*  bottom: 30px;*/
/*  left: 30px;*/
/*  color: white;*/
/*  text-shadow: 0 0 10px black;*/
/*}*/

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
