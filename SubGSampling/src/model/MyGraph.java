package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyGraph {

	private Hashtable<String, Node> nodeTable = new Hashtable<String, Node>();
	private String split="\t";
	
	public double getDegreeDif(MyGraph g){
		double maxdif=Double.MIN_VALUE;
		double [] array1=this.degreeDis();
		double [] array2=g.degreeDis();
		for(int i=0;i<101;i++){
			double dif=Math.abs(array1[i]-array2[i]);
			if(dif>maxdif){
				maxdif=dif;
			}
		}
		return maxdif;
	}
	public double getAvrCCDif(MyGraph g){
		double amout=0.0;
		double []array1=this.getAverCCArray();
		double []array2=g.getAverCCArray();
		for(int i=1;i<100;i++){
			amout+=Math.abs(array1[i]-array2[i]);
		}
		int m=0;
		for(int i=1;i<100;i++){
			int a=this.getDisArray()[i];
			if(a>0){
				m++;
			}
		}
		return amout/m;
	}
	public int []getDisArray(){
			int[] array = new int[101];
		for (Iterator it = nodeTable.entrySet().iterator(); it.hasNext();) {

			Map.Entry entry = (Map.Entry) it.next();
			Node node=(Node)entry.getValue();
			int degree=node.getAdjList().size();
			if(degree<100){
				array[degree]+=1;
			}else{
				array[100]+=1;
			}			
		
		}
		return array;
	}
	public HashSet getTwoHopSet(String id){
		HashSet s=new HashSet();
		for(String adj:nodeTable.get(id).getAdjList()){
			s.add(adj);
			for(String adj2:nodeTable.get(adj).getAdjList()){
				s.add(adj2);
			}
		}
		return s;
	}
	

	public double [] degreeDis() {
		//System.out.println("degreeDistrube");
		int[] array = new int[101];
		for (Iterator it = getNodeTable().entrySet().iterator(); it.hasNext();) {

			Map.Entry entry = (Map.Entry) it.next();
			Node node=(Node)entry.getValue();
			int degree=node.getAdjList().size();
			if(degree<100){
				array[degree]+=1;
			}else{
				array[100]+=1;
			}			
		
		}	
		for(int i=1;i<array.length;i++){
			array[i]=array[i]+array[i-1];
		}
		double []perArray=new double[array.length];
		for(int i=0;i<perArray.length;i++){
			perArray[i]=array[i]*1.0/array[100];
		}
		return perArray;
	}
	public double[]  getAverCCArray(){
		
		double []ccArray=new double[101];
		for(int i=0;i<101;i++){
			ccArray[i]=0.0;
		}
		for (Iterator it = getNodeTable().entrySet().iterator(); it.hasNext();) {

			Map.Entry entry = (Map.Entry) it.next();
			Node node=(Node)entry.getValue();
			String []array=new String[node.getAdjList().size()];
			int index=0;
			for(String adj:node.getAdjList()){
				array[index]=adj;
				index++;
			}
			int l=0;
			for(int i=0;i<array.length;i++){
				for(int j=i+1;j<array.length;j++){
					if(this.nodeTable.get(array[i]).getAdjList().contains(array[j])){
						l++;
					}
				}
			}
			double closeness=0.0;
			if(node.getAdjList().size()>1){
				closeness=2*l/((node.getAdjList().size()-1)*node.getAdjList().size());
			}
			int d=node.getAdjList().size();
			if(d>100){
				d=100;
			}
			ccArray[d]+=closeness;
		
		}	
		int[] disArray=this.getDisArray();
		for(int i=0;i<101;i++){
			if(disArray[i]!=0){
				ccArray[i]=ccArray[i]/disArray[i];
			}
			
		}
		
		return ccArray;
		
	}
	public void setNodeTable(Hashtable<String, Node> nodeTable) {
		this.nodeTable = nodeTable;
	}

	public Hashtable<String, Node> getNodeTable() {
		return nodeTable;
	}

	public List<String> getAdjList(String id) {
		//System.out.println("error:"+id);
		if(this.nodeTable.get(id)==null){
			System.out.println("error:"+id);
			return null;
		}
		return this.nodeTable.get(id).getAdjList();
	}

	public String getRondomNode() {
		for (Iterator it = getNodeTable().entrySet().iterator(); it.hasNext();) {

			Map.Entry entry = (Map.Entry) it.next();
			String id = (String) entry.getKey();
			Random r = new Random();
			if (r.nextInt() % 8 == 0) {
				return id;
			}
		}
		return nodeTable.values().iterator().next().getId();
	}

	public void readGraphFile(String fileName) {

		try {
			FileInputStream file = new FileInputStream(new File(fileName));
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					file, "UTF-8"));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				line++;
			
				String[] array = tempString.split(this.split);
				if(array.length<2){
					continue;
				}
				
				if(line%10000==0){
					System.out.println(line+":"+array[0]+" "+array[1]);
				}
				Node node1 = nodeTable.get(array[0]);
				if (node1 == null) {
					node1 = new Node();
					node1.setId(array[0]);
					node1.getAdjList().add(array[1]);
				} else {
					if (!node1.getAdjList().contains(array[1])) {  
						node1.getAdjList().add(array[1]);
					}
				}
				this.nodeTable.put(node1.getId(), node1);
				Node node2 = nodeTable.get(array[1]);
				if (node2 == null) {
					node2 = new Node();
					node2.setId(array[1]);
					node2.getAdjList().add(array[0]);
				} else {
					if (!node2.getAdjList().contains(array[0])) {
						node2.getAdjList().add(array[0]);
					}
				}
				this.nodeTable.put(node2.getId(), node2);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		MyGraph g=new MyGraph();
		g.readGraphFile("d:/CA-CondMat-ID-LCC-ID.txt");
		double degreeDif=0.0;
		double ccDif=0.0;
		
		double degreeDif1=0.0;
		double ccDif1=0.0;
		File dir = new File("d:/CA-CondMat/CA-CondMat");
		String []files=dir.list();
		int cnt=0;
		int cnt1=0;
		for(int i=0;i<files.length;i++){
			if(files[i].contains("myExp")){
				cnt++;
				System.out.println("myExp:"+cnt);			
				MyGraph g1=new MyGraph();
				g1.readGraphFile(dir.getAbsolutePath()+"/"+files[i]);
				degreeDif+=g.getDegreeDif(g1);
				ccDif+=g.getAvrCCDif(g1);
			}else{
				cnt1++;
				System.out.println("Exp:"+cnt1);
				MyGraph g2=new MyGraph();
				g2.readGraphFile(dir.getAbsolutePath()+"/"+files[i]);
				degreeDif1+=g.getDegreeDif(g2);
				ccDif1+=g.getAvrCCDif(g2);
			}
		}
		System.out.println(cnt1);
		System.out.println("myExp 度分布差值:"+degreeDif/cnt);
		System.out.println("myExp cc差值:"+ccDif/cnt);
		
		System.out.println("Exp 度分布差值:"+degreeDif1/cnt1);
		System.out.println("Exp cc差值:"+ccDif1/cnt1);
	}
	public void setSplit(String split) {
		this.split = split;
	}
	public String getSplit() {
		return split;
	}
	
}
