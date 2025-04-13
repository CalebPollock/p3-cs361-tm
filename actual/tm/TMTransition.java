
package tm;

public class TMTransition {

   public TMTransition(int state, short direction, short new_value) {
      this.state = state;
      this.direction = direction;
      this.new_value = new_value;
   }

   public int state;
   public short direction;
   public short new_value;
}
