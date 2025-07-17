package com.loopers.domain.point

import org.springframework.stereotype.Service

@Service
class PointService {
    fun getPoints(userId: String): Long? {
        return 100L // 임시로 100 포인트를 반환
    }
}
