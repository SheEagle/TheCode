<!--<template>-->
<!--  <div class="chat-container">-->
<!--    <div class="chat-box">-->
<!--      <el-scrollbar class="scrollbar">-->
<!--        <div v-for="(message, index) in messages" :key="index" class="message">-->
<!--          <el-card :class="{'user-message': message.user, 'ai-message': !message.user}" shadow="never">-->
<!--            <div class="message-content">{{ message.content }}</div>-->
<!--            <div class="timestamp">{{ formatDate(message.time) }}</div>-->
<!--          </el-card>-->
<!--        </div>-->
<!--      </el-scrollbar>-->
<!--    </div>-->
<!--    <div class="input-box">-->
<!--      <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>-->
<!--      <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>-->
<!--    </div>-->
<!--    <el-loading :fullscreen="true" :loading="loading" text="AI is thinking..."></el-loading>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import {ref} from 'vue';-->
<!--import {ElMessage} from 'element-plus';-->
<!--import {post} from '@/net';  // 使用你封装好的 post 方法-->

<!--const question = ref('');-->
<!--const messages = ref([]);-->
<!--const loading = ref(false);-->

<!--const askQuestion = () => {-->
<!--  if (!question.value.trim()) {-->
<!--    ElMessage.warning('Please enter a question.');-->
<!--    return;-->
<!--  }-->

<!--  const userMessage = {user: true, content: question.value, time: new Date()};-->
<!--  messages.value.push(userMessage);-->

<!--  loading.value = true;-->

<!--  post('/api/chat/ask', {question: question.value}, (data) => {-->
<!--    const aiMessage = {-->
<!--      user: false,-->
<!--      content: data.answer,-->
<!--      time: new Date(data.time)-->
<!--    };-->
<!--    messages.value.push(aiMessage);-->
<!--    loading.value = false;-->
<!--  }, (message, code, url) => {-->
<!--    console.warn(`Request failed: ${url}, status: ${code}, message: ${message}`);-->
<!--    ElMessage.error('Failed to get response from AI.');-->
<!--    loading.value = false;-->
<!--  });-->

<!--  question.value = '';-->
<!--};-->

<!--const formatDate = (date) => {-->
<!--  const d = new Date(date);-->
<!--  return `${d.getHours()}:${d.getMinutes()}:${d.getSeconds()}`;-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--.chat-container {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  justify-content: space-between;-->
<!--  height: 100vh;-->
<!--  width: 100%;-->
<!--  background-color: #1e1e1e;-->
<!--  color: #ffffff;-->
<!--}-->

<!--.chat-box {-->
<!--  flex: 1;-->
<!--  padding: 10px;-->
<!--  background-color: #2c2c2c;-->
<!--  border-radius: 10px;-->
<!--  margin: 10px;-->
<!--}-->

<!--.scrollbar {-->
<!--  height: calc(100% - 20px);-->
<!--  padding-right: 10px;-->
<!--}-->

<!--.message {-->
<!--  margin-bottom: 10px;-->
<!--}-->

<!--.user-message {-->
<!--  text-align: right;-->
<!--  background-color: #007bff;-->
<!--  color: #fff;-->
<!--  border-radius: 15px;-->
<!--  padding: 10px;-->
<!--}-->

<!--.ai-message {-->
<!--  text-align: left;-->
<!--  background-color: #4caf50;-->
<!--  color: #fff;-->
<!--  border-radius: 15px;-->
<!--  padding: 10px;-->
<!--}-->

<!--.timestamp {-->
<!--  display: block;-->
<!--  font-size: 0.8em;-->
<!--  color: #ccc;-->
<!--  margin-top: 5px;-->
<!--}-->

<!--.input-box {-->
<!--  display: flex;-->
<!--  padding: 10px;-->
<!--  background-color: #2c2c2c;-->
<!--  border-radius: 10px;-->
<!--  margin: 10px;-->
<!--}-->

<!--.el-input__inner {-->
<!--  background-color: #3c3c3c;-->
<!--  border-color: #3c3c3c;-->
<!--  color: #fff;-->
<!--}-->

<!--.el-button {-->
<!--  margin-left: 10px;-->
<!--  background-color: #007bff;-->
<!--  border-color: #007bff;-->
<!--}-->

<!--.el-card {-->
<!--  margin-bottom: 10px;-->
<!--  padding: 10px;-->
<!--}-->

<!--.message-content {-->
<!--  white-space: pre-wrap;-->
<!--}-->

<!--.el-loading-mask {-->
<!--  background-color: rgba(0, 0, 0, 0.7);-->
<!--}-->
<!--</style>-->

<!--<template>-->
<!--  <div class="chat-container">-->
<!--    <div class="chat-box">-->
<!--      <el-scrollbar class="scrollbar">-->
<!--        <div v-for="(message, index) in messages" :key="index" class="message">-->
<!--          <el-card :class="{'user-message': message.user, 'ai-message': !message.user}" shadow="never">-->
<!--            <div class="message-content" v-if="message.user">{{ message.content }}</div>-->
<!--            <div class="message-content" v-else v-html="message.content"></div>-->
<!--            <div class="timestamp">{{ formatDate(message.time) }}</div>-->
<!--          </el-card>-->
<!--        </div>-->
<!--      </el-scrollbar>-->
<!--    </div>-->
<!--    <div class="input-box" >-->
<!--      <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>-->
<!--      <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>-->
<!--    </div>-->
<!--    <el-loading :fullscreen="true" :loading="loading" text="AI is thinking..."></el-loading>-->
<!--  </div>-->
<!--</template>-->

<!--<template>-->
<!--  <div class="chat-container">-->
<!--    <div class="chat-box">-->
<!--      <el-scrollbar class="scrollbar">-->
<!--        <div v-for="(message, index) in messages" :key="index" class="message">-->
<!--          <el-card :class="{'user-message': message.user, 'ai-message': !message.user}" shadow="never">-->
<!--            <div class="message-content" v-if="message.user">{{ message.content }}</div>-->
<!--            <div class="message-content" v-else v-html="message.content"></div>-->
<!--            <div class="timestamp">{{ formatDate(message.time) }}</div>-->
<!--          </el-card>-->
<!--        </div>-->
<!--      </el-scrollbar>-->
<!--    </div>-->
<!--    <div class="input-box">-->
<!--      <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>-->
<!--      <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>-->
<!--    </div>-->
<!--    <el-loading :fullscreen="true" :loading="loading" text="AI is thinking..."></el-loading>-->
<!--  </div>-->
<!--</template>-->


<!--<script setup>-->
<!--import {ref} from 'vue';-->
<!--import {ElMessage} from 'element-plus';-->
<!--import {post} from '@/net';  // 使用你封装好的 post 方法-->
<!--import {marked} from 'marked';  // 引入 marked 库-->

<!--const question = ref('');-->
<!--const messages = ref([]);-->
<!--const loading = ref(false);-->

<!--const askQuestion = () => {-->
<!--  if (!question.value.trim()) {-->
<!--    ElMessage.warning('Please enter a question.');-->
<!--    return;-->
<!--  }-->

<!--  const userMessage = {user: true, content: question.value, time: new Date()};-->
<!--  messages.value.push(userMessage);-->

<!--  loading.value = true;-->

<!--  post('/api/chat/ask', {question: question.value}, (data) => {-->
<!--    const aiMessage = {-->
<!--      user: false,-->
<!--      content: marked(data.answer),  // 使用 marked 解析 markdown 格式-->
<!--      time: new Date(data.time)-->
<!--    };-->
<!--    messages.value.push(aiMessage);-->
<!--    loading.value = false;-->
<!--  }, (message, code, url) => {-->
<!--    console.warn(`Request failed: ${url}, status: ${code}, message: ${message}`);-->
<!--    ElMessage.error('Failed to get response from AI.');-->
<!--    loading.value = false;-->
<!--  });-->

<!--  question.value = '';-->
<!--};-->

<!--const formatDate = (date) => {-->
<!--  const d = new Date(date);-->
<!--  return `${d.getHours()}:${d.getMinutes()}:${d.getSeconds()}`;-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--.chat-container {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  justify-content: space-between;-->
<!--  height: 100vh;-->
<!--  width: 100%;-->
<!--  background-color: #1e1e1e;-->
<!--  color: #ffffff;-->
<!--}-->

<!--.chat-box {-->
<!--  flex: 1;-->
<!--  padding: 10px;-->
<!--  background-color: #2c2c2c;-->
<!--  border-radius: 10px;-->
<!--  margin: 10px;-->
<!--}-->

<!--.scrollbar {-->
<!--  height: calc(100% - 20px);-->
<!--  padding-right: 10px;-->
<!--}-->

<!--.message {-->
<!--  margin-bottom: 10px;-->
<!--}-->

<!--.user-message {-->
<!--  text-align: right;-->
<!--  background: linear-gradient(to right, #e91e63, #9c27b0); /* Purple gradient */-->
<!--  color: #fff;-->
<!--  border-radius: 15px;-->
<!--  padding: 10px;-->
<!--  display: inline-block;-->
<!--  /* Added display inline-block for rounded corners */-->
<!--}-->

<!--.ai-message {-->
<!--  text-align: left;-->
<!--  background: linear-gradient(to right, #2196f3, #1e88e5); /* Purple gradient */-->
<!--  color: #fff;-->
<!--  border-radius: 15px;-->
<!--  padding: 10px;-->
<!--  display: inline-block;-->
<!--  /* Added display inline-block for rounded corners */-->
<!--}-->

<!--.timestamp {-->
<!--  display: block;-->
<!--  font-size: 0.8em;-->
<!--  color: #ccc;-->
<!--  margin-top: 5px;-->
<!--}-->

<!--.input-box {-->
<!--  display: flex;-->
<!--  padding: 10px;-->
<!--  background-color: #2c2c2c;-->
<!--  border-radius: 10px;-->
<!--  /* Moved input box to bottom with flexbox */-->
<!--  position: absolute;-->
<!--  bottom: 10px;-->
<!--  left: 10px;-->
<!--  right: 10px;-->
<!--}-->

<!--.el-input__inner {-->
<!--  background-color: #3c3c3c;-->
<!--  border-color: #3c3c3c;-->
<!--  color: #fff;-->
<!--}-->

<!--.el-button {-->
<!--  margin-left: 10px;-->
<!--  background-color: #007bff;-->
<!--  border-color: #007bff;-->
<!--}-->

<!--.el-card {-->
<!--  margin-bottom: 10px;-->
<!--  padding: 10px;-->
<!--}-->

<!--.message-content {-->
<!--  white-space: pre-wrap;-->
<!--}-->

<!--.el-loading-mask {-->
<!--  background-color: rgba(0, 0, 0, 0.7);-->
<!--}-->
<!--</style>-->


<!--<style scoped>-->
<!--.chat-container {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  justify-content: space-between;-->
<!--  height: 100vh;-->
<!--  width: 100%;-->
<!--  background-color: #1e1e1e;-->
<!--  color: #ffffff;-->
<!--}-->

<!--.chat-box {-->
<!--  flex: 1;-->
<!--  padding: 10px;-->
<!--  background-color: #2c2c2c;-->
<!--  border-radius: 10px;-->
<!--  margin: 10px;-->
<!--}-->

<!--.scrollbar {-->
<!--  height: calc(100% - 20px);-->
<!--  padding-right: 10px;-->
<!--}-->

<!--.message {-->
<!--  margin-bottom: 10px;-->
<!--}-->

<!--.user-message {-->
<!--  text-align: right;-->
<!--  background-color: #007bff;-->
<!--  color: #fff;-->
<!--  border-radius: 15px;-->
<!--  padding: 10px;-->
<!--}-->

<!--.ai-message {-->
<!--  text-align: left;-->
<!--  background-color: #4caf50;-->
<!--  color: #fff;-->
<!--  border-radius: 15px;-->
<!--  padding: 10px;-->
<!--}-->

<!--.timestamp {-->
<!--  display: block;-->
<!--  font-size: 0.8em;-->
<!--  color: #ccc;-->
<!--  margin-top: 5px;-->
<!--}-->

<!--.input-box {-->
<!--  display: flex;-->
<!--  padding: 10px;-->
<!--  background-color: #2c2c2c;-->
<!--  border-radius: 10px;-->
<!--  margin: 10px;-->

<!--}-->

<!--.el-input__inner {-->
<!--  background-color: #3c3c3c;-->
<!--  border-color: #3c3c3c;-->
<!--  color: #fff;-->
<!--}-->

<!--.el-button {-->
<!--  margin-left: 10px;-->
<!--  background-color: #007bff;-->
<!--  border-color: #007bff;-->
<!--}-->

<!--.el-card {-->
<!--  margin-bottom: 10px;-->
<!--  padding: 10px;-->
<!--}-->

<!--.message-content {-->
<!--  white-space: pre-wrap;-->
<!--}-->

<!--.el-loading-mask {-->
<!--  background-color: rgba(0, 0, 0, 0.7);-->
<!--}-->

<!--/* Markdown content styles */-->
<!--.message-content :global(p) {-->
<!--  margin: 0;-->
<!--}-->

<!--.message-content :global(a) {-->
<!--  color: #ffeb3b;-->
<!--  text-decoration: none;-->
<!--}-->

<!--.message-content :global(pre) {-->
<!--  background-color: #333;-->
<!--  padding: 10px;-->
<!--  border-radius: 5px;-->
<!--  overflow: auto;-->
<!--}-->

<!--.message-content :global(code) {-->
<!--  background-color: #333;-->
<!--  padding: 2px 4px;-->
<!--  border-radius: 3px;-->
<!--}-->
<!--</style>-->


<!--<template>-->
<!--  <el-loading :fullscreen="true" :loading="loading" text="AI is thinking...">-->
<!--    <div class="chat-container">-->
<!--      <div class="chat-box">-->
<!--        <el-scrollbar class="scrollbar">-->
<!--          <div v-for="(message, index) in messages" :key="index" class="message">-->
<!--            <div :class="['message-bubble', message.user ? 'user-message' : 'ai-message']">-->
<!--              <div class="message-content" v-if="message.user">{{ message.content }}</div>-->
<!--              &lt;!&ndash;<div class="message-content" v-else v-html="message.content">&ndash;&gt;-->
<!--              &lt;!&ndash;  <el-button class="copy-button" type="text" @click="copyAnswer(message.content)">&ndash;&gt;-->
<!--              &lt;!&ndash;    <el-icon>&ndash;&gt;-->
<!--              &lt;!&ndash;      <DocumentCopy/>&ndash;&gt;-->
<!--              &lt;!&ndash;    </el-icon>&ndash;&gt;-->
<!--              &lt;!&ndash;  </el-button>&ndash;&gt;-->
<!--              &lt;!&ndash;</div>&ndash;&gt;-->
<!--              <div v-else>-->
<!--                <div class="message-content">{{ message.content }}</div>-->
<!--                <el-button class="copy-button" type="text" @click="copyAnswer(message.content)">-->
<!--                  <el-icon>-->
<!--                    <DocumentCopy/>-->
<!--                  </el-icon>-->
<!--                </el-button>-->
<!--              </div>-->
<!--              <div class="timestamp">{{ formatDate(message.time) }}</div>-->
<!--            </div>-->
<!--          </div>-->
<!--        </el-scrollbar>-->
<!--      </div>-->
<!--      <div class="input-box">-->
<!--        <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>-->
<!--        <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>-->
<!--      </div>-->
<!--    </div>-->
<!--  </el-loading>-->

<!--</template>-->

<!--<template>-->
<!--  <div class="chat-container">-->
<!--    <el-loading :fullscreen="true" :loading="loading" text="AI is thinking...">-->
<!--      <div class="chat-box">-->
<!--        <el-scrollbar class="scrollbar">-->
<!--          <div v-for="(message, index) in messages" :key="index" class="message">-->
<!--            <div :class="['message-bubble', message.user ? 'user-message' : 'ai-message']">-->
<!--              <div class="message-content" v-if="message.user">{{ message.content }}</div>-->
<!--              <div v-else>-->
<!--                <div class="message-content">{{ message.content }}</div>-->
<!--                <el-button class="copy-button" type="text" @click="copyAnswer(message.content)">-->
<!--                  <el-icon>-->
<!--                    <DocumentCopy/>-->
<!--                  </el-icon>-->
<!--                </el-button>-->
<!--              </div>-->
<!--              <div class="timestamp">{{ formatDate(message.time) }}</div>-->
<!--            </div>-->
<!--          </div>-->
<!--        </el-scrollbar>-->
<!--      </div>-->
<!--      <div class="input-box">-->
<!--        <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>-->
<!--        <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>-->
<!--      </div>-->
<!--    </el-loading>-->
<!--  </div>-->
<!--</template>-->

<!--<template>-->
<!--  <div class="chat-container" v-loading="loading" element-loading-text="AI is thinking...">-->
<!--    <div class="chat-box">-->
<!--      <el-scrollbar class="scrollbar">-->
<!--        <div v-for="(message, index) in messages" :key="index" class="message">-->
<!--          <div :class="['message-bubble', message.user ? 'user-message' : 'ai-message']">-->
<!--            <div class="message-content" v-if="message.user">{{ message.content }}</div>-->
<!--            <div v-else>-->
<!--              <div v-html="message.content"></div>-->
<!--              <el-button class="copy-button" style="width:20px" type="text" @click="copyAnswer(message.content)">-->
<!--                <el-icon>-->
<!--                  <DocumentCopy/>-->
<!--                </el-icon>-->
<!--              </el-button>-->

<!--            </div>-->
<!--            <div class="timestamp">{{ formatDate(message.time) }}</div>-->

<!--          </div>-->
<!--        </div>-->
<!--      </el-scrollbar>-->
<!--    </div>-->
<!--    <div class="input-box">-->
<!--      <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>-->
<!--      <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->
<!--<template>-->
<!--  <div class="chat-container">-->
<!--    <div class="chat-box" >-->
<!--      <el-scrollbar class="scrollbar">-->
<!--        <div v-for="(message, index) in messages" :key="index" class="message">-->
<!--          <div :class="['message-bubble', message.user ? 'user-message' : 'ai-message']">-->
<!--            <div class="message-content" v-if="message.user">{{ message.content }}</div>-->
<!--            <div v-else v-loading="loading" element-loading-text="AI is thinking...">-->
<!--              <div v-html="message.content"></div>-->
<!--              <el-button class="copy-button" style="width:20px" type="text" @click="copyAnswer(message.content)">-->
<!--                <el-icon>-->
<!--                  <DocumentCopy/>-->
<!--                </el-icon>-->
<!--              </el-button>-->
<!--            </div>-->
<!--            <div class="timestamp">{{ formatDate(message.time) }}</div>-->
<!--          </div>-->
<!--        </div>-->
<!--      </el-scrollbar>-->
<!--    </div>-->
<!--    <div class="input-box">-->
<!--      <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>-->
<!--      <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import {ref} from 'vue';-->
<!--import {ElMessage} from 'element-plus';-->
<!--import {post} from '@/net';-->
<!--import {marked} from 'marked';-->
<!--import {DocumentCopy} from "@element-plus/icons-vue";-->

<!--const question = ref('');-->
<!--const messages = ref([]);-->
<!--const loading = ref(false);-->

<!--const askQuestion = () => {-->
<!--  if (!question.value.trim()) {-->
<!--    ElMessage.warning('Please enter a question.');-->
<!--    return;-->
<!--  }-->

<!--  const userMessage = {user: true, content: question.value, time: new Date()};-->
<!--  messages.value.push(userMessage);-->
<!--  loading.value = true;-->

<!--  post('/api/chat/ask', {question: question.value}, (data) => {-->
<!--    const aiMessage = {-->
<!--      user: false,-->
<!--      content: marked(data.answer),-->
<!--      time: new Date(data.time)-->
<!--    };-->
<!--    messages.value.push(aiMessage);-->
<!--    loading.value = false;-->
<!--  }, (message, code, url) => {-->
<!--    console.warn(`Request failed: ${url}, status: ${code}, message: ${message}`);-->
<!--    ElMessage.error('Failed to get response from AI.');-->
<!--    loading.value = false;-->
<!--  });-->

<!--  question.value = '';-->
<!--};-->

<!--const formatDate = (date) => {-->
<!--  const d = new Date(date);-->
<!--  return `${d.getHours()}:${d.getMinutes()}:${d.getSeconds()}`;-->
<!--};-->


<!--const copyAnswer = (content) => {-->
<!--  navigator.clipboard.writeText(content)-->
<!--      .then(() => {-->
<!--        ElMessage.success('Answer copied to clipboard');-->
<!--      })-->
<!--      .catch((err) => {-->
<!--        console.error('Failed to copy answer: ', err);-->
<!--        ElMessage.error('Failed to copy answer');-->
<!--      });-->
<!--};-->
<!--</script>-->

<!--<template>-->
<!--  <div class="chat-container">-->
<!--    <div class="chat-box">-->
<!--      <el-scrollbar class="scrollbar">-->
<!--        <div v-for="(message, index) in messages" :key="index" class="message">-->
<!--          <div :class="['message-bubble', message.user ? 'user-message' : 'ai-message']">-->
<!--            <div class="message-content" v-if="message.user">{{ message.content }}</div>-->
<!--            <div v-else class="ai-message-container">-->
<!--              <div v-loading="message.loading" element-loading-text="AI is thinking...">-->
<!--                <div v-html="message.content"></div>-->
<!--                <el-button class="copy-button" style="width:20px" type="text" @click="copyAnswer(message.content)">-->
<!--                  <el-icon>-->
<!--                    <DocumentCopy/>-->
<!--                  </el-icon>-->
<!--                </el-button>-->
<!--              </div>-->
<!--              <div class="timestamp">{{ formatDate(message.time) }}</div>-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->
<!--      </el-scrollbar>-->
<!--    </div>-->
<!--    <div class="input-box">-->
<!--      <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>-->
<!--      <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<template>-->
<!--  <div class="chat-container">-->
<!--    <div class="chat-box">-->
<!--      <el-scrollbar class="scrollbar">-->
<!--        <div v-for="(message, index) in messages" :key="index" class="message">-->
<!--          <div :class="['message-bubble', message.user ? 'user-message' : 'ai-message']">-->
<!--            &lt;!&ndash; 用户消息 &ndash;&gt;-->
<!--            <div class="message-content" v-if="message.user">{{ message.content }}</div>-->
<!--            &lt;!&ndash; AI 消息 &ndash;&gt;-->
<!--            <div v-else class="ai-message-container">-->
<!--              &lt;!&ndash; 加载提示 &ndash;&gt;-->
<!--              <template v-if="message.loading">-->
<!--                <div v-loading="true" element-loading-text="AI is thinking..."></div>-->
<!--              </template>-->
<!--              &lt;!&ndash; 回答内容和复制按钮 &ndash;&gt;-->
<!--              <template v-else-if="message.showContent">-->
<!--                <div v-html="message.content"></div>-->
<!--                <el-button class="copy-button" type="text" @click="copyAnswer(message.content)">-->
<!--                  <el-icon>-->
<!--                    <DocumentCopy/>-->
<!--                  </el-icon>-->
<!--                </el-button>-->
<!--              </template>-->
<!--              <div class="timestamp">{{ formatDate(message.time) }}</div>-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->
<!--      </el-scrollbar>-->
<!--    </div>-->
<!--    <div class="input-box">-->
<!--      <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>-->
<!--      <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import {ref} from 'vue';-->
<!--import {ElMessage} from 'element-plus';-->
<!--import {post} from '@/net';-->
<!--import {marked} from 'marked';-->
<!--import {DocumentCopy} from "@element-plus/icons-vue";-->

<!--const question = ref('');-->
<!--const messages = ref([]);-->

<!--const askQuestion = () => {-->
<!--  if (!question.value.trim()) {-->
<!--    ElMessage.warning('Please enter a question.');-->
<!--    return;-->
<!--  }-->

<!--  const userMessage = {user: true, content: question.value, time: new Date()};-->
<!--  messages.value.push(userMessage);-->

<!--  const aiMessage = {-->
<!--    user: false,-->
<!--    content: '',-->
<!--    time: new Date(),-->
<!--    loading: true,-->
<!--    showContent: false // 控制回答内容和复制按钮的显示-->
<!--  };-->
<!--  messages.value.push(aiMessage);-->

<!--  post('/api/chat/ask', {question: question.value}, (data) => {-->
<!--    aiMessage.content = marked(data.answer);-->
<!--    aiMessage.time = new Date(data.time);-->
<!--    aiMessage.loading = false;-->
<!--    aiMessage.showContent = true; // 回答生成后，允许显示-->
<!--  }, (message, code, url) => {-->
<!--    console.warn(`Request failed: ${url}, status: ${code}, message: ${message}`);-->
<!--    ElMessage.error('Failed to get response from AI.');-->
<!--    aiMessage.loading = false;-->
<!--    aiMessage.showContent = false; // 失败情况下也确保不显示-->
<!--  });-->

<!--  question.value = '';-->
<!--};-->

<!--const formatDate = (date) => {-->
<!--  const d = new Date(date);-->
<!--  return `${d.getHours()}:${d.getMinutes()}:${d.getSeconds()}`;-->
<!--};-->

<!--const copyAnswer = (content) => {-->
<!--  navigator.clipboard.writeText(content)-->
<!--      .then(() => {-->
<!--        ElMessage.success('Answer copied to clipboard');-->
<!--      })-->
<!--      .catch((err) => {-->
<!--        console.error('Failed to copy answer: ', err);-->
<!--        ElMessage.error('Failed to copy answer');-->
<!--      });-->
<!--};-->
<!--</script>-->

<template>
  <div class="chat-container" v-loading="loading" element-loading-text="AI is thinking...">
    <div class="chat-box">
      <el-scrollbar class="scrollbar">
        <div v-for="(message, index) in messages" :key="index" class="message">
          <div :class="['message-bubble', message.user ? 'user-message' : 'ai-message']">
            <div class="message-content" v-if="message.user">{{ message.content }}</div>
            <div v-else>
              <div v-html="message.content"></div>
              <el-button class="copy-button" style="width:20px" type="text" @click="copyAnswer(message.content)">
                <el-icon>
                  <DocumentCopy/>
                </el-icon>
              </el-button>

            </div>
            <div class="timestamp">{{ formatDate(message.time) }}</div>

          </div>
        </div>
      </el-scrollbar>
    </div>
    <div class="input-box">
      <el-input v-model="question" placeholder="Ask something..." @keyup.enter="askQuestion"/>
      <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">Send</el-button>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {ElMessage} from 'element-plus';
import {post} from '@/net';
import {marked} from 'marked';
import {DocumentCopy} from "@element-plus/icons-vue";

const question = ref('');
const messages = ref([]);
const loading = ref(false);

const askQuestion = () => {
  if (!question.value.trim()) {
    ElMessage.warning('Please enter a question.');
    return;
  }

  const userMessage = {user: true, content: question.value, time: new Date()};
  messages.value.push(userMessage);
  loading.value = true;

  post('/api/chat/ask', {question: question.value}, (data) => {
    const aiMessage = {
      user: false,
      content: marked(data.answer),
      time: new Date(data.time)
    };
    messages.value.push(aiMessage);
    loading.value = false;
  }, (message, code, url) => {
    console.warn(`Request failed: ${url}, status: ${code}, message: ${message}`);
    ElMessage.error('Failed to get response from AI.');
    loading.value = false;
  });

  question.value = '';
};

const formatDate = (date) => {
  const d = new Date(date);
  return `${d.getHours()}:${d.getMinutes()}:${d.getSeconds()}`;
};


const copyAnswer = (content) => {
  navigator.clipboard.writeText(content)
      .then(() => {
        ElMessage.success('Answer copied to clipboard');
      })
      .catch((err) => {
        console.error('Failed to copy answer: ', err);
        ElMessage.error('Failed to copy answer');
      });
};
</script>



<style scoped>
.copy-button {
  position: absolute;
  top: 5px;
  right: 5px;
  color: rgba(255, 255, 255, 0.7);
  transition: color 0.3s ease;
}

.copy-button:hover {
  color: #fff;
}

.chat-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, rgba(106, 17, 203, 0.2), rgba(37, 117, 252, 0.2));
  color: #fff;
  font-family: 'Roboto', sans-serif;
}

.chat-box {
  flex: 1;
  padding: 20px;
  overflow: hidden;
}

.scrollbar {
  height: calc(100% - 160px);
  padding-right: 10px;
}

.message {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.message-bubble {
  max-width: 70%;
  padding: 15px;
  border-radius: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  position: relative;
}

.user-message {
  /*background: linear-gradient(135deg, #6a11cb, #2575fc);*/
  background: black;
  margin-left: auto;
}

.ai-message {
  /*background: linear-gradient(135deg, #11998e, #38ef7d);*/

  background: linear-gradient(135deg, #6a11cb, #2575fc);
}

.timestamp {
  font-size: 0.8em;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 5px;
}

.input-box {
  display: flex;
  padding: 20px;
  background-color: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.el-input__inner {
  background-color: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: #fff;
  border-radius: 20px;
  padding-left: 20px;
}

.el-button {
  margin-left: 10px;
  background: linear-gradient(135deg, #11998e, #38ef7d);
  border-color: transparent;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.el-loading-mask {
  background-color: rgba(0, 0, 0, 0.7);
}

/* Markdown content styles */
.message-content :global(p) {
  margin: 0;
}

.message-content :global(a) {
  color: #ffeb3b;
  text-decoration: none;
}

.message-content :global(pre) {
  background-color: rgba(0, 0, 0, 0.3);
  padding: 10px;
  border-radius: 5px;
  overflow: auto;
}

.message-content :global(code) {
  background-color: rgba(0, 0, 0, 0.3);
  padding: 2px 4px;
  border-radius: 3px;
}

.message-bubble {
  max-width: 70%;
  padding: 15px;
  border-radius: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  position: relative;
}


.user-message:before {
  content: '';
  position: absolute;
  top: 10px;
  right: -10px;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  border-left: 10px solid #8e2de2;
}


.ai-message:before {
  content: '';
  position: absolute;
  top: 10px;
  left: -10px;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  border-right: 10px solid #11998e;
}
</style>










