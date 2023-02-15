package dot.reyn.whatsinyourwallet

import com.cobblemon.mod.common.api.storage.player.PlayerDataExtensionRegistry
import com.google.gson.GsonBuilder
import dot.reyn.whatsinyourwallet.api.CurrencyAPI
import dot.reyn.whatsinyourwallet.api.wallet.WalletProvider
import dot.reyn.whatsinyourwallet.commands.BalanceCommand
import dot.reyn.whatsinyourwallet.commands.BalanceOtherCommand
import dot.reyn.whatsinyourwallet.config.CurrencyConfig
import dot.reyn.whatsinyourwallet.integration.cobblemon.CobblemonProvider
import dot.reyn.whatsinyourwallet.integration.cobblemon.CobblemonPlayerWallet
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class WhatsInYourWallet : ModInitializer {

    private lateinit var config: CurrencyConfig

    companion object {
        var platform: FabricServerAudiences? = null
    }

    override fun onInitialize() {
        // Adventure platform
        ServerLifecycleEvents.SERVER_STARTING.register { platform = FabricServerAudiences.of(it) }
        ServerLifecycleEvents.SERVER_STOPPED.register { platform = null }

        // Setup config and reloading
        this.loadConfig()
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register { _, _ ->
            this.loadConfig()
            this.config.currencies.forEach { CurrencyAPI.getInstance().registerCurrency(it) }
        }

        // Register currencies
        val api = CurrencyAPI.getInstance()
        this.config.currencies.forEach { api.registerCurrency(it) }

        // Register Cobblemon integration
        PlayerDataExtensionRegistry.register("whatsinyourwallet", CobblemonPlayerWallet::class.java)
        api.registerProvider(CobblemonProvider() as WalletProvider<Any>)

        // Commands
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            BalanceCommand.register(dispatcher)
            BalanceOtherCommand.register(dispatcher)
        }
    }

    /**
     * Loads the configuration file.
     * If the config does not exist, it will be created.
     */
    private fun loadConfig() {
        val configDir = File("./config/")
        if (!configDir.exists()) {
            configDir.mkdirs()
        }

        val configFile = File(configDir, "whatsinyourwallet.json")
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

        if (!configFile.exists()) {
            this.config = CurrencyConfig()
            val fileWriter = FileWriter(configFile, Charsets.UTF_8)

            gson.toJson(this.config, fileWriter)

            fileWriter.flush()
            fileWriter.close()
        } else {
            val fileReader = FileReader(configFile, Charsets.UTF_8)
            this.config = gson.fromJson(fileReader, CurrencyConfig::class.java)
            fileReader.close()
        }
    }

}