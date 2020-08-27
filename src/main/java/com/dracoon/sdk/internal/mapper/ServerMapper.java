package com.dracoon.sdk.internal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.internal.model.ApiEncryptionPasswordPolicies;
import com.dracoon.sdk.internal.model.ApiServerDefaults;
import com.dracoon.sdk.internal.model.ApiServerGeneralSettings;
import com.dracoon.sdk.internal.model.ApiSharesPasswordPolicies;
import com.dracoon.sdk.model.PasswordPolicies;
import com.dracoon.sdk.model.PasswordPoliciesCharacterType;
import com.dracoon.sdk.model.ServerDefaults;
import com.dracoon.sdk.model.ServerGeneralSettings;

public class ServerMapper extends BaseMapper {

    public static ServerGeneralSettings fromApiGeneralSettings(
            ApiServerGeneralSettings apiServerGeneralSettings) {
        if (apiServerGeneralSettings == null) {
            return null;
        }

        ServerGeneralSettings serverGeneralSettings = new ServerGeneralSettings();
        serverGeneralSettings.setSharePasswordSmsEnabled(toBoolean(
                apiServerGeneralSettings.sharePasswordSmsEnabled));
        serverGeneralSettings.setCryptoEnabled(toBoolean(
                apiServerGeneralSettings.cryptoEnabled));
        serverGeneralSettings.setMediaServerEnabled(toBoolean(
                apiServerGeneralSettings.mediaServerEnabled));
        serverGeneralSettings.setWeakPasswordEnabled(toBoolean(
                apiServerGeneralSettings.weakPasswordEnabled));
        return serverGeneralSettings;
    }

    public static ServerDefaults fromApiServerDefaults(ApiServerDefaults apiServerDefaults) {
        if (apiServerDefaults == null) {
            return null;
        }

        ServerDefaults serverDefaults = new ServerDefaults();
        serverDefaults.setDownloadShareExpirationPeriod(
                apiServerDefaults.downloadShareDefaultExpirationPeriod);
        serverDefaults.setUploadShareExpirationPeriod(
                apiServerDefaults.uploadShareDefaultExpirationPeriod);
        serverDefaults.setFileExpirationPeriod(
                apiServerDefaults.fileDefaultExpirationPeriod);
        return serverDefaults;
    }

    public static PasswordPolicies fromApiEncryptionPasswordPolicies(
            ApiEncryptionPasswordPolicies apiEncryptionPasswordPolicies) {
        if (apiEncryptionPasswordPolicies == null) {
            return null;
        }

        PasswordPolicies policies = new PasswordPolicies();
        policies.setMinLength(apiEncryptionPasswordPolicies.minLength);
        policies.setCharacterTypes(fromApiCharacterTypes(
                apiEncryptionPasswordPolicies.characterRules.mustContainCharacters));
        policies.setRejectUserInfo(toBoolean(
                apiEncryptionPasswordPolicies.rejectUserInfo));
        policies.setRejectKeyboardPatterns(toBoolean(
                apiEncryptionPasswordPolicies.rejectKeyboardPatterns));
        policies.setRejectDictionaryWords(false);
        return policies;
    }

    public static PasswordPolicies fromApiSharesPasswordPolicies(
            ApiSharesPasswordPolicies apiSharesPasswordPolicies) {
        if (apiSharesPasswordPolicies == null) {
            return null;
        }

        PasswordPolicies policies = new PasswordPolicies();
        policies.setMinLength(apiSharesPasswordPolicies.minLength);
        policies.setCharacterTypes(fromApiCharacterTypes(
                apiSharesPasswordPolicies.characterRules.mustContainCharacters));
        policies.setRejectUserInfo(toBoolean(
                apiSharesPasswordPolicies.rejectUserInfo));
        policies.setRejectKeyboardPatterns(toBoolean(
                apiSharesPasswordPolicies.rejectKeyboardPatterns));
        policies.setRejectDictionaryWords(toBoolean(
                apiSharesPasswordPolicies.rejectDictionaryWords));
        return policies;
    }

    private static List<PasswordPoliciesCharacterType> fromApiCharacterTypes(
            List<String> apiCharacterTypes) {
        if (apiCharacterTypes == null) {
            return new ArrayList<>();
        }

        ArrayList<PasswordPoliciesCharacterType> characterTypes = new ArrayList<>();
        for (String apiCharacterType : apiCharacterTypes) {
            if (apiCharacterType.equals("all")) {
                characterTypes.add(PasswordPoliciesCharacterType.LOWERCASE);
                characterTypes.add(PasswordPoliciesCharacterType.UPPERCASE);
                characterTypes.add(PasswordPoliciesCharacterType.NUMERIC);
                characterTypes.add(PasswordPoliciesCharacterType.SPECIAL);
                break;
            } else {
                PasswordPoliciesCharacterType characterType =
                        PasswordPoliciesCharacterType.getByValue(apiCharacterType);
                if (characterType != null) {
                    characterTypes.add(characterType);
                }
            }
        }
        return characterTypes;
    }

}
