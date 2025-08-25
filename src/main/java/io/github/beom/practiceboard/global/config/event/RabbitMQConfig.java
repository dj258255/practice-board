package io.github.beom.practiceboard.global.config.event;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 설정
 * 이벤트 기반 아키텍처를 위한 메시지 큐 설정
 */
@Configuration
public class RabbitMQConfig {

    // Exchange 이름들
    public static final String COMMENT_EXCHANGE = "comment.exchange";
    public static final String BOARD_EXCHANGE = "board.exchange";
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    // Queue 이름들
    public static final String COMMENT_CREATED_QUEUE = "comment.created.queue";
    public static final String COMMENT_UPDATED_QUEUE = "comment.updated.queue";
    public static final String COMMENT_DELETED_QUEUE = "comment.deleted.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    // Routing Key들
    public static final String COMMENT_CREATED_ROUTING_KEY = "comment.created";
    public static final String COMMENT_UPDATED_ROUTING_KEY = "comment.updated";
    public static final String COMMENT_DELETED_ROUTING_KEY = "comment.deleted";
    public static final String NOTIFICATION_ROUTING_KEY = "notification";

    /**
     * JSON 메시지 컨버터
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 설정
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    /**
     * 리스너 컨테이너 팩토리 설정
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        
        // 연결 실패 시 안전한 처리
        factory.setAutoStartup(false);
        factory.setMissingQueuesFatal(false);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        
        return factory;
    }

    // === 댓글 관련 Exchange, Queue, Binding ===

    /**
     * 댓글 Exchange
     */
    @Bean
    public TopicExchange commentExchange() {
        return new TopicExchange(COMMENT_EXCHANGE);
    }

    /**
     * 댓글 생성 큐
     */
    @Bean
    public Queue commentCreatedQueue() {
        return QueueBuilder.durable(COMMENT_CREATED_QUEUE).build();
    }

    /**
     * 댓글 수정 큐
     */
    @Bean
    public Queue commentUpdatedQueue() {
        return QueueBuilder.durable(COMMENT_UPDATED_QUEUE).build();
    }

    /**
     * 댓글 삭제 큐
     */
    @Bean
    public Queue commentDeletedQueue() {
        return QueueBuilder.durable(COMMENT_DELETED_QUEUE).build();
    }

    /**
     * 댓글 생성 바인딩
     */
    @Bean
    public Binding commentCreatedBinding() {
        return BindingBuilder
                .bind(commentCreatedQueue())
                .to(commentExchange())
                .with(COMMENT_CREATED_ROUTING_KEY);
    }

    /**
     * 댓글 수정 바인딩
     */
    @Bean
    public Binding commentUpdatedBinding() {
        return BindingBuilder
                .bind(commentUpdatedQueue())
                .to(commentExchange())
                .with(COMMENT_UPDATED_ROUTING_KEY);
    }

    /**
     * 댓글 삭제 바인딩
     */
    @Bean
    public Binding commentDeletedBinding() {
        return BindingBuilder
                .bind(commentDeletedQueue())
                .to(commentExchange())
                .with(COMMENT_DELETED_ROUTING_KEY);
    }

    // === 알림 관련 Exchange, Queue, Binding ===

    /**
     * 알림 Exchange
     */
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    /**
     * 알림 큐
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    /**
     * 알림 바인딩 (댓글 이벤트들을 알림으로 연결)
     */
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(commentExchange())
                .with("comment.*"); // 모든 댓글 이벤트를 알림으로
    }
}