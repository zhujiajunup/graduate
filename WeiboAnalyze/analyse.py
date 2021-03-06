from __future__ import absolute_import  # 导入3.x的特征函数
from __future__ import print_function
import pandas as pd
import numpy as np
import jieba

from keras.preprocessing import sequence
from keras.optimizers import SGD, RMSprop

from keras.utils import np_utils
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation
from keras.layers.embeddings import Embedding
from keras.layers.recurrent import LSTM, GRU

negative = pd.read_csv('./data/tweet_negative.csv', header=None, error_bad_lines=False)
positive = pd.read_csv('./data/tweet_positive.csv', header=None, error_bad_lines=False)
negative['mark'] = 0
positive['mark'] = 1

pos_neg = pd.concat([positive, negative], ignore_index=True)
print('cut words start')
pos_neg['words'] = pos_neg[0].apply(lambda x: list(jieba.cut(x)))
print('cut words end')
d2v_train = pos_neg['words']
total_words = []
for w in d2v_train:
    total_words.extend(w)

word_count = pd.DataFrame(pd.Series(total_words).value_counts())
del total_words, d2v_train
word_count['id'] = list(range(1, len(word_count)+1))
pos_neg['sent'] = pos_neg['words'].apply(lambda x: list(word_count['id'][x]))
maxlen = 50
print("Pad sequences (samples x time)")
pos_neg['sent'] = list(sequence.pad_sequences(pos_neg['sent'], maxlen=maxlen))
x = np.array(list(pos_neg['sent']))[::2]  # 训练集
y = np.array(list(pos_neg['mark']))[::2]
xt = np.array(list(pos_neg['sent']))[1::2]  # 测试集
yt = np.array(list(pos_neg['mark']))[1::2]
xa = np.array(list(pos_neg['sent']))  # 全集
ya = np.array(list(pos_neg['mark']))

print('Build model...')
model = Sequential()
model.add(Embedding(len(word_count) + 1, 256))
model.add(LSTM(128))  # try using a GRU instead, for fun
model.add(Dropout(0.5))
model.add(Dense(128))
model.add(Activation('sigmoid'))

model.compile(loss='binary_crossentropy', optimizer='adam', class_mode="binary")

model.fit(xa, ya, batch_size=16, nb_epoch=10)  # 训练时间为若干个小时

classes = model.predict_classes(xa)
acc = np_utils.accuracy(classes, ya)
print('Test accuracy:', acc)
