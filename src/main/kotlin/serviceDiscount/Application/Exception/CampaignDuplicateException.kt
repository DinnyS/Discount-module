package serviceDiscount.Application.Exception

class CampaignDuplicateException() : RuntimeException("There are same campaigns in the same category.") {
}