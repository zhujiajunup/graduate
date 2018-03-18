export default function Tree(id, tid) {
    var dom = document.getElementById(id);
    var myChart = echarts.init(dom);
    myChart.showLoading();
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/tweet/getChain',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid}),
        success: function (data) {
            myChart.hideLoading();
            var option = {
                tooltip: {
                    trigger: 'item',
                    triggerOn: 'mousemove'
                },
                series: [
                    {
                        type: 'tree',

                        data: [data.data],

                        top: '18%',
                        bottom: '14%',

                        layout: 'radial',

                        symbol: 'emptyCircle',

                        symbolSize: 7,

                        initialTreeDepth: 3,

                        animationDurationUpdate: 750

                    }
                ]
            };
            myChart.setOption(option);
        }
    });
}