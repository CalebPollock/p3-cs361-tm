
package tm;

import java.util.Arrays;

public abstract class TMBlock {

   public abstract int hash();

   public abstract void eval(TMAction args, TM machine);

   public abstract int[] arr();

   public abstract TMBlock[] van();

   public abstract int type();

   public boolean equals(TMBlock other) {

      if (other.type() != type())
         return false;

      if (type() == 0 && Arrays.equals(arr(),other.arr()))
         return true;
      else if (type() == 1 && Arrays.equals(van(),other.van()))
         return true;

      return false;
   }

   public abstract String toString();

   public abstract int sum();

   public abstract int min();

   public abstract int max();

   public abstract void set_bound(TMBound bound);

}
