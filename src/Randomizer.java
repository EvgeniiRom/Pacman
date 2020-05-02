import sun.security.jca.GetInstance;

import java.util.Random;

public class Randomizer {
    private static Randomizer instance;
    private Random random = new Random();

    public static Randomizer getInstance() {
        if (instance==null){
            instance = new Randomizer();
        }
        return instance;
    }

    public int getInteger(){
        return random.nextInt();
    }
}
