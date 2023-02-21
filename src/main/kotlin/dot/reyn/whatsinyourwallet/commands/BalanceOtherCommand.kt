package dot.reyn.whatsinyourwallet.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import dot.reyn.whatsinyourwallet.extensions.smallCaps
import dot.reyn.whatsinyourwallet.api.Currency
import dot.reyn.whatsinyourwallet.api.CurrencyAPI
import dot.reyn.whatsinyourwallet.extensions.getBalance
import dot.reyn.whatsinyourwallet.extensions.message
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object BalanceOtherCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal<ServerCommandSource>("balanceother")
                .requires(Permissions.require("whatsinyourwallet.balance.others", 4))
                .then(
                    argument<ServerCommandSource, EntitySelector>("player", EntityArgumentType.player())
                        .then(
                            argument<ServerCommandSource, String>("currency", StringArgumentType.string())
                                .executes(BalanceOtherCommand::balanceOther)
                        )
                        .executes(BalanceOtherCommand::balanceOther)
                )
        )
    }

    private fun balanceOther(ctx: CommandContext<ServerCommandSource>): Int {
        val player = ctx.source.player ?: return Command.SINGLE_SUCCESS
        val otherPlayer = EntityArgumentType.getPlayer(ctx, "player")
        val query = this.queryBalanceFor(otherPlayer, ctx) ?: return Command.SINGLE_SUCCESS
        val formattedBalance = "%,d".format(query.second)

        player.message("<color:#ef9f76>${"balance".smallCaps()}</color> <color:#51576d>></color> <color:#f2d5cf>${otherPlayer.gameProfile.name} has a balance of $formattedBalance ${query.first.name}</color>")
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