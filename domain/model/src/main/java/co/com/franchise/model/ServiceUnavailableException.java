package co.com.franchise.model;

public class ServiceUnavailableException extends RuntimeException {

    private final TechnicalMessage technicalMessage;

    public ServiceUnavailableException(TechnicalMessage technicalMessage) {
        this(technicalMessage.getMessage(), technicalMessage);
    }

    public ServiceUnavailableException(String message, TechnicalMessage technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }

    public ServiceUnavailableException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause);
        this.technicalMessage = technicalMessage;
    }

    public TechnicalMessage getTechnicalMessage() {
        return technicalMessage;
    }
}
