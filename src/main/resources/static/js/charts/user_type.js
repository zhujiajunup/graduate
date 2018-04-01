export default function UserType(id, tid) {
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
    return {
        title : {
            text: '参与者类型',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{b} : {d}%"
        },
        legend: {
            x : 'center',
            y : 'bottom',
            data:['橙V用户', '蓝V用户', '微博达人', '普通用户']
        },
        toolbox: {
            show : true,
            feature : {
                mark : {show: true},
                dataView : {show: true, readOnly: false},
                magicType : {
                    show: true,
                    type: ['pie', 'funnel']
                },
                restore : {show: true},
                saveAsImage : {show: true}
            }
        },
        calculable : true,
        series : [
            {
                type:'pie',
                radius : [30, 110],
                center : ['50%', '50%'],
                roseType : 'area',
                data:[
                    {value:10, name:'橙V用户'},
                    {value:5, name:'蓝V用户'},
                    {value:15, name:'微博达人'},
                    {value:25, name:'普通用户'}
                ]
            }
        ]
    };


}