import requests
import user_agents
import random
import setting


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
    session.post(setting.LOGIN_URL, data=args, headers=head, verify=False)
    return session
