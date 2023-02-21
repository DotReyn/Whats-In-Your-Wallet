package dot.reyn.whatsinyourwallet.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import dot.reyn.whatsinyourwallet.WhatsInYourWallet
import dot.reyn.whatsinyourwallet.api.Currency
import dot.reyn.whatsinyourwallet.api.CurrencyAPI
import dot.reyn.whatsinyourwallet.api.transaction.Transaction
import dot.reyn.whatsinyourwallet.api.transaction.TransactionResultType
import dot.reyn.whatsinyourwallet.api.transaction.TransactionType
import dot.reyn.whatsinyourwallet.extensions.getBalance
import dot.reyn.whatsinyourwallet.extensions.message
import dot.reyn.whatsinyourwallet.extensions.transaction
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object PayCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            literal<ServerCommandSource>("pay")
                .requires(Permissions.require("whatsinyourwallet.pay", 4))
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

        if (player.getBalance(currency) < amount) {
            ctx.source.sendError(Text.literal("You don't have enough ${currency.name}!"))
            return Command.SINGLE_SUCCESS
        }

        val result = player.transaction(Transaction(
            to = player,
            from = null,
            transactionType = TransactionType.WITHDRAW,
            currency = currency,
            amount = amount,
        ))

        if (result.resultType == TransactionResultType.SUCCESS) {
            receivingPlayer.transaction(Transaction(
                to = receivingPlayer,
                from = player,
                transactionType = TransactionType.DEPOSIT,
                currency = currency,
                amount = amount,
            ))

            player.message("<green>You paid ${receivingPlayer.gameProfile.name} $amount ${currency.name}!</green>")
            receivingPlayer.message("<green>${player.gameProfile.name} paid you $amount ${currency.name}!</green>")
        } else {
            ctx.source.sendError(Text.literal("An error occurred while paying!"))
        }
        return Command.SINGLE_SUCCESS
    }
}