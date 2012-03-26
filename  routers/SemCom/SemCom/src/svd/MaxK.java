package svd;

public class MaxK {
	static public double[]getMaxK(double []array,int k){
		double[] topArray=new double[k];
		for(int i=0;i<k;i++){
			topArray[i]=array[i];
		}
		//build heap
		for(int i=k/2-1;i>=0;i--){
			 HeapAdjust(topArray,i,k-1);
		}
			  
		for(int i=k;i<array.length;i++){
			if(array[i]>topArray[0]){
				topArray[0]=array[i];
				HeapAdjust(topArray,0,k-1);
			}
		}
		
		return topArray;
	}
	static private void HeapAdjust(double[] toparray,int top,int len)
	{
		double tmp=toparray[top];
		for(int i=top*2;i<=len;i*=2)
		{
		   if(toparray[i]>toparray[i+1]&&i<len){
			   i++;
		   }	    
		   if(tmp>toparray[i])
		   {
		    toparray[top]=toparray[i];
		    top=i;
		   }else{
			   break;
		   }
		}
		toparray[top]=tmp;
		}

}
