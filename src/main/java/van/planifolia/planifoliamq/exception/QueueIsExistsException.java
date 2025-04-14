

package van.planifolia.planifoliamq.exception;
/**
 * @author Van.Planifolia
 * @create 2025/4/14 10:52
 * @description
 */
public class QueueIsExistsException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public QueueIsExistsException(String message) {
        super(message);
    }
}
