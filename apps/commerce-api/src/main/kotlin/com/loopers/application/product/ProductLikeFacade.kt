package com.loopers.application.product

import com.loopers.domain.product.ProductLikeEntity
import com.loopers.domain.product.ProductLikeService
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class ProductLikeFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val productLikeService: ProductLikeService,
) {
    fun like(like: ProductLikeCommand.Like) {
        val user = userService.getUserById(like.userId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다. userId: ${like.userId}")
        val product = productService.getProduct(like.productId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다. productId: ${like.productId}")

        productLikeService.like(
            ProductLikeEntity(
                user.id,
                product.id,
            ),
        )
    }

    fun unlike(like: ProductLikeCommand.Unlike) {
        TODO("Not yet implemented")
    }
}
