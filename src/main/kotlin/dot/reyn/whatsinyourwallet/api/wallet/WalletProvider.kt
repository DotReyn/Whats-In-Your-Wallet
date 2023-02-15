package dot.reyn.whatsinyourwallet.api.wallet

interface WalletProvider<H> {

    fun name(): String

    fun getHolderClass(): Class<H>

    fun provideWallet(holder: H): Wallet

}