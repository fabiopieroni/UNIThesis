package business.exception;

public class TransizioneNonValidaException extends RuntimeException {
  public TransizioneNonValidaException(String messaggio) {
    super(messaggio);
  }
}
