import java.util.*;
import java.util.function.Function;

/**
 * A simple predator-prey simulator, based on a rectangular field containing 
 * Hyenas, Zebras, Giraffes, Hunters and Lions.
 * 
 * @author David J. Barnes, Michael Kölling, Tanjim Islam and Keiran Matthews
 * @version 7.1
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that each actor will be created in any given grid position.
    private static final double HYENA_CREATION_PROBABILITY = 0.02;
    private static final double ZEBRA_CREATION_PROBABILITY = 0.08;
    private static final double GIRAFFE_CREATION_PROBABILITY = 0.06;
    private static final double HUNTER_CREATION_PROBABILITY = 0.01;
    private static final double LION_CREATION_PROBABILITY = 0.02;


    // The current state of the field.
    private Field field;
    // The current environment of the simulation.
    private Environment env;
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
     * Main method to allow this class to be executed
     * @param args unused
     */
    public static void main(String[] args) {
        Simulator sim = new Simulator();
        sim.runLongSimulation();
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
            delay(50);         // adjust this to change execution speed
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each Actor.
     */
    public void simulateOneStep()
    {
        env.incrementTime();
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Actor> Actors = field.getActors();
        for (Actor anActor : Actors) { 
            anActor.act(field, nextFieldState, env);
        }
        
        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(env.getEnvString(), field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        env = new Environment();
        populate();
        view.showStatus(env.getEnvString(), field);
    }
    
    /**
    * Randomly populate the field with Actors.
    */
   private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Actor actor = null;
                if (rand.nextDouble() < HYENA_CREATION_PROBABILITY) {
                    actor = new Hyena(true, location);
                }
                else if (rand.nextDouble() < ZEBRA_CREATION_PROBABILITY) {
                    actor = new Zebra(true, location);
                }
                else if (rand.nextDouble() < GIRAFFE_CREATION_PROBABILITY) {
                    actor = new Giraffe(true, location);
                }
                else if (rand.nextDouble() < LION_CREATION_PROBABILITY) {
                    actor = new Lion(true, location);
                }
                else if (rand.nextDouble() < HUNTER_CREATION_PROBABILITY) {
                    actor = new Hyena(true, location);
                }
                if (actor != null) {
                    field.placeActor(actor, location);
                }
            }
        }
    }

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
