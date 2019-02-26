package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiServerGeneralSettings;
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

}
