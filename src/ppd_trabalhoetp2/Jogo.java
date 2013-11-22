package ppd_trabalhoetp2;

import java.util.ArrayList;

/**
 *
 * @author victor
 */
public class Jogo {
    private ArrayList<ControladorJogador> ctrlJogadores;

    public Jogo(ArrayList<ControladorJogador> arr){
        this.ctrlJogadores = arr;
    }

    public void broadcastMsg(String str){
        for(ControladorJogador cj : this.ctrlJogadores){
            cj.enviaMensagem(str);
        }
    }
}
