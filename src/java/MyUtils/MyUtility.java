/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyUtils;

/**
 *
 * @author Alessandro
 */
public class MyUtility {

    private static final String TITLE_REGEX = "^[a-zA-Z0-9]+(\\s[a-zA-Z0-9]+)*$"; // espressione regolare per i titoli dei gruppi
    private static final String LINK_REGEX = "((https?|ftp)://)?([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])(\\.([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9]))+(/[A-Za-z0-9_\\.\\-~%]+)*/?"; // controllo del link

    //private static final String LINK_REGEX_02 = "((https?|ftp|file)://)?[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";  
    //private static final String LINK_REGEX = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"; 
    public static boolean checkHtml(String s) { // controllo che la stringa inserita sia nella forma espressa nella regular expression
        if (s != null) {
            return s.matches(TITLE_REGEX);
        } else {
            return false;
        }
    }

    public static String cleanHTMLTags(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static String checkMultiLink(String s) {
        boolean control = true;
        int first = 0;
        int second = 0;
        String link = "";
        String substitute;

        if (s.contains("$$")) {
            while (control) {
                first = s.indexOf("$$", first + link.length());

                if (first >= 0) {
                    second = s.indexOf("$$", first + 2); // 1 o 2?

                    if ((second > 0) && (first < second)) {
                        link = s.substring(first + 2, second);

                        if (link.matches(LINK_REGEX)) {
                            if ((link.startsWith("http")) || (link.startsWith("ftp")) || (link.startsWith("https"))) { // se il link comincia già con http/ftp/https... non aggiungo nulla
                                substitute = "<a href = '" + link + "' target='_blank'>"+ link + "</a>";
                            } else {
                                substitute = "<a href='http://" + link + "' target='_blank'>"+ link + "</a>"; //altrimenti aggiungo il link
                            }
                            s = s.substring(0, first) + substitute + s.substring(second + 2);
                        }
                    } else {
                        control = false;
                    }
                } else {
                    control = false;
                }
            }
        }
        return s;
    }
}
