package com.dracoon.sdk.internal.mapper;

import com.dracoon.sdk.internal.model.ApiServerGeneralSettings;
import com.dracoon.sdk.model.ServerGeneralSettings;

public class ServerMapper {

    public static ServerGeneralSettings fromApiGeneralSettings(
            ApiServerGeneralSettings apiServerGeneralSettings) {
        if (apiServerGeneralSettings == null) {
            return null;
        }

        ServerGeneralSettings serverGeneralSettings = new ServerGeneralSettings();
        serverGeneralSettings.setSharePasswordSmsEnabled(
                apiServerGeneralSettings.sharePasswordSmsEnabled);
        serverGeneralSettings.setCryptoEnabled(apiServerGeneralSettings.cryptoEnabled);
        serverGeneralSettings.setMediaServerEnabled(apiServerGeneralSettings.mediaServerEnabled);
        serverGeneralSettings.setWeakPasswordEnabled(apiServerGeneralSettings.weakPasswordEnabled);
        return serverGeneralSettings;
    }

}
