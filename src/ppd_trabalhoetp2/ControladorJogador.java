package ppd_trabalhoetp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author victor
 */
public class ControladorJogador implements Runnable {
    Socket socket;
    DataInputStream input;
    DataOutputStream output;
    Estados estado = Estados.DESCONECTADO;
    private Jogo jogo; // o jogador (antigo TrataConexao) referencia o Jogo que está jogando
    // todos os clientes jogando juntos referenciarão o mesmo Jogo
    private String nome; // no login, o jogador diz o nome
    private int pontuacao; // número de vezes que o jogador ganhou
    private int erros; // erros cometidos - zerados a cada jogo
    private boolean vivo; // se está ainda jogando ou já perdeu. Com seis erros, troca o vivo para false
    private int contEspera; // a quantas jogadas está esperando para jogar

    public ControladorJogador(Socket socket, Jogo jogo){
        this.socket = socket;
        this.jogo = jogo;

        estado = Estados.CONECTADO;

        estado = Estados.DESAUTENTICADO;
        this.vivo = true;
        this.contEspera = 0;
        this.pontuacao = 0;

        /*
        * TODO
        * Para setar os erros, tem que receber a info do servidor.
        * Se o jogo ainda não tiver iniciado, fica com 0. Caso contrário,
        * recebe o número de erros do que mais tem erros (vivo) no jogo.
        * Aqui no construtor, chamar um método que, a partir disso, inicializa o número de erros.
        */
    }

    public void createStreams(){
        try{
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException e){
            System.out.println("Erro ao criar streams: " + e);

            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ControladorJogador.class.getName()).log(Level.SEVERE, null, ex);
            }

            estado = Estados.DESCONECTADO;
        }
    }

     public void enviaMensagem(String msg){
        try{
            output.writeUTF(msg);
        }
        catch (IOException ex){
            System.out.println("Erro ao enviar Mensagem - " + ex.getMessage());
            try{
                estado = Estados.DESCONECTADO; // para que o while não fique rodando.
                socket.close();
            }
            catch (IOException ex1){
                System.out.println("Erro ao enviar mensagem: " + ex1.getMessage());
            }
        }
    }

    public String recebeMensagem(){
        String msg = "";
        try{
            msg = input.readUTF();
        }
        catch (IOException ex){
            this.estado = Estados.DESCONECTADO;
            System.out.println("Erro ao receber mensagem - "+ ex.getMessage());
            try{
                estado = Estados.DESCONECTADO; // para que o while não fique rodando.
                socket.close();
            }
            catch (IOException ex1){
                Logger.getLogger(ControladorJogador.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return msg;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getNome(){
        return this.nome;
    }

    public Estados getEstado(){
        return this.estado;
    }

    public int getErros(){
        return this.erros;
    }

    public int getPontuacao(){
        return this.pontuacao;
    }

    public Socket getSocket(){
        return this.socket;
    }

    public Integer getContEspera(){
        return this.contEspera;
    }

    public void setContEspera(int num){
        this.contEspera = num;
    }

    public void resetErros(){
        this.erros = 0;
    }

    public void incrementarErros(){
        this.erros++;
    }

    public void incrementarPontuacao(){
        this.pontuacao++;
    }

    public void setEstado(Estados estado){
        this.estado = estado;
    }

    protected void avisarVez(){
        this.estado = Estados.SUAVEZ;
        System.out.println("Avisou a vez!");
        this.enviaMensagem("SUAVEZ");
    }

    public void setErrosEntrada(){
        this.erros = this.jogo.getErrosMaisLascado();
    }

    public void enviaMensagem(Socket socket, String msg){
        try{
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(msg);
        }
        catch(IOException e){
            System.out.println("ERRO: " + e);
        }
    }

    @Override
    public void run(){
        this.createStreams();

        while(estado != Estados.DESCONECTADO){
            String msgCliente;

            msgCliente = recebeMensagem();

            String[] msg = msgCliente.split("#");

            switch(estado){
                case DESAUTENTICADO:
                    // parse da string do cliente
                    switch(msg[0]){
                        case "LOGIN":
                            if(msg[2].equals("IFSUL")){
                                this.setNome(msg[1]);
                                this.estado = Estados.ESPERA;

                                // pegar o número de erros de acordo com quem está pior no jogo
                                // e já colocar na mensagem de LOGINREPLY#ACCEPT.
                                this.setErrosEntrada();

                                this.enviaMensagem("LOGINREPLY#ACCEPT#0#ERROS");
                                this.jogo.addCtrlJogador(this);
                            }
                            else
                                this.enviaMensagem("LOGINREPLY#REJECT");
                            break;

                        case "EXIT":
                            try{
                                this.enviaMensagem("EXITREPLY#ACCEPT#Encerrando...");
                                this.socket.close();
                                this.estado = Estados.DESCONECTADO;
                            }
                            catch(IOException ex){
                                System.out.println("Erro ao desconectar: " + ex.getMessage());
                            }
                            break;

                        default:
                            this.enviaMensagem("ERRO#COMANDO INVALIDO");
                    }
                    break;

                case ESPERA: case MORTO:
                     switch(msg[0]){
                        case "LOGOUT":
                            this.enviaMensagem("LOGOUTREPLY#ACCEPT");
                            this.estado = Estados.DESAUTENTICADO;
                            this.jogo.removerJogador(this);
                            this.jogo.broadcastMsg("Mensagem do servidor: " + this.getNome() + " saiu do jogo.");
                            break;

                        case "EXIT":
                            try{
                                this.enviaMensagem("EXITREPLY#ACCEPT#Encerrando...");
                                this.jogo.removerJogador(this);
                                this.socket.close();
                                this.estado = Estados.DESCONECTADO;
                                this.jogo.broadcastMsg("Mensagem do servidor: " + this.getNome() + " saiu do jogo.");
                            }
                            catch(IOException ex){
                                System.out.println("Erro ao desconectar: " + ex.getMessage());
                            }
                            break;

                        default:
                            this.enviaMensagem("ERRO#COMANDO INVALIDO");
                     }
                     break;

                case SUAVEZ:
                    // é a vez do jogador jogar
                    switch(msg[0])
                    {
                        case "JOGADA":
                            Character letra = msg[1].toLowerCase().charAt(0);
                            boolean flag = true;

                            // antes de verificar a letra, ver se já não foi testada.
                            for(int i = 0; i < jogo.tentativas.size(); i++){
                                if(letra == jogo.tentativas.get(i)){
                                    this.enviaMensagem("Mensagem do servidor: a letra escolhida já foi usada.");
                                    flag = false;
                                }
                            }

                            if(flag)
                                this.jogo.verificarTentativa(letra);
                            break;

                        case "LOGOUT":
                            this.enviaMensagem("LOGOUTREPLY#ACCEPT");
                            this.estado = Estados.DESAUTENTICADO;
                            this.jogo.removerJogador(this);
                            this.jogo.broadcastMsg("Mensagem do servidor: " + this.getNome() + " saiu do jogo.");
                            this.jogo.definirJogadorAtual();
                            break;

                        case "EXIT":
                            try{
                                this.enviaMensagem("EXITREPLY#ACCEPT#Encerrando...");
                                this.jogo.removerJogador(this);
                                this.socket.close();
                                this.estado = Estados.DESCONECTADO;
                                this.jogo.broadcastMsg("Mensagem do servidor: " + this.getNome() + " saiu do jogo.");
                                this.jogo.definirJogadorAtual();
                            }
                            catch(IOException ex){
                                System.out.println("Erro ao desconectar: " + ex.getMessage());
                            }
                            break;

                        default:
                            this.enviaMensagem("ERRO#COMANDO INVALIDO");

                    }
                    break;
            }
        }
    }
}