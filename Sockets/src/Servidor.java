import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void connecta() {
        try {
            srvSocket = new ServerSocket(PORT);
            System.out.println("Servidor escuchando en el puerto " + PORT);
            clientSocket = srvSocket.accept();
            System.out.println("Cliente conectado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void repDades() {
        try {
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = reader.readLine();
                if (message == null || message.equalsIgnoreCase("chao")) {
                    System.out.println("Cliente desconectado.");
                    reader.close();
                    break;
                }
                System.out.println("Mensaje recibido: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tanca() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (srvSocket != null) srvSocket.close();
            System.out.println("Servidor cerrado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.connecta();
        servidor.repDades();
        servidor.tanca();
    }
}
