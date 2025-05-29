package serviceDiscount.Application.dto

class checkDiscountResultDto (
        val coupon: CampaignDto?,
        val onTop: CampaignDto?,
        val seasonal: CampaignDto?,
)