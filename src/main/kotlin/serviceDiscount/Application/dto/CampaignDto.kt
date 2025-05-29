package serviceDiscount.Application.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes(
        JsonSubTypes.Type(value = FixedAmountCouponDto::class, name = "FixedAmountCoupon"),
        JsonSubTypes.Type(value = PercentageDiscountCouponDto::class, name = "PercentageCoupon"),
        JsonSubTypes.Type(value = PercentageCategoryDiscountOnTopDto::class, name = "OnTopPercentageDiscount"),
        JsonSubTypes.Type(value = DiscountByPointOnTopDto::class, name = "OnTopDiscountByPoint"),
        JsonSubTypes.Type(value = SpecialCampaignsSeasonalDto::class, name = "SeasonalSpecialCampaigns")
)
sealed interface CampaignDto