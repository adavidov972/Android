package com.example.avidavidov.tictactoe_clientside;

import android.media.Image;

/**
 * Created by avi.davidov on 10/02/2017.
 */

public class Users {

    Image userImage;
    String userName;
    String password;
    Boolean isAvalable;

    public Users(Image userImage, String userName, String password, Boolean isAvalable) {
        this.userImage = userImage;
        this.userName = userName;
        this.password = password;
        this.isAvalable = isAvalable;
    }

    public Users(String userName, String password, Boolean isAvalable) {
        this.userName = userName;
        this.password = password;
        this.isAvalable = isAvalable;
    }

    public Users(String userName, Image userImage) {
        this.userName = userName;
        this.userImage = userImage;
    }

    public Image getUserImage() {
        return userImage;
    }

    public void setUserImage(Image userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAvalable() {
        return isAvalable;
    }

    public void setAvalable(Boolean avalable) {
        isAvalable = avalable;
    }
}
