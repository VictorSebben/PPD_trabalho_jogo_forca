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

        defineProximoJogador();

        // avisar jogador que é sua vez
        jogadorAtual.avisarVez();
        this.atualizaTempoDeEspera();
    }

    public void iniciarJogo(){

    }

    public void atualizaTempoDeEspera(){
        for(ControladorJogador cj : ctrlJogadores){
            if(!cj.equals(this.jogadorAtual))
                cj.setContEspera(cj.getContEspera() + 1);
        }
    }

    public void defineProximoJogador(){
        ControladorJogador maiorEspera;

        if(ctrlJogadores.isEmpty()){
            System.out.println("Sem jogadores!");
            return;
        }

        maiorEspera = ctrlJogadores.get(0);
        for(int i = 1; i < ctrlJogadores.size(); i++){
            if(ctrlJogadores.get(i).getContEspera() > maiorEspera.getContEspera())
                maiorEspera = ctrlJogadores.get(i);
        }

        this.jogadorAtual = maiorEspera;
        this.jogadorAtual.setContEspera(0);
    }

    public void broadcastMsg(String str){
        for(ControladorJogador cj : this.ctrlJogadores){
            cj.enviaMensagem(cj.getSocket(), str);
        }
    }
}
