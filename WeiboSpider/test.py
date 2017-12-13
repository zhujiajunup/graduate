from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException
# from redis_cookies import RedisCookies
import os
from time import sleep
from PIL import Image
from code_recognize import YunDaMa
# from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
# dcap = dict(DesiredCapabilities.PHANTOMJS)
# driver = webdriver.PhantomJS(desired_capabilities=dcap)
browser = webdriver.Firefox()

yun_da_ma = YunDaMa(username='zhujiajun', password='vs7452014')


def save_verify_code_img():
    screen_shot_path = 'D:\\graduate\\WeiboSpider\\img\\screenshot.png'
    code_img_path = 'D:\\graduate\\WeiboSpider\\img\\verify_code.png'
    browser.save_screenshot(screen_shot_path)
    code_img = browser.find_element_by_xpath('//img[@node-type="verifycode_image"]')
    left = code_img.location['x']
    top = code_img.location['y']
    width = code_img.location['x'] + code_img.size['width']
    height = code_img.location['y'] + code_img.size['height']
    picture = Image.open('./img/screenshot.png')
    picture = picture.crop((left, top, width, height))
    picture.save(code_img_path)
    os.remove(screen_shot_path)
    return code_img_path


def login():
    try_time = 10
    browser.get('https://weibo.com/login.php')
    username = browser.find_element_by_id("loginname")
    username.clear()
    username.send_keys("g6382912shanlu4@163.com")
    psd = browser.find_element_by_xpath('//input[@type="password"]')
    psd.clear()
    psd.send_keys("vs7452014")
    commit_btn = browser.find_element_by_xpath('//a[@node-type="submitBtn"]')
    commit_btn.click()
    # 没那么快登录成功
    sleep(10)
    while try_time:
        try:
            # 如果登录不成功是有验证码框的
            browser.find_element_by_xpath('//div[@node-type="verifycode_box"]')
            img_path = save_verify_code_img()
            while not os.path.exists(img_path):
                print(img_path + "not exist")
                sleep(1)
            print(img_path)
            sleep(1)
            captcha_id, code_text = yun_da_ma.recognize(img_path)
            os.remove(img_path)
            code_str = bytes.decode(code_text)
            print('recognize result: %s' % code_str)
            code_input = browser.find_element_by_xpath('//input[@node-type="verifycode"]')

            code_input.clear()
            code_input.send_keys(code_str)
            commit_btn = browser.find_element_by_xpath('//a[@node-type="submitBtn"]')
            commit_btn.click()
            # 稍等一会
            sleep(5)
            try_time -= 1
        except NoSuchElementException:
            print('login success')
            break

    for elem in browser.get_cookies():
        print(elem["name"], elem["value"])
    print("end")


def home():
    browser.get('https://weibo.cn/1316949123/info')
login()
home()

# cookies_json = RedisCookies.fetch_cookies()
# # driver.delete_all_cookies()
# cookies = cookies_json['cookies']
# for k, v in cookies.items():
#     print(k, v)
#     driver.add_cookie({'name': k, 'value': v})
# driver.get('https://weibo.com/6014513352/Fz6Td1IgJ')
# print(driver.page_source)
