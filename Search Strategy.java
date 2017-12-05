import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class homework {
    static int[] dx = {0, 0, -1, 1};
    static int[] dy = {-1, 1, 0, 0};

    int n, p;
    double time;
    char[][] b;

    int mDepth;

    Move[] gmove;

    class Move {
        Move(int r_, int c_, int count_) {
            r = r_;
            c = c_;
            count = count_;
        }

        public int r;
        public int c;
        public int count;

        public String toString() {
            return "(" + r + ", " + c + "): " + count;
        }
    }

    int flood(char[][] b, boolean[][] v, int r, int c, char ch, boolean erase) {
        if (v[r][c] || b[r][c] != ch)
            return 0;
        if (erase) b[r][c] = '*';
        v[r][c] = true;
        int res = 1;
        for (int i = 0; i < 4; ++i) {
            int nr = r + dx[i];
            int nc = c + dy[i];
            if (nr < 0 || nr >= n || nc < 0 || nc >= n) continue;
            res += flood(b, v, nr, nc, ch, erase);
        }
        return res;
    }

    ArrayList<Move> process(char[][] b) {
        ArrayList<Move> res = new ArrayList<>();
        boolean[][] v = new boolean[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                char ch = b[i][j];
                if (ch == '*' || v[i][j]) continue;
                int count = flood(b, v, i, j, ch, false);
                if (count > 0) {
                    res.add(new Move(i, j, count));
                }
            }
        }
        res.sort((Move o1, Move o2) -> o2.count - o1.count);
        return res;
    }

    int dfs(char[][] b, int depth, int alpha, int beta, int sum, boolean who) {
        ArrayList<Move> moves = process(b);
        //System.out.println("depth = " + depth + " moves.size() = " + moves.size());
        if (moves.size() == 0 || depth >= mDepth) {
            int c = who ? 1 : 0;
            for (int i = 0; i < moves.size(); ++i) {
                Move move = moves.get(i);
                sum += c * move.count * move.count * 0.8;
                c ^= 1;
            }
            return sum;
        }
//        if (depth == 0)
//        System.out.println("depth = " + depth + " move.count = " + moves.get(0).count);
        char[][] nb = new char[n][n];
        if (who) {
            int v = 0, cc = 0;
            for (int i = 0; i < moves.size() && cc < 10; ++i) {
                Move move = moves.get(i);
                remove(b, nb, move);
                int nsum = sum + move.count * move.count;
                if (move.count == 1) cc ++;
                int nv = dfs(nb, depth + 1, alpha, beta, nsum, false);
                if (v < nv) {
                    gmove[depth] = move;
                    v = nv;
                }
                alpha = Math.max(v, alpha);
                //System.out.println("alpha = " + alpha + " beta = " + beta);
                if (beta <= alpha)
                    break;
            }
            return v;
        } else {
            int v = 20000, cc = 0;
            for (int i = 0; i < moves.size() && cc < 10; ++i) {
                Move move = moves.get(i);
                remove(b, nb, move);
                if (move.count == 1) cc ++;
                int nv = dfs(nb, depth + 1, alpha, beta, sum, true);
                if (v > nv) {
                    gmove[depth] = move;
                    v = nv;
                }
                beta = Math.min(v, beta);
                if (beta <= alpha)
                    break;
            }
            return v;
        }
    }

    void remove(char[][] b, char[][] nb, Move move) {
        boolean[][] v = new boolean[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                nb[i][j] = b[i][j];
            }
        }
        flood(nb, v, move.r, move.c, nb[move.r][move.c], true);
        for (int j = 0; j < n; ++j) {
            int k = n - 1;
            for (int i = n - 1; i >= 0; --i) {
                if (nb[i][j] != '*') {
                    nb[k --][j] = nb[i][j];
                }
            }
            for (; k >= 0; --k) {
                nb[k][j] = '*';
            }
        }
    }

    void work() {
        ArrayList<Move> moves = process(b);
        mDepth = (int)(Math.log(5000000) / Math.log(Math.pow(moves.size(), 0.6)));
        // System.out.println(mDepth);
        gmove = new Move[40];
        char[][] nb = new char[n][n];
        dfs(b, 0, 0, 20000, 0, true);
        remove(b, nb, gmove[0]);
        b = nb;
    }

    void read() {
        try {
            InputReader reader = new InputReader(new FileInputStream("input.txt"));
            n = reader.nextInt();
            p = reader.nextInt();
            time = reader.nextDouble();
            b = new char[n][n];
            for (int i = 0; i < n; ++i) {
                b[i] = reader.next().toCharArray();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(n);
//        System.out.println(p);
//        System.out.println(time);
//        for (int i = 0; i < n; ++i) {
//            System.out.println(b[i]);
//        }
    }

    void write() {
        Move move = gmove[0];
//        System.out.println((char)('A' + move.c) + "" + move.r);
//        for (int i = 0; i < n; ++i) {
//            System.out.println(b[i]);
//        }
        try {
            FileWriter writer = new FileWriter("output.txt");
            writer.write((char)('A' + move.c) + "" + (1 + move.r));
            writer.write(System.getProperty("line.separator"));
            for (int i = 0; i < n; ++i) {
                writer.write(b[i]);
                writer.write(System.getProperty("line.separator"));
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    void run() {
//        generate();
        read();
        work();
        write();
    }

    void generate() {
        int n = 26, p = 4;
        try {
            FileWriter writer = new FileWriter("input.txt");
            writer.write(n + "");
            writer.write(System.getProperty("line.separator"));
            writer.write(p + "");
            writer.write(System.getProperty("line.separator"));
            writer.write(0 + "");
            writer.write(System.getProperty("line.separator"));
            Random random = new Random();
            for (int i = 0; i < n; ++i) {
                char[] b = new char[n];
                for (int j = 0; j < n; ++j) {
                    b[j] = (char)('1' + random.nextInt(p));
                }
                writer.write(b);
                writer.write(System.getProperty("line.separator"));
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        (new homework()).run();
    }

    class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public double nextDouble() {
            return Double.parseDouble(next());
        }
    }
}
