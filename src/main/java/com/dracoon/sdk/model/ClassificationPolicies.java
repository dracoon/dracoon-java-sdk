package com.dracoon.sdk.model;

/**
 * Classification policies model.<br>
 * <br>
 * This model stores information about classification policies.
 */
@SuppressWarnings("unused")
public class ClassificationPolicies {

    private ShareClassificationPolicies mShareClassificationPolicies;

    /**
     * Returns the classification policies for shares.
     *
     * @return the classification policies for shares
     */
    public ShareClassificationPolicies getShareClassificationPolicies() {
        return mShareClassificationPolicies;
    }

    /**
     * Sets the classification policies for shares.
     *
     * @param classificationPolicies The classification policies for shares.
     */
    public void setShareClassificationPolicies(ShareClassificationPolicies classificationPolicies) {
        mShareClassificationPolicies = classificationPolicies;
    }

}
