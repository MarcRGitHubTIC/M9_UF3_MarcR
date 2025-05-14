import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir = false;

    public void connecta() throws IOException {
        socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws IOException {
        oos.writeObject(missatge);
        oos.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            System.out.println("Tancant client...");
        } catch (IOException e) {
            System.err.println("Error al tancar client: " + e.getMessage());
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pas obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línea en blanco)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        String linia;
        do {
            System.out.print(missatge);
            linia = scanner.nextLine().trim();
            if (!obligatori && linia.isEmpty()) {
                return linia;
            }
        } while (linia.isEmpty());
        return linia;
    }

    public void run() {
    try {
        ois = new ObjectInputStream(socket.getInputStream());
        System.out.println("DEBUG: Iniciant rebuda de missatges...");
        
        while (!sortir) {
            try {
                String missatgeRaw = (String) ois.readObject();
                if (missatgeRaw == null || missatgeRaw.trim().isEmpty()) {
                    continue;
                }

                String[] parts = missatgeRaw.split("#", 2);
                if (parts.length < 2) {
                    System.err.println("WARN: missatge incomplet: " + missatgeRaw);
                    continue;
                }

                String remitent = parts[0];
                String contingut = parts[1];

                if (remitent.equals("SERVER")) {
                    String codi = Missatge.getCodiMissatge(contingut);
                    if (codi != null) {
                        switch (codi) {
                            case Missatge.CODI_SORTIR_TOTS:
                                System.out.println("SERVIDOR: " + Missatge.getPartsMissatge(contingut)[1]);
                                sortir = true;
                                break;
                            default:
                                System.out.println("SERVIDOR: " + contingut);
                        }
                    } else {
                        System.out.println("SERVIDOR: " + contingut);
                    }
                } else {
                    System.out.println("Missatge de (" + remitent + "): " + contingut);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Error rebent missatge. Sortint...");
                sortir = true;
            }
        }
    } catch (IOException e) {
        System.err.println("Error al fil de recepció: " + e.getMessage());
    } finally {
        System.out.println("Flux d'entrada tancat.");
        System.out.println("Flux de sortida tancat.");
    }
}

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try {
            client.connecta();
            
            Thread thread = new Thread(client::run);
            thread.start();
            
            client.ajuda();
            Scanner scanner = new Scanner(System.in);
            
            while (!client.sortir) {
                String linia = client.getLinea(scanner, "", false);
                if (linia.isEmpty()) {
                    client.sortir = true;
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                } else {
                    try {
                        int opcio = Integer.parseInt(linia);
                        switch (opcio) {
                            case 1:
                                String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                                client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                                break;
                                
                            case 2:
                                String dest = client.getLinea(scanner, "Destinatari: ", true);
                                String msg = client.getLinea(scanner, "Missatge a enviar: ", true);
                                client.enviarMissatge(Missatge.getMissatgePersonal(dest, msg));
                                break;
                                
                            case 3:
                                String msgGrup = client.getLinea(scanner, "Missatge al grup: ", true);
                                client.enviarMissatge(Missatge.getMissatgeGrup(msgGrup));
                                break;
                                
                            case 4:
                                client.sortir = true;
                                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                                break;
                                
                            case 5:
                                client.sortir = true;
                                client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                                break;
                                
                            default:
                                System.out.println("Opció no vàlida");
                        }
                        client.ajuda();
                    } catch (NumberFormatException e) {
                        System.out.println("Introdueix un número vàlid");
                    } catch (IOException e) {
                        System.err.println("Error enviant missatge: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al client: " + e.getMessage());
        } finally {
            client.tancarClient();
        }
    }
}