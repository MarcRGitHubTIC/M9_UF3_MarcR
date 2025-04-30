import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "C:\\Users\\Fuck off\\Desktop\\DAM\\tmp\\received";
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    public void connectar() throws IOException {
        socket = new Socket(Servidor.HOST, Servidor.PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        System.out.println("[Cliente] Conectado al servidor en: " + socket.getInetAddress().getHostAddress() +
                           ":" + socket.getPort());
    }

    public void rebreFitxer() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("[Cliente] Introduce la ruta completa del fichero en el servidor: ");
            String nomFitxer = scanner.nextLine();

            out.writeObject(nomFitxer);
            out.flush();
            System.out.println("[Cliente] Nombre del fichero enviado al servidor: " + nomFitxer);

            byte[] contingut = (byte[]) in.readObject();
            System.out.println("[Cliente] Fichero recibido (" + contingut.length + " bytes)");

            String nom = new File(nomFitxer).getName();
            File fitxerLocal = new File(DIR_ARRIBADA, nom);
            Files.write(fitxerLocal.toPath(), contingut);

            System.out.println("[Cliente] Fichero guardado en: " + fitxerLocal.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[Cliente] Error al recibir el fichero: " + e.getMessage());
        }
    }

    public void tancarConnexio() throws IOException {
        if (socket != null) socket.close();
        System.out.println("[Cliente] Conexión cerrada.");
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connectar();
            client.rebreFitxer();
            client.tancarConnexio();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
