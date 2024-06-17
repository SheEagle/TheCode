<script setup>
import {useRoute} from "vue-router";
import {get, post} from "@/net";
import {computed, reactive} from "vue";
import {
  ArrowLeft,
  ChatSquare,
  CircleCheck,
  Delete,
  DeleteFilled,
  EditPen,
  Female,
  Male,
  Plus, Share,
  Star
} from "@element-plus/icons-vue";
import {QuillDeltaToHtmlConverter} from 'quill-delta-to-html';
import Card from "@/components/Card.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import {ref} from "vue";
import {useStore} from "@/store";
import TopicEditor from "@/components/TopicEditor.vue";
import TopicCommentEditor from "@/components/TopicCommentEditor.vue";

const route = useRoute()
const store = useStore()

const tid = route.params.tid

const topic = reactive({
  data: null,
  like: false,
  collect: false,
  comments: null,
  page: 1
})

function loadComments(page) {
  topic.comments = null
  top.page = page
  get(`/api/forum/comments?tid=${tid}&page=${page - 1}`, data => topic.comments = data)
}

const init = () => get(`api/forum/topic?tid=${tid}`, data => {
  topic.data = data
  topic.like = data.interact.like
  topic.collect = data.interact.collect
  loadComments(1)
})

init()


function convertToHtml(content) {
  const ops = JSON.parse(content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles: true});
  return converter.convert();
}


function interact(type, message) {
  get(`/api/forum/interact?tid=${tid}&type=${type}&state=${!topic[type]}`, () => {
    topic[type] = !topic[type]
    if (topic[type])
      ElMessage.success(`${message}成功！`)
    else
      ElMessage.success(`已取消${message}！`)
  })
}

function updateTopic(editor) {
  post('/api/forum/update-topic', {
    id: tid,
    type: editor.type.id,
    title: editor.title,
    content: editor.text
  }, () => {
    ElMessage.success('帖子内容更新成功')
    edit.value = false
    init()
  })
}

const edit = ref(false)
const comment = reactive({
  show: false,
  text: '',
  quote: null
})

function onCommentAdd() {
  comment.show = false
  loadComments(Math.floor(++topic.data.comments / 10) + 1)
}

// function deleteComment(id) {
//   get(`api/forum/delete-comment?id=${id}`, () => {
//     ElMessage.success('删除评论成功！')
//     loadComments(topic.page)
//   })
// }
const confirmDialogVisible = ref(false);
const adminDialogVisible = ref(false);
const selectedRule = ref('');
const commentIdToDelete = ref(null);

function confirmDeleteComment(id) {
  console.log('User delete comment:', id); // 添加日志
  commentIdToDelete.value = id;
  confirmDialogVisible.value = true;
}

function deleteComment() {
  get(`/api/forum/delete-comment?id=${commentIdToDelete.value}`, () => {
    ElMessage.success('删除评论成功！');
    loadComments(topic.page);
    confirmDialogVisible.value = false;
  });
}

function adminDeleteComment(id) {
  console.log('Admin delete comment:', id); // 添加日志
  commentIdToDelete.value = id;
  adminDialogVisible.value = true;
}

function confirmAdminDeleteComment() {
  post(`/api/forum/admin-delete-comment`, {
    id: commentIdToDelete.value,
    rule: selectedRule.value
  }, () => {
    ElMessage.success('管理员删除评论成功！');
    loadComments(topic.page);
    adminDialogVisible.value = false;
    selectedRule.value = '';
  });
}


const confirmTopicDialogVisible = ref(false);
const adminTopicDialogVisible = ref(false);
const selectedRuleTopic = ref('');
const topicIdToDelete = ref(null);

function confirmDeletePost() {
  topicIdToDelete.value = tid;
  confirmTopicDialogVisible.value = true;
}

function deletePost() {
  get(`/api/forum/delete-topic?id=${topicIdToDelete.value}`, () => {
    ElMessage.success('帖子删除成功');
    confirmTopicDialogVisible.value = false;
    router.push('/index');

  });
}

function adminConfirmDeletePost() {
  topicIdToDelete.value = tid;
  adminTopicDialogVisible.value = true;
}

function adminDeletePost() {
  post('/api/forum/admin-delete-topic', {id: topicIdToDelete.value, rule: selectedRuleTopic.value}, () => {
    ElMessage.success('管理员删除帖子成功');
    adminTopicDialogVisible.value = false;
    selectedRuleTopic.value = '';
    router.push('/index');
  });
}


function shareWith() {
  // 获取当前页面的URL
  const url = window.location.href;
  // 社交平台的分享链接，这里以QQ和微信为例
  const shareLinks = {
    qq: `http://connect.qq.com/widget/shareqq/index.html?url=${encodeURIComponent(url)}`,
    weibo: `http://service.weibo.com/share/share.php?url=${encodeURIComponent(url)}`,
  };

  // 弹出社交平台的分享页面
  if (navigator.share) {
    // 使用Web Share API，适用于支持Web Share API的浏览器
    navigator.share({
      title: topic.data.title, // 帖子标题
      text: '我想分享这个帖子给你看看', // 分享文案
      url: url // 分享的链接
    }).then(() => console.log('分享成功'))
        .catch((error) => console.error('分享失败', error));
  } else {
    // 降级处理，使用传统的window.open方法
    const platform = prompt('选择分享平台（qq, weibo, wechat）:', 'qq');
    if (shareLinks[platform]) {
      window.open(shareLinks[platform], '_blank');
    } else {
      alert('不支持的平台');
    }
  }
}


</script>

<template>
  <div class="topic-page" v-if="topic.data">
    <div class="topic-main" style="position: sticky;top: 0;z-index: 10">
      <card style="display: flex;width: 100%;">
        <el-button :icon="ArrowLeft" type="info" size="small"
                   plain round @click="router.push('/index')">返回首页
        </el-button>
        <div style="text-align: center;flex: 1">
          <topic-tag :type="topic.data.type"/>
          <span style="font-weight: bold;margin-left: 5px">{{ topic.data.title }}</span>
        </div>
      </card>
    </div>
    <div class="topic-main">
      <div class="topic-main-left">
        <el-avatar :src="store.avatarUserUrl(topic.data.user.avatar)" :size="60"/>
        <div>
          <div style="font-size: 18px;font-weight: bold">
            {{ topic.data.user.username }}
            <span style="color: hotpink" v-if="topic.data.user.gender === 1">
                            <el-icon><Female/></el-icon>
                        </span>
            <span style="color: dodgerblue" v-if="topic.data.user.gender === 0">
                            <el-icon><Male/></el-icon>
                        </span>
          </div>
          <div class="desc">{{ topic.data.user.email }}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div style="text-align: left;margin: 0 5px">
          <div class="desc">微信: {{ topic.data.user.wx || '隐藏或未填写' }}</div>
          <div class="desc">QQ: {{ topic.data.user.qq || '隐藏或未填写' }}</div>
          <div class="desc">手机号: {{ topic.data.user.phone || '隐藏或未填写' }}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div class="desc" style="margin: 0 5px">{{ topic.data.user.desc }}</div>
      </div>
      <div class="topic-main-right">
        <div class="topic-content" v-html="convertToHtml(topic.data.content)"></div>
        <el-divider/>
        <div style="font-size: 13px;color: grey;text-align: center">
          <div>发帖时间: {{ new Date(topic.data.time).toLocaleString() }}</div>
        </div>
        <div style="text-align: right;margin-top: 30px">

          <!--<div v-if="store.user.id === topic.data.user.id" style="text-align: right; margin-top: 20px;">-->
          <!--  <el-button-->
          <!--      type="danger"-->
          <!--      plain-->
          <!--      round-->
          <!--      @click="confirmDeletePost">-->
          <!--    删除帖子-->
          <!--  </el-button>-->
          <!--</div>-->

          <!--&lt;!&ndash; 管理员删除帖子按钮 &ndash;&gt;-->
          <!--<div v-if="store.user.id !== topic.data.user.id && store.user.role === 'admin'"-->
          <!--     style="text-align: right; margin-top: 20px;">-->
          <!--  <el-button-->
          <!--      type="danger"-->
          <!--      plain-->
          <!--      round-->
          <!--      @click="adminConfirmDeletePost">-->
          <!--    管理员删除帖子-->
          <!--  </el-button>-->
          <!--</div>-->

          <!--&lt;!&ndash;编辑&ndash;&gt;-->
          <!--<interact-button name="删除帖子" color="red" :check="false"-->
          <!--                 @check="edit=true" style="margin-right: 20px"-->
          <!--                 v-if="store.user.id === topic.data.user.id"-->
          <!--                 @click="confirmDeletePost" >-->
          <!--  <el-icon>-->
          <!--    <EditPen/>-->
          <!--  </el-icon>-->
          <!--</interact-button>-->
          <!-- 编辑按钮 -->
          <interact-button name="删除帖子" color="#FF7373" :check="false"
                           @check="confirmDeletePost" style="margin-right: 20px"
                           v-if="store.user.id === topic.data.user.id">
            <el-icon>
              <DeleteFilled/>
            </el-icon>
          </interact-button>

          <!-- 管理员删除帖子按钮 -->
          <interact-button name="管理员删帖" color="#FF7373" :check="false"
                           @check="adminConfirmDeletePost" style="margin-right: 20px"
                           v-if="store.user.id !== topic.data.user.id && store.user.role === 'admin'">
            <el-icon>
              <Delete/>
            </el-icon>
          </interact-button>


          <!--编辑-->
          <interact-button name="编辑帖子" color="dodgerblue" :check="false"
                           @check="edit=true" style="margin-right: 20px"
                           v-if="store.user.id===topic.data.user.id">
            <el-icon>
              <EditPen/>
            </el-icon>
          </interact-button>

          <!--点赞-->
          <interact-button name="点赞" check-name="已点赞" color="pink" :check="topic.like"
                           @check="interact('like', '点赞')">
            <el-icon>
              <CircleCheck/>
            </el-icon>
          </interact-button>

          <!--收藏-->
          <interact-button name="收藏" check-name="已收藏" color="orange" :check="topic.collect"
                           @check="interact('collect', '收藏')"
                           style="margin-left: 20px">
            <el-icon>
              <Star/>
            </el-icon>
          </interact-button>
        </div>
      </div>
    </div>

    <!--评论展示-->
    <transition name="el-fade-in-linear" mode="out-in">
      <div v-if="topic.comments">
        <div class="topic-main" style="margin-top: 10px" v-for="item in topic.comments">
          <div class="topic-main-left">
            <el-avatar :src="store.avatarUserUrl(item.user.avatar)" :size="60"/>
            <div>
              <div style="font-size: 18px;font-weight: bold">
                {{ item.user.username }}
                <span style="color: hotpink" v-if="item.user.gender === 1">
                            <el-icon><Female/></el-icon>
                        </span>
                <span style="color: dodgerblue" v-if="item.user.gender === 0">
                            <el-icon><Male/></el-icon>
                        </span>
              </div>
              <div class="desc">{{ item.user.email }}</div>
            </div>
            <el-divider style="margin: 10px 0"/>
            <div style="text-align: left;margin: 0 5px">
              <div class="desc">微信: {{ item.user.wx || '隐藏或未填写' }}</div>
              <div class="desc">QQ: {{ item.user.qq || '隐藏或未填写' }}</div>
              <div class="desc">手机号: {{ item.user.phone || '隐藏或未填写' }}</div>
            </div>
            <el-divider style="margin: 10px 0"/>
            <div class="desc" style="margin: 0 5px">{{ item.user.desc }}</div>
          </div>

          <div class="topic-main-right">
            <div v-if="item.quote" class="comment-quote">
              回复给:{{ item.quote }}
            </div>
            <div style="font-size: 13px;color: grey">
              <div>回复日期: {{ new Date(item.time).toLocaleString() }}</div>
            </div>
            <div class="topic-content" v-html="convertToHtml(item.content)"></div>
            <div style="text-align: right">
              <el-link :icon="ChatSquare" @click="comment.show=true;comment.quote=item"
                       type="info">&nbsp;回复
              </el-link>

              <!--<el-link :icon="Delete" type="danger" v-if="item.user.id===store.user.id"-->
              <!--         style="margin-left: 20px" @click="deleteComment(item.id)">&nbsp;Delete-->
              <!--</el-link>-->
              <!--<el-link :icon="Delete" type="danger" v-if="item.user.id === store.user.id || store.isAdmin"-->
              <!--         style="margin-left: 20px" @click="deleteComment(item.id)">&nbsp;Delete-->
              <!--</el-link>-->

              <el-link :icon="Delete" type="danger" v-if="item.user.id === store.user.id"
                       style="margin-left: 20px" @click="confirmDeleteComment(item.id)">&nbsp;删除
              </el-link>
              <el-link :icon="Delete" type="warning"
                       v-if="item.user.id !== store.user.id && store.user.role === 'admin'"
                       style="margin-left: 20px" @click="adminDeleteComment(item.id)">&nbsp;管理员删除
              </el-link>

            </div>
          </div>
        </div>
        <div style="width: fit-content;margin: 20px auto">
          <el-pagination background layout="prev,pager,next"
                         v-model:current-page="topic.page" @current-change="loadComments"
                         :total="topic.data.comments" :page-size="10"
                         hide-on-single-page/>
        </div>
      </div>
    </transition>

    <!--编辑帖子-->
    <topic-editor :show="edit" @close="edit = false" v-if="topic.data && store.forum.types"
                  :default-type="topic.data.type" :default-text="topic.data.content"
                  :default-title="topic.data.title" submit-button="更新帖子" :submit="updateTopic"></topic-editor>

    <!--发表评论-->
    <topic-comment-editor :show="comment.show" @close="comment.show=false" :tid="tid"
                          :quote="comment.quote" @comment="onCommentAdd"></topic-comment-editor>
    <div class="add-comment" @click="comment.show = true;comment.quote=null">
      <el-icon>
        <Plus/>
      </el-icon>

    </div>

    <div class="add-comment" @click="shareWith" style="margin-right: 60px">
      <el-icon>
        <Share/>
      </el-icon>

    </div>

    <!-- 用户删除确认对话框 -->
    <el-dialog title="确认删除" v-model="confirmDialogVisible" width="30%">
      <span>您确认要删除这条评论吗？</span>
      <span slot="footer" class="dialog-footer">
                  <el-button @click="confirmDialogVisible = false">取消</el-button>
                  <el-button type="primary" @click="deleteComment">确认</el-button>
                </span>
    </el-dialog>

    <!-- 管理员删除对话框 -->
    <el-dialog title="管理员删除评论" v-model="adminDialogVisible" width="30%">
      <span>请选择评论违反的规则：</span>
      <el-radio-group v-model="selectedRule">
        <el-radio label="含有暴力、色情等内容">含有暴力、色情内容</el-radio>
        <el-radio label="内含广告">含有广告</el-radio>
        <el-radio label="恶意灌水或引战">恶意灌水或引战</el-radio>
        <el-radio label="与分区或贴子主题不符">与分区或贴子主题不符</el-radio>
      </el-radio-group>
      <span slot="footer" class="dialog-footer">
                  <el-button @click="adminDialogVisible = false">取消</el-button>
                  <el-button type="primary" @click="confirmAdminDeleteComment">确认</el-button>
                </span>
    </el-dialog>


    <!-- 用户删除确认对话框 -->
    <el-dialog title="确认删除" v-model="confirmTopicDialogVisible" width="30%">
      <span>您确认要删除这条帖子吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="confirmTopicDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="deletePost">确认</el-button>
      </span>
    </el-dialog>

    <!-- 管理员删除对话框 -->
    <el-dialog title="管理员删除帖子" v-model="adminTopicDialogVisible" width="30%">
      <span>请选择帖子违反的规则：</span>
      <el-radio-group v-model="selectedRuleTopic">
        <el-radio label="含有暴力、色情等内容">含有暴力、色情内容</el-radio>
        <el-radio label="内含广告">含有广告</el-radio>
        <el-radio label="恶意灌水或引战">恶意灌水或引战</el-radio>
        <el-radio label="与分区或帖子主题不符">与分区或帖子主题不符</el-radio>
      </el-radio-group>
      <span slot="footer" class="dialog-footer">
        <el-button @click="adminTopicDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="adminDeletePost">确认</el-button>
      </span>
    </el-dialog>

  </div>
</template>


<style lang="scss" scoped>
.topic-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  //padding: 10px 0;
  background: linear-gradient(to bottom right, rgba(106, 17, 203, 0.1), rgba(37, 117, 252, 0.1));

  color: #fff;
  padding: 20px;
  font-family: 'Roboto', sans-serif;
}


.topic-main {
  display: flex;
  //border-radius: 7px;
  margin: 0 auto;
  background-color: var(--el-bg-color);
  width: 800px;
  //background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 10px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  padding: 20px;

  .topic-main-left {
    width: 200px;
    padding: 10px;
    text-align: center;
    border-right: solid 1px var(--el-border-color);

    .desc {
      font-size: 12px;
      color: grey;
    }
  }

  .topic-main-right {
    width: 600px;
    padding: 10px 20px;

    .topic-content {
      font-size: 14px;
      line-height: 22px;
      opacity: 0.8;
    }
  }
}

.add-comment {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 18px;
  color: var(--el-color-primary);
  text-align: center;
  line-height: 45px;
  background: var(--el-bg-color-overlay);
  box-shadow: var(--el-box-shadow-lighter);

  &:hover {
    background: var(--el-border-color-extra-light);
    cursor: pointer;
  }

}


.comment-quote {
  font-size: 13px;
  color: grey;
  // background-color: rgba(94, 94, 94, 0.2);
  // padding: 10px;
  //margin-top: 10px;
  //border-radius: 5px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  padding: 10px;
  margin-top: 10px;
}


.el-avatar {
  border-radius: 50%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.el-link:hover,
.el-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.share-menu {
  position: fixed;
  bottom: 80px;
  right: 20px;
  background: #fff;
  border: 1px solid #ddd;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  border-radius: 5px;
  padding: 10px;
  z-index: 1000;
}

.share-item {
  padding: 5px;
  cursor: pointer;
}

.share-item:hover {
  background-color: #f0f0f0;
}
</style>