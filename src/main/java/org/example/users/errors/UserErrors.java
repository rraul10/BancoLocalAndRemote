package org.example.users.errors;

public abstract class UserErrors {
    private final String message;
    public String getMessage(){
        return message;
    }

    public UserErrors(String message){
        this.message = message;
    }

    public static class NombreInvalido extends UserErrors{
        public NombreInvalido(String message){
            super("ERROR: " + message);
        }
    }

    public static class EmailInvalido extends UserErrors{
        public EmailInvalido(String message){
            super("ERROR: " + message);
        }
    }
}
