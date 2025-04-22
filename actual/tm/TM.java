
package tm;

import java.util.*;
import java.io.*;
import java.nio.file.*;

public class TM {

   //public int states[][];
   public TMTransition states[][];
   public int state_count;
   public final int end_state;
   public int alphabet;
   public TMNode head = null;
   public TMNode tail = null;

   TM(Path init) throws IOException {

      List<String> fin = Files.readAllLines(init);
      ListIterator<String> lines = fin.listIterator();

      state_count = Integer.parseInt(lines.next());
      alphabet = Integer.parseInt(lines.next());

      end_state = state_count - 1;

      //states = new int[state_count-1][alphabet+1];
      states = new TMTransition[state_count-1][alphabet+1];

      String line;

      for (int i = 0; i < state_count - 1; ++i) {

         for (int j = 0; j <= alphabet; ++j) {

            line = lines.next();
            String[] segments = line.split(",");

            /*
            states[i][j] = (int)((Integer.parseInt(segments[0])<<5)|
                                 (Integer.parseInt(segments[1])<<1)|
                                 (segments[2].equals("R") ? 0 : 1));

            int val = states[i][j];
                                 */

            states[i][j] = new TMTransition(       Integer.parseInt(segments[0]),
                                            (short)(segments[2].equals("R") ? 1 : -1),
                                            (short)Integer.parseInt(segments[1]));
         }

      }

      if (lines.hasNext()) {
         String tape = lines.next();

         TMNode curr = head;

         if (tape.length() != 0) {

            int pos = 0;

            int[] arr = new int[TMSimulator.block_size];

            for (int i = 0; i < tape.length(); ++i) {
               if (pos == TMSimulator.block_size) {
                  if (curr == null) {
                     curr = new TMNode();
                     curr.b = new TMBlock(arr);
                     curr.b.bound.min = 0;
                     curr.b.bound.max = TMSimulator.block_size-1;
                     head = curr;

                     TMSimulator.original.put(curr.b);
                  }
                  else {
                     TMNode xd = new TMNode();
                     xd.b = new TMBlock(arr);
                     xd.b.bound.min = 0;
                     xd.b.bound.max = TMSimulator.block_size-1;
                     curr.r = xd;
                     xd.l = curr;
                     curr = xd;

                     TMSimulator.original.put(xd.b);
                  }
                  arr = new int[TMSimulator.block_size];
                  pos = 0;
               }

               arr[pos] = tape.charAt(i)-48;
               ++pos;
            }

            if (pos != 0) {
               if (curr == null) {
                  curr = new TMNode();
                  curr.b = new TMBlock(arr);
                  curr.b.bound.min = 0;
                  curr.b.bound.max = pos-1;
                  head = curr;

                  TMSimulator.original.put(curr.b);
               }
               else {
                  TMNode xd = new TMNode();
                  xd.b = new TMBlock(arr);
                  xd.b.bound.min = 0;
                  xd.b.bound.max = pos-1;
                  curr.r = xd;
                  xd.l = curr;
                  curr = xd;

                  TMSimulator.original.put(xd.b);
               }
            }

            tail = curr;
         }
      }

   }

   void compute(TMAction args, int[] tape, TMBound bound) {

      int pos = (args.direction == 1 ? tape.length - 1 : 0);
      int state = args.state;
      //int act;
      TMTransition act;

      bound.max = pos;
      bound.min = pos;

      while (pos >= 0 && pos < tape.length) {

         if (pos < bound.min)
            bound.min = pos;
         else if (pos > bound.max)
            bound.max = pos;

         act = states[state][tape[pos]];
         /*
         tape[pos] = ((act>>1)&0xf);
         state = (act >>> 5);
         pos += ((act&0x1) == 1 ? -1 : 1);
         */
         tape[pos] = act.new_value;
         state = act.state;
         pos += act.direction;

         if (state == end_state)
            break;
      }

      args.direction = (pos < 0 ? 1 : 0);
      args.state = state;

   }

}
