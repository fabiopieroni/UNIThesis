package dao;

import dao.impl.RevisioneCapitoloDAOimpl;
import dao.impl.TesiDAOimpl;
import model.RevisioneCapitolo;
import model.Tesi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Test di INTEGRAZIONE: serve un database Postgres reale e raggiungibile.
class RevisioneCapitoloDAOimplTest {

    private RevisioneCapitoloDAOimpl revisioneDAO;
    private TesiDAOimpl tesiDAO;

    @BeforeEach
    void setUp() {
        revisioneDAO = new RevisioneCapitoloDAOimpl();
        tesiDAO = new TesiDAOimpl();
    }

    @Test
        // TEST 9: salva() con pdf_data valorizzato mantiene i byte identici alla rilettura
    void salva_conPdfData_mantieneByteIdentici() {
        int idTesiTest = trovaUnaTesiQualsiasi();

        byte[] contenutoFinto = "contenuto finto di prova per il test".getBytes();
        RevisioneCapitolo r = new RevisioneCapitolo(idTesiTest, 999, "Capitolo di test JUnit", "test.pdf", contenutoFinto);

        boolean ok = revisioneDAO.salva(r);
        assertTrue(ok);
        assertTrue(r.getIdRevisione() > 0, "L'id generato deve essere valorizzato dopo il salvataggio");

        byte[] letto = revisioneDAO.getPdfData(r.getIdRevisione());
        assertArrayEquals(contenutoFinto, letto);
    }

    @Test
        // TEST 10: findByTesi() ordina i capitoli per num_capitolo crescente
    void findByTesi_ordinaPerNumCapitolo() {
        int idTesiTest = trovaUnaTesiQualsiasi();

        // Creo due capitoli fuori ordine per verificare che il DAO li riordini correttamente
        RevisioneCapitolo capitoloAlto = new RevisioneCapitolo(idTesiTest, 50, "Capitolo alto", "a.pdf", "a".getBytes());
        RevisioneCapitolo capitoloBasso = new RevisioneCapitolo(idTesiTest, 10, "Capitolo basso", "b.pdf", "b".getBytes());

        revisioneDAO.salva(capitoloAlto);
        revisioneDAO.salva(capitoloBasso);

        List<RevisioneCapitolo> lista = revisioneDAO.findByTesi(idTesiTest);

        for (int i = 1; i < lista.size(); i++) {
            assertTrue(lista.get(i - 1).getNumCapitolo() <= lista.get(i).getNumCapitolo(),
                    "La lista non è ordinata correttamente per num_capitolo");
        }
    }

    @Test
        // TEST 11: rinviaCorrezione() aggiorna lo stato senza creare righe duplicate
    void rinviaCorrezione_aggiornaSenzaDuplicare() {
        int idTesiTest = trovaUnaTesiQualsiasi();

        RevisioneCapitolo originale = new RevisioneCapitolo(idTesiTest, 777, "Capitolo da correggere", "originale.pdf", "originale".getBytes());
        revisioneDAO.salva(originale);

        int conteggioPrima = revisioneDAO.findByTesi(idTesiTest).size();

        byte[] nuovoContenuto = "versione corretta".getBytes();
        boolean ok = revisioneDAO.rinviaCorrezione(originale.getIdRevisione(), "corretto.pdf", nuovoContenuto);
        assertTrue(ok);

        int conteggioDopo = revisioneDAO.findByTesi(idTesiTest).size();
        assertEquals(conteggioPrima, conteggioDopo, "Il rinvio non deve creare una riga nuova, solo aggiornare quella esistente");

        byte[] letto = revisioneDAO.getPdfData(originale.getIdRevisione());
        assertArrayEquals(nuovoContenuto, letto);
    }

    // Metodo di supporto: prende una tesi qualsiasi esistente per collegarci i capitoli di test
    private int trovaUnaTesiQualsiasi() {
        List<Tesi> tutte = tesiDAO.trovaDisponibili();
        if (!tutte.isEmpty()) {
            return tutte.get(0).getIdTesi();
        }
        List<Tesi> diLuigi = tesiDAO.cercaPerProfessore(trovaIdProfessore());
        assertFalse(diLuigi.isEmpty(), "Serve almeno una tesi nel DB per questo test");
        return diLuigi.get(0).getIdTesi();
    }

    private int trovaIdProfessore() {
        dao.UtenteDAO utenteDAO = new dao.impl.UtenteDAOimpl();
        model.Utente u = utenteDAO.trovaPerEmail("luigi.verdi@unifi.it");
        return u != null ? u.getIdUtente() : -1;
    }
}