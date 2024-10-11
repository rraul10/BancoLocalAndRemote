package org.example.client.service.errors;

public abstract class ServiceError {
    private final String message;
    public String getMessage(){
        return message;
    }

    public ServiceError(String message){
        this.message = message;
    }
    public static class UsersLoadError extends ServiceError{
        public UsersLoadError(String message){
            super("ERROR: " + message);
        }
    }

    public static class UservalidatorError extends ServiceError{
        public UservalidatorError(String message){
            super("ERROR: " + message);
        }
    }
    public static class tarjetaCreditValidatorError extends ServiceError{
        public tarjetaCreditValidatorError(String message){
            super("ERROR: " + message);
        }
    }
    public static class UserNotCreated extends ServiceError{
        public UserNotCreated(String message){
            super("ERROR: " + message);
        }
    }

    public static class UserNotDeleted extends ServiceError{
        public UserNotDeleted(String message){
            super("ERROR: " + message);
        }
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
