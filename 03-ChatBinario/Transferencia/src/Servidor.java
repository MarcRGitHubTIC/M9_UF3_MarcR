import java.io.*;
import java.net.*;

public class Servidor {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";

    private ServerSocket serverSocket;
    private Socket socket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("[Servidor] Esperando conexiones en el puerto " + PORT + "...");
        socket = serverSocket.accept();
        System.out.println("[Servidor] Conexión aceptada desde: " + socket.getInetAddress().getHostAddress() +
                           ":" + socket.getPort());
        return socket;
    }

    public void tancaConnexio(Socket socket) throws IOException {
        if (socket != null) socket.close();
        if (serverSocket != null) serverSocket.close();
        System.out.println("[Servidor] Conexión cerrada.");
    }

    public void enviarFitxer(Socket socket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            String nomFitxer = (String) in.readObject();
            System.out.println("[Servidor] Nombre del fichero solicitado: " + nomFitxer);

            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] dades = fitxer.getContingut();

            out.writeObject(dades);
            out.flush();

            System.out.println("[Servidor] Fichero leído correctamente desde disco.");
            System.out.println("[Servidor] Enviando " + dades.length + " bytes al cliente: " +
                               socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        } catch (Exception e) {
            System.err.println("[Servidor] Error al enviar fichero: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            Socket socket = servidor.connectar();
            servidor.enviarFitxer(socket);
            servidor.tancaConnexio(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
