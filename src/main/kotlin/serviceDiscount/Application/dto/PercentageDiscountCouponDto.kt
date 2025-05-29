package serviceDiscount.Application.dto

import serviceDiscount.Application.Enum.DiscountType

data class PercentageDiscountCouponDto(
        val percentage: Int,
        val discountType: DiscountType = DiscountType.COUPON
) : CampaignDto
