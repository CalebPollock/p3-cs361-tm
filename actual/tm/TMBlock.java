
package tm;

import java.util.Arrays;

public class TMBlock {

   public TMBound bound;
   private final int[] arr;
   private final int hash_val;
   
   TMBlock(int[] arr) {
      super();

      this.arr = arr;
      hash_val = compute_hash();
      this.bound = new TMBound();
   }

   TMBlock(int[] arr, TMBound bound) {
      super();

      this.arr = arr;
      this.bound = bound;
      hash_val = compute_hash();
   }

   public boolean equals(TMBlock other) {
      return Arrays.equals(arr(),other.arr());
   }


   public final int hash() {
      return hash_val;
   }

   private final int compute_hash() {

      int res = 0x811c9dc5;

      for (int i = 0; i < arr.length; ++i) {
         res ^= arr[i];
         res *= 0x01000193;
      }

      return Math.abs(res);
   }

   public void eval(TMAction args, TM machine) {

      int[] workspace = arr.clone();

      int enter_dir = (args.direction);

      TMBound new_bound = new TMBound();
      machine.compute(args,workspace,new_bound);

      TMBlock nev = new TMBlock(workspace,new_bound);

      /*
      TMBlock og = TMSimulator.original.get(nev);
      */

      //if (og == null) {
         //TMSimulator.original.put(nev);
         args.block = nev;
         /*
      }
      else {
         og.set_bound(new_bound);
         args.block = og;
      }
      */

   }

   public final int[] arr() {
      return arr;
   }

   public String toString() {
      String res = "";
      for (int i = 0; i < arr.length; ++i) {
         res += ((char)(arr[i]+48));
      }
      return res;
   }

   public int sum() {
      int s = 0;
      for (int i = 0; i < arr.length; ++i)
         s += arr[i];
      return s;
   }

   public int min() {
      return bound.min;
   }

   public int max() {
      return bound.max;
   }

   public void set_bound(TMBound bound) {
      this.bound.max = bound.max;
      this.bound.min = bound.min;
   }


}
