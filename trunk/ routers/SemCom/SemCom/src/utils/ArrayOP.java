package utils;

public class ArrayOP {
	
	static public  int getMaxId(double []array){
		int maxId = 0;
		double max=Double.MIN_VALUE;
		for(int i=0;i<array.length;i++){
			if(array[i]>max){
				maxId=i;
			}
		}
		return maxId;
	}
	
	static public double getSim(Object[]objects,Object[]objects2){
		double comNum=0.0;
		for(int i=0;i<objects.length;i++){
			for(int j=0;j<objects2.length;j++){
				Integer a=(Integer)objects[i];
				Integer b=(Integer)objects2[j];
				if(a.equals(b)){
					comNum+=1.0;
				}
			}
		}
		return comNum/Math.max(objects.length, objects2.length);
	}
	
	
	/**
	 * @param array1
	 * @param array2
	 * @return array1+=array2
	 */
	static public void addEquals(double[] array1,double[] array2){
		for(int i=0;i<array1.length;i++){
			array1[i]+=array2[i];
		}
		
	}
	
	static public double [] add(double[] array1,double[] array2){
		double []ret=new double[array1.length];
		for(int i=0;i<array1.length;i++){
			ret[i]=array1[i]+array2[i];
		}
		return ret;
	}
//	static public double[] mult(double[] array1, double[] array2){
//		
//		double [] array=new double[array1.length];
//		for(int i=0;i<array1.length;i++){
//			array[i]=array1[i]*array[i];
//		}
//		return array;
//		
//	}
	static public double[] times(double[] array,double s){
		double[] array1=new double[array.length];
		for(int i=0;i<array.length;i++){			
			array1[i]=array[i]*s;
		}
		return array1;
		
	}
	static public  double dotProduct(double[] array1, double[] array2) {
		double ret = 0.0;
		for (int i = 0; i < array1.length; i++) {
			ret += array1[i] * array2[i];
		}
		return ret;
	}
	static public void divideEquals(double[] array, double divsor){
		for(int i=0;i<array.length;i++){
			array[i]=array[i]/divsor;
		}
	}
	static public double[] divide(double[] array, double divsor){
		double[] ret=new double[array.length];
		for(int i=0;i<array.length;i++){
			ret[i]=array[i]/divsor;
		}
		return ret;
	}
	static public double[] minus(double[] array1, double[] array2){
		double[] array=new double[array1.length];
		for(int i=0;i<array1.length;i++){
			array[i]=array1[i]-array2[i];
		}
		return array;
	}
	public static void clear(double[] ds) {
		// TODO Auto-generated method stub
		for( int i=0;i<ds.length;i++){
			ds[i]=0;
		}
		
	}

}
