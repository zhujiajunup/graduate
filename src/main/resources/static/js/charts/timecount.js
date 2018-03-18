export default function TimeCount(name, id, tid) {
    var dom = document.getElementById(id);
    var myChart = echarts.init(dom);
    myChart.showLoading();
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/stat/time',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid}),
        success: function (res) {
            var lengend = [];
            var data = [];
            var categories = [];
            $.map(res.data, function (value, key) {
                lengend.push(key);
                var d = [];
                var c = [];
                for(var i = 0 ; i < value.length; i ++){
                    d.push(value[i].count);
                    c.push(value[i].t);
                }
                categories.push(c);
                data.push(d);
            });
            myChart.hideLoading();
            var option = {
                title: {
                    text: '微博转发量和评论数'
                },
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data: lengend
                },
                toolbox: {
                    show: true,
                    feature: {
                        dataView: {show: true, readOnly: false},
                        magicType: {show: true, type: ['line', 'bar']},
                        restore: {show: true},
                        saveAsImage: {show: true}
                    }
                },
                calculable: true,
                xAxis: [
                    {
                        type: 'category',
                        data: categories[0]
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: lengend[0],
                        type: 'bar',
                        data: data[0],
                        markPoint: {
                            data: [
                                {type: 'max', name: '最大值'},
                                {type: 'min', name: '最小值'}
                            ]
                        },
                        markLine: {
                            data: [
                                {type: 'average', name: '平均值'}
                            ]
                        }
                    },
                    {
                        name: lengend[1],
                        type: 'bar',
                        data: data[1],
                        markPoint: {
                            data: [
                                {name: '年最高', value: 182.2, xAxis: 7, yAxis: 183},
                                {name: '年最低', value: 2.3, xAxis: 11, yAxis: 3}
                            ]
                        },
                        markLine: {
                            data: [
                                {type: 'average', name: '平均值'}
                            ]
                        }
                    }
                ]
            };
            console.log(option);
            //myChart.setOption(getOption(name, res));
            myChart.setOption(option);
        }
    });
}

function getOption(name, res) {
    return {
        title: {
            text: name
        },
        tooltip: {
            trigger: "axis"
        },
        legend: {
            data: res.legend
        },
        grid: {
            left: "3%",
            right: "4%",
            bottom: "3%",
            containLabel: true
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        xAxis: {
            type: "category",
            boundaryGap: false,
            data: res.label
        },
        yAxis: {
            type: "value"
        },
        series: res.series.map(o => ({type: "line",...o
    }
))
}
    ;
}
