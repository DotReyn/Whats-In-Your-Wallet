package dot.reyn.whatsinyourwallet.api.transaction

import dot.reyn.whatsinyourwallet.api.Currency

class TransactionResult<T, F>(
    val to: T,
    val from: F? = null,
    val transactionType: TransactionType,
    val resultType: TransactionResultType,
    val currency: Currency,
    val previousBalance: Int,
    val newBalance: Int,
    val amount: Int,
    val details: String = "No details provided."
) {
    override fun toString(): String {
        return "TransactionResult(from=$from, to=$to, transactionType=$transactionType, resultType=$resultType, currency=$currency, previousBalance=$previousBalance, newBalance=$newBalance, amount=$amount, details='$details')"
    }
}
