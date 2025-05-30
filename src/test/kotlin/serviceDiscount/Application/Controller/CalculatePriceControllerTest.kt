package serviceDiscount.Application.Controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import serviceDiscount.Application.Exception.CampaignDuplicateException
import serviceDiscount.Application.dto.*

import serviceDiscount.Application.service.CalculatePriceService
import kotlin.test.assertEquals

class CalculatePriceControllerTest{
    private val calculatePriceService = mockk<CalculatePriceService>()

    @Test
    fun `Get total price from cart`() {

        val cart = listOf(
                ShopItem(name = "T-Shirt", category = "Clothing", price = 350.0, quantity = 2),
                ShopItem(name = "Watch", category = "Accessories", price = 850.0, quantity = 1)
        )

        val requestDto = CalculateDiscountRequestDto(cart = cart, campaigns = emptyList())

        val result = calculateDiscointResponseDto(
                totalPrice = 1550.0,
                totalOfCategoryDiscount = null,
                twentyOfTotal = 310,
                userPointUse = null,
                couponDiscount = 0.0,
                onTopDiscount = 0.0,
                seasonalDiscount = 0.0,
                totalDiscount = 0.0,
                finalPrice = 1550.0)

        val expectResult = result

        every { calculatePriceService.calculateDiscount(requestDto) } returns expectResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.calculateDiscount(requestDto) }
    }

    @Test
    fun `Get total price from cart and FixedAmountCoupon`() {

        val cart = listOf(
                ShopItem(name = "T-Shirt", category = "Clothing", price = 350.0, quantity = 2),
                ShopItem(name = "Watch", category = "Accessories", price = 850.0, quantity = 1)
        )

        val campaigns = listOf(FixedAmountCouponDto(
                amount = 100.0
        ))

        val requestDto = CalculateDiscountRequestDto(cart = cart, campaigns = campaigns)

        val result = calculateDiscointResponseDto(totalPrice = 1550.0,
                totalOfCategoryDiscount = null,
                twentyOfTotal = 310,
                userPointUse = null,
                couponDiscount = 0.0,
                onTopDiscount = 0.0,
                seasonalDiscount = 0.0,
                totalDiscount = 0.0,
                finalPrice = 1550.0)


        val expectResult = result

        every { calculatePriceService.calculateDiscount(requestDto) } returns expectResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.calculateDiscount(requestDto) }
    }

    @Test
    fun `Get total price from cart and PercentageDiscountCouponDto`() {

        val cart = listOf(
                ShopItem(name = "T-Shirt", category = "Clothing", price = 350.0, quantity = 2),
                ShopItem(name = "Watch", category = "Accessories", price = 850.0, quantity = 1)
        )

        val campaigns = listOf(PercentageDiscountCouponDto(
                percentage = 50
        ))

        val requestDto = CalculateDiscountRequestDto(cart = cart, campaigns = campaigns)

        val result = calculateDiscointResponseDto(totalPrice = 1550.0,
                totalOfCategoryDiscount = null,
                twentyOfTotal = 310,
                userPointUse = null,
                couponDiscount = 775.0,
                onTopDiscount = 0.0,
                seasonalDiscount = 0.0,
                totalDiscount = 0.0,
                finalPrice = 775.0)


        val expectResult = result

        every { calculatePriceService.calculateDiscount(requestDto) } returns expectResult

        val actualResult = calculatePriceService.calculateDiscount(requestDto)

        assertEquals(expectResult, actualResult)

        verify(exactly = 1) { calculatePriceService.calculateDiscount(requestDto) }
    }

    @Test
    fun `throw exception when have same campaign`() {

        val cart = listOf(
                ShopItem(name = "T-Shirt", category = "Clothing", price = 350.0, quantity = 2),
                ShopItem(name = "Watch", category = "Accessories", price = 850.0, quantity = 1)
        )

        val campaigns = listOf(PercentageDiscountCouponDto(
                percentage = 50,),
                PercentageDiscountCouponDto(
                percentage = 50
        ))

        val requestDto = CalculateDiscountRequestDto(cart = cart, campaigns = campaigns)

        every { calculatePriceService.calculateDiscount(requestDto) } throws CampaignDuplicateException()

        assertThrows<RuntimeException> {
            throw RuntimeException("There are same campaigns in the same category.")
        }
    }
}