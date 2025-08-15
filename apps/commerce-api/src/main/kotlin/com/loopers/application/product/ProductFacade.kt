package com.loopers.application.product

import com.loopers.domain.brand.BrandService
import com.loopers.domain.product.ProductService
import com.loopers.domain.product.query.ProductQueryService
import com.loopers.domain.product.query.ProductSearchCondition
import com.loopers.domain.productlike.ProductLikeService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.domain.user.UserService
import com.loopers.support.cache.CacheRepository
import com.loopers.support.cache.dto.ProductListPageCacheValue
import com.loopers.support.cache.policy.ProductListCachePolicy
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Component
class ProductFacade(
    private val productService: ProductService,
    private val productQueryService: ProductQueryService,
    private val stockService: StockService,
    private val brandService: BrandService,
    private val productLikeService: ProductLikeService,
    private val userService: UserService,
    private val cacheRepository: CacheRepository,
) {

    private val log = LoggerFactory.getLogger(ProductFacade::class.java)

    @Transactional
    fun createProduct(criteria: ProductCriteria.Create): ProductInfo.ProductResult {
        userService.findUserBy(criteria.username) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "사용자를 찾을 수 없습니다. username: ${criteria.username}",
        )
        val createdProduct = productService.createProduct(criteria.toCommand())
        val createdStock = stockService.createStock(StockCommand.Create(createdProduct.id, criteria.quantity ?: 0))
        return ProductInfo.ProductResult.from(createdProduct, createdStock)
    }

    @Transactional(readOnly = true)
    fun getProduct(id: Long): ProductInfo.ProductDetail {
        val product = productService.findProductBy(id) ?: throw CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다. $id")
        val brand = brandService.findBrandBy(product.brandId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "브랜드를 찾을 수 없습니다. ${product.brandId}",
        )
        val productLikeCount = productLikeService.getProductLikeCount(product.id)
        return ProductInfo.ProductDetail.from(product, brand, productLikeCount)
    }

    @Transactional(readOnly = true)
    fun searchProducts(condition: ProductSearchCondition, pageable: Pageable): Page<ProductInfo.ProductList> {
        // 페이지네이션과 정렬이 캐시 가능한지 확인
        val cacheable = ProductListCachePolicy.isCacheable(condition, pageable)
        val cacheKey = if (cacheable) {
            ProductListCachePolicy.buildCacheKey(pageable)
        } else null

        // 캐시 조회
        cacheKey?.let {
            cacheRepository.get(cacheKey, ProductListPageCacheValue::class.java)?.let { cached ->
                log.info("[Cache Hit] Product List: $it")
                return PageImpl(
                    cached.content.map { viewModel -> ProductInfo.ProductList.from(viewModel) },
                    pageable,
                    cached.totalElements,
                )
            }
        }

        // DB 조회
        val searchProductsPage = productQueryService.searchProducts(condition, pageable)
        val searchProductContentsMap = searchProductsPage.content.map(ProductInfo.ProductList::from)
        val pageInfo = PageImpl(searchProductContentsMap, pageable, searchProductsPage.totalElements)

        // 캐시 저장
        cacheKey?.let {
            log.info("[Cache Miss] Product List: $it")
            val data = ProductListPageCacheValue(
                searchProductsPage.content,
                pageable.pageNumber,
                pageable.pageSize,
                searchProductsPage.totalElements,
                ProductListCachePolicy.normalizeSort(pageable.sort),
            )
            cacheRepository.set(cacheKey, data)
        }

        return pageInfo
    }

    @Transactional(readOnly = true)
    fun searchProductsByCountQuery(pageable: Pageable): Page<ProductInfo.ProductList> {
        val searchProductsPage = productQueryService.searchProductsByCountQuery(pageable)
        val productList = searchProductsPage.content.map { productListViewModel ->
            ProductInfo.ProductList.from(productListViewModel)
        }
        return PageImpl(productList, pageable, searchProductsPage.totalElements)
    }
}
