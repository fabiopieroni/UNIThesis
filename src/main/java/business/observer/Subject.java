package business.observer;

public interface Subject {
    void aggiungiObserver(Observer observer);
    void rimuoviObserver(Observer observer);
    void notificaObservers(String messaggio, int idDestinatario);
}