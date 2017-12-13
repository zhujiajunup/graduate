from scrapy import cmdline

cmdline.execute("scrapy crawl WeiboSpider3".split())
# import yaml
# f = open('./WeiboSpider3/conf/weibo.yaml')
# print yaml.load(f)