import java.util.*;

public class LetterInventoryCodeGuesser {
    // ====== CONFIG ======
    private static final char[] ALPHABET = {'B','A','C','X','I','U'};
    private static final int MAX_LENGTH = 18;

    public void start() {
        SecretCode code = new SecretCode();

        int n = determineLength(code);
        if (n < 1) {
            System.out.println("Failed to determine secret code length.");
            return;
        }
        System.out.println("✔ Length determined: " + n);

        char[] secret;
        secret = solveA_InventoryAnchor(code, n);
        System.out.println("I have found the secret code. It is: " + new String(secret));
    }

    // ================== FINDING THE LENGTH ==================
    // Approach: leverage two pointers (one at the 1 and one at the maximum allowable length)
    // For each iteration, the program will generate an array with the left index, check for the correct length
    // If the correct length has not been found, generate another array with the right index, perform the check again
    // The program will stop if the correct length is found
    private int determineLength(SecretCode code) {
        //System.out.println("Determining the length...");
        for (int length = 1; length <= MAX_LENGTH / 2; length++) {
            int left = code.guess("B".repeat(length));
            if (left != -2) return length;
            int right = code.guess("B".repeat(MAX_LENGTH - length));
            if (right != -2) return MAX_LENGTH - length;
        }
        return -1;
    }

    private static String repeat(char c, int n) {
        char[] arr = new char[n];
        Arrays.fill(arr, c);
        return new String(arr);
    }

    /**
     * A) Letter-Inventory + Anchor-Flip (no guards).
     * Steps:
     *  1) Inventory: for each letter L, guess L^n → need[L] counts.
     *  2) Build a working array G; find first anchor (position with a confirmed letter).
     *  3) For each unknown index i, test candidate letters via one-call anchor flip.
     */
    private char[] solveA_InventoryAnchor(SecretCode code, int n) {

        // Step 1: inventory
        int[] need = new int[ALPHABET.length];
        for (int a = 0; a < ALPHABET.length; a++) {
            String s = String.valueOf(ALPHABET[a]).repeat(n);
            int res = code.guess(s);
            if (res < 0) throw new IllegalStateException("Inventory guess invalid");
            need[a] = res;
        }

        // Working guess G: start with most frequent letter to maximize base
        int bestIdx = 0; for (int i=1;i<need.length;i++) if (need[i] > need[bestIdx]) bestIdx = i;
        char[] G = repeat(ALPHABET[bestIdx], n).toCharArray();
        int placedOfBest = Math.min(need[bestIdx], n); // n anyway
        int base = code.guess(new String(G));

        // Place as many of that best letter as its quota allows later
        // We’ll replace extras as we confirm other letters.

        boolean[] confirmed = new boolean[n];
        int anchor = -1; char anchorLetter = 0; char anchorFlip = 0;

        // Step 2: find an anchor — simple per-position increases (no flip yet)
        outer:
        for (int i = 0; i < n; i++) {
            if (confirmed[i]) continue;
            for (int a = 0; a < ALPHABET.length; a++) {
                if (need[a] == 0) continue;
                char cand = ALPHABET[a];
                char old = G[i]; if (old == cand) continue; // already that letter
                G[i] = cand;
                int r = code.guess(new String(G));
                if (r > base) {
                    // confirmed
                    decQuota(need, cand);
                    confirmed[i] = true;
                    base = r;
                    anchor = i; anchorLetter = cand;
                    // pick a flip letter different from anchorLetter, also legal
                    for (char x : ALPHABET) if (x != anchorLetter) { anchorFlip = x; break; }
                    System.out.println("✔ Anchor at pos " + i + " = '" + cand + "'");
                    break outer;
                } else {
                    // revert
                    G[i] = old;
                }
            }
        }

        if (anchor == -1) {
            // Edge case: everything is the same letter
            // Then the inventory would show need[bestIdx] == n; we're done
            if (need[bestIdx] == n) return G; // all same letter
            // Otherwise, proceed but choose first index as anchor by testing pairs
            // Fallback: brute confirm one position via direct loop
            for (int i = 0; i < n && anchor==-1; i++) {
                for (int a = 0; a < ALPHABET.length; a++) {
                    if (need[a] == 0) continue;
                    char cand = ALPHABET[a]; char old = G[i];
                    if (old == cand) { // already same
                        decQuota(need, cand); confirmed[i]=true; base = code.guess(new String(G));
                        anchor = i; anchorLetter = cand; for (char x: ALPHABET) if (x!=anchorLetter){anchorFlip=x;break;} break;
                    } else {
                        G[i]=cand; int r=code.guess(new String(G));
                        if (r>base){decQuota(need,cand); confirmed[i]=true;base=r;anchor=i;anchorLetter=cand;for(char x:ALPHABET)if(x!=anchorLetter){anchorFlip=x;break;} break;}
                        G[i]=old;
                    }
                }
            }
        }

        // Step 3: anchor-flip tests for the rest
        for (int i = 0; i < n; i++) {
            if (confirmed[i]) continue;
            for (int a = 0; a < ALPHABET.length; a++) {
                if (need[a] == 0) continue;
                char L = ALPHABET[a];
                char oldI = G[i]; if (oldI == L) {
                    // try confirming quickly by flipping anchor
                    char oldA = G[anchor]; G[anchor] = anchorFlip; G[i] = L;
                    int r = code.guess(new String(G));
                    // expected: base-1 if wrong; base if correct
                    if (r == base) { // match
                        decQuota(need, L);
                        confirmed[i] = true;
                        G[anchor] = anchorLetter; // restore
                        base = code.guess(new String(G));
                        System.out.println("✔ Confirmed pos " + i + " = '" + L + "'");
                        break;
                    }
                    // wrong → restore
                    G[anchor] = anchorLetter; G[i] = oldI;
                    continue;
                }
                // Regular test
                char oldA = G[anchor];
                G[anchor] = anchorFlip; G[i] = L;
                int r = code.guess(new String(G));
                if (r == base) { // match
                    decQuota(need, L);
                    confirmed[i] = true;
                    G[anchor] = anchorLetter; // restore
                    base = code.guess(new String(G));
                    System.out.println("✔ Confirmed pos " + i + " = '" + L + "'");
                    break;
                }
                // wrong → restore
                G[anchor] = anchorLetter; G[i] = oldI;
            }
        }
        return G;
    }

    private void decQuota(int[] need, char letter) {
        for (int i = 0; i < ALPHABET.length; i++){
            if (ALPHABET[i] == letter) {
                if (need[i] > 0) need[i]--; return; 
            }
        }
    }
}
