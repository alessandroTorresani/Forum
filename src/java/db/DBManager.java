/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author harwin
 */
public class DBManager implements Serializable {

    private transient Connection con;
    private String url;
    private String user;
    private String password;

    public DBManager(String url, String user, String password) throws SQLException {
        try {
            this.url = url;
            this.user = user;
            this.password = password;

            Class.forName("org.apache.derby.jdbc.ClientDriver", true, getClass().getClassLoader()); // carico driver

        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
        Connection con = DriverManager.getConnection(url, user, password); // connessione al database
        this.con = con;
    }

    public static void shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true"); // chiudo la connessione
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).info(ex.getMessage());
        }
    }

    public User authenticate(String username, String password) throws SQLException {

        PreparedStatement stm = con.prepareStatement("SELECT * FROM UTENTE WHERE username = ? AND password = ?");
        try {
            stm.setString(1, username);
            stm.setString(2, password);
            ResultSet rs = stm.executeQuery();
            try {
                if (rs.next()) {
                    User user = new User(); // se la query va a buon fine creo un'istanza della classe user
                    user.setUsername(username);
                    user.setUserId(rs.getInt("id_utente"));
                    return user;

                } else {
                    return null;
                }

            } finally {
                rs.close();
            }

        } finally {
            stm.close();
        }
    }

    public List<Group> getGroups(int user_id) throws SQLException { // restituisce tutti i gruppi a cui un utente è inscritto
        List<Group> groups = new ArrayList<Group>();
        PreparedStatement stm = con.prepareStatement("SELECT * FROM GRUPPO WHERE id_gruppo IN (SELECT ID_GRUPPO FROM GRUPPO_UTENTE WHERE id_utente = ?)");

        try {
            stm.setString(1, "" + user_id);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {
                    Group p = new Group();
                    p.setGroupName(rs.getString("nome"));
                    p.setCreationDate(rs.getString("data_creazione"));
                    p.setGroupId(rs.getInt("id_gruppo"));
                    int proprietario = rs.getInt("id_proprietario");
                    p.setOwner(proprietario);
                    p.setGroupId(rs.getInt("id_gruppo"));

                    PreparedStatement stm1 = con.prepareStatement("SELECT username FROM utente WHERE id_utente = ?  "); // query per ottenere il nome del proprietario
                    try {
                        stm1.setString(1, "" + proprietario);
                        ResultSet rs1 = stm1.executeQuery();
                        try {
                            while (rs1.next()) {
                                p.setOwnerName(rs1.getString("username"));
                            }
                        } finally {
                            rs1.close();
                        }
                    } finally {
                        stm1.close();
                    }

                    groups.add(p);
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }

        return groups;

    }

    public List<Bid> getBids(int user_id) throws SQLException { // restituisce tutti gli inviti mandati ad un utente
        List<Bid> bids = new ArrayList<Bid>();
        PreparedStatement stm = con.prepareStatement("SELECT * FROM invito WHERE id_utente = ? ");
        try {
            stm.setString(1, "" + user_id);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {
                    Bid b = new Bid();
                    b.setBidId(rs.getInt("id_invito"));
                    int gruppo = rs.getInt("id_gruppo");
                    b.setGroupId(gruppo);
                    int invitante = rs.getInt("id_invitante");
                    b.setSenderId(invitante);
                    b.setUserId(rs.getInt("id_utente"));
                    b.setBidId(rs.getInt("id_invito"));

                    PreparedStatement stm1 = con.prepareStatement("SELECT username FROM utente WHERE id_utente = ? "); // query ottenere il nome del sender
                    try {
                        stm1.setString(1, "" + invitante);
                        ResultSet rs1 = stm1.executeQuery();
                        try {
                            while (rs1.next()) {
                                b.setSenderName(rs1.getString("username"));
                            }
                        } finally {
                            rs1.close();
                        }
                    } finally {
                        stm1.close();
                    }

                    PreparedStatement stm2 = con.prepareStatement("SELECT nome FROM gruppo WHERE id_gruppo = ?"); // query per ottenere il nome del gruppo
                    try {
                        stm2.setString(1, "" + gruppo);
                        ResultSet rs2 = stm2.executeQuery();
                        try {
                            while (rs2.next()) {
                                b.setGroupName(rs2.getString("nome"));
                            }
                        } finally {
                            rs2.close();
                        }
                    } finally {
                        stm2.close();
                    }

                    bids.add(b);
                }

            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }

        return bids;
    }

    public List<User> getUsers(int user_id) throws SQLException { // ritorno tutti gli user tranne user con id = user_id
        List<User> users = new ArrayList();
        PreparedStatement stm = con.prepareStatement("SELECT * FROM utente EXCEPT (SELECT * FROM utente WHERE id_utente = ? )");
        try {
            stm.setString(1, "" + user_id);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {
                    User u = new User();
                    u.setUsername(rs.getString("username"));
                    u.setUserId(rs.getInt("id_utente"));
                    users.add(u);
                }

            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }

        return users;
    }

    public String getGroupName(int group_id) throws SQLException {
        String group_name = null;
        PreparedStatement stm = con.prepareStatement("SELECT nome FROM gruppo WHERE id_gruppo = ?");
        try {
            stm.setString(1, "" + group_id);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {
                    group_name = rs.getString("nome");
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }

        return group_name;
    }

    public Group createGroup(int owner_id, String name, String creation_date) throws SQLException { // Creo gruppo con: id del proprietario, nome del gruppo, date creazione, 
        Group p = null;
        int group_id = 0;
        boolean create = false;
        int key = 0;

        PreparedStatement stm = con.prepareStatement("SELECT nome FROM gruppo");  // ricerca se il nome esiste gia
        try {
            ResultSet rs = stm.executeQuery();
            try {
                List<String> nomi = new ArrayList();
                while (rs.next()) {
                    String nome = rs.getString("nome"); // aggiungo nell'array tutti i nomi dei gruppi nel database
                    nomi.add(nome.toUpperCase()); // converto tutto in maiuscolo affichè non ci siano errori con le lettere maiuscole
                }
                if (nomi.contains(name.toUpperCase())) {    // converto tutto in maiuscolo affichè non ci siano errori con le lettere maiuscole
                    System.out.println("Nome esistente"); // il nome esiste già, il gruppo non viene creato
                } else {
                    // altrimenti si può creare il gruppo
                    create = true;
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }

        if (create == true) {

            PreparedStatement stm1 = con.prepareStatement("INSERT INTO gruppo (id_proprietario,nome,data_creazione)  VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            try {
                stm1.setInt(1, owner_id);
                stm1.setString(2, name);
                stm1.setString(3, creation_date);
                stm1.executeUpdate();
                ResultSet rs = stm1.getGeneratedKeys();
                try {
                    while (rs.next()) {
                        key = rs.getInt(1); // ottengo l'id del gruppo appena creato
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stm1.close();
            }

            PreparedStatement stm2 = con.prepareStatement("SELECT * FROM GRUPPO where id_gruppo = ?"); // ottengo il gruppo appena creato
            try {
                stm2.setInt(1, key);
                ResultSet rs1 = stm2.executeQuery();
                try {
                    while (rs1.next()) {
                        p = new Group();
                        p.setGroupName(rs1.getString("nome"));
                        p.setCreationDate(rs1.getString("data_creazione"));
                        p.setOwner(owner_id);
                        group_id = rs1.getInt("id_gruppo");
                        p.setGroupId(group_id);
                    }
                } finally {
                    rs1.close();
                }
            } finally {
                stm2.close();
            }

            // adesso devo aggiungere la voce nella tabella gruppo utente
            PreparedStatement stm3 = con.prepareCall("INSERT INTO gruppo_utente (id_utente,id_gruppo,amministratore) VALUES (?,?,?)"); // aggiorno la tabella relazione gruppo-utente
            try {
                stm3.setInt(1, owner_id);
                stm3.setInt(2, group_id);
                stm3.setBoolean(3, true);
                stm3.executeUpdate();
            } finally {
                stm3.close();
            }
        }

        return p;
    }

    public void sendBids(List<String> user_ids, int group_id, int sender_id) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO invito (id_utente,id_gruppo,id_invitante) VALUES (?,?,?) "); // mando gli inviti
        try {
            for (int x = 0; x < user_ids.size(); x++) {
                stm.setInt(1, Integer.parseInt(user_ids.get(x)));
                stm.setInt(2, group_id);
                stm.setInt(3, sender_id);
                stm.executeUpdate();
            }
        } finally {
            stm.close();
        }
    }

    public void acceptBids1(String[] bids_ids, int user_id) throws SQLException { // MANCA IL CONTROLLO DEL FATTO CHE SE MODIFICO

        int[] groups_ids = new int[bids_ids.length]; // mi salvo gli id dei gruppi a cui devo aggiungere gli utenti che hanno accettato l'invito
        PreparedStatement stm = con.prepareStatement("SELECT id_gruppo FROM invito WHERE id_invito = ? AND id_utente = ?"); // 
        try {
            for (int x = 0; x < bids_ids.length; x++) {
                stm.setString(1, bids_ids[x]);
                stm.setInt(2, user_id);
                ResultSet rs = stm.executeQuery();

                try {
                    while (rs.next()) {
                        groups_ids[x] = rs.getInt("id_gruppo");
                    }
                } finally {
                    rs.close();
                }
            }
        } finally {
            stm.close();
        }

        if (groups_ids.length > 0) {
            PreparedStatement stm1 = con.prepareStatement("INSERT INTO gruppo_utente (id_utente,id_gruppo,amministratore) VALUES (?,?,?)"); // aggiorno le relazioni gruppo-utente in relazione agli inviti accettati
            try {
                for (int x = 0; x < groups_ids.length; x++) {
                    stm1.setInt(1, user_id);
                    stm1.setInt(2, groups_ids[x]);
                    stm1.setBoolean(3, false);
                    stm1.executeUpdate();
                }
            } finally {
                stm1.close();
            }

            PreparedStatement stm2 = con.prepareStatement("DELETE FROM invito WHERE id_invito = ?"); // elimino gli inviti accettati
            try {
                for (int x = 0; x < bids_ids.length; x++) {
                    stm2.setInt(1, Integer.parseInt(bids_ids[x]));
                    stm2.executeUpdate();
                }
            } finally {
                stm2.close();
            }
        }
    }
    
    public void AcceptBids(List<String> bids, int userId) throws SQLException {
        PreparedStatement stm = con.prepareStatement("INSERT INTO gruppo_utente (id_utente, id_gruppo, amministratore) VALUES (?,?,?)");
        int groupId = 0;
        try {
            stm.setInt(1, userId);
            stm.setBoolean(3, false);
            for (int x = 0; x < bids.size(); x++) {
                PreparedStatement stm1 = con.prepareCall("SELECT id_gruppo FROM invito WHERE id_invito = ?");
                try {
                    stm1.setInt(1, Integer.parseInt(bids.get(x)));
                    ResultSet rs = stm1.executeQuery();
                    try {
                        while (rs.next()) {
                            groupId = rs.getInt("id_gruppo");
                        }
                    } finally {
                        rs.close();
                    }
                } finally {
                    stm1.close();
                }
                stm.setInt(2, groupId);
                stm.executeUpdate();
            }
        } finally {
            stm.close();
        }
    }
    
    public void deleteBids(List<String> bids) throws SQLException {
        PreparedStatement stm = con.prepareStatement("DELETE FROM invito WHERE id_invito=?");
        try {
            for (int x = 0; x < bids.size(); x++) {
                stm.setInt(1, Integer.parseInt(bids.get(x)));
                stm.executeUpdate();
            }
        } finally {
            stm.close();
        }
    }
    
    public void refuseBids1(String[] bids_ids) throws SQLException{
         PreparedStatement stm = con.prepareStatement("DELETE FROM invito WHERE id_invito = ?"); // elimino gli inviti con ids bids_ids
            try {
                for (int x = 0; x < bids_ids.length; x++) {
                    stm.setInt(1, Integer.parseInt(bids_ids[x]));
                    stm.executeUpdate();
                }
            } finally {
                stm.close();
            }
    }
    
    public boolean checkBids1(List<Integer> bids_ids, int user_id) throws SQLException{
        boolean res = true;
        for(int x = 0; x < bids_ids.size(); x++){
            PreparedStatement stm = con.prepareStatement("SELECT id_utente FROM invito WHERE id_invito= ?");
            try {
                stm.setInt(1, bids_ids.get(x));
                ResultSet rs = stm.executeQuery();
                try {
                    while(rs.next()){
                        if(rs.getInt("id_utente") != user_id){
                            res = false;
                        }
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stm.close();
            }
        }
        return res;
    }
    
    public boolean checkBids(int userId, int bidId) throws SQLException {
        PreparedStatement stm = con.prepareStatement("SELECT id_utente FROM invito WHERE id_invito = ? and id_utente = ?");
        int numBids = 0;
        try {
            stm.setInt(1, bidId);
            stm.setInt(2, userId);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {
                    numBids++;
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }
        return numBids == 0;
    }

    public boolean changeGroupName(String name, int group_id) throws SQLException {

        boolean res = false; // variabile booleana che serve ad indicare se l'operazione è andata a buon fine (controllo se il nome esiste gia prima di inserirlo)
        boolean create = false; // variabile che serve per il controllo del nome già presente nel db

        PreparedStatement stm = con.prepareStatement("SELECT nome FROM gruppo");  // ricerca se il nome esiste gia
        try {
            ResultSet rs = stm.executeQuery();
            try {
                List<String> nomi = new ArrayList();
                while (rs.next()) {
                    String nome = rs.getString("nome"); // aggiungo nell'array tutti i nomi dei gruppi nel database
                    nomi.add(nome.toUpperCase()); // converto tutto in maiuscolo affichè non ci siano errori con le lettere maiuscole
                }
                if (nomi.contains(name.toUpperCase())) {    // converto tutto in maiuscolo affichè non ci siano errori con le lettere maiuscole
                    System.out.println("Nome esistente"); // il nome esiste già, il gruppo non viene creato
                } else {
                    // altrimenti si può creare il gruppo
                    create = true;
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }

        if (create == true) {
            PreparedStatement stm1 = con.prepareStatement("UPDATE gruppo SET nome = ? WHERE id_gruppo = ?");
            try {
                stm1.setString(1, name);
                stm1.setInt(2, group_id);
                stm1.executeUpdate();
                res = true;
            } finally {
                stm1.close();
            }
        }
        return res;
    }

    public List<Integer> getUsersIntoGroup(int group_id) throws SQLException { // ottengo gli id degli utenti che fanno parte del gruppo con id = group_id
        List<Integer> users = new ArrayList();
        PreparedStatement stm = con.prepareStatement("SELECT DISTINCT  id_utente FROM gruppo_utente WHERE id_gruppo = ?");
        try {
            stm.setInt(1, group_id);
            ResultSet rs = stm.executeQuery();
            try {
                int id;
                while (rs.next()) {
                    id = rs.getInt("id_utente");
                    users.add(id);
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }
        return users;
    }

    public List<Integer> getUsersNotIntoGroup(int group_id) throws SQLException { // ottengo gli utenti che non fanno parte del gruppo con id = group_id
        // int[] users = new int[100];
        List<Integer> users = new ArrayList();
        PreparedStatement stm = con.prepareStatement("SELECT DISTINCT  id_utente FROM utente WHERE id_utente NOT IN (SELECT DISTINCT id_utente FROM gruppo_utente WHERE id_gruppo = ?) ");
        //PreparedStatement stm = con.prepareStatement("SELECT DISTINCT  id_utente FROM gruppo_utente WHERE id_utente NOT IN (SELECT DISTINCT id_utente FROM gruppo_utente WHERE id_gruppo = ?) ");
        try {
            stm.setInt(1, group_id);
            ResultSet rs = stm.executeQuery();
            try {
                int id;
                while (rs.next()) {
                    id = rs.getInt("id_utente");
                    users.add(id);

                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }

        return users;
    }

    public List<Integer> getUserNotInvited(int group_id) throws SQLException { // ottengo gli utenti non invitati al gruppo con id = group_id
        List<Integer> users = new ArrayList();
        PreparedStatement stm = con.prepareStatement("SELECT DISTINCT id_utente FROM invito WHERE id_gruppo = ?");
        try {
            stm.setInt(1, group_id);
            ResultSet rs = stm.executeQuery();
            try {
                int id;
                while (rs.next()) {
                    id = rs.getInt("id_utente");
                    users.add(id);

                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }
        return users;
    }

    public List<User> getUsersById(List<String> ids) throws SQLException { // ottengo i nomi degli utenti con id = ids
        List<User> users = new ArrayList();
        PreparedStatement stm = con.prepareStatement("SELECT * FROM utente WHERE id_utente = ?");
        try {
            for (int x = 0; x < ids.size(); x++) {
                stm.setInt(1, Integer.parseInt(ids.get(x)));
                ResultSet rs = stm.executeQuery();
                try {
                    while (rs.next()) {
                        User u = new User();
                        u.setUsername(rs.getString("username"));
                        u.setUserId(rs.getInt("id_utente"));
                        users.add(u);
                    }
                } finally {
                    rs.close();
                }
            }
        } finally {
            stm.close();
        }
        return users;
    }

    public int getGroupOwner(int group_id) throws SQLException { // ottengo l'ide del proprietario del gruppo con id = group_id
        int owner = 0;
        PreparedStatement stm = con.prepareStatement("SELECT id_proprietario FROM gruppo WHERE id_gruppo = ? ");
        try {
            stm.setInt(1, group_id);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {
                    owner = rs.getInt("id_proprietario");
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }

        return owner;
    }

    public int createPost(int group_id, int user_id, String creation_date, String content) throws SQLException { // funzione che crea un post
        int res = -1;
        PreparedStatement stm = con.prepareStatement("INSERT INTO post (id_gruppo, id_scrivente, contenuto, data_creazione) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        try {
            stm.setInt(1, group_id);
            stm.setInt(2, user_id);
            stm.setString(3, content);
            stm.setString(4, creation_date);
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            try {
                while (rs.next()) {
                    res = rs.getInt(1); // ottengo l'id del gruppo appena creato
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }

        return res;
    }

    public List<Post> getPosts(int group_id) throws SQLException {   // funzione che restituisce tutti i post del gruppo con id = group_id
        List<Post> posts = new ArrayList();
        PreparedStatement stm = con.prepareStatement("SELECT * FROM post WHERE id_gruppo = ? ORDER BY data_creazione DESC"); // query per ottenere tutti i post di un gruppo
        try {
            stm.setString(1, "" + group_id);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {
                    Post p = new Post();
                    p.setGroupId(rs.getInt("id_gruppo"));
                    p.SetDate(rs.getString("data_creazione"));
                    int writer_id = rs.getInt("id_scrivente");
                    p.setWriterId(writer_id);
                    p.setContent(rs.getString("contenuto"));
                    p.setPostId(rs.getInt("id_post"));

                    PreparedStatement stm1 = con.prepareStatement("SELECT username FROM utente WHERE id_utente = ?  "); // query per ottenere il nome del proprietario
                    try {
                        stm1.setString(1, "" + writer_id);
                        ResultSet rs1 = stm1.executeQuery();
                        try {
                            while (rs1.next()) {
                                p.setWriterName(rs1.getString("username"));
                            }
                        } finally {
                            rs1.close();
                        }
                    } finally {
                        stm1.close();
                    }

                    posts.add(p); // aggiungo il post al gruppo
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }
        return posts;
    }

    public boolean addFileToPost(String file_name, int post_id, int group_id, int user_id) throws SQLException { // funzione che serve per collegare ad ogni post un file caricato (se presente)
        boolean res = false;
        PreparedStatement stm = con.prepareStatement("INSERT INTO post_file (file_path, id_scrivente, id_gruppo, id_post) VALUES (?,?,?,?) ");
        try {
        stm.setString(1, file_name);
        stm.setInt(2, user_id);
        stm.setInt(3, group_id);
        stm.setInt(4, post_id);
        stm.executeUpdate();
        } finally {
            stm.close();
            res = true;
        }
        return res;
    }

    public PostFile getFileByPostId(int postId) throws SQLException { // funzione per ottenere se esiste un link al file di un post
        PostFile pf = null;
        PreparedStatement stm = con.prepareStatement("SELECT * FROM post_file WHERE id_post = ?");
        try {
            stm.setInt(1, postId);
            ResultSet rs = stm.executeQuery();
            try {
                pf = new PostFile();
                while (rs.next()) {
                    pf.setFileName(rs.getString("file_path"));
                    pf.setGroupId(rs.getInt("id_gruppo"));
                    pf.setPostId(rs.getInt("id_post"));
                    pf.setWriterId(rs.getInt("id_scrivente"));
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }
        return pf;
    }

    public List<String> getUsersNameByIds(List<Integer> ids) throws SQLException { // ottengo i nomi degli utenti tramite il loro id
        List<String> usersNames = new ArrayList();
        PreparedStatement stm = con.prepareStatement("SELECT username FROM utente WHERE id_utente = ?");
        try {
            for (int x = 0; x < ids.size(); x++) {
                stm.setInt(1, ids.get(x));
                ResultSet rs = stm.executeQuery();
                try {
                    while (rs.next()) {
                        usersNames.add(rs.getString("username"));
                    }
                } finally {
                    rs.close();
                }
            }
        } finally {
            stm.close();
        }
        return usersNames;
    }

    public String getLastPost(int group_id) throws SQLException { // ottengo la data dell'ultimo post
        String date = null;
        PreparedStatement stm = con.prepareStatement("SELECT data_creazione FROM post WHERE id_gruppo = ? AND id_post = (SELECT MAX (id_post) FROM post WHERE id_gruppo = ?)");
        try {
            stm.setInt(1, group_id);
            stm.setInt(2, group_id);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {
                    date = rs.getString("data_creazione");
                }
            } finally {
                rs.close();
            }
        } finally {
            stm.close();
        }
        return date;
    }

    public int getNumberOfPosts(int group_id) throws SQLException { // funzione che resituisce il numero di post all'interno di un gruppo
        int numberOfPosts = 0;
        PreparedStatement stm = con.prepareCall("SELECT COUNT(id_post) FROM post WHERE id_gruppo = ?");
        try {
            stm.setInt(1, group_id);
            ResultSet rs = stm.executeQuery();
            try {
                while (rs.next()) {
                    numberOfPosts = rs.getInt(1);
                }
            } finally {
                rs.close();
            }
        } finally{
            stm.close();
        } 
            return numberOfPosts;
    }
}
