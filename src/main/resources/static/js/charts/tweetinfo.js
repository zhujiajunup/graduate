export default function TweetInfo(id, tid) {
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
                    '                    <i class="fa fa-retweet icon-muted"></i>'+ result.data.transfer+
                    '                    <a href="#" class="m-l-sm"><i class="fa fa-comment-o icon-muted"></i>'+result.data.comment+'</a>\n' +
                    '                    <i class="fa fa-thumbs-up"></i>' + result.data.like +
                    '                </div>'

            }
        }
    )

}