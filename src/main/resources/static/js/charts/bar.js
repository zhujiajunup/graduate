export default function Bar(name, id, url) {
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
    title: {
      text: name
    },
    xAxis: {
      type: "category",
      data: res.label
    },
    yAxis: {
      type: "value"
    },
    series: res.series
  };
}
