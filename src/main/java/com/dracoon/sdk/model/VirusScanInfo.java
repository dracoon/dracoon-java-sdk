package com.dracoon.sdk.model;

import java.util.Date;

/**
 * Virus scan info model.<br>
 * <br>
 * This model stores information about a virus scan.
 */
@SuppressWarnings("unused")
public class VirusScanInfo {

    /**
     * Enumeration of scan verdict types.
     */
    public enum Verdict {

        NO_SCANNING("no_scanning"),
        IN_PROGRESS("in_progress"),
        CLEAN("clean"),
        MALICIOUS("malicious");

        private final String mValue;

        /**
         * Constructs a new enumeration constant with the provided scan verdict type value.
         *
         * @param value The scan verdict type value.
         */
        Verdict(String value) {
            mValue = value;
        }

        /**
         * Returns the value of the scan verdict type.
         *
         * @return the scan verdict type value
         */
        public String getValue() {
            return mValue;
        }

        /**
         * Finds a enumeration constant by a provided scan verdict type value.
         *
         * @param value The scan verdict type value of the constant to return.
         *
         * @return the appropriate enumeration constant, or <code>null</code> if no matching
         *         enumeration constant could be found
         */
        public static Verdict getByValue(String value) {
            if (value == null) {
                return null;
            }

            for (Verdict v : Verdict.values()) {
                if (value.equals(v.mValue)) {
                    return v;
                }
            }
            return null;
        }

    }

    private Verdict mVerdict;
    private Date mLastScannedAt;

    /**
     * Returns the verdict of the scan.
     *
     * @return the verdict
     */
    public Verdict getVerdict() {
        return mVerdict;
    }

    /**
     * Sets the verdict of the scan.
     *
     * @param verdict The verdict.
     */
    public void setVerdict(Verdict verdict) {
        mVerdict = verdict;
    }

    /**
     * Returns the last scan date, if the scan has been completed.
     *
     * @return the last scan date, or <code>null</code>
     */
    public Date getLastScannedAt() {
        return mLastScannedAt;
    }

    /**
     * Sets the last scan date.
     *
     * @param lastScannedAt The last scan date.
     */
    public void setLastScannedAt(Date lastScannedAt) {
        mLastScannedAt = lastScannedAt;
    }

}
