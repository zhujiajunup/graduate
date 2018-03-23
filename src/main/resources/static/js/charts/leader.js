export default function Leader(name, tid) {
    var dom = document.getElementById('leader');
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/stat/leader',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid}),
        success: function(res) {
            var data = res.data;
            var tbody = "<tbody>";
            for(var i = 0; i < data.length; i ++){
                var leader = data[i];
                var uurl = 'https://weibo.com/u/'+leader['id'];
                var turl = 'https://weibo.com/' + leader['id'] + '/' + leader['tid'];
                tbody += ' <tr>'+
                    '<th scope="row"><a href="'+uurl+'">'+leader['nickname']+'</a></th>'+
                    '<td>'+leader['fansNum']+'</td>'+
                    '<td>'+leader['followNum']+'</td>'+
                    '<td>'+leader['place']+'</td>'+
                    '<td><a href="'+turl +'">'+turl + '</td>'+
                    '</tr>';

            }
            dom.innerHTML += tbody;
        }});
}