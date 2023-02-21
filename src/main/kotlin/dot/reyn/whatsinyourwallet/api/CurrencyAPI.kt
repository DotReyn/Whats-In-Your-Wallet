package dot.reyn.whatsinyourwallet.api

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import dot.reyn.whatsinyourwallet.api.transaction.Transaction
import dot.reyn.whatsinyourwallet.api.transaction.TransactionResult
import dot.reyn.whatsinyourwallet.api.transaction.TransactionResultType
import dot.reyn.whatsinyourwallet.api.wallet.WalletProvider
import eu.pb4.placeholders.api.PlaceholderResult
import eu.pb4.placeholders.api.Placeholders
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

class CurrencyAPI {

    private val currencies = mutableMapOf<String, Currency>()
    private val providers = mutableListOf<WalletProvider<Any>>()

    companion object {
        private lateinit var instance: CurrencyAPI

        fun getInstance(): CurrencyAPI {
            if (!this::instance.isInitialized) {
                instance = CurrencyAPI()
            }
            return instance
        }
    }

    fun registerCurrency(currency: Currency) {
        currencies[currency.id] = currency

        Placeholders.register(Identifier("whatsinyourwallet", currency.id)) { ctx, _ ->
            if (!ctx.hasPlayer()) {
                return@register PlaceholderResult.invalid("No player!")
            }
            val amount = this.getBalance(ctx.player!!, currency)
            return@register PlaceholderResult.value("%,d".format(amount))
        }
    }

    fun getCurrency(id: String): Currency? {
        return currencies[id]
    }

    fun getCurrencies(): Collection<Currency> {
        return currencies.values
    }

    fun registerProvider(provider: WalletProvider<Any>) {
        providers.add(provider)
    }

    fun getDefaultCurrency(): Currency? {
        return currencies.values.firstOrNull { it.default }
    }

    fun getBalance(holder: Any, currency: Currency): Int {
        for (provider in providers) {
            if (!provider.getHolderClass().isInstance(holder)) {
                continue
            }
            return provider.provideWallet(holder).getBalance(currency)
        }
        return 0
    }

    fun <T, F> runTransaction(holder: Any, transaction: Transaction<T, F>): TransactionResult<T, F> {
        for (provider in providers) {
            if (!provider.getHolderClass().isInstance(holder)) {
                continue
            }
            return provider.provideWallet(holder).runTransaction(transaction)
        }
        return transaction.failResult(TransactionResultType.NO_PROVIDER, 0)
    }

}