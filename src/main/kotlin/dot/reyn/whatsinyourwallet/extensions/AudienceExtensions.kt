package dot.reyn.whatsinyourwallet.extensions

import dev.vankka.enhancedlegacytext.EnhancedLegacyText
import net.kyori.adventure.audience.Audience

fun Audience.message(message: String) {
    this.sendMessage(EnhancedLegacyText.get().buildComponent(message).build())
}