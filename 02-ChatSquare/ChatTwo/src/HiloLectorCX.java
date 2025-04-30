import java.io.*;

public class HiloLectorCX extends Thread {

    private ObjectInputStream in;

    public HiloLectorCX(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String mensaje;
            while (true) {
                mensaje = (String) in.readObject();
                if (mensaje.equalsIgnoreCase(ServidorChat.MSG_EXIT)) {
                    System.out.println("Server closed connection.");
                    break;
                }
                System.out.println("Server: " + mensaje);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Conection closed or error in the thread.");
        }
    }
}
