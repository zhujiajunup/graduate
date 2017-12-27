# -*- coding: cp936 -*-

from ctypes import *
from setting import LOGGER

class YunDaMa:
    YDMApi = windll.LoadLibrary('.\\dll\\yundamaAPI-x64.dll')
    appId = 4296  # 软件id
    appKey = b'fdacec8d9f1c2deb86346bfcf64e95f2'  # 软件密钥

    def __init__(self, username, password):
        LOGGER.info('app id：%d\r\napp key：%s' % (self.appId, self.appKey))
        self.username = username.encode()
        self.password = password.encode()
        self.code_type = 1005
        self.timeout = 60
        self.YDMApi.YDM_SetAppInfo(self.appId, self.appKey)

    def recognize(self, filename):
        if not isinstance(filename, bytes):
            filename = filename.encode()
        result = c_char_p(b"                              ")
        LOGGER.info('>>>正在登陆...')
        captcha_id = self.YDMApi.YDM_EasyDecodeByPath(self.username, self.password, self.appId, self.appKey,
                                                      filename, self.code_type, self.timeout, result)

        return captcha_id, result.value

