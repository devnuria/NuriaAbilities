package nr.nuria.nuriaAbilities.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ColorUtil {

    private static String replaceHexColorCodes(String message, String pattern) {
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            String replacement = "§x§" + hex.charAt(0) + "§" + hex.charAt(1) + "§" + hex.charAt(2) + "§" + hex.charAt(3) + "§" + hex.charAt(4) + "§" + hex.charAt(5);
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String format(String message) {
        message = replaceHexColorCodes(message, "(?i)&#([0-9a-fA-F]{6})");
        message = replaceHexColorCodes(message, "(?i)#([0-9a-fA-F]{6})");
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }
    public static TextComponent formatClickableText(String message, String clickAction, String clickValue, String hoverMessage) {
        String formattedMessage = format(message);
        TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(formattedMessage));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(clickAction.toUpperCase()), clickValue));
        if (hoverMessage != null && !hoverMessage.isEmpty()) {
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverMessage)));
        }

        return textComponent;
    }

    public static List<String> formatList(List<String> messages) {
        return messages.stream().map(ColorUtil::format).collect(Collectors.toList());
    }
    public static String stripColor(String message) {
        return ChatColor.stripColor(message);
    }
    public static String strip(String message) {
        return ChatColor.stripColor(message);
    }
}