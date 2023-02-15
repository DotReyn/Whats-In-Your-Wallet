package dot.reyn.whatsinyourwallet.config

import dot.reyn.whatsinyourwallet.api.Currency

data class CurrencyConfig(
    val currencies: List<Currency> = listOf(
        Currency(
            id = "pokedollar",
            name = "Pokédollars",
            defaultBalance = 0,
            maxBalance = Int.MAX_VALUE.toLong(),
            default = true
        )
    )
)
