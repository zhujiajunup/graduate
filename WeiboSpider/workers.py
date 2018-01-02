from time import sleep
from setting import LOGGER
from redis_cookies import RedisJob, RedisCookies
from bs4 import BeautifulSoup
from enum import Enum
import traceback
import requests
import re


class JobType(Enum):
    comment = 'comment'
    tweet = 'tweet'
    follower = 'follower'


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
                self.user_id_in_queue(user_id)
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
