package Assignment2;

public class SecretCodeGuesser {
    private static final char[] CHAR_LIST = { 'B', 'A', 'C', 'X', 'I', 'U' };

    public void start() {
        SecretCode code = new SecretCode();
        int correctLength = -1;
        int[] freq = new int[CHAR_LIST.length];

        // Step 1: Find code length
        for (int length = 20; length >= 1; length--) {
            String candidate = "B".repeat(length);
            int result = code.guess(candidate);
            if (result != -2) {
                correctLength = length;
                if (result == correctLength) {
                    System.out.println("Found code: " + candidate);
                    return;
                }
                freq[0] = result; // frequency of 'B'
                break;
            }
        }
        if (correctLength == -1) {
            System.out.println("Failed to determine code length.");
            return;
        }

        // Step 2.1: Get frequencies of each character
        int totalKnown = freq[0];
        for (int i = 1; i < CHAR_LIST.length - 1; i++) {
            String candidate = String.valueOf(CHAR_LIST[i]).repeat(correctLength);
            int result = code.guess(candidate);
            if (result == correctLength) {
                System.out.println("Found code: " + candidate);
                return;
            }
            freq[i] = result;
            totalKnown += result;
        }
        // last char frequency is whatever remains
        freq[CHAR_LIST.length - 1] = correctLength - totalKnown;

        // Step 2.2: Placement phase
        char[] str = new char[correctLength];
        for (int i = 0; i < correctLength; i++) str[i] = 'B'; // initial all B
        int matchedCount = 0;

        // Try filling from most frequent char first
        Integer[] order = new Integer[CHAR_LIST.length];

        for (int i = 0; i < CHAR_LIST.length; i++){
          order[i] = i;
        }  
        java.util.Arrays.sort(order, (a, b) -> Integer.compare(freq[b], freq[a]));

        for (int pos = 0; pos < correctLength; pos++) {
            char current = str[pos];
            for (int idx : order) {
                char c = CHAR_LIST[idx];
                if (c == current || freq[idx] == 0) continue;

                // replace
                char old = str[pos];
                str[pos] = c;
                int result = code.guess(new String(str));

                if (result > matchedCount) {
                    matchedCount = result;
                    freq[idx]--; // used this char
                    break;       // lock position, go to next
                } else {
                    str[pos] = old; // revert
                }
            }
        }

        System.out.println("Found code: " + new String(str));
    }
}
