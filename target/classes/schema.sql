DROP TABLE IF EXISTS richieste_tesi CASCADE;
DROP TABLE IF EXISTS corsi_di_laurea CASCADE;
DROP TABLE IF EXISTS revisioni_capitoli CASCADE;
DROP TABLE IF EXISTS candidature CASCADE;
DROP TABLE IF EXISTS tesi CASCADE;
DROP TABLE IF EXISTS professori CASCADE;
DROP TABLE IF EXISTS studenti CASCADE;
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
                      stato VARCHAR(50) NOT NULL DEFAULT 'DISPONIBILE',
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
