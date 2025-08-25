package io.github.beom.practiceboard.global.config.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 연결 초기화 컴포넌트
 * 애플리케이션 시작 후 RabbitMQ 연결이 안정화되면 리스너를 시작합니다.
 * RabbitMQ 4.x 버전 지원
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class RabbitMQInitializer {

    private final RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
    private final ConnectionFactory connectionFactory;

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void startRabbitListeners() {
        log.info("애플리케이션 시작 완료 - RabbitMQ 리스너 시작 시도");
        
        try {
            // RabbitMQ 연결 테스트
            connectionFactory.createConnection().close();
            
            // 리스너 컨테이너들을 시작
            log.info("RabbitMQ 리스너 컨테이너 시작 중");
            rabbitListenerEndpointRegistry.getListenerContainers().forEach(listenerContainer -> {
                if (!listenerContainer.isRunning()) {
                    log.info("RabbitMQ 리스너 컨테이너 시작: {}", listenerContainer);
                    listenerContainer.start();
                }
            });
            
            log.info("모든 RabbitMQ 리스너가 성공적으로 시작되었습니다.");
            
        } catch (Exception e) {
            log.warn("RabbitMQ 연결 실패 - 리스너 시작 연기: {}", e.getMessage());
            // 5초 후 재시도
            retryStartListeners();
        }
    }

    private void retryStartListeners() {
        try {
            Thread.sleep(5000);
            startRabbitListeners();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("RabbitMQ 리스너 재시작 중 인터럽트 발생", e);
        }
    }
}