
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
      final int block_size = 64;
      final int hash_size = 7919;

      int cycles = 0;
      int hits = 0;

      long tStart;

      tStart = System.nanoTime();

      TM machine = new TM(new File(args[0]));

      System.out.printf("parsing took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      tStart = System.nanoTime();

      cache = new TMHash(machine.state_count,hash_size);
      original = new TMHash2(hash_size);

      System.out.printf("allocation took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      int pos = 0;

      int min = pos, max = pos;

      TMBlock[] area = new TMBlock[2];

      TMBlock empty = new TMBlockRaw(new int[block_size]);
      area[0] = empty;
      area[1] = empty;
      original.put(empty);

      TMAction act = new TMAction();
      act.state = 0;
      act.direction = 0;

      Scanner wait_e = new Scanner(System.in);

      while (true) {
         /*
         cycles += 1;

         if (debug == 1) {
            System.out.println("current array:");
            for (int i = min; i <= max; ++i) {
               if (i == pos)
                  System.out.print("\u001B[32m");
               System.out.print(area[i].toString());
               System.out.print("\u001B[0m");
            }
            System.out.println();
         }

         act.block = area[pos];

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

         area[pos] = act.block;

         if (act.state == machine.end_state)
            break;

         pos += (act.direction == 1 ? -1 : 1);
         if (area[pos] == null) {
            if (pos < min)
               min = pos;
            else
               max = pos;
            area[pos] = empty; 
         }
         */

         /*
         if (debug == 1) {
            System.out.println("current array:");
            if (pos == 0)
               System.out.print("\u001B[32m");
            System.out.print(area[0].toString());
            System.out.print("\u001B[0m");
            if (pos == 1)
               System.out.print("\u001B[32m");
            System.out.print(area[1].toString());
            System.out.print("\u001B[0m\n");
         }

         act.block = area[pos];

         TMAction next = cache.get(act);

         if (next == null) {
            TMAction unknown = new TMAction();
            unknown.state = act.state;
            unknown.direction = act.direction;
            act.block.eval(unknown,machine);
            MTMSimulator.cache.put(act,unknown);
            act = unknown;
         }
         else
            act = next;

         area[pos] = act.block;

         if (act.state == machine.end_state)
            break;

         pos += (act.direction == 1 ? -1 : 1);

         if (pos < 0) {
            TMBlock nev = new TMBlockAbstract(area[0],area[1]);
            area[1] = nev;
            area[0] = empty;
            original.put(nev);
            pos = 0;
         }
         else if (pos > 2) {
            TMBlock nev = new TMBlockAbstract(area[0],area[1]);
            area[0] = nev;
            area[1] = empty;
            original.put(nev);
            pos = 1;
         }
         */

         if (debug == 1)
            wait_e.nextLine();
      }

      System.out.printf("simulating took %fs\n",(double)(System.nanoTime() - tStart) / 1e9);

      int len = 0;
      int sum = 0;

      sum = area[0].sum() + area[1].sum();

      System.out.printf("length: %d\n",len);
      System.out.printf("sum: %d\n",sum);
      System.out.printf("cycles: %d hits: %d savings: %f%%\n",cycles,hits,
                        ((double)hits / cycles) * 100.0);

   }
}
