
package ru.etysoft.cute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.r0adkll.slidr.Slidr;

import ru.etysoft.cute.R;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.data.CachedUrls;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.LanguageParsingException;
import ru.etysoft.cute.themes.Theme;

public class AppearanceSettings extends AppCompatActivity {


    private static final String ISDARK_THEME = "APP_THEME_NIGHT";
    public static String DAY_THEME_TEMPLATE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<resources>\n" +
            "\n" +
            "    <!-- Theme colored -->\n" +
            "\n" +
            "    <color name=\"themeMyMessage\">#E6E6E6</color>\n" +
            "    <color name=\"themeMyMessageText\">#171717</color>\n" +
            "    <color name=\"themeMyMessageTextLink\">#526FFF</color>\n" +
            "    <color name=\"themeMessage\">@color/accent_blue</color>\n" +
            "    <color name=\"themeMessageText\">#FFFFFF</color>\n" +
            "    <color name=\"themeMessageTextLink\">#FFFFFF</color>\n" +
            "    <color name=\"themeAccent\">#526FFF</color>\n" +
            "    <color name=\"themeAccentLight\">#7D93FF</color>\n" +
            "    <color name=\"themeBackground\">#FFFFFF</color>\n" +
            "    <color name=\"themeBackgroundLight\">#D6D6D6</color>\n" +
            "    <color name=\"themeBackgroundSupport\">#F1F1F1</color>\n" +
            "    <color name=\"themeGrayIcon\">#ACACAC</color>\n" +
            "    <color name=\"themeButton\">#526FFF</color>\n" +
            "\n" +
            "    <!-- Static values -->\n" +
            "\n" +
            "    <color name=\"black\">#1B1B1B</color>\n" +
            "    <color name=\"white\">#FFFFFF</color>\n" +
            "    <color name=\"achromatic_20\">#232D40</color>\n" +
            "    <color name=\"achromatic_30\">#4D4D4D</color>\n" +
            "    <color name=\"achromatic_40\">#8E93A3</color>\n" +
            "    <color name=\"achromatic_50\">#666666</color>\n" +
            "    <color name=\"achromatic_60\">#808080</color>\n" +
            "    <color name=\"achromatic_75\">#BFBFBF</color>\n" +
            "    <color name=\"black_opacity_50\">#803F414D</color>\n" +
            "    <color name=\"white_opacity_50\">#80FFFFFF</color>\n" +
            "    <color name=\"accent_blue\">#526FFF</color>\n" +
            "    <color name=\"accent_support\">#3752DB</color>\n" +
            "    <color name=\"orange\">#FF8800</color>\n" +
            "    <color name=\"light_accent\">#66BFFF</color>\n" +
            "    <color name=\"state_green\">#22A64D</color>\n" +
            "    <color name=\"state_red\">#E62333</color>\n" +
            "    <color name=\"state_gray\">#EBEEFF</color>\n" +
            "    <color name=\"light_green\">#7AE097</color>\n" +
            "    <color name=\"light_red\">#F08294</color>\n" +
            "    <color name=\"light_yellow\">#FFD685</color>\n" +
            "    <color name=\"debug\">#8B226CFF</color>\n" +
            "    <color name=\"light_blue\">#9AA9F4</color>\n" +
            "    <color name=\"light_sky_blue\">#9AA9F4</color>\n" +
            "    <color name=\"background_red\">#FFEFF5</color>\n" +
            "    <color name=\"text_black\">#000000</color>\n" +
            "    <color name=\"text_soft_gray\">#999999</color>\n" +
            "    <color name=\"text_gray\">#4E596E</color>\n" +
            "    <color name=\"text_white\">#FFFFFF</color>\n" +
            "    <color name=\"gray\">#4E596E</color>\n" +
            "    <color name=\"soft_gray\">#999999</color>\n" +
            "\n" +
            "    <color name=\"paintBlack\">#131313</color>\n" +
            "    <color name=\"paintWhite\">#F3F3F3</color>\n" +
            "    <color name=\"paintGreen\">#84E63D</color>\n" +
            "    <color name=\"paintBlue\">#3D75E6</color>\n" +
            "    <color name=\"paintLightBlue\">#6A92E4</color>\n" +
            "    <color name=\"paintRed\">#CF3441</color>\n" +
            "    <color name=\"paintOrange\">#CF8434</color>\n" +
            "    <color name=\"paintYellow\">#F6E055</color>\n" +
            "    <color name=\"paintPurple\">#962DCD</color>\n" +
            "\n" +
            "    <color name=\"colorPrimaryDark\">#4E4E4E</color>\n" +
            "\n" +
            "    <color name=\"colorLightGray\">#D9D9D9</color>\n" +
            "    <color name=\"colorUnderWhite\">#F9F9F9</color>\n" +
            "    <color name=\"colorMainLight\">@color/themeAccentLight</color>\n" +
            "    <color name=\"colorAccent10\">#1A3456FF</color>\n" +
            "    <color name=\"colorOrange\">#FF4F00</color>\n" +
            "    <color name=\"colorLightLGray\">#F5F5F5</color>\n" +
            "    <color name=\"colorPrimaryGray\">#C1C1C1</color>\n" +
            "    <color name=\"colorEtysoft\">#4A98FF</color>\n" +
            "    <color name=\"colorGoodGray\">#E4E4E4</color>\n" +
            "    <color name=\"colorTransBlack\">#88000000</color>\n" +
            "    <color name=\"colorTextDark\">@color/colorPrimaryDark</color>\n" +
            "    <color name=\"colorMessageText\">@color/colorPrimaryDark</color>\n" +
            "    <color name=\"colorSupportButton\">@color/colorPrimaryDark</color>\n" +
            "\n" +
            "\n" +
            "    <color name=\"colorTextBackground\">#D7FFFFFF</color>\n" +
            "\n" +
            "    <color name=\"colorBackgroundElement\">@color/themeBackgroundSupport</color>\n" +
            "\n" +
            "\n" +
            "    <color name=\"colorNavigationUnfocused\">@color/achromatic_75</color>\n" +
            "    <color name=\"colorNavigationFocused\">@color/themeAccent</color>\n" +
            "\n" +
            "    <color name=\"colorEditText\">#F1F1F1</color>\n" +
            "    <color name=\"colorEditTextOutline\">#F1F1F1</color>\n" +
            "    <color name=\"colorEditTextIcon\">#ACACAC</color>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "    <!-- Snap buttons -->\n" +
            "    <color name=\"colorIconTint\">@color/themeAccent</color>\n" +
            "    <color name=\"colorIconTintAccent\">#FFFFFF</color>\n" +
            "    <Ф8</color>\n" +
            "    <color name=\"avatar04\">#7368CF</color>\n" +
            "    <color name=\"avatar05\">#B468CF</color>\n" +
            "    <color name=\"avatar06\">#79BF4E</color>\n" +
            "    <color name=\"avatar07\">#C4C67A</color>\n" +
            "    <color name=\"avatar08\">#21AAD5</color>\n" +
            "    <color name=\"avatar09\">#4251B3</color>\n" +
            "    <color name=\"avatar10\">#757575</color>\n" +
            "    <color name=\"avatar11\">#5C2E85</color>\n" +
            "    <color name=\"avatar12\">#4FCA51</color>\n" +
            "    <color name=\"avatar13\">#D19554</color>\n" +
            "    <color name=\"avatar14\">#D1548E</color>\n" +
            "    <color name=\"avatar15\">#E33644</color>\n" +
            "    <color name=\"avatar16\">#3C48B3</color>\n" +
            "    <color name=\"avatar17\">#F3662D</color>\n" +
            "    <color name=\"avatar18\">#2DF3AE</color>\n" +
            "    <color name=\"avatar19\">#373737</color>\n" +
            "    <color name=\"avatar20\">#DC8BFF</color>\n" +
            "\n" +
            "    <!-- Theme preview colors -->\n" +
            "    <color name=\"colorMessageOldschool\">#C043FB</color>\n" +
            "    <color name=\"colorMyMessageOldschool\">#C6C6C6</color>\n" +
            "    <color name=\"colorBackgroundOldschool\">@color/colorBackground</color>\n" +
            "</resources>\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appearance_settings);
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (cacheUtils.hasKey(ISDARK_THEME, this)) {
            if (cacheUtils.getBoolean(ISDARK_THEME, this)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Slidr.attach(this);
    }

    public void changeTheme(boolean isLight) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        CacheUtils cacheUtils = CacheUtils.getInstance();
        if (!isLight) {
            cacheUtils.setBoolean(ISDARK_THEME, true, this);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            cacheUtils.setBoolean(ISDARK_THEME, false, this);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


    }

    public void recreateTheme() {
        changeTheme(Theme.isDayTheme(this));
    }

    public void resetTheme(View v) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        CachedUrls.removeThemeUrl(this);
        if (Theme.isDayTheme(this)) {
            CachedValues.removeCustomThemeDay(this);
        } else {
            CachedValues.removeCustomThemeNight(this);
        }
        Theme.clear();
        recreateTheme();
        applyChanges();
    }

    public void oldSchoolTheme(View v) {
        try {
            if (Theme.isDayTheme(this)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                CachedUrls.removeThemeUrl(this);
                Theme.applyXml(oldSchoolLight, this);
                applyChanges();
            }

        } catch (LanguageParsingException e) {
            e.printStackTrace();
        }

    }

    public void applyChanges() {
        Intent intent = new Intent(AppearanceSettings.this, AppearanceSettings.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void onLightThemeClick(View v) {

        changeTheme(true);
        applyChanges();
    }

    public void onDarkThemeClick(View v) {
        changeTheme(false);
        applyChanges();
    }

    public static final String oldSchoolLight = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<resources>\n" +
            "\n" +
            "    <!-- Static values -->\n" +
            "\n" +
            "    <color name=\"black\">#1B1B1B</color>\n" +
            "    <color name=\"white\">#FFFFFF</color>\n" +
            "    <color name=\"achromatic_20\">#232D40</color>\n" +
            "    <color name=\"achromatic_30\">#4D4D4D</color>\n" +
            "    <color name=\"achromatic_40\">#8E93A3</color>\n" +
            "    <color name=\"achromatic_50\">#666666</color>\n" +
            "    <color name=\"achromatic_60\">#808080</color>\n" +
            "    <color name=\"achromatic_75\">#BFBFBF</color>\n" +
            "    <color name=\"black_opacity_50\">#803F414D</color>\n" +
            "    <color name=\"white_opacity_50\">#80FFFFFF</color>\n" +
            "    <color name=\"accent_blue\">#526FFF</color>\n" +
            "    <color name=\"accent_support\">#D06DFF</color>\n" +
            "    <color name=\"orange\">#FF8800</color>\n" +
            "    <color name=\"light_accent\">#66BFFF</color>\n" +
            "    <color name=\"state_green\">#22A64D</color>\n" +
            "    <color name=\"state_red\">#E62333</color>\n" +
            "    <color name=\"state_gray\">#EBEEFF</color>\n" +
            "    <color name=\"light_green\">#7AE097</color>\n" +
            "    <color name=\"light_red\">#F08294</color>\n" +
            "    <color name=\"light_yellow\">#FFD685</color>\n" +
            "    <color name=\"debug\">#8B226CFF</color>\n" +
            "    <color name=\"light_blue\">#9AA9F4</color>\n" +
            "    <color name=\"light_sky_blue\">#9AA9F4</color>\n" +
            "    <color name=\"background_red\">#FFEFF5</color>\n" +
            "    <color name=\"text_black\">#000000</color>\n" +
            "    <color name=\"text_soft_gray\">#999999</color>\n" +
            "    <color name=\"text_gray\">#4E596E</color>\n" +
            "    <color name=\"text_white\">#FFFFFF</color>\n" +
            "    <color name=\"gray\">#4E596E</color>\n" +
            "    <color name=\"soft_gray\">#999999</color>\n" +
            "\n" +
            "    <color name=\"paintBlack\">#131313</color>\n" +
            "    <color name=\"paintWhite\">#F3F3F3</color>\n" +
            "    <color name=\"paintGreen\">#84E63D</color>\n" +
            "    <color name=\"paintBlue\">#3D75E6</color>\n" +
            "    <color name=\"paintLightBlue\">#6A92E4</color>\n" +
            "    <color name=\"paintRed\">#CF3441</color>\n" +
            "    <color name=\"paintOrange\">#CF8434</color>\n" +
            "    <color name=\"paintYellow\">#F6E055</color>\n" +
            "    <color name=\"paintPurple\">#962DCD</color>\n" +
            "\n" +
            "    <color name=\"colorPrimaryDark\">#4E4E4E</color>\n" +
            "\n" +
            "    <color name=\"colorLightGray\">#D9D9D9</color>\n" +
            "    <color name=\"colorUnderWhite\">#F9F9F9</color>\n" +
            "    <color name=\"colorMainLight\">#E3A7FF</color>\n" +
            "    <color name=\"colorAccent10\">#1A3456FF</color>\n" +
            "    <color name=\"colorOrange\">#FF4F00</color>\n" +
            "    <color name=\"colorLightLGray\">#F5F5F5</color>\n" +
            "    <color name=\"colorPrimaryGray\">#C1C1C1</color>\n" +
            "    <color name=\"colorEtysoft\">#4A98FF</color>\n" +
            "    <color name=\"colorGoodGray\">#E4E4E4</color>\n" +
            "    <color name=\"colorTransBlack\">#88000000</color>\n" +
            "    <color name=\"colorTextDark\">@color/colorPrimaryDark</color>\n" +
            "    <color name=\"colorMessageText\">@color/colorPrimaryDark</color>\n" +
            "    <color name=\"colorSupportButton\">@color/colorPrimaryDark</color>\n" +
            "\n" +
            "\n" +
            "    <color name=\"colorTextBackground\">#FFFFFF</color>\n" +
            "\n" +
            "    <color name=\"colorBackgroundElement\">@color/colorLightLGray</color>\n" +
            "\n" +
            "\n" +
            "    <color name=\"colorNavigationUnfocused\">@color/achromatic_75</color>\n" +
            "    <color name=\"colorNavigationFocused\">#C043FB</color>\n" +
            "\n" +
            "    <color name=\"colorEditText\">#F1F1F1</color>\n" +
            "    <color name=\"colorEditTextOutline\">#F1F1F1</color>\n" +
            "    <color name=\"colorEditTextIcon\">#ACACAC</color>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "    <!-- Snap buttons -->\n" +
            "    <color name=\"colorIconTint\">@color/accent_support</color>\n" +
            "    <color name=\"colorIconTintAccent\">#FFFFFF</color>\n" +
            "    <color name=\"colorIconSupportTint\">#878787</color>\n" +
            "    <color name=\"colorButton\">#C043FB</color>\n" +
            "    <color name=\"colorButtonCircle\">@color/colorLightLGray</color>\n" +
            "\n" +
            "    <!-- Buttons -->\n" +
            "    <color name=\"colorPassiveMini\">#ECECEC</color>\n" +
            "    <color name=\"colorPassiveMiniOutline\">#ECECEC</color>\n" +
            "    <color name=\"colorButtonTrans\">@color/text_soft_gray</color>\n" +
            "    <color name=\"colorBackButton\">#D8D8D8</color>\n" +
            "    <color name=\"colorButtonPassive\">@color/colorLightLGray</color>\n" +
            "\n" +
            "    <color name=\"colorCloud\">@color/colorMainLight</color>\n" +
            "    <color name=\"colorPanelGray\">@color/colorLightLGray</color>\n" +
            "    <color name=\"splashLogo\">#C043FB</color>\n" +
            "\n" +
            "    <color name=\"colorBackground\">#FFFFFF</color>\n" +
            "    <color name=\"colorBackgroundElements\">#D6D6D6</color>\n" +
            "\n" +
            "\n" +
            "    <!-- Text -->\n" +
            "    <color name=\"colorText\">#252525</color>\n" +
            "    <color name=\"colorTextAccent\">#C043FB</color>\n" +
            "    <color name=\"colorSupportText\">@color/text_soft_gray</color>\n" +
            "    <color name=\"colorChatMessageText\">#FFFFFF</color>\n" +
            "    <color name=\"colorChatMessageLink\">#FFFFFF</color>\n" +
            "    <color name=\"colorMyMessageText\">#000000</color>\n" +
            "    <color name=\"colorMyMessageLink\">#C043FB</color>\n" +
            "    <color name=\"colorTextLink\">#BABABA</color>\n" +
            "    <color name=\"colorTextPanel\">@color/colorLightLGray</color>\n" +
            "    <color name=\"colorButtonText\">#FFFFFF</color>\n" +
            "    <color name=\"colorHint\">#757575</color>\n" +
            "    <color name=\"colorPassiveButtonText\">#505050</color>\n" +
            "    <color name=\"colorTitle\">#C043FB</color>\n" +
            "\n" +
            "\n" +
            "    <color name=\"colorError\">@color/colorOrange</color>\n" +
            "    <color name=\"colorErrorIcon\">@color/colorOrange</color>\n" +
            "    <color name=\"colorSuccess\">@color/state_green</color>\n" +
            "    <color name=\"colorDivider\">#F4F4F4</color>\n" +
            "    <color name=\"colorPlaceholder\">@color/colorBackground</color>\n" +
            "    <color name=\"splashBackground\">#FFF</color>\n" +
            "\n" +
            "    <color name=\"colorAccentTop\">#C043FB</color>\n" +
            "    <color name=\"colorAccentIcon\">#C043FB</color>\n" +
            "\n" +
            "\n" +
            "    <color name=\"colorOnline\">@color/state_green</color>\n" +
            "    <color name=\"colorOnlineOutLine\">@color/white</color>\n" +
            "\n" +
            "\n" +
            "    <color name=\"colorAccountTop\">@color/colorLightLGray</color>\n" +
            "\n" +
            "    <color name=\"colorProgressBar\">#C043FB</color>\n" +
            "\n" +
            "    <color name=\"colorAltSupport\">#C043FB</color>\n" +
            "\n" +
            "\n" +
            "    <color name=\"colorNotReaded\">#F4F4F4</color>\n" +
            "    <color name=\"colorNoMessagesPlanet\">#C043FB</color>\n" +
            "    <color name=\"colorMyMessage\">#C6C6C6</color>\n" +
            "    <color name=\"colorMessage\">#C043FB</color>\n" +
            "    <color name=\"colorUnreadBackground\">#EDEDED</color>\n" +
            "    <color name=\"colorMessageState\">#C043FB</color>\n" +
            "\n" +
            "\n" +
            "    <color name=\"colorOffline\">#707070</color>\n" +
            "    <color name=\"colorOfflineOutLine\">@color/colorBackground</color>\n" +
            "\n" +
            "    <color name=\"colorTransparent\">#00C1C1C1</color>\n" +
            "    <color name=\"colorTooltip\">#B0000000</color>\n" +
            "\n" +
            "    <color name=\"colorWhite\">#FFFFFF</color>\n" +
            "    <color name=\"colorBlack\">#000000</color>\n" +
            "\t<color name=\"colorIconTintLight\">#D784FF</color>\n" +
            "\n" +
            "    <!-- Avatar colors -->\n" +
            "    <color name=\"avatar01\">#1B93C1</color>\n" +
            "    <color name=\"avatar02\">#4FD596</color>\n" +
            "    <color name=\"avatar03\">#1DA898</color>\n" +
            "    <color name=\"avatar04\">#7368CF</color>\n" +
            "    <color name=\"avatar05\">#B468CF</color>\n" +
            "    <color name=\"avatar06\">#79BF4E</color>\n" +
            "    <color name=\"avatar07\">#C4C67A</color>\n" +
            "    <color name=\"avatar08\">#21AAD5</color>\n" +
            "    <color name=\"avatar09\">#4251B3</color>\n" +
            "    <color name=\"avatar10\">#757575</color>\n" +
            "    <color name=\"avatar11\">#5C2E85</color>\n" +
            "    <color name=\"avatar12\">#4FCA51</color>\n" +
            "    <color name=\"avatar13\">#D19554</color>\n" +
            "    <color name=\"avatar14\">#D1548E</color>\n" +
            "    <color name=\"avatar15\">#E33644</color>\n" +
            "    <color name=\"avatar16\">#3C48B3</color>\n" +
            "    <color name=\"avatar17\">#F3662D</color>\n" +
            "    <color name=\"avatar18\">#2DF3AE</color>\n" +
            "    <color name=\"avatar19\">#373737</color>\n" +
            "    <color name=\"avatar20\">#DC8BFF</color>\n" +
            "\n" +
            "</resources>";
}