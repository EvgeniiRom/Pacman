import java.awt.*;
import java.util.*;
import java.util.logging.Logger;

public class Engine {
    private int worldUpdateDelay = 10;
    private boolean started = false;
    private boolean stopped = true;
    private Logger logger = Logger.getLogger(Engine.class.getName());

    private Map<String, IWorldObject> worldObjectsMap = new HashMap<>();


    public void addWorldObject(String id, IWorldObject object){
        worldObjectsMap.put(id, object);
    }

    private synchronized void worldTick(long time) {
        Collection<IWorldObject> worldObjects = worldObjectsMap.values();
        for (IWorldObject iWorldObject : worldObjects) {
            iWorldObject.update(time);
        }
    }

    public void startWorld() {
        if (started || !stopped) {
            logger.info("engine already started");
            return;
        }
        Collection<IWorldObject> worldObjects = worldObjectsMap.values();
        for (IWorldObject iWorldObject : worldObjects) {
            iWorldObject.start();
        }
        started = true;
        stopped = false;
        logger.info("engine started");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long lastUpdate = System.currentTimeMillis();
                    while (started) {
                        Thread.sleep(worldUpdateDelay);
                        long currentTime = System.currentTimeMillis();
                        worldTick(currentTime - lastUpdate);
                        lastUpdate = currentTime;
                    }
                } catch (InterruptedException e) {
                    logger.info("engine update thread was interrupted");
                }
                finally {
                    started = false;
                    stopped = true;
                }
            }
        }).start();
    }

    public void stopWorld() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                started = false;
                try {
                    while (!stopped) {
                        Thread.sleep(1);
                    }
                    Collection<IWorldObject> worldObjects = worldObjectsMap.values();
                    for (IWorldObject iWorldObject : worldObjects) {
                        iWorldObject.finish();
                    }
                    logger.info("engine stopped");
                } catch (InterruptedException e) {
                    logger.info("engine finish thread was interrupted");
                }
            }
        }).start();
    }
}