package co.com.franchise.model;

public class EntityNotFoundException extends RuntimeException {

    private final TechnicalMessage technicalMessage;

    public EntityNotFoundException(TechnicalMessage technicalMessage) {
        this(technicalMessage.getMessage(), technicalMessage);
    }

    public EntityNotFoundException(String message, TechnicalMessage technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }

    public EntityNotFoundException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause);
        this.technicalMessage = technicalMessage;
    }

    public TechnicalMessage getTechnicalMessage() {
        return technicalMessage;
    }
}
