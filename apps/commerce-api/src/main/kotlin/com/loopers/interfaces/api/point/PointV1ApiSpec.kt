package com.loopers.interfaces.api.point

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest

@Tag(name = "Point V1 API", description = "포인트 API 입니다.")
interface PointV1ApiSpec {
    @Operation(
        summary = "포인트 조회",
        description = "내 포인트 조회",
    )
    fun getPoint(
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<PointV1Dto.PointResponse>
}
