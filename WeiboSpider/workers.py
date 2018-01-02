from time import sleep
from setting import LOGGER, WEIBO_CN_HOST
from redis_cookies import RedisJob, RedisCookies
from bs4 import BeautifulSoup
from enum import Enum
import traceback
import requests
import re
from kafka import KafkaProducer
import json
import datetime

class JobType(Enum):
    comment = 'comment'
    tweet = 'tweet'
    follower = 'follower'
    user = 'user'


class WeiboProcuder:
    topic = 'sinaweibo'
    producer = KafkaProducer(bootstrap_servers=['localhost:9092'],
                             value_serializer=lambda msg: json.dumps(msg).encode('utf-8'))

    @classmethod
    def send(cls, msg):
        LOGGER.info('send type: %s, id: %s' % (msg['type'], msg['id']))
        cls.producer.send(topic=cls.topic, value=msg)
        LOGGER.info('send successful.')


class Utils(object):
    time_current_pattern = re.compile(r'(\d*)分钟前')
    time_today_pattern = re.compile(r'今天\s*(\d*):(\d*)')
    time_year_pattern = re.compile(r'(\d*)月(\d*)日\s*(\d*):(\d*)')

    @classmethod
    def get_time(cls, time_str):
        current_result = cls.time_current_pattern.findall(time_str)
        time_now = datetime.datetime.now()
        if current_result:
            result_time = time_now - datetime.timedelta(minutes=int(current_result[0]))
            return result_time.strftime('%Y-%m-%d %H:%M:%S')
        else:
            current_result = cls.time_today_pattern.findall(time_str)
            if current_result:
                result_time = datetime.datetime(time_now.year, time_now.month,
                                                time_now.day, int(current_result[0][0]), int(current_result[0][0]))
                return result_time.strftime('%Y-%m-%d %H:%M:%S')
            else:
                current_result = cls.time_year_pattern.findall(time_str)
                if current_result:
                    result_time = datetime.datetime(time_now.year, int(current_result[0][0]),
                                                    int(current_result[0][1]), int(current_result[0][2]),
                                                    int(current_result[0][3]))
                    return result_time.strftime('%Y-%m-%d %H:%M:%S')
                else:
                    return time_str

    @staticmethod
    def get_user_id_from_homepage(home_page):
        session = requests.Session()
        cookies = RedisCookies.fetch_cookies()
        response = session.get(home_page, cookies=cookies['cookies'], verify=False)
        home_page_html = BeautifulSoup(response.text, "lxml")
        info_a = home_page_html.find('a', string='资料')
        # LOGGER.info('get id from home page: %s' % home_page)
        if info_a:
            user_id = info_a.get('href').split('/')[1]
            # LOGGER.info('id got: %s' % user_id)
            return user_id
        return 0


class TweetWorker:

    def crawl_comment(self):
        while True:
            comment_job_info = RedisJob.fetch_job(JobType.comment.value)
            sleep(1)
            try:
                self.grab_tweet_comments(comment_job_info)
            except:
                LOGGER.error(traceback.format_exc())
                sleep(5 * 60)

    @staticmethod
    def grab_tweet_comments(comment_url):
        LOGGER.info('start grab: %s' % str(comment_url))
        session = requests.Session()
        cookies = RedisCookies.fetch_cookies()
        response = session.get(comment_url['url'], cookies=cookies['cookies'], verify=False)
        response.encoding = 'utf-8'
        comment_html = BeautifulSoup(response.text, "lxml")

        comment_divs = comment_html.find_all(id=re.compile('C_[\d]'), class_='c')
        for comment_div in comment_divs:
            comment_info = {}
            comment_id = comment_div.get('id')
            user_a = comment_div.find('a')
            if user_a:
                user_href = user_a.get('href')
                if user_href.startswith('/u/'):
                    user_id = user_href[3:]
                else:
                    user_id = Utils.get_user_id_from_homepage(WEIBO_CN_HOST + user_href)
                # self.user_id_in_queue(user_id)
                RedisJob.push_job(JobType.user.value, {'user_id', user_id})

                comment_info['userId'] = user_id
                comment_info['content'] = comment_div.find(class_='ctt').get_text()
                others = comment_div.find(class_='ct').get_text()
                if others:
                    others = others.split('\u6765\u81ea')
                    comment_info['pubTime'] = Utils.get_time(others[0])
                    if len(others) == 2:
                        comment_info['source'] = others[1]
                comment_info['id'] = comment_id
                comment_info['tweetId'] = comment_url['tweetId']
                comment_info['type'] = 'comment_info'
                WeiboProcuder.send(comment_info)
        if 'page=' not in comment_url['url']:
            tweet_div = comment_html.find(id='M_', class_='c')
            if tweet_div:
                tweet_user_a = tweet_div.find('a')
                if tweet_user_a:
                    tweet = {}
                    tweet_user_href = tweet_user_a.get('href')
                    if tweet_user_href.startswith('/u/'):
                        tweet_user_id = tweet_user_href[3:]
                    else:
                        tweet_user_id = Utils.get_user_id_from_homepage(WEIBO_CN_HOST + tweet_user_href)
                    tweet_content = tweet_div.find('span', class_='ctt').get_text()
                    others = tweet_div.find(class_='ct').get_text()
                    tweet_details = list(
                        filter(lambda div: div.find(class_='pms'), comment_html.find_all('div', id=False, class_=False)))
                    detail = tweet_details[0].get_text(';').replace('\xa0', '')
                    like = re.findall(u'\u8d5e\[(\d+)\];', detail)  # 点赞数
                    transfer = re.findall(u'\u8f6c\u53d1\[(\d+)\];', detail)  # 转载数
                    comment = re.findall(u'\u8bc4\u8bba\[(\d+)\];', detail)  # 评论数
                    if others:
                        others = others.split('\u6765\u81ea')
                        tweet['time'] = Utils.get_time(others[0])
                        if len(others) == 2:
                            tweet['source'] = others[1]
                    tweet['content'] = tweet_content
                    tweet['id'] = comment_url['tweetId']
                    tweet['like'] = like[0] if like else 0
                    tweet['transfer'] = transfer[0] if transfer else 0
                    tweet['comment'] = comment[0] if comment else 0
                    tweet['type'] = 'tweet_info'
                    tweet['uid'] = tweet_user_id
                    WeiboProcuder.send(tweet)

            page_div = comment_html.find(id='pagelist')
            if page_div:

                max_page = int(page_div.input.get('value'))
                for page in range(2, max_page + 1):
                    RedisJob.push_job(JobType.comment.value, {'url': self.tweet_comment_url2 % (comment_url['tweetId'], page),
                                            'tweetId': comment_url['tweetId']})

