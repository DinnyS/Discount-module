package serviceDiscount.Application.service

import org.springframework.stereotype.Service
import serviceDiscount.Application.dto.*

@Service
class CalculatePriceService {

    fun calculateDiscount(request: CalculateDiscountRequestDto): calculateDiscointResponseDto {

        val checkDiscountResult = checkDiscount(request)

        var categoryTotalPrice: Double? = null

        var couponDiscount = 0.0
        var onTopDiscount = 0.0
        var seasonalDiscount = 0.0
        val totalPrice: Double = request.cart.sumOf { it.price * it.quantity }
        val twentyOfTotal = (totalPrice * 0.2).toInt()
        var userPointUse: Int? = null

        if (checkDiscountResult.coupon != null) {
            couponDiscount = when (checkDiscountResult.coupon) {
                is FixedAmountCouponDto -> {
                    checkDiscountResult.coupon.amount
                }

                is PercentageDiscountCouponDto -> {
                    totalPrice * (checkDiscountResult.coupon.percentage.toDouble() / 100)
                }

                else -> {
                    throw RuntimeException()
                }
            }
        }
        if (checkDiscountResult.onTop != null) {
            when (checkDiscountResult.onTop) {
                is PercentageCategoryDiscountOnTopDto -> {

                    categoryTotalPrice = 0.0

                    request.cart.forEach {
                        if (checkDiscountResult.onTop.category == it.category) {
                            categoryTotalPrice = +it.price * it.quantity
                        }
                    }

                    onTopDiscount = categoryTotalPrice!! * (checkDiscountResult.onTop.percentage / 100)
                }

                is DiscountByPointOnTopDto -> {
                    onTopDiscount = if (checkDiscountResult.onTop.point >= twentyOfTotal) {
                        userPointUse = twentyOfTotal
                        twentyOfTotal.toDouble()
                    } else {
                        twentyOfTotal.toDouble() - (twentyOfTotal.toDouble() - checkDiscountResult.onTop.point)
                    }

                }

                else -> {
                    throw RuntimeException()
                }
            }
        }
        if (checkDiscountResult.seasonal != null) {
            when (checkDiscountResult.seasonal) {
                is SpecialCampaignsSeasonalDto -> {
                    seasonalDiscount = (totalPrice / checkDiscountResult.seasonal.every).toInt() * checkDiscountResult.seasonal.discount
                }

                else -> {
                    throw RuntimeException()
                }
            }
        }

        val totalDiscount = couponDiscount + onTopDiscount + seasonalDiscount
        val finalPrice = totalPrice - totalDiscount

        println("totalPrice ========== $totalPrice")
        println("totalDiscount ========== $totalDiscount")
        println("finalPrice ========== $finalPrice")

        return calculateDiscointResponseDto(
                totalPrice = totalPrice,
                totalOfCategoryDiscount = categoryTotalPrice,
                twentyOfTotal= twentyOfTotal,
                userPointUse= userPointUse,
                couponDiscount= couponDiscount,
                onTopDiscount= onTopDiscount,
                seasonalDiscount= seasonalDiscount,
                totalDiscount= totalDiscount,
                finalPrice = finalPrice,

        )
    }

    fun checkDiscount(request: CalculateDiscountRequestDto): checkDiscountResultDto {
        var couponTypeCount = 0
        var onTopTypeCount = 0
        var seasonalTypeCount = 0

        var couponTypeData: CampaignDto? = null
        var onTopTypeData: CampaignDto? = null
        var seasonalTypeData: CampaignDto? = null

        request.campaigns.forEach { campaign ->
            when (campaign) {
                is FixedAmountCouponDto -> {
                    couponTypeCount++
                    couponTypeData = campaign
                }

                is PercentageDiscountCouponDto -> {
                    couponTypeCount++
                    couponTypeData = campaign
                }

                is PercentageCategoryDiscountOnTopDto -> {
                    onTopTypeCount++
                    onTopTypeData = campaign
                }

                is DiscountByPointOnTopDto -> {
                    onTopTypeCount++
                    onTopTypeData = campaign
                }

                is SpecialCampaignsSeasonalDto -> {
                    seasonalTypeCount++
                    seasonalTypeData = campaign
                }
            }
        }
        if (couponTypeCount > 1 || onTopTypeCount > 1 || seasonalTypeCount > 1) {
            throw RuntimeException()
        }

        return checkDiscountResultDto(
                coupon = couponTypeData,
                onTop = onTopTypeData,
                seasonal = seasonalTypeData
        )
    }

}