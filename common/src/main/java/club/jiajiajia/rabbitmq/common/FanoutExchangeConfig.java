package club.jiajiajia.rabbitmq.common;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
* @author: Jiajiajia
* @Date: 2021/8/26
* @Description: 扇形交换机（广播模式）
*/
@Configuration
public class FanoutExchangeConfig {

    public FanoutExchangeConfig(){
        System.out.println("FanoutExchangeConfig init");
    }
    @Bean
    public Queue fanoutQueue(){
        return new Queue("fanoutQueue",true);
    }

    @Bean
    public Queue fanoutQueue2(){
        return new Queue("fanoutQueue2",true);
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("fanoutExchange",true,false);
    }

    @Bean
    public Binding queueBinding(){
        return BindingBuilder.bind(this.fanoutQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding queueBinding2(){
        return BindingBuilder.bind(this.fanoutQueue2()).to(fanoutExchange());
    }
}
