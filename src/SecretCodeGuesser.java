public class SecretCodeGuesser {
  public void start() {
    // brute force secret code guessing
    SecretCode code = new SecretCode();
    int correctLength = -1; // track correct key length
    int MAX_LENGTH = 18;

    // Approarch: two‑ends (optimised: check left, then right)
    for(int length = 1; length <= MAX_LENGTH / 2; length++){
      int leftLength = length;
      int rightLength = MAX_LENGTH - length;
      String leftCandidate = "B".repeat(leftLength);
      String rightCadidate = "B".repeat(rightLength);

      int leftResult = code.guess(leftCandidate);
      if(leftResult != -2){
        correctLength = leftLength;
        break;
      }

      int rightResult = code.guess(rightCadidate);
      if(rightResult != -2){
        correctLength = rightLength;
        break;
      }
    }

    if (correctLength == -1) {
      System.out.println("Failed to determine secret code length.");
      return;
    }

    // brute force key guessing
    String candidate = "B".repeat(correctLength);
    char[] charArr = candidate.toCharArray();
    int oldMatchCount = code.guess(candidate);
    int contentPos = 0;

    while (oldMatchCount < correctLength && contentPos < correctLength) {
        char testChar = charArr[contentPos];
        int attempts = 0;

        do {
            testChar = generateChar(testChar);  // cycle through B → A → C → ...
            char[] testArr = charArr.clone();
            testArr[contentPos] = testChar;

            int newMatchCount = code.guess(String.valueOf(testArr));
            if (newMatchCount > oldMatchCount) {
                // Found correct character at this position
                charArr[contentPos] = testChar;
                oldMatchCount = newMatchCount;
                contentPos++;
                break;
            }

            attempts++;
        } while (attempts < 6);  // 6 possible characters total
    }

    // Print the result
    System.out.println("I found the secret code. It is " + String.valueOf(charArr));

  }

  static char generateChar(char c) {
    if (c == 'B') {
      return 'A';
    } else if (c == 'A') {
      return 'C';
    } else if (c == 'C') {
      return 'X';
    } else if (c == 'X') {
      return 'I';
    } else if (c == 'I') {
      return 'U';
    } 
    return 'B';
  }
}