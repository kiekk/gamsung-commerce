import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '2s', target: 1 },   // 워밍업: 1 VU로 10초
        { duration: '30s', target: 100 }, // 본 테스트: 100 VU로 30초
    ]
};

// 실행 시 환경변수로 파라미터를 주입
const params = {
    name: __ENV.NAME,           // 예: --env NAME=Pro
    brandId: __ENV.BRAND_ID,    // 예: --env BRAND_ID=1
    sort: __ENV.SORT,           // 예: --env SORT=latest
    page: __ENV.PAGE,           // 예: --env PAGE=0
    size: __ENV.SIZE,           // 예: --env SIZE=20
};

// 쿼리스트링 생성 함수
function buildUrl(baseUrl, queryParams) {
    const entries = Object.entries(queryParams).filter(
        ([, value]) => value !== undefined && value !== null && value !== ''
    );
    if (entries.length === 0) {
        return baseUrl;
    }
    const queryString = entries
        .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
        .join('&');
    return `${baseUrl}?${queryString}`;
}

export default function () {
    const baseUrl = 'http://localhost:8080/api/v1/products';
    const url = buildUrl(baseUrl, params);

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}