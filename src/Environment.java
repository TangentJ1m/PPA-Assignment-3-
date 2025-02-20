// Can't we add weather dynamics to the environment class?
public class Environment {
    private int time; // Time in hours from start of simulation
    private Weather weather;

    public Environment() {
        time = 0;
        weather = new Weather();
    }

    public void incrementTime() {
        time++;
        weather.updateWeather(time % 24);
    }

    public boolean isNight() {
        int hour = time % 24;
        return hour < 6 || hour >= 18; // Before 6am or after 6pm
    }

    public String getEnvString() {
        return String.format("Day: %s Hour: %s Weather: %s", time / 24, time % 24, weather.getCondition() );
    }
    
    public String getWeatherCondition(){
        return weather.getCondition();
    }
}
