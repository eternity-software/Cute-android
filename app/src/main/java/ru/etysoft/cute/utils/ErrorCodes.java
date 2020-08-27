package ru.etysoft.cute.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import ru.etysoft.cute.R;

public class ErrorCodes {

    private static Map<String, String> codes = new HashMap<String, String>();

    public static void initialize(Context context) {
        codes.put("#AM000.1", context.getString(R.string.errcode_AM000_1));
        codes.put("#AM001.1", context.getString(R.string.errcode_AM001_1));
        codes.put("#AM001.2", context.getString(R.string.errcode_AM001_2));
        codes.put("#AM001.3", context.getString(R.string.errcode_AM001_3));
        codes.put("#AM001.3.1", context.getString(R.string.errcode_AM001_3_1));
        codes.put("#AM001.4", context.getString(R.string.errcode_AM001_4));
        codes.put("#AM001.5", context.getString(R.string.errcode_AM001_5));

        codes.put("#AM002.1", context.getString(R.string.errcode_AM002_1));
        codes.put("#AM002.2", context.getString(R.string.errcode_AM002_2));
        codes.put("#AM002.3", context.getString(R.string.errcode_AM002_2_3));
        codes.put("#AM002.4", context.getString(R.string.errcode_AM002_4));
        codes.put("#AM002.5", context.getString(R.string.errcode_AM002_5));

        codes.put("#AM003.1", context.getString(R.string.errcode_AM003_1));
        codes.put("#AM003.2", context.getString(R.string.errcode_AM003_2));

        codes.put("#AM004.1", context.getString(R.string.errcode_AM004_1));

        codes.put("#AM005.1", context.getString(R.string.errcode_AM005_1));
        codes.put("#AM005.2", context.getString(R.string.errcode_AM005_2));

        codes.put("#AM006.1", context.getString(R.string.errcode_AM006_1));
        codes.put("#AM006.2", context.getString(R.string.errcode_AM006_2));

        codes.put("#AM007.1", context.getString(R.string.errcode_AM007_1));
        codes.put("#AM007.2", context.getString(R.string.errcode_AM007_2));
        codes.put("#AM007.3", context.getString(R.string.errcode_AM007_3));
        codes.put("#AM007.4", context.getString(R.string.errcode_AM007_4));
        codes.put("#AM007.5", context.getString(R.string.errcode_AM007_5));

        codes.put("#AM008.1", context.getString(R.string.errcode_AM008_1));
        codes.put("#AM008.2", context.getString(R.string.errcode_AM008_2));
        codes.put("#AM008.3", context.getString(R.string.errcode_AM008_3));

        codes.put("#CM000.1", context.getString(R.string.errcode_CM000_1));

        codes.put("#CM001.1", context.getString(R.string.errcode_CM001_1));
        codes.put("#CM001.2", context.getString(R.string.errcode_CM001_2));
        codes.put("#CM001.3", context.getString(R.string.errcode_CM001_3));
        codes.put("#CM001.4", context.getString(R.string.errcode_CM001_4));

        codes.put("#CM003.1", context.getString(R.string.errcode_CM003_1));

        codes.put("#CMS001.1", context.getString(R.string.errcode_CMS001_1));
        codes.put("#CMS001.2", context.getString(R.string.errcode_CMS001_2));
        codes.put("#CMS001.3", context.getString(R.string.errcode_CMS001_3));
        codes.put("#CMS001.4", context.getString(R.string.errcode_CMS001_4));

        codes.put("#CMS002.1", context.getString(R.string.errcode_CMS002_1));
        codes.put("#CMS002.2", context.getString(R.string.errcode_CMS002_2));
        codes.put("#CMS002.3", context.getString(R.string.errcode_CMS002_3));
        codes.put("#CMS002.4", context.getString(R.string.errcode_CMS002_4));
        codes.put("#CMS002.5", context.getString(R.string.errcode_CMS002_5));

        codes.put("#CMS003.1", context.getString(R.string.errcode_CMS003_1));

        codes.put("#CMS004.1", context.getString(R.string.errcode_CMS004_1));
        codes.put("#CMS004.2", context.getString(R.string.errcode_CMS004_2));
        codes.put("#CMS004.3", context.getString(R.string.errcode_CMS004_3));

        codes.put("#CMM001.1", context.getString(R.string.errcode_CMM001_1));
        codes.put("#CMM001.2", context.getString(R.string.errcode_CMM001_2));
        codes.put("#CMM001.3", context.getString(R.string.errcode_CMM001_3));

        codes.put("#CMM002.1", context.getString(R.string.errcode_CMM002_1));
        codes.put("#CMM002.2", context.getString(R.string.errcode_CMM002_2));
        codes.put("#CMM002.3", context.getString(R.string.errcode_CMM002_3));

    }

    public static String getError(String code) {

        String error = codes.get(code);
        if (error == null) {
            return code;
        }
        return error;
    }

}
