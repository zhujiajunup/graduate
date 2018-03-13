import Graph from './charts/graph.js';
import Scatter from './charts/scatter.js';
import Migration from './charts/migration.js';
import Pie from './charts/pie.js';
import Line from './charts/line.js';
import Bar from './charts/bar.js';
import Wordcloud from './charts/wordcloud.js';
$(() => {
  Graph('Example Graph', 'graph', 'data/force.json');
  Scatter('微博签到数据点亮中国', 'scatter', 'data/scatter.json');
  Migration('模拟迁徙', 'migration', 'data/migration.json');
  Pie('同名数量统计', 'pie', 'data/pie.json');
  Line('折线图堆叠', 'line', 'data/line.json');
  Bar('asdfg', 'bar', 'data/bar.json');
  Wordcloud('词云', 'wordcloud', 'data/wordcloud.json');
})