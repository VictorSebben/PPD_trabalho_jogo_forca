package ppd_trabalhoetp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author victor
 */
public class Jogador {
    private String host;
    private Integer porta;
    private Socket socket;
    public static String cmds[] = {"loginRQT#user#password",
                                   "exitRQT#try"};

    public Jogador(String host, Integer porta){
        this.host = host;
        this.porta = porta;
    }


    public String lerInput(){
        while(true){
            Scanner scanner = new Scanner(new InputStreamReader(System.in));
            System.out.print("Digite o comando no formato 'TIPO#param1#param2' ou ? para ajuda: \n->");
            String str = scanner.nextLine().toUpperCase();

            if(str.trim().equals("?")){
                System.out.println("-------------------------------\nComandos:");
                for (String cmd : cmds){
                    System.out.println("\t" + cmd);
                }
                System.out.println("\n-------------------------------\n");
            }
            else
                return str;
        }
    }

    public void comunicaServidor() throws IOException
    {
        socket = new Socket(host, porta);

        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        DataInputStream input = new DataInputStream(socket.getInputStream());

        String str = "";
        ComunicaCliente comCliente = new ComunicaCliente(this.socket);
        Thread t = new Thread(comCliente);
        t.start();

        while(!str.startsWith("EXITREQUEST")){

            str = this.lerInput();
            output.writeUTF(str);

            try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){
                System.out.println("Erro ao fazer thread nanar: " + e);
            }

        }

        socket.close();
    }

    public static void main(String args[])
    {
        Jogador c = new Jogador("localhost", 55555);

        try {
            c.comunicaServidor();
        } catch (IOException ex) {
            Logger.getLogger(Jogador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
