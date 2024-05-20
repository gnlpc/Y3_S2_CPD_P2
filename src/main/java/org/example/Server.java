package org.example;
import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.*;

import static org.example.Utils.getKeyByValue;
import static org.example.Utils.read;
import static org.example.Utils.readInt;
import static org.example.Utils.readString;


public class Server {

    // Clients connected and their ports
    private static final Map<String, SSLSocket> clients = new HashMap<>();

    // Queues
    private static final Queue<String> simpleQ = new LinkedList<>();
    private static final Queue<String> rank1Q = new LinkedList<>();   // 0-10
    private static final Queue<String> rank2Q = new LinkedList<>();   // 11-25
    private static final Queue<String> rank3Q = new LinkedList<>();   // 26+

    // Locks
    private static final ReentrantLock lock = new ReentrantLock();
    private static final ReentrantLock condLock = new ReentrantLock();
    private static final Condition cond = condLock.newCondition();

    private static final Database db = new Database();


    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java ServerTest <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try {
            char[] keystorePassword = "password".toCharArray(); // replace with your keystore password
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream keyStoreStream = new FileInputStream("serverkeystore.jks")) {
                keyStore.load(keyStoreStream, keystorePassword);
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            try (SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port)) {
                lock.lock();
                db.logoutAll();
                db.finishActiveGames();
                lock.unlock();
                System.out.println("Server is listening on port " + port);

                Thread.startVirtualThread(() -> {
                    try {
                        startGames();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                while (true) {
                    SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                    Thread.startVirtualThread(() -> new ClientHandler(clientSocket).run());
                }

            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startGames() throws IOException, InterruptedException {
        while(true){
            condLock.lock();
            cond.await();
            if(simpleQ.size()>=2){
                String player1 = simpleQ.poll();
                String player2 = simpleQ.poll();
                System.out.println("Simple game started between " + player1 + " and " + player2);
                SSLSocket s1 = clients.get(player1);
                SSLSocket s2 = clients.get(player2);
                Thread.startVirtualThread(() -> {
                    try {
                        lock.lock();
                        var p1 = db.getUserID(player1);
                        var p2 = db.getUserID(player2);
                        int game_id = db.insertGame(p1, p2, "simple");
                        lock.unlock();
                        int winner = start(s1,s2, game_id);
                        lock.lock();
                        if(winner==0){
                            db.finishGame(game_id, p1);
                        }
                        else{
                            db.finishGame(game_id, p2);
                        }
                        lock.unlock();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            if(rank1Q.size()>=2){
                String player1 = rank1Q.poll();
                String player2 = rank1Q.poll();
                System.out.println("Ranked game started between " + player1 + " and " + player2);
                SSLSocket s1 = clients.get(player1);
                SSLSocket s2 = clients.get(player2);
                Thread.startVirtualThread(() -> {
                    try {
                        lock.lock();
                        var p1 = db.getUserID(player1);
                        var p2 = db.getUserID(player2);
                        int game_id = db.insertGame(p1, p2, "ranked");
                        lock.unlock();
                        int winner = start(s1,s2, game_id);
                        lock.lock();
                        if(winner==0){
                            db.finishGame(game_id, p1);
                        }
                        else{
                            db.finishGame(game_id, p2);
                        }
                        lock.unlock();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            if(rank2Q.size()>=2){
                String player1 = rank2Q.poll();
                String player2 = rank2Q.poll();
                System.out.println("Ranked game started between " + player1 + " and " + player2);
                SSLSocket s1 = clients.get(player1);
                SSLSocket s2 = clients.get(player2);
                OutputStream output1 = s1.getOutputStream();
                Thread.startVirtualThread(() -> {
                    try {
                        lock.lock();
                        var p1 = db.getUserID(player1);
                        var p2 = db.getUserID(player2);
                        int game_id = db.insertGame(p1, p2, "ranked");
                        lock.unlock();
                        int winner = start(s1,s2, game_id);
                        lock.lock();
                        if(winner==0){
                            db.finishGame(game_id, p1);
                        }
                        else{
                            db.finishGame(game_id, p2);
                        }
                        lock.unlock();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            if(rank3Q.size()>=2){
                String player1 = rank3Q.poll();
                String player2 = rank3Q.poll();
                System.out.println("Ranked game started between " + player1 + " and " + player2);
                SSLSocket s1 = clients.get(player1);
                SSLSocket s2 = clients.get(player2);
                Thread.startVirtualThread(() -> {
                    try{
                        lock.lock();
                        var p1 = db.getUserID(player1);
                        var p2 = db.getUserID(player2);
                        int game_id = db.insertGame(p1, p2, "ranked");
                        lock.unlock();
                        int winner = start(s1,s2, game_id);
                        lock.lock();
                        if(winner==0){
                            db.finishGame(game_id, p1);
                        }
                        else{
                            db.finishGame(game_id, p2);
                        }
                        lock.unlock();
                    }catch(Exception e){
                        throw new RuntimeException(e);
                    }

                });
            }
            condLock.unlock();
        }
    }

    public static int start(SSLSocket s1, SSLSocket s2, int game_id){
        try {

            var r = new Object() {
                boolean disconnected1 = false;
                boolean disconnected2 = false;
            };

            Thread th1 = Thread.startVirtualThread(() -> {
                try {
                    OutputStream output1 = s1.getOutputStream();
                    PrintWriter writer1 = new PrintWriter(output1, true);

                    writer1.println("START GAME");

                    var check = read(s1, "GAME STARTED");
                    if(check==0){
                        r.disconnected1 = true;
                    }
                    else if(check==-1){
                        r.disconnected1 = true;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread th2 = Thread.startVirtualThread(() -> {
                try {
                    OutputStream output2 = s2.getOutputStream();
                    PrintWriter writer2 = new PrintWriter(output2, true);

                    writer2.println("START GAME");
                    var check = read(s2, "GAME STARTED");
                    if(check==0){
                        r.disconnected2 = true;
                    }
                    else if(check==-1){
                        r.disconnected2 = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            th1.join();
            th2.join();

            if (r.disconnected1) {
                lock.lock();
                db.logoutUser(getKeyByValue(clients, s1));
                removeFromQueues(getKeyByValue(clients, s1));
                db.finishGame(game_id, db.getUserID(getKeyByValue(clients, s2)));
                lock.unlock();
                Thread.startVirtualThread(() -> {
                    try {
                        OutputStream output2 = s2.getOutputStream();
                        PrintWriter writer2 = new PrintWriter(output2, true);


                        writer2.println("DISCONNECTED");
                        if(read(s2, "ACK")==0){
                            lock.lock();
                            db.logoutUser(getKeyByValue(clients, s2));
                            removeFromQueues(getKeyByValue(clients, s2));
                            lock.unlock();
                            return;
                        }
                        writer2.println("GOING TO MENU");
                        var username = getKeyByValue(clients, s2);
                        new ClientHandler(s2, username).menu();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return 1;
            }
            if (r.disconnected2) {
                lock.lock();
                db.logoutUser(getKeyByValue(clients, s2));
                removeFromQueues(getKeyByValue(clients, s2));
                db.finishGame(game_id, db.getUserID(getKeyByValue(clients, s1)));
                lock.unlock();
                Thread.startVirtualThread(() -> {
                    try {
                        OutputStream output1 = s1.getOutputStream();
                        PrintWriter writer1 = new PrintWriter(output1, true);


                        writer1.println("DISCONNECTED");
                        if(read(s1, "ACK")==0){
                            lock.lock();
                            db.logoutUser(getKeyByValue(clients, s1));
                            removeFromQueues(getKeyByValue(clients, s1));
                            lock.unlock();
                            return;
                        }
                        writer1.println("GOING TO MENU");
                        var username = getKeyByValue(clients, s1);
                        new ClientHandler(s1, username).menu();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return 0;
            }


            int p1Score = 0;
            int p2Score = 0;

            for(int i=1;i<=5;i++){
                int random = (int)(Math.random()*2);
                int finalI = i;

                var ref = new Object() {
                    int p1Choice = 0;
                    int p2Choice = 0;
                };
                Thread t1 = Thread.startVirtualThread(() -> {
                    ref.p1Choice = runGame(s1, random==0, finalI);
                    if (ref.p1Choice == -1) {
                        r.disconnected1= true;
                    }
                });
                Thread t2 = Thread.startVirtualThread(() -> {
                    ref.p2Choice = runGame(s2, random==1, finalI);
                    if (ref.p2Choice == -1) {
                        r.disconnected2 = (true);
                    }
                });

                t1.join();
                t2.join();

                if (r.disconnected1) {
                    lock.lock();
                    db.logoutUser(getKeyByValue(clients, s1));
                    removeFromQueues(getKeyByValue(clients, s1));
                    db.finishGame(game_id, db.getUserID(getKeyByValue(clients, s2)));
                    lock.unlock();
                    Thread.startVirtualThread(() -> {
                        try {
                            OutputStream output2 = s2.getOutputStream();
                            PrintWriter writer2 = new PrintWriter(output2, true);

                            writer2.println("DISCONNECTED");
                            if(read(s2, "ACK")==0){
                                lock.lock();
                                db.logoutUser(getKeyByValue(clients, s2));
                                removeFromQueues(getKeyByValue(clients, s2));
                                lock.unlock();
                                return;

                            }
                            writer2.println("GOING TO MENU");
                            var username = getKeyByValue(clients, s2);
                            new ClientHandler(s2, username).menu();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    return 1;
                }
                if (r.disconnected2) {
                    lock.lock();
                    db.logoutUser(getKeyByValue(clients, s2));
                    removeFromQueues(getKeyByValue(clients, s2));
                    db.finishGame(game_id, db.getUserID(getKeyByValue(clients, s1)));
                    lock.unlock();
                    Thread.startVirtualThread(() -> {
                        try {
                            OutputStream output1 = s1.getOutputStream();
                            PrintWriter writer1 = new PrintWriter(output1, true);


                            writer1.println("DISCONNECTED");
                            if(read(s1, "ACK")==0){
                                lock.lock();
                                db.logoutUser(getKeyByValue(clients, s1));
                                removeFromQueues(getKeyByValue(clients, s1));
                                lock.unlock();
                                return;
                            }
                            writer1.println("GOING TO MENU");
                            var username = getKeyByValue(clients, s1);
                            new ClientHandler(s1, username).menu();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    return 0;
                }

                int sum = ref.p1Choice + ref.p2Choice;

                if(sum%2==0){
                    if(random==0){
                        p2Score++;
                    }
                    else {
                        p1Score++;
                    }
                }
                else{
                    if(random==0){
                        p1Score++;
                    }
                    else {
                        p2Score++;
                    }
                }
            }

            final int score1 = p1Score;
            final int score2 = p2Score;

            Thread.startVirtualThread(() -> {
                try {
                    OutputStream output1 = s1.getOutputStream();
                    PrintWriter writer1 = new PrintWriter(output1, true);

                    writer1.println("GAME OVER");
                    if(read(s1, "GAME OVER") == 0){
                        throw new RuntimeException("ERROR WHILE READING 'GAME OVER'");
                    }
                    writer1.println("YOUR SCORE: " + score1);
                    if(read(s1, "YOUR SCORE")==0){
                        throw new RuntimeException("ERROR WHILE READING 'YOUR SCORE'");
                    }
                    writer1.println("OPPONENT SCORE: " + score2);
                    if(read(s1, "OPPONENT SCORE")==0){
                        throw new RuntimeException("ERROR WHILE READING 'OPPONENT SCORE'");
                    }
                    writer1.println("YOU " + (score1>score2?"WON":"LOST"));
                    if(read(s1, "GOING TO MENU")==0){
                        throw new RuntimeException("ERROR WHILE READING 'GOING TO MENU'");
                    }
                    else{
                        writer1.println("MENU");
                        var username = getKeyByValue(clients, s1);
                        new ClientHandler(s1, username).menu();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread.startVirtualThread(() -> {
                try {
                    OutputStream output2 = s2.getOutputStream();
                    PrintWriter writer2 = new PrintWriter(output2, true);

                    writer2.println("GAME OVER");
                    if(read(s2, "GAME OVER") == 0){
                        throw new RuntimeException("ERROR WHILE READING 'GAME OVER'");
                    }
                    writer2.println("YOUR SCORE: " + score2);
                    if(read(s2, "YOUR SCORE")==0){
                        throw new RuntimeException("ERROR WHILE READING 'YOUR SCORE'");
                    }
                    writer2.println("OPPONENT SCORE: " + score1);
                    if(read(s2, "OPPONENT SCORE")==0){
                        throw new RuntimeException("ERROR WHILE READING 'OPPONENT SCORE'");
                    }
                    writer2.println("YOU " + (score2>score1?"WON":"LOST"));
                    if(read(s2, "GOING TO MENU")==0){
                        throw new RuntimeException("ERROR WHILE READING 'GOING TO MENU'");
                    }
                    else{
                        writer2.println("MENU");
                        var username = getKeyByValue(clients, s2);
                        new ClientHandler(s2, username).menu();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            if(score1>score2){
                return 0;
            }
            return 1;

        }catch (Exception e){
            e.printStackTrace();
        }

        return -1;

    }

    private static int runGame(SSLSocket s, boolean isOdd, int round){
        try {
            OutputStream output = s.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String str = "ROUND " + round;
            Thread.sleep(10);
            writer.println(str);

            var check = read(s, "ACK");
            if(check==0){
                return -1;
            }
            if ( check == -1){
                System.out.println("TIMEOUT");
                return -1;
            }


            if (isOdd) {
                writer.println("ODD");
            } else {
                writer.println("EVEN");
            }

            var check2 = read(s, "ACK");
            if(check2==0){
                return -1;
            }
            if(check2==-1){
                System.out.println("TIMEOUT");
                return -1;
            }
            writer.println("CHOICE");

            int ret = readInt(s);

            if(ret==-1){
                System.out.println("TIMEOUT");
                return -1;
            }

            return ret;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    private static void removeFromQueues(String username){
        if(simpleQ.contains(username)){
            simpleQ.remove(username);
        }
        if(rank1Q.contains(username)){
            rank1Q.remove(username);
        }
        if(rank2Q.contains(username)){
            rank2Q.remove(username);
        }
        if(rank3Q.contains(username)){
            rank3Q.remove(username);
        }
        if(clients.containsKey(username)){
            clients.remove(username);
        }
    }

    private static class ClientHandler {

        private final SSLSocket clientSocket;

        private String username;


        public ClientHandler(SSLSocket socket){
            clientSocket = socket;
        }

        public ClientHandler(SSLSocket socket, String username){
            clientSocket = socket;
            this.username = username;
        }


        private void run() {
            try {
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                String line;
                while (true) {
                    boolean toBreak = false;

                    line = readString(clientSocket);

                    if (line==null){
                        lock.lock();
                        db.logoutUser(this.username);
                        removeFromQueues(this.username);
                        lock.unlock();
                        writer.close();
                        clientSocket.close();
                        return;
                    }
                    switch (line){
                        case "NEW CLIENT":
                            writer.println("AUTH OPTIONS");
                            int option = readInt(clientSocket);
                            if (option == -1) {
                                writer.println("ERROR");
                                continue;
                            }
                            writer.println("USERNAME");
                            String username = readString(clientSocket);
                            if (username == null){
                                writer.println("ERROR");
                                continue;
                            }
                            writer.println("PASSWORD");
                            String password = readString(clientSocket);

                            if (password == null) {
                                continue;
                            }
                            int connection;
                            if(option==2){
                                Server.lock.lock();
                                connection = db.insertUser(username, password);
                                Server.lock.unlock();
                                if (connection == -1) {
                                    writer.println("REGISTER FAILED");
                                } else  {
                                    writer.println("REGISTER SUCCESSFUL");
                                    Server.lock.lock();
                                    if(clients.put(username, clientSocket)!=null){
                                        clients.replace(username, clientSocket);
                                    }
                                    Server.lock.unlock();
                                    this.username = username;
                                    System.out.println("Clients: " + clients);
                                    menu();
                                    toBreak = true;
                                }

                            }
                            else if(option==1) {
                                Server.lock.lock();
                                connection = db.loginUser(username, password);
                                Server.lock.unlock();

                                if (connection == 1) {
                                    writer.println("LOGIN SUCCESSFUL");
                                    Server.lock.lock();
                                    if(clients.put(username, clientSocket)!=null){
                                        clients.replace(username, clientSocket);
                                    }
                                    Server.lock.unlock();
                                    this.username = username;
                                    System.out.println("Clients: " + clients);
                                    menu();
                                    toBreak = true;
                                } else if (connection == -2) {
                                    writer.println("USER CONNECTED");
                                } else {
                                    writer.println("LOGIN FAILED");
                                }
                            }
                            break;
                        case "ERROR":
                            writer.close();
                            clientSocket.close();
                            break;
                        default:
                            lock.lock();
                            db.logoutUser(this.username);
                            removeFromQueues(this.username);
                            lock.unlock();
                            writer.close();
                            clientSocket.close();
                            break;
                    }
                    if(toBreak)break;
                }
            } catch (IOException ex) {
                if (ex.getMessage().equals("Connection reset")) {
                    System.out.println("Client disconnected");
                    clients.remove(this.username);
                    System.out.println("Clients: " + clients);
                } else {
                    System.out.println("Server exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }

        private void menu(){
            try {
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);


                if (read(clientSocket, "MENU")==0){
                    lock.lock();
                    db.logoutUser(this.username);
                    removeFromQueues(this.username);
                    lock.unlock();
                    return;
                }

                writer.println("MENU OPTION");

                String opt = readString(clientSocket);

                if(opt ==null){
                    lock.lock();
                    db.logoutUser(this.username);
                    removeFromQueues(this.username);
                    lock.unlock();
                    return;
                }

                if (opt.equals("LOGOUT")){
                    Server.lock.lock();
                    int logout = db.logoutUser(this.username);
                    removeFromQueues(this.username);
                    if (logout == 1) {
                        writer.println("LOGOUT SUCCESSFUL");
                        clients.remove(this.username);
                        Server.lock.unlock();
                        return;
                    } else {
                        writer.println("LOGOUT FAILED");
                    }
                    Server.lock.unlock();
                }
                else if(opt.equals("LEADERBOARD")){
                    Server.lock.lock();
                    var leaderboard = db.getTop10();
                    var userRank = db.getRank(this.username);
                    Server.lock.unlock();

                    for(var i : leaderboard){
                        writer.println(i);
                        if(read(clientSocket, "ACK")==0){
                            lock.lock();
                            db.logoutUser(this.username);
                            removeFromQueues(this.username);
                            lock.unlock();
                            return;
                        }
                    }

                    writer.println("...->");
                    if(read(clientSocket, "ACK")==0){
                        lock.lock();
                        db.logoutUser(this.username);
                        removeFromQueues(this.username);
                        lock.unlock();
                        return;
                    }
                    writer.println(userRank + " -- YOUR POSITION --");
                    if(read(clientSocket, "ACK")==0){
                        lock.lock();
                        db.logoutUser(this.username);
                        removeFromQueues(this.username);
                        lock.unlock();
                        return;
                    }

                    writer.println("->DONE");
                    menu();
                }
                else{
                    playGame();
                }

            }
            catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        private void playGame(){
            try {
                InputStream input = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.println("GAME TYPE");

                String gameType = readString(clientSocket);

                if(gameType==null){
                    lock.lock();
                    db.logoutUser(this.username);
                    removeFromQueues(this.username);
                    lock.unlock();
                    return;
                }

                while(true) {
                    if (gameType==null) continue;
                    if (gameType.equals("SIMPLE")) {
                        lock.lock();
                        simpleQ.add(this.username);
                        lock.unlock();
                        writer.println("WAIT");
                    } else {
                        lock.lock();
                        int score = db.getScore(this.username);
                        lock.unlock();
                        if (score == -1) {
                            writer.println("ERROR");
                            return;
                        }
                        if (score >= 0 && score <= 10) {
                            rank1Q.add(this.username);

                            writer.println("WAIT");
                        } else if (score >= 11 && score <= 25) {
                            rank2Q.add(this.username);
                            writer.println("WAIT");

                        } else {
                            rank3Q.add(this.username);
                            writer.println("WAIT");
                        }
                    }
                    break;
                }

                condLock.lock();
                cond.signal();
                condLock.unlock();

            }
            catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
