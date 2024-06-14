<script setup>
import {useStore} from "@/store";

const store = useStore()

defineProps({
  type: Number
})
</script>

<template>
  <div class="topic-type"
       :style="{
             color: store.findTypeById(type)?.color + 'EE',
             'border-color': store.findTypeById(type)?.color + '77',
             'background': store.findTypeById(type)?.color + '33'
         }">
    {{ store.findTypeById(type)?.name }}
  </div>
</template>

<style scoped>

.topic-type {
  display: inline-block;
  border: none; /* 移除边框 */
  border-radius: 10px; /* 增加更大的圆角 */
  font-family: 'Roboto', sans-serif; /* 使用现代字体 */
  font-size: 10px;
  padding: 5px 10px;
  height: auto;
  text-transform: uppercase; /* 文本大写 */
  letter-spacing: 1px; /* 字母间距 */
  transition: transform 0.3s ease, box-shadow 0.3s ease; /* 过渡效果 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 默认阴影 */
  color: black; /* 文本颜色 */
  background-size: 200% 100%; /* 渐变背景大小 */
  background-position: 100% 0; /* 渐变背景位置 */
  cursor: pointer; /* 鼠标悬停时显示手形图标 */
}

.topic-type:hover {
  background-position: 0 0; /* 鼠标悬停时渐变移动 */
  transform: translateY(-2px); /* 鼠标悬停时上移 */
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3); /* 鼠标悬停时更明显的阴影 */
}


</style>
