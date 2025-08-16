public class SecretCodeGuesser {
  private static final char[] CHARS = {'B','A','C','X','I','U'};

  public void start() {
    // brute force secret code guessing
    SecretCode code = new SecretCode();
    int correctLength = -1; // track correct key length

    for (int length = 1; length <= 20; length++) {
      String candidate = "B".repeat(length);
      int result = code.guess(candidate);
      if (result != -2) {
        correctLength = length;
        break;
      }
    }

    if (correctLength == -1) {
      System.out.println("Failed to determine secret code length.");
      return;
    }

    char[] guess = new char[correctLength];
    for (int i = 0; i < correctLength; i++) guess[i] = 'B';

    int matched = code.guess(new String(guess));

    /* Try each possible char at the pos instead of every combination
     * Complexity:
     * Worst-case here: 20(L) * 6(chars) = 120 guesses
     */
    for (int pos = 0; pos < correctLength; pos++) {
      char keep = guess[pos]; // current char at this position
      for (char c : CHARS) {
        if (c == keep) continue;

        guess[pos] = c;
        int r = code.guess(new String(guess));

        if (r > matched) { // correct char so matched +1
          matched = r;
          keep = c;
          break; // move to next position immediately
        } else {
          guess[pos] = keep; // revert (Not likely, delete later)
        }
      }
    }

    System.out.println("I found the secret code: " + new String(guess));
  }

}

/* Worst Case:
   Best Case: BBBBBBBBBBBBBBBBBBBB (20xB) so only 21 guess for length and each char (Bug with println)
 */
