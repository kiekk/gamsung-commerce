package com.loopers.application.product

import com.loopers.domain.productlike.ProductLikeCommand
import com.loopers.domain.productlike.ProductLikeService
import com.loopers.domain.productlike.ProductService
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
    fun like(like: ProductLikeCriteria.Like) {
        val user = userService.getUserById(like.userId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다. userId: ${like.userId}")
        val product = productService.getProduct(like.productId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다. productId: ${like.productId}")

        productLikeService.like(
            ProductLikeCommand.Like(
                user.id,
                product.id,
            ),
        )
    }

    fun unlike(like: ProductLikeCriteria.Unlike) {
        TODO("Not yet implemented")
    }
}
