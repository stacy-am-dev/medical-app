package git.stacyamdev.medicalapp.model.exception;

public class MedicalException extends Exception {

    public MedicalException() {
    }

    public MedicalException(String message) {
        super(message);
    }

    public MedicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public MedicalException(Throwable cause) {
        super(cause);
    }

    public MedicalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
