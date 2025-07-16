package club.jiajiajia.rabbitmq.consumer.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMqConfig {
    @Bean("customContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPrefetchCount(2);  // 设置预取数量
        factory.setConcurrentConsumers(2); // 设置固定消费者数量，默认1，如果消息处理时间较长，有消息积压，可根据业务需要设置并发数
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置手动ack
        factory.setDefaultRequeueRejected(false); // 超时后不重新入队
        return factory;
    }
}
