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

    private static final EnumMap<HangmanLayout, String> LAYOUTS = initializeLayouts();

    private static EnumMap<HangmanLayout, String> initializeLayouts() {
        EnumMap<HangmanLayout, String> layouts = new EnumMap<>(HangmanLayout.class);
        layouts.put(GALLOWS_1, """
                ┌─────┐
                │     │
                │     O
                │    /|\\
                │    / \\
                │
                └─────┘
                """);
        layouts.put(GALLOWS_2, """
                ┌─────┐
                │     │
                │     O
                │    /|\\
                │    /
                │
                └─────┘
                """);
        layouts.put(GALLOWS_3, """
                ┌─────┐
                │     │
                │     O
                │    /|\\
                │
                │
                └─────┘
                """);
        layouts.put(GALLOWS_4, """
                ┌─────┐
                │     │
                │     O
                │    /|
                │
                │
                └─────┘
                """);
        layouts.put(GALLOWS_5, """
                ┌─────┐
                │     │
                │     O
                │     |
                │
                │
                └─────┘
                """);
        layouts.put(GALLOWS_6, """
                ┌─────┐
                │     │
                │     O
                │
                │
                │
                └─────┘
                """);
        layouts.put(GALLOWS_7, """
                ┌─────┐
                │     │
                │
                │
                │
                │
                └─────┘
                """);
        layouts.put(GALLOWS_8, """
                ┌─────┐
                │
                │
                │
                │
                │
                └─────┘
                """);
        layouts.put(EXERCISE_1, """
                []--,---,--[]
                    \\ O /
                     - -
                      -
                     / \\
                    =   =
                """);
        layouts.put(EXERCISE_2, """
                []--=-O-=--[]
                     '-'
                      v
                     / )
                    ~  z
                """);
        layouts.put(EXERCISE_3, """
                    ._O_.
                []--<-+->--[]
                      X
                     / \\
                    -   -
                """);
        layouts.put(EXERCISE_4, """
                    ,_O_,
                []--(---)--[]
                     >'>
                     - -
                """);
        layouts.put(EXERCISE_5, """
                    ,-O-,
                []--=---=--[]
                     2"2
                """);
        layouts.put(EXERCISE_6, """
                     _._
                    / O \\
                    \\| |/
                []--+=-=+--[]
                """);
        return layouts;
    }

    public String getLayout() {
        return LAYOUTS.getOrDefault(this, "Layout not found");
    }
}