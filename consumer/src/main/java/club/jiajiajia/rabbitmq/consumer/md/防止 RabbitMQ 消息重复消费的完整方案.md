消息重复消费是分布式系统中常见的问题，以下是针对 RabbitMQ 的全面解决方案：

### 一、消息重复消费的原因

1. 生产者重复发送
   - 网络问题导致生产者重发
   - 生产者未收到Broker确认
2. 消费者重复处理

    - 消费者处理超时导致消息重新入队
    - 消费者崩溃后消息重新投递

### 二、解决方案

> 消费者端幂等设计（最根本方案）

数据库唯一约束

```java
// 订单处理示例
public void processOrder(Order order) {
    try {
        // 基于订单ID唯一约束
        orderDao.insert(order); // 唯一索引或主键冲突会抛出异常
        // 业务处理...
    } catch (DuplicateKeyException e) {
        log.warn("订单已处理: {}", order.getOrderId());
        // 更新操作或直接返回
    }
}
```

Redis原子操作

```java
// 使用SETNX命令
String key = "order:" + orderId;
Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "processing", 24, TimeUnit.HOURS);
if (!Boolean.TRUE.equals(result)) {
    log.warn("订单正在处理或已处理: {}", orderId);
    return;
}
// 业务处理...
```

> 消息去重表方案

```java
CREATE TABLE message_dedup (
    message_id VARCHAR(128) PRIMARY KEY,
    status TINYINT DEFAULT 0 COMMENT '0-处理中 1-处理完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;
```

```java
@Transactional
public void handleMessage(Message message) {
    // 1. 插入去重记录
    int inserted = messageDedupDao.insertIfNotExists(message.getId());
    if (inserted == 0) {
        log.warn("消息重复: {}", message.getId());
        return;
    }
    
    // 2. 业务处理
    processBusiness(message);
    
    // 3. 更新状态
    messageDedupDao.updateStatus(message.getId(), 1);
}
```

> RabbitMQ服务端配置

消息唯一ID

```java
MessageProperties properties = new MessageProperties();
properties.setMessageId(UUID.randomUUID().toString());
Message message = new Message(body.getBytes(), properties);
rabbitTemplate.send(exchange, routingKey, message);
```

死信队列设置

```java
@Bean
public Queue businessQueue() {
    return QueueBuilder.durable("business.queue")
            .withArgument("x-dead-letter-exchange", "dlx.exchange")
            .withArgument("x-dead-letter-routing-key", "business.dlq")
            .withArgument("x-message-ttl", 600000) // 10分钟过期
            .build();
}
```

> 消费者确认机制优化

```java
@RabbitListener(queues = "order.queue")
public void handleOrder(
        Order order,
        Channel channel,
        @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
    
    // 1. 幂等检查
    if (orderService.isProcessed(order.getId())) {
        channel.basicAck(tag, false); // 已处理直接确认
        return;
    }
    
    try {
        // 2. 业务处理
        orderService.process(order);
        
        // 3. 确认消息
        channel.basicAck(tag, false);
    } catch (Exception e) {
        // 根据异常类型决定是否重试
        if (isRetryable(e)) {
            channel.basicNack(tag, false, true); // 重试
        } else {
            channel.basicNack(tag, false, false); // 丢弃
        }
    }
}
```


### 三、生产环境最佳实践

>消息设计规范

```java
public class BusinessMessage {
    private String messageId; // 唯一标识
    private Long businessId;  // 业务ID
    private Integer retryCount = 0; // 重试次数
    private Long timestamp;   // 消息创建时间
    // 其他业务字段...
}
```

>消费者模板代码

```java
public abstract class IdempotentConsumer<T> {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    protected abstract void doProcess(T message);
    
    protected abstract String getMessageId(T message);
    
    protected boolean isRetryable(Exception e) {
        return e instanceof TemporaryException;
    }
    
    public final void handleMessage(T message, Channel channel, long tag) throws IOException {
        String messageId = getMessageId(message);
        String lockKey = "msg:" + messageId;
        
        try {
            // 获取分布式锁
            Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", 30, TimeUnit.MINUTES);
            
            if (!Boolean.TRUE.equals(locked)) {
                channel.basicAck(tag, false);
                return;
            }
            
            // 业务处理
            doProcess(message);
            
            // 确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            if (isRetryable(e)) {
                channel.basicNack(tag, false, true);
            } else {
                channel.basicNack(tag, false, false);
            }
            throw e;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
}
```