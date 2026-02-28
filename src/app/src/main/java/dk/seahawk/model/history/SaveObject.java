package dk.seahawk.model.history;

public class SaveObject {
    private Object gridLocation;
    private Object muckUpLocation;

    public SaveObject(Object gridLocation, Object muckUpLocation) {
        this.gridLocation = gridLocation;
        this.muckUpLocation = muckUpLocation;
    }

    public Object getGridLocation() {
        return gridLocation;
    }

    public Object getMuckUpLocation() {
        return muckUpLocation;
    }
}

