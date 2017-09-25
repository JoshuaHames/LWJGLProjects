package Engine;

/**
 * Created by IceEye on 2017-03-02.
 */

public class Timer {

    private double lastLoopTime;

    public void init() {
        lastLoopTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        return elapsedTime;
    }

    public double getLastLoopTime() {
        return lastLoopTime;
    }

    public int ReturnFPS(){
        double startTime = System.nanoTime() / 1000_000_000.0;
        boolean hasSecondPassed = false;
        double newTime = 0;
        int FPScount = 0;

        while(!hasSecondPassed){
            newTime = System.nanoTime() / 1000_000_000.0;

            if ((newTime - startTime) >= 1){
                hasSecondPassed = true;
            } else {
                FPScount += 1;
            }

        }

        return FPScount;
    }
}
