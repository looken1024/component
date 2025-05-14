import redis.clients.jedis.Jedis;

public class TestRedis {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("62.234.20.62", 8888);
        jedis.auth("**************");
        String resourceName = "order_123";
        int lockTimeout = 30; // 锁超时时间（秒）
        long acquireTimeout = 5000; // 获取锁的超时时间（毫秒）

        RedisDistributedLock lock = new RedisDistributedLock(jedis, resourceName, lockTimeout);

        try {
            // 尝试获取锁
            boolean acquired = lock.acquire(acquireTimeout);
            if (acquired) {
                // 获得锁，执行业务逻辑
                System.out.println("成功获取锁，执行关键操作...");
                // 模拟业务操作
                Thread.sleep(2000);
            } else {
                // 获取锁失败
                System.out.println("获取锁超时，稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放锁
            lock.release();
            jedis.close();
        }
    }
}
