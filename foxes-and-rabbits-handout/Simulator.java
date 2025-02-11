import java.util.*;
import java.util.function.Function;

/**
 * A simple predator-prey simulator, based on a rectangular field containing 
 * Zebras and Hyenaes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a Hyena will be created in any given grid position.
    private static final double Hyena_CREATION_PROBABILITY = 0.02;
    // The probability that a Zebra will be created in any given position.
    private static final double Zebra_CREATION_PROBABILITY = 0.08;
    // The probability that a Zebra will be created in any given position.
    private static final double Giraffe_CREATION_PROBABILITY = 0.06;
    // The probability that a Zebra will be created in any given position.
    private static final double Hunter_CREATION_PROBABILITY = 0.01;
    // The probability that a Zebra will be created in any given position.
    private static final double Lion_CREATION_PROBABILITY = 0.02;
    

    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);

        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(1200);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            //delay(50);         // adjust this to change execution speed
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each Hyena and Zebra.
     */
    public void simulateOneStep()
    {
        step++;
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Actor> Actors = field.getActors();
        for (Actor anActor : Actors) { 
            anActor.act(field, nextFieldState);
        }
        
        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(calculateTime(step), field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(calculateTime(step), field);
    }
    
    /**
    * Randomly populate the field with Hyenaes and Zebras.
    */
   private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
    
        // Define a map of probabilities and actor creators
        Map<Double, Function<Location, Actor>> actorMap = new HashMap<>();
        actorMap.put(Hyena_CREATION_PROBABILITY, loc -> new Hyena(true, loc));
        actorMap.put(Zebra_CREATION_PROBABILITY, loc -> new Zebra(true, loc));
        actorMap.put(Giraffe_CREATION_PROBABILITY, loc -> new Giraffe(true, loc));
        actorMap.put(Lion_CREATION_PROBABILITY, loc -> new Lion(true, loc));
        actorMap.put(Hunter_CREATION_PROBABILITY, loc -> new Hunter(loc));
    
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for (Map.Entry<Double, Function<Location, Actor>> entry : actorMap.entrySet()) {
                    if (rand.nextDouble() <= entry.getKey()) {
                        field.placeActor(entry.getValue().apply(location), location);
                        break; // Ensure only one actor is placed per location
                    }
                }
            }
        }
    }
/*    
private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= Hyena_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hyena hyena = new Hyena(true, location);
                    field.placeActor(hyena, location);
                }
                else if(rand.nextDouble() <= Zebra_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(true, location);
                    field.placeActor(zebra, location);
                }
                else if(rand.nextDouble() <= Giraffe_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Giraffe giraffe = new Giraffe(true, location);
                    field.placeActor(giraffe, location);
                }
                else if(rand.nextDouble() <= Lion_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, location);
                    field.placeActor(lion, location);
                }
                else if(rand.nextDouble() <= Hunter_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hunter hunter = new Hunter(location);
                    field.placeActor(hunter, location);
                }
                // else leave the location empty.
            }
        }
    }
*/
    /**
     * Report on the number of each type of Actor in the field.
     */
    public void reportStats()
    {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    } 
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }
    
    /**
     * Calculates the time based on the given number of steps.
     * Assumes each step represents one minute.
     *
     * @param step The number of steps (minutes).
     * @return The formatted time in HH:mm format.
     */
    private String calculateTime(int step) {
        int hours = (step / 60) % 24;
        int minutes = step % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
}
