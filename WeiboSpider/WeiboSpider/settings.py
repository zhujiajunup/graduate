# encoding=utf-8
import yaml
import os
print os.path.split(os.path.realpath(__file__))[0]
PROPERTIES = yaml.load(open(os.path.split(os.path.realpath(__file__))[0] + '/conf/weibo.yaml'))

BOT_NAME = 'WeiboSpider'

SPIDER_MODULES = ['WeiboSpider.spiders']
NEWSPIDER_MODULE = 'WeiboSpider.spiders'
# HTTPCACHE_ENABLED = False
DOWNLOADER_MIDDLEWARES = {
    "WeiboSpider.middleware.UserAgentMiddleware": 401,
    "WeiboSpider.middleware.CookiesMiddleware": 402,
}

ITEM_PIPELINES = {
    'WeiboSpider.pipelines.FilePipeline': 300,
}

DOWNLOAD_DELAY = 1  # 间隔时间
# CONCURRENT_ITEMS = 1000
# CONCURRENT_REQUESTS = 100
# REDIRECT_ENABLED = False
# CONCURRENT_REQUESTS_PER_DOMAIN = 100
# CONCURRENT_REQUESTS_PER_IP = 0
# CONCURRENT_REQUESTS_PER_SPIDER=100
# DNSCACHE_ENABLED = True
# LOG_LEVEL = 'INFO'    # 日志级别
# CONCURRENT_REQUESTS = 70
