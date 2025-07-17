package com.loopers.interfaces.api.point

import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/points")
class PointV1Controller : PointV1ApiSpec {

    @GetMapping("")
    override fun getPoint(httpServletRequest: HttpServletRequest): ApiResponse<PointV1Dto.PointResponse> {
        val userId = httpServletRequest.getHeader("X-USER-ID")
            ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.")
        return PointV1Dto.PointResponse.from(userId, 1000L)
            .let { ApiResponse.success(it) }
    }
}
