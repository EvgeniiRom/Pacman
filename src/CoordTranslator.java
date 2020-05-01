import java.awt.*;

public class CoordTranslator {
    private double fieldPxWidth;
    private double fieldPxHeight;
    private int blockRowCount;
    private int blockColumnCount;
    private double unitByBlock;

    public CoordTranslator(double fieldPxWidth, double fieldPxHeight, int blockRowCount, int blockColumnCount, double unitByBlock) {
        this.fieldPxWidth = fieldPxWidth;
        this.fieldPxHeight = fieldPxHeight;
        this.blockRowCount = blockRowCount;
        this.blockColumnCount = blockColumnCount;
        this.unitByBlock = unitByBlock;
    }

    public int getCanvasWidthByUnit(double unit){
        return (int)(fieldPxWidth*unit/(unitByBlock*blockColumnCount));
    }

    public int getCanvasHeightByUnit(double unit){
        return (int)(fieldPxHeight*unit/(unitByBlock*blockRowCount));
    }

    public Coord<Integer> getCanvasCoordByUnit(Coord<Double> unitCoord){
        int x = getCanvasWidthByUnit(unitCoord.x);
        int y = getCanvasHeightByUnit(unitCoord.y);
        return new Coord<>(x, y);
    }

    public Coord<Double> getBlockIndexByUnit(Coord<Double> unitCoord){
        double x = unitCoord.x/unitByBlock;
        double y = unitCoord.y/unitByBlock;
        return new Coord<>(x, y);
    }
}
