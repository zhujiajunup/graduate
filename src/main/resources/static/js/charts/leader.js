export default function Leader(name, tid) {
    var dom = document.getElementById('leader');
    var timeLineDom = document.getElementById('timeline');
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/stat/leader',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid}),
        success: function (res) {
            console.log(res);
            var data = res.data;
            var tbody = "<tbody>";
            var timeline = "";
            var left = true;
            for (var i = 0; i < data.length; i++) {
                var leader = data[i]['leader'];
                var tweet = data[i]['tweet'];
                var uurl = 'https://weibo.com/u/' + leader['id'];
                var turl = 'https://weibo.com/' + leader['id'] + '/' + leader['tid'];
                tbody += ' <tr>' +
                    '<th scope="row"><a href="' + uurl + '">' + leader['nickname'] + '</a></th>' +
                    '<td>' + leader['fansNum'] + '</td>' +
                    '<td>' + leader['followNum'] + '</td>' +
                    '<td>' + leader['place'] + '</td>' +
                    '<td><a href="' + turl + '">' + turl + '</td>' +
                    '</tr>';
                var direction = left ? 'left' : 'right';
                var alt = left ? '' : 'alt';
                timeline += '<article class="timeline-item ' + alt + '">\n' +
                    '                        <div class="timeline-caption">\n' +
                    '                            <div class="panel panel-default">\n' +
                    '                                <div class="panel-body">\n' +
                    '                                    <span class="arrow ' + direction + '"></span>\n' +
                    '                                    <span class="timeline-icon"><img src="' + leader['head'] + '" width="35px"></i></span>\n' +
                    '                                    <span class="timeline-date">' + tweet['time'] + '</span>\n' +
                    '                                    <h3 class="text-lg">' + leader['nickname'] + '</h3>\n' +
                    '                                    <h4>\n' + tweet['content'] +
                    '                                    </h4>\n' +
                    '<div class="text-muted">\n' +
                    '                    <font style="color:blue">转发</font>：'+tweet['transfer']+ '|'+
                    '                    <font style="color:blue">评论</font>：'+tweet['comment']+'\n' +'|'+
                    '                    <font style="color:blue">点赞</font>：'+tweet['like']+'</div>' +
                    '                                </div>\n' +
                    '                            </div>\n' +
                    '                        </div>\n' +
                    '                    </article>';
                left = !left;
            }
            dom.innerHTML += tbody;
            timeLineDom.innerHTML += timeline;
        }
    });
}