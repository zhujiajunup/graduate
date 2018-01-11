# -*- coding:utf-8 -*-
import asyncio
import datetime
import json
import random
import re
import sys
import traceback
from enum import Enum
from time import sleep

import aiohttp
import async_timeout
import requests
import urllib3
from bs4 import BeautifulSoup
from kafka import KafkaProducer
from pybloom import ScalableBloomFilter

import user_agents
from setting import LOGGER
from weibo_redis import RedisCookies, RedisJob

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


class WeiboProcuder:
    def __init__(self, bootstrap_servers, topic):
        self.topic = topic
        self.producer = KafkaProducer(bootstrap_servers=bootstrap_servers,
                                      value_serializer=lambda msg: json.dumps(msg).encode('utf-8'))

    async def send(self, msg, url):
        LOGGER.info(url)
        LOGGER.info('send type: %s, id: %s, (%s)' % (msg['type'], msg['id'], str(msg)))
        self.producer.send(topic=self.topic, value=msg)
        LOGGER.info('send successful.')


class JobType(Enum):
    comment = 'comment'
    tweet = 'tweet'
    follower = 'follower'
    user = 'user'
    repost = 'repost'


class WeiboCnSpider:
    def __init__(self):
        self.redis_cookies = RedisCookies()
        self.redis_job = RedisJob()
        self.bloom_filter = ScalableBloomFilter(mode=ScalableBloomFilter.SMALL_SET_GROWTH)
        self.weibo_limit = False
        self.time_current_pattern = re.compile(r'(\d*)分钟前')
        self.time_today_pattern = re.compile(r'今天\s*(\d*):(\d*)')
        self.time_year_pattern = re.compile(r'(\d*)月(\d*)日\s*(\d*):(\d*)')
        self.user_id_pattern = re.compile(r'https://weibo.cn/u/(\d*)')
        self.weibo_host = 'https://weibo.cn'
        self.follow_url = self.weibo_host + '/%s/follow'

        self.fan_url = self.weibo_host + '/%s/fans'
        self.user_info_url = self.weibo_host + '/%s/info'
        self.user_tweet_url = self.weibo_host + '/%s'
        self.user_tweet_url2 = self.weibo_host + '/%s?page=%d'
        self.user_repost_url = self.weibo_host + '/repost/%s'
        self.user_repost_url2 = self.weibo_host + '/repost/%s?page=%d'
        self.tweet_comment_url = self.weibo_host + '/comment/%s'
        self.tweet_comment_url2 = self.weibo_host + '/comment/%s?page=%d'
        self.weibo_producer = WeiboProcuder(['localhost:9092'], 'sinaweibo')

    async def crawl_comment(self):
        while True:
            comment_job_info = await self.redis_job.fetch_job(JobType.comment.value)
            if comment_job_info:
                try:
                    await self.grab_tweet_comments(comment_job_info)
                except:
                    LOGGER.error("something error")
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

    async def start(self, args):
        # self.user_queue.put('6037294528')
        # self.grab_user_info('1316949123')
        # return self.grab_user_info('1316949123')

        # self.get_follow({'uid': '2365758410', 'url': self.follow_url % '2365758410'})
        await self.crawl_comment()
        # if 'f' in args:
        #     self.crawl_follow()
        # if 'c' in args:
        #
        # if 'u' in args:
        #     self.crawl_user()
        # for i in range(0, THREAD_NUM):
        #     if 'f' in args:
        #         follow_thread = threading.Thread(target=self.crawl_follow, name='follow_thread_'+str(i))
        #         follow_thread.start()
        #     if 'c' in args:
        #         comment_thread = threading.Thread(target=self.crawl_comment, name='comment_thread_'+str(i))
        #         comment_thread.start()
        #     if 'u' in args:
        #         user_thread = threading.Thread(target=self.crawl_user, name='user_thread_'+str(i))
        #         user_thread.start()
        #     # self.weibo_queue.put({'url': self.user_tweet_url % '277118746', 'uid': '277118746'})
        #     if 'w' in args:
        #         weibo_thread = threading.Thread(target=self.crawl_weibo, name='weibo_thread_'+str(i))
        #         weibo_thread.start()
        #     # self.repost_queue.put({'url': self.user_repost_url % 'FCoPpaIQp', 'tweetId': 'FCoPpaIQp'})
        #     if 'r' in args:
        #         repost_thread = threading.Thread(target=self.crawl_repost, name='repost_thread_'+str(i))
        #         repost_thread.start()

    @staticmethod
    async def grab_html2(session, url):
        with async_timeout.timeout(10):
            async with session.get(url, verify_ssl=False) as response:
                return await response.text()

    async def grab_html(self, url):
        cookies = await self.redis_cookies.fetch_cookies()
        async with aiohttp.ClientSession(cookies=cookies['cookies']) as session:
            return await self.grab_html2(session, url)

    async def user_id_in_queue(self, user_id):
        if user_id and user_id not in self.bloom_filter:
            # LOGGER.info('%s in user queue.' % user_id)
            self.bloom_filter.add(user_id)
            await self.redis_job.push_job(JobType.user.value, {'user_id': user_id})

    @staticmethod
    async def get_header():
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

    async def get_user_id_from_homepage(self, home_page):
        html_content = await self.grab_html(home_page)
        home_page_html = BeautifulSoup(html_content, "lxml")
        info_a = home_page_html.find('a', string='资料')
        # LOGGER.info('get id from home page: %s' % home_page)
        if info_a:
            user_id = info_a.get('href').split('/')[1]
            # LOGGER.info('id got: %s' % user_id)
            return user_id
        return 0

    async def grab_tweet_comments(self, comment_job):
        LOGGER.info('start grab comment: %s' % str(comment_job))
        html_content = await self.grab_html(comment_job['url'])
        comment_html = BeautifulSoup(html_content, "lxml")

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
                    user_id = await self.get_user_id_from_homepage(self.weibo_host + user_href)
                await self.user_id_in_queue(user_id)
                comment_info['userId'] = user_id
                comment_info['content'] = comment_div.find(class_='ctt').get_text()
                others = comment_div.find(class_='ct').get_text()
                if others:
                    others = others.split('\u6765\u81ea')
                    comment_info['pubTime'] = self.get_time(others[0])
                    if len(others) == 2:
                        comment_info['source'] = others[1]
                comment_info['id'] = comment_id
                comment_info['tweetId'] = comment_job['tweetId']
                comment_info['type'] = 'comment_info'
                await self.weibo_producer.send(comment_info, comment_job['url'])

        if 'page=' not in comment_job['url']:
            self.redis_job.push_job(JobType.repost.value, {'url': self.user_repost_url % comment_job['tweetId'],
                                                           'tweetId': comment_job['tweetId']})
            tweet_div = comment_html.find(id='M_', class_='c')
            if tweet_div:
                tweet_user_a = tweet_div.find('a')
                flag = False
                if tweet_user_a:
                    tweet = {}
                    tweet_user_href = tweet_user_a.get('href')
                    if tweet_user_href.startswith('/u/'):
                        tweet_user_id = tweet_user_href[3:]
                    else:
                        tweet_user_id = await self.get_user_id_from_homepage(self.weibo_host + tweet_user_href)
                    if tweet_div.find(class_='cmt', string='转发理由:'):
                        flag = True
                    else:
                        tweet_content = tweet_div.find('span', class_='ctt').get_text()
                        tweet['content'] = tweet_content
                    tweet_details = list(
                        filter(lambda div: div.find(class_='pms'),
                               comment_html.find_all('div', id=False, class_=False)))
                    detail = tweet_details[0].get_text(';').replace('\xa0', '')
                    like = re.findall(u'\u8d5e\[(\d+)\];', detail)  # 点赞数
                    transfer = re.findall(u'\u8f6c\u53d1\[(\d+)\];', detail)  # 转载数
                    comment = re.findall(u'\u8bc4\u8bba\[(\d+)\];', detail)  # 评论数
                    tweet['id'] = comment_job['tweetId']
                    tweet['like'] = like[0] if like else 0
                    tweet['transfer'] = transfer[0] if transfer else 0
                    tweet['comment'] = comment[0] if comment else 0
                    tweet['type'] = 'tweet_info'
                    if flag:
                        self.weibo_producer.send(tweet, comment_job['url'])
                    else:
                        others = tweet_div.find(class_='ct').get_text()
                        if others:
                            others = others.split('\u6765\u81ea')
                            tweet['time'] = self.get_time(others[0])
                            if len(others) == 2:
                                tweet['source'] = others[1]
                        tweet['uid'] = tweet_user_id
                        await self.weibo_producer.send(tweet, comment_job['url'])

            page_div = comment_html.find(id='pagelist')
            if page_div:

                max_page = int(page_div.input.get('value'))
                for page in range(2, max_page + 1):
                    await self.redis_job.push_job(JobType.comment.value,
                                            {'url': self.tweet_comment_url2 % (comment_job['tweetId'], page),
                                             'tweetId': comment_job['tweetId']})


if __name__ == '__main__':
    args = sys.argv[1:]
    LOGGER.info(args)
    loop = asyncio.get_event_loop()
    loop.run_until_complete(WeiboCnSpider().start(args))
