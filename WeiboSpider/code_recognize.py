# -*- coding: cp936 -*-

from ctypes import *
from setting import LOGGER

class YunDaMa:
    YDMApi = windll.LoadLibrary('.\\dll\\yundamaAPI-x64.dll')
    appId = 4296  # 软件ＩＤ，开发者分成必要参数。登录开发者后台【我的软件】获得！
    appKey = b'fdacec8d9f1c2deb86346bfcf64e95f2'  # 软件密钥，开发者分成必要参数。登录开发者后台【我的软件】获得！

    def __init__(self, username, password):
        LOGGER.info('app id：%d\r\napp key：%s' % (self.appId, self.appKey))
        self.username = b'zhujiajun'
        self.password = b'vs7452014'
        self.code_type = 1005
        self.timeout = 60
        self.YDMApi.YDM_SetAppInfo(self.appId, self.appKey)

    def recognize(self, filename):
        if not isinstance(filename, bytes):
            filename = filename.encode()
        result = c_char_p(b"                              ")
        LOGGER.info('\r\n>>>正在登陆...')
        captcha_id = self.YDMApi.YDM_EasyDecodeByPath(self.username, self.password, self.appId, self.appKey,
                                                      filename, self.code_type, self.timeout, result)

        return captcha_id, result.value

        # 第一步：初始化云打码，只需调用一次即可


if __name__ == '__main__':
    i, code = YunDaMa(username='zhujiajun', password='vs7452014').recognize(
        'D:\\graduate\\WeiboSpider3\\img\\verify_code.png')
    print(type(code))
    print(bytes.decode(code))
    print(len(bytes.decode(code)))
    print(code)
