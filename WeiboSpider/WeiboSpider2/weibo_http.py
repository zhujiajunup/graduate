
from WeiboSpider2 import setting
from WeiboSpider2 import user_agents
import traceback
import random
import json
import http.cookiejar
import urllib.parse
import urllib.request
from time import sleep
import ssl


def login(user_name, password):
    cj = http.cookiejar.CookieJar()
    opener = make_my_opener(cj)
    setting.LOGGER.info(user_name + ' login')
    args = {
        'username': user_name,
        'password': password,
        'savestate': 1,
        'ec': 0,
        'pagerefer': 'https://passport.weibo.cn/signin/'
                     'welcome?entry=mweibo&r=http%3A%2F%2Fm.weibo.cn%2F&wm=3349&vt=4',
        'entry': 'mweibo',
        'wentry': '',
        'loginfrom': '',
        'client_id': '',
        'code': '',
        'qq': '',
        'hff': '',
        'hfp': ''
    }

    post_data = urllib.parse.urlencode(args).encode()
    try_time = 0
    while try_time < setting.TRY_TIME:
        try:
            resp = opener.open(setting.LOGIN_URL, post_data)
            resp_json = json.loads(resp.read().decode())
            if 'retcode' in resp_json and resp_json['retcode'] == 20000000:
                setting.LOGGER.info("%s login successful" % user_name)
                for item in cj:
                    print(item.name + "->" + item.value)
                break
            else:
                setting.LOGGER.warning('login fail:%s' % str(resp_json))
                sleep(10)
                try_time += 1
        except Exception:
            setting.LOGGER.error("login failed")
            setting.LOGGER.error(traceback.print_exc())
            sleep(10)
            try_time += 1
            setting.LOGGER.info('try %d time' % try_time)

def get_openner():
    cj = http.cookiejar.CookieJar()
    opener = make_my_opener(cj)
    accounts = setting.PROPERTIES.get('accounts')
    print(accounts)
    curr_index = random.randint(0, len(accounts) - 1)  # 随机选取用户
    setting.LOGGER.info('user index : %d' % curr_index)
    login(accounts[curr_index]['user'], accounts[curr_index]['password'])
    change_header(opener)
    return opener, cj


def change_header(opener, ext=None):
    head = {
        'Accept': '*/*',
        'Connection': 'keep-alive',
        'Accept-Language': 'zh-CN,zh;q=0.8,en;q=0.6',
        'Host': 'm.weibo.cn',
        'Proxy-Connection': 'keep-alive',
        'User-Agent': user_agents.USER_AGENTS[random.randint(0, len(user_agents.USER_AGENTS) - 1)]
    }
    if ext:
        head.update(ext)
    header = []
    for key, value in head.items():
        elem = (key, value)
        header.append(elem)
    opener.addheaders = header


def change_proxy(opener):
    proxy_handler = urllib.request.ProxyHandler(setting.PROXIES[random.randint(0, len(setting.PROXIES) -1)])
    opener.add_handler(proxy_handler)


def make_my_opener(cj):
    """
            模拟浏览器发送请求
            :return:
            """
    # cj = http.cookiejar.CookieJar()
    opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

    header = []
    head = {
        'Accept': '*/*',
        'Accept-Encoding': 'gzip,deflate',
        'Accept-Language': 'zh-CN,zh;q=0.8,en;q=0.6',
        'Connection': 'keep-alive',
        'Content-Length': '254',
        'Content-Type': 'application/x-www-form-urlencoded',
        'Host': 'passport.weibo.cn',
        'Origin': 'https://passport.weibo.cn',
        'Referer': 'https://passport.weibo.cn/signin/login?'
                   'entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fm.weibo.cn%2F',
        'User-Agent': user_agents.USER_AGENTS[random.randint(0, len(user_agents.USER_AGENTS) - 1)]
    }
    for key, value in head.items():
        elem = (key, value)
        header.append(elem)
    opener.addheaders = header
    return opener


# login('jjzhu_ncu@163.com', 'jvs7452014')
