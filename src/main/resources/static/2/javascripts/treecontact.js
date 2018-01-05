$(document).ready(function () {
});

function large() {
    var diameter = document.documentElement.scrollWidth * (2 / 3);
    d3.select("g.treezoom").attr("transform", "translate(" + diameter / 2 + "," + diameter / 2.5 + ")scale(2)");
}

function small() {
    var diameter = document.documentElement.scrollWidth * (2 / 3);
    d3.select("g.treezoom").attr("transform", "translate(" + diameter / 2 + "," + diameter / 2.5 + ")scale(1)");
}

function tree() {
    document.getElementById("tree").innerHTML = "";
    document.getElementById("line").innerHTML = "";
    document.getElementById("rz").innerHTML = "";
    // document.getElementById("ceng").innerHTML = "";
    // document.getElementById("sex").innerHTML = "";
    // document.getElementById("prov").innerHTML = "";


    function showload() {
        $("#dialog").show();
    }

    function hideload() {
        $("#dialog").hide();
    }

    var inputId = $("#exampleInputName2").val();
    source(inputId);
    commentTimeLine(inputId);
    userInfo(inputId);
    tweetContent(inputId);
}

function tweetContent(tweetId) {
    $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/tweet/getById',
            contentType: "application/json",
            data: JSON.stringify({'tweetId': tweetId}),
            success: function (result) {
                console.log(result['data']);
                document.getElementById("tweet_content").innerHTML =
                    '<div class="caption wrapper-lg">\n' +
                    '<div class="post-sum">\n' +
                    '    <p>' + result['data']['content'].substring(0, 140) +
                    '    </p>\n' +
                    '</div>\n' +
                    '<div class="line line-lg"></div>\n' +
                    '<div class="text-muted">\n' +
                    '    <i class="fa fa-clock-o icon-muted"></i>' + result['data']['time']+
                    '    <i class="fa fa-retweet icon-muted"></i>' + result['data']['transfer']+
                    '<a href="#" class="m-l-sm"><i class="fa fa-comment-o icon-muted"></i>'+ result['data']['comment']+'</a>\n' +
                    '    <i class="icon-like icon-muted"></i>' + result['data']['like']+
                    '</div>' +
                    '</div>'
            }
        }
    )
}

var margin = {
    top: 20,
    right: 50,
    bottom: 20,
    left: 50
};
var wikuang = document.documentElement.scrollWidth * (2 / 3) - 100;
var widthz = wikuang - margin.left - margin.right;
var heightz = 200 - margin.bottom - margin.top;

function createsvgzhe() {
    var svg = d3.select("#line").append("svg")
        .attr("width", widthz + margin.left + margin.right)
        .attr("height", heightz + margin.top + margin.bottom);
    return svg;
}

function render_time(one, two, three, four, five, six, seven, eight, nine, ten, eleven, twelve, thirteen, fourteen, fifteen,
                     sixteen, seventeen, eighteen, ninteen, twenty, twentyone, twentytwo, twentythree, twentyfour, svgname) {
    var datatime = [
        [
            [1, one], [2, two], [3, three], [4, four], [5, five], [6, six], [7, seven], [8, eight], [9, nine], [10, ten], [11, eleven], [12, twelve],
            [13, thirteen], [14, fourteen], [15, fifteen], [16, sixteen], [17, seventeen], [18, eighteen], [19, ninteen], [20, twenty], [21, twentyone],
            [22, twentytwo], [23, twentythree], [24, twentyfour]
        ]
    ];

    svgname.append("g")
        .attr("class", "zhexian")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var x = d3.scale.linear().range([0, widthz]);
    var y = d3.scale.linear().range([heightz, 0]);
    var xAxis = d3.svg.axis().scale(x).orient("bottom");
    var yAxis = d3.svg.axis().scale(y).orient("left");

    var tempData = [];
    for (var i = 0; i < datatime.length; i++) {
        for (var j = 0; j < datatime[i].length; j++) {
            tempData.push(datatime[i][j]);
        }
    }

    x.domain(d3.extent(tempData, function (d) {
        return d[0];
    }));
    y.domain(d3.extent(tempData, function (d) {
        return d[1];
    }));
    var area_generator = d3.svg.area()
        .x(function (d) {
            return x(d[0]);
        })
        .y0(heightz)
        .y1(function (d) {
            return y(d[1])
        })
        .interpolate("monotone")

    var paths = d3.select("g.zhexian").selectAll('.line').data(datatime);
    paths.enter().append("path").attr("class", "line").attr("d", area_generator).style("fill", "steelblue");
    paths.exit().remove();

    d3.select("g.zhexian").append("g")
        .attr("class", "y axis")
        .call(yAxis)
        .append("text")
        .text("转发量/条")
    d3.select("g.zhexian").append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + heightz + ")")
        .call(xAxis)
        .append("text")
        .text("时间/h")
        .attr("style", "z-index:1000")
        .attr("transform", "translate(" + (wikuang - 120) + ",0)");

}//zhexian

function userInfo(tweetId) {
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/usr/pub',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tweetId}),
        success: function (result) {
            console.log(result);
            document.getElementById('user_info').innerHTML =
                '<div>昵称:' + result['data']['nickname'] + '</div>' +
                '<div>所在地: ' + result['data']['place'] + '</div>' +
                '<div>性别:' + result['data']['gender'] + '</div>' +
                '<div>性取向: ' + result['data']['sexOrientation'] + '</div>' +
                '<div>生日: ' + result['data']['birthday'] + '</div>' +
                '<div>简介: ' + result['data']['signature'] + '</div>' +
                '<div>教育信息: ' + result['data']['eduInfo'] + '</div>' +
                '<div>工作信息: ' + result['data']['workInfo'] + '</div>' +
                '<div>婚姻状况: ' + result['data']['marriage'] + '</div>' +
                '<div>标签: ' + result['data']['tags'] + '</div>'
        }
    });
}

// 'FCoPpaIQp'
function commentTimeLine(tweetId) {
    $.ajax({
        type: 'POST',
        dataType: 'json',
        url: '/comment/timeCnt/stat',
        contentType: "application/json",
        data: JSON.stringify({'tweetId': tweetId}),
        success: function (result) {
            var timeCntData = result['data'];

            var data = [];
            for (var i = 0; i < timeCntData.length; i++) {
                var t = [];
                t[0] = timeCntData[i]['time'];
                t[1] = timeCntData[i]['count'];
                data[i] = t;
            }
            console.log(data);
            chart = Highcharts.chart('comment-line', {
                chart: {
                    zoomType: 'x'
                },
                title: {
                    text: '评论走势'
                },
                subtitle: {
                    text: document.ontouchstart === undefined ?
                        '鼠标拖动可以进行缩放' : '手势操作进行缩放'
                },
                /*xAxis: {
type: 'datetime',
dateTimeLabelFormats: {
    millisecond: '%H:%M:%S.%L',
    second: '%H:%M:%S',
    minute: '%H:%M',
    hour: '%H:%M',
    day: '%m-%d',
    week: '%m-%d',
    month: '%Y-%m',
    year: '%Y'
}
                },*/
                /*tooltip: {
dateTimeLabelFormats: {
    millisecond: '%H:%M:%S.%L',
    second: '%H:%M:%S',
    minute: '%H:%M',
    hour: '%H:%M',
    day: '%Y-%m-%d',
    week: '%m-%d',
    month: '%Y-%m',
    year: '%Y'
}
                },*/
                yAxis: {
                    title: {
                        text: '评论数'
                    }
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    area: {
                        fillColor: {
                            linearGradient: {
                                x1: 0,
                                y1: 0,
                                x2: 0,
                                y2: 1
                            },
                            stops: [
                                [0, Highcharts.getOptions().colors[0]],
                                [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                            ]
                        },
                        marker: {
                            radius: 2
                        },
                        lineWidth: 1,
                        states: {
                            hover: {
                                lineWidth: 1
                            }
                        },
                        threshold: null
                    }
                },
                series: [{
                    type: 'area',
                    name: '微博时间-评论',
                    data: data
                }]
            });
        }
    });

}

function source(tweetId) {
    $.ajax({
            type: 'POST',
            dataType: 'json',
            url: '/comment/source/stat',
            contentType: "application/json",
            data: JSON.stringify({'tweetId': tweetId}),
            success: function (result) {
                var source_data = result['data'];
                var seriesData = [];
                var legendData = [];
                for (var i = 0; i < source_data.length; i++) {
                    legendData.push(source_data[i]['source']);
                    seriesData.push({name: source_data[i]['source'], value: source_data[i]['count']})
                }
                option = {
                    title: {
                        text: '微博评论客户端统计',
                        x: 'center'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        type: 'scroll',
                        orient: 'vertical',
                        right: 10,
                        top: 20,
                        bottom: 20,
                        data: legendData
                    },
                    series: [
                        {
                            name: '来源',
                            type: 'pie',
                            radius: '55%',
                            center: ['40%', '50%'],
                            data: seriesData,
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                };
                var myChart = echarts.init(document.getElementById("rz"));
                myChart.setOption(option);
            }
        }
    );
}

function render_rect1(data) {
    var width = document.documentElement.scrollWidth * (1 / 3) - 150,
        height = 60,
        margin = {left: 40, right: 40, top: 10, bottom: 10},
        svg_height = height + margin.top + margin.bottom,
        svg_width = width + margin.left + margin.right;

    var scale = d3.scale.linear()
        .domain([0, d3.max(data, function (d) {
            return d.bili;
        })])
        .range([0, width]);

    var scale_y = d3.scale.ordinal()
        .domain(data.map(function (d) {
            return d.str;
        }))
        .rangeBands([0, height], 0.1);

    var svg = d3.select("#rz")
        .append("svg")
        .attr("width", svg_width)
        .attr("height", svg_height)

    var chart = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var y_axis = d3.svg.axis().scale(scale_y).orient("left");
    chart.append("g").call(y_axis);

    var bar = chart.selectAll(".bar")
        .data(data)
        .enter()
        .append("g")
        .attr("class", "bar")
        .attr("transform", function (d, i) {
            return "translate(0," + scale_y(d.str) + ")";
        })

    bar.append("rect")
        .attr({
            "y": function (d) {
                return scale_y();
            },
            "width": function (d) {
                return scale(d.bili)
            },
            "height": scale_y.rangeBand()
        })
        .style("fill", "steelblue")

    bar.append("text")
        .text(function (d) {
            return d.bili + "%";
        })
        .attr({
            "x": function (d) {
                return scale(d.bili);
            },
            "y": scale_y.rangeBand() / 2,
            "text-anchor": "start"
        })

}//rect1
function render_rect2(data) {
    var width = document.documentElement.scrollWidth * (1 / 3) - 150,
        height = 180,
        margin = {left: 40, right: 40, top: 10, bottom: 10},
        svg_height = height + margin.top + margin.bottom,
        svg_width = width + margin.left + margin.right;

    var scale = d3.scale.linear()
        .domain([0, d3.max(data, function (d) {
            return d.bili;
        })])
        .range([0, width]);

    var scale_y = d3.scale.ordinal()
        .domain(data.map(function (d) {
            return d.str;
        }))
        .rangeBands([0, height], 0.1);

    var svg = d3.select("#ceng")
        .append("svg")
        .attr("width", svg_width + 10)
        .attr("height", svg_height)

    var chart = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var y_axis = d3.svg.axis().scale(scale_y).orient("left");
    chart.append("g").call(y_axis);

    var bar = chart.selectAll(".bar")
        .data(data)
        .enter()
        .append("g")
        .attr("class", "bar")
        .attr("transform", function (d, i) {
            return "translate(0," + scale_y(d.str) + ")";
        })

    bar.append("rect")
        .attr({
            "y": function (d) {
                return scale_y();
            },
            "width": function (d) {
                return scale(d.bili)
            },
            "height": scale_y.rangeBand()
        })
        .style("fill", "steelblue")

    bar.append("text")
        .text(function (d) {
            return d.bili + "%";
        })
        .attr({
            "x": function (d) {
                return scale(d.bili);
            },
            "y": scale_y.rangeBand() / 2,
            "text-anchor": "start"
        })

}//rect2
function render_rect3(data) {
    var width = document.documentElement.scrollWidth * (1 / 3) - 150,
        height = 60,
        margin = {left: 40, right: 40, top: 10, bottom: 10},
        svg_height = height + margin.top + margin.bottom,
        svg_width = width + margin.left + margin.right;

    var scale = d3.scale.linear()
        .domain([0, d3.max(data, function (d) {
            return d.bili;
        })])
        .range([0, width]);

    var scale_y = d3.scale.ordinal()
        .domain(data.map(function (d) {
            return d.str;
        }))
        .rangeBands([0, height], 0.1);

    var svg = d3.select("#sex")
        .append("svg")
        .attr("width", svg_width)
        .attr("height", svg_height)

    var chart = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var y_axis = d3.svg.axis().scale(scale_y).orient("left");
    chart.append("g").call(y_axis);

    var bar = chart.selectAll(".bar")
        .data(data)
        .enter()
        .append("g")
        .attr("class", "bar")
        .attr("transform", function (d, i) {
            return "translate(0," + scale_y(d.str) + ")";
        })

    bar.append("rect")
        .attr({
            "y": function (d) {
                return scale_y();
            },
            "width": function (d) {
                return scale(d.bili)
            },
            "height": scale_y.rangeBand()
        })
        .style("fill", "steelblue")

    bar.append("text")
        .text(function (d) {
            return d.bili + "%";
        })
        .attr({
            "x": function (d) {
                return scale(d.bili);
            },
            "y": scale_y.rangeBand() / 2,
            "text-anchor": "start"
        })

}//rect3