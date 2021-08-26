package club.jiajiajia.rabbitmq.consumer.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;


/**
* @author: Jiajiajia
* @Date: 2021/8/26
* @Description: 主题 监听器
*/
@Component
public class TopicListener {
    @RabbitListener(queues = "topicQueueA")
    public void receive(Map map){
        System.out.println("topicQueueA监听接收到消息:"+map);
    }

    @RabbitListener(queues = "topicQueueB")
    public void receive2(Map map){
        System.out.println("topicQueueB监听接收到消息:"+map);
    }

    @RabbitListener(queues = "topicQueueC")
    public void receive3(Map map){
        System.out.println("topicQueueC监听接收到消息:"+map);
    }
}
