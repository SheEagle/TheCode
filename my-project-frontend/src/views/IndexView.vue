<script setup>
import {get, logout} from '@/net'
import router from "@/router";
import {useStore} from "@/store";
import {reactive, ref} from "vue";
import {
  Back,
  Bell,
  ChatDotSquare, ChatLineRound, Check, Collection, DataLine,
  Document, Files, Grid,
  Location, Lock, Message, Monitor,
  Operation,
  Position,
  Search,
  User, View
} from "@element-plus/icons-vue";
import LightCard from "@/components/LightCard.vue";
import SiteFooter from "@/components/Footer.vue";

const store = useStore()
const loading = ref(true)

const searchInput = reactive({
  type: '1',
  text: ''
})

get('/api/user/info', (data) => {
  store.user = data
  loading.value = false
})

const notification = ref([])

const loadNotification =
    () => get('api/notification/list', data => notification.value = data)
loadNotification()

function confirmNotification(id, url) {
  get(`api/notification/delete?id=${id}`, () => {
    loadNotification()
    window.open(url)
  })
}

function deleteAllNotification() {
  get('api/notification/delete-all', loadNotification)
}

function userLogout() {
  logout(() => router.push("/"))
}


const isAsideCollapsed = ref(false);
const asideWidth = ref("230px");

const toggleAside = () => {
  isAsideCollapsed.value = !isAsideCollapsed.value;
  asideWidth.value = isAsideCollapsed.value ? "64px" : "230px";
};


</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="loading...">
    <el-container style="height: 100%" v-if="!loading">
      <!--上边栏-->
      <el-header class="main-content-header">
        <el-image class="logo" src="/src/assets/the.png" style="height: 50px;width: 100px"/>
        <div style="flex: 1;padding: 0 20px;text-align: center">
          <el-input v-model="searchInput.text" style="width: 100%;max-width: 500px" placeholder="搜索论坛相关内容...">
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
            <template #append>
              <el-select style="width: 120px" v-model="searchInput.type">
                <el-option value="1" label="帖子广场"/>
                <el-option value="2" label="校园活动"/>
                <el-option value="3" label="表白墙"/>
                <el-option value="4" label="教务通知"/>
              </el-select>
            </template>
          </el-input>
        </div>

        <div class="user-info">
          <el-popover placement="bottom" :width="350" trigger="click">
            <template #reference>
              <el-badge style="margin-right: 15px" is-dot :hidden="!notification.length">
                <div class="notification">
                  <el-icon>
                    <Bell></Bell>
                  </el-icon>
                  <div style="font-size: 10px">消息</div>
                </div>
              </el-badge>
            </template>

            <el-empty :image-size="80" description="No new notifications right now"
                      v-if="!notification.length"></el-empty>
            <el-scrollbar :max-height="500" v-else>
              <light-card v-for="item in notification" class="notification-item"
                          @click="confirmNotification(item.id,item.url)">
                <div>
                  <el-tag size="small" :type="item.type">消息</el-tag>&nbsp;
                  <span style="font-weight: bold">{{ item.title }}</span>
                </div>
                <el-divider style="margin: 7px 0 3px 0"></el-divider>
                <div style="font-size: 13px;color: grey">
                  {{ item.content }}
                </div>
              </light-card>
            </el-scrollbar>

            <div style="margin-top: 10px">
              <el-button size="small" type="info" :icon="Check" @click="deleteAllNotification"
                         style="width: 100%" plain>清除消息
              </el-button>
            </div>
          </el-popover>
          <div class="profile">
            <div>{{ store.user.username }}</div>
            <div>{{ store.user.email }}</div>
          </div>
          <el-dropdown>
            <!--<el-avatar :src="store.avatarUrl"/>-->
            <el-avatar
                :src="store.avatarUrl"
                :class="{ 'admin-border': store.isAdmin }">
            </el-avatar>
            <template #dropdown>
              <el-dropdown-item @click="userLogout" divided>
                <el-icon>
                  <Back/>
                </el-icon>
                退出登录
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!--左边栏-->
      <el-container>
        <el-aside :width="asideWidth">
          <el-button @click="toggleAside" icon="el-icon-menu"></el-button>
          <el-scrollbar style="height: calc(100vh - 55px)">
            <el-menu
                :collapse="isAsideCollapsed"
                router
                :default-active="$route.path"
                :default-openeds="['1', '2', '3']"
                style="min-height: calc(100vh - 55px)">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon>
                    <Location/>
                  </el-icon>
                  <span><b>The Code</b></span>
                </template>
                <el-menu-item index="/index">
                  <template #title>
                    <el-icon>
                      <View/>
                    </el-icon>
                    帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/ai-chat">
                  <template #title>
                    <el-icon>
                      <ChatLineRound/>
                    </el-icon>
                    AI问答
                    <el-tag style="margin-left: 10px" size="small">施工中……</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>

              <el-sub-menu index="3">
                <template #title>
                  <el-icon>
                    <Operation/>
                  </el-icon>
                  <span><b>设置</b></span>
                </template>
                <el-menu-item index="/index/user-setting">
                  <template #title>
                    <el-icon>
                      <User/>
                    </el-icon>
                    账号设置
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/privacy-setting">
                  <template #title>
                    <el-icon>
                      <Lock/>
                    </el-icon>
                    安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>
        </el-aside>
        <el-main class="main-content-page">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <router-view v-slot="{ Component }" :key="$route.fullPath">
              <transition name="el-fade-in-linear" mode="out-in">
                <component :is="Component" style="height: 100%"/>
              </transition>
            </router-view>
          </el-scrollbar>
        </el-main>
      </el-container>
      <site-footer/>
    </el-container>
  </div>
</template>


<style lang="less" scoped>
body {
  background: linear-gradient(to right, #6a11cb, #2575fc);
}

body {
  font-family: 'Roboto', sans-serif;
}

.notification-item {
  transition: .3s;

  &:hover {
    opacity: 0.7;
    cursor: pointer;
  }
}

.notification {
  font-size: 22px;
  line-height: 14px;
  text-align: center;
  transition: color .3s;

  &:hover {
    color: grey;
    cursor: pointer;
  }
}

.main-content-page {
  padding: 0;
  background-color: #f3f4f5;
}

.dark .main-content-page {
  background-color: #212225;
}

.main-content {
  height: 100vh;
  width: 100vw;
}

.main-content-header {
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  background: linear-gradient(135deg, rgba(106, 17, 203, 0.42), rgba(37, 117, 252, 0.42));

  .logo {
    height: 32px;
  }

  .user-info {
    display: flex;
    justify-content: flex-end;
    align-items: center;

    .el-avatar:hover {
      cursor: pointer;
    }

    .profile {
      text-align: right;
      margin-right: 20px;

      :first-child {
        font-size: 18px;
        font-weight: bold;
        line-height: 20px;
      }

      :last-child {
        font-size: 10px;
        color: grey;
      }
    }
  }
}

button {
  padding: 10px 15px;
  margin-left: 10px;
  margin-right: 15px;
  background-color: #625DE5A5;
  border: none;
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
  width: 95%;
  height: 10px;
}


.admin-border {
  position: relative; /* 确保伪元素绝对定位相对于这个元素 */
  border: 6px solid mediumpurple; /* 边框颜色 */
  border-radius: 50%; /* 圆角 */
  display: inline-block;
  overflow: hidden; /* 确保内容不会溢出边框 */
  box-shadow: 0 0 10px rgba(128, 0, 128, 0.5), /* 外部阴影 */
  0 0 20px rgba(128, 0, 128, 0.4), /* 外部阴影 */
  inset 0 0 5px rgba(255, 255, 255, 0.6); /* 内部高光 */
  transition: box-shadow 0.3s ease-in-out; /* 平滑过渡阴影效果 */
}

/* 添加一个伪元素来创建边框的发光效果 */
.admin-border::after {
  content: '';
  position: absolute;
  top: -50px;
  left: -50px;
  right: -50px;
  bottom: -50px;
  background: linear-gradient(45deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0));
  border-radius: 50%;
  z-index: -1;
  animation: glow 5s infinite alternate;
}

/* 动画效果 */
@keyframes glow {
  from {
    transform: scale(1);
    opacity: 0.6;
  }
  to {
    transform: scale(1.5);
    opacity: 0;
  }
}

/* 鼠标悬停效果 */
.admin-border:hover {
  box-shadow: 0 0 20px mediumpurple, /* 更大的阴影 */
  0 0 30px mediumpurple,
  inset 0 0 10px rgba(255, 255, 255, 0.8);
}

/* 鼠标悬停时动画效果 */
.admin-border:hover::after {
  animation: none; /* 停止动画 */
}
</style>
