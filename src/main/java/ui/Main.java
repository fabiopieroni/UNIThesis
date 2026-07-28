package ui;

import business.AuthService;
import business.Sessione;
import business.strategy.RequisitiIngegneriaStrategy;
import business.strategy.VerificaRequisitiContext;
import business.strategy.VerificaRequisitiStrategy;
import dao.NotificaDAO;
import dao.RevisioneCapitoloDAO;
import dao.impl.NotificaDAOimpl;
import dao.impl.RevisioneCapitoloDAOimpl;
import model.Notifica;
import model.RevisioneCapitolo;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   TEST AUTENTICAZIONE E CONTROLLO RUOLI  ");
        System.out.println("==========================================\n");

        AuthService authService = new AuthService();

        // --- TEST 1: Studente ---
        System.out.println("📌 [TEST 1] Login Studente...");
        if (authService.login("mario.rossi@studenti.it", "password123")) {
            System.out.println("✅ Login RIUSCITO!");
            System.out.println("   Utente: " + Sessione.getInstance().getUtenteCorrente().getNome() + " " + Sessione.getInstance().getUtenteCorrente().getCognome());
            System.out.println("   Ruolo Studente? " + Sessione.getInstance().isStudente());
        } else {
            System.err.println("❌ Login FALLITO.");
        }
        authService.logout();

        System.out.println("\n------------------------------------------\n");

        // --- TEST 2: Professore ---
        System.out.println("📌 [TEST 2] Login Professore...");
        if (authService.login("luigi.verdi@unifi.it", "prof123")) {
            System.out.println("✅ Login RIUSCITO!");
            System.out.println("   Utente: " + Sessione.getInstance().getUtenteCorrente().getNome() + " " + Sessione.getInstance().getUtenteCorrente().getCognome());
            System.out.println("   Ruolo Professore? " + Sessione.getInstance().isProfessore());
        } else {
            System.err.println("❌ Login FALLITO.");
        }
        authService.logout();

        System.out.println("\n------------------------------------------\n");

        // --- TEST 3: Segreteria ---
        System.out.println("📌 [TEST 3] Login Segreteria...");
        if (authService.login("segreteria@unifi.it", "admin123")) {
            System.out.println("✅ Login RIUSCITO!");
            System.out.println("   Utente: " + Sessione.getInstance().getUtenteCorrente().getNome() + " " + Sessione.getInstance().getUtenteCorrente().getCognome());
            System.out.println("   Ruolo Segreteria? " + Sessione.getInstance().isSegreteria());
        } else {
            System.err.println("❌ Login FALLITO.");
        }
        authService.logout();

        System.out.println("\n------------------------------------------\n");

        // --- TEST 4: Password Errata ---
        System.out.println("📌 [TEST 4] Tentativo password errata...");
        if (!authService.login("mario.rossi@studenti.it", "sbagliata")) {
            System.out.println("✅ Comportamento corretto: Accesso negato!");
        } else {
            System.err.println("❌ Errore: Ha fatto entrare l'utente con password sbagliata!");
        }

        System.out.println("\n==========================================");
        System.out.println("   TEST STRATEGY VERIFICA REQUISITI CFU   ");
        System.out.println("==========================================\n");

        // --- TEST 5: Strategy Ingegneria (120 CFU) ---
        System.out.println("📌 [TEST 5] Strategy Ingegneria (Soglia >= 120 CFU)...");
        VerificaRequisitiStrategy stratIng = new RequisitiIngegneriaStrategy();

        if (stratIng.verificaRequisiti(125)) {
            System.out.println("✅ Studente con 125 CFU: Idoneo!");
        } else {
            System.err.println("❌ Errore Strategy Ingegneria (125 CFU dovrebbero bastare).");
        }

        if (!stratIng.verificaRequisiti(100)) {
            System.out.println("✅ Studente con 100 CFU: Correttamente respinto!");
        } else {
            System.err.println("❌ Errore Strategy Ingegneria (100 CFU non devono bastare).");
        }

        System.out.println("\n------------------------------------------\n");

        // --- TEST 6: Context Strategy dinamico ---
        System.out.println("📌 [TEST 6] Factory Context Strategy (Economia)...");
        VerificaRequisitiStrategy stratEco = VerificaRequisitiContext.getStrategy("economia");

        if (stratEco.verificaRequisiti(105)) {
            System.out.println("✅ Studente Economia con 105 CFU: Idoneo!");
        } else {
            System.err.println("❌ Errore Context Economia.");
        }

        System.out.println("\n==========================================");
        System.out.println("  TEST REVISIONE CAPITOLI E NOTIFICHE DAO ");
        System.out.println("==========================================\n");

        RevisioneCapitoloDAO revisioneDAO = new RevisioneCapitoloDAOimpl();
        NotificaDAO notificaDAO = new NotificaDAOimpl();

        // --- TEST 7: Inserimento Revisione ---
        System.out.println("📌 [TEST 7] Creazione Nuova Revisione Capitolo...");
        RevisioneCapitolo nuovaRev = new RevisioneCapitolo(1, 2, "Capitolo 2: Progettazione Database", "/pdf/capitolo2.pdf");
        if (revisioneDAO.salva(nuovaRev)) {
            System.out.println("✅ Revisione salvata con successo! ID generato: " + nuovaRev.getIdRevisione());
        } else {
            System.err.println("❌ Errore durante il salvataggio della revisione.");
        }

        System.out.println("\n------------------------------------------\n");

        // --- TEST 8: Lettura Revisioni ---
        System.out.println("📌 [TEST 8] Lettura Revisioni per Tesi ID 1...");
        List<RevisioneCapitolo> listaRevisioni = revisioneDAO.findByTesi(1);
        if (!listaRevisioni.isEmpty()) {
            System.out.println("✅ Trovate " + listaRevisioni.size() + " revisioni per la tesi 1:");
            for (RevisioneCapitolo r : listaRevisioni) {
                System.out.println("   - Cap " + r.getNumCapitolo() + ": " + r.getTitoloCapitolo() + " [" + r.getStatoRevisione() + "]");
            }
        } else {
            System.err.println("❌ Nessuna revisione trovata per la tesi 1.");
        }

        System.out.println("\n------------------------------------------\n");

        // --- TEST 9: Aggiornamento Stato Revisione ---
        if (!listaRevisioni.isEmpty()) {
            int idRevToUpdate = listaRevisioni.get(0).getIdRevisione();
            System.out.println("📌 [TEST 9] Aggiornamento Valutazione Professore su Revisione ID " + idRevToUpdate + "...");
            if (revisioneDAO.aggiornaStatoENote(idRevToUpdate, "APPROVATO", "Ottimo lavoro, capitolo approvato!")) {
                System.out.println("✅ Stato e note del professore aggiornati con successo!");
            } else {
                System.err.println("❌ Errore nell'aggiornamento della revisione.");
            }
        }

        System.out.println("\n------------------------------------------\n");

        // --- TEST 10: Inserimento Notifica ---
        System.out.println("📌 [TEST 10] Creazione Notifica per Utente ID 1...");
        Notifica nuovaNotifica = new Notifica(1, "La tua revisione per il Capitolo 1 è stata approvata!");
        if (notificaDAO.salva(nuovaNotifica)) {
            System.out.println("✅ Notifica creata con successo! ID generato: " + nuovaNotifica.getIdNotifica());
        } else {
            System.err.println("❌ Errore nella creazione della notifica.");
        }

        System.out.println("\n------------------------------------------\n");

        // --- TEST 11: Lettura e Segnalazione Notifiche ---
        System.out.println("📌 [TEST 11] Lettura e gestione Notifiche per Utente ID 1...");
        List<Notifica> notificheUtente = notificaDAO.findByUtente(1);
        if (!notificheUtente.isEmpty()) {
            System.out.println("✅ Trovate " + notificheUtente.size() + " notifiche per l'utente 1:");
            for (Notifica n : notificheUtente) {
                System.out.println("   - [" + (n.isLetta() ? "LETTA" : "NON LETTA") + "] " + n.getMessaggio());
            }

            int idNotifica = notificheUtente.get(0).getIdNotifica();
            if (notificaDAO.segnaComeLetta(idNotifica)) {
                System.out.println("✅ Notifica ID " + idNotifica + " segnata come LETTA!");
            } else {
                System.err.println("❌ Errore nell'aggiornamento dello stato della notifica.");
            }
        } else {
            System.err.println("❌ Nessuna notifica trovata per l'utente 1.");
        }

        System.out.println("\n==========================================");
        System.out.println("            TEST COMPLETATI               ");
        System.out.println("==========================================");
    }
}