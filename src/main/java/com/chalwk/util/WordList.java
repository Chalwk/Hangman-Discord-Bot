/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.util;

import com.chalwk.util.Logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordList {

    private static List<String> wordList;

    public WordList() {
        wordList = new ArrayList<>();
        loadWordsFromFile();
    }

    public static String getRandomWord() {
        return wordList.get((int) (Math.random() * wordList.size()));
    }

    private void loadWordsFromFile() {
        String fileName = "words.txt";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                Logger.warning("File not found: " + fileName);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                Collections.addAll(wordList, words);
            }
        } catch (IOException e) {
            Logger.warning("Failed to load words from file: " + e.getMessage());
        }
    }
}
