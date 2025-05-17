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

配置文件：

mybatis-config.xml：核心配置，包含数据源和映射文件位置。

UserMapper.xml：SQL 映射，通过 namespace 与接口绑定。

接口与 XML 映射：

Mapper 接口中的方法名和参数需与 XML 中的id和parameterType对应。

resultType指定返回类型（全限定类名或别名）。

SQL 参数：

#{}语法用于预编译 SQL，防止 SQL 注入。

事务管理：

默认手动提交事务，需调用session.commit()或session.rollback()。

这个样例涵盖了 MyBatis 的基本用法，包括环境配置、CRUD 操作和事务管理。你可以基于此样例扩展更多功能，如动态 SQL、关联查询等。

