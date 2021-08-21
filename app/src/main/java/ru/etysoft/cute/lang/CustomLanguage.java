package ru.etysoft.cute.lang;

import android.app.Activity;
import android.content.Context;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.data.CachedUrls;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.LanguageParsingException;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.requests.GET;

public class CustomLanguage {

    public static void loadFromUrl(final String urlToXml, final Activity context, final boolean silentMode) {
        Thread loading = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String xmlString = GET.execute(urlToXml);
                    StringsRepository.applyXml(xmlString);
                    CachedUrls.setLangUrl(context, urlToXml);
                    CachedValues.setCustomLanguage(context, xmlString);
                } catch (ResponseException | LanguageParsingException e) {
                    if (!silentMode) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CuteToast.show(context.getResources().getString(R.string.err_lang), R.drawable.icon_error, context);
                            }
                        });

                    }
                    e.printStackTrace();
                }
            }
        });
        loading.start();
    }

    public static void loadExisting(Activity context) throws NotCachedException, LanguageParsingException {
        StringsRepository.applyXml(CachedValues.getCustomLanguage(context));
        loadFromUrl(CachedUrls.getLangUrl(context), context, true);
    }

    public static void remove(Context context) {
        CachedValues.removeCustomLanguage(context);
    }
}
