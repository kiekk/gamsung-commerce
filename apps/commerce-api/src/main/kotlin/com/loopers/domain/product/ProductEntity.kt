package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import com.loopers.domain.vo.Price
import com.loopers.support.enums.product.ProductStatusType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "product")
class ProductEntity(
    val brandId: Long,
    val name: String,
    val description: String? = null,
    @Embedded
    val price: Price,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var status: ProductStatusType

    init {
        !name.matches(PRODUCT_NAME_REGEX) && throw IllegalArgumentException("상품명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다.")
        description?.let {
            !it.matches(PRODUCT_DESCRIPTION_REGEX) && throw IllegalArgumentException("상품 설명은 최대 100자 이내로 입력해야 합니다.")
        }
        status = ProductStatusType.ACTIVE
    }

    fun isNotActive(): Boolean {
        return status != ProductStatusType.ACTIVE
    }

    fun inactive() {
        status = ProductStatusType.INACTIVE
    }

    override fun toString(): String {
        return "ProductEntity(id=$id, brandId=$brandId, name='$name', description=$description, price=$price, status=$status," +
                " createdAt=$createdAt, updatedAt=$updatedAt)"
    }

    companion object {
        private val PRODUCT_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
        private val PRODUCT_DESCRIPTION_REGEX = "^.{0,100}$".toRegex()
    }
}
