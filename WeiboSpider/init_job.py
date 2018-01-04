from redis_cookies import RedisJob
from weibo_cn import JobType

RedisJob.push_job(JobType.comment.value, {'url': 'https://weibo.cn/comment/FCoPpaIQp', 'tweetId': 'FCoPpaIQp'})