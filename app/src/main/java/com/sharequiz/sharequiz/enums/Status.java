package com.sharequiz.sharequiz.enums;

import com.google.gson.annotations.SerializedName;

public enum Status {
    @SerializedName("0") DEFAULT, @SerializedName("1") ACTIVE, @SerializedName("2") DISCONNECTED,
    @SerializedName("3") FINISHED
}
