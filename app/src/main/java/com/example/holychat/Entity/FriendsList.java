package com.example.holychat.Entity;

import java.io.Serializable;

public class FriendsList {

    private String Account;
    private String Friends_Account;
    private String Friends_Name;

    public FriendsList(String account, String friends_Account, String friends_Name) {
        Account = account;
        Friends_Account = friends_Account;
        Friends_Name = friends_Name;
    }

    @Override
    public String toString() {
        return "FriendsList{" +
                "Account='" + Account + '\'' +
                ", Friends_Account='" + Friends_Account + '\'' +
                ", Friends_Name='" + Friends_Name + '\'' +
                '}';
    }

    public FriendsList(String account, String friends_Account) {
        Account = account;
        Friends_Account = friends_Account;
    }

    public FriendsList() {

    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getFriends_Account() {
        return Friends_Account;
    }

    public void setFriends_Account(String friends_Account) {
        Friends_Account = friends_Account;
    }

    public String getFriends_Name() {
        return Friends_Name;
    }

    public void setFriends_Name(String friends_Name) {
        Friends_Name = friends_Name;
    }
}
