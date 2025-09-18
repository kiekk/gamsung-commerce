package com.loopers.interfaces.web.productrank

import com.loopers.application.productrank.ProductRankCriteria
import com.loopers.application.productrank.ProductRankFacade
import com.loopers.support.enums.rank.RankType
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDateTime

@Controller
@RequestMapping("/rankings")
class ProductRankController(
    private val productRankFacade: ProductRankFacade,
) {

    @GetMapping("daily")
    fun dailyRanking(model: Model): String {
        val today = LocalDateTime.now()
        val products = productRankFacade.getProductRanks(
            ProductRankCriteria.Search(
                today,
                RankType.DAILY,
            ),
            PageRequest.of(0, 20),
        )
        model.addAttribute("rankingDate", today)
        model.addAttribute("items", products.content)
        return "product-rank/daily"
    }

    @GetMapping("daily-refresh")
    fun dailyRankingRefresh(model: Model): String {
        val today = LocalDateTime.now()
        val products = productRankFacade.getProductRanks(
            ProductRankCriteria.Search(
                today,
                RankType.DAILY,
            ),
            PageRequest.of(0, 20),
        )
        model.addAttribute("rankingDate", today)
        model.addAttribute("items", products.content)
        return "product-rank/daily-list :: product-ranking-auto"
    }

    @GetMapping("hourly")
    fun hourlyRanking(model: Model): String {
        val today = LocalDateTime.now()
        val products = productRankFacade.getProductRanks(
            ProductRankCriteria.Search(
                today,
                RankType.HOURLY,
            ),
            PageRequest.of(0, 20),
        )
        model.addAttribute("rankingDate", today)
        model.addAttribute("items", products.content)
        return "product-rank/hourly"
    }

    @GetMapping("hourly-refresh")
    fun hourlyRankingRefresh(model: Model): String {
        val today = LocalDateTime.now()
        val products = productRankFacade.getProductRanks(
            ProductRankCriteria.Search(
                today,
                RankType.HOURLY,
            ),
            PageRequest.of(0, 20),
        )
        model.addAttribute("rankingDate", today)
        model.addAttribute("items", products.content)
        return "product-rank/hourly-list :: product-ranking-auto"
    }
}
