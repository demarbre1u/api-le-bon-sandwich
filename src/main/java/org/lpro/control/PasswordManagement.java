package org.lpro.control;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordManagement
{
    public static String digestPassword(String plainTextPwd)
    {
        try
        {
            String hashed = BCrypt.hashpw(plainTextPwd, BCrypt.gensalt());

            return hashed;
        }
        catch(Exception e)
        {
            throw new RuntimeException("An error has occured while trying to hash the password");
        }
    }
}