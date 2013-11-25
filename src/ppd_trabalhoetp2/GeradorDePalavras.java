package ppd_trabalhoetp2;

import java.io.File;
import java.util.ArrayList;


public class GeradorDePalavras {

    ManipuladorDeArquivos leitor;
    File arquivo;
    String dicaAtual;
    String palavraAtual;


    public GeradorDePalavras(String pathDoArquivo, String nomeDoArquivo)
    {
        this.leitor = new ManipuladorDeArquivos(new File( new File(pathDoArquivo) , nomeDoArquivo) );
        File f = leitor.abreDir(pathDoArquivo);
        this.arquivo = leitor.abreArq(f, nomeDoArquivo);
        this.dicaAtual = "";
        this.palavraAtual = "";
    }
    /**
     * Lê a próxima palavra do arquivo e a retorna;
     *
     * @return a próxima palavra da lista, null se nao ha mais palavras;
     */
    public String getProximaPalavra()
    {

        palavraAtual = leitor.getLine();
        dicaAtual = leitor.getLine();

        return palavraAtual;
    }

    /**
     *
     * @return lista com todas as palavras da lista;
     */
    public ArrayList<String> getTodasPalavras()
    {
        String s;
        ArrayList<String> lista = new ArrayList<String>();

//        ManipuladorDeArquivos m = new ManipuladorDeArquivos(null);

        s = leitor.leArq(arquivo);
        System.out.println("Arq: "+ s);
        String [] temp = s.split("#");
        for(int i = 0; i< temp.length; i ++)
        {
            System.out.println("Temp[]= "+temp[i] );
            if(i%2 != 0)
                lista.add( temp[i]);

        }
        return lista;


    }

    /**
     * Retorna a dica da palavra atual;
     *
     * @return Dica da palavra em uso;
     */
    public String getDicaAtual()
    {
      return dicaAtual;
    }


}
