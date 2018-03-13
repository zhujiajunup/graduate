export default function Wordcloud(name, id, url) {
  var dom = document.getElementById(id);
  var myChart = echarts.init(dom);
  myChart.showLoading();
  $.get(url, function(res) {
    myChart.hideLoading();
    myChart.setOption(getOption(name, res));
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
