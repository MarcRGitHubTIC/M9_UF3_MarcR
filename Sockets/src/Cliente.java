
import java.util.Scanner;



public class Cliente {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";

    

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
