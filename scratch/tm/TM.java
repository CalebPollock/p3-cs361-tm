
package tm;

import java.util.*;
import java.io.*;

public class TM {

   public int states[][];
   public int state_count;
   public final int end_state;
   public int alphabet;

   TM(File init) throws FileNotFoundException {

      Scanner fin = new Scanner(init);

      state_count = Integer.parseInt(fin.nextLine());
      alphabet = Integer.parseInt(fin.nextLine());

      end_state = state_count - 1;

      states = new int[state_count-1][alphabet+1];

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

   }

   void compute(TMAction args, int[] tape, TMBound bound) {

      int pos = (args.direction == 1 ? tape.length - 1 : 0);
      int state = args.state;
      int act;

      bound.max = pos;
      bound.min = pos;

      while (pos >= 0 && pos < tape.length) {

         act = states[state][tape[pos]];
         tape[pos] = ((act>>1)&0xf);
         state = (act >>> 5);
         pos += ((act&0x1) == 1 ? -1 : 1);

         if (pos < bound.min)
            bound.min = pos;
          if (pos > bound.max)
            bound.max = pos;

         if (state == end_state)
            break;
      }

      args.direction = (pos < 0 ? 1 : 0);
      args.state = state;

   }

   void react(TMState args) {

      int act = states[args.state][args.value];

      int nev = ((act>>1)&0xf);

      args.value = nev;

      args.direction = act&0x1;

      args.state = (act >>> 5);

   }

}
