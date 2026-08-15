package business.state;

import business.exception.TransizioneNonValidaException;
import model.Tesi;

public class ConsegnataState implements TesiState {

    @Override
    public String getNomeStato() {
        return "CONSEGNATA";
    }

    @Override
    public void pubblica(Tesi tesi) {
        throw new TransizioneNonValidaException("Una tesi consegnata non può essere ripubblicata.");
    }

    @Override
    public void assegna(Tesi tesi) {
        throw new TransizioneNonValidaException("Una tesi consegnata non può essere assegnata di nuovo.");
    }
}