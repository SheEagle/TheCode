<script setup>
import LightCard from "@/components/LightCard.vue";
import {
  Calendar,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen,
  Link,
  Picture,
  Microphone, CircleCheck, Star, FolderOpened, ArrowRightBold
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";


const store = useStore()

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})
const editor = ref(false)
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  end: false,
  top: []
})

const collects = ref(false)

watch(() => topics.type, () => resetList(), {immediate: true})

const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})

get('/api/forum/top-topic', data => topics.top = data)

function updateList() {
  if (topics.end) return
  get(`/api/forum/list-topic?page=${topics.page}&type=${topics.type}`, data => {
    if (data) {
      data.forEach(d => topics.list.push(d))
      topics.page++
    }
    if (!data || data.length < 10)
      topics.end = true
  })
}

function onTopicCreate() {
  editor.value = false
  resetList()
}

function resetList() {
  topics.page = 0
  topics.end = false
  topics.list = []
  updateList()
}

// 使用浏览器的 geolocation API 获取当前位置信息
navigator.geolocation.getCurrentPosition(
    // 成功获取位置信息时的回调函数
    position => {
      // 提取经度和纬度信息
      const longitude = position.coords.longitude; // 经度
      const latitude = position.coords.latitude; // 纬度
      // 发送请求获取天气信息，并将返回的数据合并到 weather 对象中
      get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
        Object.assign(weather, data);
        weather.success = true; // 设置天气信息获取成功标志为 true
      });
    },
    // 获取位置信息失败时的回调函数
    error => {
      console.info(error); // 打印错误信息到控制台
      // 提示用户位置信息获取超时
      ElMessage.warning('位置信息获取超时，请检测网络设置');
      // 使用默认经纬度发送请求获取天气信息，并将返回的数据合并到 weather 对象中
      get(`/api/forum/weather?longitude=116.40529&latitude=39.90499`, data => {
        Object.assign(weather, data);
        weather.success = true; // 设置天气信息获取成功标志为 true
      });
    },
    // 配置选项，设置超时时间和精确度
    {
      timeout: 3000, // 超时时间，单位为毫秒，默认值为无穷大
      enableHighAccuracy: true // 是否尽可能获取精确的位置信息，默认为 false
    }
);


</script>

<template>
  <!-- 主容器 -->
  <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 900px">
    <!-- 左侧内容 -->
    <div style="flex: 1">
      <!-- 发表主题卡片 -->
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon>
            <EditPen/>
          </el-icon>
          点击发表主题...
        </div>
        <!-- 工具栏 -->
        <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
          <el-icon>
            <Edit/>
          </el-icon>
          <el-icon>
            <Document/>
          </el-icon>
          <el-icon>
            <Compass/>
          </el-icon>
          <el-icon>
            <Picture/>
          </el-icon>
          <el-icon>
            <Microphone/>
          </el-icon>
        </div>
      </light-card>

      <!-- 置顶主题列表 -->
      <light-card style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <div v-for="item in topics.top" class="top-topic" @click="router.push(`/index/topic-detail/${item.id}`)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{ item.title }}</div>
          <div>{{ new Date(item.time).toLocaleDateString() }}</div>
        </div>
      </light-card>

      <!-- 主题类型选择 -->
      <light-card style="margin-top: 10px;display: flex;gap: 7px">
        <div :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
             v-for="item in store.forum.types"
             @click="topics.type = item.id">
          <color-dot :color="item.color"/>
          <span style="margin-left: 5px">{{ item.name }}</span>
        </div>
      </light-card>

      <!-- 主题列表 -->
      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list.length">
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px"
               v-infinite-scroll="updateList">
            <light-card v-for="item in topics.list" class="topic-card"
                        @click="router.push('/index/topic-detail/'+item.id)">

              <!-- 作者信息 -->
              <div style="display: flex">
                <div>
                  <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar)"/>
                </div>
                <div style="margin-left: 7px;transform: translateY(-2px)">
                  <div style="font-size: 13px;font-weight: bold">{{ item.username }}</div>
                  <div style="font-size: 12px;color: grey">
                    <el-icon>
                      <Clock/>
                    </el-icon>
                    <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                      {{ new Date(item.time).toLocaleString() }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- 主题信息 -->
              <div style="margin-top: 5px">
                <topic-tag :type="item.type"/>
                <span style="font-weight: bold;margin-left: 7px">{{ item.title }}</span>
              </div>

              <!-- 主题内容 -->
              <div class="topic-content">{{ item.text }}</div>

              <!-- 图片列表 -->
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image class="topic-image" v-for="img in item.images" :src="img" fit="cover"></el-image>
              </div>

              <!-- 点赞和收藏信息 -->
              <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px;opacity: 0.8">
                <div>
                  <el-icon style="vertical-align: middle">
                    <CircleCheck/>
                  </el-icon>
                  {{ item.like }}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle">
                    <Star/>
                  </el-icon>
                  {{ item.collect }}收藏
                </div>
              </div>
            </light-card>
          </div>
        </div>
      </transition>
    </div>

    <!-- 右侧栏 -->
    <div style="width: 280px">
      <div style="position: sticky;top: 20px">
        <!-- 查看收藏按钮 -->
        <light-card>
          <div class="collect-list-button" @click="collects = true">
            <span><el-icon><FolderOpened/></el-icon> 查看我的收藏</span>
            <el-icon style="transform: translateY(3px)">
              <ArrowRightBold/>
            </el-icon>
          </div>
        </light-card>

        <!-- 论坛公告 -->
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon>
              <CollectionTag/>
            </el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;margin: 10px;color: grey">
            发言时请遵守论坛守则
          </div>
        </light-card>

        <!-- 天气信息 -->
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon>
              <Calendar/>
            </el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"/>
          <weather :data="weather"/>
        </light-card>

        <!-- 当前日期和IP地址 -->
        <light-card style="margin-top: 10px">
          <div class="info-text">
            <div>当前日期</div>
            <div>{{ today }}</div>
          </div>
          <div class="info-text">
            <div>当期IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>

        <!-- 友情链接 -->
        <div style="font-size: 14px;margin-top: 10px;color: grey">
          <el-icon>
            <Link/>
          </el-icon>
          友情链接
          <el-divider style="margin: 10px 0"/>
        </div>

        <!-- 友情链接列表 -->
        <div style="display: grid;grid-template-columns: repeat(2, 1fr);grid-gap: 10px;margin-top: 10px">
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/js-design-banner.jpg"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/vform-banner.png"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/sponsors/jnpfsoft.jpg"/>
          </div>
        </div>
      </div>
    </div>

    <!-- 发表主题编辑器 -->
    <topic-editor :show="editor" @success="onTopicCreate" @close="editor = false"/>

    <!-- 我的收藏列表 -->
    <topic-collect-list :show="collects" @close="collects = false"/>
  </div>
</template>


<style lang="less" scoped>
.collect-list-button {
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

  &:hover {
    cursor: pointer;
    opacity: 0.6;
  }
}

.top-topic {
  display: flex;

  div:first-of-type {
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
    transition: color .3s;

    &:hover {
      color: grey;
    }
  }

  div:nth-of-type(2) {
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }

  &:hover {
    cursor: pointer;
  }
}

.type-select-card {
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color .3s;

  &.active {
    border: solid 1px #ead4c4;
  }

  &:hover {
    cursor: pointer;
    background-color: #dadada;
  }
}

.topic-card {
  padding: 15px;
  transition: scale .3s;

  &:hover {
    scale: 1.015;
    cursor: pointer;
  }

  .topic-content {
    font-size: 13px;
    color: grey;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .topic-image {
    width: 100%;
    height: 100%;
    max-height: 110px;
    border-radius: 5px;
  }
}

.info-text {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}

.friend-link {
  border-radius: 5px;
  overflow: hidden;
}

.create-topic {
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover {
    cursor: pointer;
  }
}

.dark {
  .create-topic {
    background-color: #232323;
  }

  .type-select-card {
    background-color: #282828;

    &.active {
      border: solid 1px #64594b;
    }

    &:hover {
      background-color: #5e5e5e;
    }
  }
}
</style>
