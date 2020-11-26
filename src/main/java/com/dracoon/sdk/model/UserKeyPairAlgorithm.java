package com.dracoon.sdk.model;

/**
 * User key pair algorithm model.<br>
 * <br>
 * This model stores information about a user key pair algorithm.
 */
@SuppressWarnings("unused")
public class UserKeyPairAlgorithm {

    /**
     * Enumeration of algorithm versions.
     */
    public enum Version {

        RSA2048("A"),
        RSA4096("RSA-4096");

        private final String mValue;

        /**
         * Constructs a new enumeration constant with the provided version value.
         *
         * @param value The version value.
         */
        Version(String value) {
            mValue = value;
        }

        /**
         * Returns the value of the version.
         *
         * @return the version value
         */
        public String getValue() {
            return mValue;
        }

        /**
         * Finds a enumeration constant by a provided version value.
         *
         * @param value The version value of the constant to return.
         *
         * @return the appropriate enumeration constant, or <code>null</code> if no matching
         *         enumeration constant could be found
         */
        public static Version getByValue(String value) {
            if (value == null) {
                return null;
            }

            for (Version v : Version.values()) {
                if (value.equals(v.mValue)) {
                    return v;
                }
            }
            return null;
        }

    }

    /**
     * Enumeration of algorithm states.
     */
    public enum State {

        REQUIRED("REQUIRED"),
        DISCOURAGED("DISCOURAGED");

        private final String mValue;

        /**
         * Constructs a new enumeration constant with the provided state value.
         *
         * @param value The state value.
         */
        State(String value) {
            mValue = value;
        }

        /**
         * Returns the value of the state.
         *
         * @return the state value
         */
        public String getValue() {
            return mValue;
        }

        /**
         * Finds a enumeration constant by a provided state value.
         *
         * @param value The state value of the constant to return.
         *
         * @return the appropriate enumeration constant, or <code>null</code> if no matching
         *         enumeration constant could be found
         */
        public static State getByValue(String value) {
            if (value == null) {
                return null;
            }

            for (State s : State.values()) {
                if (value.equals(s.mValue)) {
                    return s;
                }
            }
            return null;
        }

    }

    private Version mVersion;
    private State mState;

    /**
     * Returns the version of the user key pair algorithm.
     *
     * @return the version
     */
    public Version getVersion() {
        return mVersion;
    }

    /**
     * Sets the version of the user key pair algorithm.
     *
     * @param version The version.
     */
    public void setVersion(Version version) {
        mVersion = version;
    }

    /**
     * Returns the state of the user key pair algorithm.
     *
     * @return the state
     */
    public State getState() {
        return mState;
    }

    /**
     * Sets the state of the user key pair algorithm.
     *
     * @param state The state.
     */
    public void setState(State state) {
        mState = state;
    }

}
