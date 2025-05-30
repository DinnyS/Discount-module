package serviceDiscount.Application.Controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import serviceDiscount.Application.dto.CalculateDiscountRequestDto
import serviceDiscount.Application.dto.calculateDiscointResponseDto
import serviceDiscount.Application.service.CalculatePriceService

@RestController
@RequestMapping("/api/calculates")
class CalculatePriceController(
        val calculatePrice: CalculatePriceService
) {

    @PostMapping("/discount")
    fun calculateDiscount(@RequestBody calculateDiscountRequestDto: CalculateDiscountRequestDto): calculateDiscointResponseDto {
        return calculatePrice.calculateDiscount(calculateDiscountRequestDto)
    }
}