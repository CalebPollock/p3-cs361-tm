
package tm;

public class TMPack {

   TMPack(int val) {
      reps = 1;
      pattern = new int[1];
      pattern[0] = val;
   }

   TMPack() {
      reps = 0;
      pattern = null;
   }

   public int[] pattern;
   public int reps;

   public TMPack r = null;
   public TMPack l = null;

};
