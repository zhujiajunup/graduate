export default function Pie(name, id, tid) {
    var dom = document.getElementById(id);
    var myChart = echarts.init(dom);
    myChart.showLoading();

    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/comment/source/stat',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid}),
        success: function (res) {
            var legend = [];
            var series = [];
            res.data.forEach(function (elem) {
                legend.push(elem['source']);
                series.push({"value": elem['count'], "name": elem['source']});
            });
            myChart.hideLoading();
            myChart.setOption(getOption(name, {"legend": legend, "series": series}));
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
function getOption(name, res) {
    return {
        tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b}: {c} ({d}%)"
        },
        legend: {
            orient: "vertical",
            x: "left",
            data: res.legend
        },
        series: [
            {
                name: name,
                type: "pie",
                radius: ["40%", "60%"],
                data: res.series
            }
        ]
    };
}
