export default function CommentTop(name, tid) {
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/comment/top10',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid,'type': 2}),
        success: function (res) {
            var dom = document.getElementById('n_comment_top');
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
            dom.innerHTML = tbody;
        }
    });
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/comment/top10',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid,'type': 1}),
        success: function (res) {
            var dom = document.getElementById('p_comment_top');
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
            dom.innerHTML = tbody;
        }
    });
}