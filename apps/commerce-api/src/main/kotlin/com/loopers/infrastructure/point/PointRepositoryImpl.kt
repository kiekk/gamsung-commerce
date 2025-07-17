package com.loopers.infrastructure.point

import com.loopers.domain.point.PointRepository
import org.springframework.stereotype.Repository

@Repository
class PointRepositoryImpl: PointRepository {
    override fun getPoints(userId: String): Long? {
        return 100L // 임시로 100 포인트를 반환
    }
}
