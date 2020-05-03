import java.awt.*;
import java.util.*;
import java.util.logging.Logger;

public class Engine {
    private int worldUpdateDelay = 10;
    private boolean started = false;
    private boolean stopped = true;
    private double timeScale = 1d;

    private Logger logger = Logger.getLogger(Engine.class.getName());

    private Map<String, IWorldObject> worldObjectsMap = new HashMap<>();

    public void addWorldObject(String id, IWorldObject object) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (worldObjectsMap) {
                    worldObjectsMap.put(id, object);
                }
            }
        }).start();
    }

    public void removeWorldObject(String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (worldObjectsMap) {
                    worldObjectsMap.remove(id);
                }
            }
        }).start();
    }

    private synchronized void worldTick(long time) {
        synchronized (worldObjectsMap) {
            Collection<IWorldObject> worldObjects = worldObjectsMap.values();
            for (IWorldObject iWorldObject : worldObjects) {
                iWorldObject.update(time);
            }
        }
    }


    public void restartObjects() {
        synchronized (worldObjectsMap) {
            Collection<IWorldObject> worldObjects = worldObjectsMap.values();
            for (IWorldObject iWorldObject : worldObjects) {
                iWorldObject.start();
            }
        }
    }

    public void start() {
        if (started || !stopped) {
            logger.info("engine already started");
            return;
        }
        started = true;
        stopped = false;
        restartObjects();
        logger.info("engine started");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long lastUpdate = System.currentTimeMillis();
                    while (started) {
                        Thread.sleep(worldUpdateDelay);
                        long currentTime = System.currentTimeMillis();
                        int time = (int) ((currentTime - lastUpdate) * timeScale);
                        worldTick(time);
                        lastUpdate = currentTime;
                    }
                } catch (InterruptedException e) {
                    logger.info("engine update thread was interrupted");
                } finally {
                    started = false;
                    stopped = true;
                }
            }
        }).start();
    }

    public void pause(){
        timeScale = 0d;
    }

    public void resume(){
        timeScale = 1d;
    }

    public void stop() {
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