# 简介
一般来说，对数据进行加锁时，程序先通过acquire获取锁来对数据进行排他访问，然后对数据进行一些列的操作，最后需要释放锁。Redis 本身用 watch命令进行了加锁，这个锁是乐观锁。使用 watch命令对于频繁访问的键会引起性能的问题。

# redis命令介绍

- SETNX（SET if Not eXists）当且仅当 key 不存在，将 key 的值设为 value ，并返回1；若给定的 key 已经存在，则 SETNX 不做任何动作，并返回0。
- SETEX 设置超时时间
- GET  返回 key 所关联的字符串值，如果 key 不存在那么返回特殊值 nil 。
- DEL 删除给定的一个或多个 key ,不存在的 key 会被忽略。

实现思路

由于redis的setnx命令天生就适合用来实现锁的功能，这个命令只有在键不存在的情况下为键设置值。获取锁之后，其他程序再设置值就会失败，即获取不到锁。获取锁失败。只需不断的尝试获取锁，直到成功获取锁，或者到设置的超时时间为止。

另外为了防治死锁，即某个程序获取锁之后，程序出错，没有释放，其他程序无法获取锁，从而导致整个分布式系统无法获取锁而导致一系列问题，甚至导致系统无法正常运行。这时需要给锁设置一个超时时间，即setex命令，锁超时后，从而其它程序就可以获取锁了。

# 实现

依赖：

	<!-- 开启web -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	<!-- redis -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-redis</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>


创建一个锁类
	 
	public class Lock {
	    private String name;
	    private String value;
	
	    public Lock(String name, String value) {
	        this.name = name;
	        this.value = value;
	    }
	
	    public String getName() {
	        return name;
	    }
	
	    public String getValue() {
	        return value;
	    }	
	}

创建分布式锁
 	
		
	@Component
	public class DistributedLockHandler {
	
	    private static final Logger logger            = LoggerFactory.getLogger(DistributedLockHandler.class);
	    private final static long   LOCK_EXPIRE       = 30 * 1000L;                                           //单个业务持有锁的时间30s，防止死锁
	    private final static long   LOCK_TRY_INTERVAL = 30L;                                                  //默认30ms尝试一次
	    private final static long   LOCK_TRY_TIMEOUT  = 20 * 1000L;                                           //默认尝试20s
	
	    @Autowired
	    private StringRedisTemplate template;
	
	    /**
	     * 尝试获取全局锁
	     * @param lock 锁的名称
	     * @return true 获取成功，false获取失败
	     */
	    public boolean tryLock(Lock lock) {
	        return getLock(lock, LOCK_TRY_TIMEOUT, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
	    }
	
	    /**
	     * 尝试获取全局锁
	     *
	     * @param lock    锁的名称
	     * @param timeout 获取超时时间 单位ms
	     * @return true 获取成功，false获取失败
	     */
	    public boolean tryLock(Lock lock, long timeout) {
	        return getLock(lock, timeout, LOCK_TRY_INTERVAL, LOCK_EXPIRE);
	    }
	
	    /**
	     * 尝试获取全局锁
	     *
	     * @param lock        锁的名称
	     * @param timeout     获取锁的超时时间
	     * @param tryInterval 多少毫秒尝试获取一次
	     * @return true 获取成功，false获取失败
	     */
	    public boolean tryLock(Lock lock, long timeout, long tryInterval) {
	        return getLock(lock, timeout, tryInterval, LOCK_EXPIRE);
	    }
	
	    /**
	     * 尝试获取全局锁
	     *
	     * @param lock           锁的名称
	     * @param timeout        获取锁的超时时间
	     * @param tryInterval    多少毫秒尝试获取一次
	     * @param lockExpireTime 锁的过期
	     * @return true 获取成功，false获取失败
	     */
	    public boolean tryLock(Lock lock, long timeout, long tryInterval, long lockExpireTime) {
	        return getLock(lock, timeout, tryInterval, lockExpireTime);
	    }
	
	    /**
	     * 操作redis获取全局锁
	     *
	     * @param lock           锁的名称
	     * @param timeout        获取的超时时间
	     * @param tryInterval    多少ms尝试一次
	     * @param lockExpireTime 获取成功后锁的过期时间
	     * @return true 获取成功，false获取失败
	     */
	    public boolean getLock(Lock lock, long timeout, long tryInterval, long lockExpireTime) {
	        try {
	            if (StringUtils.isEmpty(lock.getName()) || StringUtils.isEmpty(lock.getValue())) {
	                return false;
	            }
	            long startTime = System.currentTimeMillis();
	            do {
	                if (!template.hasKey(lock.getName())) {
	                    ValueOperations<String, String> ops = template.opsForValue();
	                    ops.set(lock.getName(), lock.getValue(), lockExpireTime, TimeUnit.MILLISECONDS);
	                    return true;
	                } else {//存在锁
	                    logger.debug("lock is exist!！！");
	                }
	                if (System.currentTimeMillis() - startTime > timeout) {//尝试超过了设定值之后直接跳出循环
	                    return false;
	                }
	                Thread.sleep(tryInterval);
	            } while (template.hasKey(lock.getName()));
	        } catch (InterruptedException e) {
	            logger.error(e.getMessage());
	            return false;
	        }
	        return false;
	    }
	
	    /**
	     * 释放锁
	     */
	    public void releaseLock(Lock lock) {
	        if (!StringUtils.isEmpty(lock.getName())) {
	            template.delete(lock.getName());
	        }
	    }
	
	}
	
4.用法：

	@Autowired
	DistributedLockHandler distributedLockHandler;
	Lock lock=new Lock("lockk","sssssssss);
	if(distributedLockHandler.tryLock(lock){
	    doSomething();
	    distributedLockHandler.releaseLock();
	}
	 
注意点

在使用全局锁时为了防止死锁采用 setex命令，这种命令需要根据具体的业务具体设置锁的超时时间。

另外一个就是锁的粒度性。比如在redis实战中有个案列，为了实现买卖市场交易的功能，把整个交易市场都锁住了，导致了性能不足的情况，改进方案只对买卖的商品进行加锁而不是整个市场。

# redlock

关于redis实现分布式锁，redis官方推荐使用redlock。

在不同进程需要互斥地访问共享资源时，分布式锁是一种非常有用的技术手段。实现高效的分布式锁有三个属性需要考虑：

- 安全属性：互斥，不管什么时候，只有一个客户端持有锁
- 效率属性A:不会死锁
- 效率属性B：容错，只要大多数redis节点能够正常工作，客户端端都能获取和释放锁。

Redlock是redis官方提出的实现分布式锁管理器的算法。这个算法会比一般的普通方法更加安全可靠。关于这个算法的讨论可以看下[官方文档](https://github.com/antirez/redis-doc/blob/master/topics/distlock.md)

增加依賴：

	<!-- redisson-->
    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson</artifactId>
        <version>3.3.2</version>
    </dependency>
