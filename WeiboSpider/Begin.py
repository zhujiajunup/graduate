from scrapy import cmdline

cmdline.execute("scrapy crawl WeiboSpider".split())
# import yaml
# f = open('./WeiboSpider/conf/weibo.yaml')
# print yaml.load(f)