package org.example;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.security.MessageDigest;

public class Utils {

    public final static int TIMEOUT = 1000;

    public static int read (SSLSocket socket, String toRead) {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));


            while(true){
                if(socket.isClosed()){
                    return -1;
                }
                String line = reader.readLine();
                if(line == null)continue;
                if(line.equals("DISCONNECTED")) {
                    return -2;
                }
                if (line.equals(toRead)) {
                    break;
                }
            }

            return 1;
        }
        catch (SocketTimeoutException e){
            return -1;
        }
        catch (IOException e) {
            return 0;
        }
    }

    public static int readInt (SSLSocket socket) {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line = "";

            while(true){
                if(socket.isClosed()){
                    return -1;
                }
                line = reader.readLine();
                if(line == null)continue;
                if(line.equals("DISCONNECTED")) {
                    return -2;
                }
                break;
            }

            return Integer.parseInt(line);
        }
        catch (Exception e) {
            return -1;
        }
    }

    public static String readString (SSLSocket socket) {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line = "";


            while(true){
                if(socket.isClosed()){
                    return "TIMEOUT";
                }
                line = reader.readLine();
                if(line == null)continue;
                if(line.equals("DISCONNECTED")) {
                    return "DISCONNECTED";
                }
                break;
            }

            return line;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String readOneFrom (SSLSocket socket, String[] toRead) {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line = "";

            while(true){
                if(socket.isClosed()){
                    return "TIMEOUT";
                }
                line = reader.readLine();
                if(line == null)continue;
                if(line.equals("DISCONNECTED")) {
                    return "DISCONNECTED";
                }
                for (String s : toRead) {
                    if (line.equals(s)) {
                        return s;
                    }
                }
            }
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String readOneFromWithoutTimeout (SSLSocket socket, String[] toRead) {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line = "";

            while(true){
                if(socket.isClosed()){
                    return "TIMEOUT";
                }
                line = reader.readLine();
                if(line == null)continue;
                if(line.equals("DISCONNECTED")) {
                    return "DISCONNECTED";
                }
                for (String s : toRead) {
                    if (line.equals(s)) {
                        return s;
                    }
                }
            }
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String readStringContaining(SSLSocket socket, String toRead) {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line = "";

            while(true){
                if(socket.isClosed()){
                    return "TIMEOUT";
                }
                line = reader.readLine();
                if(line == null)continue;
                if(line.equals("DISCONNECTED")) {
                    return "DISCONNECTED";
                }
                if (line.contains(toRead)) {
                    return line;
                }
            }
        }
        catch (Exception e) {
            return null;
        }
    }


    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null; // Return null if the value is not found
    }

    public static String hashWithSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
