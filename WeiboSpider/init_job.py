from redis_cookies import RedisJob
from weibo_cn import JobType

RedisJob.push_job(JobType.comment.value, {'url': 'https://weibo.cn/comment/FCa7KxpHE', 'tweetId': 'FCa7KxpHE'})
# RedisJob.push_job(JobType.user.value, {'user_id': '2210643391'})
# RedisJob.push_job(JobType.tweet.value, {'url': 'https://weibo.cn/2210643391', 'uid': '2210643391'})
