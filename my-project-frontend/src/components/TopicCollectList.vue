<script setup>
import {get} from "@/net";
import {ref} from "vue";
import LightCard from "@/components/LightCard.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import {ElMessage} from "element-plus";

defineProps({
  show: Boolean
})

const emit = defineEmits(['close'])

const list = ref([])

function init() {
  get('/api/forum/collects', data => list.value = data)
}

function deleteCollect(index, tid) {
  get(`/api/forum/interact?tid=${tid}&type=collect&state=false`, () => {
    ElMessage.success('已取消收藏！')
    list.value.splice(index, 1)
  })
}
</script>

<!--<template>-->
<!--  <el-drawer :model-value="show" @close="emit('close')" @open="init" title="我的收藏夹">-->
<!--    <div class="collect-list">-->
<!--      <light-card v-for="(item, index) in list" class="topic-card"-->
<!--                  @click="router.push(`/index/topic-detail/${item.id}`)">-->
<!--        <topic-tag :type="item.type"/>-->
<!--        <div class="title">-->
<!--          <b>{{ item.title }}</b>-->
<!--        </div>-->
<!--        <el-link type="danger" @click.stop="deleteCollect(index, item.id)">Delete</el-link>-->
<!--      </light-card>-->
<!--    </div>-->
<!--  </el-drawer>-->
<!--</template>-->
<!--<template>-->
<!--  <el-drawer :model-value="show" @close="emit('close')" @open="init" title="我的收藏夹">-->
<!--    <div class="collect-list">-->
<!--      <light-card v-for="(item, index) in list" :key="index" class="topic-card"-->
<!--                  @click="item.id ? router.push(`/index/topic-detail/${item.id}`) : null">-->
<!--        <topic-tag v-if="item.id" :type="item.type"/>-->
<!--        <div class="title" v-if="item.id">-->
<!--          <b v-if="item.id">{{ item.title }}</b>-->
<!--        </div>-->
<!--        <el-link v-if="item.id" type="danger" @click.stop="deleteCollect(index, item.id)">Delete</el-link>-->
<!--      </light-card>-->
<!--    </div>-->
<!--  </el-drawer>-->
<!--</template>-->
<!--<template>-->
<!--  <el-drawer :model-value="show" @close="emit('close')" @open="init" title="我的收藏夹">-->
<!--    <div class="collect-list">-->
<!--      <light-card v-for="(item, index) in list" :key="index" v-if="item.id" class="topic-card"-->
<!--                  @click="router.push(`/index/topic-detail/${item.id}`)">-->
<!--        <topic-tag :type="item.type"/>-->
<!--        <div class="title">-->
<!--          <b>{{ item.title }}</b>-->
<!--        </div>-->
<!--        <el-link type="danger" @click.stop="deleteCollect(index, item.id)">Delete</el-link>-->
<!--      </light-card>-->
<!--    </div>-->
<!--  </el-drawer>-->
<!--</template>-->
<template>
  <el-drawer :model-value="show" @close="emit('close')" @open="init" title="我的收藏夹">
    <div class="collect-list">
      <light-card v-for="(item, index) in list" :key="index" class="topic-card"
                  @click="item.id ? router.push(`/index/topic-detail/${item.id}`) : null">
        <topic-tag v-if="item.id" :type="item.type"/>
        <div class="title">
          <b v-if="item.id">{{ item.title }}</b>
          <b v-else>帖子已被删除</b>
        </div>
        <el-link v-if="item.id" type="danger" @click.stop="deleteCollect(index, item.id)">Delete</el-link>
      </light-card>
    </div>
  </el-drawer>
</template>


<style lang="scss" scoped>
.collect-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.topic-card {
  background-color: rgba(128, 128, 128, 0.2);
  transition: .3s;
  display: flex;
  justify-content: space-between;

  .title {
    margin-left: 5px;
    font-size: 14px;
    flex: 1;
    white-space: nowrap;
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &
  :hover {
    scale: 1.02;
    cursor: pointer;
  }

}
</style>


