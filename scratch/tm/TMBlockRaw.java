
package tm;

public class TMBlockRaw extends TMBlock {
    

   private final TMBound bound;
   private final int[] arr;
   private final int hash_val;

   TMBlockRaw(int[] arr) {
      super();

      this.arr = arr;
      hash_val = compute_hash();
      this.bound = new TMBound();
   }

   TMBlockRaw(int[] arr, TMBound bound) {
      super();

      this.arr = arr;
      this.bound = bound;
      hash_val = compute_hash();
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

      return res;
   }

   public void eval(TMAction args, TM machine) {

      int[] workspace = arr.clone();

      int enter_dir = (args.direction);

      TMBound new_bound = new TMBound();
      machine.compute(args,workspace,new_bound);

      TMBlockRaw nev = new TMBlockRaw(workspace,new_bound);

      TMBlock og = MTMSimulator.original.get(nev);

      if (og == null) {
         MTMSimulator.original.put(nev);
         args.block = nev;
      }
      else {
         og.set_bound(new_bound);
         args.block = og;
      }

   }

   public final int[] arr() {
      return arr;
   }

   public final TMBlock[] van() {
      return null;
   }

   public final int type() {
      return 0;
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
