package math_yinxs;

import javax.vecmath.GVector;

public class RowElement {
	public int i;
	public GVector xi;
	
    public RowElement() {
    	
    }
	public RowElement(int i, int len) {
		this.i = i;
		this.xi = new GVector(len);
	}
	
	public RowElement(int i, double[] x) {
		this.i = i;
		this.xi = new GVector(x);
	}
}
