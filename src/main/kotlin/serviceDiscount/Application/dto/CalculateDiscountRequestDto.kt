package serviceDiscount.Application.dto

data class CalculateDiscountRequestDto(
        val cart: List<ShopItem>,
        val campaigns: List<CampaignDto>
)
