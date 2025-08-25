package com.loopers.application.productlike

import com.loopers.domain.product.ProductService
import com.loopers.domain.productlike.ProductLikeCommand
import com.loopers.domain.productlike.ProductLikeService
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
    fun like(like: ProductLikeCriteria.Like) {
        val user = userService.findUserBy(like.username)
            ?: throw CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다. username: ${like.username}")
        val product = productService.findProductBy(like.productId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다. productId: ${like.productId}")

        productLikeService.likeOptimistic(
            ProductLikeCommand.Like(
                user.id,
                product.id,
            ),
        )
    }

    fun unlike(like: ProductLikeCriteria.Unlike) {
        val user = userService.findUserBy(like.username)
            ?: throw CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다. username: ${like.username}")
        val product = productService.findProductBy(like.productId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다. productId: ${like.productId}")

        productLikeService.unlikeOptimistic(
            ProductLikeCommand.Unlike(
                user.id,
                product.id,
            ),
        )
    }

    @Transactional(readOnly = true)
    fun getUserLikeProducts(username: String): List<ProductLikeInfo.UserLikeProductDetail> {
        val user = userService.findUserBy(username)
            ?: throw CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다. username: $username")
        val productLikes = productLikeService.getProductLikesByUserId(user.id)
        return productService.getProductsByIds(productLikes.map { it.id })
            .map { ProductLikeInfo.UserLikeProductDetail.from(it) }
    }
}
