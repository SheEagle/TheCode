<template>
  <div class="login-form-container">
    <div style="text-align: center;margin: 0 20px">
      <div style="margin-top: 150px">
        <div style="font-size: 25px;font-weight: bold">Log in</div>
        <div style="font-size: 14px;color: grey">Please enter your username and password to log in the system.</div>
      </div>
      <div style="margin-top: 50px">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input v-model="form.username" maxlength="30" type="text" placeholder="用户名/邮箱">
              <template #prefix>
                <el-icon>
                  <User/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" maxlength="20" style="margin-top: 10px"
                      placeholder="密码">
              <template #prefix>
                <el-icon>
                  <Lock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-row style="margin-top: 5px">
            <el-col :span="12" style="text-align: left">
              <el-form-item prop="remember">
                <el-checkbox v-model="form.remember" label="Remember me"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" style="text-align: right">
              <el-link @click="router.push('/forget')">Forget the password?</el-link>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div style="margin-top: 40px">
        <el-button @click="userLogin()" style="width: 270px" type="success" plain>Log in now</el-button>
      </div>
      <el-divider>
        <span style="color: grey;font-size: 13px">no account?</span>
      </el-divider>
      <div>
        <el-button style="width: 270px" @click="router.push('/register')" type="warning" plain>Register</el-button>
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

/*!* 表单卡片 *!*/
/*.el-form {*/
/*  padding: 30px;*/
/*  background: white;*/
/*  border-radius: 10px;*/
/*  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);*/
/*}*/

/* 输入框样式 */
.el-input__inner {
  border-radius: 20px; /* 圆角边框 */
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 按钮样式 */
.el-button {
  border-radius: 20px; /* 圆角边框 */
  transition: all 0.3s ease;
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
}

</style>



