// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: le impostazioni. Scadenza: domenica 1 novembre 2026.
package pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Le impostazioni di una partita di Pong: quanti punti servono per vincere, con quale scarto
 * minimo, a che velocità va la palla. È un record (lezione 4): una volta create, non cambiano.
 *
 * IL PEZZO MANCANTE: il costruttore che rifiuta i valori sbagliati e il metodo leggi.
 * Ogni metodo ha il suo contratto, nel commento sopra. I test ufficiali (cartella test)
 * controllano i contratti.
 */
public record Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {

    /** Le impostazioni standard: 11 punti per vincere, scarto minimo 2, velocità della palla 5. */
    public static final Impostazioni STANDARD = new Impostazioni(11, 2, 5);

    private static boolean nellIntervallo(int valore, int min, int max) {
        return valore >= min && valore <= max;
    }

    /**
     * Il costruttore (lezione 7: un record che nasce giusto).
     * Promette: se i tre valori sono nei loro intervalli, le impostazioni con quei valori.
     * Lancia IllegalArgumentException se puntiPerVincere non è fra 1 e 99, se scartoMinimo
     * non è fra 1 e 10, o se velocitaPalla non è fra 1 e 20 (estremi compresi). Il messaggio
     * dice quale valore è sbagliato e perché.
     */
    public Impostazioni {
        if (!nellIntervallo(puntiPerVincere, 1, 99)) {
            throw new IllegalArgumentException (
                "Valore non valido, i punti per vincere sono " + puntiPerVincere + ", ma dovrebbero essere compresi tra 1 e 99"
            );
        }
        if (!nellIntervallo(scartoMinimo, 1, 10)) {
            throw new IllegalArgumentException (
                "Valore non valido, lo scarto minimo è " + scartoMinimo + ", ma dovrebbe essere compreso tra 1 e 10"
            );
        }
        if (!nellIntervallo(velocitaPalla, 1, 20)) {
            throw new IllegalArgumentException (
                "Valore non valido, la velocità della palla è " + velocitaPalla + ", ma dovrebbe essere compresa tra 1 e 20"
            );
        }
    }

    /**
     * Legge le impostazioni dalle righe di un file, una impostazione per riga.
     * Pretende: righe non è null e non contiene null.
     * Promette:
     *  - le righe vuote (o di soli spazi) e quelle che, tolti gli spazi iniziali, cominciano
     *    con # sono ignorate;
     *  - ogni altra riga è nella forma  chiave = valore , con spazi qualsiasi attorno alla chiave,
     *    al segno = e al valore; le chiavi sono puntiPerVincere, scartoMinimo, velocitaPalla;
     *  - un'impostazione che nelle righe non c'è prende il valore standard;
     *  - il risultato sono le impostazioni lette.
     * Lancia IllegalArgumentException, con un messaggio che comincia con «riga N: » (N è il numero
     * della riga, contando da 1 tutte le righe, anche quelle vuote e i commenti), se in una riga:
     *  1. manca il segno = ;
     *  2. la chiave non è una delle tre;
     *  3. il valore non è un numero intero;
     *  4. la stessa chiave c'era già in una riga precedente;
     *  5. il valore è fuori dal suo intervallo (gli intervalli del costruttore).
     * Se ci sono errori in più righe, il messaggio è quello della riga più in alto.
     */
    public static Impostazioni leggi(List<String> righe) {
        int puntiPerVincere = STANDARD.puntiPerVincere();
        int scartoMinimo = STANDARD.scartoMinimo();
        int velocitaPalla = STANDARD.velocitaPalla();
        boolean vistoPunti = false;
        boolean vistoScarto = false;
        boolean vistoVelocita = false;

        for (int i=0; i<righe.size(); i++) {
            String riga = righe.get(i).trim();
            int numeroRiga = i+1;

            if (riga.isEmpty() || riga.startsWith("#")) {
                continue;
            }

            int posizioneUguale = riga.indexOf("=");
            if (posizioneUguale < 0) {
                throw new IllegalArgumentException(
                    "riga " + numeroRiga + ": non c'è l'uguale"
                );
            }

            String chiave = riga.substring(0, posizioneUguale).trim();
            String valore = riga.substring(posizioneUguale + 1).trim();

            switch(chiave) {
                case "puntiPerVincere" -> {
                    puntiPerVincere = leggiIntero(chiave, valore, numeroRiga);
                    if (vistoPunti) {
                        throw new IllegalArgumentException(
                            "riga " + numeroRiga + ": chiave puntiPerVincere è scritta più di una volta"
                        );
                    }
                    if (!nellIntervallo(puntiPerVincere, 1, 99)) {
                        throw new IllegalArgumentException(
                            "riga " + numeroRiga + ": puntiPerVincere deve essere compreso tra 1 e 99"
                        );
                    }
                    vistoPunti = true;
                }
                case "scartoMinimo" -> {
                    scartoMinimo = leggiIntero(chiave, valore, numeroRiga);
                    if (vistoScarto) {
                        throw new IllegalArgumentException(
                            "riga " + numeroRiga + ": chiave scartoMinimo è scritta più di una volta"
                        );
                    }
                    if (!nellIntervallo(scartoMinimo, 1, 10)) {
                        throw new IllegalArgumentException(
                            "riga " + numeroRiga + ": scartoMinimo deve essere compreso tra 1 e 10"
                        );
                    }
                    vistoScarto = true;
                }
                case "velocitaPalla" -> {
                    velocitaPalla = leggiIntero(chiave, valore, numeroRiga);
                    if (vistoVelocita) {
                        throw new IllegalArgumentException(
                            "riga " + numeroRiga + ": chiave velocitaPalla è scritta più di una volta"
                        );
                    }
                    if (!nellIntervallo(velocitaPalla, 1, 20)) {
                        throw new IllegalArgumentException(
                            "riga " + numeroRiga + ": velocitaPalla deve essere compreso tra 1 e 20"
                        );
                    }
                    vistoVelocita = true;
                }
                default -> {
                    throw new IllegalArgumentException(
                        "riga " + numeroRiga + ": la chiave non è una tra puntiPerVincere, scartoMinimo e velocitaPalla"
                    );
                }
            }
        }

        return new Impostazioni(puntiPerVincere, scartoMinimo, velocitaPalla);
    }

    private static int leggiIntero(String chiave, String valore, int riga) {
        try {
            return Integer.parseInt(valore);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "riga " + riga + ": Valore non valido per " + chiave + ": \"" + valore + "\" (serve un numero intero)");
        }
    }

    /**
     * Legge le impostazioni da un file di testo.
     * Promette: come leggi, con le righe del file.
     * Lancia IOException se il file non si può leggere (per esempio non esiste): non la cattura,
     * la lascia a chi chiama. Lancia IllegalArgumentException come leggi.
     */
    public static Impostazioni daFile(Path file) throws IOException {
        return leggi(Files.readAllLines(file));
    }
}
