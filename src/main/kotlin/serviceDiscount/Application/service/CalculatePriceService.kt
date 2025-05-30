package serviceDiscount.Application.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import serviceDiscount.Application.Exception.CampaignDuplicateException
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
                    println("============== ERROR ============")
                    throw CampaignDuplicateException()
                }
            }
        }
        if (checkDiscountResult.onTop != null) {
            when (checkDiscountResult.onTop) {
                is PercentageCategoryDiscountOnTopDto -> {

                    categoryTotalPrice = 0.0

                    request.cart.forEach {
                        if (checkDiscountResult.onTop.category == it.category) {
                            categoryTotalPrice = categoryTotalPrice!! + (it.price * it.quantity)
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
                    println("============== ERROR ============")
                    throw CampaignDuplicateException()
                }
            }
        }
        if (checkDiscountResult.seasonal != null) {
            when (checkDiscountResult.seasonal) {
                is SpecialCampaignsSeasonalDto -> {
                    seasonalDiscount = (totalPrice / checkDiscountResult.seasonal.every).toInt() * checkDiscountResult.seasonal.discount
                }

                else -> {
                    println("============== ERROR ============")
                    throw CampaignDuplicateException()
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
                    if (campaign.amount < 0){
                        println("Amount must must Not Less than 0")
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must must Not Less than 0")
                    }
                }

                is PercentageDiscountCouponDto -> {
                    couponTypeCount++
                    couponTypeData = campaign
                    if (campaign.percentage < 0 || campaign.percentage > 100){
                        println("Percentage must Not Less than 0 Or More than 100")
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "percentage must Not Less than 0 Or More than 100")
                    }
                }

                is PercentageCategoryDiscountOnTopDto -> {
                    onTopTypeCount++
                    onTopTypeData = campaign
                    if (campaign.percentage < 0 || campaign.percentage > 100){
                        println("Percentage must Not Less than 0 Or More than 100")
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "percentage must Not Less than 0 Or More than 100")
                    }
                }

                is DiscountByPointOnTopDto -> {
                    onTopTypeCount++
                    onTopTypeData = campaign
                    if (campaign.point < 0 ){
                        println("Point must Not Less than 0")
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "every And discount must Not Less than 0")
                    }
                }

                is SpecialCampaignsSeasonalDto -> {
                    seasonalTypeCount++
                    seasonalTypeData = campaign
                    if (campaign.every < 0 || campaign.discount < 0){
                        println("Every And Discount must Not Less than 0")
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Point And discount must Not Less than 0")
                    }
                }
            }
        }
        if (couponTypeCount > 1 || onTopTypeCount > 1 || seasonalTypeCount > 1) {
            println("============== ERROR ============")
            throw CampaignDuplicateException()
        }

        return checkDiscountResultDto(
                coupon = couponTypeData,
                onTop = onTopTypeData,
                seasonal = seasonalTypeData
        )
    }

}