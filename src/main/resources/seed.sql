-- ==========================================
-- PULIZIA TABELLE (per evitare duplicati)
-- ==========================================
TRUNCATE utenti, corsi_di_laurea, tesi, revisioni_capitoli, notifiche CASCADE;

-- ==========================================
-- 1. UTENTI
-- ==========================================
-- Studente (id_utente 1)
INSERT INTO utenti (email, password, ruolo, nome, cognome)
VALUES ('mario.rossi@studenti.it', 'password123', 'STUDENTE', 'Mario', 'Rossi');

INSERT INTO studenti (id_utente, matricola, cfu_totali, corso_laurea)
VALUES (currval('utenti_id_utente_seq'), '123456', 120, 'Informatica');

-- Professore (id_utente 2)
INSERT INTO utenti (email, password, ruolo, nome, cognome)
VALUES ('luigi.verdi@unifi.it', 'prof123', 'PROFESSORE', 'Luigi', 'Verdi');

INSERT INTO professori (id_utente, matricola_docente, corso_laurea, num_tesisti_attivi)
VALUES (currval('utenti_id_utente_seq'), 'DOC999', 'Informatica', 2);

-- Segreteria (id_utente 3)
INSERT INTO utenti (email, password, ruolo, nome, cognome)
VALUES ('segreteria@unifi.it', 'admin123', 'SEGRETERIA', 'Anna', 'Bianchi');

-- Studente 2 (id_utente 4)
INSERT INTO utenti (email, password, ruolo, nome, cognome)
VALUES ('giulia.bianchi@studenti.it', 'password456', 'STUDENTE', 'Giulia', 'Bianchi');

INSERT INTO studenti (id_utente, matricola, cfu_totali, corso_laurea)
VALUES (currval('utenti_id_utente_seq'), '654321', 90, 'Ingegneria Informatica');

-- Professore 2 (id_utente 5)
INSERT INTO utenti (email, password, ruolo, nome, cognome)
VALUES ('paolo.neri@unifi.it', 'prof456', 'PROFESSORE', 'Paolo', 'Neri');

INSERT INTO professori (id_utente, matricola_docente, corso_laurea, num_tesisti_attivi)
VALUES (currval('utenti_id_utente_seq'), 'DOC888', 'Ingegneria Informatica', 0);

-- ==========================================
-- 2. CORSI DI LAUREA
-- ==========================================
INSERT INTO corsi_di_laurea (nome, dipartimento) VALUES
                                                     ('Informatica', 'DINFO'),
                                                     ('Ingegneria Informatica', 'DINFO'),
                                                     ('Economia e Commercio', 'DISEI')
    ON CONFLICT (nome) DO NOTHING;

-- ==========================================
-- 3. TESI DI PROVA
-- ==========================================
INSERT INTO tesi (titolo, descrizione, corso_laurea, stato, id_professore)
VALUES (
           'Sviluppo Applicazioni Web Java',
           'Progetto di tesi sull architettura layered con Java e PostgreSQL',
           'Informatica',
           'PUBBLICATA',
           (SELECT id_utente FROM utenti WHERE email = 'luigi.verdi@unifi.it')
       );

-- Tesi 2: pubblicata dal nuovo professore, per testare candidatura/accettazione da zero
INSERT INTO tesi (titolo, descrizione, corso_laurea, stato, id_professore)
VALUES (
           'Sistemi Distribuiti e Microservizi',
           'Tesi sperimentale su architetture a microservizi con Docker e Kubernetes',
           'Ingegneria Informatica',
           'PUBBLICATA',
           (SELECT id_utente FROM utenti WHERE email = 'paolo.neri@unifi.it')
       );

-- ==========================================
-- 4. REVISIONI CAPITOLI (Dati di prova)
-- ==========================================
INSERT INTO revisioni_capitoli (id_tesi, num_capitolo, titolo_capitolo, percorso_pdf, note_professore, stato_revisione)
VALUES (
           (SELECT id_tesi FROM tesi WHERE titolo = 'Sviluppo Applicazioni Web Java'),
           1,
           'Capitolo 1: Introduzione e Architettura',
           '/pdf/capitolo1_mario_rossi.pdf',
           'Buona prima stesura, aggiungere dettagli sui DAO.',
           'IN_REVISIONE'
       );

-- ==========================================
-- 5. NOTIFICHE (Dati di prova)
-- ==========================================
INSERT INTO notifiche (id_utente, messaggio, letta)
VALUES (
           (SELECT id_utente FROM utenti WHERE email = 'mario.rossi@studenti.it'),
           'Il professore ha inserito una nota sul Capitolo 1.',
           FALSE
       );