<template>
  <div class="login-form-container">
    <div style="text-align: center;margin: 0 20px">
      <div style="margin-top: 150px">
        <div style="font-size: 25px;font-weight: bold">登录</div>
        <div style="font-size: 14px;color: grey">请输入用户名和密码以登录The Code论坛</div>
      </div>
      <div style="margin-top: 40px">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input v-model="form.username" maxlength="30" type="text" placeholder="用户名/邮箱" style="height: 50px;margin:10px">
              <template #prefix style="height: 50px;">
                <el-icon>
                  <User/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password" >
            <el-input v-model="form.password" type="password" maxlength="20" style="height: 50px;margin:10px;margin-top: 2px"
                      placeholder="密码">
              <template #prefix>
                <el-icon>
                  <Lock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-row style="margin-top: 5px;margin-left:10px">
            <el-col :span="12" style="text-align: left">
              <el-form-item prop="remember">
                <el-checkbox v-model="form.remember" label="记住我"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" style="text-align: right">
              <el-link @click="router.push('/forget')">忘记密码?</el-link>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div style="margin-top: 40px">
        <el-button @click="userLogin()" style="width: 270px" type="success" plain>登录</el-button>
      </div>
      <el-divider>
        <span style="color: grey;font-size: 13px">没有账号?</span>
      </el-divider>
      <div>
        <el-button style="width: 270px" @click="router.push('/register')" type="warning" plain>注册</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {User, Lock} from '@element-plus/icons-vue'
import router from "@/router";
import {reactive, ref} from "vue";
import {login} from '@/net'

const formRef = ref()
const form = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [
    {required: true, message: 'Please enter username'}
  ],
  password: [
    {required: true, message: 'Please enter password'}
  ]
}

function userLogin() {
  formRef.value.validate((isValid) => {
    if (isValid) {
      login(form.username, form.password, form.remember, () => router.push("/index"))
    }
  });
}
</script>

<style scoped>

/* 输入框样式 */
.el-input__inner {
  border-radius: 10px; /* 调整圆角边框为更方的形状 */
  height: 50px !important;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

/*!* 按钮样式 *!*/
/*.el-button {*/
/*  border-radius: 10px; !* 圆角边框 *!*/
/*  transition: all 0.3s ease;*/
/*}*/
/* 按钮样式调整 */
.el-button {
  border-radius: 10px; /* 调整为更方的圆角 */
  height: 40px; /* 增加按钮高度 */
  background: linear-gradient(to right, rgba(74, 144, 226, 0.6), rgba(115, 103, 240, 0.6)); /* 蓝紫渐变背景 */
  color: white;
  transition: all 0.3s ease;
  border-color: transparent; /* 移除按钮边框 */
}

.el-button:hover {
  transform: translateY(-2px); /* 悬停时上移 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}


/* 分隔线样式 */
.el-divider__text {
  background-color: white !important; /* 确保文本颜色与背景对比 */
  color: #888;
}

/* 添加边框的容器 */
.login-container {
  border: 1px solid #ccc; /* 灰色边框 */
  border-radius: 8px; /* 边框圆角 */
  padding: 20px; /* 容器内边距 */
  margin: 50px auto; /* 外边距自动，水平居中 */
  max-width: 400px; /* 最大宽度 */
  background: #fff; /* 背景色 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 轻微的阴影效果 */
}

/* 表单标题和说明文本 */
.login-container .title,
.login-container .description {
  margin-bottom: 20px; /* 与表单字段的间距 */
}

/* 表单字段样式 */
.login-container .el-form-item {
  margin-bottom: 15px; /* 表单项之间的间距 */
}

/* 按钮样式 */
.login-container .el-button {
  width: 100%; /* 按钮宽度占满容器 */
  padding: 10px; /* 按钮内边距 */
  font-size: 16px; /* 按钮文字大小 */
  border-radius: 2px;
}

</style>



