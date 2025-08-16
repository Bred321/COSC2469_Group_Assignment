public class SecretCodeGuesser {
  public void start() {
      char[] letters = {'B', 'A', 'C', 'X', 'I', 'U'};
      int[] freq = new int[letters.length];
      SecretCode code = new SecretCode();
      int correctLength = -1; 

      // 1. Find correct length
      for (int length = 20; length > 0 ; length--) {
          String candidate = "B".repeat(length);
          int result = code.guess(candidate);
          freq[0] = result;
        
          if (result != -2) {
              correctLength = length;
              if (result == length) { // Solved in length check
                  System.out.println("I found the secret code. It is " + candidate);
                  return;
              }
              break;
          }
      }

      if (correctLength == -1) {
          System.out.println("Failed to determine secret code length.");
          return;
      }

      // 2. Frequency check
      freq[letters.length-1] = correctLength - freq[0];
      for (int i = 1; i < letters.length - 1; i++) {
          String guess = String.valueOf(letters[i]).repeat(correctLength);
          freq[i] = code.guess(guess);
          freq[letters.length-1] -= freq[i];
          if (freq[i] == correctLength) { // Found full match
              System.out.println("I found the secret code. It is " + guess);
              return;
          }
          
      }
      

      // Find letter with max frequency
      int maxIndex = 0;
      for (int i = 1; i < freq.length; i++) {
          if (freq[i] > freq[maxIndex]) {
              maxIndex = i;
          }
      }

      // 3. Start with the most frequent letter
      char[] guessArray = new char[correctLength];
      for (int i = 0; i < correctLength; i++) {
          guessArray[i] = letters[maxIndex];
      }
      int currentScore = freq[maxIndex];
  
      // 4. Replace only positions not matching yet
      for (int pos = 0; pos < correctLength; pos++) {
          if (currentScore == correctLength) break; // Done early
          for (char letter : letters) {
              
              if (letter == letters[maxIndex]) continue; // Skip baseline letter

              if(freq[indexOf(letters,letter)] != 0){
                  char old = guessArray[pos];
                  guessArray[pos] = letter;
                  int newScore = code.guess(new String(guessArray));
                  if (newScore > currentScore) {
                      freq[indexOf(letters,letter)] = freq[indexOf(letters,letter)] - 1;
                      currentScore = newScore;
                      break;
                  } else {
                      guessArray[pos] = old;
                  }
              }
              else{
                  continue;
              }
          }
      }

      // 5. Output result
      System.out.println("I found the secret code. It is " + new String(guessArray));

  }
  // Support method to get index in array
  public static int indexOf(char[] arr, char target) {
      for (int i = 0; i < arr.length; i++) {
          if (arr[i] == target) {
              return i; // return the index
          }
      }
      return -1;
  }
}
