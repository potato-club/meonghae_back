package com.meonghae.profileservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;



import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitmqConfig {
    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Bean
    Queue queue(){
        return new Queue("meonghae.queue",false);
    }
    @Bean
    CustomExchange customExchange(){ // direct는 라우팅 키와 큐의 바인딩 키가 일치해야 메시지를 큐로 보냄
        Map<String,Object> args = new HashMap<String,Object>();
        args.put("x-delayed-type","direct");
        return new CustomExchange("meonghae.exchange","x-delayed-message",true,false, args);
    }
    @Bean
    Binding binding(CustomExchange customExchange, Queue queue){
        return BindingBuilder.bind(queue).to(customExchange).with("lab303").and(customExchange.getArguments());
    }
    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    ConnectionFactory connectionFactory(){
        //CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        connectionFactory.setVirtualHost("/");
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setConnectionTimeout(30000);
        connectionFactory.setRequestedHeartbeat(20);

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(connectionFactory);
        cachingConnectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        cachingConnectionFactory.setPublisherReturns(true);
        return cachingConnectionFactory;
    }
    //에러처리 핸들링
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAdviceChain(retryAdvice());

        factory.setConcurrentConsumers(1); //동시에 활성화될 소비자 수를 1로 설정
        factory.setPrefetchCount(1); //한 번에 처리할 메시지 수를 1로 설정
        return factory;
    }
    @Bean
    public RetryOperationsInterceptor retryAdvice() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3) // 최대 재시도
                .backOffOptions(1000,2.0,10000)// 재시도 간격 설정 (초기 1초, 멀티플라이어 2, 최대 10초)
                .recoverer(messageRecoverer())
                .build();
    }
    @Bean
    public MessageRecoverer messageRecoverer() {
        return ((message, cause) -> {
            //최종 실패시 로직 구현
            System.out.println("메시지 최종실패: "+message.getBody());
        });
    }

}

