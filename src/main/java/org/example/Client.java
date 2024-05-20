package org.example;

import java.io.*;
import javax.net.ssl.*;
import java.security.KeyStore;

import static org.example.Utils.*;

public class Client {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ClientTest <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            char[] keystorePassword = "password".toCharArray(); // replace with your keystore password
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream keyStoreStream = new FileInputStream("serverkeystore.jks")) {
                keyStore.load(keyStoreStream, keystorePassword);
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(hostname, port)) {
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                while (true) {
                    writer.println("NEW CLIENT");

                    int option = 0;
                    InputStream input = socket.getInputStream();
                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(input));

                    var check = read(socket, "AUTH OPTIONS");
                    if (check == 0) {
                        System.out.println("Server not found");
                        return;
                    } else if (check == -1) {
                        System.out.println("TIMEOUT");
                        return;
                    }
                    System.out.println("\n\t\t\t\t***ODD OR EVEN***");
                    System.out.println("\t\tWELCOME TO THE GAME THAT IS EVEN ODDER THAN YOU THINK!\n");

                    while (true) {
                        System.out.println("PRESS 1 FOR LOGIN, PRESS 2 FOR REGISTER: ");
                        String opt = (reader1.readLine());
                        try {
                            option = Integer.parseInt(opt);
                            if (option == 1 || option == 2) {
                                writer.println(option);
                                break;
                            } else {
                                System.out.println("INVALID OPTION");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("INVALID OPTION");
                        }
                    }


                    if (option == 1) {

                        var check1 = read(socket, "USERNAME");
                        if (check1 == 0) {
                            System.out.println("Server not found");
                            return;
                        } else if (check1 == -1) {
                            System.out.println("TIMEOUT");
                            return;
                        }

                        System.out.println("ENTER YOUR USERNAME: ");
                        String username1 = reader1.readLine();
                        writer.println(username1);

                        var check2 = read(socket, "PASSWORD");
                        if (check2 == 0) {
                            System.out.println("Server not found");
                            return;
                        } else if (check2 == -1) {
                            System.out.println("TIMEOUT");
                            return;
                        }

                        System.out.println("ENTER YOUR PASSWORD: ");
                        String password = reader1.readLine();
                        writer.println(hashWithSHA256(password));


                        String line3 = readString(socket);

                        if (line3 == null) {
                            System.out.println("Server not found");
                            return;
                        } else if (line3.equals("TIMEOUT")) {
                            System.out.println("TIMEOUT");
                            return;
                        }

                        if (line3.equals("LOGIN SUCCESSFUL")) {
                            System.out.println("LOGIN SUCCESSFUL");
                            menu(socket);
                            break;
                        } else if (line3.equals("USER CONNECTED")) {
                            System.out.println("USER ALREADY CONNECTED");
                        } else {
                            System.out.println("LOGIN FAILED");
                        }
                    } else if (option == 2) {
                        var check3 = read(socket, "USERNAME");
                        if (check3 == 0) {
                            System.out.println("Server not found");
                            return;
                        } else if (check3 == -1) {
                            System.out.println("TIMEOUT");
                            return;
                        }

                        System.out.println("ENTER YOUR USERNAME: ");
                        String username1 = reader1.readLine();
                        writer.println(username1);


                        var check4 = read(socket, "PASSWORD");
                        if (check4 == 0) {
                            System.out.println("Server not found");
                            return;
                        } else if (check4 == -1) {
                            System.out.println("TIMEOUT");
                            return;
                        }


                        var check5 = read(socket, "PASSWORD");
                        if (check5 == 0) {
                            System.out.println("Server not found");
                            return;
                        } else if (check5 == -1) {
                            System.out.println("TIMEOUT");
                            return;
                        }

                        while (true) {
                            System.out.println("ENTER YOUR PASSWORD: ");
                            String password = reader1.readLine();
                            System.out.println("RE ENTER YOUR PASSWORD: ");
                            String password2 = reader1.readLine();
                            if (password.equals(password2)) {
                                writer.println(hashWithSHA256(password));
                                break;
                            } else {
                                writer.println("ERROR");
                            }
                        }

                        String line3 = readString(socket);

                        if (line3 == null) {
                            System.out.println("Server not found");
                            return;
                        }
                        if (line3.equals("TIMEOUT")) {
                            System.out.println("TIMEOUT");
                            return;
                        }

                        if (line3.equals("REGISTER SUCCESSFUL")) {
                            System.out.println("REGISTER SUCCESSFUL");
                            menu(socket);
                            break;
                        } else if (line3.equals("USER EXISTS")) {
                            System.out.println("USER ALREADY EXISTS");
                        } else {
                            System.out.println("REGISTER FAILED");
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Server not found: " + ex.getMessage());

            }
        } catch (Exception e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }


    private static void menu(SSLSocket socket){
        try{
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            InputStream input = socket.getInputStream();

            writer.println("MENU");


            var check = read(socket, "MENU OPTION");
            if(check==0){
                System.out.println("Server not found");
                return;
            }
            else if (check == -1){
                System.out.println("TIMEOUT");
                return;
            }

            while (true) {
                System.out.println("PRESS 1 TO LOGOUT, PRESS 2 TO PLAY GAME, PRESS 3 TO SEE LEADERBOARD: ");

                try {
                    int option = Integer.parseInt(reader1.readLine());

                    if (option == 1) {
                        logout(socket);
                        return;
                    } else if (option == 2) {
                        startGame(socket);
                        break;
                    }
                    else if(option==3){
                        leaderboard(socket);
                        break;
                    }
                    else {
                        System.out.println("INVALID OPTION MENU");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid option");
                } catch (IOException e) {
                    System.out.println("An error occurred while reading input");
                    e.printStackTrace();
                }
            }

        }
        catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private static void leaderboard(SSLSocket socket){
        try {
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println("LEADERBOARD");
            System.out.println("LEADERBOARD");

            while(true){
                var read = readStringContaining(socket, "->");

                if( read== null){
                    System.out.println("Server not found");
                    return;
                }
                if(read.equals("->DONE")){
                    break;
                }
                if(read.equals("...->")){
                    read = "...";
                }
                System.out.println(read);
                writer.println("ACK");

            }
            System.out.println("\nPRESS ANY KEY + ENTER TO GO BACK TO MENU");
            reader1.readLine();

            menu(socket);
        }
        catch (Exception e){
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    private static void logout(SSLSocket socket){
        try{
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            InputStream input = socket.getInputStream();

            writer.println("LOGOUT");

            String line = readString(socket);

            if(line == null){
                System.out.println("Server not found");
                return;
            }
            else if(line.equals("TIMEOUT")){
                System.out.println("TIMEOUT");
                return;
            }

            if (line.equals("LOGOUT SUCCESSFUL")){
                System.out.println("LOGOUT SUCCESSFUL");
            }
            else{
                System.out.println("LOGOUT FAILED");
            }
        }
        catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private static void startGame(SSLSocket socket){
        try{
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            InputStream input = socket.getInputStream();

            writer.println("PLAY GAME");

            var check = read(socket, "GAME TYPE");
            if(check==0){
                System.out.println("Server not found");
                return;
            }
            else if (check == -1){
                System.out.println("TIMEOUT");
                return;
            }

            int option = 0;

            while(true){
                System.out.println("CHOOSE A TYPE OF GAME: 1 for ranked, 2 for simple");
                String type = reader1.readLine();
                try{
                    option = Integer.parseInt(type);
                    if (option==1 || option==2) {
                        break;
                    }
                    else{
                        System.out.println("INVALID OPTION");
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println("INVALID OPTION");
                }
            }

            if (option==1){
                writer.println("RANKED");
                while(true){
                    String line1 = readOneFromWithoutTimeout(socket, new String[] {"START GAME", "WAIT"});
                    if(line1 == null){
                        System.out.println("Server not found");
                        return;
                    }
                    if(line1.equals("TIMEOUT")){
                        System.out.println("TIMEOUT");
                        return;
                    }

                    if (line1.equals("START GAME")){
                        System.out.println("GAME STARTED");
                        playGame(socket);
                        break;
                    }
                    if(line1.equals("WAIT")){
                        System.out.println("WAITING FOR ANOTHER PLAYER");
                    }
                }

            }
            else{
                writer.println("SIMPLE");

                while(true){
                    String line1 = readOneFrom(socket, new String[] {"START GAME", "WAIT"});
                    if(line1 == null){
                        System.out.println("Server not found");
                        return;
                    }
                    if(line1.equals("TIMEOUT")){
                        System.out.println("TIMEOUT");
                        return;
                    }

                    if (line1.equals("START GAME")){
                        System.out.println("GAME STARTED");
                        playGame(socket);
                        break;
                    }
                    if(line1.equals("WAIT")){
                        System.out.println("WAITING FOR ANOTHER PLAYER");
                    }
                }
            }
        }
        catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private static void playGame(SSLSocket socket){
        try{
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            InputStream input = socket.getInputStream();

            writer.println("GAME STARTED");
            while(true) {
                boolean gameOver = false;

                String game = readGame(socket);
                if(game.equals("TIMEOUT")){
                    System.out.println("SERVER WENT DOWN");
                    return;
                }

                if(game.equals("GAME OVER")){
                    writer.println("GAME OVER");
                    gameOver = true;
                    String myScore = readStringContaining(socket, "YOUR SCORE");
                    if(myScore == null){
                        System.out.println("Server not found");
                        return;
                    }
                    if(myScore.equals("TIMEOUT")){
                        System.out.println("TIMEOUT");
                        return;
                    }
                    writer.println("YOUR SCORE");
                    System.out.println(myScore);
                    String opponentScore = readStringContaining(socket, "OPPONENT SCORE");
                    if(opponentScore == null){
                        System.out.println("Server not found");
                        return;
                    }
                    if(opponentScore.equals("TIMEOUT")){
                        System.out.println("TIMEOUT");
                        return;
                    }
                    writer.println("OPPONENT SCORE");
                    System.out.println(opponentScore);
                    String result = readOneFrom(socket, new String[]{"YOU WON", "YOU LOST"});
                    if(result == null){
                        System.out.println("Server not found");
                        return;
                    }
                    if(result.equals("TIMEOUT")){
                        System.out.println("TIMEOUT");
                        return;
                    }
                    System.out.println(result);
                    writer.println("GOING TO MENU");
                    var check = read(socket, "MENU");
                    if(check == 0){
                        System.out.println("Server not found");
                        return;
                    }
                    else if (check == -1){
                        System.out.println("TIMEOUT");
                        return;
                    }
                    menu(socket);
                }
                else if(game.equals("DISCONNECTED")){
                    System.out.println("YOUR OPPONENT DISCONNECTED");
                    System.out.println("YOU WON");
                    writer.println("ACK");
                    if(read(socket, "GOING TO MENU") == 0){
                        System.out.println("Server not found");
                        return;
                    }
                    menu(socket);
                    return;
                }
                else{
                    writer.println("ACK");
                    System.out.println(game);
                }
                if (gameOver) break;

                String line = readOneFrom(socket, new String[]{"ODD", "EVEN"});
                if(line == null){
                    System.out.println("Server not found");
                    return;
                }
                if(line.equals("TIMEOUT")){
                    System.out.println("TIMEOUT");
                    return;
                }
                else if(line.equals("DISCONNECTED")){
                    System.out.println("YOUR OPPONENT DISCONNECTED");
                    System.out.println("YOU WON");
                    writer.println("ACK");
                    if(read(socket, "GOING TO MENU") == 0){
                        System.out.println("Server not found");
                        return;
                    }
                    menu(socket);
                    return;
                }
                System.out.println(line);
                writer.println("ACK");

                var check = read(socket, "CHOICE");
                if(check== 0){
                    System.out.println("Server not found");
                    return;
                }
                else if (check == -1){
                    System.out.println("TIMEOUT");
                    return;
                }
                else if(check==-2){
                    System.out.println("YOUR OPPONENT DISCONNECTED");
                    System.out.println("YOU WON");
                    writer.println("ACK");
                    if(read(socket, "GOING TO MENU") == 0){
                        System.out.println("Server not found");
                        return;
                    }
                    menu(socket);
                    return;
                }

                while (true) {
                    System.out.println("ENTER A NUMBER FROM 0 TO 9: ");
                    String n = reader1.readLine();
                    try {
                        int num = Integer.parseInt(n);
                        if (num < 0 || num > 9) {
                            System.out.println("INVALID NUMBER");
                            continue;
                        }
                        writer.println(n);
                        System.out.println("WAITING FOR OPPONENT");
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("INVALID NUMBER");
                    }
                }
            }
        }
        catch (Exception ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private static String readGame(SSLSocket socket){
        try{
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line = "";
            var actual = System.currentTimeMillis();
            while(true){
                if(actual - System.currentTimeMillis() > TIMEOUT) {
                    return "TIMEOUT";
                }
                line = reader.readLine();
                if(line == null) continue;
                if(line.equals("GAME OVER")) break;
                if(line.contains("ROUND")) break;
                if(line.equals("DISCONNECTED")) {
                    break;
                }
            }

            return line;
        }
        catch (Exception e) {
            return "TIMEOUT";
        }
    }

}