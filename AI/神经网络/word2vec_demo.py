import pandas as pd
import jieba
import word2vec

# 读取数据
dataframe = pd.read_csv('word2vec_input.csv', encoding='utf-8')
# 对文本列进行分词
dataframe['分词结果'] = dataframe['文本列'].apply(lambda x: ' '.join(jieba.cut(x)))
# 保存为word2vec需要的格式
dataframe['分词结果'].to_csv('word2vec_jieba_output.txt', index=False, header=False)

# 训练模型
word2vec.word2vec(train='word2vec_jieba_output.txt', 
                  output='word2vec_model.bin', 
                  size=100,  # 词向量维度
                  window=5,  # 上下文窗口大小
                  negative=5,  # 负采样数
                  threads=4,  # 线程数
                  min_count=1,
                  iter_=5)    # 迭代次数

