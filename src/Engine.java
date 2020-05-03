import java.awt.*;
import java.util.*;
import java.util.logging.Logger;

public class Engine {
    private int worldUpdateDelay = 10;
    private boolean started = false;
    private boolean stopped = true;
    private double timeScale = 1d;
    private Thread updateThread = null;

    private Logger logger = Logger.getLogger(Engine.class.getName());

    private Map<String, IWorldObject> worldObjectsMap = new HashMap<>();

    public void addWorldObject(String id, IWorldObject object) {
        synchronized (worldObjectsMap) {
            worldObjectsMap.put(id, object);
        }
    }

    public void removeWorldObject(String id) {
        synchronized (worldObjectsMap) {
            worldObjectsMap.remove(id);
        }
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
        if (started){
            logger.info("engine already started");
            return;
        }
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long lastUpdate = System.currentTimeMillis();
                    while (true) {
                        Thread.sleep(worldUpdateDelay);
                        long currentTime = System.currentTimeMillis();
                        int time = (int) ((currentTime - lastUpdate) * timeScale);
                        worldTick(time);
                        lastUpdate = currentTime;
                    }
                } catch (InterruptedException e) {
                    logger.info("engine update thread interrupted");
                }
            }
        });
        updateThread.start();
        started = true;
    }

    public void pause(){
        timeScale = 0d;
    }

    public void resume(){
        timeScale = 1d;
    }

    public void stop() {
        if(!started){
            logger.info("engine is not started");
            return;
        }
        updateThread.interrupt();
        synchronized (worldObjectsMap) {
            Collection<IWorldObject> worldObjects = worldObjectsMap.values();
            for (IWorldObject iWorldObject : worldObjects) {
                iWorldObject.finish();
            }
        }
    }
}