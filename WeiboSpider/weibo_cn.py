# -*- coding:utf-8 -*-
import json
import random
import re
import traceback
from queue import Queue
from time import sleep

import requests
from bs4 import BeautifulSoup
from kafka import KafkaProducer

import user_agents
from redis_cookies import RedisCookies
from setting import LOGGER


class WeiboProcuder:
    def __init__(self, bootstrap_servers, topic):
        self.topic = topic
        self.producer = KafkaProducer(bootstrap_servers=bootstrap_servers,
                                      value_serializer=lambda msg: json.dumps(msg, encoding='utf-8'))

    def send(self, msg):
        LOGGER.info('send msg: %s' % msg)
        self.producer.send(topic=self.topic, value=msg)


class WeiboCnSpider:
    def __init__(self):
        self.user_info_url = 'https://weibo.cn/%s/info'
        self.user_tweet_url = 'https://weibo.cn/%s/profile'
        self.user_tweet_url2 = 'https://weibo.cn/%s/profile?page=%d'
        self.weibo_producer = WeiboProcuder('localhost:9092', 'sina_weibo')
        self.weibo_queue = Queue()

    def crawl_weibo(self):
        while True:
            uer_tweet_url = self.weibo_queue.get()
            sleep(5)
            try:
                self.grab_user_tweet(uer_tweet_url)
            except:
                LOGGER.error(traceback.print_exc())
                sleep(5 * 60)

    def start(self):
        self.grab_user_info('1316949123')
        # return self.grab_user_info('1316949123')
        # self.weibo_queue.put({'url': self.user_tweet_url % '1316949123', 'uid': '1316949123'})
        # thread_weibo = threading.Thread(target=self.crawl_weibo, name='weibo_thread')
        # thread_weibo.start()

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

    def grab_user_tweet(self, tweet_url):

        session = requests.Session()
        cookies = RedisCookies.fetch_cookies()
        LOGGER.info(cookies)
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
            if tweet['flag'] == '转发' and 'sourceTid' not in tweet:
                print('--' * 10)
                print(tweet_url['url'])
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
        if 'page=' not in tweet_url['url']:
            print(tweet_url)
            page_div = user_tweet_html.find(id='pagelist')
            print(page_div)
            max_page = int(page_div.input.get('value'))
            for page in range(2, max_page + 1):
                self.weibo_queue.put({'url': self.user_tweet_url2 % (tweet_url['uid'], page),
                                      'uid': tweet_url['uid']})

    def grab_user_info(self, user_id):
        session = requests.Session()
        session2 = requests.Session()
        cookies = RedisCookies.fetch_cookies()
        cookie = ''
        for k, v in cookies['cookies'].items():
            cookie = cookie + k + '=' + v + ';'
        LOGGER.info(cookies)
        LOGGER.info(cookie)
        headers = self.get_header()
        response = session.get(self.user_info_url % user_id, cookies=cookies['cookies'], verify=False)
        print(response.text)
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
        return user_info_html


if __name__ == '__main__':
    WeiboCnSpider().start()
