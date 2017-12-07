# -*- coding:utf-8 -*-
import logging
import urllib
import urllib.parse


def login(user_name, password, opener):
    logging.info(user_name + ' login')
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
    while try_time < constants.TRY_TIME:
        try:
            resp = opener.open(constants.LOGIN_URL, post_data)
            resp_json = json.loads(resp.read().decode())
            if 'retcode' in resp_json and resp_json['retcode'] == 20000000:
                LOGGER.info("%s login successful" % user_name)
                break
            else:
                LOGGER.warn('login fail:%s' % str(resp_json))
                sleep(10)
                try_time += 1
        except :
            LOGGER.error("login failed")
            LOGGER.error(traceback.print_exc())
            sleep(10)
            try_time += 1
            LOGGER.info('try %d time' % try_time)