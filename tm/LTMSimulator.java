
package tm;

import java.util.*;
import java.io.*;

class LTMSimulator {
   public static void main(String[] args) throws FileNotFoundException {

      /* the start will be a simple version to make sure
       * that things are done correctly. Then we will
       * proceed to a more complex implimentation       */


      int alphabet;
      int state_count;

      long tStart = System.nanoTime();
      Scanner fin = new Scanner(new File(args[0]));

      state_count = Integer.parseInt(fin.nextLine());
      alphabet = Integer.parseInt(fin.nextLine());

      int[][] states = new int[state_count-1][alphabet+1];

      String line;
      for (int i = 0; i < state_count - 1; ++i) {

         for (int j = 0; j <= alphabet; ++j) {

            line = fin.nextLine();
            String[] segments = line.split(",");

            states[i][j] = (int)((Integer.parseInt(segments[0])<<5)|
                                 (Integer.parseInt(segments[1])<<1)|
                                 (segments[2].equals("R") ? 0 : 1));

            int val = states[i][j];
         }

      }

      System.out.printf("parsing took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);


      int state = 0;
      int act;


      LTMNode curr = new LTMNode();
      LTMNode leftmost = curr;
      curr.value = 0;


      while (true) {

         act = states[state][curr.value];

         curr.value = (byte)((act>>1)&0xf);

         if ((act&0x1) == 1) {
            if (curr.l == null) {
               curr.l = new LTMNode();
               curr.l.r = curr;
               leftmost = curr.l;
            }
            curr = curr.l;
         }
         else {
            if (curr.r == null) {
               curr.r = new LTMNode();
               curr.r.l = curr;
            }
            curr = curr.r;
         }

         state = (act >>> 5);

         if (state == state_count-1)
            break;

      }


      System.out.printf("simulating took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      int len = 0;
      int sum = 0;

      for (LTMNode i = leftmost; i != null; i = i.r) {
         len += 1;
         sum += i.value;
      }

      System.out.printf("length: %d\n",len);
      System.out.printf("sum: %d\n",sum);

   }
}
