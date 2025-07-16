package club.jiajiajia.rabbitmq.consumer.listener;

import club.jiajiajia.rabbitmq.dto.OrderMessage;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/**
* @author: Jiajiajia
* @Date: 2021/8/26
* @Description: 直接交换机 监听器
*/

/**
 * spring:
 *   rabbitmq:
 *     listener:
 *       simple:
 *         acknowledge-mode: manual  # 全局设置为手动确认
 */

@Component
public class DirectListener {
    // queues  队列名称
    // ackMode = "MANUAL" 直接在注解上设置手动确认，覆盖全局配置
    @RabbitListener(queues = "directQueue",ackMode = "MANUAL",containerFactory = "customContainerFactory")
    public void receive(
            @Payload OrderMessage map,// 消息体
            Message message,          // 完整消息对象
            Channel channel           // RabbitMQ通道
            // @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag // 方式二，通过@Header注解获取deliveryTag
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String correlationId = message.getMessageProperties().getCorrelationId();
        try {
            System.out.println("监听接收到消息，ID："+correlationId+"，内容："+map);
            Thread.sleep(10000);
            channel.basicAck(deliveryTag, false);
            System.out.println("Message acknowledged");
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, true);
            System.err.println("Message rejected and requeued: " + e.getMessage());
        }
    }
}
