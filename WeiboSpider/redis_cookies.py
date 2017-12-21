# -*- coding:utf-8 -*-
import redis
import json
import datetime
from login import WeiboLogin
from setting import LOGGER, ACCOUNTS


class RedisCookies(object):
    redis_pool = redis.ConnectionPool(host='localhost', port=6379, db=0)

    @classmethod
    def save_cookies(cls, user_name, cookies):

        pickled_cookies = json.dumps({
            'user_name': user_name,
            'cookies': cookies,
            'login_time': datetime.datetime.now().timestamp()
        })
        LOGGER.info('save cookie in redis: %s' % str(pickled_cookies))
        r = redis.Redis(connection_pool=cls.redis_pool)
        r.hset('account', user_name, pickled_cookies)
        cls.user_in_queue(user_name)

    @classmethod
    def user_in_queue(cls, user_name):
        r = redis.Redis(connection_pool=cls.redis_pool)

        if not r.sismember('users', user_name):
            LOGGER.info('user in queue: %s' % user_name)
            r.sadd("users", user_name)
        else:
            LOGGER.info('user already in queue: %s' % user_name)

    @classmethod
    def fetch_cookies(cls):
        LOGGER.info('get cookies from reids')
        r = redis.Redis(connection_pool=cls.redis_pool)
        while True:
            user = r.spop('users')
            r.sadd('users', user)
            c = r.hget('account', user)
            if c:
                user_cookies = c.decode('utf-8')
                cookies_json = json.loads(user_cookies)
                LOGGER.info(cookies_json)
                return cookies_json
            LOGGER.warn('cookies not get')

    @classmethod
    def clean(cls):
        r = redis.Redis(connection_pool=cls.redis_pool)
        r.delete('users')
        r.delete('account')


def main():
    # RedisCookies.clean()
    weiboLogin = WeiboLogin()
    success = []
    failed = []
    for account in ACCOUNTS:
        try:
            LOGGER.info('get cookies for %s' % str(account))
            cookies = weiboLogin.login_by_selenium(account['user'], account['password'])
            if cookies is not None and 'SSOLoginState' in cookies and 'SUBP' in cookies and 'SUHB' in cookies:
                success.append(account)
                RedisCookies.save_cookies(account['user'], cookies)
            else:
                failed.append(account)
        except Exception:
            failed.append(account)
    LOGGER.info("%d accounts login success" % len(success))
    LOGGER.info("%d accounts login failed" % len(failed))


if __name__ == '__main__':
    main()
