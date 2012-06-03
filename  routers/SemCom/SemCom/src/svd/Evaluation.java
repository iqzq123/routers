package svd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import utils.ArrayOP;

public class Evaluation {
	
	private Hashtable<Integer,Integer> labelTable=new Hashtable<Integer,Integer>();
	int [] docvid=new int[100];
	int []label=new int[100];
	int []match=new int[4];
	private double max=Double.MIN_VALUE;
	double [][]edge=null;
	
	public void loadTestData(String inPath) throws IOException{
		
		
		docvid=new int[100];
		label=new int[100];
		
		BufferedReader reader1 = null;
		FileInputStream file1 = new FileInputStream(new File(inPath
				+ "/docvid.txt"));
		System.out.println("read mdt��");
		reader1 = new BufferedReader(new InputStreamReader(file1, "UTF-8"));
		String tempString = null;

		
		int cnt=0;
		while ((tempString = reader1.readLine()) != null) {
			docvid[cnt]=Integer.parseInt(tempString);
			cnt++;
		}
		

		BufferedReader reader2 = null;
		FileInputStream file2 = new FileInputStream(new File(inPath
				+ "/doclabel.txt"));
		System.out.println("read mda��");
		reader2 = new BufferedReader(new InputStreamReader(file2, "UTF-8"));
	    
		cnt = 0;
		while ((tempString = reader2.readLine()) != null) {
			label[cnt]=Integer.parseInt(tempString)-1;
			cnt++;
		}
		
		for(int i=0;i<label.length;i++){
			this.labelTable.put(docvid[i], label[i]);
		}
	}
	
	private int getMaxId(double []array){
		int maxId = 0;
		double max=Double.MIN_VALUE;
		for(int i=0;i<array.length;i++){
			if(array[i]>max){
				maxId=i;
			}
		}
		return maxId;
	}
	public void evaluate(double[][] docTopic){
		
		//mapping
		List<Integer> [] docOfCL=new List[docTopic[0].length];
		for(int i=0;i<docOfCL.length;i++){
			docOfCL[i]=new ArrayList<Integer>();
		}
		for(int doc:this.docvid){
			int label=this.getMaxId(docTopic[doc]);
			docOfCL[label].add(doc);
		}
		
		List<Integer> []docOfL=new List[docTopic[0].length];
		for(int i=0;i<docOfL.length;i++){
			docOfL[i]=new ArrayList<Integer>();
		}
		for(int i=0;i<this.label.length;i++){
			docOfL[this.label[i]].add(this.docvid[i]);
		}
		
		// find max match L->CL
		int k=docTopic[0].length;
		edge=new double[k][k];
		for(int i=0;i<k;i++){
			for(int j=0;j<k;j++){
				edge[i][j]=ArrayOP.getSim(docOfL[i].toArray(), docOfCL[j].toArray());
			}
		}
		this.match(k);
		//compute ac
		int correctNum=0;
		for(int i=0;i<this.docvid.length;i++){
			int label=this.label[i];
			int cLabel=this.getMaxId(docTopic[docvid[i]]);
			if(this.match[label]==cLabel){
				correctNum++;
			}
		}
		double ac=correctNum*1.0/this.docvid.length;
		System.out.println(ac);
		
		
	}

	/*
	�������ƣ�Permutation
	�������ܣ�ȫ����ѭ����λ�������n����������ȫ����
	���������int n��1��2��3��...��n��n����Ȼ��
	�����������
	*/
	void match(int n)
	{
	    int[] a = new int[n];//�����洢n����Ȼ��
	    for (int i=0; i<n; i++) //�洢ȫ���е�Ԫ��ֵ��������ȫ���е�����
	    {
	        a[i] = i ;
	    }
	   
	    Recursion(a, n, 1);
	
	}
	/*
	�������ƣ�Recursion
	�������ܣ�ѭ�����Ƶݹ����n����������ȫ����
	���������int a[]���洢��0,1��2��3��...��n��n����Ȼ��������
	          int n������a[]�ĳ���
	          int k�����ڴ����k��Ԫ������ɵ�����
	�����������
	 
	*/
	void Recursion(int a[], int n, int k)
	{
	    if (k > n){
	    	String str="";
	    	
	    	double sim=0.0;
	    	for(int i=0;i<a.length;i++){
	    		sim+=this.edge[i][a[i]];
	    		str+=a[i]+",";
	    	}
	    	if(sim>this.max){
	    		this.match=a.clone();
	    		this.max=sim;
	    	}
	        System.out.println(str+","+sim);
	    }
	    	
	    else
	    {
	        int temp;
	        for (int i=0; i<k; i++)//ѭ������
	        {
	            temp = a[0];
	            for (int j=1; j<k; j++)
	                a[j-1] = a[j];
	            a[k-1] = temp;
	            
	            Recursion(a, n, k+1);
	        }
	    }
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Evaluation e=new Evaluation();
		e.edge=new double[4][4];
		e.edge[0][3]=1.0;
		e.edge[1][2]=1.0;
		e.edge[2][0]=1.0;
		e.edge[3][1]=1.0;
		
		e.match(4);
		for(int i=0;i<e.match.length;i++){
			System.out.println(e.match[i]);
		}

	}

}
