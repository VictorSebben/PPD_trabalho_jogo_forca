package ppd_trabalhoetp2;

import java.util.ArrayList;

/**
 *
 * @author victor
 */
public class Jogo {
    private ArrayList<ControladorJogador> ctrlJogadores;

    public Jogo(){
        this.ctrlJogadores = new ArrayList<>();
    }

    public void addCtrlJogador(ControladorJogador cj){
        this.ctrlJogadores.add(cj);
        // this.broadcastMsg(cj.getNome() + " entrou no jogo.");
    }

    public void broadcastMsg(String str){
        for(ControladorJogador cj : this.ctrlJogadores){
            cj.enviaMensagem(cj.getSocket(), str);
        }
    }
}
