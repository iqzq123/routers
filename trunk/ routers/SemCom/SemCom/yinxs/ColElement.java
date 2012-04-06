package math_yinxs;

import javax.vecmath.GVector;

public class ColElement {
	public int j;
	public GVector xj;
	
    public ColElement() {
    	
    }
	public ColElement(int j, int len) {
		this.j = j;
		this.xj = new GVector(len);
	}
	
	public ColElement(int j, double[] x) {
		this.j = j;
		this.xj = new GVector(x);
	}
}

