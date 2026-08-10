package business.observer;

import dao.NotificaDAO;
import dao.impl.NotificaDAOimpl;
import model.Notifica;

public class GestoreNotifiche implements Observer {
    private final NotificaDAO notificaDAO;

    public GestoreNotifiche() {
        this.notificaDAO = new NotificaDAOimpl();
    }

    @Override
    public void update(String messaggio, int idDestinatario) {
        Notifica nuovaNotifica = new Notifica(idDestinatario, messaggio);
        boolean salvata = notificaDAO.salva(nuovaNotifica);
        if (salvata) {
            System.out.println("🔔 [OBSERVER] Notifica creata sul DB per l'utente ID " + idDestinatario + ": " + messaggio);
        } else {
            System.err.println("❌ [OBSERVER] Errore: Impossibile salvare la notifica sul Database.");
        }
    }
}