package svd;

public class Vector {
	
	

	private double []valueArray=null;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public String toString(){
		String retS="";
		for(double d:this.valueArray){
			retS+=d+",";
		}
		return retS;
	}
	public double multWith(Vector v){
		double ret=0.0;
		for(int i=0;i<this.valueArray.length;i++){
			ret+=valueArray[i]*v.getValueArray()[i];
		}
		return ret;
	}
	public Vector add(Vector v){
		Vector retV=new Vector(this.getDemension());
		for(int i=0;i<this.getDemension();i++){
			retV.getValueArray()[i]=this.getValueArray()[i]+v.getValueArray()[i];
		}
		return retV;
	}
	public Vector sub(Vector v){
		Vector retV=new Vector(this.getDemension());
		for(int i=0;i<this.getDemension();i++){
			retV.getValueArray()[i]=this.getValueArray()[i]-v.getValueArray()[i];
		}
		return retV;
	}
	public Vector scale(double time){
		Vector retV=new Vector(this.getDemension());
		for(int i=0;i<this.getDemension();i++){
			retV.getValueArray()[i]=this.getValueArray()[i]*time;
		}
		return retV;
	}
	public int getDemension(){
		return this.valueArray.length;
	}
	public Vector(int demension) {
		// TODO Auto-generated constructor stub
		valueArray=new double[demension];
		for(int i=0;i<demension;i++){
			valueArray[i]=Math.random();
		}
	}
    public double at(int index){
    	return valueArray[index];
    }

	public void setValueArray(double [] valueArray) {
		this.valueArray = valueArray;
	}
	public double [] getValueArray() {
		return valueArray;
	}

}
