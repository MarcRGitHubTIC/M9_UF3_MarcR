import java.io.*;

public class HiloServidorChat extends Thread {

    private ObjectInputStream in;

    public HiloServidorChat(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String mensaje;
            while (true) {
                mensaje = (String) in.readObject();
                if (mensaje.equalsIgnoreCase(ServidorChat.MSG_EXIT)) {
                    System.out.println("Client left the chat.");
                    break;
                }
                System.out.println("Client: " + mensaje);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Conexion closed or error in the thread.");
        }
    }
}
