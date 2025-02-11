import java.util.*;

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
        assert location != null;
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
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location, 1);
        for(Location next : adjacent) {
            Actor anActor = field.get(next);
            if(anActor == null) {
                free.add(next);
            }
            else if(!anActor.isActive()) {
                free.add(next);
            }
        }
        return free;
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
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
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
     * Print out the number of Hyenaes and Zebras in the field.
     */
    public void fieldStats()
    {
        int numHyenaes = 0, numZebras = 0;
        for(Actor anActor : field.values()) {
            if(anActor instanceof Hyena Hyena) {
                if(Hyena.isActive()) {
                    numHyenaes++;
                }
            }
            else if(anActor instanceof Zebra Zebra) {
                if(Zebra.isActive()) {
                    numZebras++;
                }
            }
        }
        System.out.println("Zebras: " + numZebras +
                           " Hyenaes: " + numHyenaes);
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least one Zebra and one Hyena in the field.
     * @return true if there is at least one Zebra and one Hyena in the field.
     */
    public boolean isViable()
    {
        boolean ZebraFound = false;
        boolean HyenaFound = false;
        Iterator<Actor> it = actors.iterator();
        while(it.hasNext() && ! (ZebraFound && HyenaFound)) {
            Actor anActor = it.next();
            if(anActor instanceof Zebra Zebra) {
                if(Zebra.isActive()) {
                    ZebraFound = true;
                }
            }
            else if(anActor instanceof Hyena Hyena) {
                if(Hyena.isActive()) {
                    HyenaFound = true;
                }
            }
        }
        return ZebraFound && HyenaFound;
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
