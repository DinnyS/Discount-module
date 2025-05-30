package serviceDiscount.Application.dto

import serviceDiscount.Application.Enum.DiscountType

data class DiscountByPointOnTopDto(
        val point: Int,
        val discountType: DiscountType = DiscountType.ON_TOP
) : CampaignDto