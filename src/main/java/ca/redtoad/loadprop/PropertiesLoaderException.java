package ca.redtoad.loadprop;

public class PropertiesLoaderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PropertiesLoaderException(String message) {
        super(message);
    }

    public PropertiesLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

}
