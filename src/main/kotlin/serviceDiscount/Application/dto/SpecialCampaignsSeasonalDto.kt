package serviceDiscount.Application.dto

import serviceDiscount.Application.Enum.DiscountType

data class SpecialCampaignsSeasonalDto(
        val every: Double,
        val discount: Double,
        val discountType: DiscountType = DiscountType.SEASONAL
) : CampaignDto
