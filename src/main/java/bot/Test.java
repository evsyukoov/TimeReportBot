package bot;

import com.google.inject.internal.asm.$ConstantDynamic;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Test {

    BotContext context;


    public Test(BotContext context) {
        this.context = context;
    }

    public void run() {
        if (context.getUpdate().getCallbackQuery() == null) {
            return;
        }
        int id = context.getUpdate().getCallbackQuery().getMessage().getMessageId();
        String text = context.getUpdate().getCallbackQuery().getData();
        String newText = null;
        if (text.startsWith("✅")) {
            newText = text.replace("✅", "🔳");
        } else if (text.startsWith("🔳")) {
            newText = text.replace("🔳", "✅");
        }
        InlineKeyboardMarkup markup = context.getUpdate().getCallbackQuery().
                getMessage().getReplyMarkup();
        InlineKeyboardButton button = markup.getKeyboard().stream().flatMap(k -> k.stream())
                .filter(item -> item.getText().equals(text))
                .findAny().orElse(null);
        if (button != null) {
            button.setText(newText);
            button.setCallbackData(newText);
        }

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup()
                .setMessageId(id)
                .setChatId(context.getClient().getUid())
                .setReplyMarkup(markup);
        try {
            context.getBot().execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
