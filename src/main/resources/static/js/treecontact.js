//import Graph from './charts/graph.js';
import Tree from './charts/tree.js';
import PlaceBar from './charts/place_bar.js';
//import Scatter from './charts/scatter.js';
import Sex from './charts/sex.js';
import Distribution from './charts/distribution.js';
import Migration from './charts/migration.js';
import Pie from './charts/pie.js';
import TimeCount from './charts/timecount.js';
import Bar from './charts/bar.js';
import Wordcloud from './charts/wordcloud.js';
import GetQueryString  from './graduate.js';
import UserInfo from './charts/userinfo.js';
import TweetInfo from './charts/tweetinfo.js'
$(() => {
  //Graph('Example Graph', 'graph', GetQueryString('tid'));

  Tree('graph', GetQueryString('tid'));
  Distribution( 'scatter', 'data/scatter.json');
  Migration('模拟迁徙', 'migration', GetQueryString('tid'));
  Pie('同名数量统计', 'pie', GetQueryString('tid'));
  TimeCount('折线图堆叠', 'time_count', GetQueryString('tid'));
  Bar('asdfg', 'bar', 'data/bar.json');
  Wordcloud('词云', 'wordcloud', 'data/wordcloud.json');
  Sex('sex', GetQueryString('tid'));
  Sex('sex2', GetQueryString('tid'));
  PlaceBar('placeBar', GetQueryString('tid'));
  UserInfo('user_info', GetQueryString('tid'));
  TweetInfo('tweet_info', GetQueryString('tid'));
})