package ppd_trabalhoetp2;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author victor
 */
public class ComunicaCliente implements Runnable {
    private Socket s;
    private String ultimaMsg;
    private DataInputStream input;

    public ComunicaCliente(Socket s){
        try{
            this.s = s;
            ultimaMsg = "";
            input = new DataInputStream(s.getInputStream());
        }
        catch(IOException e){
            System.out.println("Erro: ComunicaCliente -> " + e);
        }
    }

    private String getUltimaMsgRecebida(){
        return this.ultimaMsg;
    }

    @Override
    public void run(){
        int flag = 1;

        while(flag == 1){
            try{
                this.ultimaMsg = input.readUTF();
                System.out.println("Mensagem do servidor: " + ultimaMsg);
                System.out.print("->");
            }
            catch(IOException e){
                flag = 0;
            }
        }
    }
}
