package club.jiajiajia.rabbitmq.common;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @author: Jiajiajia
* @Date: 2021/8/26
* @Description: 主题交换机
*/
@Configuration
public class TopicExchangeConfig {

    public TopicExchangeConfig(){
        System.out.println("TopicExchangeConfig init");
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("topicExchange",true,false);
    }

    @Bean
    public Queue topicQueryA(){
        return new Queue("topicQueueA",true);
    }

    @Bean
    public Queue topicQueryB(){
        return new Queue("topicQueueB",true);
    }

    @Bean
    public Queue topicQueryC(){
        return new Queue("topicQueueC",true);
    }

    @Bean
    Binding topicQueryABinding(){
        return BindingBuilder.bind(topicQueryA()).
                to(topicExchange()).with("abc.#");
    }

    @Bean
    Binding topicQueryBBinding(){
        return BindingBuilder.bind(topicQueryB()).
                to(topicExchange()).with("#.123");
    }

    @Bean
    Binding topicQueryCBinding(){
        return BindingBuilder.bind(topicQueryC()).
                to(topicExchange()).with("#.456");
    }

}
