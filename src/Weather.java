import java.util.Random;
/**
 * A class used to support the environment class in determining weather
 *
 * @author Tanjim Islam
 * @version 1.0.0
 */
public enum Weather
{
    SUNNY("Sunny"), RAINY("Rainy"), CLOUDY("Cloudy"), FOGGY("Foggy"), WINDY("Windy"), STORMY("Stormy");

    private final String name;

    Weather(final String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
