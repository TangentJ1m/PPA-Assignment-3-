// TODO: Add Weather class + param
public record Environment(int time) {
    public boolean isNight() {
        // FIXME: time string is calculated in Simulator, could it be calculated here instead?
        int hour = (time / 60) % 24;
        return hour < 6 || hour >= 18; // Before 6am or after 6pm
    }
}
