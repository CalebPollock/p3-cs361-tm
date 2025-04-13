
package tm;

import java.util.*;
import java.io.*;

class PTMSimulator {
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

      final int max_p = 4;
      final int debug = 0;

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

            dir = ((act&0x1) == 1);

            state = (act >>> 5);

            if (prev_dir == dir && prev_state == state && prev_val == nev) {
               if (dir) {
                  pos -= 1;
                  if (curr.l == null) {
                     curr.l = new TMPack(0);
                     leftmost = curr.l;
                  }
                  curr.l.r = curr.r;
                  curr.r.l = curr.l;
                  curr.r.reps += 1;
                  curr = curr.l;
               }
               else {
                  pos += 1;
                  if (curr.r == null) {
                     curr.r = new TMPack(0);
                  }
                  curr.r.l = curr.l;
                  curr.l.r = curr.r;
                  curr.l.reps += 1;
                  curr = curr.r;
               }
            }
            else {
               prev_dir = dir;
               prev_state = state;
               prev_val = nev;

               if (dir) {
                  pos -= 1;
                  if (curr.l == null) {
                     curr.l = new TMPack(0);
                     curr.l.r = curr;
                     leftmost = curr.l;
                  }
                  curr = curr.l;
               }
               else {
                  pos += 1;
                  if (curr.r == null) {
                     curr.r = new TMPack(0);
                     curr.r.l = curr;
                  }
                  curr = curr.r;
               }
            }
         }
         else {

            if (curr.pattern.length > 1) {
               enter_state = state;
               enter_dir = dir;
               int internal_pos = (enter_dir ? curr.pattern.length - 1 : 0);

               int[] workspace = new int[curr.pattern.length];
               System.arraycopy(curr.pattern,0,workspace,0,curr.pattern.length);

               //System.out.println("workspacing");
               while (internal_pos >= 0 && internal_pos < curr.pattern.length) {


                  val = workspace[internal_pos];
                  act = states[state][val];

                  int nev = ((act>>1)&0xf);
                  workspace[internal_pos] = nev;

                  dir = ((act&0x1) == 1);

                  state = (act >>> 5);

                  if (dir) 
                     internal_pos -= 1;
                  else 
                     internal_pos += 1;
                  
                  /*
                  System.out.print("wrksp: [");
                  for (int l = 0; l < workspace.length; ++l) {
                     System.out.printf("%d",workspace[l]);
                  }
                  System.out.println("]");
                  */
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
                  }
                  else {
                     pos += curr.pattern.length * curr.reps;
                     if (curr.r == null)
                        curr.r = new TMPack(0);
                     curr.r.l = curr;
                     curr = curr.r;
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
            else {

               enter_state = state;
               enter_dir = dir;

               val = curr.pattern[0];
               act = states[state][val];

               int nev = ((act>>1)&0xf);

               dir = ((act&0x1) == 1);

               state = (act >>> 5);

               if (state == enter_state && dir == enter_dir) {
                  curr.pattern[0] = nev;

                  if (prev_dir == dir && prev_state == state && prev_val == nev) {
                     if (dir) {
                        pos -= curr.reps;
                        if (curr.l == null) {
                           curr.l = new TMPack(0);
                           leftmost = curr.l;
                        }
                        curr.l.r = curr.r;
                        curr.r.l = curr.l;
                        curr.r.reps += curr.reps;
                        curr = curr.l;
                     }
                     else {
                        pos += curr.reps;
                        if (curr.r == null) {
                           curr.r = new TMPack(0);
                        }
                        curr.r.l = curr.l;
                        curr.l.r = curr.r;
                        curr.l.reps += curr.reps;
                        curr = curr.r;
                     }
                  }
                  else {
                     prev_dir = dir;
                     prev_state = state;
                     prev_val = nev;

                     if (dir) {
                        pos -= curr.reps;
                        if (curr.l == null) {
                           curr.l = new TMPack(0);
                           curr.l.r = curr;
                           leftmost = curr.l;
                        }
                        curr = curr.l;
                     }
                     else {
                        pos += curr.reps;
                        if (curr.r == null) {
                           curr.r = new TMPack(0);
                           curr.r.l = curr;
                        }
                        curr = curr.r;
                     }
                  }

               }
               else {

                  curr.reps -= 1;

                  TMPack crumb = new TMPack(nev);
                  if (enter_dir) {
                     crumb.l = curr;
                     crumb.r = curr.r;
                     curr.r.l = crumb;
                     curr.r = crumb;
                  }
                  else {
                     crumb.r = curr;
                     crumb.l = curr.l;
                     curr.l.r = crumb;
                     curr.l = crumb;
                  }

                  if (dir) {
                     pos -= 1;
                     if (crumb.l == null) {
                        crumb.l = new TMPack(0);
                        crumb.l.r = crumb;
                        leftmost = crumb.l;
                     }
                     curr = crumb.l;
                  }
                  else {
                     pos += 1;
                     if (crumb.r == null) {
                        crumb.r = new TMPack(0);
                        crumb.r.l = crumb;
                     }
                     curr = crumb.r;
                  }
               }

            }

         }

         if (state == state_count-1)
            break;

         int dist = Math.abs(folder[state].p_pos - pos);
         if (dist < max_p && dist > 1 && curr.reps == 1) {
            /*
            System.out.println("Possible pattern detected");
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
         */

            int arr[] = new int[dist];
            int lp;
            TMPack i = curr; int bp = pos;
            if (folder[state].p_pos < pos) {
               lp = dist - 1;
               while (lp >= 0) {
                  i = i.l;
                  bp -= i.reps * i.pattern.length;
                  if (bp < folder[state].p_pos)
                     break;
                  for (int j = 0; j < i.reps; ++j) {
                     for (int k = i.pattern.length-1; k >= 0; --k) {
                        arr[lp--] = i.pattern[k];
                     }
                  }
               }

               if (lp != -1)
                  dist = Integer.MAX_VALUE;
            }
            else {
               lp = 0;
               while (lp < dist) {
                  i = i.r;
                  bp += i.reps * i.pattern.length;
                  if (bp > folder[state].p_pos)
                     break;
                  for (int j = 0; j < i.reps; ++j) {
                     for (int k = 0; k < i.pattern.length; ++k) {
                        arr[lp++] = i.pattern[k];
                     }
                  }
               }

               if (lp != dist)
                  dist = Integer.MAX_VALUE;
            }

            /*
            System.out.print("subarray: [");
            for (int j = 0; j < arr.length; ++j) {
               System.out.printf("%d",arr[j]);
            }
            System.out.println("]");

            if (folder[state].A != null) {
               System.out.print("pattern : [");
               for (int j = 0; j < folder[state].A.length; ++j) {
                  System.out.printf("%d",folder[state].A[j]);
               }
               System.out.println("]");
            }

            System.out.printf("%d %d\n",Math.abs(folder[state].p_pos-folder[state].pos),dist);
            System.out.printf("%d %d\n",dist,(folder[state].A != null ?
                     folder[state].A.length:0));
                     */

            if (Math.abs(folder[state].p_pos - folder[state].pos) == dist &&
                dist == folder[state].A.length &&
                Arrays.equals(arr,0,arr.length,folder[state].A,0,arr.length)) {

               //System.out.printf("would match %d %s\n",arr.length,dir?"R":"L");

               TMPack pat = new TMPack();
               pat.pattern = arr;
               pat.reps = 2;
               if (folder[state].p_pos - pos < 0) {

                  if (folder[state].start.l == null) {
                     folder[state].start.l = new TMPack(0);
                     leftmost = folder[state].start.l;
                  }
                  pat.l = folder[state].start.l;
                  folder[state].start.l.r = pat;
                  pat.r = curr;
                  curr.l = pat;

               }
               else {

                  if (folder[state].start.r == null)
                     folder[state].start.r = new TMPack(0);
                  pat.r = folder[state].start.r;
                  folder[state].start.r.l = pat;
                  pat.l = curr;
                  curr.r = pat;

               }


               /*
         System.out.printf("current state: %d current pos: %d\n",state,pos);
         System.out.printf("current array:\n");
         for (TMPack j = leftmost; j != null; j = j.r) {
            if (j == curr)
               System.out.print("\u001B[31m");
            else if (j == curr.l)
               System.out.print("\u001B[32m");
            else if (j == curr.r)
               System.out.print("\u001B[34m");
            if (j.reps > 1) {
               System.out.print("[");
                  for (int k = 0; k < j.pattern.length; ++k) {
                     System.out.printf("%d",j.pattern[k]);
                  }
                  System.out.printf("(%d)",j.reps);
               System.out.print("]");
            }
            else {
               for (int k = 0; k < j.pattern.length; ++k) {
                  System.out.printf("%d",j.pattern[k]);
               }
            }
            System.out.print("\u001B[0m");
         }
         System.out.print("\n");
         */

            }
            else if (dist < max_p) {

               folder[state].pos = folder[state].p_pos;
               folder[state].A = arr;
               folder[state].start = i;

            }

         }

         folder[state].p_pos = pos;

         if (debug == 1)
            wait_e.nextLine();

         if (curr.l != null && curr.r != null &&
             (curr.l.r != curr || curr.r.l != curr))
            return;
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
}
