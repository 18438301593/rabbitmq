package club.jiajiajia.rabbitmq.provider.controller;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("1");
        Map map = new HashMap();
        map.put("id","1");
        map.put("name","peiqi");
        rabbitTemplate.convertAndSend("directExchange","directRouting", map,correlationData);
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
