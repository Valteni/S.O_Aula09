import java.util.Random;

public class Pagina {
    
    int n;
    int i;
    int d;
    int r;
    int m;
    int t;

    public Pagina(int indice, Random random) {
        this.n = indice;
        this.i = indice + 1;
        this.d = random.nextInt(50) + 1;
        this.r = 0;
        this.m = 0;
        this.t = random.nextInt(9900) + 100;
    }

    public Pagina(Pagina outra) {
        this.n = outra.n;
        this.i = outra.i;
        this.d = outra.d;
        this.r = outra.r;
        this.m = outra.m;
        this.t = outra.t;
    }

    @Override
    public String toString() {
        return String.format(
            "| N: %2d | I: %3d | D: %2d | R: %d | M: %d | T: %4d |",
            n, i, d, r, m, t
        );
    }
}