import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class SimuladorPaginacao {

    private static final int TAM_RAM = 10;
    private static final int TAM_SWAP = 100;
    private static final int NUM_INSTRUCOES = 1000;
    private static final int INTERVALO_ZERAR_R = 10;

    private Pagina[] matrizRam = new Pagina[TAM_RAM];
    private Pagina[] matrizSwap = new Pagina[TAM_SWAP];

    private List<Integer> lruTracker = new ArrayList<>();

    private Random random = new Random();
    private int totalPageFaults = 0;

    public SimuladorPaginacao() {
        inicializarSwap();
        inicializarRam();
    }

    public void iniciar() {
        System.out.println("========= ESTADO INICIAL DAS MEMÓRIAS =========");
        imprimirMatrizes();

        executarSimulacao();

        System.out.println("\n========= ESTADO FINAL DAS MEMÓRIAS =========");
        imprimirMatrizes();

        System.out.println("\n========= RESULTADO DA SIMULAÇÃO =========");
        System.out.println("Algoritmo: LRU (Least Recently Used)");
        System.out.println("Total de Instruções Executadas: " + NUM_INSTRUCOES);
        System.out.println("Total de Page Faults (Faltas de Página): " + totalPageFaults);
    }

    private void inicializarSwap() {
        for (int j = 0; j < TAM_SWAP; j++) {
            matrizSwap[j] = new Pagina(
                    j,
                    j + 1,
                    random.nextInt(50) + 1,
                    0,
                    0,
                    random.nextInt(9900) + 100
            );
        }
    }

    private void inicializarRam() {
        Set<Integer> indicesSorteados = new HashSet<>();
        for (int i = 0; i < TAM_RAM; i++) {
            int indiceSwap;
            do {
                indiceSwap = random.nextInt(TAM_SWAP);
            } while (indicesSorteados.contains(indiceSwap));

            indicesSorteados.add(indiceSwap);
            matrizRam[i] = new Pagina(matrizSwap[indiceSwap]);
            lruTracker.add(matrizRam[i].n);
        }
    }

    private void executarSimulacao() {
        for (int inst = 1; inst <= NUM_INSTRUCOES; inst++) {
            if (inst % INTERVALO_ZERAR_R == 0) {
                zerarBitsR();
            }

            int instrucaoRequisitada = random.nextInt(100) + 1;
            int indiceRam = findInRamByInstrucao(instrucaoRequisitada);

            if (indiceRam != -1) {
                handleHit(indiceRam);
            } else {
                handleMiss(instrucaoRequisitada);
            }
        }
    }

    private void zerarBitsR() {
        for (int i = 0; i < TAM_RAM; i++) {
            if (matrizRam[i] != null) {
                matrizRam[i].r = 0;
            }
        }
    }

    private int findInRamByInstrucao(int instrucao) {
        for (int i = 0; i < TAM_RAM; i++) {
            if (matrizRam[i] != null && matrizRam[i].i == instrucao) return i;
        }
        return -1;
    }

    private int findInRamByN(int n) {
        for (int i = 0; i < TAM_RAM; i++) {
            if (matrizRam[i] != null && matrizRam[i].n == n) return i;
        }
        return -1;
    }

    private int findInSwapByInstrucao(int instrucao) {
        return instrucao - 1;
    }

    private int findInSwapByN(int n) {
        return n;
    }

    private void handleHit(int indiceRam) {
        Pagina pagina = matrizRam[indiceRam];
        pagina.r = 1;
        if (random.nextDouble() < 0.5) {
            pagina.d += 1;
            pagina.m = 1;
        }
        lruTracker.remove(Integer.valueOf(pagina.n));
        lruTracker.add(pagina.n);
    }

    private void handleMiss(int instrucaoRequisitada) {
        totalPageFaults++;

        int nDaVitima = lruTracker.remove(0);
        int indiceRamVitima = findInRamByN(nDaVitima);
        Pagina paginaVitima = matrizRam[indiceRamVitima];

        if (paginaVitima.m == 1) {
            writeBack(paginaVitima);
        }

        int indiceSwapNova = findInSwapByInstrucao(instrucaoRequisitada);
        matrizRam[indiceRamVitima] = new Pagina(matrizSwap[indiceSwapNova]);
        lruTracker.add(matrizRam[indiceRamVitima].n);
    }

    private void writeBack(Pagina pagina) {
        int indiceSwap = findInSwapByN(pagina.n);
        matrizSwap[indiceSwap] = new Pagina(pagina);
        matrizSwap[indiceSwap].m = 0;
    }

    public void imprimirMatrizes() {
        System.out.println("\n--- MATRIZ RAM (10x6) ---");
        for (int i = 0; i < TAM_RAM; i++) {
            System.out.println(matrizRam[i]);
        }

        System.out.println("\n--- MATRIZ SWAP (100x6) ---");
        for (int i = 0; i < TAM_SWAP; i++) {
            System.out.println(matrizSwap[i]);
        }
    }
}