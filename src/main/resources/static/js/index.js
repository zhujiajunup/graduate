$(() => {
  console.log('app start');
  const addbtn = $('#weiboClick');
  const content$I = $('#content');
  const weiboTable = $('#weibo-table');
  const userTable = $('#user-table');
  const topicTable = $('#topic-table');
  function getLocalTime(nS) {
    return new Date(parseInt(nS) * 1000).toLocaleString().replace(/:\d{1,2}$/,' ');
  }
function addRow(rows, type) {
    if (rows == null) throw 'rows is null';
    if (!$.isArray(rows)) rows = [rows];
    if(type == 'tweet'){
        rows = rows.map(row => $(
            `<tr>
        <td>
          <div class="progress">
            <div class="progress-bar" role="progressbar" style="width:${row.progress || 20}%" aria-valuenow="${row.progress || 20}" aria-valuemin="${row.progress || 20}" aria-valuemax="100"></div>
          </div>
        </td>
        <td>
          <a href="treecontact.html?tid=${row.weiboId}">${row.weiboId}</a>
        </td>
        <td>${row.weiboUser}</td>
        <td>
            <div style="width: 450px;">
                ${row.weiboContent}
            </div>
        </td>
        <td>${row.createTime}</td>
        <td>
                    <a href="#" class="btn btn-sm btn-info disabled">删除</a>
                </td>
      </tr>`
        ));
        weiboTable.append(rows);
    }else if(type == 'user'){
        rows = rows.map(row => $(
            `<tr>
                <td style="text-align: center">${row.status}</td>
                <td style="text-align: center">${row.userId}</a></td>
                <td style="text-align: center">${row.nickname}</td>
                <td style="text-align: center">${row.gender}</td>
                <td style="text-align: center">${row.place}</td>
                <td style="text-align: center">${row.tweetNum}</td>
                <td style="text-align: center">${row.fansNum}</td>
                <td style="text-align: center">${row.followNum}</td>
                <td style="text-align: center">${row.createTime}</td>
                <td>
                    <a href="#" class="btn btn-sm btn-info">删除</a>
                </td>
            </tr>
            `
        ));
        userTable.append(rows);
    }else if(type == 'topic'){
        rows = rows.map(row => $(
            `<tr>
                <td style="text-align: center">${row.status}</td>
                <td style="text-align: center">${row.topic}</a></td>
                <td style="text-align: center">${row.createTime}</td>
                <td>
                    <a href="#" class="btn btn-sm btn-info">删除</a>
                </td>
            </tr>
            `
        ));
        topicTable.append(rows);
    }

  }

  function initForm() {
    addbtn.click(e => {
      e.preventDefault();
      var jobType = $("#jobType").val();
      console.log(jobType);
      const content = content$I.val().trim();
      if (content === "") return;
      $.ajax({
              type: 'POST',
              dataType: 'json',
              url: '/task/add',
              contentType: "application/json",
              data: JSON.stringify({ 'content': content, 'type': jobType}),
              success: function (result) {
                if(result.code != 200){
                  alert(result.msg);
                }
                  //document.getElementById("tweet_content").innerHTML = tweetInfoHtml(result['data']);
                  console.log(result)
              }
          }
      );
    })
  }

  function initTable() {
    $.post('/task/tweet/all').done(res => { addRow(res.data, 'tweet') });
    $.post('/task/user/all').done(res => { addRow(res.data, 'user') });
      $.post('/task/topic/all').done(res => { addRow(res.data, 'topic') });
  }

  initForm();
  initTable();
})