public class Coord<T> {
    public T x;
    public T y;

    public Coord(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public T getX() {
        return x;
    }

    public void setX(T x) {
        this.x = x;
    }

    public T getY() {
        return y;
    }

    public void setY(T y) {
        this.y = y;
    }

    public Coord clone(){
        return new Coord<T>(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Coord<T> coord = (Coord<T>) obj;
            return this.x.equals(coord.x) && this.y.equals(coord.y);
        }
        catch (ClassCastException e){
            return super.equals(obj);
        }
    }
}
