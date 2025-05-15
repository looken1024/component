# Redis

## 击穿、雪崩和穿透

1、缓存击穿：高并发时，当一个key非常热点（类似于爆款），在不停的扛着大并发，当这个key在失效的瞬间，持续的大并发就击穿缓存，直接请求数据库并设置到缓存中，导致性能下降

解决方案：a）永不过期；b）加锁排队；

2、缓存雪崩：缓存集中过期，或者缓存服务器宕机，导致大量请求访问数据库，造成数据库瞬间压力过大、宕机

解决方案：a）加锁排队；b）随机失效时间；c）redis高可用（哨兵、灾备）；

3、缓存穿透：数据库不存在缓存中也不存在，导致每次请求都会去查数据库，这时的用户很可能是攻击者，如发起id=-1的数据或id为特别大（不存在的数据），导致数据库压力过大或宕机

解决方案：a）参数校验；b）缓存空对象；c）布隆过滤器；

## 如何快速判断几十亿个数中是否存在某个数？

使用位图！

在redis-cli中设置位图

```shell
62.234.20.62:8888> setbit test_bit 123 1
(integer) 0
```

pom.xml

```shell
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>untitled</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>22</maven.compiler.source>
        <maven.compiler.target>22</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>3.16.4</version> <!-- 使用最新稳定版本 -->
        </dependency>
    </dependencies>
</project>
```

TestBit.java

```java
import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class TestBit {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://62.234.20.62:8888")
                .setPassword("*************"); // 如果有密码

        RedissonClient redissonClient = Redisson.create(config);
        RBitSet bitSet = redissonClient.getBitSet("test_bit");
        if (bitSet.get(123)) {
            System.out.println("123 exists");
        }
        else {
            System.out.println("123 does not exist");
        }
    }
}
```
