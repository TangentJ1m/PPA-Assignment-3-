import java.util.*;
/**
 * Common elements of animals 
 *
 * @author Tanjim Islam
 * @version 7.0
 */


public abstract class Animal extends Actor {
    protected static final double GENDER_PROBABILITY = 0.5;
    private static final Random rand = Randomizer.getRandom();
    protected int foodValue;
    protected int foodLevel;
    
    private Gender gender;
    // FIXME: make this private
    protected int age;
    private AnimalState state;

    /**
     * Constructs a new Animal; called by subclasses instead of directly.
     * @param randomAge whether this animal should have a random age or be age 0
     * @param location the location to create this animal
     */
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

    /**
     * Indicate that the animal is no longer active.
     */
    protected void setDead()
    {
        setActive(false);
        location = null;
    }

    /**
     * @return true if the Animal is male
     */
    protected boolean isMale() {
        return gender == Gender.MALE;
    }

    /**
     * @return true if the Animal is female
     */
    protected boolean isFemale() {
        return gender == Gender.FEMALE;
    }

    protected Gender getGender() {
        return gender;
    }

    protected AnimalState getState() {
        return state;
    }

    protected void setState(AnimalState state) {
        this.state = state;
    }

    /**
     * @return the MAX_AGE of this Animal instance
     */
    protected abstract int getMaxAge();
    
    /**
     * Increase the age.
     * This could result in the Animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Get if there is a male Animal of the same subtype in an adjacent cell
     * @param field the field of animals to check
     * @return true if there is a male Animal of the same subtype in an adjacent cell
     */
    protected boolean isMaleNearby(Field field){
        List<Location> adjacent = field.getAdjacentLocations(getLocation(),1);
        for (Location loc : adjacent) {
            Actor actor = field.getActorAt(loc);
            // Check this is the same type of animal
            if (actor != null && actor.getClass() == this.getClass()) {
                Animal animal = (Animal) actor;
                if (animal.isActive() && animal.isMale()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected Location findFood(Field field){
        for (Location loc : field.getAdjacentLocations(getLocation(), 1)){
            Actor actor = field.getActorAt(loc); 
            if(actor != null){
                if (!isPrey(actor) && actor.isActive()){
                    ((Animal) actor).setDead();
                    foodLevel +=((Animal) actor).getFoodValue();
                    System.out.println("Food found");
                    return loc; 
                }
            }
        }
        return null;
    }
    
    protected boolean isPrey(Actor actor){
        return actor != null;
    }
    
    protected int getFoodValue(){
        return foodValue;
    }
}
    