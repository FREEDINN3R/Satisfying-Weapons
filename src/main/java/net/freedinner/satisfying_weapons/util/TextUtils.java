package net.freedinner.satisfying_weapons.util;

import com.google.common.collect.Iterables;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.util.*;

public class TextUtils {
    public static final int MAX_LINE_LENGTH = 32;

    /* MY CUSTOM SYNTAX FOR DESCRIPTIONS
          _ = non-breaking space
          {xxx} = set text color, accepts any int
     */

    public static List<MutableText> breakDownLongTooltip(Text tooltip) {
        String text = tooltip.getString();

        // Removing color values from text, and saving them in a separate list
        Queue<Pair<Integer, Integer>> colorsQueue = new LinkedList<>();
        text = extractColorValues(text, colorsQueue);

        // Splitting into words, based on spaces
        List<String> words = Arrays.asList(text.split("\\s+"));
        if (words.isEmpty()) {
            SatisfyingWeapons.LOGGER.warn("Encountered an empty description");
            return List.of();
        }

        List<String> lines = new ArrayList<>();
        StringBuilder currLine = new StringBuilder(words.get(0));

        // Dividing words into lines
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

        List<MutableText> textLines = new ArrayList<>();

        int currColor = 0xAAAAAA; // Gray
        int currPos = 0;

        // Converting string to text, applying color
        for (String line : lines) {
            MutableText textLine = Text.empty();
            int currPosInLine = 0;

            // While the queue still has colors belonging to this line
            while (!colorsQueue.isEmpty() && colorsQueue.peek().getLeft() < currPos + line.length()) {
                Pair<Integer, Integer> colorPair = colorsQueue.remove();

                // Color and append the previous text piece
                MutableText textPiece = Text.literal(line.substring(currPosInLine, colorPair.getLeft() - currPos));
                textLine.append(textPiece.setStyle(Style.EMPTY.withColor(currColor)));

                // Save the current one
                currColor = colorPair.getRight();
                currPosInLine = colorPair.getLeft() - currPos;
            }

            // Color and append the remaining text
            MutableText textPiece = Text.literal(line.substring(currPosInLine));
            textLine.append(textPiece.setStyle(Style.EMPTY.withColor(currColor)));

            textLines.add(textLine);
            currPos += line.length() + 1; // accounts for a missing space
        }

        return textLines;
    }

    private static String extractColorValues(String text, Queue<Pair<Integer, Integer>> targetQueue) {
        // TODO: texts can't change dynamically, so validating them each tick is redundant, but idk how to fix it properly
        if (!validateBrackets(text)) {
            throw new RuntimeException("Encountered a tooltip that violates bracket placement rules");
        }

        int colorPos = 0;

        while (colorPos != -1) {
            colorPos = text.indexOf("{", colorPos);

            if (colorPos != -1) {
                int color = Integer.parseInt(text.substring(colorPos + 1, colorPos + 7), 16);
                targetQueue.add(new Pair<>(colorPos, color));

                text = text.substring(0, colorPos) + text.substring(colorPos + 8);
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
            // Closing bracket must be exactly 7 pos ahead
            if (clb.get(i) - opb.get(i) != 7) {
                return false;
            }

            // Next pair, if exists, must not intersect with this one
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
