# Java Reactor

## 1. 什么是 Reactor？
Reactor 是基于 Reactive Streams 规范的 Java 实现，是 Spring WebFlux 的基础。它提供了 Mono（0-1 个元素）和 Flux（0-N 个元素）两种异步数据流类型，并支持丰富的操作符。

## 2. 核心概念
Publisher：发布者（如 Mono/Flux）
Subscriber：订阅者（通过 subscribe() 触发数据流）
Operator：操作符（如 map、filter、flatMap 等）
Backpressure：背压（订阅者控制数据接收速率的机制）

## 3. 基本使用示例
以下是 Reactor 的简单使用示例：

```java
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactorExample {
    public static void main(String[] args) {
        // 创建 Flux
        Flux<String> flux = Flux.just("apple", "banana", "cherry");
        
        // 转换并过滤
        flux.map(String::toUpperCase)
            .filter(fruit -> fruit.startsWith("B"))
            .subscribe(System.out::println); // 输出: BANANA
        
        // 创建 Mono
        Mono<String> mono = Mono.just("hello");
        
        // 组合操作
        mono.map(s -> s + " world")
            .subscribe(System.out::println); // 输出: hello world
        
        // 异步操作示例
        Flux.range(1, 5)
            .flatMap(i -> Mono.just(i * 2).delayElement(Duration.ofMillis(100)))
            .subscribe(System.out::println);
        
        // 确保异步操作完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

## 4. 常用操作符
创建操作：just()、fromIterable()、range()
转换操作：map()、flatMap()、transform()
过滤操作：filter()、take()、skip()
组合操作：concat()、merge()、zip()
错误处理：onErrorReturn()、retry()、doOnError()

## 5. 背压处理
通过 subscribe() 方法可以传入 Subscriber 实现背压控制：

```java
flux.subscribe(new BaseSubscriber<String>() {
    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        request(1); // 首次请求 1 个元素
    }
    
    @Override
    protected void hookOnNext(String value) {
        // 处理元素
        System.out.println("Processing: " + value);
        request(1); // 处理完后再请求 1 个
    }
});

## 6. 测试 Reactor 代码
使用 StepVerifier 进行测试：

```java
import reactor.test.StepVerifier;

public class ReactorTest {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.range(1, 3)
            .map(i -> i * 2);
        
        StepVerifier.create(flux)
            .expectNext(2, 4, 6)
            .verifyComplete();
    }
}
```

## 7. 资源
官方文档：https://projectreactor.io/docs
参考书籍：《Reactive Programming with Reactor》
