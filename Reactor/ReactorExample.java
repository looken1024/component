package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

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

        /*
            为什么打印出来的是10 4 2 6 8
            flatMap 的并行特性
            flatMap 会将每个元素映射为一个内部 Publisher（这里是 Mono），并并行处理这些内部流。
            默认情况下，flatMap 使用 Schedulers.parallel() 调度器，这会导致元素的处理顺序不确定。
            delayElement 的调度器
            当你调用 Mono.just(i * 2).delayElement(Duration.ofMillis(100)) 时：
            如果不指定调度器，delayElement 会默认使用 Schedulers.parallel()。
            并行调度器会在后台线程执行延迟，多个元素的延迟可能重叠或交错，导致输出顺序混乱。

            1 使用 concatMap 保证顺序
            concatMap 会按顺序处理并连接每个内部流，确保输出顺序与输入一致：
            Flux.range(1, 5)
                .concatMap(i -> Mono.just(i * 2).delayElement(Duration.ofMillis(100)))
                .subscribe(System.out::println); // 输出: 2 4 6 8 10

            2 指定 Schedulers.single() 调度器
            强制所有延迟在同一个线程中执行：
            Flux.range(1, 5)
                .flatMap(i -> Mono.just(i * 2)
                    .delayElement(Duration.ofMillis(100), Schedulers.single())
                )
                .subscribe(System.out::println); // 输出: 2 4 6 8 10

            3 使用 flatMapSequential
            保持输出顺序与输入一致，但允许并行处理内部流：
            Flux.range(1, 5)
                .flatMapSequential(i -> Mono.just(i * 2).delayElement(Duration.ofMillis(100)))
                .subscribe(System.out::println); // 输出: 2 4 6 8 10
         */

        // 确保异步操作完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
