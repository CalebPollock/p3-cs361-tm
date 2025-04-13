
package tm;

class link {

   public link(type t) {
      this.t = t;
   }

   public link next;
   public link prev;

   public enum type {
      E, P, C;
   };

   public type t;

   int c;

   int reps;
   public link p_r;
   public link p_l;

   public boolean cmp(link other) {
      if (t != other.t)
         return false;
      switch (other.t) {
         case type.E:
            return true;
         case type.P:
            {
               link p_s = p_r;
               link o_s = other.p_r;
               while (p_s != null && o_s != null) {
                  if (p_s.cmp(o_s)) {
                     p_s = p_s->next;
                     o_s = o_s->next;
                  }
                  if (p_s == null && o_s == null)
                     return true;
                  return false;
               }
            }
         case type.C:
            return (c == other.c);
      }
   }

};
