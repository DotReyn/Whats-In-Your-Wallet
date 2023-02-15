package dot.reyn.whatsinyourwallet.extensions

import dot.reyn.whatsinyourwallet.api.CurrencyAPI
import dot.reyn.whatsinyourwallet.api.Currency
import dot.reyn.whatsinyourwallet.api.transaction.Transaction
import dot.reyn.whatsinyourwallet.api.transaction.TransactionResult
import net.minecraft.server.network.ServerPlayerEntity

fun ServerPlayerEntity.getBalance(
    currency: Currency? = CurrencyAPI.getInstance().getDefaultCurrency()): Int {
    // Use default currency if none is specified and null check for its existence
    if (currency == null) {
        return 0
    }
    return CurrencyAPI.getInstance().getBalance(this, currency)
}

fun <T, F> ServerPlayerEntity.transaction(transaction: Transaction<T, F>): TransactionResult<T, F> {
    return CurrencyAPI.getInstance().runTransaction(this, transaction)
}