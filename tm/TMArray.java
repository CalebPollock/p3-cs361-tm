
package tm;

import java.util.*;
import java.lang.reflect.Array;

public class TMArray {

   public final int chunk_size = 2048;
   public final int log2_chunk_size = 11;
   public Hashtable<Integer,byte[]> arr;
   public int min = -1;
   public int max = 1;

   TMArray() {

      arr = new Hashtable<Integer,byte[]>();

      arr.put(-1,new byte[chunk_size]);
      arr.put(0,new byte[chunk_size]);
      arr.put(1,new byte[chunk_size]);

   }

   public byte read(int idx) {

      int s_idx = idx >> log2_chunk_size;

      if (s_idx < min || s_idx > max)
         return 0;

      return arr.get(s_idx)[idx%chunk_size];
   }

   public void write(byte val, int idx) {

      int s_idx = idx >> log2_chunk_size;

      if (s_idx < min || s_idx > max)
         arr.put(s_idx,new byte[chunk_size]);

      Array.set(arr.get(s_idx),idx%chunk_size,val);

   }

}
