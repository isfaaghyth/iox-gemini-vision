package domain.model

data class ContentModel(
    val text: String,
    val succeed: Boolean
) {

    companion object {
        fun empty() = ContentModel("", false)
    }
}