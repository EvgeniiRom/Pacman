import java.util.Comparator;

public class RenderObjectComparator implements Comparator<IRenderObject> {
    @Override
    public int compare(IRenderObject o1, IRenderObject o2) {
        return new Integer(o1.getZIndex()).compareTo(o2.getZIndex());
    }
}
