export default function UserInfo(id, tid) {
    var dom = document.getElementById(id);
    $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/tweet/getById',
            contentType: "application/json",
            data: JSON.stringify({'tweetId': tid}),
            success: function (result) {
                dom.innerHTML = '<p class="card-text" >'+result.data.content+'</p>\n' +
                    '                <div class="text-muted">\n' +
                    '                    <i class="fa fa-clock-o icon-muted"></i>'+ result.data.time+'\n' +
                    '                    <i class="fa fa-retweet icon-muted"></i>\n' +
                    '                    <a href="#" class="m-l-sm"><i class="fa fa-comment-o icon-muted"></i>1</a>\n' +
                    '                    <i class="fa fa-thumbs-up"></i>123\n' +
                    '                    <i class="fa fa-mobile"></i>213\n' +
                    '                </div>'

            }
        }
    )

}