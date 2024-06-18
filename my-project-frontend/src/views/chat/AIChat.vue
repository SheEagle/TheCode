<template>
  <div style="height: 100%">
    <div class="chat-container" v-loading="loading" element-loading-text="AI is thinking...">
      <div class="session-list">
        <el-scrollbar class="scrollbar" style="height: 100%">
          <div v-for="(session, index) in sessions" :key="index" class="session-item"
               @click="selectSession(session.id)">
            <!--<div class="session-title">{{ session.sessionName || '未命名对话' }}</div>-->
            <div class="session-title">{{ session.sessionName || '未命名对话' }}
              <el-button type="text" style="background: linear-gradient(135deg, #6a11cb, #2575fc)" class="rename-button"
                         @click.stop="renameSession(session)">
                <el-icon>
                  <Edit/>
                </el-icon>
              </el-button>
            </div>
            <div class="session-date">{{ formatDate(session.createdAt) }}</div>
          </div>
        </el-scrollbar>
      </div>

      <div class="chat-right">
        <div class="chat-box">
          <el-scrollbar class="scrollbar" style="height: 100%">
            <div v-for="(message, index) in messages" :key="index" class="message">
              <div class="message-bubble question-bubble" v-if="message.question">
                <div class="message-content">{{ message.question }}</div>
                <el-button class="copy-button" style="width:20px" type="text" @click="copyAnswer(message.question)">
                  <el-icon>
                    <DocumentCopy/>
                  </el-icon>
                </el-button>
                <div class="timestamp">{{ formatDate(message.createdAt) }}</div>
              </div>

              <div style="background: linear-gradient(135deg, #6a11cb, #2575fc);margin-top: 10px" class="message-bubble"
                   v-if="message.answer">
                <div class="message-content" v-html="marked(message.answer)"></div>
                <el-button class="copy-button" style="width:20px" type="text" @click="copyAnswer(message.answer)">
                  <el-icon>
                    <DocumentCopy/>
                  </el-icon>
                </el-button>
                <div class="timestamp">{{ formatDate(message.createdAt) }}</div>
              </div>

            </div>
          </el-scrollbar>
        </div>

        <div class="input-box">
          <el-input v-model="question" placeholder="想要问点什么呢?" @keyup.enter="askQuestion"/>
          <el-button @click="askQuestion" type="primary" icon="el-icon-s-promotion">发送</el-button>
        </div>
      </div>

    </div>
  </div>

</template>

<script setup>
import {ref, onMounted} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {get, post} from '@/net';
import {marked} from 'marked';
import {DocumentCopy, Edit} from "@element-plus/icons-vue";
import {useStore} from "@/store";

const question = ref('');
const messages = ref([]);
const sessions = ref([]);
const currentSessionId = ref(null);
const loading = ref(false);
const store = useStore()

const fetchSessions = () => {
  const userId = store.user.id;
  get(`/api/chat/sessions?userId=${userId}`, (data) => {
    sessions.value = data;
  }, (message, code, url) => {
    console.warn(`Request failed: ${url}, status: ${code}, message: ${message}`);
    ElMessage.error('Failed to load sessions.');
  });
};

const selectSession = (sessionId) => {
  currentSessionId.value = sessionId;
  get(`/api/chat/sessions/${sessionId}`, (data) => {
    messages.value = data.map(chat => ({
      question: chat.question,
      answer: chat.answer,
      createdAt: new Date(chat.createdAt)
    }));
  }, (message, code, url) => {
    console.warn(`Request failed: ${url}, status: ${code}, message: ${message}`);
    ElMessage.error('Failed to load session messages.');
  });
};

const createSession = (callback) => {
  const newSession = {
    sessionName: '未命名会话',
    createdAt: new Date(),
  };
  post('/api/chat/create-session', newSession, (data) => {
    sessions.value.push(data);
    currentSessionId.value = data.id;
    if (callback) callback();
  }, (message, code, url) => {
    console.warn(`Request failed: ${url}, status: ${code}, message: ${message}`);
    ElMessage.error('Failed to create a new session.');
  });
};


const askQuestion = () => {
  if (!question.value.trim()) {
    ElMessage.warning('Please enter a question.');
    return;
  }

  if (!currentSessionId.value) {
    createSession(() => {
      sendQuestion();
    });
  } else {
    sendQuestion();
  }
};

const sendQuestion = () => {
  const userMessage = {question: question.value, createdAt: new Date(), answer: null};
  messages.value.push(userMessage);
  loading.value = true;

  post('/api/chat/ask', {question: question.value, sessionId: currentSessionId.value}, (data) => {
    const aiMessage = {
      question: null,
      answer: marked(data.answer),
      createdAt: new Date(data.time)
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
  return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()} ${d.getHours()}:${d.getMinutes()}:${d.getSeconds()}`;
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

onMounted(() => {
  fetchSessions();
});


const renameSession = (session) => {
  ElMessageBox.prompt('请输入新的会话名称', '重命名会话', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: session.sessionName,
  })
      .then(({value}) => {
        const newName = value.trim();
        if (newName) {
          post('/api/chat/rename-session', {id: session.id, sessionName: newName}, (data) => {
            session.sessionName = data.sessionName;
            ElMessage.success('会话名称修改成功');
          }, (message, code, url) => {
            console.warn(`Request failed: ${url}, status: ${code}, message: ${message}`);
            ElMessage.error('修改会话名称失败');
          });
        } else {
          ElMessage.warning('会话名称不能为空');
        }
      })
      .catch(() => {
      });
};
</script>

<style lang="scss" scoped>

.rename-button {
  color: #fff;
  border-radius: 20px;
  padding: 2px 5px;
  transition: all 0.3s ease;

  &:hover {
    background: linear-gradient(90deg, #6dd5ed, #2193b0);
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  }
}

.chat-container {
  display: flex;
  height: 100vh;
  width: 100%;

  font-family: 'Roboto', sans-serif;
}

.session-list {
  width: 250px;
  border-right: 2px solid gray;
  padding: 20px;
  overflow: hidden;
}

.session-item {
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: background 0.3s ease;
  background: linear-gradient(135deg, rgba(106, 17, 203, 0.3), rgba(37, 117, 252, 0.3));
}

.session-item:hover {
  background: darkgrey;
}

.session-title {
  font-weight: bold;
}

.session-date {
  font-size: 0.8em;

}

.chat-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%
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
  flex-direction: column;
  align-items: flex-start;
}

.message-bubble {
  max-width: 70%;
  padding: 15px;
  border-radius: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  position: relative;
  display: flex;
  flex-direction: column;
}

.user-message .message-bubble {
  background: #5e5e5e;
  align-self: flex-end;
}

.ai-message .message-bubble {
  background: linear-gradient(135deg, rgba(106, 17, 203, 0.3), rgba(37, 117, 252, 0.3)) !important;
}

.dark .ai-message .message-bubble {
  background: linear-gradient(135deg, #6a11cb, #2575fc) !important;
}

.question-bubble {
  background: #555;
  align-self: flex-start;
}

.copy-button {
  position: absolute;
  top: 5px;
  right: 5px;
  transition: color 0.3s ease;
}

.copy-button:hover {

}

.timestamp {
  font-size: 0.8em;
  margin-top: 5px;
  align-self: flex-end;
}

.input-box {
  display: flex;
  margin: 20px;
  background-color: rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(20px);

  padding: 10px;
  border-radius: 5px;
}

.el-input__inner {
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

</style>












