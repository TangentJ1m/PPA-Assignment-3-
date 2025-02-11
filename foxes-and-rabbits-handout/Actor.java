
/**
 * the interface to be extended by any class wishing
 * to participate in the simulation
 *
 * @author Tanjim Islam
 * @version 1.0.0
 */
public abstract class Actor
{
    private boolean active;
    protected Location location;
    
    public Actor(Location location) {
        this.active = true;
        this.location = location;
    }
    
    /**
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    public abstract void act(Field currentField, Field nextFieldState);//Event Class needed
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isActive()
    {
        return active;
    }
    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location; 
    }
    
    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }
    
    protected void setActive(boolean active) {
        this.active = active;
    }
}
