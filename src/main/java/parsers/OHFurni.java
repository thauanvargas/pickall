package parsers;

import gearth.protocol.HPacket;

import java.nio.charset.StandardCharsets;

public class OHFurni {
//    Id            int
//    Class         string
//    X, Y          int
//    Width, Height int
//    Direction     int
//    Z             float64
//    Colors        string
//    RuntimeData   string
//    Extra         int
//    StuffData     string
    private int id;
    private String className;
    private int x;
    private int y;
    private int width;
    private int height;
    private int direction;
    private double z;
    private String colors;
    private String runtimeData;
    private int extra;
    private String stuffData;

    public OHFurni(HPacket packet) {
        this.id = Integer.parseInt(packet.readString());
        this.className = packet.readString();
        this.x = packet.readInteger();
        this.y = packet.readInteger();
        this.width = packet.readInteger();
        this.height = packet.readInteger();
        this.direction = packet.readInteger();
        this.z = Double.parseDouble(packet.readString());
        this.colors = packet.readString();
        this.runtimeData = packet.readString();
        this.extra = packet.readInteger();
        this.stuffData = packet.readString();
    }

    public static OHFurni[] parse(HPacket packet) {
        int size = packet.readInteger();

        if(size == 0){
            size = 1;
        }

        OHFurni[] entities = new OHFurni[size];

        for(int i = 0; i < entities.length; ++i) {
            entities[i] = new OHFurni(packet);
        }

        return entities;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getRuntimeData() {
        return runtimeData;
    }

    public void setRuntimeData(String runtimeData) {
        this.runtimeData = runtimeData;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    public String getStuffData() {
        return stuffData;
    }

    public void setStuffData(String stuffData) {
        this.stuffData = stuffData;
    }
}
