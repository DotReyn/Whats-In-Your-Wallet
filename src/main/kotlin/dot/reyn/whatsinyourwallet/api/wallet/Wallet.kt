package dot.reyn.whatsinyourwallet.api.wallet

import dot.reyn.whatsinyourwallet.api.Currency
import dot.reyn.whatsinyourwallet.api.transaction.Transaction
import dot.reyn.whatsinyourwallet.api.transaction.TransactionResult
import dot.reyn.whatsinyourwallet.api.transaction.TransactionResultType
import dot.reyn.whatsinyourwallet.api.transaction.TransactionType

interface Wallet {

    fun getBalance(currency: Currency): Int

    fun setBalance(currency: Currency, amount: Int)

    fun <T, F> runTransaction(transaction: Transaction<T, F>): TransactionResult<T, F> {
        val balance = this.getBalance(transaction.currency)
        if (transaction.amount < 1) {
            return transaction.failResult(TransactionResultType.INVALID_AMOUNT, balance)
        }

        val updatedBalance = when (transaction.transactionType) {
            TransactionType.DEPOSIT -> balance + transaction.amount
            TransactionType.WITHDRAW -> balance - transaction.amount
            TransactionType.SET -> transaction.amount
        }

        if (updatedBalance < 0) {
            return transaction.failResult(TransactionResultType.INSUFFICIENT_BALANCE, balance)
        }

        if (updatedBalance > transaction.currency.maxBalance) {
            return transaction.failResult(TransactionResultType.MAX_BALANCE_EXCEEDED, balance)
        }

        // TODO: Post an event to see if the transaction should be attempted
        this.setBalance(transaction.currency, updatedBalance)
        return transaction.successResult(balance, updatedBalance)
    }

}