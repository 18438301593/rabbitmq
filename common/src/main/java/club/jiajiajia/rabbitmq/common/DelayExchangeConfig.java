package club.jiajiajia.rabbitmq.common;

import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
* @author: Jiajiajia
* @Date: 2021/8/26
* @Description: 延时交换机（需要安装插件 rabbitmq_delayed_message_exchange）
*/
@Configuration
@ConditionalOnProperty(name = "spring.delay", havingValue = "true")
public class DelayExchangeConfig {
    public DelayExchangeConfig(){
        System.out.println("DelayExchangeConfig init");
    }

    /**
     * 延时队列
     * @return
     */
    @Bean
    public Queue delayQueue(){
        return new Queue("delayQueue",true);
    }


    /**
     * 延时队列交换机
     * 注意这里的交换机类型：CustomExchange
     * @return
     */
    @Bean
    public CustomExchange delayExchange(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("delayExchange","x-delayed-message",true, false,args);
    }

    /**
     * 给延时队列绑定交换机
     * @return
     */
    @Bean
    public Binding delayBinding(Queue delayQueue, CustomExchange delayExchange){
        return BindingBuilder.bind(delayQueue).to(delayExchange).with("delay").noargs();
    }
}
