#!/usr/bin/python
# -*- coding: utf-8 -*-

import re

data = 'testasdtest'

# match是从位置0开始匹配
a = re.match('[abct]{1}', data)

print(a)
if a:
    print(a.group())
    print(a.span())


# 在整个字符串中搜索第一个匹配的值
b = re.search('asd', data)
print(b)
if b:
    print(b.group())
    print(b.span())


# 在“整个字符串”中搜索“所有符合”匹配模式的字符串
c = re.findall('te', data)
print(c)

c2 = re.findall('e(s[dhwqt]{1})', data)
print(c2)


# 字符串替换
d = re.sub('st', ' 222 ', data)
print(d)

d2 = re.sub('st', ' 222 ', data, 1)
print(d2)


# 分割字符串
e = re.split('st', data)
print(e)

e2 = re.split('st', data, 1)
print(e2)

