package com.loopers.application.productlike

import com.loopers.domain.productlike.ProductLikeCommand
import com.loopers.domain.productlike.ProductLikeService
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductLikeFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val productLikeService: ProductLikeService,
) {
    @Transactional
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

    @Transactional
    fun unlike(like: ProductLikeCriteria.Unlike) {
        val user = userService.getUserById(like.userId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다. userId: ${like.userId}")
        val product = productService.getProduct(like.productId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다. productId: ${like.productId}")

        productLikeService.unlike(
            ProductLikeCommand.Unlike(
                user.id,
                product.id,
            ),
        )
    }
}
