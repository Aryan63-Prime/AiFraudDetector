package com.aifraud.admin.model

data class PagedResponse<T>(
    val content: List<T>,
    val pageable: PageableResponse?,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: SortResponse?,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)

data class PageableResponse(
    val pageNumber: Int?,
    val pageSize: Int?,
    val offset: Long?,
    val paged: Boolean?,
    val unpaged: Boolean?,
    val sort: SortResponse?
)

data class SortResponse(
    val empty: Boolean?,
    val sorted: Boolean?,
    val unsorted: Boolean?
)
