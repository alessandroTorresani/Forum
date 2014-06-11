CREATE TABLE UTENTE (
    ID_UTENTE INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    USERNAME VARCHAR(255),
    PASSWORD VARCHAR(255),
    CONSTRAINT primary_key PRIMARY KEY (ID_UTENTE) 
);

CREATE TABLE GRUPPO (
    ID_GRUPPO INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    ID_PROPRIETARIO INTEGER NOT NULL,
    NOME VARCHAR(255),
    DATA_CREAZIONE DATE,
    CONSTRAINT PRIMARY_KEY1 PRIMARY KEY (ID_GRUPPO) 
);

CREATE TABLE GRUPPO_UTENTE (
    ID_UTENTE  INT NOT NULL,
    ID_GRUPPO INT NOT NULL,
    AMMINISTRATORE BOOLEAN,
    CONSTRAINT PRIMARY_KEY2 PRIMARY KEY(ID_UTENTE,ID_GRUPPO)
);

CREATE TABLE INVITO (
    ID_INVITO INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    ID_UTENTE INT NOT NULL,
    ID_GRUPPO INT NOT NULL,
    ID_INVITANTE INT NOT NULL,
    CONSTRAINT PRIMARY_KEY3 PRIMARY KEY(ID_INVITO)
);

CREATE TABLE POST (
    ID_POST INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    ID_SCRIVENTE INT NOT NULL,
    ID_GRUPPO INT NOT NULL,
    CONTENUTO VARCHAR (4000),
    DATA_CREAZIONE TIMESTAMP,
    CONSTRAINT PRIMARY_KEY4 PRIMARY KEY(ID_POST)
);

CREATE TABLE POST_FILE (
    ID_FILE INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    ID_SCRIVENTE INT NOT NULL,
    ID_GRUPPO INT NOT NULL,
    ID_POST INT NOT NULL,
    FILE_PATH VARCHAR(500),
    CONSTRAINT PRIMARY_KEY5 PRIMARY KEY(ID_FILE,ID_POST)
);


INSERT INTO utente (username, password) VALUES ('utente1','utente1p');
INSERT INTO utente (username, password) VALUES ('utente2','utente2p');
INSERT INTO utente (username, password) VALUES ('utente3','utente3p');
INSERT INTO utente (username, password) VALUES ('utente4','utente4p');
INSERT INTO utente (username, password) VALUES ('utente5','utente5p');

INSERT INTO gruppo (id_proprietario, nome, data_creazione) VALUES(1,'gruppo1','2014-06-09');
INSERT INTO gruppo (id_proprietario, nome, data_creazione) VALUES(2,'gruppo2','2014-06-08');
INSERT INTO gruppo (id_proprietario, nome, data_creazione) VALUES(3,'gruppo3','2014-06-10');

INSERT INTO gruppo_utente (id_utente, id_gruppo, amministratore) VALUES (1,1,true);
INSERT INTO gruppo_utente (id_utente, id_gruppo, amministratore) VALUES (2,2,true);
INSERT INTO gruppo_utente (id_utente, id_gruppo, amministratore) VALUES (3,3,true);
INSERT INTO gruppo_utente (id_utente, id_gruppo, amministratore) VALUES (1,3,false);
INSERT INTO gruppo_utente (id_utente, id_gruppo, amministratore) VALUES (2,4,false);
INSERT INTO gruppo_utente (id_utente, id_gruppo, amministratore) VALUES (3,5,false);
INSERT INTO gruppo_utente (id_utente, id_gruppo, amministratore) VALUES (3,4,false);

INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (1,1,'post1','2014-06-09 09:09:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (3,1,'post2','2014-06-09 09:10:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (3,1,'post3','2014-06-09 09:11:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (1,1,'post4','2014-06-09 09:12:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (1,1,'post5','2014-06-09 09:13:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (3,1,'post6','2014-06-09 09:14:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (1,1,'post7','2014-06-09 09:15:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (1,1,'post8','2014-06-09 09:16:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (3,1,'post9','2014-06-09 09:17:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (1,1,'post10','2014-06-09 09:18:10');

INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (2,2,'post1','2014-06-08 08:08:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (4,2,'post2','2014-06-08 08:09:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (4,2,'post3','2014-06-08 08:10:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (2,2,'post4','2014-06-08 08:11:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (2,2,'post5','2014-06-08 08:12:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (4,2,'post6','2014-06-08 08:13:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (2,2,'post7','2014-06-08 08:14:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (2,2,'post8','2014-06-08 08:15:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (4,2,'post9','2014-06-08 08:16:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (2,2,'post10','2014-06-08 08:17:10');

INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (5,3,'post1','2014-06-10 10:10:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (3,3,'post2','2014-06-10 10:11:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (3,3,'post3','2014-06-10 10:12:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (4,3,'post4','2014-06-10 10:13:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (5,3,'post5','2014-06-10 10:14:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (3,3,'post6','2014-06-10 10:15:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (5,3,'post7','2014-06-10 10:16:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (4,3,'post8','2014-06-10 10:17:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (3,3,'post9','2014-06-10 10:18:10');
INSERT INTO post (id_scrivente, id_gruppo, contenuto, data_creazione) VALUES (4,3,'post10','2014-06-10 10:19:10');

INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (1,1,1,'1.txt');
INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (3,1,2,'2.txt');
INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (3,1,3,'3.txt');
INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (1,1,4,'4.txt');

INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (2,2,11,'1.txt');
INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (4,2,12,'2.txt');
INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (4,2,13,'3.txt');
INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (2,2,14,'4.txt');

INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (5,3,21,'1.txt');
INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (3,3,22,'2.txt');
INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (3,3,23,'3.txt');
INSERT INTO post_file (id_scrivente, id_gruppo, id_post,file_path) VALUES (4,3,24,'4.txt');



