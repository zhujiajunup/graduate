import requests
import user_agents
import random
import setting
import base64
import json
from setting import LOGGER


def get_cookie_from_login_sina_com_cn(account, password):
    """ 获取一个账号的Cookie """
    login_url = "https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.19)"
    username = base64.b64encode(account.encode("utf-8")).decode("utf-8")
    headers = {
        'Referer': 'https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.19)',
        'Upgrade-Insecure-Requests': '1',
        'Host': 'login.sina.com.cn',
        'Connection': 'keep-alive',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Accept-Encoding': 'gzip, deflate, br',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8'
    }
    post_data = {
        "entry": "sso",
        "gateway": "1",
        "from": "null",
        "savestate": "30",
        "useticket": "0",
        "pagerefer": "",
        "vsnf": "1",
        "su": username,
        "service": "sso",
        "sp": password,
        "sr": "1440*900",
        "encoding": "UTF-8",
        "cdult": "3",
        "domain": "sina.com.cn",
        "prelt": "0",
        "returntype": "TEXT",
    }
    session = requests.Session()
    r = session.post(login_url, headers=headers, data=post_data, verify=False)
    json_str = r.content.decode("gbk")
    info = json.loads(json_str)
    LOGGER.info('get cookies for %s' % account)
    if info["retcode"] == "0":
        LOGGER.info("Get Cookie Success!( Account:%s )" % account)

        cookies = session.cookies.get_dict()
        for k, v in cookies.items():
            print(k, v)
        return cookies
    else:
        LOGGER.warning("Get Cookie failed!( Account:%s )" % account)
        LOGGER.warning(info)
        return None


def login(username, password):
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
    setting.LOGGER.info(username + ' login')
    args = {
        'username': username,
        'password': password,
        'savestate': 1,
        'ec': 1,
        'r': 'http://weibo.cn/?featurecode=20000320&luicode=20000174&lfid=hotword',
        'entry': 'mweibo',
        'wentry': '',
        'loginfrom': '',
        'client_id': '',
        'code': '',
        'qq': '',
        'hff': '',
        'hfp': ''
    }
    session = requests.Session()
    response = session.post(setting.LOGIN_URL, data=args, headers=head, verify=False)
    print(response.text)
    cookies = {}
    for cookie in session.cookies:
        print(cookie.name, cookie.value)
        cookies[cookie.name] = cookie.value
    return cookies


# login('767543579@qq.com', 'jvs7452014@jjzhu')
# print('-' * 10)
# get_cookie_from_login_sina_com_cn('767543579@qq.com', 'jvs7452014@jjzhu')
