import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients implements Runnable {
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket client, ServidorXat servidor) throws IOException {
        this.client = client;
        this.servidor = servidor;
        this.oos = new ObjectOutputStream(client.getOutputStream());
        this.ois = new ObjectInputStream(client.getInputStream());
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        try {
            while (!sortir) {
                String missatge = (String) ois.readObject();
                processaMissatge(missatge);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al gestor: " + e.getMessage());
        } finally {
            try {
                if (client != null && !client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                System.err.println("Error al tancar client: " + e.getMessage());
            }
        }
    }

    public void enviarMissatge(String remitent, String missatge) {
    try {
        oos.writeObject(remitent + "#" + missatge);
        oos.flush();
    } catch (IOException e) {
        System.err.println("Error enviant missatge: " + e.getMessage());
    }
}

    public void processaMissatge(String missatgeRaw) {
        String codi = Missatge.getCodiMissatge(missatgeRaw);
        String[] parts = Missatge.getPartsMissatge(missatgeRaw);

        if (codi == null || parts == null) {
            System.err.println("Missatge incorrecte rebut: " + missatgeRaw);
            return;
        }

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                this.nom = parts[1];
                servidor.afegirClient(this);
                break;
                
            case Missatge.CODI_SORTIR_CLIENT:
                servidor.eliminarClient(this.nom);
                this.sortir = true;
                break;
                
            case Missatge.CODI_SORTIR_TOTS:
                this.sortir = true;
                servidor.finalitzarXat();
                break;
                
            case Missatge.CODI_MSG_PERSONAL:
                String destinatari = parts[1];
                String msgPersonal = parts[2];
                servidor.enviarMissatgePersonal(destinatari, this.nom, msgPersonal);
                break;
                
            case Missatge.CODI_MSG_GRUP:
                String msgGrup = parts[1];
                servidor.enviarMissatgeGrup(this.nom + ": " + msgGrup);
                break;
                
            default:
                System.err.println("Codi desconegut: " + codi);
        }
    }
}