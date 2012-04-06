package math_yinxs;

public class MatElement {
	private int i;
	private int j;
	private double val;
	
	public MatElement() {
		setI(0);
		setJ(0);
		setVal(0.0);
	}
	
	public MatElement(int i, int j, double v) {
		this.setI(i);
		this.setJ(j);
		this.setVal(v);
	}

	public int getI() {
		return i;
	}

	private void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	private void setJ(int j) {
		this.j = j;
	}

	public double getVal() {
		return val;
	}

	private void setVal(double val) {
		this.val = val;
	}
	
	public String toString() {
		return i+"\t"+j+"\t"+val;
	}
	
}
