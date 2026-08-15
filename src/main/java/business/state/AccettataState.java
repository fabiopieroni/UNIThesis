package business.state;

import business.exception.TransizioneNonValidaException;
import model.Tesi;

public class AccettataState implements TesiState {

    @Override
    public String getNomeStato() {
        return "ACCETTATA";
    }

    @Override
    public void pubblica(Tesi tesi) {
        throw new TransizioneNonValidaException("Una tesi accettata non può essere ripubblicata.");
    }

    @Override
    public void assegna(Tesi tesi) {
        throw new TransizioneNonValidaException("Una tesi accettata non può essere assegnata di nuovo.");
    }
}