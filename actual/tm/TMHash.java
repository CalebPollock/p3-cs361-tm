
package tm;

public class TMHash {

   private class entry {
      public TMBlock k;
      public TMAction v;
      public entry n;
   }

   private entry[][][] table;
   int modulo;

   public TMHash(int state_count, int size) {

      table = new entry[state_count][2][size];
      modulo = size;

   }

   static int mod(int a, int n) {
      return a < 0 ? (a%n + n)%n : a%n;
   }

   public TMAction get(TMAction key) {

      int pos = key.block.hash()%modulo;

      entry head = table[key.state][key.direction][pos];

      while (head != null && !head.k.equals(key.block))
         head = head.n;

      if (head == null)
         return null;

      return new TMAction(head.v);
   }

   public void put(TMAction key, TMAction val) {

      int pos = (key.block.hash()%modulo);

      entry e = new entry();
      e.k = key.block;
      e.v = new TMAction(val);
      e.n = table[key.state][key.direction][pos];
      table[key.state][key.direction][pos] = e;

   }

}
