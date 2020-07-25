package com.sharequiz.sharequiz.utils;

public class Constants {
    // Event constants
    public static final String JOIN_EVENT = "join";
    public static final String JOIN_WITH_ROOM_EVENT = "join_with_room";
    public static final String ANSWER_EVENT = "answer";
    public static final String NEW_QUESTION_EVENT = "new_question";
    public static final String NEW_ANSWER_EVENT = "new_answer";
    public static final String GAME_OVER_EVENT = "game_over";
    public static final String GAME_EVENT = "game";

    // Constant for protocol
    public static final String WEBSOCKET_PROTOCOL = "websocket";

    // Extras for the activities
    public static final String EXTRA_GAME = "game_extra";
    public static final String OPPONENT_ID = "opponent_id";
    public static final String GAME_ID = "game_id";

    // Integer constants for the game
    public static final Integer SCORE_FOR_CORRECT_ANSWER = 10;

    // Integer constant for delay in the game answer
    public static final Integer TIME_DELAY_FOR_NEW_QUESTION = 5000;

}
