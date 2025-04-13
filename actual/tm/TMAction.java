
package tm;

public class TMAction {

   public int state;
   public int direction;
   public TMBlock block;

   public TMAction() {
   }

   public TMAction(TMAction other) {
      this.state = other.state;
      this.direction = other.direction;
      this.block = other.block;
   }

}
