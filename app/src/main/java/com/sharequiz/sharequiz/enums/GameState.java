package com.sharequiz.sharequiz.enums;

import com.google.gson.annotations.SerializedName;

public enum GameState {
    @SerializedName("0") ACTIVE, @SerializedName("1") DISCONNECTED, @SerializedName("2") FINISHED,
}
