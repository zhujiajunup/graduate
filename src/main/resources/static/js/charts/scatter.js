export default function Scatter(name, id, url) {
  var dom = document.getElementById(id);
  var myChart = echarts.init(dom);

  myChart.showLoading();
  $.get("data/china.json").done(china => {
    echarts.registerMap("china", china);
    $.get(url).done(res => {
      myChart.hideLoading();

      myChart.setOption(getOption(name, res));
    });
  });
}

function getOption(name, res) {
  res.scatter.forEach(function(serieData, idx) {
    var px = serieData[0] / 1000;
    var py = serieData[1] / 1000;
    var cord = [[px, py]];

    for (var i = 2; i < serieData.length; i += 2) {
      var dx = serieData[i] / 1000;
      var dy = serieData[i + 1] / 1000;
      var x = px + dx;
      var y = py + dy;
      cord.push([x.toFixed(2), y.toFixed(2), 1]);

      px = x;
      py = y;
    }
    res.item[idx].scatter = cord;
  });
  return {
    backgroundColor: "#404a59",
    title: {
      text: name,
      left: "center",
      top: "top",
      textStyle: {
        color: "#fff"
      }
    },
    tooltip: {},
    legend: {
      left: "left",
      data: res.item.map(i => i.name),
      textStyle: {
        color: "#ccc"
      }
    },
    geo: {
      map: "china",
      roam: true,
      label: {
        emphasis: {
          show: false
        }
      },
      itemStyle: {
        normal: {
          areaColor: "#323c48",
          borderColor: "#111"
        },
        emphasis: {
          areaColor: "#2a333d"
        }
      }
    },
    series: res.item.map(i => ({
      name: i.name,
      type: "scatter",
      coordinateSystem: "geo",
      symbolSize: 1,
      large: true,
      itemStyle: {
        normal: {
          shadowBlur: 2,
          shadowColor: i.color,
          color: i.color
        }
      },
      data: i.scatter
    }))
  };
}
