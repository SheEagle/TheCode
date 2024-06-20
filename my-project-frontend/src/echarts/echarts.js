import * as echarts from "echarts";

const gaugeData = [
    {
        value: 20,
        name: '总点赞数',
        title: {
            offsetCenter: ['0%', '-30%']
        },
        detail: {
            valueAnimation: true,
            offsetCenter: ['0%', '-20%']
        }
    },
    {
        value: 40,
        name: '总评论数',
        title: {
            offsetCenter: ['0%', '0%']
        },
        detail: {
            valueAnimation: true,
            offsetCenter: ['0%', '10%']
        }
    },
    {
        value: 60,
        name: '总收藏数',
        title: {
            offsetCenter: ['0%', '30%']
        },
        detail: {
            valueAnimation: true,
            offsetCenter: ['0%', '40%']
        }
    }
];

function getChart(option, userData) {
    option.series = [
        {
            type: 'gauge',
            startAngle: 90,
            endAngle: -270,
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
                    width: 40
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
                    value: userData[0],
                    name: '总点赞数',
                    title: {
                        offsetCenter: ['0%', '-30%']
                    },
                    detail: {
                        valueAnimation: true,
                        offsetCenter: ['0%', '-20%']
                    }
                },
                {
                    value: userData[0],
                    name: '总评论数',
                    title: {
                        offsetCenter: ['0%', '0%']
                    },
                    detail: {
                        valueAnimation: true,
                        offsetCenter: ['0%', '10%']
                    }
                },
                {
                    value: userData[1],
                    name: '总收藏数',
                    title: {
                        offsetCenter: ['0%', '30%']
                    },
                    detail: {
                        valueAnimation: true,
                        offsetCenter: ['0%', '40%']
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
                formatter: '{value}'
            }
        }
    ]
}

export {getChart}