/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.game;

import java.util.List;

public class Guess {

    public static boolean getGuess(String character, StringBuilder word, Game game) {
        char guess = character.charAt(0);
        game.guesses.add(guess);
        return word.toString().contains(character);
    }

    public static String showGuesses(List<Character> guesses) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < guesses.size(); i++) {
            sb.append(guesses.get(i).toString().toUpperCase());
            if (i != guesses.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String guessBox(StringBuilder word, Game game) {
        StringBuilder sb = new StringBuilder();
        sb.append("```");
        game.mistakes = 0;
        for (int i = 0; i < word.length(); i++) {
            char guess = word.charAt(i);
            if (game.guesses.contains(guess)) {
                game.mistakes++;
                sb.append("〔").append(guess).append("〕");
            } else {
                sb.append("〔 〕");
            }
        }
        sb.append("```");
        return sb.toString();
    }
}
