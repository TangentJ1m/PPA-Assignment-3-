
/**
 * the interface to be extended by any class wishing
 * to participate in the simulation
 *
 * @author Tanjim Islam
 * @version 1.0.0
 */
public interface Actor
{
    /**
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    void act(Field currentField, Field nextFieldState);
    
    /**
     * Is the actor still active?
     * @return true if still active, false if not
     */
    
    boolean isActive();
}
