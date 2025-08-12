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
    String str = "B".repeat(correctLength); // use discovered length
    while (code.guess(str) != correctLength) {
      str = next(str);
    }
    System.out.println("I found the secret code. It is " + str);
  }

  static int order(char c) {
    if (c == 'B') {
      return 0;
    } else if (c == 'A') {
      return 1;
    } else if (c == 'C') {
      return 2;
    } else if (c == 'X') {
      return 3;
    } else if (c == 'I') {
      return 4;
    } 
    return 5;
  }

  static char charOf(int order) {
    if (order == 0) {
      return 'B';
    } else if (order == 1) {
      return 'A';
    } else if (order == 2) {
      return 'C';
    } else if (order == 3) {
      return 'X';
    } else if (order == 4) {
      return 'I';
    } 
    return 'U';
  }

  // return the next value in 'BACXIU' order, that is
  // B < A < C < X < I < U
  public String next(String current) {
    char[] curr = current.toCharArray();
    for (int i = curr.length - 1; i >=0; i--) {
      if (order(curr[i]) < 5) {
        // increase this one and stop
        curr[i] = charOf(order(curr[i]) + 1);
        break;
      }
      curr[i] = 'B';
    }
    return String.valueOf(curr);
  }  
}
