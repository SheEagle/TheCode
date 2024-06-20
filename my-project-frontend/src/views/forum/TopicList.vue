<script setup>
import LightCard from "@/components/LightCard.vue";
import {
  Calendar,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen,
  Link,
  Picture,
  Microphone, CircleCheck, Star, FolderOpened, ArrowRightBold
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, onMounted, reactive, ref, watch} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";
import {useRoute} from "vue-router";
import * as echarts from "echarts";

const store = useStore()

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})
const editor = ref(false)
const topics = reactive({
  list: [],
  type: -1,
  page: 0,
  end: false,
  top: []
})
const collects = ref(false)


const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})

const route = useRoute();
const search = ref(route.query.search || '');

watch(() => topics.type, () => resetList(), {immediate: true})

watch(() => route.query.search, () => {
  search.value = route.query.search || '';
  resetList();
});

get('/api/forum/top-topic', data => topics.top = data)

function updateList() {
  if (topics.end) return;
  const params = new URLSearchParams({'page': topics.page, 'type': topics.type});
  if (search.value) {
    params.append('search', search.value);
  }
  get(`/api/forum/list-topic?${params.toString()}`, data => {
    if (data) {
      data.forEach(d => topics.list.push(d));
      topics.page++;
    }
    if (!data || data.length < 10)
      topics.end = true;
  });
}

function onTopicCreate() {
  editor.value = false
  resetList()
}

function resetList() {
  topics.page = 0
  topics.end = false
  topics.list = []
  updateList()
}

navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude
  const latitude = position.coords.latitude
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    Object.assign(weather, data)
    weather.success = true
  })
}, error => {
  console.info(error)
  ElMessage.warning('位置信息获取超时，请检测网络设置')
  get(`/api/forum/weather?longitude=116.40529&latitude=39.90499`, data => {
    Object.assign(weather, data)
    weather.success = true
  })
}, {
  timeout: 3000,
  enableHighAccuracy: true
})


// Fetch statistics data and initialize chart
const statistics = ref({
  totalLikes: 0,
  totalCollects: 0,
  totalComments: 0
});

// const initChart = () => {
//   const chartDom = document.getElementById('gauge-chart');
//   const myChart = echarts.init(chartDom);
//
//   const option = {
//     series: [
//       {
//         type: 'gauge',
//         startAngle: 90,
//         endAngle: -270,
//         radius: '100%',
//         pointer: {
//           show: false
//         },
//         progress: {
//           show: true,
//           overlap: false,
//           roundCap: true,
//           clip: false,
//           itemStyle: {
//             borderWidth: 1,
//             borderColor: '#464646'
//           }
//         },
//         axisLine: {
//           lineStyle: {
//             width: 30
//           }
//         },
//         splitLine: {
//           show: false,
//           distance: 0,
//           length: 10
//         },
//         axisTick: {
//           show: false
//         },
//         axisLabel: {
//           show: false,
//           distance: 50
//         },
//         data: [
//           {
//             value: statistics.value.totalLikes,
//             name: '收获点赞数',
//             title: {
//               offsetCenter: ['0%', '-50%']
//             },
//             detail: {
//               valueAnimation: true,
//               offsetCenter: ['0%', '-30%']
//             }
//           },
//           {
//             value: statistics.value.totalCollects,
//             name: '收获收藏数',
//             title: {
//               offsetCenter: ['0%', '-10%']
//             },
//             detail: {
//               valueAnimation: true,
//               offsetCenter: ['0%', '10%']
//             }
//           },
//           {
//             value: statistics.value.totalComments,
//             name: '收获评论数',
//             title: {
//               offsetCenter: ['0%', '30%']
//             },
//             detail: {
//               valueAnimation: true,
//               offsetCenter: ['0%', '50%']
//             }
//           }
//         ],
//         title: {
//           fontSize: 14
//         },
//         detail: {
//           width: 50,
//           height: 14,
//           fontSize: 14,
//           color: 'inherit',
//           borderColor: 'inherit',
//           borderRadius: 20,
//           borderWidth: 1,
//           formatter: '{value}'
//         }
//       }
//     ]
//   };
//
//   myChart.setOption(option);
// };

// onMounted(() => {
//   get('/api/user/statistics', data => {
//     statistics.value = data;
//     initChart();
//   }, error => {
//     console.error('Failed to fetch statistics:', error);
//     ElMessage.error('无法获取统计数据');
//   });
// });

const initChart = () => {
  const chartDom = document.getElementById('gauge-chart');
  const myChart = echarts.init(chartDom);

  const option = {
    series: [
      {
        type: 'gauge',
        startAngle: 90,
        endAngle: -270,
        radius: '100%',
        pointer: {
          show: false
        },
        progress: {
          show: true,
          overlap: false,
          roundCap: true,
          clip: false,
          itemStyle: {
            borderWidth: 1,
            borderColor: '#464646'
          }
        },
        axisLine: {
          lineStyle: {
            width: 30
          }
        },
        splitLine: {
          show: false,
          distance: 0,
          length: 10
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          show: false,
          distance: 50
        },
        data: [
          {
            value: statistics.value.totalLikes,
            name: '点赞数',
            title: {
              offsetCenter: ['0%', '-50%']
            },
            detail: {
              valueAnimation: true,
              offsetCenter: ['0%', '-30%']
            },
            itemStyle: {
              color: 'rgba(106, 17, 203, 0.35)' // 金色
            },
          },
          {
            value: statistics.value.totalCollects,
            name: '收藏数',
            title: {
              offsetCenter: ['0%', '-10%']
            },
            detail: {
              valueAnimation: true,
              offsetCenter: ['0%', '10%']
            },
            itemStyle: {
              color: 'rgba(37, 117, 252, 0.35)' // 金色
            },
          },
          {
            value: statistics.value.totalComments,
            name: '评论数',
            title: {
              offsetCenter: ['0%', '30%']
            },
            detail: {
              valueAnimation: true,
              offsetCenter: ['0%', '50%']
            }
          }
        ],
        title: {
          fontSize: 14
        },
        detail: {
          width: 50,
          height: 14,
          fontSize: 14,
          color: 'inherit',
          borderColor: 'inherit',
          borderRadius: 20,
          borderWidth: 1,
          formatter: '{value}次'
        }
      }
    ]
  };

  myChart.setOption(option);

  return myChart;
};

const updateChart = (chart) => {
  const option = chart.getOption();
  option.series[0].data = [
    {
      value: statistics.value.totalLikes,
      name: '收获点赞',
      title: {
        offsetCenter: ['0%', '-50%']
      },
      detail: {
        valueAnimation: true,
        offsetCenter: ['0%', '-30%']
      },
      itemStyle: {
        color: 'rgba(106, 17, 203, 0.35)' // 金色
      },
    },
    {
      value: statistics.value.totalCollects,
      name: '收获收藏',
      title: {
        offsetCenter: ['0%', '-10%']
      },
      detail: {
        valueAnimation: true,
        offsetCenter: ['0%', '10%']
      },
      itemStyle: {
        color: 'rgba(37, 117, 252, 0.35)' // 金色
      },
    },
    {
      value: statistics.value.totalComments,
      name: '收获评论',
      title: {
        offsetCenter: ['0%', '30%']
      },
      detail: {
        valueAnimation: true,
        offsetCenter: ['0%', '50%']
      }
    }
  ];

  chart.setOption(option);
};

onMounted(() => {
  const chart = initChart();
  const fetchStatistics = () => {
    get('/api/user/statistics', data => {
      statistics.value = data;
      updateChart(chart);
    }, error => {
      console.error('Failed to fetch statistics:', error);
      ElMessage.error('无法获取统计数据');
    });
  };

  fetchStatistics(); // Initial fetch
  setInterval(fetchStatistics, 600000); // Fetch every 10 minutes
});


</script>

<template>
  <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 90%">
    <div style="flex: 1">
      <!--发表帖子-->
      <light-card style="background:linear-gradient(to right, rgba(106, 17, 203, 0.35), rgba(37, 117, 252, 0.35)) ;">
        <div class="create-topic" @click="editor = true">
          <el-icon>
            <EditPen/>
          </el-icon>
          发表新帖子...
        </div>
        <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
          <el-icon>
            <Edit/>
          </el-icon>
          <el-icon>
            <Document/>
          </el-icon>
          <el-icon>
            <Compass/>
          </el-icon>
          <el-icon>
            <Picture/>
          </el-icon>
          <el-icon>
            <Microphone/>
          </el-icon>
        </div>
      </light-card>

      <!--置顶-->
      <light-card
          style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px;background: linear-gradient(to right, rgba(106, 17, 203, 0.35), rgba(37, 117, 252, 0.35))">
        <div v-for="item in topics.top" class="top-topic" @click="router.push(`/index/topic-detail/${item.id}`)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{ item.title }}</div>
          <!--<div>{{ new Date(item.time).toLocaleDateString() }}</div>-->
        </div>
      </light-card>

      <!--帖子类型-->
      <light-card style="margin-top: 10px;display: flex;gap: 7px">
        <div :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
             v-for="item in store.forum.types"
             @click="topics.type = item.id">
          <color-dot :color="item.color"/>
          <span style="margin-left: 5px">{{ item.name }}</span>
        </div>
      </light-card>


      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list.length">
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px"
               v-infinite-scroll="updateList">

            <!--帖子列表-->
            <light-card v-for="item in topics.list" class="topic-card"
                        @click="router.push('/index/topic-detail/'+item.id)">
              <div style="display: flex">
                <div>
                  <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar)"/>
                </div>
                <div style="margin-left: 7px;transform: translateY(-2px)">
                  <div style="font-size: 13px;font-weight: bold">{{ item.username }}</div>
                  <div style="font-size: 12px;color: grey">
                    <el-icon>
                      <Clock/>
                    </el-icon>
                    <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                      {{ new Date(item.time).toLocaleString() }}
                    </div>
                  </div>
                </div>
              </div>
              <div style="margin-top: 5px">
                <topic-tag :type="item.type"/>
                <span style="font-weight: bold;margin-left: 7px">{{ item.title }}</span>
              </div>
              <div class="topic-content">{{ item.text }}</div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image class="topic-image" v-for="img in item.images" :src="img" fit="cover"></el-image>
              </div>
              <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px;opacity: 0.8">
                <div>
                  <el-icon style="vertical-align: middle">
                    <CircleCheck/>
                  </el-icon>
                  {{ item.like }}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle">
                    <Star/>
                  </el-icon>
                  {{ item.collect }}收藏
                </div>
              </div>
            </light-card>
          </div>
        </div>
        <el-empty v-else description="没有相关帖子"></el-empty>
      </transition>
    </div>

    <!--侧边栏-->
    <div style="width: 25%">
      <div style="position: sticky;top: 10px">
        <!--收藏-->
        <light-card style="padding: 0px">
          <div class="collect-list-button" @click="collects = true" style="margin: 0px">
            <span><el-icon><FolderOpened/></el-icon> 我的收藏</span>
            <el-icon style="transform: translateY(3px)">
              <ArrowRightBold/>
            </el-icon>
          </div>
        </light-card>

        <!--公告栏-->
        <light-card
            style="margin-top: 10px ; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2)">
          <div style="font-weight: bold">
            <el-icon>
              <CollectionTag/>
            </el-icon>
            公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;margin: 10px;">
            欢迎来到The Code，一个专注于科学、技术、工程和数学领域知识分享与讨论的论坛。
            我们致力于打造一个充满活力、互助互学的在线环境，让每一位成员都能在这里找到灵感、解决问题并共同成长。
          </div>
        </light-card>


        <!--<light-card-->
        <!--    style="margin-top: 10px; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);">-->
        <!--  &lt;!&ndash;<div style="font-weight: bold">&ndash;&gt;-->
        <!--  &lt;!&ndash;  <el-icon>&ndash;&gt;-->
        <!--  &lt;!&ndash;    <Calendar/>&ndash;&gt;-->
        <!--  &lt;!&ndash;  </el-icon>&ndash;&gt;-->
        <!--  &lt;!&ndash;  天气&ndash;&gt;-->
        <!--  &lt;!&ndash;</div>&ndash;&gt;-->
        <!--  &lt;!&ndash;<el-divider style="border-color: rgba(255, 255, 255, 0.5); margin: 10px 0"/>&ndash;&gt;-->
        <!--  &lt;!&ndash;<weather :data="weather"/>&ndash;&gt;-->
        <!--  <div id="gauge-chart" style="width: 500px;height:500px;"></div>-->
        <!--</light-card>-->
        <light-card
            style="margin-top: 10px; padding: 10px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2)">
          <div id="gauge-chart" style="width: 100%;height:280px;"></div>
        </light-card>


        <light-card
            style="margin-top: 10px; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2)">
          <div style="display: flex; justify-content: space-between; margin-bottom: 10px;font-size: 15px">
            <div>日期</div>
            <div>{{ today }}</div>
          </div>
          <div style="display: flex; justify-content: space-between;font-size: 15px">
            <div>IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>


        <!--链接-->
        <div style="font-size: 20px;margin-top: 10px;color: grey">
          <el-icon>
            <Link/>
          </el-icon>
          链接
          <el-divider style="margin: 10px 0"/>
        </div>

        <div style="display: grid; grid-template-columns: 1fr; grid-gap: 10px; margin-top: 10px">
          <div class="friend-link">
            <!-- LinkedIn 链接和图片 -->
            <a href="https://www.linkedin.com/" target="_blank">
              <el-image
                  style="width: 80%; height: 50px; object-fit: cover;margin-left: 20px"
                  src="/src/assets/linkedin.png"
                  alt="LinkedIn"
              ></el-image>
            </a>
          </div>
          <div class="friend-link">
            <!-- Google Scholar 链接和图片 -->
            <a href="https://scholar.google.com/" target="_blank">
              <el-image
                  style="width: 80%; max-height: 50px; object-fit: cover;margin-left: 20px"
                  src="/src/assets/google.jpg"
                  alt="Google Scholar"
              ></el-image>
            </a>
          </div>

        </div>
      </div>
    </div>

    <topic-editor :show="editor" @success="onTopicCreate" @close="editor = false"/>
    <topic-collect-list :show="collects" @close="collects = false"/>
  </div>

</template>

<style lang="less" scoped>
.collect-list-button {
  //background: linear-gradient(135deg, rgba(142, 45, 226, 0.5), rgba(74, 0, 224, 0.5));
  //background: linear-gradient(90deg, #FEE140 0%, #FA709A 100%);
  // background:linear-gradient(to right, rgba(106, 17, 203, 0.30), rgba(37, 117, 252, 0.30));
  //color: #fff;
  padding: 10px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.collect-list-button:hover {
  transform: scale(1.03);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.collect-list-button {
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

  &:hover {
    cursor: pointer;
    opacity: 0.6;
  }
}

.top-topic {
  display: flex;

  div:first-of-type {
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
    transition: color .3s;

    &:hover {
      color: grey;
    }
  }

  div:nth-of-type(2) {
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }

  &:hover {
    cursor: pointer;
  }
}

.type-select-card {
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color .3s;

  &.active {
    border: solid 1px #ead4c4;
  }

  &:hover {
    cursor: pointer;
    background-color: #dadada;
  }
}

.topic-card {
  padding: 15px;
  transition: scale .3s;

  &:hover {
    scale: 1.015;
    cursor: pointer;
  }

  .topic-content {
    font-size: 13px;
    color: grey;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .topic-image {
    width: 100%;
    height: 100%;
    max-height: 110px;
    border-radius: 5px;
  }
}

.info-text {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}

.friend-link {
  border-radius: 5px;
  overflow: hidden;
}

.create-topic {
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover {
    cursor: pointer;
  }
}

.dark {
  .create-topic {
    background-color: #232323;
  }

  .type-select-card {
    background-color: #282828;

    &.active {
      border: solid 1px #64594b;
    }

    &:hover {
      background-color: #5e5e5e;
    }
  }
}


</style>




