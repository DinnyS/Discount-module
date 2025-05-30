package serviceDiscount.Application.Service

import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import serviceDiscount.Application.Exception.CampaignDuplicateException

import serviceDiscount.Application.dto.*
import serviceDiscount.Application.service.CalculatePriceService
import kotlin.test.assertEquals

class CalculatePriceServiceTest {
    private val calculatePriceService = spyk(CalculatePriceService())

    private val cart = listOf(
            ShopItem(name = "T-Shirt", category = "Clothing", price = 350.0, quantity = 2),
            ShopItem(name = "Watch", category = "Accessories", price = 850.0, quantity = 1)
    )

    private val requestDto = CalculateDiscountRequestDto(cart = cart, campaigns = emptyList())

    private val result = calculateDiscointResponseDto(
            totalPrice = 1550.0,
            totalOfCategoryDiscount = null,
            twentyOfTotal = 310,
            userPointUse = null,
            couponDiscount = 0.0,
            onTopDiscount = 0.0,
            seasonalDiscount = 0.0,
            totalDiscount = 0.0,
            finalPrice = 1550.0)


    val checkDiscountResult = checkDiscountResultDto(coupon = null, onTop = null, seasonal = null)

    @Test
    fun `Get total price from cart`() {

        val expectResult = result

        every { calculatePriceService.checkDiscount(requestDto) } returns checkDiscountResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.checkDiscount(requestDto) }
    }

    @Test
    fun `Get total price from cart and FixedAmountCoupon`() {

        val checkDiscountResult = checkDiscountResultDto(
                coupon = FixedAmountCouponDto(amount = 50.0),
                onTop = null,
                seasonal = null)

        val expectResult = result.copy(
                totalPrice = 1550.0,
                totalOfCategoryDiscount = null,
                twentyOfTotal = 310,
                userPointUse = null,
                couponDiscount = 50.0,
                onTopDiscount = 0.0,
                seasonalDiscount = 0.0,
                totalDiscount = 50.0,
                finalPrice = 1500.0)

        every { calculatePriceService.checkDiscount(requestDto) } returns checkDiscountResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.checkDiscount(requestDto) }
    }

    @Test
    fun `Get total price from cart and PercentageDiscountCoupon`() {

        val checkDiscountResult = checkDiscountResultDto(
                coupon = PercentageDiscountCouponDto(percentage = 50),
                onTop = null,
                seasonal = null)

        val expectResult = result.copy(
                totalPrice = 1550.0,
                totalOfCategoryDiscount = null,
                twentyOfTotal = 310,
                userPointUse = null,
                couponDiscount = 775.0,
                onTopDiscount = 0.0,
                seasonalDiscount = 0.0,
                totalDiscount = 775.0,
                finalPrice = 775.0)

        every { calculatePriceService.checkDiscount(requestDto) } returns checkDiscountResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.checkDiscount(requestDto) }
    }

    @Test
    fun `Get total price from cart and PercentageCategoryDiscountOnTopDto`() {

        val checkDiscountResult = checkDiscountResultDto(
                coupon = null,
                onTop = PercentageCategoryDiscountOnTopDto(category = "Clothing", percentage = 50.0),
                seasonal = null)

        val expectResult = result.copy(
                totalPrice = 1550.0,
                totalOfCategoryDiscount = 700.0,
                twentyOfTotal = 310,
                userPointUse = null,
                couponDiscount = 0.0,
                onTopDiscount = 350.0,
                seasonalDiscount = 0.0,
                totalDiscount = 350.0,
                finalPrice = 1200.0)

        every { calculatePriceService.checkDiscount(requestDto) } returns checkDiscountResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.checkDiscount(requestDto) }
    }

    @Test
    fun `Get total price from cart and DiscountByPointOnTopDto`() {

        val checkDiscountResult = checkDiscountResultDto(
                coupon = null,
                onTop = DiscountByPointOnTopDto(point = 300),
                seasonal = null)

        val expectResult = result.copy(
                totalPrice = 1550.0,
                totalOfCategoryDiscount = null,
                twentyOfTotal = 310,
                userPointUse = null,
                couponDiscount = 0.0,
                onTopDiscount = 300.0,
                seasonalDiscount = 0.0,
                totalDiscount = 300.0,
                finalPrice = 1250.0)

        every { calculatePriceService.checkDiscount(requestDto) } returns checkDiscountResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.checkDiscount(requestDto) }
    }

    @Test
    fun `Get total price from cart and SpecialCampaignsSeasonalDto`() {

        val checkDiscountResult = checkDiscountResultDto(
                coupon = null,
                onTop = null,
                seasonal = SpecialCampaignsSeasonalDto(every = 500.0, discount = 10.0))

        val expectResult = result.copy(
                totalPrice = 1550.0,
                totalOfCategoryDiscount = null,
                twentyOfTotal = 310,
                userPointUse = null,
                couponDiscount = 0.0,
                onTopDiscount = 0.0,
                seasonalDiscount = 30.0,
                totalDiscount = 30.0,
                finalPrice = 1520.0)

        every { calculatePriceService.checkDiscount(requestDto) } returns checkDiscountResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.checkDiscount(requestDto) }
    }

    @Test
    fun `Get total price from cart and 3 campaign not same category`() {

        val checkDiscountResult = checkDiscountResultDto(
                coupon = FixedAmountCouponDto(amount = 50.0),
                onTop = PercentageCategoryDiscountOnTopDto(category = "Clothing", percentage = 50.0),
                seasonal = SpecialCampaignsSeasonalDto(every = 500.0, discount = 10.0))

        val expectResult = result.copy(
                totalPrice = 1550.0,
                totalOfCategoryDiscount = 700.0,
                twentyOfTotal = 310,
                userPointUse = null,
                couponDiscount = 50.0,
                onTopDiscount = 350.0,
                seasonalDiscount = 30.0,
                totalDiscount = 430.0,
                finalPrice = 1120.0)

        every { calculatePriceService.checkDiscount(requestDto) } returns checkDiscountResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.checkDiscount(requestDto) }
    }

    @Test
    fun `have same category campaign And Throw exception`() {
        every { calculatePriceService.calculateDiscount(requestDto) } throws CampaignDuplicateException()

        assertThrows<RuntimeException> {
            throw RuntimeException("There are same campaigns in the same category.")
        }
    }

}