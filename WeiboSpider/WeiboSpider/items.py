# encoding=utf-8

from scrapy import Item, Field


class InformationItem(Item):
    """ 个人信息 """
    _id = Field()  # 用户ID
    nick_name = Field()  # 昵称
    gender = Field()  # 性别

    location = Field()  # 所在城市
    signature = Field()  # 个性签名
    birthday = Field()  # 生日
    tweets_num = Field()  # 微博数
    follows_num = Field()  # 关注数
    fans_num = Field()  # 粉丝数
    sex_orientation = Field()  # 性取向
    marriage = Field()  # 婚姻状况
    url = Field()  # 首页链接
    edu_info = Field()
    work_info = Field()
    tags = Field()

    def __str__(self):
        return self['nick_name'] + '\t' + self['gender'] + '\t' + self['location'] + '\t' + self['tags']


class FlagItem(Item):
    weibo_id = Field()


class CommentItem(Item):
    weibo_id = Field()
    id = Field()  # 评论id
    user = Field()  # 评论用户
    content = Field()  # 评论内容
    source = Field()  # 评论来源
    time = Field()  # 评论发表时间

    def __str__(self):
        return self['user'] + "\t" + self['content'] + "...\t" + self['time']


class TweetsItem(Item):
    """ 微博信息 """
    _id = Field()  # 用户ID-微博ID
    id = Field()  # 用户ID
    content = Field()  # 微博内容
    pubTime = Field()  # 发表时间
    coordinates = Field()  # 定位坐标
    tools = Field()  # 发表工具/平台
    like = Field()  # 点赞数
    comment = Field()  # 评论数
    transfer = Field()  # 转载数
    type = Field()  # 类型 转发|原创|点赞

    def __str__(self):
        return '--------------------------------------------------------------------------\n' \
               '|\t用户\t|\t\t微博\t\t|\t来源\t|\t发布时间\t|\t微博id\t|\n' \
               '------------------------------------------------------------------------------\n' \
               '|%s\t|\t%s\t|\t%s\t|\t%s\t|\t%s\t|\n' \
               '------------------------------------------------------------------------------\n' \
               % (
               self["id"], self["content"][:20], self["tools"] if 'Tools' in self else '', self['pubTime'], self['_id'])


class FollowsItem(Item):
    """ 关注人列表 """
    _id = Field()  # 用户ID
    follows = Field()  # 关注


class FansItem(Item):
    """ 粉丝列表 """
    _id = Field()  # 用户ID
    fans = Field()  # 粉丝
