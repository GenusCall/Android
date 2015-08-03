package avivbenyair.interfacetest;

/**
 * Created by and-dev on 03/08/15.
 */
public class car {

    private SpeedListener speedListener;

    public car(SpeedListener speedListener) {
        this.speedListener = speedListener;
    }


    public void speeding() {

        int s = 150;
        speedListener.onspeeding(s);

    }


}
