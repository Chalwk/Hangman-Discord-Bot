/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.game;

import java.util.*;

public class Guess {
    public static boolean getGuess(String character, Game game) {
        char guess = character.charAt(0);
        game.guesses.add(guess);
        return game.getWordToGuess().contains(character);
    }

    public static String showGuesses(List<Character> guesses) {
        Set<Character> uniqueGuesses = new HashSet<>(guesses);
        List<Character> sortedGuesses = new ArrayList<>(new TreeSet<>(uniqueGuesses));
        StringBuilder sb = new StringBuilder();
        for (Character guess : sortedGuesses) {
            sb.append(guess.toString().toUpperCase());
            if (sortedGuesses.size() > 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String formatGuessBox(Game game) {
        String word = game.getWordToGuess();
        StringBuilder sb = new StringBuilder();
        sb.append("```");
        game.correctGuesses = 0;
        for (int i = 0; i < word.length(); i++) {
            char guess = word.charAt(i);
            if (game.guesses.contains(guess)) {
                game.correctGuesses++;
                sb.append("〔").append(guess).append("〕");
            } else {
                sb.append("〔 〕");
            }
        }
        sb.append("```");
        return sb.toString();
    }
}
