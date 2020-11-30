package com.dracoon.sdk.model;

import java.util.Date;

/**
 * Node comment model.<br>
 * <br>
 * This model stores information about a node comment.
 */
@SuppressWarnings("unused")
public class NodeComment {

    private Long mId;
    private String mText;

    private Date mCreatedAt;
    private UserInfo mCreatedBy;
    private Date mUpdatedAt;
    private UserInfo mUpdatedBy;

    private Boolean mWasChanged;
    private Boolean mWasDeleted;

    /**
     * Returns the ID of the node comment.
     *
     * @return the ID
     */
    public Long getId() {
        return mId;
    }

    /**
     * Sets the ID of the node comment.
     *
     * @param id The ID.
     */
    public void setId(Long id) {
        mId = id;
    }

    /**
     * Returns the text of the node comment.
     *
     * @return the text
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the text of the node comment.
     *
     * @param text The text.
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * Returns the creation date of the node comment.
     *
     * @return the creation date
     */
    public Date getCreatedAt() {
        return mCreatedAt;
    }

    /**
     * Sets the creation date of the node comment.
     *
     * @param createdAt The creation date.
     */
    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    /**
     * Returns information about the user who created the node comment.
     *
     * @return information about the user
     */
    public UserInfo getCreatedBy() {
        return mCreatedBy;
    }

    /**
     * Sets information about the user who created the node comment.
     *
     * @param createdBy Information about the user.
     */
    public void setCreatedBy(UserInfo createdBy) {
        mCreatedBy = createdBy;
    }

    /**
     * Returns the update date of the node comment.
     *
     * @return the update date
     */
    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    /**
     * Sets the update date of the node comment.
     *
     * @param updatedAt The update date.
     */
    public void setUpdatedAt(Date updatedAt) {
        mUpdatedAt = updatedAt;
    }

    /**
     * Returns information about the user who updated the node comment.
     *
     * @return information about the user
     */
    public UserInfo getUpdatedBy() {
        return mUpdatedBy;
    }

    /**
     * Sets information about the user who updated the node comment.
     *
     * @param updatedBy Information about the user.
     */
    public void setUpdatedBy(UserInfo updatedBy) {
        mUpdatedBy = updatedBy;
    }

    /**
     * Returns <code>true</code> if node comment was changed.
     *
     * @return <code>true</code> if node comment was changed; <code>false</code> otherwise
     */
    public Boolean wasChanged() {
        return mWasChanged;
    }

    /**
     * Sets if node comment was changed.
     *
     * @param wasChanged <code>true</code> if node comment was changed; <code>false</code> otherwise.
     */
    public void setWasChanged(Boolean wasChanged) {
        mWasChanged = wasChanged;
    }

    /**
     * Returns <code>true</code> if node comment was deleted.
     *
     * @return <code>true</code> if node comment was deleted; <code>false</code> otherwise
     */
    public Boolean wasDeleted() {
        return mWasDeleted;
    }

    /**
     * Sets if node comment was deleted.
     *
     * @param wasDeleted <code>true</code> if node comment was deleted; <code>false</code> otherwise.
     */
    public void setWasDeleted(Boolean wasDeleted) {
        mWasDeleted = wasDeleted;
    }

}
