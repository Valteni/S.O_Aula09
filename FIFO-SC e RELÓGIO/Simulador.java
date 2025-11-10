public class Simulador {

    public static void main(String[] args) {
        
        String[] algoritmos = {
            "FIFO-SC",
            "RELÓGIO"
        };

        for (String nomeAlgoritmo : algoritmos) {
            
            System.out.println("\n##################################################################");
            System.out.println("INICIANDO SIMULACAO COM O ALGORITMO: " + nomeAlgoritmo);
            System.out.println("##################################################################");

            GerenciadorDeMemoria gerenciador = new GerenciadorDeMemoria();

            System.out.println("\n*** ESTADO INICIAL DAS MATRIZES ***");
            gerenciador.imprimirMatrizes();

            System.out.println("\n*** EXECUTANDO 1000 INSTRUCOES... ***");
            gerenciador.simular(nomeAlgoritmo);
            System.out.println("\n*** SIMULACAO DE 1000 INSTRUCOES CONCLUIDA ***");

            System.out.println("\n*** ESTADO FINAL DAS MATRIZES ***");
            gerenciador.imprimirMatrizes();
            
            System.out.println("\n##################################################################");
            System.out.println("FIM DA SIMULACAO: " + nomeAlgoritmo);
            System.out.println("##################################################################\n\n");
        }
    }
}