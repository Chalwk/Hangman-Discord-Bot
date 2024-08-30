/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.game;

import java.util.EnumMap;

public enum HangmanLayout {

    GALLOWS_1,
    GALLOWS_2,
    GALLOWS_3,
    GALLOWS_4,
    GALLOWS_5,
    GALLOWS_6,
    GALLOWS_7,
    GALLOWS_8,

    EXERCISE_1,
    EXERCISE_2,
    EXERCISE_3,
    EXERCISE_4,
    EXERCISE_5,
    EXERCISE_6;

    private static final EnumMap<HangmanLayout, String> LAYOUTS = new EnumMap<>(HangmanLayout.class);

    static {
        LAYOUTS.put(GALLOWS_1, """
                ┌─────┐
                │     │
                │     O
                │    /|\\
                │    / \\
                │
                └─────┘
                """);
        LAYOUTS.put(GALLOWS_2, """
                ┌─────┐
                │     │
                │     O
                │    /|\\
                │    /
                │
                └─────┘
                """);
        LAYOUTS.put(GALLOWS_3, """
                ┌─────┐
                │     │
                │     O
                │    /|\\
                │
                │
                └─────┘
                """);
        LAYOUTS.put(GALLOWS_4, """
                ┌─────┐
                │     │
                │     O
                │    /|
                │
                │
                └─────┘
                """);
        LAYOUTS.put(GALLOWS_5, """
                ┌─────┐
                │     │
                │     O
                │     |
                │
                │
                └─────┘
                """);
        LAYOUTS.put(GALLOWS_6, """
                ┌─────┐
                │     │
                │     O
                │
                │
                │
                └─────┘
                """);
        LAYOUTS.put(GALLOWS_7, """
                ┌─────┐
                │     │
                │
                │
                │
                │
                └─────┘
                """);
        LAYOUTS.put(GALLOWS_8, """
                ┌─────┐
                │
                │
                │
                │
                │
                └─────┘
                """);
        LAYOUTS.put(EXERCISE_1, """
                []--,---,--[]
                    \\ O /
                     - -
                      -
                     / \\
                    =   =
                """);
        LAYOUTS.put(EXERCISE_2, """
                []--=-O-=--[]
                     '-'
                      v
                     / )
                    ~  z
                """);
        LAYOUTS.put(EXERCISE_3, """
                    ._O_.
                []--<-+->--[]
                      X
                     / \\
                    -   -
                """);
        LAYOUTS.put(EXERCISE_4, """
                    ,_O_,
                []--(---)--[]
                     >'>
                     - -
                """);
        LAYOUTS.put(EXERCISE_5, """
                    ,-O-,
                []--=---=--[]
                     2"2
                """);
        LAYOUTS.put(EXERCISE_6, """
                     _._
                    / O \\
                    \\| |/
                []--+=-=+--[]
                """);
    }

    public String getLayout() {
        return LAYOUTS.get(this);
    }
}