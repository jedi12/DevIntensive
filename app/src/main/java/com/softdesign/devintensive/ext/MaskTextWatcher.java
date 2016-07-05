/*
    Programmed by Andrey Pribytkov
 */
package com.softdesign.devintensive.ext;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Checks EditText the string in accordance with the mask.
 * Errors are displayed in EditText parent (TextInputLayout).
 */
public class MaskTextWatcher implements TextWatcher {
    private static final String TAG = "MaskTextWatcher";

    public static final int PHONE_MASK = 1;
    public static final int EMAIL_MASK = 2;
    public static final int URL_MASK = 3;

    private TextInputLayout til;
    private String mask;
    private int maskType;
    private String formattedText;
    private int minPhoneLength;
    private int digitLength;
    private String regExp;
    private String urlPrefix;

    /**
     *
     * @param til The TextInputLayout for errors displaying.
     * @param mask The template string. 'X' in the mask - is any one required character.
     * @param maskType The type of mask (PHONE_MASK, EMAIL_MASK or URL_MASK)
     */
    public MaskTextWatcher(TextInputLayout til, String mask, int maskType) {
        this.til = til;
        this.mask = mask;
        this.maskType = maskType;

        initProps();
    }

    private void initProps() {
        formattedText = "";

        switch (maskType) {
            case PHONE_MASK:
                minPhoneLength = mask.replaceAll("[^X]", "").length();

                break;

            case EMAIL_MASK:
                int index1 = mask.lastIndexOf("@");
                int index2 = mask.lastIndexOf(".");
                int minUserName = mask.substring(0, index1).length();
                int minDomain2Name = mask.substring(index1 + 1, index2).length();
                int minDomain1Name = mask.substring(index2 + 1, mask.length()).length();

                regExp = "^([a-zA-Z0-9!#$%&'*+-/=?^_`{|}~]{" + minUserName + ",64})@([a-zA-Z0-9-]{"
                        + minDomain2Name + ",})\\.([a-zA-Z0-9]{" + minDomain1Name + ",})$";
                break;

            case URL_MASK:
                int index = mask.lastIndexOf("/X");
                urlPrefix = mask.substring(0, index + 1);
                int minLength = mask.substring(index + 1, mask.length()).length();

                regExp = "^(.*)" + urlPrefix + "(.{" + minLength + ",})$";

                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().equals(formattedText)) {
            return;
        }

        switch (maskType) {
            case PHONE_MASK:
                validateAsPhone(s);
                break;

            case EMAIL_MASK:
                validateAsEmail(s);
                break;

            case URL_MASK:
                validateAsUrl(s);
                break;
        }
    }

    private void validateAsPhone(Editable s) {
        formattedText = "";

        String phoneDigit = s.toString().replaceAll("\\D", "");
        int j = 0;
        digitLength = phoneDigit.length();

        if (minPhoneLength > digitLength) {
            til.setError(mask);
            til.setErrorEnabled(true);
        } else {
            til.setError(null);
            til.setErrorEnabled(false);
        }

        for (int i = 0; i < mask.length(); i++) {

            if (j >= digitLength) {
                break;
            }

            char charMask = mask.charAt(i);
            if (charMask != 'X' && charMask != 'x') {
                formattedText = formattedText + charMask;
                continue;
            }

            char charDigit = phoneDigit.toString().charAt(j);
            formattedText = formattedText + charDigit;

            j = j + 1;
        }

        s.replace(0, s.length(), formattedText);
    }

    private void validateAsEmail(Editable s) {
        if (s.toString().matches(regExp)) {
            til.setError(null);
            til.setErrorEnabled(false);
        } else {
            til.setError(mask);
            til.setErrorEnabled(true);
        }
    }

    private void validateAsUrl(Editable s) {
        String value = s.toString();
        if (s.toString().matches(regExp)) {
            int index = value.indexOf(urlPrefix);
            if (index == -1) {
                return;
            }
            formattedText = value.substring(index, value.length());

            s.replace(0, s.length(), formattedText);

            til.setError(null);
            til.setErrorEnabled(false);
        } else {
            til.setError(mask);
            til.setErrorEnabled(true);
        }
    }
}
