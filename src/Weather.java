import java.util.Random;
/**
 * A class used to support the environment class in determining whether 
 *
 * @Tanjim & Keiran Matthews
 * @1.0.0
 */
public class Weather
{
    private enum Condition {
        SUNNY, RAINY, CLOUDY, FOGGY, WINDY, STORMY
    }
    private Condition currentCondition;
    private Random rand;
    private static final double WEATHER_CHANCE = 0.2;
    
    
    public Weather()
    {
        rand = new Random();
        currentCondition = Condition.values()[rand.nextInt(Condition.values().length)];
    }
    
    /**
     * Calls the current condition to be changed with a 20% probability
     */
    public void updateWeather(int hour){
        if (rand.nextDouble() < 0.2){
            currentCondition = getNewCondition(hour);
        }
    }
    
    /**
     * 
     * Adjust's weather based on previous weather condition's
     * @return condition of new current weather
     */
    private Condition getNewCondition(int hour){
        if (currentCondition == Condition.STORMY && rand.nextDouble() < 0.8){
            return Condition.RAINY;
        }
        
        if (currentCondition == Condition.RAINY && rand.nextDouble() < 0.3){
            return Condition.CLOUDY;
        }
        
        if ((hour < 6 || hour > 18) && rand.nextDouble() < 0.1) {
            return Condition.FOGGY;
        }
        // return's a random condition if all else fails
        return Condition.values()[rand.nextInt(Condition.values().length)];
    }
    
    public String getCondition(){
        return currentCondition.toString();
    }
}
