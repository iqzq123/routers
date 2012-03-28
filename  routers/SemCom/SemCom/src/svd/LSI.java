package svd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LSI {
	private int m = 1000;
	private int n = 500;
	private int k = 5;
	private Vector[] mVectors = null;
	private Vector[] nVectors = null;
	private String []words=new String[12000];
	private String inFile = "";
	private String wordFile="";
	public String getWordFile() {
		return wordFile;
	}

	public void setWordFile(String wordFile) {
		this.wordFile = wordFile;
	}

	double lamda = 0.1;
	double alpha = 0.02;
	double errorAmout = 0.0;

	public void initial(int m, int n, int k) throws IOException {
		// TODO Auto-generated constructor stub
		
		BufferedReader reader = null;
		FileInputStream file = new FileInputStream(new File(this.wordFile));
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		String tempString=null;

		int cnt=0;
		while ((tempString = reader.readLine()) != null) {
			words[cnt]=tempString;
			cnt++;
		}
		reader.close();
		this.m = m;
		this.n = n;
		this.k = k;
		mVectors = new Vector[m];
		for (int i = 0; i < m; i++) {
			mVectors[i] = new Vector(k);
		}
		nVectors = new Vector[n];
		for (int i = 0; i < n; i++) {
			nVectors[i] = new Vector(k);
		}
	}

	public LSI(int m, int n, int k) {
		super();
		// TODO Auto-generated constructor stub
		this.m = m;
		this.n = n;
		this.k = k;
		mVectors = new Vector[m];
		for (int i = 0; i < m; i++) {
			mVectors[i] = new Vector(k);
		}
		nVectors = new Vector[n];
		for (int i = 0; i < n; i++) {
			nVectors[i] = new Vector(k);
		}
	}

	public LSI() {
		// TODO Auto-generated constructor stub
	}

	public void test() {
		
	
		mVectors = new Vector[m];
		for (int i = 0; i < m; i++) {
			mVectors[i] = new Vector(k);
		}
		nVectors = new Vector[n];
		for (int i = 0; i < n; i++) {
			nVectors[i] = new Vector(k);
		}

		List<MatElement> elemList = new ArrayList<MatElement>();

		for (int i = 0; i < this.m; i++) {
			for (int j = 0; j < this.n; j++) {
				MatElement e = new MatElement();
				e.i = i;
				e.j = j;
				e.value = Math.random()* 10;
				int a=(int) (Math.random()*10000);
				if(a%200<2){
					elemList.add(e);
				}
				//elemList.add(e);
			}
		}

		int trainNum = 150;
		double err=0.0;
		while (trainNum-- > 0) {
			for (MatElement e : elemList) {
				this.updateOneElem(e);
			}
			err=this.errorAmout/ elemList.size();
			System.out.println("error amout:" + err);
			this.errorAmout = 0.0;
		}
		for (MatElement e : elemList) {
			double value1 = mVectors[e.i].multWith(nVectors[e.j]);
			System.out.println(e.i + "," + e.j + "," + e.value + "," + value1);
		}
		System.out.println("cnt:"+elemList.size()+"\n");
		System.out.println("average error:"+err+"\n");

	}

	public void outPutTopics(){
		
		for(int a=0;a<this.k;a++){
			double[] array = new double[this.nVectors.length];
			for (int i = 0; i < this.nVectors.length; i++) {
				array[i] = this.nVectors[i].getValueArray()[a];
			}
			// this.getMaxK(array, 20);
			double minValue = this.getMaxK(array, 20)[0];
			String[] keywords = new String[20];
			int index = 0;
			for (int i = 0; i < this.nVectors.length; i++) {
				if (this.nVectors[i].getValueArray()[a] > minValue) {
					keywords[index] = this.words[i];
					index++;
				}
			}
			System.out.println("topic" + a + ":");
			for (int i = 0; i < keywords.length; i++) {
				System.out.println(keywords[i]);
			}
		}
	
		
		
	}
	public double[]getMaxK(double []array,int k){
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
	private void HeapAdjust(double[] toparray,int top,int len)
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
	public void train() throws IOException {
		BufferedReader reader = null;
		FileInputStream file = new FileInputStream(new File(this.inFile));
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		String tempString = null;
		List<MatElement> elemList = new ArrayList<MatElement>();
		double amout=0.0;
		while ((tempString = reader.readLine()) != null) {
			String[] strArray = tempString.split(",");
			MatElement e = new MatElement();
			e.i = Integer.parseInt(strArray[0]);
			e.j = Integer.parseInt(strArray[1]);
			e.value = Double.parseDouble(strArray[2]);
			amout+=e.value;
			elemList.add(e);
			int a=(int) (Math.random()*1000);
			if(a%100000==0){
				System.out.println("read\n");
			}
		}
		System.out.println("data size:"+elemList.size());
		System.out.println("aver value:"+amout/elemList.size());
		System.out.println("array data"+this.mVectors[0].toString());
		int trainNum = 100;
		while (trainNum-- > 0) {
			int cnt=0;
			for (MatElement e : elemList) {
				cnt++;
				if(cnt%100000==0){
					//System.out.println(".");
				}
				this.updateOneElem(e);
			}
			System.out.println("error amout:" + this.errorAmout
					/ elemList.size());
			this.errorAmout = 0.0;
		}
		for (MatElement e : elemList) {
			double value1 = mVectors[e.i].multWith(nVectors[e.j]);
			int a=(int) (Math.random()*10000);
			if(a%10000<10){
				System.out
					.println(e.i + "," + e.j + "," + e.value + "," + value1);
			}
			
		}

	}

	public void updateOneElem(MatElement e) {
		Vector mVector = mVectors[e.i];
		Vector nVector = nVectors[e.j];
		double erro = e.value - mVector.multWith(nVector);
		this.errorAmout += Math.abs(erro);
		Vector part1 = mVector.scale(this.lamda);
		Vector part2 = nVector.scale(erro);
		Vector direction = part1.sub(part2);
		Vector nextMVector = mVector.sub(direction.scale(alpha));

		//梯度下降
		erro = e.value - nextMVector.multWith(nVector);
	
		Vector part11 = nVector.scale(this.lamda);
		Vector part22 = mVector.scale(erro);
		Vector direction2 = part11.sub(part22);
		Vector nextNVector = nVector.sub(direction2.scale(alpha));
		mVectors[e.i] = nextMVector;
		nVectors[e.j] = nextNVector;
		if((nextNVector.getValueArray()[0])>10000.0){
			System.out.println("aa"+nextNVector.getValueArray()[0]);
		}
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getM() {
		return m;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getN() {
		return n;
	}

	public String getInFile() {
		return inFile;
	}

	public void setInFile(String inFile) {
		this.inFile = inFile;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		double[] array={1.0,2.0,3.0,4.0,9.0,8.0,7.0,5.0,6.0};

		double[][] nVertors1 = new double[2][10];
		nVertors1[0][0] = 1.0;
		nVertors1[0][1] = 2.0;
		// System.out.println(nVertors1[0][1]);
		LSI l = new LSI();
//		l.test();
		l.setWordFile("c:/data/term.txt");
		l.initial(28569, 11771, 4);	
		l.setInFile("c:/mdt.txt");
		l.train();
		l.outPutTopics();
//		int []a=new int[1];
//		//a[1]=1;
//		a[0]=1;

	}

}
