package com.a.eye.skywalking.collector.worker.storage;

import java.util.HashMap;

/**
 * @author pengys5
 */
public abstract class Window<T> {

    private Pointer current;

    private Data<T> dataA;
    private Data<T> dataB;

    public Window() {
        dataA = new Data(new HashMap<>());
        dataB = new Data(new HashMap<>());
        current = Pointer.A;
    }

    public void switchPointer() {
        if (current.equals(Pointer.A)) {
            current = Pointer.B;
        } else {
            current = Pointer.A;
        }
    }

    protected Data<T> getCurrentAndHold() {
        if (Pointer.A.equals(current)) {
            dataA.hold();
            return dataA;
        } else {
            dataB.hold();
            return dataB;
        }
    }

    public Data<T> getLast() {
        if (Pointer.A.equals(current)) {
            return dataB;
        } else {
            return dataA;
        }
    }

    protected void clearLast() {
        if (Pointer.A.equals(current)) {
            dataB.clear();
        } else {
            dataA.clear();
        }
    }

    enum Pointer {
        A, B
    }
}
