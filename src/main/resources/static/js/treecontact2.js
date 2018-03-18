$(document).ready(function () {
    tree(getQueryString('tid'));
});

function tree(inputId) {
    source(inputId);
    commentTimeLine(inputId);
    userInfo(inputId);
    tweetContent(inputId);
}

function tweetContent(tweetId) {
    $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/tweet/getById',
            contentType: "application/json",
            data: JSON.stringify({'tweetId': tweetId}),
            success: function (result) {
                document.getElementById("tweet_content").innerHTML = tweetInfoHtml(result['data']);

            }
        }
    )
}

function userInfo(tweetId) {
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/usr/pub',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tweetId}),
        success: function (result) {
            document.getElementById('user_info').innerHTML =
                '<div>昵称:' + result['data']['nickname']+'('+result['data']['id']+')' + '</div>' +
                '<div>所在地: ' + result['data']['place'] + '</div>' +
                '<div>性别:' + result['data']['gender'] + '</div>' +
                '<div>性取向: ' + result['data']['sexOrientation'] + '</div>' +
                '<div>生日: ' + result['data']['birthday'] + '</div>' +
                '<div>简介: ' + result['data']['signature'] + '</div>' +
                '<div>教育信息: ' + result['data']['eduInfo'] + '</div>' +
                '<div>工作信息: ' + result['data']['workInfo'] + '</div>' +
                '<div>婚姻状况: ' + result['data']['marriage'] + '</div>' +
                '<div>标签: ' + result['data']['tags'] + '</div>';
            document.getElementById('home_page_info').innerHTML = homePageHtml(result['data']);

        }
    });
}

// 'FCoPpaIQp'
function commentTimeLine(tweetId) {
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/comment/timeCnt/stat',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tweetId}),
        success: function (result) {
            var timeCntData = result['data'];

            var data = [];
            for (var i = 0; i < timeCntData.length; i++) {
                var t = [];
                t[0] = timeCntData[i]['time'];
                t[1] = timeCntData[i]['count'];
                data[i] = t;
            }
            chart = Highcharts.chart('comment-line', {
                chart: {
                    zoomType: 'x'
                },
                title: {
                    text: '评论走势'
                },
                subtitle: {
                    text: document.ontouchstart === undefined ?
                        '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                },
                /*xAxis: {
                 type: 'datetime',
                 dateTimeLabelFormats: {
                 millisecond: '%H:%M:%S.%L',
                 second: '%H:%M:%S',
                 minute: '%H:%M',
                 hour: '%H:%M',
                 day: '%m-%d',
                 week: '%m-%d',
                 month: '%Y-%m',
                 year: '%Y'
                 }
                 },*/
                /*tooltip: {
                 dateTimeLabelFormats: {
                 millisecond: '%H:%M:%S.%L',
                 second: '%H:%M:%S',
                 minute: '%H:%M',
                 hour: '%H:%M',
                 day: '%Y-%m-%d',
                 week: '%m-%d',
                 month: '%Y-%m',
                 year: '%Y'
                 }
                 },*/
                yAxis: {
                    title: {
                        text: '评论数'
                    }
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    area: {
                        fillColor: {
                            linearGradient: {
                                x1: 0,
                                y1: 0,
                                x2: 0,
                                y2: 1
                            },
                            stops: [
                                [0, Highcharts.getOptions().colors[0]],
                                [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                            ]
                        },
                        marker: {
                            radius: 2
                        },
                        lineWidth: 1,
                        states: {
                            hover: {
                                lineWidth: 1
                            }
                        },
                        threshold: null
                    }
                },
                series: [{
                    type: 'area',
                    name: '微博时间-评论',
                    data: data
                }]
            });
        }
    });

}

function source(tweetId) {
    $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/comment/source/stat',
            contentType: "application/json",
            data: JSON.stringify({'tweetId': tweetId}),
            success: function (result) {
                var source_data = result['data'];
                var seriesData = [];
                var legendData = [];
                for (var i = 0; i < source_data.length; i++) {
                    legendData.push(source_data[i]['source']);
                    seriesData.push({name: source_data[i]['source'], value: source_data[i]['count']})
                }
                option = {
                    title: {
                        text: '微博评论客户端统计',
                        x: 'center'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        type: 'scroll',
                        orient: 'vertical',
                        right: 10,
                        top: 20,
                        bottom: 20,
                        data: legendData
                    },
                    series: [
                        {
                            name: '来源',
                            type: 'pie',
                            radius: '55%',
                            center: ['40%', '50%'],
                            data: seriesData,
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                };
                var myChart = echarts.init(document.getElementById("rz"));
                myChart.setOption(option);
            }
        }
    );
}
