package net.freedinner.satisfying_weapons.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    public static final int MAX_LINE_LENGTH = 32;

    public static List<MutableText> breakDownLongTooltip(Text tooltip) {
        String text = tooltip.getString();

        String[] words = text.split("\\s+");
        List<String> lines = new ArrayList<>();
        StringBuilder currLine = new StringBuilder();

        // Replacing $ with \n, and dividing into lines
        for (String word : words) {
            if (word.contains("$")) {
                String[] splitWord = word.split("\\$");
                currLine.append(splitWord[0]).append(" ");

                lines.add(currLine.toString());
                currLine = new StringBuilder(splitWord[1]).append(" ");
            }
            else if (currLine.length() + word.length() <= MAX_LINE_LENGTH) {
                currLine.append(word).append(" ");
            }
            else {
                lines.add(currLine.toString());
                currLine = new StringBuilder(word).append(" ");
            }
        }

        lines.add(currLine.toString());

        // Replacing _ with non-breaking spaces
        lines.replaceAll(s -> s.replaceAll("_", " "));

        // Removing trailing spaces
        lines.replaceAll(String::trim);

        return lines.stream().map(Text::literal).toList();
    }
}
