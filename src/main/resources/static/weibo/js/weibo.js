$(document).ready(function () {
    var uid = getQueryString("uid");
    loadPageInfo(uid);
    loadTweets(uid);

    option = {
        color: ['#3398DB'],
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                data : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : [
            {
                name:'直接访问',
                type:'bar',
                barWidth: '60%',
                data:[10, 52, 200, 334, 390, 330, 220]
            }
        ]
    };

    // var myChart = echarts.init(document.getElementById("home_page_info1"));
    // myChart.setOption(option);
});

function loadPageInfo(uid) {
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/usr/byId',
        contentType: "application/json",
        data: JSON.stringify({'uid': uid}),
        success: function (result) {
            document.getElementById('home_page_info').insertAdjacentHTML('beforeend', homePageHtml(result['data']));
        }
    });
}

function loadTweets(uid) {
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/tweet/getByUid',
        contentType: "application/json",
        data: JSON.stringify({'uid': uid}),
        success: function (result) {
            console.log(result);
            var tweetsHtml = '';
            for(var i = 0; i < result['data'].length; i ++){
                tweetsHtml += '<div class="col-sm-12 col-xs-12 message">' + tweetInfoHtml(result['data'][i]) + '</div>';
            }
            document.getElementById('tweets').innerHTML = tweetsHtml;
        }
    });
}
