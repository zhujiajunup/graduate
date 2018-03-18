export default function UserInfo(id, tid) {
    var dom = document.getElementById(id);
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/usr/pub',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid}),
        success: function (result) {
            var fansNum = result['data']['fansNum'] < 1000? result['data']['fansNum'] : parseInt(result['data']['fansNum']/1000)+'万';
            var followNum = result['data']['followNum'] < 1000? result['data']['followNum'] : parseInt(result['data']['followNum']/1000)+'万';
            var tweetNum = result['data']['tweetNum'] < 1000? result['data']['tweetNum'] : parseInt(result['data']['tweetNum']/1000)+'万';

            dom.innerHTML = '<div class="panel-body">\n' +
                '                        <div class="clearfix text-center m-t">\n' +
                '                            <div class="inline">\n' +
                '                                <div class="easypiechart easyPieChart" data-percent="75" data-line-width="5"\n' +
                '                                     data-bar-color="#4cc0c1" data-track-color="#f5f5f5" data-scale-color="false"\n' +
                '                                     data-size="134" data-line-cap="butt" data-animate="1000"\n' +
                '                                     style="width: 134px; height: 134px; line-height: 134px;">\n' +
                '                                    <div class="thumb-lg">\n' +
                '                                        <img src="'+result.data['head'] +'"\n' +
                '                                             class="img-circle" alt="...">\n' +
                '                                    </div>\n' +
                '                                    <canvas width="134" height="134"></canvas>\n' +
                '                                </div>\n' +
                '                                <div class="h4 m-t m-b-xs">'+result.data['nickname'] + '</div>\n' +
                '                                <small class="text-muted m-b">'+result.data['signature'] + '</small>\n' +
                '                            </div>\n' +
                '                        </div>\n' +
                '                    </div>\n' +
                '                    <footer class="panel-footer bg-info text-center">\n' +
                '                        <div class="row pull-out">\n' +
                '                            <div class="col-xs-4">\n' +
                '                                <div class="padder-v">\n' +
                '                                    <span class="m-b-xs h3 block text-white">'+followNum + '</span>\n' +
                '                                    <small class="text-muted">关注</small>\n' +
                '                                </div>\n' +
                '                            </div>\n' +
                '                            <div class="col-xs-4 dk">\n' +
                '                                <div class="padder-v">\n' +
                '                                    <span class="m-b-xs h3 block text-white">'+fansNum+ '</span>\n' +
                '                                    <small class="text-muted">粉丝</small>\n' +
                '                                </div>\n' +
                '                            </div>\n' +
                '                            <div class="col-xs-4">\n' +
                '                                <div class="padder-v">\n' +
                '                                    <span class="m-b-xs h3 block text-white">'+tweetNum+'</span>\n' +
                '                                    <small class="text-muted">微博</small>\n' +
                '                                </div>\n' +
                '                            </div>\n' +
                '                        </div>\n' +
                '                    </footer>';

        }});

}