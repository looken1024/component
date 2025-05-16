# Spring Boot常用注解

## Spring & Spring Boot 常用注解整理

现代的 Spring 与 Spring Boot 应用大量使用注解来简化配置、管理组件和实现各种框架功能。本文系统整理了常用的 Spring/Spring Boot 注解，按照功能分类进行介绍。每个注解都会涵盖其含义、提供来源、应用场景以及代码示例，帮助开发者深入理解和快速检索。

## 一、Spring Boot 核心注解

@SpringBootApplication

简介： @SpringBootApplication 是 Spring Boot 应用的主入口注解。它标注在启动类上，表示这是一个 Spring Boot 应用。该注解由 Spring Boot 提供（位于 org.springframework.boot.autoconfigure 包），本质上是一个组合注解，包含了 Spring Framework 和 Spring Boot 的关键配置注解。

作用与场景： 使用 @SpringBootApplication 标记主类后，Spring Boot 会自动进行以下配置：

配置类声明： 包含了 @SpringBootConfiguration（其本身是 @Configuration 的特化），因此该类被视为配置类，可定义 Bean。

组件扫描： 内含 @ComponentScan，会自动扫描该类所在包及其子包下的组件（被诸如 @Component、@Service、@Controller 等注解标记的类），将它们注册为 Spring 容器中的 Bean。

自动配置： 内含 @EnableAutoConfiguration，根据类路径下依赖自动配置 Spring Boot 应用。例如，若 classpath 中存在 HSQLDB 数据库依赖，则会自动配置内存数据库等。开发者无需手动编写大量配置即可启动应用。

使用示例： 创建一个 Spring Boot 主启动类，在类上添加 @SpringBootApplication 注解，并编写 main 方法启动应用：

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

上述代码中，MyApplication 类由 @SpringBootApplication 注解标记为应用入口。运行 SpringApplication.run 后，Spring Boot 将引导启动内嵌服务器、初始化 Spring 容器，自动扫描组件并完成配置。

注： @SpringBootApplication 提供了属性用于定制，如 exclude 可排除特定的自动配置类。如果需要禁用某些自动配置，可以使用例如 @SpringBootApplication(exclude = DataSourceAutoConfiguration.class) 来排除。

## 二、Spring 容器与组件注册注解

这一类注解用于将类注册为 Spring 容器管理的组件或定义配置，以取代传统的 XML 配置文件，实现注解驱动的装配。

@Component

简介： @Component 是一个通用的组件注解，由 Spring Framework 提供（org.springframework.stereotype.Component）。它用于将一个普通的 Java 类标识为 Spring 容器中的 Bean。被标注的类在组件扫描时会被发现并实例化，由容器统一管理生命周期。

作用与场景： 当某个类不好归类到特定层时，可以使用 @Component 进行标注。典型场景如工具类、通用逻辑处理类等。使用 @Component 后，无需在 XML 中声明 bean，Spring 会根据配置的扫描路径自动将其注册。提供模块： Spring Context 模块提供对组件扫描和 @Component 注解的支持。

使用示例： 定义一个组件类并演示注入：

```java
@Component
public class MessageUtil {
    public String getWelcomeMessage() {
        return "Welcome to Spring!";
    }
}

// 使用组件
@Service
public class GreetingService {
    @Autowired
    private MessageUtil messageUtil;

    public void greet() {
        System.out.println(messageUtil.getWelcomeMessage());
    }
}
```

上例中，MessageUtil 类通过 @Component 标记成为容器 Bean，GreetingService 中使用 @Autowired（详见后文）将其注入，最后调用其方法。

@Service

简介： @Service 是 @Component 的一种特化，用于标注业务逻辑层的组件（Service层）。它位于 Spring 框架的 org.springframework.stereotype 包。

作用与场景： 在分层架构中，服务层类使用 @Service 注解，使代码含义更语义化。尽管行为上和 @Component 相同（被扫描注册为 Bean），@Service 强调该类承担业务服务职责。提供模块： Spring Context，同属于组件模型的一部分。

使用示例：

```java
@Service
public class OrderService {
    public void createOrder(Order order) {
        // 业务逻辑：创建订单
    }
}
```

通过 @Service，OrderService 会被自动扫描注册。在需要使用它的地方，例如控制层或其他服务层，可以通过依赖注入获取该 Bean 实例。

@Repository

简介： @Repository 是 @Component 的特化注解之一，用于标注数据访问层组件（DAO层，或仓库类）。定义在 Spring Framework 的 org.springframework.stereotype.Repository 包中。

作用与场景： DAO 类（例如访问数据库的类）使用 @Repository 注解不仅可以被扫描为容器 Bean，还能启用异常转换功能。Spring DAO 层会捕获底层数据访问异常（如 JDBC 的 SQLException 或 JPA 的异常），将其翻译为 Spring 统一的DataAccessException体系，从而简化异常处理。换句话说，如果一个类标注为 @Repository，Spring 在为其创建代理时会自动处理持久化异常，将原始异常转为 Spring 的非检查型数据访问异常，以提高健壮性。另外，标注了 @Repository 的类可以被 @Autowired 等注解自动装配到其他地方。

使用示例：

```java
@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id=?", 
                                               new BeanPropertyRowMapper<>(User.class), id);
        } catch (DataAccessException e) {
            // Spring 已将底层SQLException翻译为DataAccessException
            throw e;
        }
    }
}
```

上例中，UserDao 使用 @Repository 注解，使其成为容器管理的DAO组件。Spring 自动为其提供异常转换功能：如果 JDBC 操作抛出 SQLException，会被翻译为 DataAccessException（RuntimeException），调用处可以统一处理。标注后也允许通过 @Autowired 注入到服务层使用。

@Controller

简介： @Controller 是 Spring MVC 的控制层组件注解，同样派生自 @Component。由 Spring Web MVC 模块提供（org.springframework.stereotype.Controller），用于标识一个类是Web MVC 控制器，负责处理 HTTP 请求并返回视图或响应。

作用与场景： 在 Web 应用程序中，@Controller 注解的类会被 DispatcherServlet 识别为控制器，用于映射请求URL、封装模型数据并返回视图名。通常配合视图模板（如 Thymeleaf、JSP）返回页面。如果需要直接返回 JSON 数据，可以配合 @ResponseBody 或直接使用 @RestController（后者见下文）。

使用示例：

```
@Controller
public class HomeController {
    @RequestMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("msg", "Hello Spring MVC");
        return "home"; // 返回视图名，由视图解析器解析为页面
    }
}
```

上述 HomeController 使用 @Controller 标记，提供一个映射 “/home” 请求的处理方法。返回值 "home" 代表视图逻辑名，框架会根据配置解析到具体的页面（如 home.html）。如果我们在类上使用了 @Controller，框架在启动时会自动注册相应的映射。

@RestController

简介： @RestController 是 Spring 提供的组合注解，等价于同时在类上使用 @Controller 和 @ResponseBody。它主要由 Spring Web 模块提供，用于RESTful Web服务的控制器。

作用与场景： 标注 @RestController 的类会被识别为控制器，并且其每个处理方法的返回值会直接作为 HTTP 响应体输出，而不是作为视图名称解析。适用于需要返回 JSON、XML 等数据的场景，比如 Web API 接口。模块提供： Spring Web（Spring MVC）。

使用示例：

```
@RestController
@RequestMapping("/api")
public class UserApiController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, RESTful";
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        // 直接接收JSON反序列化为User对象，处理后返回
        return userService.save(user);
    }
}
```

UserApiController 使用 @RestController 注解，其方法返回字符串和对象将直接通过消息转换器写入响应（例如字符串作为纯文本，User 对象会序列化为 JSON）。不需要再在每个方法上加 @ResponseBody，使代码更加简洁。通常在开发 REST API 时，都使用 @RestController 来定义控制器。

@Configuration

简介： @Configuration 用于声明一个配置类，由 Spring Framework 提供（org.springframework.context.annotation.Configuration）。配置类可以包含若干个带有 @Bean 注解的方法，以定义 Bean 并交由 Spring 容器管理。@Configuration 本身也是 @Component，因此配置类也会被组件扫描注册。

作用与场景： 在 Java Config 风格的应用中，@Configuration 相当于传统 XML 配置文件。用于定义 Beans、设置依赖注入规则等。Spring Boot 应用的某些自动配置也是以配置类形式存在。提供模块： Spring Context。

使用示例：

```java
@Configuration
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        // 配置数据源 Bean，例如使用 HikariCP 数据源
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        ds.setUsername("root");
        ds.setPassword("123456");
        return ds;
    }

    @Bean
    public UserService userService() {
        // 将 UserService 注册为 Bean，并注入依赖的数据源
        return new UserService(dataSource());
    }
}
```

上述 AppConfig 被 @Configuration 注解标识为配置类。方法 dataSource() 和 userService() 上的 @Bean 注解会使 Spring 将其返回值注册为容器中的 Bean。其中 userService() 方法调用了 dataSource()，Spring 会拦截并确保返回的是容器中单例的 DataSource Bean，而非每次调用重新实例化（即 CGLIB 增强 @Configuration 类确保 Bean 单例行为）。

@Bean

简介： @Bean 注解用于定义一个 Bean。它标注在方法上，表示该方法返回的对象会注册到 Spring 容器中。@Bean 通常配合 @Configuration 使用，由 Spring Context 模块提供。

作用与场景： 当通过 JavaConfig 定义 Bean 时，用 @Bean 替代传统 XML <bean> 声明。例如整合第三方库的 Bean、或需要在创建 Bean 时执行一些自定义逻辑等场景。@Bean 方法可以指定名称（默认是方法名），还支持设置 initMethod（初始化时回调方法）和 destroyMethod（销毁时回调方法）。

使用示例：

```java
@Configuration
public class MyConfig {

    @Bean(name = "customBean", initMethod = "init", destroyMethod = "cleanup")
    public MyComponent customBean() {
        return new MyComponent();
    }
}
```

在上例中，@Bean 注解声明了 customBean 这个 Bean。容器启动时调用 customBean() 方法创建 MyComponent 实例，并以 "customBean" 名称注册。initMethod="init" 表示在 Bean 创建后自动调用其 init() 方法进行初始化；destroyMethod="cleanup" 表示容器销毁该 Bean 时调用其 cleanup() 方法。通过这种方式可以管理 Bean 的生命周期方法（类似于 InitializingBean 和 DisposableBean 接口或 @PostConstruct/@PreDestroy，见后文）。

@ComponentScan

简介： @ComponentScan 用于配置组件扫描路径的注解。由 Spring Context 提供，通常与 @Configuration 一起使用。它的作用是指示 Spring 在指定的包路径下搜索带有组件注解的类，并注册为 Bean。

作用与场景： 默认情况下，Spring Boot 的 @SpringBootApplication 已经隐含指定扫描其所在包及子包。如果需要自定义扫描范围（例如扫描其他包的组件），可以使用 @ComponentScan 注解并提供 basePackages 等属性。普通 Spring 应用（非 Boot）则经常需要在主配置类上显式使用 @ComponentScan 指定根包。提供模块： Spring Context。

使用示例：

```java
@Configuration
@ComponentScan(basePackages = {"com.example.service", "com.example.dao"})
public class AppConfig {
    // ... Bean definitions
}
```

上述配置类通过 @ComponentScan 指定 Spring 将扫描 com.example.service 和 com.example.dao 这两个包及其子包，搜索所有标注了 @Component/@Service/@Controller 等的类并注册。这样可以将应用的组件按照包组织，而由配置集中管理扫描范围。

@Import

简介： @Import 注解用于导入额外的配置类或组件到 Spring 容器。它由 Spring Context 提供，可用在 @Configuration 类上，将一个或多个配置类合并进来。也可以用于引入第三方配置。

作用与场景： 当项目拆分成多个配置类时，可以通过 @Import 将它们组合。例如，将公共配置独立出来，再在主配置中引入。Spring Boot 自动配置内部也大量使用了 @Import 来按条件加载配置类。提供模块： Spring Context。

使用示例：

```java
@Configuration
@Import({SecurityConfig.class, DataConfig.class})
public class MainConfig {
    // 主配置，导入了安全配置和数据配置
}
```

如上，MainConfig 通过 @Import 导入了 SecurityConfig 和 DataConfig 两个配置类。这样这两个配置类中定义的 Bean 同样会加载到容器中，相当于把多个配置模块拼装在一起。相比在 XML 里用 <import>，注解方式更加直观。

注： Spring Boot 提供的许多 @Enable... 注解（例如后文的 @EnableScheduling 等）内部也是通过 @Import 导入相应的配置实现启用功能的。

## 三、依赖注入注解
依赖注入（DI）是 Spring 核心机制之一。以下注解用于在容器中进行 Bean 注入和装配，解决 Bean 间的依赖关系。

@Autowired

简介： @Autowired 是 Spring 提供的自动装配注解（org.springframework.beans.factory.annotation.Autowired），用于按类型自动注入依赖对象。它可作用于字段、setter方法或者构造函数上。由 Spring Context 模块支持。

作用与场景： 标注了 @Autowired 的属性或方法，Spring 会在容器启动时自动寻找匹配的 Bean 注入。其中按类型匹配是默认行为。如果匹配到多个同类型 Bean，则需要结合 @Qualifier 或 @Primary 来消除歧义（见下文）。如果没有找到匹配 Bean，默认会抛出异常。可通过设置 @Autowired(required=false) 来表示找不到 Bean 时跳过注入而不报错。

使用示例：

```java
@Component
public class UserService {
    @Autowired  // 按类型自动装配
    private UserRepository userRepository;

    // 或者构造函数注入
    // @Autowired 
    // public UserService(UserRepository userRepository) { ... }

    public User findUser(Long id) {
        return userRepository.findById(id);
    }
}
```

上例中，UserService 有一个成员 userRepository，使用 @Autowired 标注。容器会自动将类型为 UserRepository 的 Bean 注入进来（假设已有 @Repository 或 @Component 标记的 UserRepository 实现）。开发者可以通过构造器、setter 或字段注入的方式使用 @Autowired。注意： Spring 4.3+ 如果类中只有一个构造器，且需要注入参数，可省略构造函数上的 @Autowired，仍会自动注入。

@Qualifier

简介： @Qualifier 注解与 @Autowired 配合使用，用于按照名称或限定符进行依赖注入匹配。它由 Spring 提供（org.springframework.beans.factory.annotation.Qualifier），可以解决当容器中存在多个同类型 Bean 时的冲突。

作用与场景： 默认按类型注入在有多于一个候选 Bean 时会无法确定注入哪个。例如有两个实现类实现了同一接口，都被注册为 Bean。这种情况下，可以在注入点使用 @Qualifier("beanName") 指定注入哪一个 Bean，或在 Bean 定义处使用 @Component("name") 为 Bean 命名，然后在注入处引用同名限定符。提供模块： Spring Context/Beans。

使用示例：

```java
@Component("mysqlRepo")
public class MySqlUserRepository implements UserRepository { ... }

@Component("oracleRepo")
public class OracleUserRepository implements UserRepository { ... }

@Service
public class UserService {
    @Autowired
    @Qualifier("mysqlRepo")  // 指定注入名称为 mysqlRepo 的实现
    private UserRepository userRepository;
    // ...
}
```

如上，有两个 UserRepository 实现 Bean，分别命名为 “mysqlRepo” 和 “oracleRepo”。在 UserService 中，通过 @Qualifier("mysqlRepo") 指定注入名为 mysqlRepo 的 Bean。这样即使存在多个同类型 Bean，Spring 也能准确地注入所需的依赖。

@Primary

简介： @Primary 注解用于标记一个 Bean 为主要候选者。当按类型注入出现多个 Bean 可选时，标有 @Primary 的 Bean 将优先被注入。它由 Spring 提供（org.springframework.context.annotation.Primary），可作用于类或方法（例如 @Bean 方法）上。

作用与场景： 如果不方便在每个注入点都使用 @Qualifier 指定 Bean，另一种方式是在 Bean 定义处用 @Primary 声明一个首选 Bean。当存在歧义时，容器会选择标记了 @Primary 的 Bean 注入。注意，@Primary 只能有一个，否则仍然无法明确选择。提供模块： Spring Context。

使用示例：

```java
@Configuration
public class RepoConfig {
    @Bean
    @Primary  // 将这个Bean标记为首选
    public UserRepository mysqlUserRepository() {
        return new MySqlUserRepository();
    }

    @Bean
    public UserRepository oracleUserRepository() {
        return new OracleUserRepository();
    }
}

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    // 将自动注入 mysqlUserRepository，因为它被标记为 @Primary
}
```

在上例的配置中，我们定义了两个 UserRepository Bean，其中 MySQL 实现被标记为 @Primary。因此在 UserService 中按类型注入 UserRepository 时，Spring 会注入标记了 @Primary 的 MySQL 实现。@Primary 提供了一个全局默认方案，简化了注入点的选择。

@Resource

简介： @Resource 是来自 JSR-250 规范的注解（Javax/Jakarta Annotation），Spring 对其提供了支持，用于按名称或按类型注入依赖。它通常位于 jakarta.annotation.Resource（Java EE/Jakarta EE）包下。注意： 尽管不在 Spring 包中，Spring 容器能识别并处理它。

作用与场景： @Resource 可以看作功能类似于 @Autowired + @Qualifier 的组合。默认情况下按名称注入：它首先按照属性名或指定的名称在容器中查找 Bean，找不到再按类型匹配。这在某些情况下很有用，例如需要与传统 Java EE 代码兼容时。在 Spring 应用中，也有开发者偏好使用 @Resource 进行依赖注入。提供模块： 需要引入相应的 Jakarta Annotation API，但 Spring Framework 自身支持处理。

使用示例：

```java
@Component("userRepo")
public class UserRepositoryImpl implements UserRepository { ... }

@Service
public class UserService {
    @Resource(name = "userRepo")  // 按名称注入名为"userRepo"的Bean
    private UserRepository userRepo;
    // ...
}
```

这里，UserRepositoryImpl 组件被命名为 "userRepo"。在 UserService 中，通过 @Resource(name = "userRepo") 来注入。如果省略 name 属性，@Resource 默认以属性名 userRepo 作为 Bean 名称查找。与 @Autowired 不同，@Resource 不支持 required=false 属性，但其异常信息可能更直观（若找不到 Bean 则抛出 NoSuchBeanDefinitionException）。值得一提的是，Spring 也支持 JSR-330 的 @Inject（javax.inject.Inject）注解，其语义与 @Autowired 相同，也可用于构造函数注入等。在实际开发中，可根据团队规范选择使用 Spring 原生的 @Autowired 还是标准的 @Resource/@Inject。

@Value

简介： @Value 注解用于将外部化配置中的属性值注入到 Bean 的字段或参数中。它由 Spring 提供（org.springframework.beans.factory.annotation.Value），常用于读取 application.properties/yaml 配置文件或系统环境变量、JNDI等属性。

作用与场景： 当需要在 Bean 中使用配置文件里的值时，可以使用 @Value("${property.name}") 注入。例如数据库连接参数、服务端口号等。还支持设置默认值和 SpEL 表达式。提供模块： Spring Context 环境抽象。

使用示例：

假设 application.properties 有如下内容：

```shell
app.name=MySpringApp
app.version=1.0.0
```

Java 类使用 @Value 注入：

```java
@Component
public class AppInfo {
    @Value("${app.name}")
    private String appName;

    @Value("${app.version:0.0.1}")  // 带默认值，若配置缺失则使用0.0.1
    private String appVersion;

    // ...
}
```

上述 AppInfo 类中，@Value("${app.name}") 将把配置中的 app.name 值注入到 appName 字段。如果对应属性不存在，会启动失败。而 appVersion 字段提供了默认值 0.0.1，当配置文件未设置 app.version 时就会使用默认值。这样，可以灵活地将外部配置与代码解耦，使应用更易于调整参数而无需改动源码。

@Scope

简介： @Scope 注解用于指定 Bean 的作用域，由 Spring 提供（org.springframework.context.annotation.Scope）。默认情况下，Spring 容器中的 Bean 都是单例（singleton）作用域。通过 @Scope 可以定义其他作用域，例如 prototype、request、session 等。

作用与场景： 常见的作用域：

singleton（默认）：容器中仅保持一个实例。

prototype：每次请求 Bean 时都会创建新实例。

Web相关的作用域（需要在 Web 容器环境下使用）：如 request（每个HTTP请求创建）、session（每个会话创建）等。

在需要每次使用新对象的场景（如有状态 Bean），可将 Bean 定义成 prototype；在 Web 应用中某些 Bean 希望随请求或会话存续，可用相应作用域。提供模块： Spring Context。
使用示例：

@Component

```java
@Scope("prototype")
public class Connection {
    public Connection() {
        System.out.println("New Connection created.");
    }
}
```

将 Connection Bean 声明为 prototype，每次获取都会创建新的实例：

```java
@Autowired
private Connection conn1;
@Autowired
private Connection conn2;
```

上面 conn1 和 conn2 将是不同的实例，因为 Connection 定义为 prototype。日志会打印两次 “New Connection created.”。若作用域是 singleton，则只创建一次实例并复用。需要注意，prototype Bean 的生命周期由使用方管理，Spring 只负责创建，不会自动调用其销毁方法。

@Lazy

简介： @Lazy 注解用于将 Bean 的初始化延迟到首次使用时（懒加载）。由 Spring 提供（org.springframework.context.annotation.Lazy），可用于类级别或 @Bean 方法上。

作用与场景： 默认情况下，单例 Bean 在容器启动时就会初始化。如果某些 Bean 的创建比较耗时或在应用运行期间可能不会被用到，可以标记为 @Lazy，这样只有在真正需要时才实例化，减少启动时间和资源消耗。懒加载常用于：例如调试或在单元测试中减少不必要 Bean 创建，或避免循环依赖时暂缓 Bean 的注入初始化。对于 prototype Bean，Spring 始终延迟创建（因为本身就按需创建），@Lazy主要针对单例 Bean。提供模块： Spring Context。

使用示例：

```java
@Service
@Lazy
public class HeavyService {
    public HeavyService() {
        // 构造函数可能进行大量初始化
        System.out.println("HeavyService initialized");
    }
    // ...
}

@Controller
public class DemoController {
    @Autowired
    private HeavyService heavyService; // 被@Lazy标记，不会在容器启动时实例化
    // ...
}
```

如上，HeavyService 使用 @Lazy 注解标记为懒加载单例。启动时不会打印 “HeavyService initialized”。当 DemoController 第一次实际调用 heavyService 的方法或访问它时，Spring 才会创建 HeavyService 实例并注入。这对于优化启动性能和按需加载组件很有帮助。但应谨慎使用懒加载，如果Bean在启动后马上就会用到，则不应延迟初始化，以免首次调用时产生延迟。

## 四、配置属性注解

Spring 提供了将配置文件内容绑定到对象的机制，这类注解帮助管理应用的外部化配置和环境区分。

@ConfigurationProperties

简介： @ConfigurationProperties 用于将一组配置属性映射到一个 Java 类上。由 Spring Boot 提供（org.springframework.boot.context.properties.ConfigurationProperties），通常配合 Bean 使用。通过前缀（prefix）来批量注入配置项到类的属性中。

作用与场景： 当有多项相关配置需要使用时，比起逐个使用 @Value，可以定义一个配置属性类。例如应用配置、数据源配置等。在类上标注 @ConfigurationProperties(prefix="xxx") 后，该类的各属性会根据前缀读取配置文件中的对应项赋值。需要将该类注册为 Bean（可以通过在类上加 @Component 或在配置类中用 @Bean 创建），Spring Boot 会自动将配置绑定到 Bean 实例上。

使用示例：
application.yml:

```shell
app:
  name: MyApp
  apiUrl: https://api.example.com
  pool:
    size: 20
    enableLog: true
```

定义属性绑定类：

@Component  // 确保被扫描注册为Bean
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private String apiUrl;
    private Pool pool;

    // 内部静态类或普通类用于嵌套属性
    public static class Pool {
        private int size;
        private boolean enableLog;
        // getters/setters ...
    }
    // getters/setters ...
}
AI写代码
java
运行

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
将 AppProperties 注入使用：

@RestController
public class AppInfoController {
    @Autowired
    private AppProperties appProperties;

    @GetMapping("/appInfo")
    public AppProperties getAppInfo() {
        // 返回整个配置对象，框架会序列化为JSON
        return appProperties;
    }
}
AI写代码
java
运行

1
2
3
4
5
6
7
8
9
10
11
在这个例子中，@ConfigurationProperties(prefix="app") 使得 YAML 中 app 下的配置自动绑定到 AppProperties Bean。name、apiUrl 会对应赋值，嵌套的 pool.size 和 pool.enableLog 也会注入到 Pool 类中。这样可以方便地管理和校验成组的配置属性。需要注意，绑定类必须有无参构造器，提供标准的 getter/setter。Spring Boot 还支持JSR-303校验注解（如 @Validated）配合 @ConfigurationProperties 对配置进行格式校验。

@EnableConfigurationProperties
简介： @EnableConfigurationProperties 是 Spring Boot 用于启用 @ConfigurationProperties 支持的注解。它通常加在主应用类或配置类上，用来将带有 @ConfigurationProperties 注解的配置POJO注入到容器中。

作用与场景： 在 Spring Boot 2.x 以后，如果配置属性类已经被声明为 Bean（例如加了 @Component），则无需显式使用这个注解。@EnableConfigurationProperties 常用在需要将未被组件扫描的配置属性类纳入 Spring 管理时。例如定义了一个纯 POJO 没有用@Component，则可以在主类上通过此注解指定要启用绑定的配置类列表。提供模块： Spring Boot AutoConfigure。

使用示例：

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class MyApplication {
    // ...
}
AI写代码
java
运行
1
2
3
4
5
上述在主启动类上添加了 @EnableConfigurationProperties(AppProperties.class)，显式指定将 AppProperties 这个被 @ConfigurationProperties 注解的类纳入配置属性绑定并注册为 Bean。这样即使未加 @Component，仍可使用 @Autowired 注入 AppProperties 实例。Spring Boot 自动配置模块会扫描此注解并完成相应的绑定工作。

@Profile
简介： @Profile 注解用于根据**环境（Profile）**加载 Bean。由 Spring 提供（org.springframework.context.annotation.Profile）。可以标注在类或方法（Bean 方法）上，只有在激活的环境与指定 Profile 匹配时，该 Bean 才会注册到容器。

作用与场景： 常用于区别开发、测试、生产环境的配置。例如开发环境使用嵌入式数据库，而生产环境使用正式数据库连接，就可以用 @Profile("dev") 和 @Profile("prod") 注解分别标注不同的配置类或 Bean。在运行应用时通过配置 spring.profiles.active 激活某个 Profile，则对应的 Bean 生效。提供模块： Spring Context 环境管理。

使用示例：

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("dev")
    public DataSource memoryDataSource() {
        // 开发环境使用内存数据库
        return new H2DataSource(...);
    }

    @Bean
    @Profile("prod")
    public DataSource mysqlDataSource() {
        // 生产环境使用MySQL数据源
        return new MySQLDataSource(...);
    }
}
```

当设置 spring.profiles.active=dev 时，应用启动只会创建 memoryDataSource Bean；设置为 prod 时只创建 mysqlDataSource Bean。如果不激活任何 Profile，上述两个 Bean 都不会加载（也可以用 @Profile("default") 指定默认配置）。使用 @Profile 实现了根据环境有条件地注册 Bean，方便一套代码多环境运行。

## 五、Bean 生命周期与作用域注解

Spring 管理的 Bean 具有完整的生命周期，包括初始化和销毁过程。以下注解用于在生命周期特定阶段执行方法，以及控制 Bean 的作用域与加载时机。

@PostConstruct

简介： @PostConstruct 是一个来自 Java 标准（JSR-250）的注解（位于 jakarta.annotation.PostConstruct）。Spring 容器在Bean初始化完依赖注入后，会调用被该注解标记的方法。常用于初始化逻辑。需要注意在 Spring Boot 3+ 中，@PostConstruct 等由 Jakarta 引入，需要相应依赖。

作用与场景： 当我们希望在 Bean 完成依赖注入后自动执行一些初始化代码，可以在 Bean 的方法上加 @PostConstruct。例如设置默认值、开启定时器、检查配置完整性等。在传统 Spring 中，这相当于 <bean init-method="..."> 或实现 InitializingBean 接口的 afterPropertiesSet。提供模块： JSR-250（Javax/Jakarta Annotation），由 Spring 容器支持调用。

使用示例：

```java
@Component
public class CacheManager {
    private Map<String, Object> cache;

    @PostConstruct
    public void init() {
        // 初始化缓存
        cache = new ConcurrentHashMap<>();
        System.out.println("CacheManager initialized");
    }
}
```

当 Spring 创建了 CacheManager Bean 并注入完依赖后，会自动调用其 init() 方法，输出 “CacheManager initialized” 并完成缓存容器初始化。这样开发者无需手动调用初始化逻辑，容器托管完成。这对于单例Bean非常方便。

@PreDestroy
简介： @PreDestroy 同样来自 JSR-250 标准（jakarta.annotation.PreDestroy），Spring 在 Bean 销毁前（容器关闭或 Bean 移除前）调用标注该注解的方法。常用于资源释放、保存状态等操作。

作用与场景： 当应用结束或容器要销毁 Bean 时，希望执行一些清理工作，例如关闭文件流、线程池、数据库连接等，可以在方法上加 @PreDestroy 注解。相当于 XML 配置中的 <bean destroy-method="..."> 或实现 DisposableBean 接口的 destroy 方法。提供模块： JSR-250，由 Spring 容器负责调用。

使用示例：

```java
@Component
public class ConnectionManager {
    private Connection connection;

    @PostConstruct
    public void connect() {
        // 建立数据库连接
        connection = DriverManager.getConnection(...);
    }

    @PreDestroy
    public void disconnect() throws SQLException {
        // 关闭数据库连接
        if(connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection closed.");
        }
    }
}
```

在上例中，ConnectionManager Bean 在初始化时建立数据库连接，在容器销毁时通过 @PreDestroy 标记的 disconnect() 方法关闭连接。Spring 在应用关闭时会调用该方法，确保资源释放。这使得资源管理更加可靠，避免连接泄漏等问题。

@Scope (作用域) – 见上文第三部分
(此处简要说明：) 使用 @Scope 注解可以改变 Bean 的作用域，比如 "singleton"、"prototype" 等。已在依赖注入部分详细介绍其使用。

@Lazy (懒加载) – 见上文第三部分
(此处简要说明：) 使用 @Lazy 可以延迟 Bean 的初始化直至第一次使用。在某些场景下提高启动性能或解决循环依赖。前文已介绍其概念和示例。

## 六、Web 开发注解

Spring MVC 框架提供了大量注解来简化 Web 开发，包括请求映射、参数绑定、响应处理等。这些注解大多位于 org.springframework.web.bind.annotation 包中。

@RequestMapping

简介： @RequestMapping 是最基本的请求映射注解，用于将 HTTP 请求URL路径映射到对应的控制器类或处理方法上。由 Spring Web MVC 提供。可用于类和方法级别。

作用与场景： 在类上标注 @RequestMapping("basePath") 可以为该控制器指定一个基础路径，方法上的 @RequestMapping("subPath") 则在类路径基础上进一步细分。它支持设置请求方法（GET、POST等）、请求参数和请求头等属性，用于更精确地映射请求。例如只处理 GET 请求，或某个请求参数存在时才匹配。Spring MVC 启动时会根据这些注解建立 URL 到方法的映射关系。

使用示例：

```java
@Controller
@RequestMapping("/users")
public class UserController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getUserProfile(@PathVariable Long id, Model model) {
        // 根据id查询用户...
        model.addAttribute("user", userService.findById(id));
        return "profile";
    }

    @RequestMapping(value = "", method = RequestMethod.POST, params = "action=register")
    public String registerUser(UserForm form) {
        // 处理用户注册
        userService.register(form);
        return "redirect:/users";
    }
}
```

UserController 类上的 @RequestMapping("/users") 指定了基础路径“/users”。方法级注解：

getUserProfile: 映射 GET 请求到 “/users/{id}”。使用 method = RequestMethod.GET 限定请求方法为 GET，@PathVariable 获取 URL 中的 {id} 部分。返回视图名 “profile” 供显示用户信息。

registerUser: 映射 POST 请求到 “/users”，并使用 params="action=register" 进一步限定只有请求参数包含 action=register 时才调用此方法。这是区分同一路径不同操作的方式。处理完后重定向到用户列表。

@RequestMapping 非常灵活，其常用属性：

value 或 path：映射的 URL 路径，可以是 Ant 风格模式（如 /users/*）。

method：限定 HTTP 方法，如 RequestMethod.GET 等。

params：指定必须存在的参数或参数值，如 "action=register" 或 "!admin"（必须不包含admin参数）。

headers：指定必须的请求头，如 "Content-Type=application/json"。

@GetMapping / @PostMapping 等

简介： @GetMapping 和 @PostMapping 是 @RequestMapping 的派生注解，专门用于简化映射 GET 和 POST 请求。类似的还有 @PutMapping、@DeleteMapping、@PatchMapping，分别对应 HTTP PUT/DELETE/PATCH 方法。它们由 Spring MVC 提供，从 Spring 4.3 开始引入。

作用与场景： 这些注解相当于 @RequestMapping(method = RequestMethod.X) 的快捷方式，使代码更简洁。尤其在定义 RESTful API 时，常用不同 HTTP 方法表示不同操作，用这些注解能直观体现方法用途。例如 @GetMapping 表示获取资源，@PostMapping 表示创建资源等。

使用示例：

```java
@RestController
@RequestMapping("/items")
public class ItemController {

    @GetMapping("/{id}")
    public Item getItem(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @PostMapping("")
    public Item createItem(@RequestBody Item item) {
        return itemService.save(item);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.delete(id);
    }
}
```

上例中：

@GetMapping("/{id}) 等价于 @RequestMapping(value="/{id}", method = RequestMethod.GET)，用于获取指定ID的 Item。

@PostMapping("") 等价于类路径/items下的 POST 请求（创建新的 Item），请求体通过 @RequestBody 解析为 Item 对象。

@DeleteMapping("/{id}") 处理删除操作。

这些组合注解让控制器方法定义更直观，更符合 RESTful 风格。可以根据需要使用对应的 HTTP方法注解。未提供参数时，@GetMapping 等注解的路径可以直接写在注解括号内（如上 @PostMapping("") 指当前路径）。

@PathVariable

简介： @PathVariable 用于将 URL 路径中的动态部分绑定到方法参数上。由 Spring MVC 提供。常与 @RequestMapping 或 @GetMapping 等一起使用，用于处理RESTful风格的URL。

作用与场景： 当URL中含有变量占位符（如 /users/{id}）时，可通过在方法参数上加 @PathVariable 来获取该占位符的值。可以指定名称匹配占位符，或者不指定名称则根据参数名自动推断。适用于从路径获取资源标识（ID、name等）的场景。

使用示例：

```java
@GetMapping("/orders/{orderId}/items/{itemId}")
public OrderItem getOrderItem(
        @PathVariable("orderId") Long orderId, 
        @PathVariable("itemId") Long itemId) {
    return orderService.findItem(orderId, itemId);
}
```

当收到请求 /orders/123/items/456 时：

orderId 参数会被赋值为 123（Long 类型转换），

itemId 参数赋值为 456。

@PathVariable("orderId") 中指定名称，与 {orderId} 占位符对应。如果方法参数名与占位符名称相同，可以简写为 @PathVariable Long orderId。

通过 @PathVariable，我们无需从 HttpServletRequest 手动解析路径，Spring MVC 自动完成转换和注入，简化了代码。

@RequestParam

简介： @RequestParam 用于绑定 HTTP 请求的查询参数或表单数据到方法参数上。由 Spring MVC 提供。支持为参数设置默认值、是否必需等属性。

作用与场景： 处理 GET 请求的查询字符串参数（URL ? 后的参数）或 POST 表单提交的字段时，可以使用 @RequestParam 获取。例如搜索接口的关键词，分页的页码和大小等。它可以将 String 类型的请求参数转换为所需的目标类型（如 int、boolean），自动完成类型转换和必要的校验。

使用示例：

```java
@GetMapping("/search")
public List<Product searchProducts(
        @RequestParam(name="keyword", required=false, defaultValue="") String keyword,
        @RequestParam(defaultValue="0") int pageIndex,
        @RequestParam(defaultValue="10") int pageSize) {
    return productService.search(keyword, pageIndex, pageSize);
}
```

当请求 /search?keyword=phone&pageIndex=1 到达时：

keyword 参数绑定到方法的 keyword 参数。如果未提供则使用默认值空字符串。

pageIndex 绑定到整型参数，未提供则为默认0。

pageSize 在此请求未提供，因此取默认值10。

@RequestParam 常用属性：

value 或 name：参数名，对应URL中的参数名。

required：是否必须提供，默认 true（不提供会报错）。上例中我们将 keyword 标记为 false 可选。

defaultValue：如果请求未包含该参数则使用默认值（注意即使标记 required=true，有 defaultValue 也不会报错）。

通过 @RequestParam，方法可以直接获得解析后的参数值，无需自己从 request 获取和转换，大大简化控制器代码。

@RequestBody

简介： @RequestBody 用于将 HTTP 请求报文体中的内容转换为 Java 对象并绑定到方法参数上。常用于处理 JSON 或 XML 等请求体。由 Spring MVC 提供。

作用与场景： 在 RESTful API 中，POST/PUT 等请求通常会携带 JSON 格式的数据作为请求体。使用 @RequestBody 注解在方法参数（通常是自定义的 DTO 类）上，Spring MVC 会利用 HttpMessageConverter 将 JSON/XML 等按需转换为对应的对象实例。适用于需要从请求正文获取复杂对象的场景。与之对应，返回值或方法上使用 @ResponseBody（或 @RestController）可将对象序列化为响应。

使用示例：

```java
@PostMapping("/users")
public ResponseEntity<String> addUser(@RequestBody UserDTO userDto) {
    // userDto 已自动绑定了请求JSON的数据
    userService.save(userDto);
    return ResponseEntity.ok("User added successfully");
}
```

假设客户端发送 POST 请求至 /users，请求体为：

```shell
{ "name": "Tom", "email": "tom@example.com" }
```

Spring MVC 会根据 @RequestBody UserDTO userDto：

读取请求体 JSON，

将其转换为 UserDTO 对象（要求有适当的属性和setter）。

然后传递给控制器方法使用。

方法处理后返回成功响应。使用 @RequestBody，开发者无需手动解析 JSON，提高了开发效率并减少出错。

注意： @RequestBody 默认要求请求体存在，否则报错。如果希望在请求体为空时处理为 null，可以设置 required=false。对于 GET 请求一般不使用 @RequestBody（GET没有主体或主体被忽略）。

@ResponseBody

简介： @ResponseBody 注解用于将控制器方法的返回值直接作为 HTTP 响应内容输出，而不是解析为视图名称。由 Spring MVC 提供。可以标注在方法上或（较少见）标注在类上（类上标注相当于对该类所有方法应用此行为）。

作用与场景： @ResponseBody 常用于 AJAX 接口或 RESTful 方法，需要返回 JSON、XML或纯文本等给客户端，而非页面。当方法标注该注解后，Spring 会将返回对象通过合适的 HttpMessageConverter 转换为 JSON/XML 或其他格式写入响应流。例如返回一个对象会自动序列化为 JSON 字符串。@RestController 注解实际上已经包含了 @ResponseBody 效果，所以在使用 @RestController 时无需再标注此注解在每个方法上。

使用示例：

```java
@Controller
public class StatusController {

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "OK";
    }

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> status() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("timestamp", System.currentTimeMillis());
        return info;
    }
}
```

对于上例：

/ping 请求返回纯文本 “OK” 给客户端。

/status 请求返回一个 Map，Spring 会将其转换为 JSON，如：{"status":"UP","timestamp":1638346953000}。

因为使用的是普通的 @Controller 类，所以需要在每个方法上添加 @ResponseBody 来指示直接返回内容。如果改用 @RestController 则可以省略这些注解。@ResponseBody 常用于快速测试接口或者在需要精确控制输出内容时使用。

@CrossOrigin

简介： @CrossOrigin 注解用于配置跨域资源共享 (CORS)。由 Spring Web 提供，可标注在类或方法上。它允许来自不同域名的客户端访问被标注的资源。

作用与场景： 当前端和后端分属不同域（例如前端React开发服务器 http://localhost:3000，后端 http://localhost:8080）时，浏览器会拦截跨域请求。使用 @CrossOrigin 可以在服务端指定允许跨域的来源、方法、头信息等，从而使浏览器允许调用。可以针对整个控制器类统一配置（类上标注）或针对特定方法（方法上标注）配置不同跨域策略。

使用示例：

```java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiController {

    @GetMapping("/data")
    public Data getData() { ... }

    @PostMapping("/submit")
    @CrossOrigin(origins = "http://example.com", methods = RequestMethod.POST)
    public void submitData(@RequestBody Data data) { ... }
}
```

类上标注的 @CrossOrigin(origins = "http://localhost:3000") 表示允许来自 http://localhost:3000 的跨域请求访问该控制器的所有接口。submitData 方法上单独标注了一个不同的 @CrossOrigin，表示对于 /api/submit 接口，允许来自 http://example.com 的 POST 请求跨域访问（不受类上通用配置限制）。@CrossOrigin 还可设置允许的请求头、是否发送凭证等，通过参数如 allowedHeaders, allowCredentials 等配置。使用这个注解，开发者不必在全局Web配置中配置 CorsRegistry，可以就近管理跨域策略。

@ExceptionHandler

简介： @ExceptionHandler 用于在控制器中定义异常处理方法的注解。由 Spring MVC 提供。通过指定要处理的异常类型，当控制器方法抛出该异常时，转而由标注了 @ExceptionHandler 的方法来处理。

作用与场景： 为了避免将异常堆栈暴露给客户端或者在每个控制器方法中编写重复的 try-catch，可以使用 @ExceptionHandler集中处理。例如处理表单校验异常返回友好错误信息、处理全局异常返回统一格式响应等。@ExceptionHandler 通常与 @ControllerAdvice（后述）配合，用于全局异常处理；也可以直接在本控制器内部定义专门的异常处理方法。

使用示例：

```java
@Controller
@RequestMapping("/orders")
public class OrderController {

    @GetMapping("/{id}")
    public String getOrder(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        if(order == null) {
            throw new OrderNotFoundException(id);
        }
        model.addAttribute("order", order);
        return "orderDetail";
    }

    // 本控制器专门处理 OrderNotFoundException
    @ExceptionHandler(OrderNotFoundException.class)
    public String handleNotFound(OrderNotFoundException ex, Model model) {
        model.addAttribute("error", "订单不存在，ID=" + ex.getOrderId());
        return "orderError";
    }
}
```

在 OrderController 中，getOrder 方法如果找不到订单，会抛出自定义的 OrderNotFoundException。下方用 @ExceptionHandler(OrderNotFoundException.class) 标注了 handleNotFound 方法来处理这种异常：当异常抛出后，控制器不会继续执行原流程，而是进入该方法。方法可以接收异常对象，以及 Model 等参数，处理后返回一个视图名 "orderError" 显示错误信息。

通过 @ExceptionHandler，控制器内部的异常处理逻辑与正常业务逻辑解耦，代码清晰且易于维护。

@ControllerAdvice

简介： @ControllerAdvice 是全局控制器增强注解。由 Spring MVC 提供，用于定义一个全局异常处理或全局数据绑定的切面类。标注该注解的类可以包含多个 @ExceptionHandler 方法，用于处理应用所有控制器抛出的异常；也可以包含 @ModelAttribute 或 @InitBinder 方法对所有控制器生效。

作用与场景： 当需要对所有控制器统一处理某些逻辑时，使用 @ControllerAdvice 非常方便。典型用法是结合 @ExceptionHandler 作为全局异常处理器，比如拦截所有 Exception 返回通用错误响应，或分类处理不同异常类型返回不同状态码。提供模块： Spring MVC。

使用示例：

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    // 处理所有异常的fallback
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleException(Exception ex) {
        // 记录日志
        ex.printStackTrace();
        // 返回通用错误响应
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Internal Server Error: " + ex.getMessage());
    }

    // 处理特定异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors()
                                .stream()
                                .map(ObjectError::getDefaultMessage)
                                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }
}
```

GlobalExceptionHandler 类使用 @ControllerAdvice 声明后，Spring 会将其中标注了 @ExceptionHandler 的方法应用到整个应用的控制器：

第一个方法捕获所有未被其它更专门处理的异常，打印栈Trace并返回500错误提示。

第二个方法专门处理参数校验失败异常，提取错误信息列表并返回400状态和错误列表。

此外，可以在 @ControllerAdvice 类中定义 @ModelAttribute 方法，为所有控制器请求添加模型数据（如公共下拉选项），或定义 @InitBinder 方法，注册全局属性编辑器等。@ControllerAdvice 可以通过属性限制只应用于某些包或注解的控制器，但全局异常处理通常都是应用全局的。

通过 @ControllerAdvice，我们实现了 AOP 式的全局控制器逻辑抽取，使各控制器关注自身业务，将通用逻辑集中处理，保持代码整洁。

## 七、数据访问与事务注解

在使用 Spring 管理数据持久化层时，会涉及到 JPA/Hibernate 等注解定义实体，以及 Spring 提供的事务管理注解等。

@Entity

简介： @Entity 是 Java Persistence API (JPA) 的注解（jakarta.persistence.Entity），用于将一个类声明为 JPA 实体。Spring Boot 通常通过 JPA/Hibernate 来操作数据库，因此定义模型时会用到它。@Entity 注解的类对应数据库中的一张表。

作用与场景： 标记为 @Entity 的类将由 JPA 实现（例如 Hibernate）管理，其实例可映射到数据库记录。必须提供主键（用 @Id 标注），可选地用 @Table 指定表名，不指定则默认表名为类名。提供模块： JPA 规范，由 Hibernate 等实现。在 Spring Boot 中，引入 spring-boot-starter-data-jpa 会自动扫描 @Entity 类并创建表结构（结合DDL生成策略）。

使用示例：

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 主键

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;      // 用户名列

    @Column(nullable = false)
    private String password;      // 密码列

    // getters & setters ...
}
```

User 类被 @Entity 注解标记为持久化实体，对应数据库表users（由@Table指定，若不指定默认表名User）。字段上：

id 用 @Id 标识为主键，@GeneratedValue 指定主键生成策略（自增）。

username 用 @Column 细化映射：列名指定为username，长度50，非空且唯一。

password 仅用了 @Column(nullable=false)，列名默认为属性名。

定义好实体后，可以使用 Spring Data JPA 的仓库接口来自动生成常用查询（见下文 @Repository 和 @Query）。Spring Boot 启动时若开启DDL-auto，会根据实体定义自动在数据库创建或更新表结构。

@Table

简介： @Table 是 JPA 注解（jakarta.persistence.Table），配合 @Entity 使用，用于指定实体映射的数据库表信息，如表名、schema、catalog等。

作用与场景： 默认情况下，实体类名即表名。若数据库表名与类名不同，或者需要定义 schema，使用 @Table 注解非常必要。也能定义唯一约束等。提供模块： JPA。

使用示例：

```java
@Entity
@Table(name = "T_USER", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
public class User {
    // ...
}
```

该实体指定映射到 public 模式下的 T_USER 表，并声明 email 列上有唯一约束。@Table 的属性：

name：表名。

schema/catalog：所属 schema 或 catalog 名称。

uniqueConstraints：唯一约束定义。

@Id

简介： @Id 是 JPA 注解（jakarta.persistence.Id），指定实体类的主键字段。每个 @Entity 必须有且只有一个属性使用 @Id 注解。可配合 @GeneratedValue 一起使用定义主键生成策略。

作用与场景： 标记主键后，JPA 会将该字段作为数据库表的主键列。支持基本类型或包装类型，或 java.util.UUID 等。提供模块： JPA。

使用示例：

```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // ... 其他字段
}
```

如上，id 字段为主键，使用自动生成策略。常见的 GenerationType：

IDENTITY：数据库自增字段（MySQL的AUTO_INCREMENT等）。

SEQUENCE：使用数据库序列（需要定义序列，Oracle等DB适用）。

AUTO：让 JPA 自动选择合适策略。

TABLE：使用一个数据库表模拟序列。

@GeneratedValue

简介： @GeneratedValue 是 JPA 注解（jakarta.persistence.GeneratedValue），与 @Id 联用，表示主键的生成方式。可指定策略 strategy 和生成器 generator。

作用与场景： 根据数据库和需求选择主键生成策略。比如 MySQL 用 IDENTITY 让数据库自增，Oracle 用 SEQUENCE 指定序列名称等。提供模块： JPA。

使用示例：

```java
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
private Long id;
```

上例中，使用序列生成器：

@SequenceGenerator 定义名为 “user_seq” 的序列生成器，映射数据库序列 “user_sequence”。

@GeneratedValue 引用该生成器并使用 SEQUENCE 策略。每次插入User时会从序列获取下一个值作为ID。

对于常用的 AUTO 或 IDENTITY 策略，在大多数情况下只需简单标注 @GeneratedValue(strategy = GenerationType.IDENTITY) 等，无需额外生成器配置。

@Column

简介： @Column 是 JPA 注解（jakarta.persistence.Column），用于定义实体字段与数据库表列的映射细节。可以不使用，如果不标注，JPA 默认按属性名映射列名（可能做小写下划线转换，视实现而定）。

作用与场景： @Column 可指定列名、数据类型长度、是否允许NULL、是否唯一等约束。对于日期时间类型，还可指定 columnDefinition 或 Temporal 等，控制SQL类型。提供模块： JPA。

使用示例：

```java
@Column(name = "email", length = 100, nullable = false, unique = true)
private String email;
```

如上，将字段 email 映射为名为 email 的列（其实和默认同名，但明确指出），长度100，非空且唯一。使用 @Column 可以清晰地将实体和数据库字段对应起来。

@Repository – 见上文第二部分

(此处补充：) 在数据访问层，@Repository 标注的接口或类通常与 Spring Data JPA 搭配使用。如一个接口 UserRepository extends JpaRepository<User, Long> 上加 @Repository（实际上 Spring Data JPA 的接口已经隐式有这个语义），Spring 会为其生成实现并交由容器管理。@Repository 除了提供组件扫描和异常转换外，本身没有其他方法属性。

@Transactional

简介： @Transactional 是 Spring 提供的声明式事务管理注解（org.springframework.transaction.annotation.Transactional）。可标注在类或方法上，表示其中的数据库操作应当在一个事务中执行。Spring 将在运行时提供事务支持，如开始、提交或回滚事务。

作用与场景： 数据库操作需要事务保障数据一致性，例如同时更新多张表，要么全部成功要么全部失败。使用 @Transactional 可以在不手动编程式管理事务的情况下，由框架自动处理。典型应用：

Service 层的方法需要原子性，则加上 @Transactional，Spring会在进入方法时开启事务，方法成功返回则提交，如有异常则回滚。

也可加在类上，表示类中所有公有方法都事务管理。

提供模块： Spring ORM/Transaction 模块，需要相应的事务管理器（DataSourceTransactionManager 或 JpaTransactionManager 等）配置。Spring Boot 自动根据数据源配置合适的事务管理器。

使用示例：

```java
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private AuditService auditService;

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // 扣减转出账户余额
        accountRepo.decreaseBalance(fromId, amount);
        // 增加转入账户余额
        accountRepo.increaseBalance(toId, amount);
        // 记录转账流水
        auditService.logTransfer(fromId, toId, amount);
        // 方法结束时，Spring自动提交事务。如发生运行时异常则自动回滚。
    }
}
```

transfer 方法标注了 @Transactional，因此上述三个数据库操作将处于同一个事务中：如果任何一步抛出未经捕获的异常（默认仅RuntimeException和Error会导致回滚，可通过 rollbackFor 属性更改回滚规则），所有已执行的数据库更新都会回滚，保持数据一致性。如果全部成功，则提交事务，将更新真正持久化。事务传播行为和隔离级别等也可以通过注解属性配置，例如 @Transactional(propagation=Propagation.REQUIRES_NEW) 开启新事务，@Transactional(isolation=Isolation.SERIALIZABLE) 设置高隔离级别等，视业务需求而定。

注意： 使用 @Transactional 时，需要确保启用了 Spring 的事务支持（见下文 @EnableTransactionManagement），Spring Boot 会自动在有数据源时启用事务管理。所以在 Boot 场景下通常不需要额外配置即可使用。

@JsonFormat

简介： @JsonFormat 是 Jackson 提供的序列化/反序列化格式化注解（com.fasterxml.jackson.annotation.JsonFormat）。可作用在字段、方法（getter / setter）或类型上，用于自定义日期-时间、数字、布尔等属性在 JSON ←→ Java 转换时的形态、时区与本地化设置。

作用与场景：

日期时间格式化：将 Date / LocalDateTime 等类型格式化为固定字符串（例如 yyyy-MM-dd HH:mm:ss）并指定时区，避免前后端默认时区不一致导致时间偏移。

数字 / 布尔形态控制：可把布尔值序列化成 0/1，或把 Instant、LocalDateTime 转成数值时间戳（shape = NUMBER）等。

与 Bean Validation 协同：在 DTO 中同时配合 @DateTimeFormat / 校验注解，可保持前后端格式完全一致。

优先级：字段级 @JsonFormat 会覆盖 ObjectMapper 的全局日期格式配置，适用于单独字段需要特殊格式的场景。

使用示例：

```java
@Data
public class OrderDTO {

    private Long id;

    // 1. 指定日期-时间格式 + 时区
    @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd HH:mm:ss",
                timezone = "GMT+8")
    private LocalDateTime createdAt;

    // 2. 以秒级时间戳输出
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant eventTime;

    // 3. 布尔值改为 0 / 1
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Boolean paid;
}
```

@Getter

简介： @Getter 是 Lombok 提供的生成器注解（lombok.Getter）。编译期自动为被注解的类或字段生成 public getter 方法，省去手写样板代码。

作用与场景：

简化 POJO / DTO 编写：一个注解即可为所有字段（或单独字段）生成读取方法，保持类体简洁。

与框架集成：Spring / Jackson / Hibernate 等框架依赖 getter 读取属性时可直接使用 Lombok 生成的方法。

使用示例：

```java
@Getter          // 为所有字段生成 getter
public class UserVO {

    private Long id;

    @Getter(AccessLevel.NONE) // 不生成该字段的 getter
    private String password;

    // 也可在字段级别加 @Getter 仅生成单个方法
}
```

依赖：开发环境需引入 lombok 依赖，并在 IDE 中安装 Lombok 插件或开启 Annotation Processing。

@Setter

简介： @Setter 同样由 Lombok 提供（lombok.Setter），自动为类或字段生成 public setter 方法。

作用与场景：

可变对象赋值：在需要修改字段值、或框架反射注入时使用。

粒度控制：可通过 AccessLevel 设置方法可见性（如 @Setter(AccessLevel.PROTECTED)），或仅在特定字段上使用，避免暴露全部可写接口。

使用示例：

```java
@Getter
@Setter               // 为所有字段生成 setter
public class ProductVO {

    private Long id;

    @Setter(AccessLevel.PRIVATE) // 仅类内部可修改
    private BigDecimal price;

    // price 的 setter 为 private，其余字段的 setter 为 public
}
```

@ToString

简介： @ToString 亦由 Lombok 提供（lombok.ToString）。在编译期生成 toString() 方法，自动拼接字段名和值，支持包含/排除特定字段、隐藏敏感信息等。

作用与场景：

调试与日志：快速输出对象内容而不必手写 toString()。

避免敏感字段泄漏：可用 @ToString.Exclude 排除字段，或在注解上设置 callSuper = true 包含父类字段。

链式注解：常与 @Getter/@Setter/@EqualsAndHashCode 等一起使用，快速生成完整数据类。

使用示例：

```java
@Getter
@Setter
@ToString(exclude = "password")          // 排除 password
public class AccountVO {

    private String username;

    @ToString.Exclude
    private String password;             // 或者字段级排除

    private LocalDateTime lastLogin;
}

/*
输出示例：
AccountVO(username=admin, lastLogin=2025-05-10T20:30:00)
*/

```
在生产日志中输出对象时务必排除敏感信息；@ToString 支持 exclude 数组或字段级 @ToString.Exclude 精细控制。

@EnableTransactionManagement

简介： @EnableTransactionManagement 注解用于开启 Spring 对 事务注解（如 @Transactional）的支持。由 Spring 提供（org.springframework.transaction.annotation.EnableTransactionManagement）。一般加在配置类上。

作用与场景： 在非 Spring Boot 场景下，使用 @Transactional 前通常需要在 Java 配置类上加此注解或在 XML 中配置 <tx:annotation-driven/> 来启用事务 AOP。它会注册事务管理相关的后置处理器，检测 @Transactional 并在运行时生成代理。提供模块： Spring Tx。

使用示例：

```java
@Configuration
@EnableTransactionManagement
public class TxConfig {
    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource); // 配置事务管理器
    }
}
```

上例通过 @EnableTransactionManagement 启用了注解驱动的事务管理，并显式声明了数据源事务管理器 Bean。在 Spring Boot 中，如果引入了 spring-boot-starter-jdbc 或 JPA，会自动配置 DataSourceTransactionManager 或 JpaTransactionManager，且默认启用事务支持，无需手动添加该注解（Boot 会自动应用）。但在需要更细粒度控制事务行为时，了解此注解的作用仍然重要。

@Query

简介： @Query 注解由 Spring Data JPA 提供（org.springframework.data.jpa.repository.Query），用于在 Repository 方法上定义自定义的 JPQL或原生SQL 查询。

作用与场景： 虽然 Spring Data JPA 可以通过解析方法名自动生成查询，但是复杂或特殊的查询可以用 @Query 手工编写JPQL语句。还可以通过设置 nativeQuery=true 使用原生SQL。当自动生成无法满足需求，或为了性能使用数据库特定查询时，用此注解非常有用。提供模块： Spring Data JPA。

使用示例：

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // 使用JPQL查询
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);

    // 使用原生SQL查询
    @Query(value = "SELECT * FROM users u WHERE u.status = :status LIMIT :limit", nativeQuery = true)
    List<User> findTopByStatus(@Param("status") int status, @Param("limit") int limit);
}
```

在上面的仓库接口中：

findByEmail 方法使用 JPQL 查询根据邮箱获取用户。?1 表示第一个参数。

findTopByStatus 方法使用原生SQL查询指定状态的用户若干条，使用命名参数 :status 和 :limit。需要搭配 @Param 注解绑定参数值。

Spring Data JPA 在运行时会解析这些注解并生成相应实现代码执行查询。@Query 能大大提升查询的灵活性，但要注意JPQL语句的正确性以及原生SQL的可移植性。

## 八、面向切面编程（AOP）注解

Spring AOP 提供了强大的面向切面编程功能，可以通过注解定义横切关注点，如日志记录、性能监控、权限检查等。主要的 AOP 注解包括：

@Aspect

简介： @Aspect 注解用于将一个类声明为切面类。由 AspectJ 提供（Spring AOP 使用 AspectJ 注解风格）。标记为 @Aspect 的类内部可以定义切点和通知，实现 AOP 功能。需要配合 Spring AOP 使用。

作用与场景： 切面类汇总了横切逻辑，例如日志切面、安全切面等。一个切面类里通常包含若干通知方法（@Before、@After等）和切点定义（@Pointcut）。Spring 在运行时会根据切面定义生成代理对象，将横切逻辑织入目标对象的方法调用。提供模块： org.aspectj.lang.annotation.Aspect（需要 spring-boot-starter-aop 或 Spring AOP 模块依赖）。

使用示例：

```java
@Aspect
@Component  // 切面本身也需注册为Bean
public class LoggingAspect {

    // 切点定义：匹配 service 包下所有类的公共方法
    @Pointcut("execution(public * com.example.service..*(..))")
    public void serviceMethods() {}

    // 前置通知：在满足切点的方法执行之前执行
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Entering: " + joinPoint.getSignature());
    }

    // 后置通知：无论方法正常或异常结束都会执行
    @After("serviceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("Exiting: " + joinPoint.getSignature());
    }
}
```

上面定义了一个日志切面类 LoggingAspect：

使用 @Aspect 标记为切面类，结合 @Component 使其成为 Spring Bean。

serviceMethods() 方法使用 @Pointcut 定义了一个切点，表达式 "execution(public * com.example.service..*(..))" 表示匹配 com.example.service 包及子包下所有类的任意公共方法执行。切点方法本身没有实现，仅作标识。

logBefore 方法使用 @Before("serviceMethods()") 注解，表示在执行匹配 serviceMethods() 切点的任意方法之前，先执行该通知。通过 JoinPoint 参数可以获取被调用方法的信息。

logAfter 方法使用 @After("serviceMethods()")，表示目标方法执行完成后（无论成功与否）执行。输出方法签名的退出日志。

为使上述 AOP 生效，需要启用 Spring 对 AspectJ 切面的支持。Spring Boot 自动配置已经启用了 AOP（如果引入了 starter-aop，默认会开启 @AspectJ 支持），在非 Boot 环境可能需要在配置类上添加 @EnableAspectJAutoProxy 注解来开启代理机制。如果未启用，@Aspect 注解不会生效。总之，@Aspect 注解的类定义了 AOP 的横切逻辑，是实现日志、事务、权限等横切关注点的关键。

@Pointcut

简介： @Pointcut 用于定义一个切点表达式，以命名方式重用切点。由 AspectJ 注解提供。通常是一个签名为 void 且无实现的方法注解，用于给切点命名。

作用与场景： 切点定义了哪些连接点(Join Point)需要织入切面逻辑。通过@Pointcut可以将复杂的切点表达式进行抽象，方便在多个通知上引用，避免重复书写表达式。提供模块： AspectJ。

使用示例：

```java
@Aspect
@Component
public class SecurityAspect {

    // 切点：controller 包下的所有含 @GetMapping 注解的方法
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void allGetEndpoints() {}
    
    @Before("allGetEndpoints()")
    public void checkAuthentication() {
        // 执行权限验证逻辑
        if (!SecurityContext.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
    }
}
```

此处，SecurityAspect 定义了一个切点 allGetEndpoints()，通过 @Pointcut 注解的表达式指定：凡是标注了@RestController的类中，标注了@GetMapping的方法，都是切点。然后在 @Before("allGetEndpoints()") 通知中引用这个切点，执行权限检查。如果当前用户未认证则抛出异常阻止方法执行。

切点表达式语言十分丰富，可以基于执行方法签名（execution）、注解（@annotation, within 等）、this/target对象等进行匹配组合。通过适当的切点定义，可以灵活地选择哪些点应用横切逻辑。

@Before
简介： @Before 定义一个前置通知（Advice），即在目标方法执行之前执行的切面方法。它由 AspectJ 提供（org.aspectj.lang.annotation.Before）。需要在 @Aspect 切面类中使用，注解的值是一个切点表达式或命名切点。

作用与场景： 前置通知通常用于在方法调用前执行一些检查、日志或准备工作。例如权限验证（见上例）、记录方法开始日志、在方法执行前设置环境（如初始化 ThreadLocal）等。在目标方法之前执行，不影响目标方法参数和执行结果，只作附加操作。

使用示例： 参考前述 LoggingAspect 和 SecurityAspect 中的 @Before 用法。在 LoggingAspect 中：

```java
@Before("serviceMethods()")
public void logBefore(JoinPoint joinPoint) { ... }
```

这表示在匹配 serviceMethods() 切点的每个目标方法执行前调用 logBefore 方法。JoinPoint 参数可以获取方法签名、参数等信息，用于日志输出。

@Before 通知不能阻止目标方法执行（除非抛出异常）。如果在通知中抛异常，目标方法将不会执行且异常向上抛出。因此一般前置通知不故意抛异常（权限验证除外，验证失败则通过异常中断执行）。

@After

简介： @After 定义一个后置通知，即在目标方法执行结束后执行的切面方法，无论目标方法正常返回还是抛出异常都会执行（类似 finally block）。由 AspectJ 提供。

作用与场景： 常用于清理资源、记录方法结束日志等操作。例如在方法完成后记录执行时间（需要结合开始时间），或确保某些线程上下文数据被清除。不关心方法的结果，只要离开方法就执行通知。

使用示例： 参考 LoggingAspect 中：

```java
@After("serviceMethods()")
public void logAfter(JoinPoint joinPoint) { ... }
```

不管 serviceMethods() 匹配的方法成功或异常返回，都会执行 logAfter。可以用它来打印离开方法的日志。

如果需要根据方法是否抛异常做区分，可以使用 @AfterReturning 或 @AfterThrowing（详见下文）。@After 通常用来放置最终执行的操作，比如解锁资源，不管成功失败都要执行的。

@AfterReturning

简介： @AfterReturning 定义一个返回通知，在目标方法成功返回后执行（未抛异常）。可以捕获返回值。由 AspectJ 提供。

作用与场景： 当需要获取目标方法的返回结果进行处理时，可使用 @AfterReturning。例如日志中记录返回值，或者根据返回值做后续动作。若目标方法抛异常则不会执行此通知。注解可指定 returning 属性绑定返回值。

使用示例：

```java
@AfterReturning(pointcut = "execution(* com.example.service.OrderService.placeOrder(..))", returning = "result")
public void logOrderResult(Object result) {
    System.out.println("Order placed result: " + result);
}
```

此通知针对 OrderService.placeOrder 方法执行，如果其正常完成，则将返回值绑定到 result 形参并打印日志。比如 result 可能是订单ID或确认对象。若 placeOrder 抛异常，则该通知不执行。

@AfterThrowing

简介： @AfterThrowing 定义一个异常通知，在目标方法抛出指定异常后执行。由 AspectJ 提供。可捕获异常对象。

作用与场景： 用于统一处理或记录目标方法抛出的异常，例如记录错误日志、发送告警等。可以指定 throwing 属性将异常绑定到参数。只在有未捕获异常时执行，正常返回不执行。

使用示例：

```java
@AfterThrowing(pointcut = "execution(* com.example..*.*(..))", throwing = "ex")
public void logException(Exception ex) {
    System.err.println("Exception in method: " + ex.getMessage());
}
```

该切面方法会在应用中任何未捕获的异常抛出时执行，打印异常信息。ex 参数即目标方法抛出的异常对象（可以指定具体异常类型过滤，如 throwing="ex" throwing=RuntimeException.class）。

通过 @AfterThrowing 可以集中处理异常情况，例如对特定异常进行额外处理（如事务补偿或资源回收），或统一记录。

@Around

简介： @Around 定义一个环绕通知，它包裹了目标方法的执行。由 AspectJ 提供。环绕通知最为强大，可以在方法执行前后都进行处理，并可决定是否、如何执行目标方法（通过 ProceedingJoinPoint 调用)。

作用与场景： 可以用来计算执行时间、控制方法执行（比如实现自定义注解的权限校验并决定是否调用原方法）、修改方法的返回值甚至拦截异常。@Around 通知需要显式调用 proceed() 才会执行目标方法，如果不调用则目标方法不执行。这让我们有机会在调用前后插入逻辑，甚至改变执行流程。

使用示例：

```java
@Around("execution(* com.example.service.*.*(..))")
public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    Object result;
    try {
        result = pjp.proceed(); // 执行目标方法
    } finally {
        long end = System.currentTimeMillis();
        System.out.println(pjp.getSignature() + " executed in " + (end - start) + "ms");
    }
    return result;
}
```

这个环绕通知为 service 包下所有方法计算执行时间：

在调用目标方法前记录开始时间。

通过 pjp.proceed() 执行目标方法，将返回结果保存。

方法执行后计算时间差并打印。

将目标方法的返回值返回，保证调用流程正常进行。

如果目标方法抛异常，proceed() 会抛出异常到外层（如上例没有 catch，finally执行后异常继续抛出）。也可以在环绕通知中捕获异常并处理，甚至返回替代结果，从而吞掉异常（视业务需要谨慎处理）。

@Around 通知还可以实现诸如自定义注解拦截功能，例如检查方法上是否有某注解，有则执行特殊逻辑等，灵活性最高。

@EnableAspectJAutoProxy

简介： @EnableAspectJAutoProxy 是 Spring 提供的注解（org.springframework.context.annotation.EnableAspectJAutoProxy），用于开启基于注解的 AOP 支撑。它会启用 AspectJ 注解的自动代理机制。

作用与场景： 在纯 Spring 配置中，需要在配置类上添加此注解才能使前述 @Aspect 切面生效（等同于 XML 配置中的 <aop:aspectj-autoproxy/>）。Spring Boot 在引入 AOP 起步依赖时，默认已经启用了该功能 ，因此多数情况下无需显式添加。但了解这个注解有助于在需要调整 AOP 代理选项时使用（比如 proxyTargetClass=true 强制使用CGLIB代理）。

使用示例：

```java
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig {
    // 切面类Bean或通过@Component扫描切面类
}
```

这将在容器中搜索 @Aspect 注解的类，自动创建代理。proxyTargetClass=true 强制使用类代理而不是接口代理。默认为 false，即如果有实现接口则用JDK动态代理。这一点在需要代理没有接口的类或者希望统一使用CGLIB代理时可以设置。

总结而言，Spring AOP 的注解允许我们以声明方式实现横切逻辑，将日志、性能监控、安全检查等与业务代码分离，提升模块化和可维护性。

## 九、异步与定时任务注解

Spring 提供了对多线程异步任务和定时调度的支持，只需通过注解即可开启这些功能。

@Async

简介： @Async 注解用于将某个方法声明为异步执行。由 Spring 提供（org.springframework.scheduling.annotation.Async）。标注该注解的方法会在调用时由 Spring 异步执行，而不是同步阻塞当前线程。通常需要配合 @EnableAsync 一起使用。

作用与场景： 当某些操作不需要同步完成、可以在后台线程执行时，用 @Async 能简化并发编程。例如发送邮件、短信通知，执行耗时的计算而不阻塞主流程，或并行调用多个外部服务等。Spring 会基于 TaskExecutor （默认SimpleAsyncTaskExecutor）调度异步方法。方法可以返回 void 或 Future/CompletableFuture 以便获取结果。

使用示例：

```java
@Service
public class NotificationService {

    @Async
    public void sendEmail(String to, String content) {
        // 模拟发送邮件的耗时操作
        try {
            Thread.sleep(5000);
            System.out.println("Email sent to " + to);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

```java
@SpringBootApplication
@EnableAsync  // 开启异步支持
public class MyApp { ... }
```

在 NotificationService 中，sendEmail 标注了 @Async，因此当它被调用时，Spring 会从线程池中拿出一个线程来异步执行该方法，原调用方线程不必等待5秒。需要在应用主类或配置类上添加 @EnableAsync 以激活异步处理能力。使用默认配置时，Spring 会使用一个简单线程池执行，也可以通过定义 Executor Bean 并加上 @Async("beanName") 来指定特定线程池。

调用异步方法示例：

```java
@RestController
public class OrderController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/order")
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        orderService.process(order);
        // 异步发送通知
        notificationService.sendEmail(order.getEmail(), "Your order is placed.");
        return ResponseEntity.ok("Order received");
    }
}
```

placeOrder 方法调用了 sendEmail，因为后者是异步的，所以 placeOrder 在触发邮件发送后会立即返回响应，邮件发送在另一个线程进行，不影响接口响应时间。异步调用的异常需特别处理，可以使用 AsyncUncaughtExceptionHandler 或返回 Future 在调用方监听。总之，@Async 大大方便了将任务异步化。

@EnableAsync

简介： @EnableAsync 是 Spring 提供的注解（org.springframework.scheduling.annotation.EnableAsync），用于开启对 @Async 注解的处理。加在配置类或主启动类上，激活 Spring 异步方法执行的能力。

作用与场景： 类似于 @EnableAspectJAutoProxy 之于 AOP，对于异步也需要显式开启。Spring Boot 自动配置通常不会主动开启异步，所以需要开发者添加此注解。提供模块： Spring Context 调度任务支持。

使用示例： 见上方，将 @EnableAsync 放在 @SpringBootApplication 类上或独立的配置类上均可。

启用后，Spring 容器会搜索应用中标注了 @Async 的 Bean 方法，并通过代理的方式调用线程池执行它们。默认的执行器可以通过定义 TaskExecutor Bean 来覆盖。如：

```java
@Bean(name = "taskExecutor")
public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.initialize();
    return executor;
}
```

定义名为 “taskExecutor” 的执行器后，Spring @Async 会自动使用它（因为默认执行器名称就是 taskExecutor）。也可在 @Async 注解的参数中指定一个自定义执行器 Bean 名称。

@Scheduled

简介： @Scheduled 注解用于将方法标记为定时任务。由 Spring 提供（org.springframework.scheduling.annotation.Scheduled）。可以通过 cron 表达式或固定间隔等配置何时运行该方法。需要配合 @EnableScheduling 开启调度支持。

作用与场景： 当需要周期性地执行某段代码时，例如每隔一段时间检查库存，每天夜间生成报表等，可以使用 @Scheduled 注解而不需要借助外部的调度框架。Spring 容器会在后台线程按指定计划调用这些方法。支持多种调度配置：

cron 表达式：通过 Cron 定义复杂时间计划。

固定速率 fixedRate：以上一次开始时间为基准，间隔固定毫秒执行。

固定延迟 fixedDelay：以上一次完成时间为基准，延迟固定毫秒执行。

可选属性如 initialDelay 等设置启动延迟。

使用示例：

```java
@Component
public class ReportTask {

    @Scheduled(cron = "0 0 2 * * ?")
    public void generateDailyReport() {
        // 每天凌晨2点生成报告
        System.out.println("Generating daily report at " + LocalDate.now());
        // ... 报表生成逻辑
    }

    @Scheduled(fixedRate = 60000)
    public void checkSystemHealth() {
        // 每隔60秒检查系统健康
        System.out.println("Health check at " + Instant.now());
        // ... 健康检查逻辑
    }
}
```

这里，ReportTask 类中：

generateDailyReport() 使用 cron="0 0 2 * * ?"，表示每天2:00执行（Cron表达式：“秒 分 时 日 月 周”，“?”表示不指定周几）。这个方法将在主线程之外的调度线程按计划调用。

checkSystemHealth() 使用 fixedRate=60000 表示每60秒执行一次，不论上次执行多长时间，都按固定频率触发。若上次尚未执行完，新周期到了默认不会并发执行（调度器会等待），但可以通过配置 Scheduler 实现并发。

为了使 @Scheduled 生效，需要在配置类上添加 @EnableScheduling（见下文）。Spring Boot 应用通常也需要手动加这一注解。定时任务执行由 Spring 的 TaskScheduler（默认SingleThreadScheduler）驱动，可能需要注意任务不应长时间阻塞，否则会影响后续任务调度。可自定义线程池 TaskScheduler 以提高并发度。

@EnableScheduling

简介： @EnableScheduling 注解用于开启 Spring 对定时任务调度的支持（org.springframework.scheduling.annotation.EnableScheduling）。添加在配置类或主类上。

作用与场景： 没有这个注解，@Scheduled 等注解不会被识别处理。启用后，Spring 容器会启动一个调度线程池，定时调用标记的方法。提供模块： Spring Context 定时任务支持。

使用示例：

```java
@SpringBootApplication
@EnableScheduling
public class Application { ... }
```

将 @EnableScheduling 放在启动类上即可激活调度机制。然后所有 @Scheduled 注解的方法都会按照配置的计划执行。Spring Boot 不会自动开启定时任务支持，因为有的应用可能不需要调度功能，所以必须显式声明。

如果需要自定义调度器，可以定义 Scheduler Bean 或 TaskScheduler Bean。默认使用单线程执行所有定时任务，若多个任务需要并行，建议提供 ThreadPoolTaskScheduler Bean。

通过 @Async 和 @Scheduled 这组注解，Spring 让并发编程和任务调度变得非常容易，不再需要显式创建线程或使用外部调度平台，在应用内部即可完成这些逻辑。

## 十、缓存注解

Spring 提供了便捷的缓存机制，通过注解即可实现方法级缓存，把方法调用结果存储起来，避免重复计算或数据库查询。

@EnableCaching

简介： @EnableCaching 注解用于开启 Spring 对缓存注解的支持（org.springframework.cache.annotation.EnableCaching）。通常加在配置类或主类上，激活缓存管理能力。

作用与场景： 开启后，Spring 会自动配置一个缓存管理器（可基于内存、EhCache、Redis等，取决于依赖配置），并扫描应用中的缓存注解（如 @Cacheable 等），在运行时用AOP代理实现缓存逻辑。提供模块： Spring Cache。

使用示例：

```java
@SpringBootApplication
@EnableCaching
public class Application { ... }
```

这样，Spring Boot 就会自动根据 classpath 中的缓存库选择缓存实现（如有 spring-boot-starter-cache 默认用 ConcurrentMapCache 简单实现；如果引入 spring-boot-starter-redis 则使用 RedisCacheManager 等）。确保在使用缓存注解前调用了 @EnableCaching，否则缓存注解不会生效。

@Cacheable

简介： @Cacheable 用于标记方法，将其返回结果缓存起来。由 Spring 提供（org.springframework.cache.annotation.Cacheable）。再次调用该方法时，如果传入参数相同且缓存中有结果，则直接返回缓存而不执行方法。

作用与场景： 典型用于读取操作缓存，例如从数据库查询数据后缓存，下次查询相同参数可以直接返回缓存值，提高性能。@Cacheable 需要指定缓存的名称（cacheName）以及缓存键（key），可以是 SpEL 表达式。默认键根据所有参数自动生成（需参数可哈希）。提供模块： Spring Cache。

使用示例：

```java
@Service
public class ProductService {
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        // 假设这里有复杂计算或慢速数据库查询
        System.out.println("Loading product " + id + " from DB...");
        return productRepository.findById(id).orElse(null);
    }
}
```

配置 @Cacheable(value="products", key="#id")：

value 指定缓存的名字叫 “products”（类似分类，可对应不同缓存存储）。

key="#id" 表示使用方法参数 id 作为缓存键。

第一次调用 getProductById(1L) 时，会打印“Loading product 1 from DB…”并查询数据库，然后结果缓存到名为 “products” 的缓存中，键为 1。第二次调用 getProductById(1L)，Spring 检测到相同键在缓存中有值，直接返回缓存，不执行方法主体，因此不会再打印那条日志。

@Cacheable 还有一些属性：

condition：满足条件时才缓存或才查缓存，如 condition="#id > 10".

unless：方法执行完后判断，如果满足条件则不缓存结果，如 unless="#result == null".

sync：是否在并发场景下同步，只让一个线程计算缓存，其它等待。

@CachePut

简介： @CachePut 注解用于将方法返回值直接放入缓存，但与 @Cacheable 不同的是，它始终执行方法，不会跳过。它通常用于更新缓存数据。由 Spring 提供。

作用与场景： 当执行了修改操作后，希望缓存与数据库同步更新，可使用 @CachePut 标记修改方法，使其结果及时写入缓存。这样后续再读缓存可以得到最新值。提供模块： Spring Cache。

使用示例：

```java
@CachePut(value = "products", key = "#product.id")
public Product updateProduct(Product product) {
    System.out.println("Updating product " + product.getId());
    return productRepository.save(product);
}
```

每次调用 updateProduct，都会执行保存操作并返回更新后的 Product。@CachePut 注解确保无论如何，这个返回的 Product 对象会以其 id 作为键，存入 “products” 缓存（覆盖旧值）。因此，即便之前通过 @Cacheable 缓存过旧的 Product 数据，这里也会更新缓存，使之与数据库一致。值得注意的是，@CachePut 不会影响方法执行（总会执行方法），它只是在返回后把结果写缓存。

@CacheEvict

简介： @CacheEvict 注解用于移除缓存。标记在方法上，可以在方法执行前或后将指定 key 或整个缓存清除。由 Spring 提供。

作用与场景： 当数据被删除或改变且缓存不再有效时，需要清除缓存。例如删除一个记录后，需要把对应缓存删掉；批量更新后，可以选择清空整个缓存。@CacheEvict 支持指定 key 或设置 allEntries=true 清空整个命名缓存。提供模块： Spring Cache。

使用示例：

```java
@CacheEvict(value = "products", key = "#id")
public void deleteProduct(Long id) {
    System.out.println("Deleting product " + id);
    productRepository.deleteById(id);
}
```

调用 deleteProduct(5) 时，@CacheEvict 会使缓存 “products” 中键为5的条目无效（删除）。默认地，它在方法成功执行后清除缓存。如果希望无论方法是否成功都清除，可设定 beforeInvocation=true，那将在方法进入时就清除（防止方法抛异常缓存未清）。allEntries=true 则可以不顾键，直接清空整个缓存空间。例如：

```java
@CacheEvict(value = "products", allEntries = true)
public void refreshAllProducts() { ... }
```

会清除 “products” 缓存的所有条目。

通过 @CacheEvict 与 @CachePut，我们可以维护缓存与底层数据的一致性。

注意： 使用缓存注解要求配置正确的 CacheManager 和缓存存储。Spring Boot 默认使用简单的内存缓存（ConcurrentMapCacheManager）用于开发测试。生产中常结合 Redis、Ehcache等实现，更换实现通常无需改动注解，只需配置 CacheManager Bean。

## 十一、事件监听注解

Spring 提供了应用内事件发布-订阅机制，支持松耦合的消息通信。通过注解可以方便地订阅事件。

@EventListener

简介： @EventListener 是 Spring 4.2+ 引入的注解（org.springframework.context.event.EventListener），用于将任意 Spring Bean 的方法标识为事件监听器。当有匹配的事件发布时（实现 ApplicationEvent 或自定义事件对象），该方法会被调用。相比实现 ApplicationListener 接口，注解方式更简洁。

作用与场景： 在应用内，不同组件之间可以通过发布事件进行解耦通讯。例如用户注册后发布一个 UserRegisteredEvent，由其他监听器监听来发送欢迎邮件或统计指标。使用 @EventListener，方法签名定义了它感兴趣的事件类型，也可以通过 condition 属性设置过滤条件（比如只处理某字段满足条件的事件）。提供模块： Spring Context 事件机制。

使用示例：

```java
// 定义事件类（可以继承 ApplicationEvent 也可以是普通类）
public class UserRegisteredEvent /* extends ApplicationEvent */ {
    private final User user;
    public UserRegisteredEvent(User user) { this.user = user; }
    public User getUser() { return user; }
}

// 发布事件的组件
@Service
public class UserService {
    @Autowired 
    private ApplicationEventPublisher eventPublisher;

    public void register(User user) {
        // ... 保存用户逻辑
        // 发布事件
        eventPublisher.publishEvent(new UserRegisteredEvent(user));
    }
}

// 监听事件的组件
@Component
public class WelcomeEmailListener {

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        User newUser = event.getUser();
        // 发送欢迎邮件
        System.out.println("Sending welcome email to " + newUser.getEmail());
        // ... 实际发送邮件逻辑
    }
}
```

流程说明：

UserService.register 方法在新用户注册成功后，通过 ApplicationEventPublisher 发布了一个 UserRegisteredEvent 事件。Spring Boot 默认通过 ApplicationEventPublisher 将事件发布到应用上下文。

WelcomeEmailListener 是一个普通组件（被 @Component 扫描）。其中方法 handleUserRegistered 标注了 @EventListener，且参数是 UserRegisteredEvent。这表明它订阅此类型事件。当事件被发布时，Spring 检测到存在匹配的监听方法，便调用该方法并将事件对象传入。

监听方法运行，完成发送欢迎邮件的功能。

这样，发送邮件的逻辑和用户服务逻辑完全解耦，只通过事件联系。如果以后不需要发送邮件，只需移除监听器，而不影响用户注册流程。另外，可以很容易地新增其它监听，如统计注册用户数的监听器，而不需要修改 UserService。

@EventListener 还支持 condition 属性使用 SpEL 表达式进行事件内容过滤。例如：

@EventListener(condition = "#event.user.vip")

public void handleVipUserRegistered(UserRegisteredEvent event) { ... }

仅当用户是VIP时才处理。这种细粒度控制进一步增强了事件机制的灵活性。

需要注意，默认事件监听器在发布线程内同步执行。如果想异步处理事件，可以结合 @Async 注解，将监听方法异步执行（前提是已启用 @EnableAsync）。或者使用 Spring 5 提供的 ApplicationEventMulticaster 配置为异步模式。

ApplicationListener 接口 （替代方案）

说明： 在 @EventListener 出现之前，Spring 使用实现 ApplicationListener<E> 接口的方式来监听事件。虽然这不是注解，但与事件注解结合使用时值得一提。任何 Spring Bean 实现了 ApplicationListener<MyEvent>，当 MyEvent 发布时其 onApplicationEvent 方法会被调用。自 Spring 4.2 起推荐使用 @EventListener 代替，更加简洁。

使用示例：

```java
@Component
public class StatsListener implements ApplicationListener<UserRegisteredEvent> {
    @Override
    public void onApplicationEvent(UserRegisteredEvent event) {
        // 统计用户注册
        metrics.increment("user.register.count");
    }
}
```

这个监听器无须注解，Spring根据泛型自动注册。但相比注解方式，它需要一个独立的类实现接口，不如 @EventListener 可以直接用任意方法方便。而且一个类只能实现对一种事件的监听，要监听多种事件需要写多个类或使用一些if判断，不如注解灵活。因此现在开发中更多使用 @EventListener。

综上，Spring 的事件模型通过发布订阅实现了应用内部的解耦协作。@EventListener 极大降低了使用门槛，使得监听事件就像写普通方法一样便捷。配合异步能力，还能实现类似消息队列的效果，用于不太关键的异步通知等场景。

## 十二、测试相关注解

Spring 为了方便编写测试，特别是针对 Spring MVC 或 JPA等组件的测试，提供了一系列注解来简化配置测试上下文。

@SpringBootTest

简介： @SpringBootTest 是 Spring Boot 测试框架提供的注解（org.springframework.boot.test.context.SpringBootTest），用于在测试类上，表示启动一个完整的 Spring Boot 应用上下文进行集成测试。

作用与场景： 标注此注解的测试类在运行时会通过 Spring Boot 引导启动应用（除非配置特定属性使其部分引导），这意味着：

会扫描并创建所有 Bean，加载完整应用上下文。

提供对 Bean 的依赖注入支持，使测试类可以直接 @Autowired 需要的 Bean 进行集成测试。

它常用于需要测试多个层级协同工作的场景，例如验证服务层和仓库层交互，或者整个请求流程。

使用示例：

```java
@SpringBootTest
class ApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void testUserRegistration() {
        User user = new User("alice");
        userService.register(user);
        // 验证注册结果，比如检查数据库或事件发布效果
        assertNotNull(user.getId());
    }
}
```

这个测试类使用 @SpringBootTest，则测试运行时 Spring Boot 会启动应用上下文并注入 UserService Bean，测试方法里可以直接调用业务代码进行验证。@SpringBootTest 还可以指定启动端口、环境等参数，或通过 properties 覆盖配置，比如：

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

用于启动嵌入式服务器在随机端口，以进行 Web 集成测试。

@WebMvcTest

简介： @WebMvcTest 是用于测试 Spring MVC 控制器的注解（org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest）。它会启动一个精简的 Spring MVC 环境，只包含Web相关的Bean，如@Controller、@RestController等，以及MVC配置，而不加载整个应用上下文。

作用与场景： 主要用于单元测试控制器。默认只扫描 @Controller 和 @RestController 等 Web 层组件，以及必要的配置（如 Jackson 转换、Validator）。不会加载服务层、仓库层Bean，除非通过配置指定。这样测试运行速度快且聚焦于MVC层逻辑。常配合 MockMvc （Spring提供的模拟MVC请求的工具）使用进行控制器的请求/响应测试。

使用示例：

```java
@WebMvcTest(controllers = UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;  // 将UserService模拟

    @Test
    void testGetUser() throws Exception {
        User user = new User(1L, "Bob");
        // 定义当userService.findById(1)被调用时返回user对象
        given(userService.findById(1L)).willReturn(user);

        mockMvc.perform(get("/users/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Bob"));
    }
}
```

此测试类标注 @WebMvcTest(UserController.class)：

Spring Boot 会仅启动与 UserController 相关的 MVC 组件，如 UserController 本身，MVC配置，序列化组件等。

UserService 因为不是@Controller组件，不会自动加载。因此使用了 @MockBean 注解（见后）创建一个模拟的 UserService Bean，将其注入到 UserController 中，避免涉及真实的服务层逻辑。

测试使用 MockMvc 发起GET请求到 /users/1，并断言返回状态200和返回JSON中的name字段为"Bob"。由于我们预先通过 given(userService.findById(1L)) 指定了模拟行为，所以控制器调用userService时会得到我们构造的user对象。

通过这种方式，不需要启动整个应用，也不需要真实数据库等，就能测试控制器映射、参数解析、返回结果等。@WebMvcTest 提供了对Spring MVC各方面的支持（如可以自动配置MockMvc）。

@DataJpaTest

简介： @DataJpaTest 是用于测试 JPA 持久层的注解（org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest）。它会启动一个只包含 JPA 相关组件的 Spring 应用上下文，例如实体管理器、Spring Data JPA 仓库、嵌入式数据库等。

作用与场景： 主要用于单元测试 Repository 层。它会：

配置嵌入式内存数据库（如H2）用于测试，除非明确指定其他DataSource。

扫描 @Entity 实体和 Spring Data Repository 接口并注册。

不加载 web、安全等其他非持久层Bean，以加快测试速度。

默认使用事务包装每个测试，并在结束时回滚，保证测试隔离。

使用示例：

```java
@DataJpaTest
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        // 准备测试数据
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test");
        userRepository.save(user);
        // 执行查询方法
        User found = userRepository.findByEmail("test@example.com");
        assertEquals("Test", found.getName());
    }
}
```

这里 @DataJpaTest 将自动配置一个内存数据库并初始化 JPA 环境。UserRepository 接口（假设继承自 JpaRepository）会被加载为Bean。测试中先保存一个用户，然后调用仓库自定义方法 findByEmail 验证结果。由于测试结束时事务会回滚，插入的测试数据不会污染下一个测试或实际数据库。

@DataJpaTest 同样可以与 @MockBean 配合如果需要模拟一些非JPA的Bean，但是通常持久层测试不需要。也可以通过 properties 指定连接真实数据库进行集成测试，不过大多数情况下，使用内存数据库足以测试Repository逻辑。

@MockBean

简介： @MockBean 是 Spring Boot Test 提供的注解（org.springframework.boot.test.mock.mockito.MockBean），用于在 Spring 测试上下文中添加一个由 Mockito 模拟的Bean，并替换掉容器中原本该类型的Bean（如果有）。常用于在 Web层或服务层测试中，模拟依赖的Bean行为。

作用与场景： 当测试的目标Bean有依赖，而我们不想测试依赖的真实逻辑（可能复杂或不确定），就可以用 @MockBean 来提供一个Mockito创建的模拟对象给容器。这比手工使用 Mockito.mock 然后手动注入更方便，因为 Spring 会自动把这个模拟Bean注入到需要它的地方。典型应用是在 @WebMvcTest 中模拟服务层Bean，在 @SpringBootTest 中模拟外部系统客户端Bean等。

使用示例：

```java
@MockBean
private WeatherService weatherService;
```

将 WeatherService 接口模拟为一个 Bean 注入容器。如果应用上下文本来有一个该类型的Bean（比如真实的实现），会被模拟对象替换。这使得我们可以用 given(weatherService.getTodayWeather())... 等来预设行为。这个注解可以用在测试类的字段上（如上）、也可以用在测试方法内参数上。

具体的用法在前面的 @WebMvcTest 示例已经体现。再比如，在一个服务层测试中：

```java
@SpringBootTest
class OrderServiceTests {

    @Autowired
    private OrderService orderService;
    @MockBean
    private PaymentClient paymentClient;  // 模拟外部支付服务客户端

    @Test
    void testOrderPayment() {
        Order order = new Order(...);
        // 假定调用外部支付返回成功结果
        given(paymentClient.pay(order)).willReturn(new PaymentResult(true));

        boolean result = orderService.processPayment(order);
        assertTrue(result);
        // 验证内部行为，如订单状态更新
        // ...
    }
}
```

这里 OrderService 依赖 PaymentClient，但我们不想真的调用外部服务，于是用 @MockBean 模拟它并规定返回 PaymentResult 成功。这样 OrderService.processPayment 执行时实际上用的是假的 PaymentClient，但可以测试 OrderService 自身的逻辑是否正确处理了成功结果。注意： @MockBean 底层使用 Mockito，所以需要确保引入了 Mockito 相关依赖。

其他测试注解：

@SpringBootTest 和 @WebMvcTest, @DataJpaTest 是 Spring Boot Test 提供的测试切面注解，此外还有类似 @WebFluxTest（测试WebFlux控制器）、@JdbcTest（测试JDBC）、@JsonTest（测试JSON序列化）等，根据需要使用。

JUnit本身的注解如 @Test, @BeforeEach, @AfterEach 等也在测试中大量使用（如上示例已经用到 @Test）。虽然不属于Spring范畴，但也算开发中常用的注解之一。

通过这些测试注解，开发者可以方便地编写隔离的测试用例。例如只启动Web层或持久层进行单元测试，大大提高测试执行速度和定位问题的精准度。Spring Boot 自动配置为测试裁剪了上下文，避免加载无关bean，使测试既保持类似生产环境的行为，又能高效运行。

## 十三、安全相关注解

Spring Security 框架提供了方法级安全控制的注解和配置注解，方便对控制器或服务方法实施权限检查。此外还有开启安全的配置注解等。

@EnableWebSecurity

简介： @EnableWebSecurity 是用于开启 Spring Security Web 安全支持的注解（org.springframework.security.config.annotation.web.configuration.EnableWebSecurity）。通常加在一个继承 WebSecurityConfigurerAdapter（Spring Security 5.7 之前）的配置类上，或者加在包含 SecurityFilterChain Bean 的配置类上。它启用了 Spring Security 的过滤器链。

作用与场景： 使用 Spring Security 时，需要此注解来加载 Web 安全配置，使应用受 Spring Security 管理。提供模块： Spring Security Config。

使用示例：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and().formLogin();
    }
}
```

上述经典用法在 Spring Security 5.7 之前通过继承 WebSecurityConfigurerAdapter 来配置。@EnableWebSecurity 注解开启安全功能。Spring Boot 自动配置也会在引入 starter-security 时添加该注解，因此有时无需手动添加；但当我们提供自定义安全配置类时，一般会注明此注解。注意： Spring Security 5.7 开始，官方更推荐不继承类而是声明 SecurityFilterChain Bean 配合 @EnableWebSecurity 使用，但注解作用相同。

@EnableGlobalMethodSecurity （已过时） / @EnableMethodSecurity

简介： @EnableGlobalMethodSecurity 用于开启方法级安全注解的支持（如 @PreAuthorize, @Secured）。这是 Spring Security 旧版本使用的注解，位于 org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity。它已经在 Spring Security 6 被替换为新的 @EnableMethodSecurity（@EnableGlobalMethodSecurity 标记为已弃用）。

作用与场景： 加在 Security 配置类上，启用对 @PreAuthorize, @PostAuthorize, @Secured, @RolesAllowed 注解的识别。可以通过其属性启用各类注解，如 prePostEnabled=true 支持 Pre/PostAuthorize，securedEnabled=true 支持@Secured，jsr250Enabled=true 支持@RolesAllowed 等。提供模块： Spring Security Config。

使用示例（旧）：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // ...
}
```

这将开启 @PreAuthorize/@PostAuthorize（因为 prePostEnabled=true） 和 @Secured 注解（因为 securedEnabled=true）。在 Security 6 中，等价的做法是：

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig { ... }
```

@EnableMethodSecurity 默认就启用了 Pre/Post，所以可以不用显式 prePostEnabled，secured和jsr250需要明确true。

总而言之，在当前的 Spring Boot 3 / Security 6 环境中，使用 @EnableMethodSecurity 取代 @EnableGlobalMethodSecurity 来开启方法安全注解支持。

@PreAuthorize

简介： @PreAuthorize 是 Spring Security 的方法级权限注解（org.springframework.security.access.prepost.PreAuthorize）。它可以用在方法或类上，在方法调用之前基于给定的表达式进行权限验证。需要启用了全局方法安全后（如上），此注解才会生效。

作用与场景： @PreAuthorize 可以检查当前认证用户是否具备某权限或角色，或者满足SpEL表达式定义的任意条件，然后才允许方法执行。常用于服务层或控制层方法，保护敏感操作。例如只有ADMIN角色能调用删除用户方法，或者只有资源拥有者才能访问资源等。它比 @Secured 更强大，因为可以使用Spring EL编写复杂的逻辑。

使用示例：

```java
@Service
public class AccountService {

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(Long accountId) {
        // 只有ADMIN角色用户才能执行
        accountRepository.deleteById(accountId);
    }

    @PreAuthorize("#user.name == authentication.name or hasAuthority('SCOPE_profile')")
    public Profile getUserProfile(User user) {
        // 用户本人或具有profile权限的可以查看
        return profileRepository.findByUser(user);
    }
}
```

deleteAccount 方法上，@PreAuthorize("hasRole('ADMIN')") 限制只有具有ROLE_ADMIN的用户可以调用，否则会被拒绝（抛出 AccessDeniedException）。

getUserProfile 方法上，使用了表达式：#user.name == authentication.name or hasAuthority('SCOPE_profile')。authentication.name 代表当前登录用户名。如果传入的 user.name 等于当前用户名（即查询自己的资料），或当前主体具有 SCOPE_profile 权限（例如 OAuth2 scope），则允许访问。否则拒绝。可以看到PreAuthorize能够引用方法参数（通过#参数名）和安全上下文信息（authentication对象）进行复杂判断。

@PreAuthorize 非常灵活，也支持调用自定义权限评估方法等。但要注意权限表达式越复杂可能越难维护，需要在安全和可读性之间平衡。Spring Security官方推荐使用PreAuthorize胜过Secured，因为其表达能力更强。

@Secured

简介： @Secured 是较早的 Spring Security 提供的简单方法安全注解（org.springframework.security.access.annotation.Secured）。它指定一组允许的角色，调用该方法的用户必须具备其中一个角色才行。需要在全局方法安全配置中启用 securedEnabled=true 才生效。

作用与场景： 适用于简单的基于角色的访问控制。如果系统的授权模型主要基于角色，可以使用 @Secured("ROLE_X") 来保护方法。相对于 PreAuthorize，它不支持复杂表达式，只能指定角色列表。提供模块： Spring Security（需要 @EnableMethodSecurity(securedEnabled=true) 或旧的相应配置）。

使用示例：

```java
@Secured("ROLE_ADMIN")
public void createUser(User user) { ... }

@Secured({"ROLE_USER", "ROLE_ADMIN"})
public Data getData() { ... }
```

createUser 方法要求调用者必须拥有 ROLE_ADMIN 角色。

getData 方法允许 ROLE_USER 或 ROLE_ADMIN 拥有者访问（逻辑是OR的关系）。

如果不满足要求，Spring Security同样会抛出访问拒绝异常。@Secured 内部实际上也是通过 AOP 拦截，与 @PreAuthorize 实现机制类似，但因为其功能有限，Spring官方更推荐使用Pre/PostAuthorize。

需要留意的是，@Secured 注解中的字符串需要包含完整的角色前缀（如默认前缀是 “ROLE_”)。如上必须写 “ROLE_ADMIN” 而不是 “ADMIN”，除非通过配置修改了前缀策略。

@RolesAllowed

简介： @RolesAllowed 来自 JSR-250（jakarta.annotation.security.RolesAllowed），功能与 @Secured 类似，也是指定允许访问方法的角色列表。Spring Security 支持它，需要 jsr250Enabled=true。它和 Secured的区别主要在注解来源不同。

作用与场景： 可以作为 @Secured 的替代，用标准注解来声明角色限制。在Spring环境下两者效果一样。提供模块： JSR-250，Spring Security需启用支持。

使用示例：

```java
@RolesAllowed("ADMIN")
public void updateSettings(Settings settings) { ... }
```

这里假设已将角色前缀配置成无"ROLE_"前缀或 SecurityConfigurer里做了处理，否则 Spring Security会把 “ADMIN” 当作角色名直接匹配 GrantedAuthority “ADMIN”。一般Secured和RolesAllowed不能混用不同前缀，否则容易出错。

综上，Spring Security提供的这些注解允许我们无需在方法内部手动检查权限，而由框架自动在调用前进行验证，符合条件才执行。需要注意：

要在配置类上开启相应支持（使用 @EnableMethodSecurity 或旧版 @EnableGlobalMethodSecurity）。

@PreAuthorize/@PostAuthorize 功能最强，但稍复杂，@Secured/@RolesAllowed简单直接，但只能基于角色判断。

这类注解只检查Spring Security的上下文，对于未经过滤器链保护的方法调用（比如同类中自调用方法不会触发注解检查，或者在无Security环境下）就不起作用。这是常见陷阱——所以带有安全注解的方法最好不要在内部直接调用，否则绕过了切面检查。

## 十四、其他常用注解

除了上述类别，Spring & Spring Boot 中还有一些常用但未分类到的注解，例如：

参数校验相关： Spring 对 JSR 303 Bean Validation 的支持，让我们可以在模型上使用如 @NotNull, @Size, @Valid 等注解。其中在 Controller 方法参数上使用 @Valid 可触发校验，并结合 @ExceptionHandler 或 @ControllerAdvice 统一处理校验结果。

JSON 序列化控制： 像 @JsonInclude（来自 Jackson）可以注解类或属性，控制JSON序列化包含规则，例如 @JsonInclude(JsonInclude.Include.NON_NULL) 表示忽略null值字段。这在返回REST数据时很有用。

条件装配注解： Spring Boot 提供了一系列 @ConditionalOn... 注解用于自动配置（如 @ConditionalOnProperty, @ConditionalOnClass, @ConditionalOnMissingBean 等）来有条件地装配Bean。这些主要在开发Spring Boot自动配置模块时使用，在应用层较少直接用到，但理解它们有助于明白Boot的装配机制。

以上罗列的注解涵盖了Spring核心开发中最常用的部分。从应用启动配置、Bean装配，到Web层开发、数据持久化、AOP、异步、缓存、事件、测试、安全，各个方面都有简洁的注解支持。掌握它们的用法能显著提高开发效率，减少样板代码，让我们更多关注业务逻辑实现。

总结： Spring & Spring Boot 常用注解极大地便利了开发，它们遵循“约定优于配置”的理念，通过简单的注解声明即可完成以前繁琐的XML配置或手动编码工作。在使用时要注意启用相应功能的开关（如异步、事务、缓存等），理解注解背后机制（如AOP代理、运行时处理）以避免踩坑。熟练运用上述注解，能覆盖大部分日常开发场景，实现优雅、高效和可维护的Spring应用。

