package club.jiajiajia.rabbitmq.consumer.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
* @author: Jiajiajia
* @Date: 2021/8/26
* @Description: 直接交换机 监听器
*/
@Component
public class DirectListener {
    @RabbitListener(queues = "directQueue")
    public void receive(Map map){
        System.out.println("监听接收到消息"+map);
    }
}
