package com.jsmiranda.acad.firebase_login;

/**
 * Created by josu on 12/26/2016.
 */

public class User {
    public String email,role;

    public User(){

    }

    public User(String email, String role){
        this.email = email;
        this.role = role;
    }
}
