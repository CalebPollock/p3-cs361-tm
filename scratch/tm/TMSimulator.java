
package tm;

import java.util.*;
import java.io.*;

class TMSimulator {
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

      //System.out.printf("parsing took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      tStart = System.nanoTime();
      int[] brute = new int[100000];
      System.out.printf("allocation took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      int state = 0;
      int pos = 50000;
      int min = pos;
      int max = pos;

      int count = 10;

      byte  val;
      short act;

      Scanner wait_e = new Scanner(System.in);

      tStart = System.nanoTime();

      while (state != state_count-1) {
      /*

         System.out.printf("current state: %d, current pos: %d\n",state,pos);
         System.out.printf("current array:\n");
         for (int i = min; i <= max; ++i) {
            if (i == pos)
               System.out.print("\u001B[31m");
            System.out.printf("%d\u001B[0m",brute[i]);
         }
         System.out.print("\n");
         */

         val = (byte)brute[pos];
         act = (short)states[state][val];

         brute[pos] = (byte)((act>>1)&0xf);

         pos += ((act&0x1) == 1 ? -1 : 1);

         state = (act >>> 5);

         if (pos < min)
            min = pos;
         else if (pos > max)
            max = pos;

         //wait_e.nextLine();

      }

      System.out.printf("simulation took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      /*
      System.out.printf("simulating took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      System.out.printf("current state: %d, current pos: %d\n",state,pos);
      System.out.printf("current array:\n");
      for (int i = min; i <= max; ++i) {
         if (i == pos)
            System.out.print("\u001B[31m");
         System.out.printf("%d\u001B[0m",brute[i]);
      }
      System.out.print("\n");

      int sum = 0;
      for (int i = min; i <= max; ++i) {
         sum += brute[i];
      }

      System.out.printf("sum: %d, len: %d\n",sum,max-min+1);
      */

      /*
      StringBuilder out = new StringBuilder((max - min) + 1);
      for (int i = min; i <= max; ++i)
         out.append((char)(brute[i]+48));

      System.out.println(out);
      */


   }
}
