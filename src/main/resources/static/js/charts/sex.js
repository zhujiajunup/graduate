export default function Sex(id, tid) {
    var dom = document.getElementById(id);
    var myChart = echarts.init(dom);
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/stat/sex',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid}),
        success: function (res) {
            myChart.hideLoading();
            myChart.setOption(getOption(res.data));
        }
    });
}
function getOption(res) {
    var legends = [];
    var data = [];
    $.map(res, function (value, index) {
        legends.push(index);
        data.push({'name': index, 'value': value})

    });
    return {
        title : {
            text: '参与者性别统计',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: legends
        },
        series : [
            {
                //name: '访问来源',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:data,
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

}