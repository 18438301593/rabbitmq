package club.jiajiajia.rabbitmq.provider.controller;

import club.jiajiajia.rabbitmq.dto.OrderMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class ProviderController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 测试 直接交换机
     * @return
     */
    @GetMapping("direct")
    private String direct(){
        OrderMessage orderMessage = new OrderMessage(1,UUID.randomUUID().toString());
        // correlationData 消息id
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("directExchange","directRouting", orderMessage,message -> {
            // 设置消息持久化
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        },correlationData);
        return "ok";
    }

    /**
     * 测试 扇形交换机（广播）
     * @return
     */
    @GetMapping("fanout")
    private String fanout(){
        Map map = new HashMap();
        map.put("id","1");
        map.put("name","fanout");
        rabbitTemplate.convertAndSend("fanoutExchange",null, map);
        return "ok";
    }

    /**
     * 测试 主题交换机
     * @return
     */
    @GetMapping("topic")
    private String tpoic(@RequestParam("routingKey") String routingKey){
        Map map = new HashMap();
        map.put("id","1");
        map.put("name","tpoic");
        rabbitTemplate.convertAndSend("topicExchange",routingKey, map);
        return "ok";
    }

    /**
     * 测试 延时交换机
     * @return
     */
    @GetMapping("delay")
    private String delay(@RequestParam("time") Integer time){
        Map map = new HashMap();
        map.put("id","1");
        map.put("name","delay");
        rabbitTemplate.convertAndSend("delayExchange","delay", map,
            message -> {
                message.getMessageProperties().setHeader("x-delay",time);
                return message;
            });
        return "ok";
    }
}
