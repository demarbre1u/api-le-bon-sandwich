package org.lpro.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Utilisateurs implements Serializable
{
    @Id
    @NotNull
    private String username;
    @NotNull
    private String password;

    public Utilisateurs() {}

    public Utilisateurs(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
    
    public String getPassword() 
    {
        return password;
    }

    public String getUsername() 
    {
        return username;
    }
    
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public void setUsername(String username) 
    {
        this.username = username;
    }
}