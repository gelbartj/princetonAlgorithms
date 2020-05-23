public class RabinKarp {
    private final int M; // pattern length
    private final long Q; // modulus
    private final int R; // radix
    private long RM; // R^(M-1) % Q
    private int[] matches;
    private long[] hashes;

    public RabinKarp(String[] pats, int N) {
        M = pats[0].length();
        R = 28; // 26 letters plus space and comma
        Q = longRandomPrime(M, N);
        // System.out.println(Q);

        RM = 1;
        hashes = new long[pats.length];
        matches = new int[pats.length];

        for (int i = 1; i <= M - 1; i++)
            RM = (R * RM) % Q;

        // base string hash value
        long baseHash = hash(pats[0].substring(0, M - 1), M - 1);

        for (int i = 0; i < pats.length; i++) {
            hashes[i] = (baseHash * R + (pats[i].charAt(M - 1) - 'A')) % Q;
            matches[i] = -1;
            // StdOut.println("Pat: " + pats[i]);
        }
    }


    private boolean isPrime(long num) {
        if (num % 2 == 0) return false;

        for (int i = 3; i < Math.sqrt(num); i = i + 2) {
            if (num % i == 0) return false;
        }
        return true;
    }

    private long longRandomPrime(int M, int N) {
        long startNum = (long) M * (long) N * (long) N;
        while (!isPrime(startNum)) {
            startNum--;
        }
        return startNum;
    }

    // Compute hash for M-digit key
    private long hash(String key, int M) {
        // StdOut.println("key: " + key);
        long h = 0;
        if (key.equals(" ") || key.equals("")) return h;
        for (int j = 0; j < M; j++) {
            char currChar = key.charAt(j);
            int newChar = currChar == ',' ? 26 : currChar == ' ' ? 27 : currChar - 'A';
            h = (R * h + (newChar)) % Q;
        }
        return h;
    }

    public int[] search(String txt) {
        int N = txt.length();
        long txtHash = hash(txt, M);
        for (int i = 0; i < hashes.length; i++)
            if (hashes[i] == txtHash) matches[i] = 0;
        for (int i = M; i < N; i++) {
            txtHash = (txtHash + Q - RM * txt.charAt(i - M) % Q) % Q;
            txtHash = (txtHash * R + txt.charAt(i)) % Q;
            for (int j = 0; j < hashes.length; j++) {
                if (hashes[j] == txtHash) matches[j] = i - M + 1;
            }
        }
        return matches;
    }
}
