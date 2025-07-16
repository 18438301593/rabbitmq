package club.jiajiajia.rabbitmq.common;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
* @author: Jiajiajia
* @Date: 2021/8/26
* @Description: 直接交换机
*/
@Configuration
public class DirectExchangeConfig {
    public DirectExchangeConfig(){
        System.out.println("DirectExchangeConfig init");
    }

    /**
     * 队列
     * @return
     */
    @Bean
    public Queue directQueue() {
        Map<String, Object> arguments = new HashMap<>(4);
        // 20秒内未被消费，则进入死信队列
        // x-message-ttl：队列中消息的过期时间。单位为毫秒（ms）。例如，设置 x-message-ttl 为 10000 表示队列中的消息十秒钟后过期。
        arguments.put("x-message-ttl", 20000);

        // x-max-length-bytes：队列允许存放的最大字节数。例如，设置 x-max-length-bytes 为 1024 表示队列中最多能存放 1KB 的消息数据。
        // x-max-length：队列允许存放最大消息数。例如，设置 x-max-length 为 100 表示队列中最多能存放 100 条消息。
        arguments.put("x-max-length", 1000);

        // x-dead-letter-exchange 和 x-dead-letter-routing-key：用于设置死信队列，即当队列中的消息被拒绝或超时时将消息发送到指定的队列和路由键。
        arguments.put("x-dead-letter-exchange", "dead.exchange");
        arguments.put("x-dead-letter-routing-key", "dead.message");

        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。

        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue("directQueue",true,false,false,arguments);
    }

    /**
     * Direct交换机
     * @return
     */
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange("directExchange",
                true,false);
    }

    /**
     * 绑定  将队列和交换机绑定, 并设置用于匹配键
     * @return
     */
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(directQueue()).
                to(directExchange()).with("directRouting");
    }

    /**************************************************** 死信队列配置 *************************************************************/

    /**
     * 配置死信队列
     * @return
     */
    @Bean(name = "deadQueue")
    public Queue deadQueue() {
        return new Queue("dead.queue", true, false, true);
    }
    /**
     * 配置死信交换机
     * @return
     */
    @Bean(name = "deadExchange")
    public Exchange exchange() {
        return new DirectExchange("dead.exchange", true, true);
    }
    @Bean(name = "deadBinding")
    public Binding binding() {
        return BindingBuilder.bind(deadQueue()).to(exchange()).with("dead.message").noargs();
    }
}
