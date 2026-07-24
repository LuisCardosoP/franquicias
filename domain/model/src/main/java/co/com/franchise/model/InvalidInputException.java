package co.com.franchise.model;

public class InvalidInputException extends RuntimeException {

    private final TechnicalMessage technicalMessage;

    public InvalidInputException(TechnicalMessage technicalMessage) {
        this(technicalMessage.getMessage(), technicalMessage);
    }

    public InvalidInputException(String message, TechnicalMessage technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }

    public InvalidInputException(Throwable cause, TechnicalMessage technicalMessage) {
        super(cause);
        this.technicalMessage = technicalMessage;
    }

    public TechnicalMessage getTechnicalMessage() {
        return technicalMessage;
    }
}
