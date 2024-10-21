package org.example.service.errors;

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

    public static class ClientNotCreated extends ServiceError{
        public ClientNotCreated(String message){
            super("ERROR: " + message);
        }
    }
    public static class ClienteNotFound extends ServiceError{
        public ClienteNotFound(String message){
            super("ERROR: " + message);
        }
    }

    public static class ClienteLoadErrors extends ServiceError{
        public ClienteLoadErrors(String message){
            super("ERROR: " + message);
        }
    }

    public static class TarjetaNotFound extends ServiceError{
        public TarjetaNotFound(String message){
            super("ERROR: " + message);
        }
    }

    public static class TarjeteNotCreated extends ServiceError{
        public TarjeteNotCreated(String message){
            super("ERROR: " + message);
        }
    }
    public static class TarjeteNotUpdated extends ServiceError{
        public TarjeteNotUpdated(String message){
            super("ERROR: " + message);
        }
    }
    public static class TarjeteNotDeleted extends ServiceError{
        public TarjeteNotDeleted(String message){
            super("ERROR: " + message);
        }
    }


    public static class TarjetasLoadError extends ServiceError{
        public TarjetasLoadError(String message){
            super("ERROR: " + message);
        }
    }

    public static class ExportError extends ServiceError{
        public ExportError(String message){
            super("ERROR: " + message);
        }
    }

    public static class ImportErrors extends ServiceError{
        public ImportErrors(String message){
            super("ERROR: " + message);
        }
    }

}
