public class Pagina {
    int n;
    int i;
    int d;
    int r;
    int m;
    int t;

    public Pagina(int n, int i, int d, int r, int m, int t) {
        this.n = n;
        this.i = i;
        this.d = d;
        this.r = r;
        this.m = m;
        this.t = t;
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

        return String.format("[N: %2d, I: %3d, D: %3d, R: %d, M: %d, T: %4d]", n, i, d, r, m, t);
    }
}