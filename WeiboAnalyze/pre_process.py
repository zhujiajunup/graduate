import re

def split_data():
    target_file = open(file='./data/tweet_proc.csv', mode='w', encoding='utf-8')
    expression_file = open(file='./data/tweet_expression.csv', mode='w', encoding='utf-8')
    tweet_original = open(file='./data/tweet_original.csv', mode='w', encoding='utf-8')
    expression_pattern = re.compile('.*\[.*\].*')
    with open(file='./data/tweet.csv', mode='r', encoding='utf-8') as f:
            for line in f:
                new_line = line.replace('\t', ' ').strip()
                target_file.write(new_line + '\n')
                if expression_pattern.match(new_line):
                    expression_file.write(new_line + '\n')

    target_file.close()


def split_emoji():
    reply_pattern = re.compile('回复@.*?:(.*)')
    forward_pattern = re.compile('@.*?:(.*)')
    emoji_pattern = re.compile('\[(.*?)\]')

    def load_emoji(file_name):
        emoji = set()
        with open(file=file_name, encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                emoji.add(line[1:-1])
        return emoji

    emoji_positive = load_emoji('./data/emoji_positive')
    emoji_negative = load_emoji('./data/emoji_negative')
    positive_file = open(file='./data/tweet_positive.csv', mode='w', encoding='utf-8')
    negative_file = open(file='./data/tweet_negative.csv', mode='w', encoding='utf-8')
    with open(file='./data/tweet_expression.csv', encoding='utf-8') as f:
        for line in f:
            for w in line.split('//'):
                w = w.strip()
                print(w)
                reply = reply_pattern.findall(w)
                if reply:
                    content = reply[0]
                else:
                    c = forward_pattern.findall(w)
                    if c:
                        content = c[0]
                    else:
                        content = w
                print(type(content))
                print(content)
                emojis = emoji_pattern.findall(content)
                negative_cnt = 0
                positive_cnt = 0
                for emoji in emojis:
                    if emoji in emoji_negative:
                        negative_cnt += 1
                    elif emoji in emoji_positive:
                        positive_cnt += 1
                if positive_cnt > negative_cnt:
                    positive_file.write(content + '\n')
                elif negative_cnt > positive_cnt:
                    negative_file.write(content + '\n')
    negative_file.close()
    positive_file.close()


split_emoji()
