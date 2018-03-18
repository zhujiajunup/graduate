export default function Graph(name, id, tid) {
  var dom = document.getElementById(id);
  var myChart = echarts.init(dom);
  myChart.showLoading();
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/tweet/getChain',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tid}),
        success: function(res) {
          myChart.hideLoading();
          myChart.setOption(getOption(name, res.data));
  }});
}

function getOption(name, res) {
  return {
    tooltip: {},
    legend: [
      {
        data: res.categories.map(a => a.name)
      }
    ],
    animation: false,
    series: [
      {
        name: name,
        type: "graph",
        layout: res.type,
        data: res.nodes.map(function(node, idx) {
          node.id = idx;
          return node;
        }),
        links: res.links,
        categories: res.categories,
        roam: true,
        label: {
          normal: {
            position: "right",
            formatter: "{b}"
          }
        },
        force: {
          repulsion: 20
        },
        lineStyle: {
          normal: {
            color: "source"
            // curveness: 0.3
          }
        }
      }
    ]
  };
}
