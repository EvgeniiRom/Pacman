import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class Engine {
    private int worldUpdateDelay = 10;
    private boolean started = false;
    private double timeScale = 1d;
    private Thread updateThread = null;

    private Logger logger = Logger.getLogger(Engine.class.getName());

    private Map<String, IWorldObject> worldObjectsMap = new HashMap<>();

    public void addWorldObject(IWorldObject object) {
        synchronized (worldObjectsMap) {
            worldObjectsMap.put(object.getId(), object);
        }
    }

    public void removeWorldObject(String id) {
        synchronized (worldObjectsMap) {
            worldObjectsMap.remove(id);
        }
    }

    public void removeAllWorldObjects() {
        synchronized (worldObjectsMap) {
            worldObjectsMap.clear();
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


    public void startObjects() {
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
        timeScale = 1d;
        startObjects();
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long lastUpdate = System.currentTimeMillis();
                    while (true) {
                        Thread.sleep(worldUpdateDelay);
                        long currentTime = System.currentTimeMillis();
                        if(timeScale>0.001) {
                            int time = (int) ((currentTime - lastUpdate) * timeScale);
                            worldTick(time);
                        }
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
        started = false;
    }

    public List<IWorldObject> getWorldObjectListByClass(Class c){
        List<IWorldObject> result = new ArrayList<>();
        synchronized (worldObjectsMap) {
            Collection<IWorldObject> worldObjects = worldObjectsMap.values();
            for (IWorldObject iWorldObject : worldObjects) {
                if(c.isInstance(iWorldObject)){
                    result.add(iWorldObject);
                }
            }
        }
        return result;
    }
}