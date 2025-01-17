package me.alexdevs.solstice.core.customFormats;

import me.alexdevs.solstice.Solstice;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface CustomSentMessage extends SentMessage {

    static SentMessage of(SignedMessage message, @Nullable ServerPlayerEntity sender) {
        if (message.isSenderMissing() && sender == null) {
            return new Profileless(message.getContent());
        }
        return new Chat(message, sender);
    }

    record Profileless(Text getContent) implements SentMessage {
        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            sender.networkHandler.sendProfilelessChatMessage(this.getContent, params);
        }
    }

    record Chat(SignedMessage message, ServerPlayerEntity sender) implements SentMessage {
        @Override
        public Text getContent() {
            return this.message.getContent();
        }

        @Override
        public void send(ServerPlayerEntity receiver, boolean filterMaskEnabled, MessageType.Parameters params) {
            var receiverState = Solstice.state.getPlayerState(receiver);
            if(receiverState.ignoredPlayers.contains(sender.getUuid()) && !Permissions.check(sender, "solstice.ignore.bypass", 2)) {
                return;
            }
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            //Solstice.LOGGER.info("Message params type: {}", params.type().chat().translationKey());
            if (!signedMessage.isFullyFiltered()) {
                switch (params.type().chat().translationKey()) {
                    case "chat.type.text":
                        CustomChatMessage.sendChatMessage(receiver, message, params, sender);
                        break;
                    case "chat.type.emote":
                        CustomEmoteMessage.sendEmoteMessage(receiver, message, params, sender);
                        break;
                    default:
                        receiver.networkHandler.sendProfilelessChatMessage(this.message.getContent(), params);
                        break;
                }
            }

        }
    }
}
