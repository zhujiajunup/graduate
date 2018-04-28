export default function Wordcloud(name, id, tid) {
  var dom = document.getElementById(id);
  var myChart = echarts.init(dom);
  myChart.showLoading();
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/comment/wordCloud',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid}),
        success:  function(res) {
          var result = res.data;
          var d = {};
          for(var i = 0 ; i < result.length; i ++){
           var wordcount = result[i];
           d[wordcount.word] = wordcount.count;
          }
          myChart.hideLoading();
          myChart.setOption(getOption(name, d));
        }
    });
    myChart.on('click', function (params) {
        console.log(params);
    });
}

function getOption(name, res) {
  return {
    series: [
      {
        type: "wordCloud",
        shape: "diamond",
        data: Object.keys(res)
          .map(key => ({ name: key, value: Math.sqrt(res[key]) }))
          .sort((a, b) => b.value - a.value),
        textStyle: {
          normal: {
            color: () =>
              `rgb(${[
                Math.round(Math.random() * 160),
                Math.round(Math.random() * 160),
                Math.round(Math.random() * 160)
              ].join(",")})`
          }
        }
      }
    ]
  };
}
