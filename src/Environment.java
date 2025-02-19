// TODO: Add Weather class + param
public class Environment {
    private int time; // Time in hours from start of simulation

    public Environment() {
        time = 0;
    }

    public void incrementTime() {
        time++;
    }

    public boolean isNight() {
        int hour = time % 24;
        return hour < 6 || hour >= 18; // Before 6am or after 6pm
    }

    public String getEnvString() {
        return String.format("Day: %s Hour: %s", time / 24, time % 24);
    }
}
