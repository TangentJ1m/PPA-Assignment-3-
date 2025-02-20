/**
 * The interface to be extended by any class wishing to participate in the simulation.
 * Represents an entity that exists in the environment and can take actions.
 *
 * @author Tanjim Islam & Keiran Matthews
 * @version 1.0.0
 */
public abstract class Actor {
    private boolean active; // Indicates if the actor is still active
    protected Location location; // Current location of the actor in the simulation

    /**
     * Constructor for a new Actor.
     * The actor is set to be active by default.
     *
     * @param location The initial location of the actor.
     */
    public Actor(Location location) {
        this.active = true;
        this.location = location;
    }

    /**
     * Defines the behavior of the actor in the simulation.
     * Must be implemented by subclasses.
     *
     * @param currentField    The current state of the field.
     * @param nextFieldState  The new state being built for the next simulation step.
     * @param env             The environment containing weather and time information.
     */
    public abstract void act(Field currentField, Field nextFieldState, Environment env);

    /**
     * Checks if the actor is still active in the simulation.
     *
     * @return true if the actor is active, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the current location of the actor.
     *
     * @return The actor's location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Updates the actor's location.
     *
     * @param location The new location to set.
     */
    protected void setLocation(Location location) {
        if (location == null) {
            System.out.println("TRIED TO SET LOCATION TO NULL!!!!!");
        }
        this.location = location;
    }

    /**
     * Sets whether the actor is active in the simulation.
     *
     * @param active The new active state of the actor.
     */
    protected void setActive(boolean active) {
        this.active = active;
    }
}
