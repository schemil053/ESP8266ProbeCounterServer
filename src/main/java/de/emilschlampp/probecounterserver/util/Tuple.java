package de.emilschlampp.probecounterserver.util;

public class Tuple<A, B> {
    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }
    public A a;
    public B b;

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}
