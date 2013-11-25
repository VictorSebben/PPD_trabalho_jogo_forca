package ppd_trabalhoetp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ManipuladorDeArquivos {

    File f;
    FileReader fr1;
    BufferedReader br1;

    public ManipuladorDeArquivos(File f) {

        this.f = f;
        try {
            fr1 = new FileReader(f);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
        }
        br1 = new BufferedReader(fr1);
    }

    /*
     * Este método abre um diretório para ser manipulado e retorna o objeto File
     * correspondente ao diretório aberto. Se o diretório não existe, cria um
     * diretorio com o nome. Em caso de erro, retona null @Param caminhoDir
     * Caminho completo do diretório no sistema de arquivos no formato:
     * "C:\\dir1\dir2
     */
    public File abreDir(String caminhoDir) {
        File dir = new java.io.File(caminhoDir);
        if (!dir.exists()) {
            if (dir.mkdir()) {
                System.out.println("Diretório " + dir.getAbsolutePath() + " criado");
            } else {
                System.out.println("Falha na criação do diretório");
                return null;
            }
        } else {
            System.out.println("Diretório " + dir.getAbsolutePath() + "criado com sucesso!");
        }

        return dir;
    }

    /*
     * Este método abre um arquivo que está contido no diretório passado no
     * primeiro parâmetro. O método retorna um objeto do tipo File com o arquivo
     * a ser manipulado. Se o arquivo não existe, cria-o. Em caso de erro,
     * retorna null. @param diretorio objeto File correspondente ao diretório do
     * arquivo; @param nomeArq nome do arquivo a ser aberto ou criado.
     *
     */
    public File abreArq(File diretorio, String nomeArq) {
        File arq = new File(diretorio, nomeArq);
        if (arq.exists()) {
            System.out.println("Arquivo " + nomeArq + " criado");
        } else {
            try {
                if (!arq.createNewFile()) {
                }
            } catch (IOException ex) {
                Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erro ao criar arquivo");
                return null;
            }
            System.out.println("Arquivo " + nomeArq + " aberto");
        }

        return arq;
    }

    /*
     * Retorna o conteúdo do arquivo passado por parâmetro e o retorna como
     * String. @param arq arquivo a ser lido;
     */
    public String leArq(File arq) {
        String texto = "";
        System.out.println(arq.getAbsolutePath());
        try {
            FileReader fr = new FileReader(arq);
            BufferedReader br = new BufferedReader(fr);
            String linha = "";
            try {
                while ((linha = br.readLine()) != null) {

                    texto += "#"+linha;

                }
            } catch (IOException ex) {
                Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return texto;
    }

    /*
     * Retorna o conteúdo do arquivo passado por parâmetro e o retorna como
     * String. @param arq arquivo a ser lido;
     */
    public String getLine() {
        String texto = "";
        System.out.println(f.getAbsolutePath());
        try {
            texto = br1.readLine();
            return texto;
        } catch (IOException ex) {
            System.out.println("Fim do arquivo!");
            Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
        }


        return texto;
    }

    /*
     * Escreve o conteudo do parâmetro texto no arquivo arq; Retorna false em
     * caso de erro.
     *
     * @param arq objeto File referente ao arquivo a ser escrito;
     */
    public Boolean escreveNoArq(File arq, String texto) {
        if (!arq.exists()) {
            System.out.println("Arquivo não existe!");
            return null;
        } else {
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(arq);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(texto);
                printWriter.flush();
                printWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(ManipuladorDeArquivos.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erro ao abrir/escrever arquivo!");
                return false;
            }
            return true;

        }
    }

    /*
     * Retorna uma lista dos nomes dos arquivos contidos num diretório. @param
     * diretorio objeto File refente ao diretório a ser listado;
     */
    public String[] listaArquivos(File diretorio) {
        return diretorio.list();
    }

    public static void main(String args[]) {
//        ManipuladorDeArquivos man = new ManipuladorDeArquivos();
//        File dir = man.abreDir("C:\\teste");
//        File arq = man.abreArq(dir, "arq5.txt");
//        man.escreveNoArq(arq, "texto \n testedfsd");
//        System.out.println( man.leArq(arq) );
//        String [] arqs = man.listaArquivos(dir);
//        for(int i = 0; i<arqs.length; i++)
//        {
//            System.out.println(arqs[i]);
//        }

        GeradorDePalavras gerador = new GeradorDePalavras("C:\\Users\\victor\\Documents\\NetBeansProjects\\PPD_TrabalhoEtp2\\src\\ppd_trabalhoetp2", "palavras.txt");
        System.out.println("" + gerador.getProximaPalavra() + "" + gerador.getDicaAtual());
        System.out.println("" + gerador.getProximaPalavra() + "" + gerador.getDicaAtual());
        System.out.println("" + gerador.getProximaPalavra() + "" + gerador.getDicaAtual());

        ArrayList<String> lista = gerador.getTodasPalavras();
        System.out.println("Lista:");
        for (String s : lista) {
            System.out.println(s);
        }


    }
}