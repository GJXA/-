package com.xushu.campus.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    // 交换机名称
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    // 队列名称
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String SMS_QUEUE = "sms.queue";
    public static final String WEBSOCKET_QUEUE = "websocket.queue";

    // 路由键
    public static final String NOTIFICATION_ROUTING_KEY = "notification.routing.key";
    public static final String EMAIL_ROUTING_KEY = "email.routing.key";
    public static final String SMS_ROUTING_KEY = "sms.routing.key";
    public static final String WEBSOCKET_ROUTING_KEY = "websocket.routing.key";

    /**
     * 定义直连交换机
     */
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    /**
     * 定义通知队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", NOTIFICATION_ROUTING_KEY + ".dlq")
                .build();
    }

    /**
     * 定义邮件队列
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", EMAIL_ROUTING_KEY + ".dlq")
                .build();
    }

    /**
     * 定义短信队列
     */
    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(SMS_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", SMS_ROUTING_KEY + ".dlq")
                .build();
    }

    /**
     * 定义WebSocket队列
     */
    @Bean
    public Queue websocketQueue() {
        return QueueBuilder.durable(WEBSOCKET_QUEUE)
                .withArgument("x-dead-letter-exchange", NOTIFICATION_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", WEBSOCKET_ROUTING_KEY + ".dlq")
                .build();
    }

    /**
     * 绑定通知队列到交换机
     */
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    /**
     * 绑定邮件队列到交换机
     */
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(notificationExchange())
                .with(EMAIL_ROUTING_KEY);
    }

    /**
     * 绑定短信队列到交换机
     */
    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue())
                .to(notificationExchange())
                .with(SMS_ROUTING_KEY);
    }

    /**
     * 绑定WebSocket队列到交换机
     */
    @Bean
    public Binding websocketBinding() {
        return BindingBuilder.bind(websocketQueue())
                .to(notificationExchange())
                .with(WEBSOCKET_ROUTING_KEY);
    }

    /**
     * 配置消息转换器（使用JSON）
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        // 开启消息确认机制
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送成功: " + correlationData);
            } else {
                System.out.println("消息发送失败: " + cause);
            }
        });
        // 开启消息退回机制
        template.setReturnsCallback(returned -> {
            System.out.println("消息被退回: " + returned.getMessage());
        });
        return template;
    }
}