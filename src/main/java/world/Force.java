package world;

import misc.MiscMath;

public class Force {

    public static final int X_VEL = 0, Y_VEL = 1, ACC = 2, MAG = 3;
    double[] properties, originals;

    public Force(double dx, double dy, double acc, double m) {
        this.originals = new double[]{dx, dy, acc*acc, m};
        this.properties = new double[]{dx, dy, acc*acc, m};
    }

    /**
     * The velocity in world coordinates per second.
     * @return A double[] of size 2.
     */
    public double[] velocity() {
        return new double[]{properties[X_VEL]*properties[MAG], 
            properties[Y_VEL]*properties[MAG]};
    }

    public void set(double value, int index) { properties[index] = value; }
    public double get(int index) { return properties[index]; }

    public void update() {
        properties[MAG] += MiscMath.getConstant(properties[ACC], 1);
        if (properties[MAG] < 0) properties[MAG] = 0;
    }
    
    public boolean stopped() { return velocity()[0] == 0 && velocity()[1] == 0; }
    
    public void reset() {
        System.arraycopy(originals, 0, properties, 0, originals.length);
    }

}
