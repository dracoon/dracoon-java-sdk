package com.dracoon.sdk.model;

/**
 * Crypto algorithm model.<br>
 * <br>
 * This model stores information about a crypto algorithm.
 */
@SuppressWarnings("unused")
public class CryptoAlgorithm {

    /**
     * Enumeration of crypto version states.
     */
    public enum State {

        REQUIRED("required"),
        DISCOURAGED("discouraged");

        private String mValue;

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
                if (value.toLowerCase().equals(s.mValue)) {
                    return s;
                }
            }
            return null;
        }

    }

    private String mVersion;
    private State mState;

    /**
     * Returns the version of the crypto algorithm.
     *
     * @return the version
     */
    public String getVersion() {
        return mVersion;
    }

    /**
     * Sets the version of the crypto algorithm.
     *
     * @param name The version.
     */
    public void setVersion(String name) {
        mVersion = name;
    }

    /**
     * Returns the state of the crypto algorithm.
     *
     * @return the state
     */
    public State getState() {
        return mState;
    }

    /**
     * Sets the state of the crypto algorithm.
     *
     * @param state The state.
     */
    public void setState(State state) {
        mState = state;
    }

}
