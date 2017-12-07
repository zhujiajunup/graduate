import setting
from login import login
import user_agents
import random
import requests
from bs4 import BeautifulSoup
import re
from lxml import etree

class WeiboSpider:
    def __init__(self):
        self.user_info_url = 'https://weibo.cn/%s/info'
        self.sessions = []
        self.prepare_session()

    def start(self):
        return self.grab_user_info('1316949123')

    @staticmethod
    def get_header():
        header = {
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
            'Accept-Encoding': 'gzip, deflate, br',
            'Accept-Language': 'zh-CN,zh;q=0.9',
            'Connection': 'keep - alive',
            'Host': 'weibo.cn',
            'Referer': 'https://weibo.cn/?tf=5_009',
            'User - Agent': user_agents.USER_AGENTS[random.randint(0, len(user_agents.USER_AGENTS) - 1)]
        }
        return header

    def prepare_session(self):
        for account in setting.ACCOUNTS:
            self.sessions.append(login(account['user'], account['password']))

    def grab_user_info(self, user_id):
        session = self.sessions[random.randint(0, len(self.sessions) - 1)]
        response = session.get(self.user_info_url % user_id, verify=False)
        response.encoding = 'utf-8'
        user_info_html = BeautifulSoup(response.text, "lxml")
        div_list = list(user_info_html.find_all(class_=['c', 'tip']))
        base_info_index, edu_info_index, work_info_index = -1, -1, -1
        base_info = ''
        edu_info = ''
        work_info = ''
        tags = ''
        for index, div in enumerate(div_list):
            text = div.text
            if text == u'基本信息':
                base_info_index = index
            elif text == u'学习经历':
                edu_info_index = index
            elif text == u'工作经历':
                work_info_index = index
        if base_info_index != -1:
            b = div_list[base_info_index+1]
            print(b)
            tags = ','.join(map(lambda a: a.get_text(), b.find_all('a')))
            base_info = b.get_text(';')
        if edu_info_index != -1:

            edu_info = div_list[edu_info_index+1].get_text(';')

        if work_info_index != -1:

            work_info = div_list[work_info_index+1].get_text(';')
        print(base_info)
        nickname = re.findall(u'\u6635\u79f0[:|\uff1a](.*?);', base_info)  # 昵称
        gender = re.findall(u'\u6027\u522b[:|\uff1a](.*?);', base_info)  # 性别
        place = re.findall(u'\u5730\u533a[:|\uff1a](.*?);', base_info)  # 地区（包括省份和城市）
        signature = re.findall(u'\u7b80\u4ecb[:|\uff1a](.*?);', base_info)  # 个性签名
        birthday = re.findall(u'\u751f\u65e5[:|\uff1a](.*?);', base_info)  # 生日
        sex_orientation = re.findall(u'\u6027\u53d6\u5411[:|\uff1a](.*?);', base_info)  # 性取向
        marriage = re.findall(u'\u611f\u60c5\u72b6\u51b5[:|\uff1a](.*?);', base_info)  # 婚姻状况
        print(nickname, gender, signature, birthday, edu_info, work_info)
        return user_info_html
        #
        # html_content = user_info_html.html
        # html_tree = etree.HTML(str(html_content))
        # print(html_tree.xpath('/'))
        # all_text = []
        # base_info_index, edu_info_index, work_info_index = -1, -1, -1


        # print(div_list)

        #
        # text1 = ";".join(all_text)  # 获取标签里的所有text()
        # nickname = re.findall(u'\u6635\u79f0[:|\uff1a](.*?);', base_info)  # 昵称
        # gender = re.findall(u'\u6027\u522b[:|\uff1a](.*?);', base_info)  # 性别
        # place = re.findall(u'\u5730\u533a[:|\uff1a](.*?);', base_info)  # 地区（包括省份和城市）
        # signature = re.findall(u'\u7b80\u4ecb[:|\uff1a](.*?);', base_info)  # 个性签名
        # birthday = re.findall(u'\u751f\u65e5[:|\uff1a](.*?);', base_info)  # 生日
        # sex_orientation = re.findall(u'\u6027\u53d6\u5411[:|\uff1a](.*?);', base_info)  # 性取向
        # marriage = re.findall(u'\u611f\u60c5\u72b6\u51b5[:|\uff1a](.*?);', base_info)  # 婚姻状况
        # url = re.findall(u'\u4e92\u8054\u7f51[:|\uff1a](.*?);', text1)  # 首页链接
        # print(nickname, gender, tags, place, signature, birthday, sex_orientation, marriage, url)


if __name__ == '__main__':
    WeiboSpider().start()