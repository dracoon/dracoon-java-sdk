package com.dracoon.sdk.internal.api.mapper;

import java.util.ArrayList;
import java.util.List;

import com.dracoon.sdk.internal.api.model.ApiServerClassificationPolicies;
import com.dracoon.sdk.internal.api.model.ApiServerDefaults;
import com.dracoon.sdk.internal.api.model.ApiServerGeneralSettings;
import com.dracoon.sdk.internal.api.model.ApiServerShareClassificationPolicies;
import com.dracoon.sdk.internal.api.model.ApiSharesPasswordPolicies;
import com.dracoon.sdk.internal.api.model.ApiEncryptionPasswordPolicies;
import com.dracoon.sdk.model.Classification;
import com.dracoon.sdk.model.ClassificationPolicies;
import com.dracoon.sdk.model.PasswordPolicies;
import com.dracoon.sdk.model.PasswordPoliciesCharacterType;
import com.dracoon.sdk.model.ServerDefaults;
import com.dracoon.sdk.model.ServerGeneralSettings;
import com.dracoon.sdk.model.ShareClassificationPolicies;

public class ServerMapper extends BaseMapper {

    private ServerMapper() {
        super();
    }

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

    public static ClassificationPolicies fromApiClassificationPolicies(
            ApiServerClassificationPolicies apiServerClassificationPolicies) {
        if (apiServerClassificationPolicies == null) {
            return null;
        }

        ClassificationPolicies policies = new ClassificationPolicies();
        policies.setShareClassificationPolicies(fromApiShareClassificationPolicies(
                apiServerClassificationPolicies.shareClassificationPolicies));
        return policies;
    }

    private static ShareClassificationPolicies fromApiShareClassificationPolicies(
            ApiServerShareClassificationPolicies apiServerShareClassificationPolicies) {
        if (apiServerShareClassificationPolicies == null) {
            return null;
        }

        ShareClassificationPolicies policies = new ShareClassificationPolicies();
        if (apiServerShareClassificationPolicies.classificationRequiresSharePassword != null) {
            policies.setRequirePasswordClassification(Classification.getByValue(
                    apiServerShareClassificationPolicies.classificationRequiresSharePassword));
        }
        return policies;
    }

}
