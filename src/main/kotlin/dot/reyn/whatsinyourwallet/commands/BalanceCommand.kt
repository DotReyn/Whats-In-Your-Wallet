package dot.reyn.whatsinyourwallet.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import dot.reyn.whatsinyourwallet.extensions.smallCaps
import dot.reyn.whatsinyourwallet.WhatsInYourWallet
import dot.reyn.whatsinyourwallet.api.Currency
import dot.reyn.whatsinyourwallet.api.CurrencyAPI
import dot.reyn.whatsinyourwallet.extensions.getBalance
import dot.reyn.whatsinyourwallet.extensions.message
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object BalanceCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal<ServerCommandSource>("balance")
                .requires(Permissions.require("whatsinyourwallet.balance", 0))
                .then(
                    argument<ServerCommandSource, String>("currency", StringArgumentType.string())
                        .executes(BalanceCommand::balanceSelf)
                )
                .executes(BalanceCommand::balanceSelf)
        )
    }

    private fun balanceSelf(ctx: CommandContext<ServerCommandSource>): Int {
        val player = ctx.source.player ?: return Command.SINGLE_SUCCESS
        val query = this.queryBalanceFor(player, ctx) ?: return Command.SINGLE_SUCCESS
        val formattedBalance = "%,d".format(query.second)
        val audience = WhatsInYourWallet.platform!!.player(player.uuid)
        audience.message("&#ef9f76balance".smallCaps() + " &#51576d> &#f2d5cfYou have $formattedBalance ${query.first.name}")
        return Command.SINGLE_SUCCESS
    }

    private fun queryBalanceFor(player: ServerPlayerEntity, ctx: CommandContext<ServerCommandSource>): Pair<Currency, Int>? {
        var currency: Currency? = CurrencyAPI.getInstance().getDefaultCurrency()
        try {
            val id = StringArgumentType.getString(ctx, "currency")
            currency = CurrencyAPI.getInstance().getCurrency(id)
        } catch (_: Exception) {

        }

        if (currency == null) {
            ctx.source.sendError(Text.literal("Specified currency or default currency not found!"))
            return null
        }
        return Pair(currency, player.getBalance(currency))
    }

}