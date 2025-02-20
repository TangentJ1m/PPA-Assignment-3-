import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single Actor/object.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private final int depth, width;
    // actors mapped by location.
    private final Map<Location, Actor> field = new HashMap<>();
    // The actors.
    private final List<Actor> actors = new ArrayList<>();

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Place an Actor at the given location.
     * If there is already an Actor at the location it will
     * be lost.
     * @param anActor The Actor to be placed.
     * @param location Where to place the Actor.
     */
    public void placeActor(Actor anActor, Location location)
    {
        if (location == null) {
            System.err.println("Tried to place actor at null");
            return;
        }
        Object other = field.get(location);
        if(other != null) {
            actors.remove(other);
        }
        field.put(location, anActor);
        actors.add(anActor);
    }
    
    /**
     * Return the Actor at the given location, if any.
     * @param location Where in the field.
     * @return The Actor at the given location, or null if there is none.
     */
    public Actor getActorAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     *
     * @param location Get locations adjacent to this.
     * @param range How far away to look
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeOrPlant(Location location, int range)
    {
        return getAdjacentLocations(location, range).stream().filter((loc) -> {
            Actor actor = getActorAt(loc);
            return actor == null || !actor.isActive() || actor instanceof Plant;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @param range the range of area we search around location
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location, int range)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -range; roffset <= range; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -range; coffset <= range; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Gets a random location that contains an Actor that matches the given predicate within a distance of the provided
     * location, not including the given location. Returns null if no such Actor can be found.
     * @param location The Location from which to search.
     * @param range the range of area we search around location
     * @param predicate the Predicate to check each Actor with
     * @return a Location where a suitable Actor can be found, or null if none exist
     */
    public Location findActor(Location location, int range, Predicate<Actor> predicate) {
        List<Location> locations = getAdjacentLocations(location, range);
        for (Location loc : locations) {
            Actor a = getActorAt(loc);
            // Found a suitable Actor
            if (a != null && predicate.test(a)) { return loc; }
        }
        // Didn't find one
        return null;
    }

    /**
     * Print out the number of Hyenas and Zebras in the field.
     */
    public void fieldStats()
    {
        int numHyenaes = 0, numZebras = 0;
        for(Actor anActor : field.values()) {
            if(anActor instanceof Hyena hyena) {
                if(hyena.isActive()) {
                    numHyenaes++;
                }
            }
            else if(anActor instanceof Zebra zebra) {
                if(zebra.isActive()) {
                    numZebras++;
                }
            }
        }
        System.out.println("Zebras: " + numZebras +
                           " Hyenas: " + numHyenaes);
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least two different acting species
     * @return true if there is at least two different acting species
     */
    public boolean isViable()
    {
        Set<Class<?>> seen = new HashSet<>();
        for (Actor anActor : actors) {
            seen.add(anActor.getClass());
        }
        return seen.size() >= 2;
    }
    
    /**
     * Get the list of actors.
     */
    public List<Actor> getActors()
    {
        return actors;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
