package dot.reyn.whatsinyourwallet.api.transaction

import dot.reyn.whatsinyourwallet.api.Currency

class Transaction<T, F>(
    val to: T,
    val from: F? = null,
    val transactionType: TransactionType,
    val currency: Currency,
    val amount: Int,
    val details: String = "No details provided."
) {
    fun successResult(previousBalance: Int, newBalance: Int): TransactionResult<T, F> {
        return TransactionResult(
            to = this.to,
            from = this.from,
            transactionType = this.transactionType,
            resultType = TransactionResultType.SUCCESS,
            currency = this.currency,
            previousBalance = previousBalance,
            newBalance = newBalance,
            amount = amount,
            details = this.details
        )
    }

    fun failResult(resultType: TransactionResultType, balance: Int): TransactionResult<T, F> {
        return TransactionResult(
            to = this.to,
            from = this.from,
            transactionType = this.transactionType,
            resultType = resultType,
            currency = this.currency,
            previousBalance = balance,
            newBalance = balance,
            amount = amount,
            details = this.details
        )
    }

    override fun toString(): String {
        return "Transaction(to=$to, from=$from, transactionType=$transactionType, currency=$currency, amount=$amount, details='$details')"
    }
}