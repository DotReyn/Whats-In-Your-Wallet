package dot.reyn.whatsinyourwallet.api

data class Currency(
    val id: String,
    val name: String,
    val defaultBalance: Long,
    val maxBalance: Long,
    val default: Boolean = false
)
