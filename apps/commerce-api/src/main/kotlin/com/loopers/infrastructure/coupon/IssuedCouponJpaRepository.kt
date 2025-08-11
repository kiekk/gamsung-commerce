package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.IssuedCouponEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface IssuedCouponJpaRepository : JpaRepository<IssuedCouponEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ic FROM IssuedCouponEntity ic WHERE ic.id = :id")
    fun findByIdWithPessimisticLock(id: Long): IssuedCouponEntity?
}
