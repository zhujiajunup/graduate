export default function CommentTop(name, tid) {
    var dom = document.getElementById('n_comment_top');
    var data = [];
    data.push({"nickname": "hello", "fans": "123", "follows": "123", "turl": "https://weibo.cn/adfadf"});
    var tbody = "<tbody>";
    for(var d in data){
        tbody += ' <tr>'+
            '<th scope="row">'+d['nickname']+'</th>'+
            '<td>'+d['fans']+'</td>'+
            '<td>'+d['follows']+'</td>'+
            '<td>'+d['turl']+'</td>'+
        '</tr>';

    }
    dom.innerHTML += tbody;
    // var myChart = echarts.init(dom);
    // myChart.showLoading();
    // $.ajax({
    //     type: 'POST',
    //     dataType: 'json',
    //     url: '/tweet/getChain',
    //     contentType: "application/json",
    //     data: JSON.stringify({'tweetId': tid}),
    //     success: function(res) {
    //         myChart.hideLoading();
    //         myChart.setOption(getOption(name, res.data));
    //     }});
}