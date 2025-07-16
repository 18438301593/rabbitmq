`SimpleRabbitListenerContainerFactory` 是 Spring AMQP 中用于创建消息监听容器的工厂类，提供了丰富的配置选项。以下是其主要可配置内容：

### 一、基础配置
1. 连接相关
```java
factory.setConnectionFactory(connectionFactory); // 必须设置的连接工厂
factory.setConnectionNameStrategy(() -> "my-connection"); // 连接命名策略
```

2. 并发配置

```java
factory.setConcurrentConsumers(3); // 固定消费者数量
factory.setMaxConcurrentConsumers(10); // 最大消费者数量
factory.setConsecutiveActiveTrigger(5); // 触发扩容的连续活跃次数
factory.setConsecutiveIdleTrigger(10); // 触发缩容的连续空闲次数
```
默认的固定消费者数量是1。如果业务中消息处理时间较长，且有消息积压，可根据业务需要设置多个消费者进行并发处理

### 二、消息处理配置

1. 确认模式

```java
factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 确认模式
// 可选值: 
// AUTO - 自动确认
// MANUAL - 手动确认
// NONE - 不确认(不推荐)
```

2. 预取数量

```java
factory.setPrefetchCount(10); // 每个消费者的预取消息数
```

3. 事务配置

```java
factory.setChannelTransacted(true); // 启用通道事务
factory.setTransactionManager(transactionManager); // 外部事务管理器
```

### 三、高级特性配置

1. 消息转换器

```java
factory.setMessageConverter(new Jackson2JsonMessageConverter()); // 消息转换器
```

2. 错误处理

```java
factory.setErrorHandler(new ConditionalRejectingErrorHandler()); // 错误处理器
factory.setDefaultRequeueRejected(false); // 是否重新入队
```

3. 消费者参数

```java
Map<String, Object> args = new HashMap<>();
args.put("consumerTimeout", 30000); // 消费者超时(毫秒)
factory.setConsumerArgs(args);
```

### 四、性能调优配置

1. 接收超时

```java
factory.setReceiveTimeout(1000L); // 接收超时(毫秒)
```

当队列为空时，消费者会等待该超时时间，若期间仍无消息到达，则暂时释放资源



