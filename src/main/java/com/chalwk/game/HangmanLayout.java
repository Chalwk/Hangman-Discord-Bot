/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.game;

import java.util.EnumMap;

public enum HangmanLayout {

    GALLOWS_1(0),
    GALLOWS_2(1),
    GALLOWS_3(2),
    GALLOWS_4(3),
    GALLOWS_5(4),
    GALLOWS_6(5),
    GALLOWS_7(6),
    GALLOWS_8(7),

    EXERCISE_1(0),
    EXERCISE_2(1),
    EXERCISE_3(2),
    EXERCISE_4(3),
    EXERCISE_5(4),
    EXERCISE_6(5);

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

    private final int layoutIndex;

    HangmanLayout(int layoutIndex) {
        this.layoutIndex = layoutIndex;
    }

    public String getLayout() {
        return LAYOUTS.get(this);
    }
}