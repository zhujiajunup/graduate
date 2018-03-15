export default function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}
function homePageHtml(userInfo) {
    return '<div class="row text-center inform">' +
        '<img src="' + userInfo['head'] + '">' +
        '<h4 style="font-weight: bold;">' + userInfo['nickname'] + '</h4>' +
        '<div class="col-sm-12 my_inform">' +
        '<div class="col-sm-4 col-xs-4">' +
        '<div>' + userInfo['followNum'] + '</div>' +
        '<div class="sort">关注</div>' +
        '</div>' +
        '<div class="col-sm-4 col-xs-4">' +
        '<div>' + userInfo['fansNum'] + '</div>' +
        '<div class="sort">粉丝</div>' +
        '</div>' +
        '<div class="col-sm-4 col-xs-4">' +
        '<div><a href="../../weibo/index.html?uid=' + userInfo['id'] + '" target="_blank">' + userInfo['tweetNum'] + '</a></div>' +
        '<div class="sort">博客</div>' +
        '</div>' +
        '</div>' +
        '</div>';

}

function tweetInfoHtml(tweetInfo) {
    return '<div class="post-item">' +
        '<div class="caption wrapper-lg">' +
        '<div class="post-sum">' +
        '    <p>' + tweetInfo['content'] +
        '    </p>' +
        '</div>' +
        '<div class="line line-lg"></div>' +
        '<div class="text-muted">' +
        '    <i class="fa fa-clock-o icon-muted"></i> ' + tweetInfo['time'] +
        '    <i class="fa fa-retweet icon-muted"></i> ' + tweetInfo['transfer'] +
        '<a href="#" class="m-l-sm"><i class="fa fa-comment-o icon-muted"></i>' + tweetInfo['comment'] + '</a>' +
        '    <i class="fa-thumbs-o-up"></i> ' + tweetInfo['like'] +
    '    <i class="icon-screen-smartphone"></i> ' + tweetInfo['source'] +
        '</div>' +
        '</div>' +
        '</div>'
}