package dot.reyn.whatsinyourwallet.integration.cobblemon

import com.cobblemon.mod.common.Cobblemon
import dot.reyn.whatsinyourwallet.api.wallet.WalletProvider
import net.minecraft.server.network.ServerPlayerEntity

class CobblemonProvider: WalletProvider<ServerPlayerEntity> {

    override fun name(): String {
        return "cobblemon"
    }

    override fun getHolderClass(): Class<ServerPlayerEntity> {
        return ServerPlayerEntity::class.java
    }

    override fun provideWallet(holder: ServerPlayerEntity): CobblemonPlayerWallet {
        val cobbleData = Cobblemon.playerData.get(holder)
        var walletExtension = cobbleData.extraData[CobblemonPlayerWallet.NAME_KEY]

        // Create a new wallet if one doesn't exist
        if (walletExtension == null) {
            walletExtension = CobblemonPlayerWallet()
            cobbleData.extraData[CobblemonPlayerWallet.NAME_KEY] = walletExtension
            return walletExtension
        }
        // Technically unsafe cast but some other mod would have had to use our name key
        return walletExtension as CobblemonPlayerWallet
    }

}