package club.jiajiajia.rabbitmq.consumer.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/**
* @author: Jiajiajia
* @Date: 2021/8/26
* @Description: 直接交换机 监听器
*/
@Component
public class DirectListener {
    @RabbitListener(queues = "directQueue")
    public void receive(Map map, Message message, Channel channel) throws IOException {
        try{
            //todo
            System.out.println("监听接收到消息"+map);
            int a = 1/0;
            // 手动确认 成功消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
        }catch (Exception e){
            e.printStackTrace();
            // 失败确认
             channel.basicNack(message.getMessageProperties().getDeliveryTag(),true,false);
            // 失败确认后重新发布到其他队列
            channel.basicPublish("directExchange","directRouting2",true,null,message.getBody());
        }
    }
}
