//import Graph from './charts/graph.js';
import Tree from './charts/tree.js';
import PlaceBar from './charts/place_bar.js';
//import Scatter from './charts/scatter.js';
import Sex from './charts/sex.js';
import Leader from './charts/leader.js';
import CommentTop from './charts/comment_top.js';
import Distribution from './charts/distribution.js';
import Migration from './charts/migration.js';
import Pie from './charts/pie.js';
import TimeCount from './charts/timecount.js';
import Wordcloud from './charts/wordcloud.js';
import GetQueryString  from './graduate.js';
import UserType from './charts/user_type.js';
import UserInfo from './charts/userinfo.js';
import TweetInfo from './charts/tweetinfo.js'
$(() => {
  //Graph('Example Graph', 'graph', GetQueryString('tid'));
  var tid = GetQueryString('tid');
  Tree('graph', GetQueryString('tid'));
  Distribution( 'scatter', 'data/scatter.json');
  Migration('模拟迁徙', 'migration', GetQueryString('tid'));
  Pie('同名数量统计', 'pie', GetQueryString('tid'));
  TimeCount('折线图堆叠', 'time_count', GetQueryString('tid'));
  Wordcloud('词云', 'wordcloud', tid);
  Sex('sex', GetQueryString('tid'));
  UserType('sex2', GetQueryString('tid'));
  PlaceBar('placeBar', GetQueryString('tid'));
  UserInfo('user_info', GetQueryString('tid'));
  TweetInfo('tweet_info', GetQueryString('tid'));
  CommentTop('comment top', tid);
  Leader('leader', tid);

})