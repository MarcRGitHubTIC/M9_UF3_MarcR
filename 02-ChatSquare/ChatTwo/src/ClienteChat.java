import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteChat {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connecta() throws IOException {
        socket = new Socket(ServidorChat.HOST, ServidorChat.PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connected to server succeded.");
    }

    public void enviarMensaje(String mensaje) throws IOException {
        out.writeObject(mensaje);
        out.flush();
    }

    public void tancarCliente() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Client disconnected.");
        }
    }

    public static void main(String[] args) {
        ClienteChat cliente = new ClienteChat();
        Scanner scanner = new Scanner(System.in);

        try {
            cliente.connecta();
            
            System.out.print("Input your name: ");
            String nombre = scanner.nextLine();
            cliente.enviarMensaje(nombre);

            HiloLectorCX hilo = new HiloLectorCX(cliente.in);
            hilo.start();

            String mensaje;
            do {
                mensaje = scanner.nextLine();
                cliente.enviarMensaje(mensaje);
            } while (!mensaje.equalsIgnoreCase(ServidorChat.MSG_EXIT));

            hilo.join();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
            try {
                cliente.tancarCliente();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
