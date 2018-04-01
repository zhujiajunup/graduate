export default function CommentTop(name, tid) {
    var dom = document.getElementById('n_comment_top');
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/comment/top10',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid,'type': 1}),
        success: function (res) {
            console.log(res);
            var tbody = '';
            for (var i = 0; i < res.data.length; i++) {
                var d = res.data[i];
                tbody += '<article  class="comment-item">\n' +
                    '                    <a class="pull-left thumb-sm avatar">\n' +
                    '                        <img src="' + d['user']['head'] + '"\n' +
                    '                             class="img-circle" alt="...">\n' +
                    '                    </a>\n' +
                    '                    <span class="arrow left"></span>\n' +
                    '                    <section class="comment-body panel panel-default">\n' +
                    '                        <header class="panel-heading bg-white">\n' +
                    '                            <a href="#">' + d['user']['nickname'] + '</a>\n' +

                    '                        </header>\n' +
                    '                        <div class="panel-body">\n' +
                    '                            <div>' + d['comment']['content'] + '\n' +
                    '                            </div>\n' +
                    '                            <div class="comment-action m-t-sm">\n' +
                    '                               <div class="text-muted">\n' +
                    '                    <i class="fa fa-clock-o icon-muted"></i>'+d['comment']['pubTime']+' 来自 ' + d['comment']['source']+'\n' +
                    '                            </div>\n' +
                    '                        </div>\n' +
                    '                    </section>\n' +
                    '                </article>'

            }


            dom.innerHTML += tbody;
        }
    });
    var data = [];
    data.push({
        "nickname": "hello",
        "fans": "123",
        "follows": "123",
        "content": "巨头，永垂青史也成定局！！走好，感谢你的贡献[心][心][蜡烛][蜡烛]",
        "turl": "https://weibo.cn/adfadf",
        "head": "http://tva2.sinaimg.cn/crop.0.0.179.179.1024/7361c1c3gw1ejb5jm4zwyj2050050aa9.jpg"
    });

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