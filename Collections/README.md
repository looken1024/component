# Collections

## 从列表中二分查找

```java
List<String> list = new ArrayList<String>();
list.add("A");
list.add("B");
list.add("C");
list.add("D");
int res = Collections.binarySearch(list, "C"); // 索引
System.out.println(res);
System.out.println(list.get(res));
```

## 列表排序

```java
List<String> s = new ArrayList<>();
s.add("d");
s.add("b");
s.add("a");
s.add("c");
Collections.sort(s);
System.out.println(s);
```

## 将一个列表的内容复制到另一个列表（目标列表的size不能为0）

```java
List<String> s = new ArrayList<>();
s.add("d");
s.add("b");
s.add("a");
s.add("c");
List<String> list = new ArrayList<>();
list.add("a");
list.add("b");
list.add("c");
list.add("d");
Collections.copy(list, s);
System.out.println(list);
```

## 判断两个列表是否存在交集

```java
List<String> s = new ArrayList<>();
s.add("d");
s.add("b");
s.add("a");
s.add("c");
List<String> s2 = new ArrayList<>();
s2.add("d2");
s2.add("b2");
s2.add("c");
boolean res = Collections.disjoint(s, s2);
System.out.println(res);
```

## 创建空列表

```java
List<String> s = Collections.emptyList();
s.add("1"); // 这个空列表不支持任何修改操作（如 add、remove、set），调用这些方法会直接抛出UnsupportedOperationException
System.out.println(s);
```

## 计算出现频率

```java
List<String> s = new ArrayList<>();
s.add("1");
s.add("2");
s.add("1");
s.add("4");
s.add("5");
int f = Collections.frequency(s, "1");
System.out.println(f);
```

## 填充默认值

```java
List<String> s = new ArrayList<>();
s.add("1");
s.add("2");
s.add("1");
s.add("4");
s.add("5");
Collections.fill(s, null);
System.out.println(s);
```

## 最大最小值

```java
List<String> s = new ArrayList<>();
s.add("1");
s.add("2");
s.add("1");
s.add("4");
s.add("5");
String res = Collections.min(s);
System.out.println(res);
```

## 反转

```java
List<String> s = new ArrayList<>();
s.add("1");
s.add("2");
s.add("1");
s.add("4");
s.add("5");
Collections.reverse(s);
System.out.println(s);
```

## 旋转

```java
List<String> s = new ArrayList<>();
s.add("1");
s.add("2");
s.add("1");
s.add("4");
s.add("5");
Collections.rotate(s, -1);
System.out.println(s);
```

## 交换不同位置的元素

```java
List<String> s = new ArrayList<>();
s.add("1");
s.add("2");
s.add("1");
s.add("4");
s.add("5");
Collections.swap(s, 1, 2);
System.out.println(s);
```


