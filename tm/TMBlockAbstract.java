
package tm;

public class TMBlockAbstract extends TMBlock {

   final TMBlock[] arr;
   final int hash_val;

   TMBlockAbstract(TMBlock a, TMBlock b) {
      arr = new TMBlock[2];
      arr[0] = a;
      arr[1] = b;
      hash_val = compute_hash();
   }

   public final int hash() {
      return hash_val;
   }

   private final int compute_hash() {

      int res = 0x811c9dc5;

      int a_h = arr[0].hashCode();
      int b_h = arr[1].hashCode();

      res ^= (a_h&0xff);
      res *= 0x01000193;
      a_h >>>= 4;
      res ^= (a_h&0xff);
      res *= 0x01000193;
      a_h >>>= 4;
      res ^= (a_h&0xff);
      res *= 0x01000193;
      a_h >>>= 4;
      res ^= a_h;
      res *= 0x01000193;

      res ^= (b_h&0xff);
      res *= 0x01000193;
      b_h >>>= 4;
      res ^= (b_h&0xff);
      res *= 0x01000193;
      b_h >>>= 4;
      res ^= (b_h&0xff);
      res *= 0x01000193;
      b_h >>>= 4;
      res ^= b_h;
      res *= 0x01000193;

      return res;
   }

   public void eval(TMAction args, TM machine) {

      TMBlock[] workspace = arr.clone();

      int pos = (args.direction == 1 ? -1 : 1);

      TMAction act = args;

      while (pos >= 0 && pos < 2) {

         args.block = workspace[pos];

         TMAction next = MTMSimulator.cache.get(args);

         if (next == null) {
            TMAction unknown = new TMAction();
            unknown.state = act.state;
            unknown.direction = act.direction;
            act.block.eval(unknown,machine);
            MTMSimulator.cache.put(act,unknown);
            act = unknown;
         } 
         else {
            act = next;
         }

         workspace[pos] = act.block;

         if (act.state == machine.end_state)
            break;

         pos += (act.direction == 1 ? -1 : 1);

      }

      TMBlockAbstract nev = new TMBlockAbstract(workspace[0],workspace[1]);

      TMBlock og = MTMSimulator.original.get(nev);

      if (nev == null) {
         MTMSimulator.original.put(nev);
         args.block = nev;
      }
      else
         args.block = og;

      args.state = act.state;
      args.direction = act.direction;

   }

   public final int[] arr() {
      return null;
   }

   public final TMBlock[] van() {
      return arr;
   }

   public final int type() {
      return 1;
   }

   public String toString() {
      String res = "";
      res += arr[0].toString();
      res += arr[1].toString();
      return res;
   }

   public int sum() {
      return arr[0].sum() + arr[1].sum();
   }

}
