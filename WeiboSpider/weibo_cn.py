# -*- coding:utf-8 -*-
import json
import random
import re
import datetime
import traceback
from queue import Queue
from time import sleep
import threading
import requests
from functools import reduce
from bs4 import BeautifulSoup
from kafka import KafkaProducer

import user_agents
from redis_cookies import RedisCookies
from setting import LOGGER
from pybloom import ScalableBloomFilter

class WeiboProcuder:
    def __init__(self, bootstrap_servers, topic):
        self.topic = topic
        self.producer = KafkaProducer(bootstrap_servers=bootstrap_servers,
                                      value_serializer=lambda msg: json.dumps(msg).encode('utf-8'))

    def send(self, msg):
        LOGGER.info('send type: %s, id: %s' % (msg['type'], msg['id']))
        self.producer.send(topic=self.topic, value=msg)
        LOGGER.info('send successful.')


class WeiboCnSpider:
    def __init__(self):
        self.bloom_filter = ScalableBloomFilter(mode=ScalableBloomFilter.SMALL_SET_GROWTH)

        self.time_current_pattern = re.compile(r'(\d*)分钟前')
        self.time_today_pattern = re.compile(r'今天\s*(\d*):(\d*)')
        self.time_year_pattern = re.compile(r'(\d*)月(\d*)日\s*(\d*):(\d*)')
        self.user_id_pattern = re.compile(r'https://weibo.cn/u/(\d*)')
        self.weibo_host = 'https://weibo.cn'
        self.follow_url = self.weibo_host + '/%s/follow'

        self.fan_url = self.weibo_host + '/%s/fans'
        self.user_info_url = self.weibo_host + '/%s/info'
        self.user_tweet_url = self.weibo_host + '/%s/profile'
        self.user_tweet_url2 = self.weibo_host + '/%s/profile?page=%d'
        self.tweet_comment_url = self.weibo_host + '/comment/%s'
        self.tweet_comment_url2 = self.weibo_host + '/comment/%s?page=%d'
        self.weibo_producer = WeiboProcuder(['localhost:9092'], 'sinaweibo')
        self.comment_queue = Queue()
        self.weibo_queue = Queue()
        self.user_queue = Queue()
        self.follow_queue = Queue()
        self.fans_queue = Queue()

    def crawl_user(self):
        while True:
            user_id = self.user_queue.get()
            sleep(2)
            try:
                self.grab_user_info(user_id)
                self.weibo_queue.put({'url': self.user_tweet_url % user_id, 'uid': user_id})
                self.follow_queue.put({'uid': user_id, 'url': self.follow_url % user_id})
            except:
                LOGGER.error(traceback.format_exc())
                sleep(5 * 60)

    def crawl_follow(self):
        while True:
            follow_dict = self.follow_queue.get()
            sleep(2)
            try:
                self.grab_follow(follow_dict)
            except:
                LOGGER.error(traceback.format_exc())
                sleep(5 * 60)

    def crawl_comment(self):
        while True:
            comment_url = self.comment_queue.get()
            sleep(2)
            try:
                self.grab_tweet_comments(comment_url)

            except:
                LOGGER.error(traceback.format_exc())
                sleep(5 * 60)

    def crawl_weibo(self):
        while True:
            user_tweet_url = self.weibo_queue.get()
            sleep(2)
            try:
                self.grab_user_tweet(user_tweet_url)
            except:
                LOGGER.error(traceback.format_exc())
                sleep(5 * 60)

    def get_time(self, time_str):
        current_result = self.time_current_pattern.findall(time_str)
        time_now = datetime.datetime.now()
        if current_result:
            result_time = time_now - datetime.timedelta(minutes=int(current_result[0]))
            return result_time.strftime('%Y-%m-%d %H:%M:%S')
        else:
            current_result = self.time_today_pattern.findall(time_str)
            if current_result:
                result_time = datetime.datetime(time_now.year, time_now.month,
                                                time_now.day, int(current_result[0][0]), int(current_result[0][0]))
                return result_time.strftime('%Y-%m-%d %H:%M:%S')
            else:
                current_result = self.time_year_pattern.findall(time_str)
                if current_result:
                    result_time = datetime.datetime(time_now.year, int(current_result[0][0]),
                                                    int(current_result[0][1]), int(current_result[0][2]),
                                                    int(current_result[0][3]))
                    return result_time.strftime('%Y-%m-%d %H:%M:%S')
                else:
                    return time_str

    def start(self):
        # self.user_queue.put('2210643391')
        # self.grab_user_info('1316949123')
        # return self.grab_user_info('1316949123')

        # self.get_follow({'uid': '2365758410', 'url': self.follow_url % '2365758410'})
        self.comment_queue.put({'url': 'https://weibo.cn/comment/FAr5A2OdG', 'tweetId': 'FAr5A2OdG'})
        follow_thread = threading.Thread(target=self.crawl_follow, name='follow_thread')
        follow_thread.start()
        comment_thread = threading.Thread(target=self.crawl_comment, name='comment_thread')
        comment_thread.start()
        user_thread = threading.Thread(target=self.crawl_user, name='user_thread')
        user_thread.start()
        # self.weibo_queue.put({'url': self.user_tweet_url % '2210643391', 'uid': '2210643391'})
        thread_weibo = threading.Thread(target=self.crawl_weibo, name='weibo_thread')
        thread_weibo.start()

    def grab_follow(self, follow_dict):
        session = requests.Session()
        cookies = RedisCookies.fetch_cookies()
        response = session.get(self.follow_url % follow_dict['uid'], cookies=cookies['cookies'], verify=False)
        follow_html = BeautifulSoup(response.text, "lxml")
        all_td = follow_html.find_all('td', style=True)
        for td in all_td:
            a = td.find('a').get('href')
            usr_id_result = self.user_id_pattern.findall(a)
            if usr_id_result:
                usr_id = usr_id_result[0]
            else:
                usr_id = self.get_user_id_from_homepage(a)

            if usr_id and usr_id not in self.bloom_filter:
                self.bloom_filter.add(usr_id)
                self.user_queue.put(usr_id)

        if 'page=' not in follow_dict['url']:

            page_div = follow_html.find(id='pagelist')
            max_page = int(page_div.input.get('value'))
            for page in range(2, max_page + 1):
                pass
                # self.weibo_queue.put({'url': (self.follow_url % follow_dict['uid'])+'?page=' + page,
                #                       'uid': follow_dict['uid']})
        pass

    def get_fans(self, user_id):
        pass

    @staticmethod
    def get_header():
        header = {
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Encoding': 'gzip, deflate, br',
            'Accept-Language': 'zh-CN,zh;q=0.9',
            'Upgrade-Insecure-Requests': '1',
            'Connection': 'keep-alive',
            'Host': 'weibo.cn',
            'Referer': 'https://weibo.cn',
            'User-Agent': user_agents.USER_AGENTS[random.randint(0, len(user_agents.USER_AGENTS) - 1)]
        }
        return header

    @staticmethod
    def get_user_id_from_homepage(home_page):
        session = requests.Session()
        cookies = RedisCookies.fetch_cookies()
        response = session.get(home_page, cookies=cookies['cookies'], verify=False)
        home_page_html = BeautifulSoup(response.text, "lxml")
        info_a = home_page_html.find('a', string='资料')
        LOGGER.info('get id from home page: %s' % home_page)
        if info_a:
            user_id = info_a.get('href').split('/')[1]
            LOGGER.info('id got: %s' % user_id)
            return user_id
        return 0

    def grab_tweet_comments(self, comment_url):
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
                    user_id = self.get_user_id_from_homepage(self.weibo_host + user_href)
                if user_id and user_id not in self.bloom_filter:
                    self.bloom_filter.add(user_id)
                    self.user_queue.put(user_id)
                comment_info['userId'] = user_id
                comment_info['content'] = comment_div.find(class_='ctt').get_text()
                others = comment_div.find(class_='ct').get_text()
                if others:
                    others = others.split('\u6765\u81ea')
                    comment_info['pubTime'] = self.get_time(others[0])
                    if len(others) == 2:
                        comment_info['source'] = others[1]
                comment_info['id'] = comment_id
                comment_info['tweetId'] = comment_url['tweetId']
                comment_info['type'] = 'comment_info'
                self.weibo_producer.send(comment_info)

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
                        tweet_user_id = self.get_user_id_from_homepage(self.weibo_host + tweet_user_href)
                    tweet_content = tweet_div.find('span', class_='ctt').get_text()
                    others = tweet_div.find(class_='ct').get_text()
                    tweet_details = list(
                        filter(lambda div: div.find(class_='pms'), comment_html.find_all('div', id=False, class_=False)))
                    detail = tweet_details[0].get_text(';')
                    like = re.findall(u'\u8d5e\[(\d+)\];', detail)  # 点赞数
                    transfer = re.findall(u'\u8f6c\u53d1\[(\d+)\];', detail)  # 转载数
                    comment = re.findall(u'\u8bc4\u8bba\[(\d+)\];', detail)  # 评论数
                    if others:
                        others = others.split('\u6765\u81ea')
                        tweet['time'] = self.get_time(others[0])
                        if len(others) == 2:
                            tweet['source'] = others[1]
                    tweet['content'] = tweet_content
                    tweet['id'] = comment_url['tweetId']
                    tweet['like'] = like[0] if like else -1
                    tweet['transfer'] = transfer[0] if transfer else -1
                    tweet['comment'] = comment[0] if comment else -1
                    tweet['type'] = 'tweet_info'
                    tweet['uid'] = tweet_user_id
                    self.weibo_producer.send(tweet)

            page_div = comment_html.find(id='pagelist')
            if page_div:

                max_page = int(page_div.input.get('value'))
                for page in range(2, max_page + 1):
                    self.comment_queue.put({'url': self.tweet_comment_url2 % (comment_url['tweetId'], page),
                                            'tweetId': comment_url['tweetId']})

    def grab_user_tweet(self, tweet_url):
        LOGGER.info('grab: %s' % str(tweet_url))
        session = requests.Session()
        cookies = RedisCookies.fetch_cookies()
        response = session.get(tweet_url['url'], cookies=cookies['cookies'], verify=False)
        response.encoding = 'utf-8'
        user_tweet_html = BeautifulSoup(response.text, "lxml")
        tweet_divs = user_tweet_html.find_all(id=True, class_='c')
        for tweet_div in tweet_divs:
            tweet = {}
            if tweet_div.find(class_='cmt', string='转发理由:'):  # 转发
                tweet['flag'] = '转发'
                parent = tweet_div.find(class_='cmt', string='转发理由:').parent
                try:
                    comment_href = tweet_div.find_all('div')[-2].find('a', class_='cc').get('href')

                    href = comment_href.split('?')[0]
                    tweet['sourceTid'] = href.split('/')[-1]

                except Exception:
                    pass
                text = parent.get_text()
                fields = text.split('\xa0')
                content = fields[0]
                time = fields[-2]
                source = fields[-1]
                other = ';'.join(fields[1:-2])

            else:
                tweet['flag'] = '原创'
                text = tweet_div.get_text()
                fields = text.split('\u200b')
                content = fields[0]
                other_fields = fields[-1].split('\xa0')
                time = other_fields[-2]
                source = other_fields[-1]
                other = ';'.join(other_fields[1:-2])

            like = re.findall(u'\u8d5e\[(\d+)\]', other)  # 点赞数
            transfer = re.findall(u'\u8f6c\u53d1\[(\d+)\]', other)  # 转载数
            comment = re.findall(u'\u8bc4\u8bba\[(\d+)\]', other)  # 评论数
            tweet['content'] = content
            tweet['id'] = tweet_div.get('id')
            tweet['time'] = time
            tweet['source'] = source
            tweet['like'] = like[0] if like else -1
            tweet['transfer'] = transfer[0] if transfer else -1
            tweet['comment'] = comment[0] if comment else -1
            tweet['type'] = 'tweet_info'
            tweet['uid'] = tweet_url['uid']

            self.weibo_producer.send(tweet)
            # 获取评论
            self.comment_queue.put({'url': self.tweet_comment_url % tweet['id'][2:],
                                    'tweetId': tweet['id'][2:]})

        if 'page=' not in tweet_url['url']:

            page_div = user_tweet_html.find(id='pagelist')

            max_page = int(page_div.input.get('value'))
            for page in range(2, max_page + 1):
                self.weibo_queue.put({'url': self.user_tweet_url2 % (tweet_url['uid'], page),
                                      'uid': tweet_url['uid']})

    def grab_user_info(self, user_id):
        session = requests.Session()
        cookies = RedisCookies.fetch_cookies()
        cookie = ''
        for k, v in cookies['cookies'].items():
            cookie = cookie + k + '=' + v + ';'
        headers = self.get_header()
        response = session.get(self.user_info_url % user_id, cookies=cookies['cookies'], verify=False)

        response.encoding = 'utf-8'
        user_info_html = BeautifulSoup(response.text, "lxml")
        div_list = list(user_info_html.find_all(class_=['c', 'tip']))
        base_info_index, edu_info_index, work_info_index = -1, -1, -1
        base_info = ''
        edu_info = ''
        work_info = ''
        tags = ''
        user_info = {}
        for index, div in enumerate(div_list):
            text = div.text
            if text == u'基本信息':
                base_info_index = index
            elif text == u'学习经历':
                edu_info_index = index
            elif text == u'工作经历':
                work_info_index = index
        if base_info_index != -1:
            b = div_list[base_info_index + 1]
            tags = ','.join(map(lambda a: a.get_text(), b.find_all('a')))
            base_info = b.get_text(';')
        if edu_info_index != -1:
            edu_info = div_list[edu_info_index + 1].get_text(';')

        if work_info_index != -1:
            work_info = div_list[work_info_index + 1].get_text(';')

        nickname = re.findall(u'\u6635\u79f0[:|\uff1a](.*?);', base_info)  # 昵称
        gender = re.findall(u'\u6027\u522b[:|\uff1a](.*?);', base_info)  # 性别
        place = re.findall(u'\u5730\u533a[:|\uff1a](.*?);', base_info)  # 地区（包括省份和城市）
        signature = re.findall(u'\u7b80\u4ecb[:|\uff1a](.*?);', base_info)  # 个性签名
        birthday = re.findall(u'\u751f\u65e5[:|\uff1a](.*?);', base_info)  # 生日
        sex_orientation = re.findall(u'\u6027\u53d6\u5411[:|\uff1a](.*?);', base_info)  # 性取向
        marriage = re.findall(u'\u611f\u60c5\u72b6\u51b5[:|\uff1a](.*?);', base_info)  # 婚姻状况
        user_info['tags'] = tags
        user_info['gender'] = gender[0] if gender else 'unknown'
        user_info['place'] = place[0] if place else 'unknown'
        user_info['signature'] = signature[0] if signature else 'unknown'
        user_info['birthday'] = birthday[0] if birthday else 'unknown'
        user_info['sexOrientation'] = sex_orientation[0] if sex_orientation else 'unknown'
        user_info['eduInfo'] = edu_info if edu_info else 'unknown'
        user_info['marriage'] = marriage[0] if marriage else 'unknown'
        user_info['workInfo'] = work_info if work_info else 'unknown'
        user_info['nickname'] = nickname[0] if nickname else 'unknown'
        user_info['type'] = 'user_info'
        user_info['id'] = user_id
        self.weibo_producer.send(user_info)

        #


if __name__ == '__main__':
    WeiboCnSpider().start()
