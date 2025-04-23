import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ServidorChat {

    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_EXIT = "exit";

    private ServerSocket serverSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server started PORT " + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Server stopped.");
        }
    }

    public String getNom(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public static void main(String[] args) {
        ServidorChat servidor = new ServidorChat();
        try {
            servidor.iniciarServidor();

            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connected.");

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            String nombreCliente = servidor.getNom(in);
            System.out.println("Client name is " + nombreCliente);

            HiloServidorChat hilo = new HiloServidorChat(in);
            hilo.start();

            Scanner scanner = new Scanner(System.in);
            String mensaje;
            do {
                mensaje = scanner.nextLine();
                out.writeObject(mensaje);
                out.flush();
            } while (!mensaje.equalsIgnoreCase(MSG_EXIT));

            hilo.join();
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                servidor.pararServidor();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
