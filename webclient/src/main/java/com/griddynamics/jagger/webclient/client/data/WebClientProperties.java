package com.griddynamics.jagger.webclient.client.data;

import java.io.Serializable;

public class WebClientProperties implements Serializable {

    private boolean userCommentEditAvailable = false;

    private boolean userCommentStoreAvailable = false;

    private int userCommentMaxLength = 1000;

    public boolean isUserCommentEditAvailable() {
        return userCommentEditAvailable;
    }

    public void setUserCommentEditAvailable(boolean userCommentEditAvailable) {
        this.userCommentEditAvailable = userCommentEditAvailable;
    }

    public int getUserCommentMaxLength() {
        return userCommentMaxLength;
    }

    public void setUserCommentMaxLength(int userCommentMaxLength) {
        this.userCommentMaxLength = userCommentMaxLength;
    }

    public boolean isUserCommentStoreAvailable() {
        return userCommentStoreAvailable;
    }

    public void setUserCommentStoreAvailable(boolean userCommentStoreAvailable) {
        this.userCommentStoreAvailable = userCommentStoreAvailable;
    }
}
