package net.freedinner.satisfying_weapons.util;

import com.google.common.collect.Iterables;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextUtils {
    public static final int MAX_LINE_LENGTH = 32;

    /* MY CUSTOM SYNTAX FOR DESCRIPTIONS
          _ = non-breaking space
          {-xxx} = set text color, accepts any int
     */

    public static List<MutableText> breakDownLongTooltip(Text tooltip) {
        String text = tooltip.getString();

        // Removing color values from text, and saving them in a separate list
        List<Pair<Integer, Integer>> colorsList = new ArrayList<>();
        text = extractColorValues(text, colorsList);

        // Splitting into words, based on spaces
        List<String> words = Arrays.asList(text.split("\\s+"));
        if (words.isEmpty()) {
            SatisfyingWeapons.LOGGER.warn("Encountered an empty description");
            return List.of();
        }

        List<String> lines = new ArrayList<>();
        StringBuilder currLine = new StringBuilder(words.get(0));

        // Dividing words into description lines
        for (String word : Iterables.skip(words, 1)) {
            if (currLine.length() + word.length() + 1 <= MAX_LINE_LENGTH) {
                currLine.append(" ").append(word);
            }
            else {
                lines.add(currLine.toString());
                currLine = new StringBuilder(word);
            }
        }

        lines.add(currLine.toString());

        // Replacing _ with non-breaking spaces
        lines.replaceAll(s -> s.replaceAll("_", " "));

        return lines.stream().map(Text::literal).toList();
    }

    private static String extractColorValues(String text, List<Pair<Integer, Integer>> targetList) {
        // TODO: texts can't change dynamically, so validating them each tick is redundant, but idk how to fix it properly
        if (!validateBrackets(text)) {
            throw new RuntimeException("Encountered a tooltip that violates bracket placement rules");
        }

        int opIndex = 0;

        while (opIndex != -1) {
            opIndex = text.indexOf("{", opIndex);

            if (opIndex != -1) {
                int clIndex = text.indexOf("}", opIndex);

                int color = Integer.parseInt(text.substring(opIndex + 1, clIndex));
                targetList.add(new Pair<>(opIndex, color));

                text = text.substring(0, opIndex) + text.substring(clIndex + 1);
            }
        }

        return text;
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
