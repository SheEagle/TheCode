<script setup>
import {Delta, QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import {ref} from "vue";
import {post} from "@/net";
import {ElMessage} from "element-plus";

const props = defineProps({
  show: Boolean,
  tid: String,
  quote: Object
})

const content = ref()

const emit = defineEmits(['close', 'comment'])

const init = () => content.value = new Delta()

function submitComment() {
  if (deltaToText(content.value).length > 2000) {
    ElMessage.warning('评论字数已经超出最大限制，请缩减评论内容！')
    return
  }
  post('/api/forum/add-comment', {
    tid: props.tid,
    quote: props.quote ? props.quote.id : -1,
    content: JSON.stringify(content.value)
  }, () => {
    ElMessage.success('发表评论成功！')
    emit('comment')
  })
}


// function deltaToSimpleText(delta) {
//   console.info("这是delta:" + delta)
//   let str = deltaToText(delta)
//   if (str.length > 35) str = str.substring(0, 35) + "..."
//   console.info("这是str:" + str)
//   return str
// }

function deltaToSimpleText(delta) {
  let str = ""
  for (let op of JSON.parse(delta).ops) {
    str += op.insert
  }
  if (str.length > 35) str = str.substring(0, 35) + "..."
  return str
}

function deltaToText(delta) {
  // 检查 delta 是否存在 ops 字段，并且 ops 不为空数组
  if (!delta || !Array.isArray(delta.ops) || delta.ops.length === 0) {
    return ""; // 如果 delta 不存在或 ops 为空数组，则返回空字符串
  }

  let str = "";

  // 遍历 delta.ops 数组
  for (let op of delta.ops) {
    // 检查操作对象中是否有 insert 字段
    if (op.insert) {
      // 如果有 insert 字段，则将其值添加到 str 中
      str += op.insert;
    }
  }

  // 返回提取出的文本，去除其中的空格
  return str.replace(/\s/g, "");
}


</script>

<template>
  <div>
    <el-drawer :model-value="show"
               :title="quote ? `发表对评论: ${deltaToSimpleText(quote.content)}的回复` : '发表帖子回复'"
               @open="init" @close="emit('close')"
               direction="btt" :size="270"
               :close-on-click-modal="false">
      <div>
        <div>
          <quill-editor style="height: 120px" v-model:content="content"
                        placeholder="请发表友善的评论"/>
        </div>
        <div style="margin-top: 10px;display: flex">
          <div style="flex: 1;font-size: 13px;color: grey">
            字数统计: {{ deltaToText(content).length }} (最大支持2000字)
          </div>
          <el-button type="success" @click="submitComment" plain>发表评论</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>


<style lang="less" scoped>
:deep(.el-drawer) {
  width: 800px;
  margin: 20px auto;
  border-radius: 10px;
}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.el-drawer__body) {
  padding: 10px;
}
</style>
