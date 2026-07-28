package business.strategy;

public interface VerificaRequisitiStrategy {

    /**
     * Verifica se uno studente soddisfa i requisiti per richiedere la tesi.
     *
     * @param cfuAcquisiti Il numero di CFU accumulati dallo studente
     * @return true se i requisiti sono soddisfatti, false altrimenti
     */
    boolean verificaRequisiti(int cfuAcquisiti);
}