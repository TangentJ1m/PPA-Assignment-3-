import java.util.Random;

public class Plant extends Actor implements Edible {
    private static final double SPREAD_CHANCE = 0.01;
    private static final double SPREAD_CHANCE_RAINING = 0.06;
    private static final Random RANDOM = new Random();

    public Plant(Location location) {
        super(location);
    }

    @Override
    public void act(Field currentField, Field nextFieldState, Environment env) {
        double spreadChance = SPREAD_CHANCE;
        if (env.getWeather() == Weather.RAINY || env.getWeather() == Weather.STORMY) {
            spreadChance = SPREAD_CHANCE_RAINING;
        }
        if (RANDOM.nextDouble() < spreadChance) {
            for (Location loc : nextFieldState.getFreeOrPlant(getLocation(), 1)) {
                Plant newPlant = new Plant(loc);
                nextFieldState.placeActor(newPlant, loc);
            }
        }
        if (nextFieldState.getActorAt(getLocation()) == null) {
            nextFieldState.placeActor(this, getLocation());
        }
        else {
            setActive(false);
        }
    }

    @Override
    public void eat() {
        setActive(false);
    }

    @Override
    public int getFoodValue() {
        return 18;
    }
}
