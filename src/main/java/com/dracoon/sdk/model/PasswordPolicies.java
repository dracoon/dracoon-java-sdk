package com.dracoon.sdk.model;

import java.util.List;

/**
 * Password policies model.<br>
 * <br>
 * This model stores information about the policies a password must comply with.
 */
@SuppressWarnings("unused")
public class PasswordPolicies {

    private Integer mMinLength;
    private List<PasswordPoliciesCharacterType> mCharacterTypes;
    private Boolean mRejectUserInfo;
    private Boolean mRejectKeyboardPatterns;
    private Boolean mRejectDictionaryWords;

    /**
     * Returns the required minimum length for the password.
     *
     * @return the required minimum length
     */
    public Integer getMinLength() {
        return mMinLength;
    }

    /**
     * Sets the required minimum length for the password.
     *
     * @param minLength The required minimum length.
     */
    public void setMinLength(Integer minLength) {
        mMinLength = minLength;
    }

    /**
     * Returns the list of required character types for the password.
     *
     * @return the ID
     */
    public List<PasswordPoliciesCharacterType> getCharacterTypes() {
        return mCharacterTypes;
    }

    /**
     * Sets the list of required character types for the password.
     *
     * @param characterTypes The list of required character types.
     */
    public void setCharacterTypes(List<PasswordPoliciesCharacterType> characterTypes) {
        mCharacterTypes = characterTypes;
    }

    /**
     * Returns <code>true</code> if user information is rejected for the password.
     *
     * @return <code>true</code> if user information is rejected; <code>false</code> otherwise
     */
    public Boolean getRejectUserInfo() {
        return mRejectUserInfo;
    }

    /**
     * Sets if user information is rejected for the password.
     *
     * @param rejectUserInfo <code>true</code> if user information is rejected;
     *                       <code>false</code> otherwise.
     */
    public void setRejectUserInfo(Boolean rejectUserInfo) {
        mRejectUserInfo = rejectUserInfo;
    }

    /**
     * Returns <code>true</code> if keyboard patterns are rejected for the password.
     *
     * @return <code>true</code> if keyboard patterns are rejected; <code>false</code> otherwise
     */
    public Boolean getRejectKeyboardPatterns() {
        return mRejectKeyboardPatterns;
    }

    /**
     * Sets if keyboard patterns are rejected for the password.
     *
     * @param rejectKeyboardPatterns <code>true</code> if keyboard patterns are rejected;
     *                               <code>false</code> otherwise.
     */
    public void setRejectKeyboardPatterns(Boolean rejectKeyboardPatterns) {
        mRejectKeyboardPatterns = rejectKeyboardPatterns;
    }

    /**
     * Returns <code>true</code> if dictionary words are rejected for the password.
     *
     * @return <code>true</code> if dictionary words are rejected; <code>false</code> otherwise
     */
    public Boolean getRejectDictionaryWords() {
        return mRejectDictionaryWords;
    }

    /**
     * Sets if dictionary words are rejected for the password.
     *
     * @param rejectDictionaryWords <code>true</code> if dictionary words are rejected;
     *                              <code>false</code> otherwise.
     */
    public void setRejectDictionaryWords(Boolean rejectDictionaryWords) {
        mRejectDictionaryWords = rejectDictionaryWords;
    }

}
