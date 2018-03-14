export default function Pie(name, id, url) {
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
    tooltip: {
      trigger: "item",
      formatter: "{a} <br/>{b}: {c} ({d}%)"
    },
    legend: {
      orient: "vertical",
      x: "left",
      data: res.legend
    },
    series: [
      {
        name: name,
        type: "pie",
        radius: ["40%", "60%"],
        data: res.series
      }
    ]
  };
}
