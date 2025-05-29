package serviceDiscount.Application.dto

import serviceDiscount.Application.Enum.DiscountType

data class PercentageCategoryDiscountOnTopDto(
        val category: String,
        val percentage: Double,
        val discountType: DiscountType = DiscountType.ON_TOP
) : CampaignDto
