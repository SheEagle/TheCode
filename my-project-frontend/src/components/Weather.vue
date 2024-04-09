<script setup>
defineProps({
  data: Object
})
</script>

<template>
  <!-- 天气信息容器 -->
  <div style="height: 160px" v-loading="!data.success"
       element-loading-text="正在加载天气信息...">
    <!-- 天气信息内容 -->
    <div style="display: flex;justify-content: space-between;margin: 10px 20px"
         v-if="data.success">
      <!-- 天气图标和温度 -->
      <div style="font-size: 45px;color: grey">
        <i :class="`qi-${data.now.icon}-fill`"></i>
      </div>
      <div style="font-weight: bold;text-align: center">
        <div style="font-size: 25px">{{ data.now.temp }}℃</div>
        <div style="font-size: 15px">{{ data.now.text }}</div>
      </div>

      <!-- 地理位置信息 -->
      <div style="margin-top: 13px">
        <div style="font-size: 15px">{{ `${data.location.country} ${data.location.adm1}` }}</div>
        <div style="font-size: 14px;color: grey">{{ `${data.location.adm2} ${data.location.name}区` }}</div>
      </div>
    </div>

    <!-- 分隔线 -->
    <el-divider style="margin: 10px 0"/>

    <!-- 小时天气信息 -->
    <div style="display: grid;grid-template-columns: repeat(5, 1fr);text-align: center">
      <!-- 循环展示每小时天气信息 -->
      <div v-for="item in data.hourly">
        <!-- 小时数 -->
        <div style="font-size: 13px">{{ new Date(item.fxTime).getHours() }} 时</div>
        <!-- 天气图标 -->
        <div style="font-size: 23px">
          <i :class="`qi-${item.icon}-fill`"></i>
        </div>
        <!-- 温度 -->
        <div style="font-size: 12px">{{ item.temp }}℃</div>
      </div>
    </div>
  </div>
</template>


<style scoped>

</style>
