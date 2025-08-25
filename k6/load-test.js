import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '2m', target: 10 }, // 램프업
        { duration: '5m', target: 10 }, // 유지
        { duration: '2m', target: 0 },  // 램프다운
    ],
};

export default function() {
    // Nginx 로드밸런서를 통한 헬스체크 테스트
    let nginxHealthResponse = http.get('http://nginx/health');
    
    check(nginxHealthResponse, {
        'nginx health status is 200': (r) => r.status === 200,
        'nginx response time < 500ms': (r) => r.timings.duration < 500,
        'health status is UP': (r) => r.json('status') === 'UP',
    });

    // 직접 앱 인스턴스 테스트
    let app1Response = http.get('http://app1:8080/actuator/health');
    let app2Response = http.get('http://app2:8080/actuator/health');
    
    check(app1Response, {
        'app1 health is 200': (r) => r.status === 200,
        'app1 response time < 500ms': (r) => r.timings.duration < 500,
    });
    
    check(app2Response, {
        'app2 health is 200': (r) => r.status === 200,
        'app2 response time < 500ms': (r) => r.timings.duration < 500,
    });

    // API 엔드포인트 로드밸런싱 테스트
    let boardResponse = http.get('http://nginx/api/boards');
    
    check(boardResponse, {
        'boards api status is valid': (r) => r.status === 200 || r.status === 401 || r.status === 404, // 다양한 상태 허용
        'boards api response time < 1s': (r) => r.timings.duration < 1000,
    });

    sleep(1);
}