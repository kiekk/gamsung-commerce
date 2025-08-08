package com.loopers.interfaces.api.productlike

import com.loopers.application.productlike.ProductLikeCriteria
import com.loopers.application.productlike.ProductLikeFacade
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/like/products")
class ProductLikeV1Controller(
    private val productLikeFacade: ProductLikeFacade,
) : ProductLikeV1ApiSpec {

    @PostMapping("{productId}")
    override fun like(@PathVariable("productId") productId: Long, httpServletRequest: HttpServletRequest) {
        val username = httpServletRequest.getHeader("X-USER-ID")
            ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.")
        return productLikeFacade.like(
            ProductLikeCriteria.Like(
                username,
                productId,
            ),
        )
    }

    @DeleteMapping("{productId}")
    override fun unlike(@PathVariable("productId") productId: Long, httpServletRequest: HttpServletRequest) {
        val username = httpServletRequest.getHeader("X-USER-ID")
            ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.")
        return productLikeFacade.unlike(
            ProductLikeCriteria.Unlike(
                username,
                productId,
            ),
        )
    }
}
