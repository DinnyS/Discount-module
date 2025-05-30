package serviceDiscount.Application.dto

import kotlin.reflect.jvm.internal.impl.utils.SmartSet.Companion

data class calculateDiscointResponseDto(
        val totalPrice: Double,
        val totalOfCategoryDiscount: Double?,
        val twentyOfTotal: Int?,
        val userPointUse: Int?,
        val couponDiscount: Double?,
        val onTopDiscount: Double?,
        val seasonalDiscount: Double?,
        val totalDiscount: Double,
        val finalPrice: Double
)
