
package tm;

import java.util.*;
import java.io.*;

class XTMSimulator {
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

      final int buf_len = 100;
      final int debug = 0;
      final int p_lim = 8;

      TMPack curr = new TMPack(0);

      TMFolder[] folder = new TMFolder[state_count-1];
      for (int i = 0; i < state_count-1; ++i) {
         folder[i] = new TMFolder();
      }

      int state = 0;
      int pos = 0;

      int  val;
      int act = 0;
      boolean dir = false;

      int enter_state;
      boolean enter_dir;

      int prev_val, prev_state;
      boolean prev_dir;
      prev_val = prev_state = -1;
      prev_dir = true;

      int buf[] = new int[buf_len];
      int buf_step = 0;

      int min, max;

      final int max_p = 6;

      TMPack leftmost = curr;
      
      Scanner wait_e = new Scanner(System.in);

      tStart = System.nanoTime();
      while (true) {

         if (debug == 1) {
            System.out.printf("current state: %d current pos: %d\n",state,pos);
            System.out.printf("current array:\n");
            for (TMPack i = leftmost; i != null; i = i.r) {
               if (i == curr)
                  System.out.print("\u001B[31m");
               else if (i == curr.l)
                  System.out.print("\u001B[32m");
               else if (i == curr.r)
                  System.out.print("\u001B[34m");
               if (i.reps > 1) {
                  System.out.print("[");
                     for (int j = 0; j < i.pattern.length; ++j) {
                        System.out.printf("%d",i.pattern[j]);
                     }
                     System.out.printf("(%d)",i.reps);
                  System.out.print("]");
               }
               else {
                  for (int j = 0; j < i.pattern.length; ++j) {
                     System.out.printf("%d",i.pattern[j]);
                  }
               }
               System.out.print("\u001B[0m");
            }
            System.out.print("\n");
         }

         if (curr.pattern.length == 1 && curr.reps == 1) {
            val = curr.pattern[0];
            act = states[state][val];

            int nev = ((act>>1)&0xf);
            curr.pattern[0] = nev;
            buf[mod(pos,buf_len)] = nev;
            ++buf_step;

            dir = ((act&0x1) == 1);

            state = (act >>> 5);

            if (state == state_count-1)
               break;

            if (dir) {
               pos -= 1;
               if (curr.l == null) {
                  curr.l = new TMPack(0);
                  curr.l.r = curr;
                  leftmost = curr.l;
                  min = pos;
               }
               curr = curr.l;
            }
            else {
               pos += 1;
               if (curr.r == null) {
                  curr.r = new TMPack(0);
                  curr.r.l = curr;
                  max = pos;
               }
               curr = curr.r;
            }




         }
         else {

            enter_state = state;
            enter_dir = dir;
            int internal_pos = (enter_dir ? curr.pattern.length - 1 : 0);

            int[] workspace = new int[curr.pattern.length];
            System.arraycopy(curr.pattern,0,workspace,0,curr.pattern.length);

            while (internal_pos >= 0 && internal_pos < curr.pattern.length) {

               val = workspace[internal_pos];
               act = states[state][val];

               int nev = ((act>>1)&0xf);
               workspace[internal_pos] = nev;

               dir = ((act&0x1) == 1);

               state = (act >>> 5);

               internal_pos += dir ? -1 : 1;
            }

            if (enter_state == state && enter_dir == dir) {

               System.arraycopy(workspace,0,curr.pattern,0,curr.pattern.length);

               if (dir) {
                  pos -= curr.pattern.length * curr.reps;
                  if (curr.l == null) {
                     curr.l = new TMPack(0);
                     leftmost = curr.l;
                  }
                  curr.l.r = curr;
                  curr = curr.l;

                  if (curr.r != null && curr.r.r != null && Arrays.equals(curr.r.pattern,curr.r.r.pattern)) {
                     if (debug == 1) {
                        System.out.printf("i am folding RR %d\n",curr.r.reps+curr.r.r.reps);
                     }
                     curr.r.r.reps += curr.r.reps;
                     curr.r.r.l = curr;
                     curr.r = curr.r.r;
                  }
               }
               else {
                  pos += curr.pattern.length * curr.reps;
                  if (curr.r == null)
                     curr.r = new TMPack(0);
                  curr.r.l = curr;
                  curr = curr.r;

                  if (curr.l != null && curr.l.l != null && Arrays.equals(curr.l.pattern,curr.l.l.pattern)) {
                     if (debug == 1) {
                        System.out.printf("i am folding LL %d\n",curr.l.reps+curr.l.l.reps);
                     }
                     curr.l.l.reps += curr.l.reps;
                     curr.l.l.r = curr;
                     curr.l = curr.l.l;
                  }
               }

            }
            else {

               TMPack start;
               TMPack expanded = start = new TMPack(workspace[0]);
               for (int i = 1; i < curr.pattern.length; ++i) {
                  expanded.r = new TMPack(workspace[i]);
                  expanded.r.l = expanded;
                  expanded = expanded.r;
               }

               curr.reps -= 1;

               if (enter_dir) {
                  if (curr.reps > 0) {
                     start.l = curr;
                     expanded.r = curr.r;
                     curr.r.l = expanded;
                     curr.r = start;
                  }
                  else {
                     if (curr.l == null) {
                        curr.l = new TMPack(0);
                        leftmost = curr.l;
                     }
                     start.l = curr.l;
                     expanded.r = curr.r;
                     curr.r.l = expanded;
                     curr.l.r = start;
                  }
               }
               else {
                  if (curr.reps > 0) {
                     expanded.r = curr;
                     start.l = curr.l;
                     curr.l.r = start;
                     curr.l = expanded;
                  }
                  else {
                     if (curr.r == null)
                        curr.r = new TMPack(0);
                     expanded.r = curr.r;
                     start.l = curr.l;
                     curr.l.r = start;
                     curr.r.l = expanded;
                  }
               }

               if (dir) {
                  pos -= (enter_dir ? curr.pattern.length : 1);
                  if (start.l == null) {
                     start.l = new TMPack(0);
                     start.l.r = start;
                     leftmost = start.l;
                  }
                  curr = start.l;
               }
               else {
                  pos += (enter_dir ? 1 : curr.pattern.length);
                  if (expanded.r == null) {
                     expanded.r = new TMPack(0);
                     expanded.r.l = expanded;
                  }
                  curr = expanded.r;
               }

            }

         }

         if (debug == 1)
            wait_e.nextLine();
      }
      System.out.printf("simulating took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      int len = 0;
      int sum = 0;

      for (TMPack i = leftmost; i != null; i = i.r) {
         len += i.reps * i.pattern.length;
         int interm = 0;
         for (int j = 0; j < i.pattern.length; ++j) {
            interm += i.pattern[j];
         }
         sum += interm * i.reps;
      }

      System.out.printf("length: %d\n",len);
      System.out.printf("sum: %d\n",sum);

   }

   static int mod(int a, int n) {
      return a < 0 ? (a%n + n)%n : a%n;
   }

}
