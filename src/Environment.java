import java.util.Random;

public class Environment {
    private int time; // Time in hours from start of simulation
    private Weather weather;
    private static final Random rand = new Random();
    private static final double WEATHER_CHANCE = 0.2;

    public Environment() {
        time = 0;
        weather = Weather.SUNNY;
    }

    public void incrementTime() {
        time++;
        if (rand.nextDouble() < WEATHER_CHANCE){
            weather = getNewWeather(time % 24);
        }
    }

    public boolean isNight() {
        int hour = time % 24;
        return hour < 6 || hour >= 18; // Before 6am or after 6pm
    }

    public String getEnvString() {
        return String.format("Day: %s Hour: %s Weather: %s", time / 24, time % 24, weather );
    }

    /**
     *
     * Adjusts weather based on previous weather condition's
     * @return condition of new current weather
     */
    private Weather getNewWeather(int hour){
        if (weather == Weather.STORMY && rand.nextDouble() < 0.8){
            return Weather.RAINY;
        }

        if (weather == Weather.RAINY && rand.nextDouble() < 0.3){
            return Weather.CLOUDY;
        }

        if ((hour < 6 || hour > 18) && rand.nextDouble() < 0.1) {
            return Weather.FOGGY;
        }
        // return's a random Weather if all else fails
        return Weather.values()[rand.nextInt(Weather.values().length)];
    }

    public Weather getWeather(){
        return weather;
    }
}
