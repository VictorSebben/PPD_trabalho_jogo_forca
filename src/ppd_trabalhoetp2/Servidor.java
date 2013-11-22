package ppd_trabalhoetp2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author victor
 */
public class Servidor {
    private Integer porta;
    private ServerSocket server;
    private Jogo jogo;

    public Servidor(Integer porta){
        this.porta = porta;

        this.jogo = new Jogo();
    }

    public void criaServidor() throws IOException{
        server = new ServerSocket(porta);
    }

    public void fazComunicacao() throws IOException {

        while(true){
            System.out.println("Esperando conexão...");
            Socket socket = server.accept(); // até que alguém não tentar se conectar, para no accept()

            ControladorJogador ctrlJogador = new ControladorJogador(socket, jogo);

            Thread t = new Thread(ctrlJogador);
            t.start();

            this.jogo.addCtrlJogador(ctrlJogador);
        }
    }

    public static void main(String args[]) {
        Servidor server = new Servidor(55555);
        try{
            server.criaServidor();
            server.fazComunicacao();
        }
        catch (IOException ex){
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}