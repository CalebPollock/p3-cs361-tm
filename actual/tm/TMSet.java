
package tm;

public class TMSet {

   private class entry {
      public TMBlock k;
      public entry n;
   }

   private entry[] table;
   private final int modulo;
   private int size = 0;

   public TMSet(int size) {

      table = new entry[size];
      modulo = size;

   }

   public TMBlock get(TMBlock key) {

      entry head = table[mod(key.hash(),modulo)];

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
      e.n = table[pos];
      table[pos] = e;

   }

   static int mod(int a, int n) {
      return a < 0 ? (a%n + n)%n : a%n;
   }

   public int size() {
      return size;
   }
}
