import java.util.*;
/**
 * Common elements of animals 
 *
 * @author Tanjim Islam
 * @version 7.0
 */


public abstract class Animal extends Actor{
    protected static final double GENDER_PROBABILITY = 0.5;
    private Gender gender;
    private static final Random rand = Randomizer.getRandom();
    protected int age;

    public Animal(boolean randomAge, Location location) {
        super(location);
        this.gender = assignGender();
        age = 0;
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
        }
    }
    
    /**
     * Provides equal chance to the animal being male or female
     * @return Gender assigned to animal 
     */
    private Gender assignGender() {
        return new Random().nextDouble() < GENDER_PROBABILITY ? Gender.MALE : Gender.FEMALE;
    }

    public Gender getGender() {
        return gender;
    }

    /**
     * Indicate that the animal is no longer active.
     */
    protected void setDead()
    {
        setActive(false);
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
    
    
    protected boolean isMale() {
        return gender == Gender.MALE;
    }

    protected boolean isFemale() {
        return gender == Gender.FEMALE;
    }
    
    protected abstract int getMaxAge();
    
    /**
     * Increase the age.
     * This could result in the Zebra's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    protected boolean isMaleNearby(Field field){
        List<Location> adjacent = field.getAdjacentLocations(getLocation(),1);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location loc = it.next();
            Actor actor = field.getActorAt(loc);
            // Check this is the same type of animal
            if(actor.getClass() == this.getClass()) {
                Animal animal = (Animal) actor;
                if (animal.isActive() && animal.isMale()){
                    return true;
                }
            }
        }
        return false;
    }
}
    