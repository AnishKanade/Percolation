import dsa.WeightedQuickUnionUF;
import stdlib.In;
import stdlib.StdOut;

// An implementation of the Percolation API using the UF data structure.
public class UFPercolation implements Percolation {

    private boolean[][] open; // Percolation system, boolean[][] open.
    private WeightedQuickUnionUF uf; // Union-find representation of the percolation system
    private WeightedQuickUnionUF fullSites; // Percolation system


    private int n; // Percolation system size, int n.
    private int virtualTop; // virtualTop
    private int virtualBottom; // virtualBottom
    private int openSites; // openSites

    // Constructs an n x n percolation system, with all sites blocked.
    public UFPercolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Illegal n");
        }

        this.n = n;
        open = new boolean[n][n];
        uf = new WeightedQuickUnionUF((n * n) + 2);
        fullSites = new WeightedQuickUnionUF((n * n) + 1);
        virtualBottom = (n * n) + 1;
        virtualTop = 0;
        openSites = 0;
        for (int i = virtualTop; i < n; i++) {
            uf.union(encode(0, i), virtualTop);
            fullSites.union(encode(0, i), virtualTop);
            uf.union(encode((n-1), i), virtualBottom);
            for (int j = 0; j < n; j++) {
                open[i][j] = false;
            }
        }
    }

    // Opens site (i, j) if it is not already open.
    public void open(int i, int j) {
        if (i < 0 || j < 0 || i > n - 1 || j > n - 1) {
            throw new IndexOutOfBoundsException("Illegal i or j");
        }

        // If already open, stop
        if (isOpen(i, j)) {
            return;
        }

        // Open Site
        open[i][j] = true;
        openSites++;

        if ((i - 1) >= 0 && open[i - 1][j]) {
            uf.union(encode(i, j), encode(i - 1, j));
            fullSites.union(encode(i, j), encode(i - 1, j));
        }
        if ((j + 1) < n && open[i][j + 1]) {
            uf.union(encode(i, j), encode(i, j + 1));
            fullSites.union(encode(i, j), encode(i, j + 1));
        }
        if ((j - 1) >= 0 && open[i][j - 1]) {
            uf.union(encode(i, j), encode(i, j - 1));
            fullSites.union(encode(i, j), encode(i, j - 1));
        }
        if ((i + 1) < n && open[i + 1][j]) {
            uf.union(encode(i, j), encode(i + 1, j));
            fullSites.union(encode(i, j), encode(i + 1, j));
        }

    }

    // Returns true if site (i, j) is open, and false otherwise.
    public boolean isOpen(int i, int j) {
        if (i < 0 || j < 0 || i > n - 1 || j > n - 1) {
            throw new IndexOutOfBoundsException("Illegal i or j");
        }
        return open[i][j];
    }

    // Returns true if site (i, j) is full, and false otherwise.
    public boolean isFull(int i, int j) {
        if (i < 0 || j < 0 || i > n - 1 || j > n - 1) {
            throw new IndexOutOfBoundsException("Illegal i or j");
        }
        // return uf.connected( encode(i, j) , virtualTop);
        return isOpen(i, j) && uf.connected(encode(i, j), virtualTop) &&
                fullSites.connected(encode(i, j), virtualTop);
    }

    // Returns the number of open sites.
    public int numberOfOpenSites() {
        return openSites;
    }

    // Returns true if this system percolates, and false otherwise.
    public boolean percolates() {
        if (n <= 1) {
            return false;
        }
        return uf.connected(virtualBottom, virtualTop);
    }

    // Returns an integer ID (1...n) for site (i, j).
    private int encode(int i, int j) {
        return n * i + j + 1;
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        int n = in.readInt();
        UFPercolation perc = new UFPercolation(n);
        while (!in.isEmpty()) {
            int i = in.readInt();
            int j = in.readInt();
            perc.open(i, j);
        }
        StdOut.printf("%d x %d system:\n", n, n);
        StdOut.printf("  Open sites = %d\n", perc.numberOfOpenSites());
        StdOut.printf("  Percolates = %b\n", perc.percolates());
        if (args.length == 3) {
            int i = Integer.parseInt(args[1]);
            int j = Integer.parseInt(args[2]);
            StdOut.printf("  isFull(%d, %d) = %b\n", i, j, perc.isFull(i, j));
        }
    }

}