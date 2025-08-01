package club.jiajiajia.rabbitmq.provider.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // 消息发送到Exchange确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息成功到达Exchange，ID: " +
                        (correlationData != null ? correlationData.getId() : "null"));
            } else {
                System.err.println("消息未能到达Exchange，ID:"+(correlationData != null ? correlationData.getId() : "null")+"，原因: " + cause);
                // 这里可以添加消息重发逻辑
            }
        });

        // 消息从Exchange路由到Queue失败回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.err.println("消息从Exchange路由到Queue失败");
            System.err.println("消息内容: " + new String(message.getBody()));
            System.err.println("响应码: " + replyCode);
            System.err.println("响应内容: " + replyText);
            System.err.println("交换机: " + exchange);
            System.err.println("路由键: " + routingKey);
            // 这里可以添加处理失败路由的逻辑
        });
        // 必须设置为true，否则即使设置了ReturnCallback也不会回调
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }
}