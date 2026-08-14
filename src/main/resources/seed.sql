DROP TABLE IF EXISTS richieste_tesi CASCADE;
DROP TABLE IF EXISTS corsi_di_laurea CASCADE;
DROP TABLE IF EXISTS revisioni_capitoli CASCADE;
DROP TABLE IF EXISTS candidature CASCADE;
DROP TABLE IF EXISTS tesi CASCADE;
DROP TABLE IF EXISTS professori CASCADE;
DROP TABLE IF EXISTS studenti CASCADE;
DROP TABLE IF EXISTS notifiche CASCADE;
DROP TABLE IF EXISTS utenti CASCADE;

CREATE TABLE utenti (
                        id_utente SERIAL PRIMARY KEY,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        ruolo VARCHAR(50) NOT NULL,
                        nome VARCHAR(100) NOT NULL,
                        cognome VARCHAR(100) NOT NULL
);

CREATE TABLE studenti (
                          id_utente INT PRIMARY KEY REFERENCES utenti(id_utente) ON DELETE CASCADE,
                          matricola VARCHAR(50) UNIQUE NOT NULL,
                          cfu_totali INT DEFAULT 0,
                          corso_laurea VARCHAR(150) NOT NULL
);

CREATE TABLE professori (
                            id_utente INT PRIMARY KEY REFERENCES utenti(id_utente) ON DELETE CASCADE,
                            matricola_docente VARCHAR(50) UNIQUE NOT NULL,
                            corso_laurea VARCHAR(150) NOT NULL,
                            num_tesisti_attivi INT DEFAULT 0
);

CREATE TABLE tesi (
                      id_tesi SERIAL PRIMARY KEY,
                      titolo VARCHAR(255) NOT NULL,
                      descrizione TEXT,
                      corso_laurea VARCHAR(150) NOT NULL,
                      stato VARCHAR(50) NOT NULL DEFAULT 'PUBBLICATA',
                      id_professore INT NOT NULL REFERENCES professori(id_utente) ON DELETE CASCADE
);

CREATE TABLE candidature (
                             id_candidatura SERIAL PRIMARY KEY,
                             id_studente INT NOT NULL REFERENCES studenti(id_utente) ON DELETE CASCADE,
                             id_tesi INT NOT NULL REFERENCES tesi(id_tesi) ON DELETE CASCADE,
                             data_richiesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             messaggio TEXT,
                             stato_candidatura VARCHAR(50) NOT NULL DEFAULT 'IN_ATTESA'
);

CREATE TABLE revisioni_capitoli (
                                    id_revisione SERIAL PRIMARY KEY,
                                    id_tesi INT NOT NULL REFERENCES tesi(id_tesi) ON DELETE CASCADE,
                                    num_capitolo INT NOT NULL,
                                    titolo_capitolo VARCHAR(255) NOT NULL,
                                    percorso_pdf VARCHAR(500),
                                    note_professore TEXT,
                                    stato_revisione VARCHAR(50) NOT NULL DEFAULT 'IN_REVISIONE',
                                    data_invio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE corsi_di_laurea (
                                 id SERIAL PRIMARY KEY,
                                 nome VARCHAR(100) NOT NULL UNIQUE,
                                 dipartimento VARCHAR(100) NOT NULL
);

CREATE TABLE richieste_tesi (
                                id SERIAL PRIMARY KEY,
                                id_studente INT NOT NULL,
                                id_tesi INT NOT NULL,
                                stato VARCHAR(20) DEFAULT 'IN_ATTESA',
                                data_richiesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                motivazione TEXT,
                                CONSTRAINT fk_studente FOREIGN KEY (id_studente) REFERENCES studenti(id_utente) ON DELETE CASCADE,
                                CONSTRAINT fk_tesi FOREIGN KEY (id_tesi) REFERENCES tesi(id_tesi) ON DELETE CASCADE
);

CREATE TABLE notifiche (
                           id_notifica SERIAL PRIMARY KEY,
                           id_utente INT NOT NULL REFERENCES utenti(id_utente) ON DELETE CASCADE,
                           messaggio TEXT NOT NULL,
                           data_invio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           letta BOOLEAN DEFAULT FALSE
);

CREATE UNIQUE INDEX idx_richiesta_attiva_per_studente
    ON richieste_tesi (id_studente)
    WHERE stato IN ('IN_ATTESA', 'ACCETTATA');


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
                                                     ('Ingegneria Gestionale', 'DINFO'),
                                                     ('Ingegneria Elettronica', 'DINFO'),
                                                     ('Economia', 'DISEI'),
                                                     ('Medicina', 'DMED')
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
-- Nessun dato di test qui: i capitoli si caricano da "Gestione Revisioni"
-- così il campo pdf_data viene popolato con un PDF reale.

-- ==========================================
-- 5. NOTIFICHE (Dati di prova)
-- ==========================================
INSERT INTO notifiche (id_utente, messaggio, letta)
VALUES (
           (SELECT id_utente FROM utenti WHERE email = 'mario.rossi@studenti.it'),
           'Il professore ha inserito una nota sul Capitolo 1.',
           FALSE
       );
