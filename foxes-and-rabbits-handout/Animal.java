import java.util.Random;
/**
 * Common elements of animals 
 *
 * @author Tanjim Islam
 * @version 7.0
 */


public abstract class Animal {
    
    public enum Gender {
    MALE, FEMALE;
    }
    private boolean alive;
    private Location location;
    protected static final double GENDER_PROBABILITY = 0.5;
    private Gender gender;

    public Animal(Location location) {
        this.alive = true;
        this.location = location;
        this.gender = assignGender();
    }

    private Gender assignGender() {
        return new Random().nextDouble() < GENDER_PROBABILITY ? Gender.MALE : Gender.FEMALE;
    }

    public Gender getGender() {
        return gender;
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
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
    
    protected boolean isMale() {
        return gender == Gender.MALE;
    }

    protected boolean isFemale() {
        return gender == Gender.FEMALE;
    }
}

