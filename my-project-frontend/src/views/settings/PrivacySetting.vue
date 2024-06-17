<script setup>
import Card from "@/components/Card.vue";
import {Setting} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";

// 创建一个包含密码表单的响应式对象
const form = reactive({
  password: '',
  new_password: '',
  new_password_repeat: ''
})

// Custom validation function to check if the two entered passwords match
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please enter the password again'))
  } else if (value !== form.new_password) {
    callback(new Error("The two passwords entered do not match"))
  } else {
    callback()
  }
}

// Define form validation rules
const rules = {
  password: [
    {required: true, message: 'Please enter the original password', trigger: 'blur'}
  ],
  new_password: [
    {required: true, message: 'Please enter the new password', trigger: 'blur'},
    {min: 6, max: 16, message: 'Password must be between 6 and 16 characters', trigger: ['blur']}
  ],
  new_password_repeat: [
    {required: true, message: 'Please enter the new password again', trigger: 'blur'},
    {validator: validatePassword, trigger: ['blur', 'change']},
  ]
}


// 创建表单的引用
const formRef = ref()

// 创建一个响应式引用，表示表单是否有效，默认为 false
const valid = ref(false)

// 定义一个函数，用于在表单验证时更新表单是否有效的状态
const onValidate = (prop, isValid) => valid.value = isValid

// 定义重置密码的函数
function resetPassword() {
  // 验证表单是否有效
  formRef.value.validate(valid => {
    // 如果表单有效
    if (valid) {
      // 发送请求重置密码
      post('/api/user/change-password', form, () => {
        // 显示成功消息
        ElMessage.success('修改密码成功！')
        // 重置表单字段
        formRef.value.resetFields();
      })
    }
  })
}


const saving = ref(true)
const privacy = reactive({
  phone: false,
  wx: false,
  qq: false,
  email: false,
  gender: false
})

// 发送 GET 请求获取用户隐私信息
get('/api/user/privacy', data => {
  // 更新隐私设置信息
  privacy.phone = data.phone
  privacy.email = data.email
  privacy.wx = data.wx
  privacy.qq = data.qq
  privacy.gender = data.gender
  // 设置保存状态为 false，表示保存完成
  saving.value = false
})

// 定义保存隐私设置的函数
function savePrivacy(type, status) {
  // 设置保存状态为 true，表示正在保存
  saving.value = true
  // 发送 POST 请求保存隐私设置
  post('/api/user/save-privacy', {
    type: type,
    status: status
  }, () => {
    // 显示成功消息
    ElMessage.success('隐私设置修改成功！')
    // 设置保存状态为 false，表示保存完成
    saving.value = false
  })
}

</script>

<template>
  <div style="margin:auto;max-width: 700px">
    <div style="margin-top: 20px">

      <!-- Privacy Settings -->
      <card :icon="Setting" title="隐私设置"
            desc="在这里设置哪些内容可以被其他人看到，请各位小伙伴注重自己的隐私"
            v-loading="saving">
        <div class="checkbox-list">
          <el-checkbox @change="savePrivacy('phone', privacy.phone)"
                       v-model="privacy.phone">公开展示我的手机号
          </el-checkbox>
          <el-checkbox @change="savePrivacy('email', privacy.email)"
                       v-model="privacy.email">公开展示我的邮箱
          </el-checkbox>
          <el-checkbox @change="savePrivacy('wx', privacy.wx)"
                       v-model="privacy.wx">公开展示我的微信
          </el-checkbox>
          <el-checkbox @change="savePrivacy('qq', privacy.qq)"
                       v-model="privacy.qq">公开展示我的QQ
          </el-checkbox>
          <el-checkbox @change="savePrivacy('gender', privacy.gender)"
                       v-model="privacy.gender">公开展示我的性别
          </el-checkbox>
        </div>
      </card>

      <!-- Reset Password -->
      <card style="margin: 20px 0" :icon="Setting"
            title="修改密码" desc="修改密码请在这里进行操作，请务必牢记您的密码">
        <el-form :rules="rules" :model="form" ref="formRef" @validate="onValidate" label-width="100"
                 style="margin: 20px">
          <el-form-item label="当前密码" prop="password" style="width: 90%">
            <el-input type="password" :prefix-icon="Lock" v-model="form.password"
                      placeholder="当前密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="新密码" prop="new_password" style="width: 90%">
            <el-input type="password" :prefix-icon="Lock" v-model="form.new_password"
                      placeholder="新密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="重复新密码" prop="new_password_repeat" style="width: 90%">
            <el-input type="password" :prefix-icon="Lock" v-model="form.new_password_repeat"
                      placeholder="重复新密码" maxlength="16"/>
          </el-form-item>
          <div style="text-align: center">
            <el-button @click="resetPassword" :icon="Switch" type="success">立即重置密码</el-button>
          </div>
        </el-form>
      </card>
    </div>
  </div>
</template>


<!--<style scoped>-->
<!--.checkbox-list {-->
<!--  margin: 10px 0 0 10px;-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--}-->
<!--</style>-->

<style scoped>
/* 统一卡片样式 */
.card {
  border: none;
  border-radius: 8px;
  overflow: hidden;
}

/* 表单样式 */
.el-form {
  width: 100%;
}

/* 输入框样式 */
.el-input__inner {
  border-radius: 4px;
}

/* 按钮样式 */
.el-button {
  border-radius: 4px;
  background-color: #409eff;
  border-color: #409eff;
  color: white;
  transition: background-color 0.3s, border-color 0.3s;
}

.el-button:hover {
  background-color: #3a8ee6;
  border-color: #3a8ee6;
}

/* 复选框列表样式 */
.checkbox-list {
  margin: 20px;
  display: flex;
  flex-direction: column;
}

/* 复选框项样式 */
.el-checkbox {
  margin-bottom: 10px;
}

/* 表单项标签样式 */
.el-form-item__label {
  color: #606266;
  font-size: 14px;
}

</style>