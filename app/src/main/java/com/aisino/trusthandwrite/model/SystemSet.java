package com.aisino.trusthandwrite.model;

/**
 * Created by HXQ on 2017/4/24.
 */

public class SystemSet {
    private boolean switch1;
    private boolean switch2;
    private boolean switch3;
    private boolean switch4;

    public boolean getSwitch1() {
        return switch1;
    }

    public void setSwitch1(boolean switch1) {
        this.switch1 = switch1;
    }

    public boolean getSwitch2() {
        return switch2;
    }

    public void setSwitch2(boolean switch2) {
        this.switch2 = switch2;
    }

    public boolean getSwitch3() {
        return switch3;
    }

    public void setSwitch3(boolean switch3) {
        this.switch3 = switch3;
    }

    public boolean getSwitch4() {
        return switch4;
    }

    public void setSwitch4(boolean switch4) {
        this.switch4 = switch4;
    }

    public SystemSet(boolean switch1, boolean switch2, boolean switch3, boolean switch4) {
        this.switch1 = switch1;
        this.switch2 = switch2;
        this.switch3 = switch3;
        this.switch4 = switch4;
    }

    public SystemSet() {
        switch1 = true;
        switch2 = false;
        switch3 = false;
        switch4 = false;
    }
}
