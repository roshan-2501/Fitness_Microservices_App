package com.example.activityservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    //Queue
    @Bean
    public Queue activityQueue(){
        return new Queue(queue,true);
    }

    //Defines a Spring bean that creates a RabbitMQ DirectExchange with the specified name for exact routing of messages.
    @Bean
    public DirectExchange activityExchange(){
        return new DirectExchange(exchange);
    }

    //Creates a binding between the queue and the direct exchange using the specified routing key.
    @Bean
    public Binding activityBinding(Queue activityQueue,DirectExchange activityExchange){
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }

    //your messages will be automatically converted to JSON for sending, and automatically converted back to Java objects when you receive them.
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
