import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class GerenciadorDeMemoria {

    private final Pagina[] matrizSwap;
    private final Pagina[] matrizRam;
    private final Random random;
    
    private final Queue<Integer> filaFIFO_SC;
    private int ponteiroRelogio;

    public GerenciadorDeMemoria() {
        this.matrizSwap = new Pagina[100];
        this.matrizRam = new Pagina[10];
        this.random = new Random();
        
        this.filaFIFO_SC = new LinkedList<>();
        this.ponteiroRelogio = 0;

        for (int j = 0; j < 100; j++) {
            matrizSwap[j] = new Pagina(j, random);
        }

        List<Integer> indicesSorteados = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            int indiceSwap;
            do {
                indiceSwap = random.nextInt(100);
            } while (indicesSorteados.contains(indiceSwap));
            
            indicesSorteados.add(indiceSwap);
            matrizRam[j] = new Pagina(matrizSwap[indiceSwap]);
            
            filaFIFO_SC.add(j);
        }
    }

    public void simular(String algoritmoNome) {
        long tempoSimulado = 0;

        for (int i = 1; i <= 1000; i++) {
            int instrucaoRequisitada = random.nextInt(100) + 1;
            int indiceRam = buscarPaginaNaRAM(instrucaoRequisitada);

            if (indiceRam != -1) {
                Pagina pagina = matrizRam[indiceRam];
                pagina.r = 1;

                if (random.nextInt(100) < 50) {
                    pagina.d += 1;
                    pagina.m = 1;
                }
            } else {
                int indiceParaSubstituir = encontrarPaginaParaSubstituir(algoritmoNome, tempoSimulado);
                
                salvarPaginaEmSWAP(indiceParaSubstituir);
                
                carregarPaginaDoSWAP(instrucaoRequisitada, indiceParaSubstituir);

                if (algoritmoNome.equals("FIFO-SC")) {
                    filaFIFO_SC.add(indiceParaSubstituir);
                }
            }

            if (i % 10 == 0) {
                resetarBitsR();
            }
        }
    }

    private int buscarPaginaNaRAM(int instrucao) {
        for (int i = 0; i < matrizRam.length; i++) {
            if (matrizRam[i].i == instrucao) {
                return i;
            }
        }
        return -1;
    }

    private int buscarPaginaNaSWAP(int instrucao) {
        for (int i = 0; i < matrizSwap.length; i++) {
            if (matrizSwap[i].i == instrucao) {
                return i;
            }
        }
        return -1;
    }

    private void salvarPaginaEmSWAP(int indiceRam) {
        Pagina paginaSaindo = matrizRam[indiceRam];
        if (paginaSaindo.m == 1) {
            int indiceSwap = paginaSaindo.n;
            matrizSwap[indiceSwap].d = paginaSaindo.d;
            matrizSwap[indiceSwap].m = 0;
            matrizSwap[indiceSwap].t = paginaSaindo.t;
        }
    }
    
    private void carregarPaginaDoSWAP(int instrucaoRequisitada, int indiceRam) {
        int indiceSwap = buscarPaginaNaSWAP(instrucaoRequisitada);
        matrizRam[indiceRam] = new Pagina(matrizSwap[indiceSwap]);
    }

    private void resetarBitsR() {
        for (int i = 0; i < matrizRam.length; i++) {
            matrizRam[i].r = 0;
        }
    }
    
    private int encontrarPaginaParaSubstituir(String algoritmo, long tempo) {
        switch (algoritmo) {
            case "FIFO-SC":
                return algoritmoFIFO_SC();
            case "RELÓGIO":
                return algoritmoRelogio();
            default:
                System.out.println("ERRO: Algoritmo " + algoritmo + " nao implementado. Usando RELOGIO.");
                return algoritmoRelogio();
        }
    }

    private int algoritmoFIFO_SC() {
        while (true) {
            int indiceCandidato = filaFIFO_SC.poll();
            Pagina paginaCandidata = matrizRam[indiceCandidato];

            if (paginaCandidata.r == 1) {
                paginaCandidata.r = 0;
                filaFIFO_SC.add(indiceCandidato);
            } else {
                return indiceCandidato;
            }
        }
    }

    private int algoritmoRelogio() {
        while (true) {
            Pagina paginaCandidata = matrizRam[ponteiroRelogio];
            
            if (paginaCandidata.r == 1) {
                paginaCandidata.r = 0;
                ponteiroRelogio = (ponteiroRelogio + 1) % 10;
            } else {
                int indiceVitima = ponteiroRelogio;
                ponteiroRelogio = (ponteiroRelogio + 1) % 10;
                return indiceVitima;
            }
        }
    }
    
    public void imprimirMatrizes() {
        System.out.println("\n--- ESTADO ATUAL DA MATRIZ RAM (10 Páginas) ---");
        for (int i = 0; i < matrizRam.length; i++) {
            System.out.println(matrizRam[i]);
        }
        
        System.out.println("\n--- ESTADO ATUAL DA MATRIZ SWAP (100 Páginas) ---");
        for (int i = 0; i < matrizSwap.length; i++) {
            System.out.println(matrizSwap[i]);
        }
    }
}