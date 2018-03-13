$(() => {
  console.log('app start');
  const addbtn = $('#weiboClick');
  const weiboURL$I = $('#weiboURL');
  const table = $('#weibo-table');

  function addRow(rows) {
    if (rows == null) throw 'rows is null';
    if (!$.isArray(rows)) rows = [rows];
    rows = rows.map(row => $(
      `<tr>
        <td>
          <div class="progress">
            <div class="progress-bar" role="progressbar" style="width:${row.progress || 20}%" aria-valuenow="${row.progress || 20}" aria-valuemin="${row.progress || 20}" aria-valuemax="100"></div>
          </div>
        </td>
        <td>
          <a href="htmls/treecontact.html?tid=${row.weiboId}">${row.weiboId}</a>
        </td>
        <td>${row.weiboUser}</td>
        <td>${row.weiboContent}</td>
        <td>${row.createTime}</td>
      </tr>`
    ))
    table.append(rows);
  }

  function initForm() {
    addbtn.click(e => {
      e.preventDefault();
      const weiboURL = weiboURL$I.val().trim();
      if (weiboURL === "") return;
      $.post('/task/add', { weiboID: weiboURL }).done(res => {
        console.log(res);
        // add task to table
        addRow(res.data);
      })
    })
  }

  function initTable() {
    $.post('/task/all').done(res => { addRow(res.data) });
  }

  initForm();
  initTable();
})