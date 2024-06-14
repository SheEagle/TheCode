<script setup>

import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store";
import {computed, reactive, ref} from "vue";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import axios from "axios";

const store = useStore()

const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())

const introduction = ref('')
const baseFormRef = ref()
const emailFormRef = ref()
const baseForm = reactive({
  username: '',
  gender: 1,
  phone: '',
  qq: '',
  wx: '',
  introduction: ''
})

const emailForm = reactive({
  email: '',
  code: ''
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please enter your username'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('Username cannot contain special characters, only Chinese/English characters'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 2, max: 10, message: 'Username must be between 2 and 10 characters long', trigger: ['blur', 'change']},
  ],
  email: [
    {required: true, message: 'Please enter your email address', trigger: 'blur'},
    {type: 'email', message: 'Please enter a valid email address', trigger: ['blur', 'change']}
  ]
}


const loading = reactive({
  form: true,
  base: false
})

function saveDetails() {
  // 验证表单是否有效
  baseFormRef.value.validate(isValid => {
    // 如果表单有效
    if (isValid) {
      // 设置加载状态为true，表示正在保存用户信息
      loading.base = true;
      // 发送 POST 请求保存用户信息
      post('/api/user/save-details', baseForm, () => {
        // 保存成功后显示成功消息
        ElMessage.success('用户信息保存成功');
        // 更新 Vuex 中的用户名信息
        store.user.usernamew = baseForm.username;
        // 更新简介信息（如果有的话）
        introduction.value = baseForm.introduction;
        // 设置加载状态为false，表示保存完成
        loading.base = false;
      }, (message) => {
        // 如果保存失败，显示警告消息
        ElMessage.warning(message);
        // 设置加载状态为false，表示保存完成
        loading.base = false;
      });
    }
  });
}


get('/api/user/details', data => {
  baseForm.username = store.user.username
  baseForm.gender = data.gender
  baseForm.phone = data.phone
  baseForm.wx = data.wx
  baseForm.qq = data.qq
  baseForm.introduction = introduction.value = data.introduction
  emailForm.email = store.user.email
  loading.form = false
})

// 定义一个响应式引用，用于存储冷却时间
const coldTime = ref(0);

// 定义一个响应式引用，表示邮箱是否有效，默认为 true
const isEmailValid = ref(true);

// 定义一个函数，用于在验证邮箱时更新邮箱是否有效的状态
const onValidate = (prop, isValid) => {
  // 如果验证属性为 'email'
  if (prop === 'email')
      // 更新邮箱有效状态为 isValid
    isEmailValid.value = isValid;
}


function sendEmailCode() {
  // 验证邮箱表单是否有效
  emailFormRef.value.validate(isValid => {
    // 如果表单有效
    if (isValid) {
      // 设置冷却时间为 60 秒
      coldTime.value = 60;
      // 发送请求获取验证码
      get(`/api/auth/ask-code?email=${emailForm.email}&type=modify`, () => {
        // 显示成功消息，提示验证码已发送至邮箱
        ElMessage.success(`验证码已成功发送到邮箱：${emailForm.email}，请注意查收`);
        // 设置定时器，每秒减少冷却时间
        const handle = setInterval(() => {
          coldTime.value--;
          // 冷却时间倒计时完成时清除定时器
          if (coldTime.value === 0) {
            clearInterval(handle);
          }
        }, 1000);
      }, (message) => {
        // 如果请求失败，显示警告消息，并重置冷却时间为 0
        ElMessage.warning(message);
        coldTime.value = 0;
      });
    }
  });
}


function modifyEmail() {
  // 验证邮箱表单是否有效
  emailFormRef.value.validate(isValid => {
    // 如果表单有效
    if (isValid) {
      // 发送请求修改邮箱地址
      post('/api/user/modify-email', emailForm, () => {
        // 显示成功消息，提示邮件修改成功
        ElMessage.success('邮件修改成功');
        // 更新 Vuex 中的邮箱地址
        store.user.email = emailForm.email;
        // 清空验证码输入框
        emailForm.code = '';
      });
    }
  });
}


function beforeAvatarUpload(rawFile) {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像只能是 JPG/PNG 格式的')
    return false
  } else if (rawFile.size / 1024 > 100) {
    ElMessage.error('头像大小不能大于 100KB')
    return false
  }
  return true
}

function uploadSuccess(response) {
  ElMessage.success('头像上传成功')
  store.user.avatar = response.data
}
</script>

<template>
  <div style="display: flex;max-width: 1000px;margin: auto">
    <div class="settings-left">

      <!--账号信息设置-->
      <card :icon="User" title="Account Settings"
            desc="Edit your personal information here. You can choose whether to display this information in privacy settings."
            v-loading="loading.form">
        <el-form :model="baseForm" :rules="rules" ref="baseFormRef" label-position="top"
                 style="margin: 0 10px 10px 10px">
          <el-form-item label="Username" prop="username">
            <el-input v-model="baseForm.username" maxlength="10"/>
          </el-form-item>
          <el-form-item label="Gender">
            <el-radio-group v-model="baseForm.gender">
              <el-radio :label="0">Male</el-radio>
              <el-radio :label="1">Female</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="Phone" prop="phone">
            <el-input v-model="baseForm.phone" maxlength="11"/>
          </el-form-item>
          <el-form-item label="QQ" prop="qq">
            <el-input v-model="baseForm.qq" maxlength="13"/>
          </el-form-item>
          <el-form-item label="Wechat" prop="wx">
            <el-input v-model="baseForm.wx" maxlength="20"/>
          </el-form-item>
          <el-form-item label="Introduction" prop="introduction">
            <el-input v-model="baseForm.introduction" type="textarea" :rows="6" maxlength="200"/>
          </el-form-item>
          <div>
            <el-button :icon="Select" @click="saveDetails" :loading="loading.base"
                       type="success">Save User Profile
            </el-button>
          </div>
        </el-form>
      </card>

      <!--修改邮件地址-->
      <card style="margin-top: 10px" :icon="Message" title="Email Settings"
            desc="You can modify the default email here.">
        <el-form :rules="rules" @validate="onValidate" :model="emailForm" ref="emailFormRef"
                 label-position="top" style="margin: 0 10px 10px 10px">
          <el-form-item label="Email" prop="email">
            <el-input v-model="emailForm.email"/>
          </el-form-item>
          <el-form-item prop="code">
            <el-row style="width: 100%" :gutter="10">
              <el-col :span="18">
                <el-input placeholder="Please get verification code" v-model="emailForm.code"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success" style="width: 100%" :disabled="!isEmailValid || coldTime > 0"
                           @click="sendEmailCode" plain>
                  {{ coldTime > 0 ? `Please wait ${coldTime} seconds` : 'get code' }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <div>
            <el-button :icon="Refresh" type="success" @click="modifyEmail">Update Email</el-button>
          </div>
        </el-form>
      </card>
    </div>

    <div class="settings-right">
      <div style="position: sticky;top: 20px">
        <!--修改头像-->
        <card>
          <div style="text-align: center;padding: 5px 15px 0 15px">
            <el-avatar :size="70" :src="store.avatarUrl"/>
            <div style="margin: 5px 0">
              <el-upload
                  :action="axios.defaults.baseURL + '/api/image/avatar'"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :on-success="uploadSuccess"
                  :headers="accessHeader()">
                <el-button size="small" round>Update Avatar</el-button>
              </el-upload>
            </div>
            <div style="font-weight: bold">Hi, {{ store.user.username }}</div>
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;color: grey;padding: 10px">
            {{ introduction || '这个用户很懒，没有填写个人简介~' }}
          </div>
        </card>

        <!--账号注册时间-->
        <card style="margin-top: 10px;font-size: 14px">
          <div>Registration Time: {{ registerTime }}</div>
          <div style="color: grey">Welcome to Ultraviolet!</div>
        </card>
      </div>
    </div>
  </div>
</template>

<!--<style scoped>-->
<!--.settings-left {-->
<!--  flex: 1;-->
<!--  margin: 20px;-->
<!--}-->

<!--.settings-right {-->
<!--  width: 300px;-->
<!--  margin: 20px 30px 20px 0;-->
<!--}-->
<!--</style>-->
<style scoped>
.settings-left,
.settings-right {
  margin: 20px;
}

.settings-left {
  max-width: 600px; /* 限制最大宽度 */
}

.settings-right {
  flex: none; /* 不使用flex伸缩 */
}

/* 统一卡片样式 */
.card {
  border-radius: 10px; /* 圆角边框 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
  transition: box-shadow 0.3s; /* 阴影过渡效果 */
  overflow: hidden; /* 确保内容不会超出边框 */
}

.card:hover {
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2); /* 鼠标悬停时更深的阴影 */
}

/* 表单标题样式 */
.el-form {
  padding: 20px;
}

/* 表单元素间距 */
.el-form-item {
  margin-bottom: 20px;
}

/* 按钮样式 */
.el-button {
  border-radius: 20px; /* 圆角按钮 */
  padding: 10px 20px; /* 内边距 */
  font-weight: 500; /* 字体加粗 */
  transition: background-color 0.3s, transform 0.2s; /* 背景色和缩放过渡 */
}

.el-button:hover {
  background-color: #0055ff; /* 鼠标悬停时的背景色 */
  transform: translateY(-2px); /* 鼠标悬停时上移 */
}

/* 其他样式保持不变 */
/* ... */
</style>
