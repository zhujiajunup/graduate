const planePath = 'path://M1705.06,1318.313v-89.254l-319.9-221.799l0.073-208.063c0.521-84.662-26.629-121.796-63.961-121.491c-37.332-0.305-64.482,36.829-63.961,121.491l0.073,208.063l-319.9,221.799v89.254l330.343-157.288l12.238,241.308l-134.449,92.931l0.531,42.034l175.125-42.917l175.125,42.917l0.531-42.034l-134.449-92.931l12.238-241.308L1705.06,1318.313z';

export default function Migration(name, id, url) {  
  var dom = document.getElementById(id);
  var myChart = echarts.init(dom);
  myChart.showLoading();
  $.get('data/china.json').done(china => {
    echarts.registerMap('china', china);
    $.get(url).done(res => {
      myChart.hideLoading();
      myChart.setOption(getOption(name, res))
    });
  });
}

function getOption(name, res) {
  const getData = key => res.link[key].map(c => ({
    fromName: key,
    toName: c.name,
    coords: [res.city[key], res.city[c.name]]
  }));
  
  const series = [];
  res.item.map((item, idx) => {
    const data = getData(item.key);
    series.push({
      name: item.name,
      type: 'lines',
      zlevel: 1,
      effect: {
        show: true,
        period: 6,
        trailLength: 0.7,
        color: '#fff',
        symbolSize: 3
      },
      lineStyle: {
        normal: {
          color: res.color[idx],
          width: 0,
          curveness: 0.2
        }
      },
      data: data
    }, {
        name: item.name,
        type: 'lines',
        zlevel: 2,
        symbol: ['none', 'arrow'],
        symbolSize: 10,
        effect: {
          show: true,
          period: 6,
          trailLength: 0,
          symbol: planePath,
          symbolSize: 15
        },
        lineStyle: {
          normal: {
            color: res.color[idx],
            width: 1,
            opacity: 0.6,
            curveness: 0.2
          }
        },
        data: data
      },
      {
        name: item.name,
        type: 'effectScatter',
        coordinateSystem: 'geo',
        zlevel: 2,
        rippleEffect: {
          brushType: 'stroke'
        },
        label: {
          normal: {
            show: true,
            position: 'right',
            formatter: '{b}'
          }
        },
        symbolSize: val => val[2] / 8,
        itemStyle: {
          normal: {
            color: res.color[idx]
          }
        },
        data: res.link[item.key].map((dataItem) => ({
          name: dataItem.name,
          value: res.city[dataItem.name].concat([dataItem.value])
        }))
      }
    )
  });

  return {
    backgroundColor: '#404a59',
    title: {
      text: name,
      left: 'center',
      textStyle: {
        color: '#fff'
      }
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      top: 'bottom',
      left: 'right',
      data: res.item.map(item => item.name),
      textStyle: {
        color: '#fff'
      },
      selectedMode: 'single'
    },
    geo: {
      map: 'china',
      label: {
        emphasis: {
          show: false
        }
      },
      roam: true,
      itemStyle: {
        normal: {
          areaColor: '#323c48',
          borderColor: '#404a59'
        },
        emphasis: {
          areaColor: '#2a333d'
        }
      }
    },
    series: series
  };
}
