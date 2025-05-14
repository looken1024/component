import redis.clients.jedis.Jedis;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisDistributedLock {
    private static final String LOCK_KEY_PREFIX = "distributed_lock:";
    private final Jedis jedis;
    private final String lockKey;
    private final String lockValue;
    private final int lockTimeout; // 锁的过期时间（秒）

    public RedisDistributedLock(Jedis jedis, String resourceName, int lockTimeout) {
        this.jedis = jedis;
        this.lockKey = LOCK_KEY_PREFIX + resourceName;
        this.lockValue = UUID.randomUUID().toString(); // 唯一标识锁的持有者
        this.lockTimeout = lockTimeout;
    }

    /**
     * 尝试获取锁
     * @param acquireTimeout 获取锁的超时时间（毫秒）
     * @return 是否成功获取锁
     */
    public boolean acquire(long acquireTimeout) {
        long endTime = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < endTime) {
            // 使用 SETNX 尝试获取锁
            Long result = jedis.setnx(lockKey, lockValue);
            if (result == 1) { // SETNX 成功，获取锁
                // 设置锁的过期时间（避免死锁）
                jedis.expire(lockKey, lockTimeout);
                return true;
            }

            // 检查锁是否已过期（可选优化，处理极端情况）
            String currentValue = jedis.get(lockKey);
            if (currentValue != null && isLockExpired(currentValue)) {
                // 锁已过期，尝试使用 GETSET 原子性地替换锁值
                String oldValue = jedis.getSet(lockKey, lockValue);
                if (oldValue.equals(currentValue)) {
                    jedis.expire(lockKey, lockTimeout);
                    return true;
                }
            }

            // 短暂等待后重试
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /**
     * 释放锁
     * @return 是否成功释放锁
     */
    public boolean release() {
        // 使用 Lua 脚本保证原子性：先检查锁值是否匹配，再删除锁
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "   return redis.call('del', KEYS[1]) " +
                        "else " +
                        "   return 0 " +
                        "end";
        Object result = jedis.eval(script, 1, lockKey, lockValue);
        return result.equals(1L);
    }

    /**
     * 检查锁是否已过期（锁值为时间戳时有效）
     */
    private boolean isLockExpired(String lockValue) {
        try {
            long expireTime = Long.parseLong(lockValue);
            return System.currentTimeMillis() > expireTime;
        } catch (NumberFormatException e) {
            return false; // 非时间戳格式的锁值，默认未过期
        }
    }

    /**
     * 自动释放锁的语法糖（try-with-resources）
     */
    public void close() {
        release();
    }
}
