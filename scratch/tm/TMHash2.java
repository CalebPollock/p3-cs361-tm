
package tm;

public class TMHash2 {

   private class entry {
      public TMBlock k;
      public entry n;
   }

   private entry[][] table;
   private final int modulo;
   private int size = 0;

   public TMHash2(int size) {

      table = new entry[2][size];
      modulo = size;

   }

   public TMBlock get(TMBlock key) {

      entry head = table[key.type()][mod(key.hash(),modulo)];

      while (head != null && !head.k.equals(key))
         head = head.n;

      if (head == null)
         return null;

      return head.k;
   }

   public void put(TMBlock key) {

      ++size;

      int pos = mod(key.hash(),modulo);

      entry e = new entry();
      e.k = key;
      e.n = table[key.type()][pos];
      table[key.type()][pos] = e;

   }

   static int mod(int a, int n) {
      return a < 0 ? (a%n + n)%n : a%n;
   }

   public int size() {
      return size;
   }
}
