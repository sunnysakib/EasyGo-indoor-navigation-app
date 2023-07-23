package rijve.shovon.easygo;

public class CircleCoordinates {
    private float x;
    private float y;
    private String nodeName="";

    public CircleCoordinates(float x, float y,String name) {
        this.x = x;
        this.y = y;
        this.nodeName = name;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public String getNodeName(){return nodeName;}

    public void setY(float y) {
        this.y = y;
    }
}
