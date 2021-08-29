# rabbitmq
rabbitmq测试案例

* common模块：交换机、队列等配置类。
* provider模块：生产者模块，发送数据到交换机。
* consumer模块：消费者模块。

注意，如果你的rabbitmq没有安装rabbitmq_delayed_message_exchang插件
请不要再配置文件中添加 spring.delay 这个配置项。

如果你想启用延时队列，请添加如下配置：
```yaml
spring:
  delay: true
```

ack分支 新增关于 发布确认和消费确认（ack）的代码
