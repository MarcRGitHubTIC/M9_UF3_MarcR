import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    
    private Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;
    private ServerSocket serverSocket;

    public void servidorAEscoltar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        
        while (!sortir) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());
            
            GestorClients gestor = new GestorClients(clientSocket, this);
            Thread thread = new Thread(gestor);
            thread.start();
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al parar el servidor: " + e.getMessage());
        }
    }

    public void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        clients.clear();
        sortir = true;
        System.out.println("Tancant tots els clients.");
        pararServidor();
        System.exit(0);
    }

    public void afegirClient(GestorClients client) {
        clients.put(client.getNom(), client);
        enviarMissatgeGrup("DEBUG: multicast Entra: " + client.getNom());
    }

    public void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
            System.out.println(nom + " desconnectat.");
        }
    }

    public void enviarMissatgeGrup(String missatge) {
    for (GestorClients client : clients.values()) {
        client.enviarMissatge("SERVER", missatge);
    }
    System.out.println("DEBUG: multicast " + missatge);
}

    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
    if (clients.containsKey(destinatari)) {
        clients.get(destinatari).enviarMissatge(remitent, missatge);
        System.out.println("Missatge personal enviat a (" + destinatari + ") de (" + remitent + "): " + missatge);
    } else {
        System.out.println("Destinatari no trobat: " + destinatari);
    }
}

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.servidorAEscoltar();
        } catch (IOException e) {
            System.err.println("Error al servidor: " + e.getMessage());
        } finally {
            servidor.pararServidor();
        }
    }
}