<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 100px">
      <div style="font-size: 25px;font-weight: bold">Register</div>
      <div style="font-size: 14px;color: grey">Welcome to register on our platform.</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" :maxlength="8" type="text" placeholder="Username">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" :maxlength="16" type="password" placeholder="Password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="Repeat Your Password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" type="email" placeholder="E-mail">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="17">
              <el-input v-model="form.code" :maxlength="6" type="text" placeholder="Verify Code">
                <template #prefix>
                  <el-icon>
                    <EditPen/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="5">
              <el-button type="success" @click="validateEmail"
                         :disabled="!isEmailValid || coldTime > 0">
                {{ coldTime > 0 ? 'Please wait ' + coldTime + ' seconds' : 'to get Verification Code' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style="margin-top: 80px">
      <el-button style="width: 270px" type="warning" @click="register" plain>Register</el-button>
    </div>
    <div style="margin-top: 20px">
      <span style="font-size: 14px;line-height: 15px;color: grey">Already have account? </span>
      <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">Log in now</el-link>
    </div>
  </div>
</template>


<!--<template>-->
<!--  <div class="register-container">-->
<!--    <div class="register-form">-->
<!--      <div class="header">-->
<!--        <div class="title">Register</div>-->
<!--        <div class="description">Welcome to register on our platform.</div>-->
<!--      </div>-->
<!--      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef" class="form-content">-->
<!--        <el-form-item prop="username">-->
<!--          <el-input v-model="form.username" :maxlength="8" type="text" placeholder="Username">-->
<!--            <template #prefix>-->
<!--              <el-icon>-->
<!--                <User/>-->
<!--              </el-icon>-->
<!--            </template>-->
<!--          </el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item prop="password">-->
<!--          <el-input v-model="form.password" :maxlength="16" type="password" placeholder="Password">-->
<!--            <template #prefix>-->
<!--              <el-icon>-->
<!--                <Lock/>-->
<!--              </el-icon>-->
<!--            </template>-->
<!--          </el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item prop="password_repeat">-->
<!--          <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="Repeat Your Password">-->
<!--            <template #prefix>-->
<!--              <el-icon>-->
<!--                <Lock/>-->
<!--              </el-icon>-->
<!--            </template>-->
<!--          </el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item prop="email">-->
<!--          <el-input v-model="form.email" type="email" placeholder="E-mail">-->
<!--            <template #prefix>-->
<!--              <el-icon>-->
<!--                <Message/>-->
<!--              </el-icon>-->
<!--            </template>-->
<!--          </el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item prop="code">-->
<!--          <el-row :gutter="10" style="width: 100%">-->
<!--            <el-col :span="17">-->
<!--              <el-input v-model="form.code" :maxlength="6" type="text" placeholder="Verify Code">-->
<!--                <template #prefix>-->
<!--                  <el-icon>-->
<!--                    <EditPen/>-->
<!--                  </el-icon>-->
<!--                </template>-->
<!--              </el-input>-->
<!--            </el-col>-->
<!--            <el-col :span="5">-->
<!--              <el-button type="success" @click="validateEmail"-->
<!--                         :disabled="!isEmailValid || coldTime > 0">-->
<!--                {{ coldTime > 0 ? 'Please wait ' + coldTime + ' seconds' : 'to get Verification Code' }}-->
<!--              </el-button>-->
<!--            </el-col>-->
<!--          </el-row>-->
<!--        </el-form-item>-->
<!--      </el-form>-->
<!--      <el-button type="warning" @click="register" class="register-button">Register</el-button>-->
<!--      <div class="footer">-->
<!--        <span>Already have account? </span>-->
<!--        <el-link type="primary" @click="router.push('/')">Log in now</el-link>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<script setup>
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})


const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please enter your username'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('Username cannot contain special characters and must be in Chinese/English'))
  } else {
    callback()
  }
}


const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please re-enter your password'))
  } else if (value !== form.password) {
    callback(new Error("The two passwords entered are inconsistent"))
  } else {
    callback()
  }
}

const rules = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {
      min: 2,
      max: 8,
      message: 'The length of the username must be between 2 and 8 characters',
      trigger: ['blur', 'change']
    },
  ],
  password: [
    {required: true, message: 'Please enter your password', trigger: 'blur'},
    {
      min: 6,
      max: 16,
      message: 'The length of the password must be between 6 and 16 characters',
      trigger: ['blur', 'change']
    }
  ],
  password_repeat: [
    {validator: validatePassword, trigger: ['blur', 'change']},
  ],
  email: [
    {required: true, message: 'Please enter your email address', trigger: 'blur'},
    {type: 'email', message: 'Please enter a valid email address', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: 'Please enter the verification code you received', trigger: 'blur'},
  ]
}

const formRef = ref()
const isEmailValid = ref(false)
const coldTime = ref(0)

const onValidate = (prop, isValid) => {
  if (prop === 'email')
    isEmailValid.value = isValid
}

const register = () => {
  formRef.value.validate((isValid) => {
    if (isValid) {
      post('/api/auth/register', {
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code
      }, () => {
        ElMessage.success('Registration successful, welcome to join us')
        router.push("/")
      })
    } else {
      ElMessage.warning('Please complete the form!')
    }
  })
}

const validateEmail = () => {
  coldTime.value = 60
  get(`/api/auth/ask-code?email=${form.email}&type=register`, () => {
    ElMessage.success(`The verification code has been sent to your email: ${form.email}, please check it`)
    const handle = setInterval(() => {
      coldTime.value--
      if (coldTime.value === 0) {
        clearInterval(handle)
      }
    }, 1000)
  }, undefined, (message) => {
    ElMessage.warning(message)
    coldTime.value = 0
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
  box-sizing: border-box;
}

.register-form {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  padding: 30px;
  width: 100%;
  max-width: 400px;
}

.header {
  text-align: center;
  margin-bottom: 20px;
}

.title {
  font-size: 2em;
  font-weight: bold;
  margin-bottom: 8px;
}

.description {
  color: #666;
}

.form-content {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
  border-radius: 20px;
  padding: 10px;
  font-size: 1em;
  transition: background-color 0.3s;
}

.register-button:hover {
  background-color: #e6a23c;
}

.footer {
  margin-top: 15px;
  text-align: center;
  font-size: 0.9em;
}

/* 其他样式保持不变 */
/* ... */
</style>
