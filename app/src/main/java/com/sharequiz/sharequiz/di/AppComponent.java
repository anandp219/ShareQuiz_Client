package com.sharequiz.sharequiz.di;

import android.app.Application;

import com.sharequiz.sharequiz.GameModeSelectionActivity;
import com.sharequiz.sharequiz.HorizontalItemsFragment;
import com.sharequiz.sharequiz.LanguageSelectionActivity;
import com.sharequiz.sharequiz.MainActivity;
import com.sharequiz.sharequiz.OTPVerificationActivity;
import com.sharequiz.sharequiz.PhoneVerificationActivity;
import com.sharequiz.sharequiz.QuestionActivity;
import com.sharequiz.sharequiz.WorldMapActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ResourceModule.class})
public interface AppComponent {

    void inject(HorizontalItemsFragment horizontalItemsFragment);
    void inject(LanguageSelectionActivity languageSelectionActivity);
    void inject(MainActivity mainActivity);
    void inject(PhoneVerificationActivity phoneVerificationActivity);
    void inject(Application application);
    void inject(OTPVerificationActivity otpVerificationActivity);
    void inject(GameModeSelectionActivity gameModeSelectionActivity);
    void inject(WorldMapActivity worldMapActivity);
    void inject(QuestionActivity questionActivity);

}
