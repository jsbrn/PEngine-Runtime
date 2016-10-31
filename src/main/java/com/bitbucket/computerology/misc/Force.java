package com.bitbucket.computerology.misc;

import com.bitbucket.computerology.gui.GameScreen;

public class Force {
    
    double x_mag, y_mag, orig_x_mag, orig_y_mag, x_acc, y_acc;
    int lifespan = 0; //in frames
    
    public Force(double xm, double ym, double xa, double ya) {
        this.x_mag = xm;
        this.y_mag = ym;
        this.x_acc = xa;
        this.y_acc = ya;
        this.orig_x_mag = xm;
        this.orig_y_mag = ym;
    }
    
    public void update() {
        x_mag+=MiscMath.getConstant(x_acc*60, 1);
        y_mag+=MiscMath.getConstant(y_acc*60, 1);
        lifespan++;
    }
    
    public double getXMagnitude() {
        return x_mag;
    }
    
    public double getYMagnitude() {
        return y_mag;
    }
    
    public void setYMagnitude(int mag) {
        y_mag = mag;
    }
    
    public void setXMagnitude(int mag) {
        x_mag = mag;
    }
    
    public void reset() {
        lifespan = 0;
        x_mag = orig_x_mag;
        y_mag = orig_y_mag;
    }
    
}
