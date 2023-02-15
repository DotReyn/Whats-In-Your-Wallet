package dot.reyn.whatsinyourwallet.api.transaction

enum class TransactionResultType {
    SUCCESS,
    NO_PROVIDER,
    INSUFFICIENT_BALANCE,
    INVALID_AMOUNT,
    RECEIVER_TRANSACTION_FAILED,
    RECEIVER_NO_WALLET,
    MAX_BALANCE_EXCEEDED,
    CANCELLED,
}