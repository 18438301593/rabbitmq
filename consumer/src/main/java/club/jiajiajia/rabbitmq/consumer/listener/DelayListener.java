package club.jiajiajia.rabbitmq.consumer.listener;

import club.jiajiajia.rabbitmq.common.DelayExchangeConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
* @author: Jiajiajia
* @Date: 2021/8/26
* @Description: 延迟交换机 监听器
*/
@Component
@ConditionalOnBean(DelayExchangeConfig.class)
public class DelayListener {
    @RabbitListener(queues = "delayQueue")
    public void receive(Map map){
        System.out.println("delayQueue监听接收到消息:"+map);
    }
}
