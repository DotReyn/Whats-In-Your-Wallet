package dot.reyn.whatsinyourwallet.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import dot.reyn.whatsinyourwallet.api.Currency
import dot.reyn.whatsinyourwallet.api.CurrencyAPI
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object PayCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal<ServerCommandSource>("pay")
                .requires(Permissions.require("whatsinyourwallet.balance.pay", 4))
                .then(
                    argument<ServerCommandSource, EntitySelector>("receiver", EntityArgumentType.player())
                        .then(
                            argument<ServerCommandSource, String>("currency", StringArgumentType.string())
                                .then(
                                    argument<ServerCommandSource, Int>("amount", IntegerArgumentType.integer())
                                        .executes(PayCommand::pay)
                                )
                        )
                        .then(
                            argument<ServerCommandSource, Int>("amount", IntegerArgumentType.integer())
                                .executes(PayCommand::pay)
                        )
                )
        )
    }

    private fun pay(ctx: CommandContext<ServerCommandSource>): Int {
        val amount = IntegerArgumentType.getInteger(ctx, "amount")
        if (amount < 1) {
            ctx.source.sendError(Text.literal("Amount must be greater than 0!"))
            return Command.SINGLE_SUCCESS
        }

        val player = ctx.source.player ?: return Command.SINGLE_SUCCESS
        val receivingPlayer = EntityArgumentType.getPlayer(ctx, "receiver")

        if (player.uuid == receivingPlayer.uuid) {
            ctx.source.sendError(Text.literal("You can't pay yourself!"))
            return Command.SINGLE_SUCCESS
        }

        var currency: Currency? = CurrencyAPI.getInstance().getDefaultCurrency()
        try {
            val id = StringArgumentType.getString(ctx, "currency")
            currency = CurrencyAPI.getInstance().getCurrency(id)
        } catch (_: Exception) {

        }

        if (currency == null) {
            ctx.source.sendError(Text.literal("Specified currency or default currency not found!"))
            return Command.SINGLE_SUCCESS
        }

        // TODO: Implement payment logic
        return Command.SINGLE_SUCCESS
    }
}