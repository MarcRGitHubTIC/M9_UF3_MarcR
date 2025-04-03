import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";

    private Socket socket;
    private PrintWriter out;

    public void conecta() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Conectado al servidor en " + HOST + ":" + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void envia(String mensaje) {
        if (out != null) {
            out.println(mensaje);
            System.out.println("Mensaje enviado: " + mensaje);
        }
    }

    public void tanca() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Cliente cerrado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.conecta();

        cliente.envia("Prueba 1");
        cliente.envia("Prueba 2");
        cliente.envia("Chao");

        System.out.println("Presiona ENTER para cerrar el cliente...");
        new Scanner(System.in).nextLine();

        cliente.tanca();
    }
}
