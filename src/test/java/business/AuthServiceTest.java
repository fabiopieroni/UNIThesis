package business;

import dao.UtenteDAO;
import model.Ruolo;
import model.Utente;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Test FUNZIONALI (Black Box) con Mockito: il DAO è "finto", nessun database reale necessario.
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UtenteDAO utenteDAOMock;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(utenteDAOMock);
    }

    @AfterEach
    void tearDown() {
        // Pulizia della sessione dopo ogni test, per non far "sopravvivere" lo stato tra un test e l'altro
        Sessione.getInstance().chiudiSessione();
    }

    @Test
        // Login con credenziali corrette: successo e sessione avviata
    void login_credenzialiCorrette_riesceEAvviaSessione() {
        Utente utenteFinto = new Utente();
        utenteFinto.setIdUtente(1);
        utenteFinto.setEmail("test@test.it");
        utenteFinto.setPassword("password123");
        utenteFinto.setRuolo(Ruolo.STUDENTE);

        when(utenteDAOMock.login("test@test.it", "password123")).thenReturn(utenteFinto);

        boolean ok = authService.login("test@test.it", "password123");

        assertTrue(ok);
        assertTrue(Sessione.getInstance().isLogged());
        assertEquals("test@test.it", Sessione.getInstance().getUtenteCorrente().getEmail());
    }

    @Test
        // Login con credenziali sbagliate: fallisce, nessuna sessione avviata
    void login_credenzialiErrate_fallisceENonAvviaSessione() {
        when(utenteDAOMock.login("test@test.it", "passwordSbagliata")).thenReturn(null);

        boolean ok = authService.login("test@test.it", "passwordSbagliata");

        assertFalse(ok);
        assertFalse(Sessione.getInstance().isLogged());
    }

    @Test
        // Login con email vuota: fallisce senza nemmeno interrogare il DAO
    void login_emailVuota_fallisceSenzaChiamareDAO() {
        boolean ok = authService.login("", "qualsiasi");

        assertFalse(ok);
        verify(utenteDAOMock, never()).login(anyString(), anyString());
    }

    @Test
        // Logout chiude correttamente la sessione
    void logout_chiudeLaSessione() {
        Utente utenteFinto = new Utente();
        utenteFinto.setEmail("test@test.it");
        when(utenteDAOMock.login(anyString(), anyString())).thenReturn(utenteFinto);
        authService.login("test@test.it", "qualsiasi");

        assertTrue(Sessione.getInstance().isLogged());

        authService.logout();

        assertFalse(Sessione.getInstance().isLogged());
    }
}