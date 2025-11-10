public class Pagina {
    
    int n; // Número da Página (0-99)
    int i; // Instrução (1-100)
    int d; // Dado (1-50, modificado)
    int r; // Bit de Acesso (0 ou 1)
    int m; // Bit de Modificação (0 ou 1)
    int t; // Tempo de Envelhecimento (100-9999)

    /**
     * Construtor principal para criar uma nova página (normalmente para a SWAP).
     */
    public Pagina(int n, int i, int d, int r, int m, int t) {
        this.n = n;
        this.i = i;
        this.d = d;
        this.r = r;
        this.m = m;
        this.t = t;
    }

    /**
     * Construtor de cópia.
     * Usado para copiar uma página da SWAP para a RAM.
     */
    public Pagina(Pagina original) {
        this.n = original.n;
        this.i = original.i;
        this.d = original.d;
        this.r = original.r;
        this.m = original.m;
        this.t = original.t;
    }

    /**
     * Retorna uma representação em String da página para facilitar a impressão.
     */
    @Override
    public String toString() {
        return String.format(
            "N=%-2d | I=%-3d | D=%-4d | R=%d | M=%d | T=%-4d",
            n, i, d, r, m, t
        );
    }
}