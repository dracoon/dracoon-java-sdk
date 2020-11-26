package com.dracoon.sdk.internal.validator;

import java.util.UUID;

public class UserValidator extends BaseValidator {

    public static void validateUserId(long id) {
        BaseValidator.validateUserId(id);
    }

    public static void validateAvatarUuid(UUID uuid) {
        ValidatorUtils.validateNotNull("Avatar UUID", uuid);
    }

}
