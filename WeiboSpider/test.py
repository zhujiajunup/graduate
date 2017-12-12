from selenium import webdriver
from redis_cookies import RedisCookies
from time import sleep
# from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
# dcap = dict(DesiredCapabilities.PHANTOMJS)
# driver = webdriver.PhantomJS(desired_capabilities=dcap)
browser = webdriver.Firefox()
browser.get('https://weibo.com/login.php')
username = browser.find_element_by_id("loginname")
username.clear()
username.send_keys("767543579@qq.com")
psd = browser.find_element_by_xpath('//input[@type="password"]')
psd.clear()
psd.send_keys("jvs7452014@jjzhu")
commit_btn = browser.find_element_by_xpath('//a[@node-type="submitBtn"]')
commit_btn.click()
sleep(10)
verify_box = browser.find_element_by_xpath('//div[@node-type="verifycode_box"]')
if verify_box:
    browser.save_screenshot("verify_code.png")
    code = input("输入验证码：")
    code_input = browser.find_element_by_xpath('//input[@node-type="verifycode"]')
    code_input.clear()
    code_input.send_keys(code)
    commit_btn = browser.find_element_by_xpath('//a[@node-type="submitBtn"]')
    commit_btn.click()
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
