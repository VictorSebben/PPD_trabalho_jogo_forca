package ppd_trabalhoetp2;

import java.util.ArrayList;

/**
 *
 * @author victor
 */
public class Jogo {
    private ArrayList<ControladorJogador> ctrlJogadores;
    ControladorJogador jogadorAtual;
    ArrayList<Character> tentativas;
    String dicaAtual;
    String palavraAtual;
    String palavraParaEnviar;


    public Jogo(){
        this.ctrlJogadores = new ArrayList<>();
    }

    public void addCtrlJogador(ControladorJogador cj){
        this.ctrlJogadores.add(cj);
        this.broadcastMsg(cj.getNome() + " entrou no jogo.");

        if(ctrlJogadores.size() == 1)
            iniciarJogo();

        // avisar o jogador qual a palavra atual.
        cj.enviaMensagem("Palavra: " + palavraAtual);
    }

    public void iniciarJogo(){
        GeradorDePalavras gerador = new GeradorDePalavras("C:\\Users\\victor\\Documents\\NetBeansProjects\\PPD_TrabalhoEtp2\\src\\ppd_trabalhoetp2", "palavras.txt");
        this.palavraAtual = gerador.getProximaPalavra();
        this.dicaAtual = gerador.getDicaAtual();
        this.tentativas = new ArrayList<>();

        this.definirJogadorAtual();
        // avisar jogador que é sua vez
        jogadorAtual.avisarVez();
        this.atualizaTempoDeEspera();
    }

    public void atualizaTempoDeEspera(){
        for(ControladorJogador cj : ctrlJogadores){
            if(!cj.equals(this.jogadorAtual))
                cj.setContEspera(cj.getContEspera() + 1);
        }
    }

    public void definirJogadorAtual(){
        ControladorJogador jogadorMaiorEspera;

        if(ctrlJogadores.isEmpty()){
            System.out.println("Sem jogadores!");
            return;
        }

        jogadorMaiorEspera = ctrlJogadores.get(0);
        for(int i = 1; i < ctrlJogadores.size(); i++){
            if(ctrlJogadores.get(i).getContEspera() > jogadorMaiorEspera.getContEspera())
                jogadorMaiorEspera = ctrlJogadores.get(i);
        }

        this.jogadorAtual = jogadorMaiorEspera;
        this.jogadorAtual.setContEspera(0);
    }

    public void broadcastMsg(String str){
        for(ControladorJogador cj : this.ctrlJogadores){
            cj.enviaMensagem(cj.getSocket(), str);
        }
    }
}
