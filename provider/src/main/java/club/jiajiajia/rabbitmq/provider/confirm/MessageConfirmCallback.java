package club.jiajiajia.rabbitmq.provider.confirm;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @ClassName MessageConfirmCallback
 * @Author Jiajiajia
 * @Date 2021/8/29 20:17
 * @Description 消息发送到交换机回调confirm方法，当交换机路由不到相应的队列回调returnedMessage方法
 **/
@Component
public class MessageConfirmCallback implements RabbitTemplate.ConfirmCallback ,RabbitTemplate.ReturnCallback{

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        //指定 ConfirmCallback
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 消息发送到交换机时，回调此方法
     * @param correlationData 发送时传入的数据
     * @param ack true代表发送成功，false代表发送失败
     * @param cause 失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("消息唯一标识：" + correlationData);
        System.out.println("确认结果：" + ack);
        System.out.println("失败原因：" + cause);
    }

    /**
     * 只有消息路由失败的时候才会回调此方法
     * @param message 消息主体
     * @param replyCode 失败码
     * @param replyText 失败愿意
     * @param exchange 交换机
     * @param routingKey 路由键
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("消息主体 message : "+message);
        System.out.println("消息主体 message : "+replyCode);
        System.out.println("描述："+replyText);
        System.out.println("消息使用的交换器 exchange : "+exchange);
        System.out.println("消息使用的路由键 routing : "+routingKey);
    }
}