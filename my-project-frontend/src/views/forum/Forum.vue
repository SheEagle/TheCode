<script setup>
import {get} from "@/net";
import {useStore} from "@/store";

const store = useStore()

get('/api/forum/types', data => {
  const array = []
  array.push({name: '最热', id: -1, color: 'linear-gradient(45deg, white, red, orange, gold, green, blue)'})
  array.push({name: '最新', id: 0, color: 'linear-gradient(to right, #6a11cb, #2575fc)'})
  data.forEach(d => array.push(d))
  store.forum.types = array
})
</script>

<template>
  <div>
    <router-view v-slot="{ Component }" :key="$route.fullPath">
      <transition name="el-fade-in-linear" mode="out-in">
        <keep-alive include="TopicList">
          <component :is="Component"/>
        </keep-alive>
      </transition>
    </router-view>
    <el-backtop target=".main-content-page .el-scrollbar__wrap" :right="20" :bottom="70"/>
  </div>
</template>
