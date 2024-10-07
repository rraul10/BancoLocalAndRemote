package org.example.creditcard.errors;

public abstract class TarjetaErrors {
    private final String message;
    public String getMessage(){
        return message;
    }

    public TarjetaErrors(String message){
        this.message = message;
    }

    public static class NumeroInvalido extends TarjetaErrors{
        public NumeroInvalido(String message){
            super("ERROR: " + message);
        }
    }

    public static class CaducidadInvalida extends TarjetaErrors{
        public CaducidadInvalida(String message){
            super("ERROR: " + message);
        }
    }

    public static class NombreInvalido extends TarjetaErrors{
        public NombreInvalido(String message){
            super("ERROR: " + message);
        }
    }
}
