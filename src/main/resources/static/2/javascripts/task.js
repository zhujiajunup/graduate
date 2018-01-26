$(document).ready(function () {
        $.ajax({
                type: 'POST',
                dataType: 'json',
                url: '/task/all',
                contentType: "application/json",
                success: function (result) {
                    var trs = '';
                    for (var i = 0; i < result['data'].length; i++) {
                        var task = result['data'][i];
                        trs += '<tr>' +
                            '<td>' +
                            '<div class="progress progress-sm progress-striped active m-t-xs m-b-none">' +
                            '<div class="progress-bar bg-success" data-toggle="tooltip" data-original-title="80%" style="width: 80%"></div>' +
                            '</div>' +
                            '</td>' +
                            '<td><a href="htmls/treecontact.html?tid=' + task['weiboId'] + '">' + task['weiboId'] + '</a></td>' +
                            '<td>' + task['weiboUser'] + '</td>' +
                            '<td>' + task['weiboContent'] + '</td>' +
                            '<td>' + task['createTime'] + '</td>' +
                            '<td class="text-right">' +
                            '<div class="btn-group">' +
                            '<a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-pencil"></i></a>' +
                            '<ul class="dropdown-menu pull-right">' +
                            '<li><a href="#">Action</a></li>' +
                            '<li><a href="#">Another action</a></li>' +
                            '<li><a href="#">Something else here</a></li>' +
                            '<li class="divider"></li>' +
                            '<li><a href="#">Separated link</a></li>' +
                            '</ul>' +
                            '</div>' +
                            '</td>' +
                            '</tr>'
                    }
                    document.getElementById('tasks').innerHTML += trs

                }
            }
        )
    }
);

function addTask() {
    var inputId = $("#exampleInputName2").val();
    $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/task/add',
            contentType: "application/json",
            data: JSON.stringify({'weiboId': inputId}),
            success: function (result) {
                console.log(result)

            }
        }
    )

}