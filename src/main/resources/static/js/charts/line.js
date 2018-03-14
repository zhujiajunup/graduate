export default function Line(name, id, url) {
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
    tooltip: {
      trigger: "axis"
    },
    legend: {
      data: res.legend
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true
    },
    toolbox: {
      feature: {
        saveAsImage: {}
      }
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: res.label
    },
    yAxis: {
      type: "value"
    },
    series: res.series.map(o => ({ type: "line", ...o }))
  };
}
