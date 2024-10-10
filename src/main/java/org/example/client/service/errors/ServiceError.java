package org.example.client.service.errors;

public abstract class ServiceError {
    private final String message;
    public String getMessage(){
        return message;
    }

    public ServiceError(String message){
        this.message = message;
    }

    public static class UserNotFound extends ServiceError{
        public UserNotFound(String message){
            super("ERROR: " + message);
        }
    }

    public static class UserNotUpdated extends ServiceError{
        public UserNotUpdated(String message){
            super("ERROR: " + message);
        }
    }
}
