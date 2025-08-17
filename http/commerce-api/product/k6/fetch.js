import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 1000, // 동시에 실행할 가상 사용자 수
    duration: '30s' // 테스트 지속 시간
};

export default function () {
    // const productId = Math.floor(Math.random() * 1000) + 1; // 예시로 1부터 1000까지의 ID를 사용
    // const url = `http://localhost:8080/api/v1/products/${productId}`;
    const url = 'http://localhost:8080/api/v1/products/1';

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1); // 요청 간 대기 시간
}