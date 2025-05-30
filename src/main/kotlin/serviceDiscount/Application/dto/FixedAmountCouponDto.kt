package serviceDiscount.Application.dto

import serviceDiscount.Application.Enum.DiscountType

data class FixedAmountCouponDto(
        val amount: Double,
        val discountType: DiscountType = DiscountType.COUPON,
) : CampaignDto