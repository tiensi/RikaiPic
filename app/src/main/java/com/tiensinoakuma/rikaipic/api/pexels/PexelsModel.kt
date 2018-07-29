package com.tiensinoakuma.rikaipic.api.pexels

data class PexelsResponse(val page: Int, val perPage: Int, val photos: List<PexelsPhoto>)

data class PexelsPhoto(val id: Int, val url: String, val photographer: String, val src: PexelsImageSource)

data class PexelsImageSource(val original: String, val tiny: String, val small: String, val medium: String)