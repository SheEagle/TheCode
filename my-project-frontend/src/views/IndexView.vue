<script setup>
import {get, logout} from '@/net'
import router from "@/router";
import {useStore} from "@/store";
import {reactive, ref} from "vue";
import {
  Back,
  Bell,
  ChatDotSquare, Check, Collection, DataLine,
  Document, Files,
  Location, Lock, Message, Monitor,
  Operation,
  Position,
  Search,
  User
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
          <el-input v-model="searchInput.text" style="width: 100%;max-width: 500px" placeholder="Please Search...">
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
            <template #append>
              <el-select style="width: 120px" v-model="searchInput.type">
                <el-option value="1" label="Posts"/>
                <el-option value="2" label="ArtWorks"/>
              </el-select>
            </template>
          </el-input>
        </div>
        <!--<div class="search-container">-->
        <!--  <div class="search-box">-->
        <!--    <el-input v-model="searchInput.text" style="width: 100%;max-width: 500px" placeholder="Please Search...">-->
        <!--      <template #prefix>-->
        <!--        <el-icon>-->
        <!--          <Search/>-->
        <!--        </el-icon>-->
        <!--      </template>-->
        <!--      <template #append>-->
        <!--        <el-select style="width: 120px" v-model="searchInput.type">-->
        <!--          <el-option value="1" label="Posts"/>-->
        <!--          <el-option value="2" label="ArtWorks"/>-->
        <!--        </el-select>-->
        <!--      </template>-->
        <!--    </el-input>-->
        <!--    <el-button @click="searchPosts" type="primary">Search</el-button>-->
        <!--  </div>-->
        <!--  <div class="results-box">-->
        <!--    <el-card v-for="post in posts" :key="post.id" class="result-card">-->
        <!--      <h3>{{ post.title }}</h3>-->
        <!--      <p>{{ post.content }}</p>-->
        <!--    </el-card>-->
        <!--  </div>-->
        <!--</div>-->
        <div class="user-info">
          <el-popover placement="bottom" :width="350" trigger="click">
            <template #reference>
              <el-badge style="margin-right: 15px" is-dot :hidden="!notification.length">
                <div class="notification">
                  <el-icon>
                    <Bell></Bell>
                  </el-icon>
                  <div style="font-size: 10px">Notifications</div>
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
                         style="width: 100%" plain>Clear all
              </el-button>
            </div>
          </el-popover>
          <div class="profile">
            <div>{{ store.user.username }}</div>
            <div>{{ store.user.email }}</div>
          </div>
          <el-dropdown>
            <el-avatar :src="store.avatarUrl"/>
            <template #dropdown>
              <el-dropdown-item>
                <el-icon>
                  <Operation/>
                </el-icon>
                Personal Settings
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon>
                  <Message/>
                </el-icon>
                Notifications
              </el-dropdown-item>
              <el-dropdown-item @click="userLogout" divided>
                <el-icon>
                  <Back/>
                </el-icon>
                Logout
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
                  <span><b>Ultraviolet</b></span>
                </template>
                <el-menu-item index="/index">
                  <template #title>
                    <el-icon>
                      <ChatDotSquare/>
                    </el-icon>
                    Posts
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/ai-chat">
                  <template #title>
                    <el-icon>
                      <Bell/>
                    </el-icon>
                    Chat
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <!--<el-sub-menu index="2">-->
              <!--  <template #title>-->
              <!--    <el-icon>-->
              <!--      <Position/>-->
              <!--    </el-icon>-->
              <!--    <span><b>Imagination Markets</b></span>-->
              <!--  </template>-->
              <!--  <el-menu-item>-->
              <!--    <template #title>-->
              <!--      <el-icon>-->
              <!--        <Document/>-->
              <!--      </el-icon>-->
              <!--      Music-->
              <!--    </template>-->
              <!--  </el-menu-item>-->
              <!--  <el-menu-item>-->
              <!--    <template #title>-->
              <!--      <el-icon>-->
              <!--        <Files/>-->
              <!--      </el-icon>-->
              <!--      Painting-->
              <!--    </template>-->
              <!--  </el-menu-item>-->
              <!--  <el-menu-item>-->
              <!--    <template #title>-->
              <!--      <el-icon>-->
              <!--        <Monitor/>-->
              <!--      </el-icon>-->
              <!--      Photography-->
              <!--    </template>-->
              <!--  </el-menu-item>-->
              <!--  <el-menu-item>-->
              <!--    <template #title>-->
              <!--      <el-icon>-->
              <!--        <Collection/>-->
              <!--      </el-icon>-->
              <!--      Books-->
              <!--    </template>-->
              <!--  </el-menu-item>-->
              <!--  <el-menu-item>-->
              <!--    <template #title>-->
              <!--      <el-icon>-->
              <!--        <DataLine/>-->
              <!--      </el-icon>-->
              <!--      Scripts-->
              <!--    </template>-->
              <!--  </el-menu-item>-->
              <!--</el-sub-menu>-->
              <el-sub-menu index="3">
                <template #title>
                  <el-icon>
                    <Operation/>
                  </el-icon>
                  <span><b>Settings</b></span>
                </template>
                <el-menu-item index="/index/user-setting">
                  <template #title>
                    <el-icon>
                      <User/>
                    </el-icon>
                    Account
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/privacy-setting">
                  <template #title>
                    <el-icon>
                      <Lock/>
                    </el-icon>
                    Security
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

.el-card {
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-menu-item.is-active {
  background: linear-gradient(to right, rgba(106, 17, 203, 0.50), rgba(37, 117, 252, 0.50)) !important;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  color: #fff;
}

.main-content-header {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  border-bottom: none;
}

.main-content-header .logo,
.main-content-header .user-info .profile {
  color: #fff;
}

.main-content-header .user-info .notification:hover,
.main-content-header .user-info .el-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}

.main-content-header .el-input__inner {
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.3);
  color: #fff;
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
  background-color: #f7f8fa;
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

.main-content {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100vh;
  width: 100%;
}

//.main-content-header {
//  display: flex;
//  align-items: center;
//  background-color: #fff;
//  padding: 0 20px;
//}

.user-info {
  display: flex;
  align-items: center;
}

//.notification {
//  display: flex;
//  flex-direction: column;
//  align-items: center;
//}

//.profile {
//  margin-right: 15px;
//  text-align: right;
//}

.el-scrollbar {
  height: 100%;
}

.input-box {
  display: flex;
  padding: 10px;
  background-color: #fff;
}

input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
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
</style>
