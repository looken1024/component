#include <iostream>

#include <thread_pool.h>

// 使用示例
int main() {
    // 第一阶段：线程池初始化
    // 创建包含4个工作线程的线程池对象
    // 注意：线程数通常设置为硬件支持的并发数（std::thread::hardware_concurrency()）
    ThreadPool pool(4);

    // 第二阶段：任务提交
    // 使用future容器保存异步任务的结果
    // std::future<int> 表示未来将获取的int类型结果
    std::vector<std::future<int>> results;

    // 提交8个任务到线程池
    for(int i = 0; i < 8; ++i) {
        // 通过线程池的enqueue方法提交任务
        results.emplace_back(
            // 使用lambda表达式定义任务逻辑
            // 值捕获循环变量i
            pool.enqueue([i] {
                // 模拟耗时操作（实际应用中可能是复杂计算或IO操作）
                std::this_thread::sleep_for(std::chrono::seconds(1));
                // 返回计算结果（平方值）
                return i * i;
            }) // enqueue返回该任务的future对象
        );
    }

    // 第三阶段：结果处理
    // 遍历所有future对象获取结果
    // 使用右值引用(auto&&)实现完美转发
    for(auto && result : results) {
        // 调用future.get()获取计算结果
        // 注意：此操作会阻塞直到对应任务完成
        std::cout << result.get() << ' ';
    }

    std::cout << std::endl;

    return 0;
}
