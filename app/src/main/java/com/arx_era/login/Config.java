package com.arx_era.login;

/**
 * Created by Tronix99 on 06-07-2017.
 */

public class Config {
    //URL to our login.php file
    public static final String LOGIN_URL = "https://arxtest.000webhostapp.com/login.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_USER = "username";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "login";

    //This would be used to store the email of current logged in user
    public static final String EMAIL_SHARED_PREF = "email";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}
