package net.gausman.ftl.test;

public class Crew2 {
    public int a, b, c, d, e;

    public Crew2(int a, int b, int c, int d, int e) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    @Override
    public String toString() {
        return String.format("Crew(a=%d, b=%d, c=%d, d=%d, e=%d)", a, b, c, d, e);
    }
}

