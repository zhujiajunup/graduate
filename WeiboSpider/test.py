from selenium import webdriver
from redis_cookies import RedisCookies
from urllib.request import urlretrieve
from time import sleep
import requests
# from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
# dcap = dict(DesiredCapabilities.PHANTOMJS)
# driver = webdriver.PhantomJS(desired_capabilities=dcap)
browser = webdriver.Firefox()
browser.get('https://weibo.com/login.php')
username = browser.find_element_by_id("loginname")
username.clear()
username.send_keys("767543579@qq.com")
sleep(1)
psd = browser.find_element_by_xpath('//input[@type="password"]')
psd.clear()
psd.send_keys("jvs7452014@jjzhu")
sleep(1)
commit_btn = browser.find_element_by_xpath('//a[@node-type="submitBtn"]')
commit_btn.click()
sleep(10)
while True:
    verify_box = browser.find_element_by_xpath('//div[@node-type="verifycode_box"]')
    if verify_box:
        code_img = browser.find_element_by_xpath('//img[@node-type="verifycode_image"]')
        img_url = code_img.get_attribute('src')
        urlretrieve(img_url, "img.png")

        browser.save_screenshot("verify_code.png")
        code = input("输入验证码：")
        code_input = browser.find_element_by_xpath('//input[@node-type="verifycode"]')
        code_input.clear()
        code_input.send_keys(code)
        commit_btn = browser.find_element_by_xpath('//a[@node-type="submitBtn"]')
        commit_btn.click()
    else:
        break
for elem in browser.get_cookies():
    print(elem["name"], elem["value"])
print("end")
# cookies_json = RedisCookies.fetch_cookies()
# # driver.delete_all_cookies()
# cookies = cookies_json['cookies']
# for k, v in cookies.items():
#     print(k, v)
#     driver.add_cookie({'name': k, 'value': v})
# driver.get('https://weibo.com/6014513352/Fz6Td1IgJ')
# print(driver.page_source)
