package net.freedinner.satisfying_weapons.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    public static final int MAX_LINE_LENGTH = 32;

    /* MY CUSTOM SYNTAX FOR DESCRIPTIONS
          $ = new line
          _ = non-breaking space
          {-xxx} = set text color, accepts any int
     */

    public static List<MutableText> breakDownLongTooltip(Text tooltip) {
        String text = tooltip.getString();

        if (!validateBrackets(text)) {
            throw new RuntimeException("Encountered tooltip with an incorrect brackets placement");
        }

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

    private static boolean validateBrackets(String text) {
        // Pos of opening & closing brackets
        List<Integer> opb = findAllOccurrences(text, "{");
        List<Integer> clb = findAllOccurrences(text, "}");

        // Should come in pairs
        if (opb.size() != clb.size()) {
            return false;
        }

        for (int i = 0; i < opb.size(); i++) {
            // Closing bracket must be >1 pos ahead
            if (clb.get(i) < opb.get(i) + 2) {
                return false;
            }

            // Next pair, if exists, must be ahead
            if (i + 1 < opb.size() && opb.get(i + 1) < clb.get(i) + 1) {
                return false;
            }
        }

        // All checks successful
        return true;
    }

    private static List<Integer> findAllOccurrences(String text, String substr) {
        List<Integer> list = new ArrayList<>();
        int startingIndex = 0;

        while (startingIndex != -1) {
            startingIndex = text.indexOf(substr, startingIndex);

            if (startingIndex != -1) {
                list.add(startingIndex);
                startingIndex += 1;
            }
        }

        return list;
    }
}
