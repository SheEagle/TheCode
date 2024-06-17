<script setup>
// 引入需要的模块和组件
import {Check, Document} from "@element-plus/icons-vue";
import {computed, defineProps, defineEmits, ref, reactive} from "vue";
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill"; // 引入富文本编辑器 Quill
import ImageResize from "quill-image-resize-vue"; // 引入图片调整插件
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module"; // 引入图片扩展插件
import '@vueup/vue-quill/dist/vue-quill.snow.css'; // 引入富文本编辑器的样式
import axios from "axios";
import {accessHeader, post} from "@/net"; // 自定义的网络请求方法
import {ElMessage} from "element-plus"; // 引入 Element Plus 的消息提示组件
import ColorDot from "@/components/ColorDot.vue"; // 引入自定义的颜色点组件
import {useStore} from "@/store"; // 引入 Vuex store

const store = useStore(); // 获取 Vuex store

// 定义组件的 props
const props = defineProps({
  show: Boolean, // 是否显示编辑器
  defaultTitle: {default: '', type: String}, // 默认标题
  defaultText: {default: '', type: String}, // 默认文本内容
  defaultType: {default: null, type: Number}, // 默认帖子类型
  submitButton: {default: '立即发帖', type: String}, // 提交按钮的文本
  submit: { // 提交按钮点击事件，默认为发表主题的函数
    default: (editor, success) => {
      post('/api/forum/create-topic', {
        type: editor.type.id,
        title: editor.title,
        content: editor.text
      }, () => {
        ElMessage.success("帖子发表成功！");
        success();
      });
    },
    type: Function
  }
});

// 定义组件的 emits
const emit = defineEmits(['close', 'success']);

const refEditor = ref(); // 富文本编辑器的 ref
const editor = reactive({ // 编辑器的数据
  type: null, // 帖子类型
  title: '', // 标题
  text: '', // 文本内容
  loading: false // 是否正在加载
});

// 初始化编辑器内容
function initEditor() {
  if (props.defaultText)
    editor.text = new Delta(JSON.parse(props.defaultText));
  else
    refEditor.value.setContents('', 'user');
  editor.title = props.defaultTitle;
  editor.type = findTypeById(props.defaultType);
}

// 将 Delta 对象转换为文本
function deltaToText(delta) {
  if (!delta.ops) return "";
  let str = "";
  for (let op of delta.ops)
    str += op.insert;
  return str.replace(/\s/g, "");
}

// 计算编辑器内容的长度
const contentLength = computed(() => deltaToText(editor.text).length);

// 根据帖子类型 ID 查找类型
function findTypeById(id) {
  for (let type of store.forum.types) {
    if (type.id === id)
      return type;
  }
}


// 提交帖子
function submitTopic() {
  const text = deltaToText(editor.text);
  if (text.length > 20000) {
    ElMessage.warning('字数超出限制，无法发布主题！');
    return;
  }
  if (!editor.title) {
    ElMessage.warning('请填写标题！');
    return;
  }
  if (!editor.type) {
    ElMessage.warning('请选择一个合适的帖子类型！');
    return;
  }
  props.submit(editor, () => emit('success'));
}

// 注册 Quill 插件
Quill.register('modules/imageResize', ImageResize);
Quill.register('modules/ImageExtend', ImageExtend);

// 富文本编辑器的选项
const editorOption = {
  modules: {
    // 富文本编辑器的工具栏配置
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike", "clean",  // 加粗、斜体、下划线、删除线、清除格式
        {color: []}, {'background': []},  // 文字颜色、背景颜色
        {size: ["small", false, "large", "huge"]},  // 字号
        {header: [1, 2, 3, 4, 5, 6, false]},  // 标题
        {list: "ordered"}, {list: "bullet"}, {align: []},  // 有序列表、无序列表、对齐方式
        "blockquote", "code-block", "link", "image",  // 引用、代码块、链接、插入图片
        {indent: '-1'}, {indent: '+1'}  // 缩进
      ],
      // 自定义工具栏按钮的事件处理函数
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id);  // 触发插入图片事件
        }
      }
    },
    // 图片缩放模块的配置
    imageResize: {
      modules: ['Resize', 'DisplaySize']  // 可调整大小、显示尺寸
    },
    // 图片扩展模块的配置
    ImageExtend: {
      action: axios.defaults.baseURL + '/api/image/cache',  // 图片上传接口地址
      name: 'file',  // 文件字段名称
      size: 5,  // 图片大小限制（单位：MB）
      loading: true,  // 是否显示上传加载动画
      accept: 'image/png, image/jpeg',  // 允许上传的图片格式
      // 图片上传成功后的响应处理函数
      response: (resp) => {
        if (resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data;  // 返回图片地址
        } else {
          return null;  // 返回空值
        }
      },
      methods: 'POST',  // 请求方法
      // 请求头配置函数
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization);  // 设置 Authorization 头
      },
      // 图片上传开始时的回调函数
      start: () => editor.uploading = true,
      // 图片上传成功时的回调函数
      success: () => {
        ElMessage.success('图片上传成功!');  // 显示上传成功消息
        editor.uploading = false;  // 设置上传状态为 false
      },
      // 图片上传失败时的回调函数
      error: () => {
        ElMessage.warning('图片上传失败，请联系管理员!');  // 显示上传失败消息
        editor.uploading = false;  // 设置上传状态为 false
      }
    }
  }
};

</script>

<template>
  <!-- Editor Main Body -->
  <div>
    <el-drawer :model-value="show"
               direction="btt"
               @open="initEditor"
               :close-on-click-modal="false"
               :size="650"
               @close="emit('close')">

      <!-- Header Title -->
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的帖子</div>
          <div style="font-size: 13px">发表内容之前，请遵守论坛守则和相关法律法规，不要出现不文明行为。
          </div>
        </div>
      </template>

      <!-- Select Topic Type and Title Input -->
      <div style="display: flex; gap: 10px">
        <div style="width: 150px">
          <el-select placeholder="请选择帖子分区..." value-key="id" v-model="editor.type"
                     :disabled="!store.forum.types.length">
            <el-option v-for="item in store.forum.types.filter(type => type.id > 0)" :value="item" :label="item.name">
              <div>
                <color-dot :color="item.color"/>
                <span style="margin-left: 10px">{{ item.name }}</span>
              </div>
            </el-option>
          </el-select>
        </div>

        <div style="flex: 1">
          <el-input v-model="editor.title" placeholder="请输入帖子标题..." :prefix-icon="Document"
                    style="height: 100%" maxlength="30"/>
        </div>
      </div>

      <!-- Topic Type Description -->
      <div style="margin-top: 5px; font-size: 13px; color: grey">
        <color-dot :color="editor.type ? editor.type.color : '#dedede'"/>
        <span style="margin-left: 5px">{{
            editor.type ? editor.type.description : '请在上方选择一个帖子分区'
          }}</span>
      </div>

      <!-- Rich Text Editor -->
      <div style="margin-top: 10px; height: 440px; overflow: hidden; border-radius: 5px"
           v-loading="editor.uploading"
           element-loading-text="正在上传图片，请稍后...">
        <quill-editor v-model:content="editor.text" style="height: calc(100% - 45px)"
                      content-type="delta" ref="refEditor"
                      placeholder="今天想分享点什么呢?" :options="editorOption"/>
      </div>

      <!-- Word Count and Submit Button -->
      <div style="display: flex; justify-content: space-between; margin-top: 5px">
        <div style="color: grey; font-size: 13px">
          当前字数: {{ contentLength }} (最大支持20000字)
        </div>
        <div>
          <el-button type="success" :icon="Check" @click="submitTopic" plain>{{ submitButton }}</el-button>
        </div>
      </div>

    </el-drawer>
  </div>
</template>


<style scoped>
:deep(.el-drawer) {
  width: 800px;
  margin: auto;
  border-radius: 10px 10px 0 0;
}

/* 富文本编辑器样式 */
:deep(.quill) {
  border: 1px solid #ccc;
  border-radius: 5px;
}

/* 提交按钮样式 */
:deep(.el-button) {
  border-radius: 20px;
  padding: 10px 20px;
  font-weight: 500;
  transition: background-color 0.3s, transform 0.2s;
}

:deep(.el-drawer__header) {
  margin: 0;
}
</style>


