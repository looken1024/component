# MyBatis 

下面是一个简单的 MyBatis 入门样例，包含基础配置和 CRUD 操作。这个示例使用 MyBatis 3.5.11 版本，通过 Maven 管理依赖。

## 1. 项目结构

```shell
mybatis-demo/
├── src/main/java/
│   └── com/example/demo/
│       ├── entity/          # 实体类
│       │   └── User.java
│       ├── mapper/          # Mapper接口
│       │   └── UserMapper.java
│       ├── utils/           # 工具类
│       │   └── MyBatisUtil.java
│       └── App.java         # 主程序
├── src/main/resources/
│   ├── mybatis-config.xml   # MyBatis核心配置
│   └── mapper/
│       └── UserMapper.xml   # SQL映射文件
└── pom.xml                  # Maven依赖
```

## 2. 依赖配置（pom.xml）

```shell
<dependencies>
    <!-- MyBatis核心 -->
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.5.11</version>
    </dependency>
    <!-- MySQL驱动 -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
    <!-- 日志 -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.36</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.11</version>
    </dependency>
</dependencies>
```

## 3. 数据库表结构

```shell
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    age INT
);
```

## 4. 实体类（User.java）

```java
package com.example.demo.entity;

public class User {
    private Integer id;
    private String username;
    private String email;
    private Integer age;

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
```

## 5. Mapper 接口（UserMapper.java）

```java
package com.example.demo.mapper;

import com.example.demo.entity.User;
import java.util.List;

public interface UserMapper {
    // 查询所有用户
    List<User> selectAll();

    // 根据ID查询用户
    User selectById(Integer id);

    // 插入用户
    int insert(User user);

    // 更新用户
    int update(User user);

    // 删除用户
    int deleteById(Integer id);
}
```

## 6. MyBatis 配置（mybatis-config.xml）

```shell
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 配置环境 -->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/test?useSSL=false"/>
                <property name="username" value="root"/>
                <property name="password" value="yourpassword"/>
            </dataSource>
        </environment>
    </environments>

    <!-- 映射器 -->
    <mappers>
        <mapper resource="mapper/UserMapper.xml"/>
    </mappers>
</configuration>
```

## 7. SQL 映射文件（UserMapper.xml）

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.demo.mapper.UserMapper">

    <!-- 查询所有用户 -->
    <select id="selectAll" resultType="com.example.demo.entity.User">
        SELECT * FROM users
    </select>

    <!-- 根据ID查询用户 -->
    <select id="selectById" parameterType="int" resultType="com.example.demo.entity.User">
        SELECT * FROM users WHERE id = #{id}
    </select>

    <!-- 插入用户 -->
    <insert id="insert" parameterType="com.example.demo.entity.User">
        INSERT INTO users (username, email, age)
        VALUES (#{username}, #{email}, #{age})
    </insert>

    <!-- 更新用户 -->
    <update id="update" parameterType="com.example.demo.entity.User">
        UPDATE users
        SET username = #{username},
            email = #{email},
            age = #{age}
        WHERE id = #{id}
    </update>

    <!-- 删除用户 -->
    <delete id="deleteById" parameterType="int">
        DELETE FROM users WHERE id = #{id}
    </delete>
</mapper>
```

## 8. 工具类（MyBatisUtil.java）

```java

package com.example.demo.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisUtil {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            // 加载配置文件
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            // 构建SqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取SqlSession
    public static SqlSession getSession() {
        return sqlSessionFactory.openSession();
    }
}
```

## 9. 测试主程序（App.java）

```java
package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class App {
    public static void main(String[] args) {
        // 插入用户
        insertUser();

        // 查询所有用户
        selectAllUsers();

        // 根据ID查询用户
        selectUserById(1);

        // 更新用户
        updateUser();

        // 删除用户
        deleteUser();
    }

    // 插入用户
    public static void insertUser() {
        SqlSession session = MyBatisUtil.getSession();
        try {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = new User();
            user.setUsername("test");
            user.setEmail("test@example.com");
            user.setAge(25);
            
            int rows = mapper.insert(user);
            System.out.println("插入成功，影响行数：" + rows);
            
            // 提交事务
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            session.rollback();
        } finally {
            session.close();
        }
    }

    // 查询所有用户
    public static void selectAllUsers() {
        try (SqlSession session = MyBatisUtil.getSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            List<User> users = mapper.selectAll();
            System.out.println("所有用户：");
            users.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 根据ID查询用户
    public static void selectUserById(int id) {
        try (SqlSession session = MyBatisUtil.getSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectById(id);
            System.out.println("ID为" + id + "的用户：" + user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 更新用户
    public static void updateUser() {
        SqlSession session = MyBatisUtil.getSession();
        try {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = new User();
            user.setId(1);
            user.setUsername("updated");
            user.setEmail("updated@example.com");
            user.setAge(30);
            
            int rows = mapper.update(user);
            System.out.println("更新成功，影响行数：" + rows);
            
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        } finally {
            session.close();
        }
    }

    // 删除用户
    public static void deleteUser() {
        SqlSession session = MyBatisUtil.getSession();
        try {
            UserMapper mapper = session.getMapper(UserMapper.class);
            int rows = mapper.deleteById(1);
            System.out.println("删除成功，影响行数：" + rows);
            
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.rollback();
        } finally {
            session.close();
        }
    }
}
```

### 运行步骤

创建数据库：执行上面的 SQL 脚本创建users表。

配置数据库连接：在mybatis-config.xml中修改数据库用户名和密码。

运行主程序：执行App.java中的main方法，观察控制台输出。

### 关键点说明

#### 配置文件：

mybatis-config.xml：核心配置，包含数据源和映射文件位置。

UserMapper.xml：SQL 映射，通过 namespace 与接口绑定。

#### 接口与 XML 映射：

Mapper 接口中的方法名和参数需与 XML 中的id和parameterType对应。

resultType指定返回类型（全限定类名或别名）。

#### SQL 参数：

#{}语法用于预编译 SQL，防止 SQL 注入。

#### 事务管理：

默认手动提交事务，需调用session.commit()或session.rollback()。

这个样例涵盖了 MyBatis 的基本用法，包括环境配置、CRUD 操作和事务管理。你可以基于此样例扩展更多功能，如动态 SQL、关联查询等。


# MyBatis如何实现数据库连接池？

MyBatis 本身并不内置数据库连接池实现，而是通过 数据源（DataSource）配置 来集成第三方连接池或使用 JDBC 原生连接。其核心是通过 <dataSource> 标签指定连接池类型，并依赖具体的连接池实现类。以下是详细的实现方式和示例：

### 一、MyBatis 支持的数据源类型

在 mybatis-config.xml 的 <environments> 标签中，通过 <dataSource type="XXX"> 配置数据源类型，MyBatis 支持三种类型：

|类型|	说明|
|---------|----------|
|UNPOOLED|	不使用连接池，每次请求创建新的数据库连接（性能差，适用于简单场景）。|
|POOLED|	使用 MyBatis 内置的简易连接池（基于传统的 DataSource 实现，支持基本连接复用）。|
|JNDI|	从应用服务器（如 Tomcat、WildFly）的 JNDI 中获取数据源（适用于 Java EE 环境）。|

### 二、使用 MyBatis 内置连接池（POOLED）

MyBatis 自带一个轻量级连接池实现（PooledDataSource），通过配置即可启用，无需额外依赖。

配置示例（mybatis-config.xml）

```shell
<environments default="development">
    <environment id="development">
        <transactionManager type="JDBC"/> <!-- 使用 JDBC 事务管理 -->
        <dataSource type="POOLED"> <!-- 指定为连接池模式 -->
            <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="url" value="jdbc:mysql://localhost:3306/test?useSSL=false"/>
            <property name="username" value="root"/>
            <property name="password" value="your-password"/>
            
            <!-- 连接池可选配置（默认值见下方说明） -->
            <property name="poolMaximumActiveConnections" value="10"/>  <!-- 最大活跃连接数（默认 10） -->
            <property name="poolMaximumIdleConnections" value="5"/>   <!-- 最大空闲连接数（默认 5） -->
            <property name="poolMaximumCheckoutTime" value="20000"/> <!-- 连接最大占用时间（毫秒，默认 20000） -->
            <property name="poolTimeToWait" value="2000"/>           <!-- 连接获取等待时间（毫秒，默认 2000） -->
        </dataSource>
    </environment>
</environments>
```

核心配置参数

|参数名|	说明|
|----------|---------|
|poolMaximumActiveConnections|	连接池最大活跃连接数（同一时间能被使用的最大连接数）。|
|poolMaximumIdleConnections|	连接池最大空闲连接数（保持空闲但不关闭的连接数）。|
|poolMaximumCheckoutTime	|连接被占用的最大时间（超时后强制回收，避免连接泄漏）。|
|poolTimeToWait|	当连接池无可用连接时，等待新连接的最大时间（超时抛异常）。|
|poolPingQuery|	验证连接有效性的 SQL 语句（如 SELECT 1，默认不验证）。|
|poolPingEnabled|	是否开启连接有效性验证（默认 false）。|
|poolPingConnectionsNotUsedFor|	闲置多久的连接需要被验证（毫秒，默认 0 表示不验证）。|

### 三、集成第三方连接池（推荐）

MyBatis 推荐使用 HikariCP、DBCP、C3P0 等专业连接池，性能更强、配置更丰富。以下是集成步骤：

#### 1. 添加依赖（以 HikariCP 为例）

```shell
<!-- Maven 依赖 -->
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

#### 2. 配置数据源为 UNPOOLED 并指定连接池实现类

在 mybatis-config.xml 中，将 <dataSource type="UNPOOLED"> 与第三方连接池结合，通过 type 指定连接池的全限定类名：

```shell
<dataSource type="com.zaxxer.hikari.HikariDataSource"> <!-- 直接指定 HikariCP 实现类 -->
    <!-- HikariCP 配置参数 -->
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/test?useSSL=false"/>
    <property name="username" value="root"/>
    <property name="password" value="your-password"/>
    
    <!-- HikariCP 特有配置 -->
    <property name="maximumPoolSize" value="15"/>       <!-- 最大连接数 -->
    <property name="minimumIdle" value="5"/>           <!-- 最小空闲连接数 -->
    <property name="idleTimeout" value="60000"/>        <!-- 空闲连接超时时间（毫秒） -->
    <property name="connectionTimeout" value="30000"/>  <!-- 连接获取超时时间（毫秒） -->
    <property name="autoCommit" value="true"/>          <!-- 是否自动提交事务 -->
</dataSource>
```

#### 3. 常见第三方连接池配置对比

|连接池|	MyBatis 配置 type 值	|核心配置参数示例|
|---------|-------------|-------------|
|HikariCP|	com.zaxxer.hikari.HikariDataSource|	maximumPoolSize、idleTimeout、connectionTestQuery（SQL 验证语句）|
|DBCP2|	org.apache.commons.dbcp2.BasicDataSource|	maxTotal（最大连接数）、maxIdle（最大空闲数）、validationQuery|
|C3P0|	com.mchange.v2.c3p0.ComboPooledDataSource	|maxPoolSize、minPoolSize、checkoutTimeout、preferredTestQuery|

### 四、连接池的工作原理

无论使用内置还是第三方连接池，核心流程如下：

初始化连接池：应用启动时创建一定数量的空闲连接（minimumIdle），存入连接池。

获取连接：业务请求时从连接池获取空闲连接，若没有则创建新连接（不超过 maximumPoolSize）。

使用连接：执行 SQL 操作，连接使用期间标记为 “活跃”。

释放连接：使用完毕后，连接归还给连接池，标记为 “空闲” 而非关闭。

连接管理：连接池定期清理超时连接（idleTimeout），验证连接有效性（通过 validationQuery），避免无效连接堆积。

### 五、最佳实践建议

优先使用第三方连接池：MyBatis 内置连接池功能简单，生产环境推荐 HikariCP（性能最优）或 DBCP2。

配置连接验证：通过 connectionTestQuery 或 validationQuery 设置健康检查 SQL（如 SELECT 1），防止获取到已失效的连接。

结合 Spring 管理：在 Spring 项目中，通过 Spring 的 DataSource 配置统一管理连接池，避免 MyBatis 单独配置（例如使用 spring-datasource 自动配置）。

监控连接池状态：利用 HikariCP 自带的监控接口或集成 Micrometer，监控连接池的活跃连接数、等待时间等指标。

### 总结

MyBatis 通过 数据源抽象层 实现连接池集成：

内置连接池：使用 POOLED 类型，适合简单场景，配置轻量但功能有限。

第三方连接池：通过 UNPOOLED 类型 + 连接池实现类（如 HikariCP），功能更强大，推荐生产环境使用。

通过合理配置连接池参数，可以显著提升数据库访问性能和稳定性，避免频繁创建连接带来的开销。






