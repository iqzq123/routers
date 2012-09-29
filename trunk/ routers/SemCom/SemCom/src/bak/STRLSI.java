package bak;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import svd.MatElement;
import svd.MaxK;
import svd.Vector;

import bak.CDRLSI.DTIndex;
import bak.CDRLSI.TDIndex;

public class STRLSI {

	public class Item {
		public Item(int i, int j, double value) {
			super();
			this.docId = i;
			this.termId = j;
			this.value = value;
		}

		int docId;
		int termId;
		double value;
	}

	private int demesion = 4;
	private double[][] termTopics = null;
	private double[][] docTopics = null;
	private String[] terms = null;
	private String[] docName = null;
	private List<Item> ItemList = null;

	double lamda = 0.1;
	double alpha = 0.005;

	public STRLSI(int demesion) {
		// TODO Auto-generated constructor stub
		this.demesion=demesion;
	}

public void test() throws IOException{
		
		this.initial(5, 5, 4, 1.0);
		double [][]data={
		{1,2,3,1,5},  
        {1,2,3,1,5},  
        {5,1,3,1,2},  
        {5,1,3,1,2},  
        {5,1,3,1,2}};

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				Item e = new Item(i,j,data[i][j]);		
				this.ItemList.add(e);		
				//elemList.add(e);
			}
		}

		this.train();
		for(int i=0;i<5;i++){
			for(int j=0;j<5;j++){
				double pre=0.0;
				for(int k=0;k<this.demesion;k++){
					pre=pre+this.docTopics[i][k]*this.termTopics[j][k];
				}
				System.out.println(i+"\t"+j+"\t"+data[i][j]+"\t"+pre);
			}
		}

		System.out.println("end");
	}
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		STRLSI rlsi=new STRLSI(4);
		rlsi.test();
		rlsi.readMatrix("c:/testDT.txt");
		rlsi.train();
		for(int k=0;k<rlsi.demesion;k++){
			rlsi.outputTopic(k);
		}
		
		File outfile = new File("c:/testDT1.doc");
		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));
		rlsi.printWord(w);
		

	}

	private void initial(int docNum, int termNum, int demesion,double mid)
			throws IOException {
		// TODO Auto-generated constructor stub
		this.termTopics = new double[termNum][demesion];
		this.docTopics = new double[docNum][demesion];
		for(int i=0;i<docNum;i++){
			for(int j=0;j<demesion;j++){
				this.docTopics[i][j]=Math.random()*mid;
			}
		}
		for(int i=0;i<termNum;i++){
			for(int j=0;j<demesion;j++){
				this.termTopics[i][j]=Math.random()*mid;
			}
		}
		this.ItemList=new ArrayList<Item>();
	}

	public void readMatrix(String dtFile) throws IOException {

		// replace term and doc Id
		BufferedReader reader = null;
		FileInputStream file = new FileInputStream(new File(dtFile));
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		String tempString = null;

		int cnt = 0;
		Hashtable<String, Integer> termDict = new Hashtable<String, Integer>();
		Hashtable<String, Integer> docDict = new Hashtable<String, Integer>();

		int docIndex = 0;
		int termIndex = 0;	
		double average=0.0;
		while ((tempString = reader.readLine()) != null) {
			cnt++;
			if (cnt % 100000 == 0) {
				System.out.println("read");
			}
			String[] strArray = tempString.split("\t");
			if (docDict.get(strArray[0]) == null) {
				docDict.put(strArray[0], docIndex);
				docIndex++;
			}
			if (termDict.get(strArray[1]) == null) {
				termDict.put(strArray[1], termIndex);
				termIndex++;
			}
			double weight = Double.parseDouble(strArray[2]);
			average+=weight;
		}
		average=average/cnt;
		reader.close();
		System.out.println("docNum:"+docIndex);
		System.out.println("termNum:"+termIndex);
		// initialize vector
		double mid=Math.sqrt(average/demesion);
		System.out.println("average:"+average);
		System.out.println("mid:"+mid);
		initial(docIndex, termIndex, this.demesion,1.0);
		// read doc and build invertedIndex
		file = new FileInputStream(new File(dtFile));
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		this.docName = new String[docIndex];
		this.terms = new String[termIndex];
	
		while ((tempString = reader.readLine()) != null) {
			cnt++;
			if (cnt % 100000 == 0) {
				System.out.println("read");
			}
			String[] strArray = tempString.split("\t");
			Integer dId = docDict.get(strArray[0]);
			Integer tId = termDict.get(strArray[1]);
			this.docName[dId] = strArray[0];
			this.terms[tId] = strArray[1];
			double weight = Double.parseDouble(strArray[2]);
			this.ItemList.add(new Item(dId, tId, weight));

		}
		
		reader.close();

	}

	public void outputTopic(int k){
		double []array=new double[20];
		int termNum=this.termTopics.length;
		double[] wordArray=new double[termNum];
		for(int i=0;i<termNum;i++){
			wordArray[i]=this.termTopics[i][k];
		}
		array=MaxK.getMaxK(wordArray, 20);
		double min=array[0];
		int[]wordId=new int[20];
		String[] keywords=new String[20];
		int index=0;
		for(int i=0;i<termNum;i++){
			if(wordArray[i]>=min){
				//keywords[index]=this.words[i];
				if(index<20){
				wordId[index]=i;
				index++;
				}
				
			}
		}
		//sort words
		for(int i=0;i<20-1;i++){
			for(int j=0;j<20-i-1;j++){
				if(this.termTopics[wordId[j]][k]<this.termTopics[wordId[j+1]][k]){
					int tmp=wordId[j];
					wordId[j]=wordId[j+1];
					wordId[j+1]=tmp;
				}
			}
		}
		System.out.println("topic: "+k);
		for(int i=0;i<20;i++){
			System.out.println(this.terms[wordId[i]]+"	"+this.termTopics[wordId[i]][k]);
		}
	}

	public double[] getMaxK(double[] array, int k) {
		double[] topArray = new double[k];
		for (int i = 0; i < k; i++) {
			topArray[i] = array[i];
		}
		// build heap
		for (int i = k / 2 - 1; i >= 0; i--) {
			HeapAdjust(topArray, i, k - 1);
		}

		for (int i = k; i < array.length; i++) {
			if (array[i] > topArray[0]) {
				topArray[0] = array[i];
				HeapAdjust(topArray, 0, k - 1);
			}
		}

		return topArray;
	}

	private void HeapAdjust(double[] topArray, int top, int len) {
		double tmp = topArray[top];
		for (int i = top * 2; i <= len; i *= 2) {
			if (topArray[i] > topArray[i + 1] && i < len) {
				i++;
			}
			if (tmp > topArray[i]) {
				topArray[top] = topArray[i];
				top = i;
			} else {
				break;
			}
		}
		topArray[top] = tmp;
	}
	public void printDoc(Writer w) throws IOException {

		for (int i = 0; i < this.docTopics.length; i++) {
			String line = this.docName[i]+"\t";
			for (int k = 0; k < this.docTopics[i].length; k++) {
				line += this.docTopics[i][k] + "\t";
			}
			line+="\n";
			w.write(line);
		}
		w.flush();
	}
	public void printWord(Writer w) throws IOException {		
		for (int i = 0; i < this.termTopics.length; i++) {
			String line =this.terms[i]+ "\t";
			for (int k = 0; k < this.termTopics[i].length; k++) {
				line += this.termTopics[i][k] + "\t";
			}
			line+="\n";
			w.write(line);
		}
		w.flush();
	}
	public void train() throws IOException {

		int trainNum = 100;
		double errorAmount = 0.0;
		while (trainNum-- > 0) {
			for (Item e : ItemList) {
				double pre = 0.0;
				for (int i = 0; i < this.demesion; i++) {
					pre = pre + this.docTopics[e.docId][i]
							* this.termTopics[e.termId][i];
				}
				double err = e.value - pre;
				double a = 1.0 - alpha*lamda;
				double b = alpha * err;
				
				//System.out.println(err);
				errorAmount += err*err;
				for (int i = 0; i < this.demesion; i++) {
					
					double t = this.termTopics[e.termId][i]+ this.docTopics[e.docId][i] * b;
					//double d = this.docTopics[e.docId][i]+ this.termTopics[e.termId][i] * b;
					// Truncation of t
					double sign = 1.0;
					if (t < 0) {
						sign = -1.0;
					}
					//double v = Math.abs(d) - this.lamda * this.alpha;
					double v = Math.abs(t) - 0.003;
					if (v < 0) {
						v = 0.0;
					}
					this.termTopics[e.termId][i] = sign * v;
					this.docTopics[e.docId][i]=this.docTopics[e.docId][i]*a+this.termTopics[e.termId][i]*b;
				}

			}
			System.out.println("error amout:" + errorAmount / ItemList.size());
			errorAmount = 0.0;
		}
	
		
	}

}
