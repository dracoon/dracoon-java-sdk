package com.dracoon.sdk.model;

/**
 * Share classification policies model.<br>
 * <br>
 * This model stores information about classification policies for shares.
 */
@SuppressWarnings("unused")
public class ShareClassificationPolicies {

    private Classification mRequirePasswordClassification;

    /**
     * Returns the classification from which passwords are required, if there is a requirement.
     *
     * @return the classification, or <code>null</code>
     */
    public Classification getRequirePasswordClassification() {
        return mRequirePasswordClassification;
    }

    /**
     * Sets the classification from which passwords are required.
     *
     * @param classification The classification.
     */
    public void setRequirePasswordClassification(Classification classification) {
        mRequirePasswordClassification = classification;
    }

}
