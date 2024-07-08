package entities;

public class Furni {
    private int id;
    private String className;
    private String type;

    public Furni(int id, String className, String type) {
        this.id = id;
        this.className = className;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
