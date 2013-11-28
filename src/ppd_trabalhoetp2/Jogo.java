package ppd_trabalhoetp2;

import java.util.ArrayList;
import java.util.Collections;

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
    GeradorDePalavras gerador;


    public Jogo(){
        this.ctrlJogadores = new ArrayList<>();
        this.tentativas = new ArrayList<>();
        gerador = new GeradorDePalavras("C:\\Users\\victor\\Documents\\NetBeansProjects\\PPD_TrabalhoEtp2\\src\\ppd_trabalhoetp2", "palavras.txt");
    }

    public void addCtrlJogador(ControladorJogador cj){
        this.ctrlJogadores.add(cj);
        this.broadcastMsg(cj.getNome() + " entrou no jogo.");

        if(ctrlJogadores.size() == 1)
            iniciarJogo();

        // avisar o jogador qual a palavra atual.
        cj.enviaMensagem("Palavra: " + this.palavraParaEnviar);
    }

    public void iniciarJogo(){
        this.palavraAtual = gerador.getProximaPalavra();
        this.dicaAtual = gerador.getDicaAtual();

        // gerando a palavra para enviar (a princípio, só asteriscos)
        this.palavraParaEnviar = this.palavraAtual.replaceAll("[A-Za-z]", "*");

        this.broadcastMsg("Mensagem do servidor -> NOVO JOGO INICIADO");

        this.broadcastMsg("Palavra: " + this.palavraParaEnviar);

        for(ControladorJogador cj : this.ctrlJogadores){
            if(cj.getEstado() == Estados.MORTO){
                cj.setEstado(Estados.ESPERA);
                cj.setContEspera(1);
            }
            else{
                cj.setContEspera(0);
            }
        }

        this.definirJogadorAtual();
    }

    public void atualizaTempoDeEspera(){
        for(ControladorJogador cj : ctrlJogadores){
            if(!cj.equals(this.jogadorAtual))
                cj.setContEspera(cj.getContEspera() + 1);
        }
    }

    public int getErrosMaisLascado(){
        int err = 0;

        for(ControladorJogador cj : this.ctrlJogadores){
            if((!cj.getEstado().equals(Estados.MORTO)) && (cj.getErros() > err))
                err = cj.getErros();
        }

        return err;
    }

    public void verificarTentativa(Character letra){
        Boolean flag = false;

        // colocar a letra fornecida no vetor de tentativas
        this.tentativas.add(letra);

        // verificar se a letra está na palavra e substituir os asteriscos pela letra em this.palavraParaEnviar
        for(int i = 0; i < this.palavraAtual.length(); i++){
            if(letra == this.palavraAtual.charAt(i)){
                // substituir caractere na posição adequada em this.palavraParaEnviar
                char arr[] = this.palavraParaEnviar.toCharArray();
                arr[i] = letra;
                this.palavraParaEnviar = String.valueOf(arr);

                // a letra existe na palavra
                flag = true;

                // somar pontuação do acerto da letra
                this.jogadorAtual.incrementarPontuacao();

                // verificar se o jogo terminou (se completou a palavra)
                if(this.isFimJogo())
                    this.finalizarJogo();
                else
                    this.jogadorAtual.enviaMensagem("Acertou. Jogue novamente.");
            }
        }

        this.atualizaTempoDeEspera();

        if(flag == false){
            // incrementa erros
            this.jogadorAtual.incrementarErros();
            this.jogadorAtual.enviaMensagem("Errou - não é mais sua vez.\nVocê tem "
                    + this.jogadorAtual.getErros() + " erros.");

            // troca para espera ou morto
            if(this.jogadorAtual.getErros() == 2){
                this.jogadorAtual.setEstado(Estados.MORTO);
                this.jogadorAtual.enviaMensagem("Mensagem do servidor -> você chegou a 6 erros. Perdeu.");
            }
            else
                this.jogadorAtual.setEstado(Estados.ESPERA);

            // errou - determinar novo jogador
            this.definirJogadorAtual();
        }

        // chamar o método atualizarJogadores()
        atualizarJogadores();
    }

    public boolean haSobreviventes(){
        for(ControladorJogador cj : this.ctrlJogadores){
            if(cj.getEstado() != Estados.MORTO){
                System.out.println(cj.getNome() + " está vivo.");
                return true;
            }
        }

        System.out.println("NINGUÉM MAIS ESTÁ VIVO!");
        return false;
    }

    public void atualizarJogadores(){
        // TODO atualizar conforme o protocolo UPDATE#FRASE#TENTATIVAS#DICA#JOG1,JOG2,JOG3…
        String atualizacao = "UPDATE#" + this.palavraParaEnviar + "#" +
                String.valueOf(this.tentativas) + "#" + this.dicaAtual +
                "#" + this.getRanking(); // atualiza conforme o protocolo

//        for(ControladorJogador cj : ctrlJogadores)
//            cj.update(atualizacao);
        this.broadcastMsg(atualizacao);
    }

    public void removerJogador(ControladorJogador cj){
        this.ctrlJogadores.remove(cj);
    }

    public boolean isFimJogo(){
        return this.palavraParaEnviar.indexOf("*") > -1;
    }

    public void finalizarJogo(){
        this.broadcastMsg("Mensagem do servidor -> FIM DE JOGO#" + this.jogadorAtual.getNome() + " VENCEU.");
        this.iniciarJogo();

        // zerar erros de todos
        for(ControladorJogador cj : this.ctrlJogadores){
            cj.resetErros();
        }

        // zerar vetor de tentativas
        this.tentativas.clear();
    }

    public String getRanking(){
        // ranking de acordo com o número de vezes que ele ganhou --> ctrlJogador.pontuacao
        String ranking = "";

        ControladorJogador temp;

        for(int i = 0; i < this.ctrlJogadores.size(); i++){
            for(int j = (i + 1); j < this.ctrlJogadores.size(); j++){
                if(this.ctrlJogadores.get(i).getPontuacao() < this.ctrlJogadores.get(j).getPontuacao()){
                    temp = this.ctrlJogadores.get(i);
                    this.ctrlJogadores.set(i, this.ctrlJogadores.get(j));
                    this.ctrlJogadores.set(j, temp);
                }
            }
        }

        for(int i = 0; i < ctrlJogadores.size(); i++){
            ranking += this.ctrlJogadores.get(i).getNome();
            System.out.println(ranking);
        }

        return ranking;
    }

    public void definirJogadorAtual(){
        if(!haSobreviventes()){
            this.broadcastMsg("Mensgem do servidor -> Todos estão mortos.");
            this.iniciarJogo();
            return;
        }

        ControladorJogador jogadorMaiorEspera;

        if(ctrlJogadores.isEmpty()){
            System.out.println("Sem jogadores!");
            return;
        }

        jogadorMaiorEspera = ctrlJogadores.get(0);
        for(int i = 1; i < ctrlJogadores.size(); i++){
            if((ctrlJogadores.get(i).getContEspera() > jogadorMaiorEspera.getContEspera())
                    && (ctrlJogadores.get(i).getEstado() == Estados.ESPERA))
                jogadorMaiorEspera = ctrlJogadores.get(i);
        }

        this.jogadorAtual = jogadorMaiorEspera;
        this.jogadorAtual.setContEspera(0);
        this.jogadorAtual.avisarVez();
    }

    public void broadcastMsg(String str){
        for(ControladorJogador cj : this.ctrlJogadores){
            cj.enviaMensagem(cj.getSocket(), str);
        }
    }

    public void dbMostraJogAtivos(){
        System.out.println("\n********************\n");
        for(ControladorJogador cj : this.ctrlJogadores){
            System.out.println("cj.getNome()");
        }
        System.out.println("\n********************\n");
    }
}
