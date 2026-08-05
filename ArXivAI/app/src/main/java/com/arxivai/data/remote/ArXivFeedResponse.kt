package com.arxivai.data.remote

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.NamespaceList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "feed", strict = false)
@NamespaceList(
    Namespace(prefix = "arxiv", reference = "http://arxiv.org/schemas/atom")
)
data class ArXivFeedResponse(
    @field:ElementList(inline = true, required = false)
    var entries: List<ArXivEntry>? = null,

    @field:Element(name = "totalResults", required = false)
    @org.simpleframework.xml.Path("opensearch:totalResults")
    var totalResults: Int = 0,

    @field:Element(name = "startIndex", required = false)
    @org.simpleframework.xml.Path("opensearch:startIndex")
    var startIndex: Int = 0,

    @field:Element(name = "itemsPerPage", required = false)
    @org.simpleframework.xml.Path("opensearch:itemsPerPage")
    var itemsPerPage: Int = 0
)

@Root(name = "entry", strict = false)
data class ArXivEntry(
    @field:Element(name = "id", required = false)
    var id: String = "",

    @field:Element(name = "title", required = false)
    var title: String = "",

    @field:Element(name = "summary", required = false)
    var summary: String = "",

    @field:ElementList(name = "author", inline = true, required = false)
    var authors: List<ArXivAuthor>? = null,

    @field:Element(name = "published", required = false)
    var published: String = "",

    @field:Element(name = "updated", required = false)
    var updated: String = "",

    @field:Element(name = "comment", required = false)
    @org.simpleframework.xml.Namespace(prefix = "arxiv")
    var comment: String? = null,

    @field:ElementList(name = "category", inline = true, required = false)
    @org.simpleframework.xml.Namespace(prefix = "arxiv")
    var categories: List<ArXivCategory>? = null,

    @field:ElementList(name = "link", inline = true, required = false)
    var links: List<ArXivLink>? = null
) {
    fun getPdfUrl(): String {
        return links?.find { it.title == "pdf" }?.href
            ?: links?.find { it.rel == "alternate" }?.href?.replace("abs", "pdf")
            ?: ""
    }

    fun getAbstractUrl(): String {
        return links?.find { it.rel == "alternate" }?.href ?: ""
    }

    fun extractArxivId(): String {
        // Extract arXiv ID from URL like http://arxiv.org/abs/2301.12345v1
        val url = id
        return url.substringAfterLast("/").substringBefore("v")
    }

    fun getCategoriesString(): String {
        return categories?.joinToString(", ") { it.term } ?: ""
    }

    fun getAuthorsString(): String {
        return authors?.joinToString(", ") { it.name } ?: ""
    }
}

@Root(name = "author", strict = false)
data class ArXivAuthor(
    @field:Element(name = "name", required = false)
    var name: String = ""
)

@Root(name = "category", strict = false)
data class ArXivCategory(
    @field:Attribute(name = "term", required = false)
    var term: String = ""
)

@Root(name = "link", strict = false)
data class ArXivLink(
    @field:Attribute(name = "href", required = false)
    var href: String = "",

    @field:Attribute(name = "rel", required = false)
    var rel: String = "",

    @field:Attribute(name = "title", required = false)
    var title: String? = null
)