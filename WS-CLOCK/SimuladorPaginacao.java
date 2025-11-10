import java.util.Random;
public class SimuladorPaginacao {

    // --- Constantes da Simulação ---
    static final int RAM_SIZE = 10;
    static final int SWAP_SIZE = 100;
    static final int NUM_INSTRUCOES = 1000;
    static final int RESET_R_INTERVAL = 10;
    static final String ALGORITMO = "WS-CLOCK"; // Focado apenas neste algoritmo

    static Pagina[] matrizRAM = new Pagina[RAM_SIZE];
    static Pagina[] matrizSWAP = new Pagina[SWAP_SIZE];
    static Random random = new Random();
    static int clockPointer; 

    public static void main(String[] args) {
        System.out.println("\n==================================================");
        System.out.println("EXECUTANDO SIMULAÇÃO COM: " + ALGORITMO);
        System.out.println("==================================================");

        // 1. Inicializa (ou reseta) as matrizes
        initializeMatrices();

        // 2. Imprime o estado inicial (Obs6)
        System.out.println("\n--- ESTADO INICIAL ---");
        printMatrix("MATRIZ RAM (Inicial)", matrizRAM);
        printMatrix("MATRIZ SWAP (Inicial)", matrizSWAP);

        // 3. Roda a simulação
        int pageFaults = runSimulation();

        // 4. Imprime o estado final (Obs6)
        System.out.println("\n--- ESTADO FINAL (Após " + NUM_INSTRUCOES + " instruções) ---");
        printMatrix("MATRIZ RAM (Final)", matrizRAM);
        printMatrix("MATRIZ SWAP (Final)", matrizSWAP);

        System.out.println("\nResultado (" + ALGORITMO + "): " + pageFaults + " Faltas de Página.");
    }

    /**
     * Inicializa as matrizes SWAP e RAM conforme as regras.
     */
    public static void initializeMatrices() {
        // 1. Preenche a MATRIZ SWAP (100x6)
        for (int i = 0; i < SWAP_SIZE; i++) {
            matrizSWAP[i] = new Pagina(
                i,                          // N: 0 a 99
                i + 1,                      // I: 1 a 100
                random.nextInt(50) + 1,     // D: 1-50
                0,                          // R
                0,                          // M
                random.nextInt(9900) + 100  // T: 100-9999
            );
        }

        // 2. Preenche a MATRIZ RAM (10x6)
        for (int i = 0; i < RAM_SIZE; i++) {
            int swapIndex = random.nextInt(SWAP_SIZE); // Sorteia 0-99
            // Copia a página da SWAP para a RAM
            matrizRAM[i] = new Pagina(matrizSWAP[swapIndex]);
        }
        
        // 3. Reseta ponteiro do relógio
        clockPointer = 0;
    }

    /**
     * Imprime o conteúdo de uma matriz de páginas no console.
     */
    public static void printMatrix(String title, Pagina[] matrix) {
        System.out.println("--- " + title + " ---");
        System.out.println("----------------------------------------------------------------");
        for (int i = 0; i < matrix.length; i++) {
            System.out.printf("Linha %-3d: %s\n", i, matrix[i].toString());
        }
        System.out.println("----------------------------------------------------------------\n");
    }

    /**
     * Executa o loop principal da simulação com 1000 instruções.
     */
    public static int runSimulation() {
        int pageFaults = 0;

        for (int i = 1; i <= NUM_INSTRUCOES; i++) {
            // Sorteia a instrução requisitada (1 a 100)
            int instructionToFind = random.nextInt(100) + 1;

            // Procura a instrução na RAM
            int ramIndex = -1;
            for (int j = 0; j < RAM_SIZE; j++) {
                if (matrizRAM[j].i == instructionToFind) {
                    ramIndex = j;
                    break;
                }
            }

            if (ramIndex != -1) { // --- PAGE HIT ---
                handlePageHit(matrizRAM[ramIndex]);
            } else { // --- PAGE FAULT ---
                pageFaults++;
                handlePageFault(instructionToFind);
            }

            // Obs4: A cada 10 instruções, zera o Bit R
            if (i % RESET_R_INTERVAL == 0) {
                resetRBits();
            }
        }
        return pageFaults;
    }

    /**
     * Lógica a ser executada em caso de Page Hit.
     */
    public static void handlePageHit(Pagina page) {
        // 1. Bit R = 1
        page.r = 1;

        // 2. 50% de chance de modificar
        if (random.nextDouble() < 0.50) {
            page.d++; // 2.1 Atualiza Dado
            page.m = 1; // 2.2 Atualiza Bit M
        }
    }

    /**
     * Lógica a ser executada em caso de Page Fault.
     */
    public static void handlePageFault(int instructionToFind) {
        // 1. Encontrar a página vítima usando o WS-Clock
        int victimIndex = findVictimWSClock();
        Pagina victimPage = matrizRAM[victimIndex];

        // 2. Salvar em SWAP se modificada (Obs5)
        if (victimPage.m == 1) {
            writeToSwap(victimPage);
        }

        // 3. Buscar a nova página na SWAP
        // (A instrução I=1 está no índice N=0, I=100 está no N=99)
        int swapIndex = instructionToFind - 1;
        
        // 4. Substituir a página na RAM
        matrizRAM[victimIndex] = new Pagina(matrizSWAP[swapIndex]);
        
        // 5. Atualizar estado da nova página (acabou de ser carregada e referenciada)
        matrizRAM[victimIndex].r = 1; 
    }

    /**
     * Obs5: Escreve a página de volta na SWAP.
     */
    public static void writeToSwap(Pagina victimPage) {
        // A página N=0 fica no índice 0 da SWAP, N=1 no índice 1, etc.
        int swapIndex = victimPage.n;
        
        // Copia os dados atualizados (D, T, etc.) de volta para a SWAP
        matrizSWAP[swapIndex] = new Pagina(victimPage);
        // O Bit M é zerado na SWAP
        matrizSWAP[swapIndex].m = 0; 
    }

    /**
     * Obs4: Zera o bit R de todas as páginas na RAM.
     */
    public static void resetRBits() {
        for (Pagina page : matrizRAM) {
            page.r = 0;
        }
    }

    // --- Implementação do Algoritmo de Substituição ---

    /**
     * WS-CLOCK (Working Set Clock)
     * Implementa a lógica da Obs3.
     */
    public static int findVictimWSClock() {
        while (true) {
            Pagina page = matrizRAM[clockPointer];

            if (page.r == 1) {
                // R=1: Página em uso, dá segunda chance e avança
                page.r = 0;
            } else {
                // R=0: Página não referenciada. Verificar envelhecimento (Obs3)
                int EP = random.nextInt(9900) + 100; // Sorteia EP (100-9999)
                int T_page = page.t;

                if (EP > T_page) {
                    // Obs3: EP > T -> Página NÃO está no conjunto de trabalho.
                    // Esta é a vítima!
                    int victimIndex = clockPointer;
                    clockPointer = (clockPointer + 1) % RAM_SIZE;
                    return victimIndex;
                }
                // Se EP <= T, a página está no conjunto de trabalho.
                // Não é substituída, mas o ponteiro avança.
            }
            
            // Avança o ponteiro do relógio para a próxima verificação
            clockPointer = (clockPointer + 1) % RAM_SIZE;
        }
    }
}