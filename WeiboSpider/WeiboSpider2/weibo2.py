
import json
import os
import sys

from time import sleep
import queue
import threading
from multiprocessing import Lock
import random
import traceback
import logging
from WeiboSpider2 import setting
from WeiboSpider2 import weibo_http


class WeiboCrawler:

    def __init__(self, user, password):
        self.user = user
        self.password = password
        self.CURR_USER_INDEX = -1
        self.weibo_queue = queue.Queue()  # 微博任务队列
        self.user_queue = queue.Queue()  # 用户信息队列
        self.fans_queue = queue.Queue()  # 用户粉丝队列
        self.follower_queue = queue.Queue()  # 用户关注队列
        self.m_info_queue = queue.Queue()  # 用户关注队列
        self.M_INFO_CRAWLED_USER = set()
        self.CRAWLED_USERS = set()
        self.WEIBO_CRAWLED_USERS = set()
        self.FANS_CRAWLED_USERS = set()
        self.FOLLOWER_CRAWLED_USERS = set()
        self.weibo_set_lock = Lock()
        self.m_info_set_lock = Lock()
        self.user_set_lock = Lock()
        self.fans_set_lock = Lock()
        self.follower_set_lock = Lock()
        self.logger = logging
        self.init_thread()  # 开启进程

    def init_thread(self):
        self.logger.info('init  threads: %d' % setting.THREAD_NUM)
        for i in range(setting.THREAD_NUM):
            thread_weibo = threading.Thread(target=self.crawl_weibo, name='weibo_thread_'+str(i))
            thread_user = threading.Thread(target=self.crawl_user, name='user_thread_' + str(i))
            thread_fans = threading.Thread(target=self.crawl_fans, name='fans_thread_' + str(i))
            thread_follower = threading.Thread(target=self.crawl_follower, name='follower_thread_' + str(i))
            # thread_m_info = threading.Thread(target=self.crawl_m_info, name='m_info_thread_' + str(i))
            thread_weibo.start()
            thread_user.start()
            thread_fans.start()
            thread_follower.start()
            # thread_m_info.start()

    def crawl_weibo(self):
        while True:
            user_id = self.weibo_queue.get()
            try:
                self.grab_user_blogs(user_id)

            except:
                self.logger.error(traceback.print_exc())
                sleep(5 * 60)

    def crawl_fans(self):
        while True:
            try:
                user_id = self.fans_queue.get()
                self.grab_user_fans(user_id)
            except:
                self.logger.error(traceback.print_exc())
                sleep(5 * 60)

    def crawl_follower(self):
        while True:
            try:
                user_id = self.follower_queue.get()
                self.grab_user_follower(user_id)
            except:
                self.logger.error(traceback.print_exc())
                sleep(5 * 60)

    def crawl_m_info(self):
        while True:
            try:
                user_id = self.m_info_queue.get()
                self.grab_m_info(user_id)
            except:
                self.logger.error(traceback.print_exc())
                sleep(5 * 60)

    def crawl_user(self):
        while True:
            user_id = self.user_queue.get()
            try:
                self.grab_user(user_id)
                self.id_enqueue(user_id, self.fans_set_lock, self.FANS_CRAWLED_USERS, self.fans_queue)
                self.id_enqueue(user_id, self.follower_set_lock, self.FOLLOWER_CRAWLED_USERS, self.follower_queue)
                self.id_enqueue(user_id, self.weibo_set_lock, self.WEIBO_CRAWLED_USERS, self.weibo_queue)
                self.id_enqueue(user_id, self.m_info_set_lock, self.M_INFO_CRAWLED_USER, self.m_info_queue)
                time = setting.SLEEP_TIME[random.randint(0, len(setting.SLEEP_TIME) - 1)]
                self.logger.info('sleep time:%d seconds' % time)
                sleep(time)
            except:  # 可以细化
                self.logger.error(traceback.print_exc())
                sleep(1 * 60)

    def id_enqueue(self, user_id, id_lock, id_set, id_queue):
        if user_id in id_set:
            self.logger.info('\n\n\nuser_id: %s is already crawled\n\n\n' % user_id)
            return
        with id_lock:
            id_set.add(user_id)
            id_queue.put(user_id)

    def grab_m_info(self, user_id):

        self.logger.info('grab follower for user:%s' % user_id)
        opener = weibo_http.get_openner()

        weibo_http.change_header(opener)
        url = setting.INFO_URL_PATTERN % str(user_id)
        self.logger.info(url)
        rsp = opener.open(url)
        rsp_data = rsp.read().decode()

        return_json = json.loads(rsp_data)
        print(return_json)
        pass

    def grab_user_fans(self, user_id):
        opener, cj = weibo_http.get_openner()
        weibo_http.change_header(opener, {'Refer': setting.FANS_URL_PATTERN2 % (user_id, user_id)})
        page = 1

        max_page = 10
        while page <= max_page:

            resp = opener.open(setting.FANS_URL_PATTERN % (user_id, user_id, str(page)))
            self.logger.info(setting.FANS_URL_PATTERN % (user_id, user_id, str(page)))
            r = resp.read()

            resp_json = json.loads(r.decode())
            if 'msg' in resp_json:
                break
            for card in resp_json['cards']:
                for cg in card['card_group']:
                    print('-'*10 + 'fans' + '-' * 10)
                    # print(cg)
                    # self.id_enqueue(fan.id, self.user_set_lock, self.CRAWLED_USERS, self.user_queue)
            page += 1

    def grab_user_follower(self, user_id):
        opener, cj = weibo_http.get_openner()
        for item in cj:
            print(item.name + "->" + item.value)
        max_page = 20
        page = 1
        while page <= max_page:
            self.logger.info(setting.FOLLOWER_URL_PATTERN % (user_id, user_id, str(page)))
            resp = opener.open(setting.FOLLOWER_URL_PATTERN % (user_id, user_id, str(page)))

            r = resp.read()
            resp_json = json.loads(r.decode())
            if 'msg' in resp_json:
                break
            for card in resp_json['cards']:

                for cg in filter(lambda c: 'user' in c, card['card_group']):
                    pass
                    # print(cg)
                    # self.id_enqueue(follower.id, self.user_set_lock, self.CRAWLED_USERS, self.user_queue)
            page += 1
        pass

    def grab_user_blogs(self, user_id):
        opener, cj = weibo_http.get_openner()
        has_get_pages = False
        max_page = 2
        page = 1
        while page <= max_page:
            url = setting.WEIBO_URL_PATTERN % (str(user_id), str(page))
            self.logger.info("正在打开："+url)
            rsp = opener.open(url)
            # print(rsp.read())
            return_json = json.loads(rsp.read().decode())

            cards = return_json['cards']
            # print(card['maxPage'])
            if not has_get_pages:
                total = return_json['cardlistInfo']['total']
                max_page = int(int(total)/9)
                has_get_pages = True

            # print('-'*16+"\n"+str(cards))
            for card in filter(lambda c: 'mblog' in c and 'msg' not in c, cards):
                blog_info = card['mblog']
                print(blog_info)
                # weibo = dao.save_blog_info(blog_info)
                # if 'retweeted_status' in blog_info:
                #     rew_json = blog_info['retweeted_status']
                #     # 这里，转发的微博可能被删除、举报，user就为null
                #     if rew_json['user'] is not None and rew_json['user']['id'] is not None:
                #         try:
                #             ret_weibo = Weibo.objects.get(pk=rew_json['id'])
                #         except Weibo.DoesNotExist:
                #             ret_weibo = dao.save_blog_info(rew_json)
                #         weibo.retweented_status = ret_weibo
                #         weibo.save()
            page += 1
            time = setting.SLEEP_TIME[random.randint(0, len(setting.SLEEP_TIME)-1)]
            self.logger.info('sleep time:%d seconds' % time)
            sleep(time)
            weibo_http.change_proxy(opener)

    def grab_user(self, user_id):
        self.logger.info('grab follower for user:%s' % user_id)
        opener, cj = weibo_http.get_openner()

        weibo_http.change_header(opener)

        url = setting.WEIBO_URL_PATTERN % (str(user_id), str(1))
        self.logger.info(url)
        rsp = opener.open(url)
        rsp_data = rsp.read().decode()
        return_json = json.loads(rsp_data)
        card = return_json['cards'][0]
        print(card)
        # user = dao.save_user_info(card['mblog']['user'])
        # self.weibo_enqueue(user.id)
        # return user

    def relogin(self, opener):
        """
        還未用到
        :param opener: 
        :return: 
        """
        opener.open('http://m.weibo.cn/home/logout')  # 登出
        curr_index = random.randint(0, len(setting.ACCOUNTS)-1)
        while curr_index == self.CURR_USER_INDEX:
            curr_index = random.randint(0, len(setting.ACCOUNTS)-1)
        self.CURR_USER_INDEX = curr_index
        print(setting.ACCOUNTS[self.CURR_USER_INDEX]['username'] + " login")
        weibo_http.login(setting.ACCOUNTS[self.CURR_USER_INDEX]['username'],
                         setting.ACCOUNTS[self.CURR_USER_INDEX]['password'], opener)
        weibo_http.change_header(opener)

    def get_comment_by_page(self, blog_id, page_num):
        url = 'http://m.weibo.cn/single/rcList?format=cards&id='
        req_url = url + str(blog_id) + '&type=comment&hot=0&page='+str(page_num)
        print('浏览器正在打开url：'+req_url)
        opener = weibo_http.make_my_opener()
        rsp = opener.open(req_url)
        return_json = json.loads(rsp.read().decode())
        print('请求返回数据:\t'+str(return_json))
        if page_num == 1:
            comment_json = return_json[1]
        else:
            comment_json = return_json[0]
        return comment_json

    def grab_comment(self, blog_id):
        page = 1
        comment_json = self.get_comment_by_page(blog_id, page)
        print('评论——json\t' + str(comment_json))
        if 'maxPage' not in comment_json:
            return
        max_page = comment_json['maxPage']
        page += 1
        if 'card_group' in comment_json:
            comment_card_group = comment_json['card_group']
            for comment_group in comment_card_group:
                pass
        print("总页面数：max_page：\t"+str(max_page))
        while page <= max_page:
            print("curr_page:\t"+str(page)+"\t    max_page\t:"+str(max_page))
            comment_json = self.get_comment_by_page(blog_id, page)
            if 'card_group' in comment_json:
                comment_card_group = comment_json['card_group']
                for comment_group in comment_card_group:
                    pass
            page += 1

    def start(self):
        self.id_enqueue('2210643391', self.user_set_lock, self.CRAWLED_USERS, self.user_queue)



def main():
    my = WeiboCrawler("", "")
    my.start()


if __name__ == '__main__':
    main()
