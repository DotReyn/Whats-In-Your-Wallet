package dot.reyn.whatsinyourwallet.integration.cobblemon

import com.cobblemon.mod.common.api.storage.player.PlayerDataExtension
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dot.reyn.whatsinyourwallet.api.Currency
import dot.reyn.whatsinyourwallet.api.wallet.Wallet

class CobblemonPlayerWallet(
    private val wallet: MutableMap<String, Int> = mutableMapOf()
): PlayerDataExtension, Wallet {

    companion object {
        const val NAME_KEY = "whatsinyourwallet"

        private val GSON = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()
    }

    override fun getBalance(currency: Currency): Int {
        return this.wallet[currency.id] ?: 0
    }

    override fun setBalance(currency: Currency, amount: Int) {
        this.wallet[currency.id] = amount
    }

    override fun name(): String {
        return "whatsinyourwallet"
    }

    override fun serialize(): JsonObject {
        val jsonObject = GSON.toJsonTree(this).asJsonObject
        jsonObject.addProperty(PlayerDataExtension.NAME_KEY, this.name())
        return jsonObject
    }

    override fun deserialize(json: JsonObject): PlayerDataExtension {
        return GSON.fromJson(json, CobblemonPlayerWallet::class.java)
    }
}