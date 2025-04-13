
package tm;

import java.util.*;
import java.io.*;

class MTMSimulator {
   public static TMHash cache;
   public static TMHash2 original;
   public static void main(String[] args) throws FileNotFoundException {

      /* the start will be a simple version to make sure
       * that things are done correctly. Then we will
       * proceed to a more complex implimentation       */

      final int debug = 0;
      final int timing = 0;
      final int verify = 0;
      final int block_size = 256;
      final int hash_size = 11119;

      int cycles = 0;
      int hits = 0;

      long tStart;

      tStart = System.nanoTime();

      TM machine = new TM(new File(args[0]));

      if (timing == 1)
         System.out.printf("parsing took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      tStart = System.nanoTime();

      cache = new TMHash(machine.state_count,hash_size);
      original = new TMHash2(hash_size);

      if (timing == 1)
         System.out.printf("allocation took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      TMNode curr = new TMNode();

      TMNode min = curr, max = curr;
      int l_min = 255;
      int l_max = 0;

      TMBlockRaw empty = new TMBlockRaw(new int[block_size]);
      curr.b = empty;
      original.put(empty);

      TMAction act = new TMAction();
      act.state = 0;
      act.direction = 0;

      Scanner wait_e = new Scanner(System.in);

      while (true) {
         cycles += 1;

         if (debug == 1) {
            System.out.println("current array:");
            for (TMNode i = min; i != null; i = i.r) {
               if (i == curr)
                  System.out.print("\u001B[32m");
               System.out.print(i.b.toString());
               System.out.print("\u001B[0m");
            }
            System.out.println();
         }

         act.block = curr.b;

         TMAction next = cache.get(act);

         if (next == null) {
            TMAction unknown = new TMAction();
            unknown.state = act.state;
            unknown.direction = act.direction;
            act.block.eval(unknown,machine);
            cache.put(act,unknown);
            act = unknown;
         }
         else {
            act = next;
            hits += 1;
         }

         curr.b = act.block;

         if (curr == max && curr.b.max() > l_max)
            l_max = curr.b.max();
         if (curr == min && curr.b.min() < l_min)
            l_min = curr.b.min();

         if (act.state == machine.end_state)
            break;

         if (act.direction == 1) {
            if (curr.l == null) {
               curr.l = new TMNode();
               curr.l.r = curr;
               curr.l.b = empty;
               min = curr.l;
               l_min = 255;
            }
            curr = curr.l;
         }
         else {
            if (curr.r == null) {
               curr.r = new TMNode();
               curr.r.l = curr;
               curr.r.b = empty;
               max = curr.r;
               l_max = 0;
            }
            curr = curr.r;
         }

         if (debug == 1)
            wait_e.nextLine();
      }

      if (timing == 1)
         System.out.printf("simulating took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      int len = 0;
      int sum = 0;

      if (verify == 1) {
         len += block_size - l_min;
         sum += min.b.sum();
         for (TMNode i = min.r; i != max; i = i.r) {
            len += block_size;
            sum += i.b.sum();
         }
         len += l_max+1;
         sum += max.b.sum();

         System.out.printf("length: %d\n",len);
         System.out.printf("sum: %d\n",sum);
         System.out.printf("cycles: %d hits: %d savings: %f%%\n",cycles,hits,
                           ((double)hits / cycles) * 100.0);
      }

      tStart = System.nanoTime();
      StringBuilder out = new StringBuilder((original.size())*block_size);

      for (int i = l_min; i < block_size; ++i)
         out.append((char)(min.b.arr()[i]+48));
      for (TMNode i = min.r; i != max; i = i.r) {
         for (int j = 0; j < block_size; ++j)
            out.append((char)(i.b.arr()[j]+48));
      }
      for (int i = 0; i <= l_max; ++i)
         out.append((char)(max.b.arr()[i]+48));

      System.out.println(out);
      if (timing == 1)
         System.out.printf("printing took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);


   }
}
