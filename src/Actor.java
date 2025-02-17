
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

    /**
     * Constructor for a new Actor, sets the new actor to be active by default
     * @param location the location to create the actor
     */
    public Actor(Location location) {
        this.active = true;
        this.location = location;
    }
    
    /**
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    public abstract void act(Field currentField, Field nextFieldState, Environment env);
    
    /**
     * @return true if the Actor is still active
     */
    public boolean isActive()
    {
        return active;
    }
    
    /**
     * @return The Actor's location
     */
    public Location getLocation()
    {
        return location; 
    }
    
    /**
     * Set the Actor's location
     * @param location The new location
     */
    protected void setLocation(Location location)
    {
        if (location == null) {
            System.out.println("TRIED TO SET LOCATION TO NULL!!!!!");
        }
        this.location = location;
    }

    /**
     * Set whether this Actor is active
     * @param active the new value for active
     */
    protected void setActive(boolean active) {
        this.active = active;
    }
}
