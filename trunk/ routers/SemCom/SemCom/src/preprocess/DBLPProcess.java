package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DBLPProcess {
	public void generateCoNet() throws IOException{

		BufferedReader reader = null;
		FileInputStream file = new FileInputStream(new File("c:/data/mda.txt"));
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		
		File out = new File("c:/coPaper.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(out));
		
		String tempString = null;
		int line = 1;
		List<String> edgeList=new ArrayList<String>();
		Hashtable<String,String> paperTable=new Hashtable<String,String>();
		while ((tempString = reader.readLine()) != null) {
			String []strArray=tempString.split(",");
			String paper=strArray[0];
			String au=strArray[1];
			String aus=paperTable.get(paper);
			if(aus==null){
				paperTable.put(paper, au);	
			}else{
				aus+=","+au;
				paperTable.put(paper, aus);
			}		
		}
		Hashtable<String,Integer> edgeTable=new Hashtable<String,Integer>();
		for (Iterator it = paperTable.entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry) it.next();
			String p=(String)entry.getKey();
			String aus=(String)entry.getValue();
			String[] auArray=aus.split(",");
			for(int i=0;i<auArray.length-1;i++){	
				for(int j=i+1;j<auArray.length;j++){
					String edge="";
					if(Integer.parseInt(auArray[i])>Integer.parseInt(auArray[j])){
						edge=auArray[i]+" "+auArray[j];
					}else{
						edge=auArray[j]+" "+auArray[i];
					}
					Integer cnt=edgeTable.get(edge);
					if(cnt==null){
						edgeTable.put(edge, 1);
					}else{
						cnt++;
						edgeTable.put(edge, cnt);
					}
				}
			}
		}
		for(Iterator it=edgeTable.entrySet().iterator();it.hasNext();){
			Map.Entry<String, Integer> map=(Entry<String, Integer>) it.next();
			String edge=map.getKey();
			Integer cnt=map.getValue();
			
			if(cnt>0)
			w.write(edge+" "+cnt+"\n");
		}
		w.flush();
		w.close();
	}
	public void generateDocNet() throws IOException{

		BufferedReader reader = null;
		FileInputStream file = new FileInputStream(new File("c:/data/mda.txt"));
		System.out.println("以行为单位读取文件内容，一次读一整行：");
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		
		File out = new File("c:/coPaper.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(out));
		
		String tempString = null;
		int line = 1;
		List<String> edgeList=new ArrayList<String>();
		Hashtable<String,String> auDocTable=new Hashtable<String,String>();
		int []auNumOfDoc=new int[28569];
		while ((tempString = reader.readLine()) != null) {
			String []strArray=tempString.split(",");
			String curDoc=strArray[0];
			auNumOfDoc[Integer.parseInt(curDoc)]++;
			String au=strArray[1];
			String docs=auDocTable.get(au);
			if(docs==null){
				auDocTable.put(au, curDoc);	
			}else{
				docs+=","+curDoc;
				auDocTable.put(au, docs);
			}		
		}
		Hashtable<String,Integer> edgeTable=new Hashtable<String,Integer>();
		for (Iterator it = auDocTable.entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry) it.next();
			String aus=(String)entry.getKey();
			String docs=(String)entry.getValue();
			String[] docArray=docs.split(",");
			for(int i=0;i<docArray.length-1;i++){	
				for(int j=i+1;j<docArray.length;j++){
					String edge="";
					if(Integer.parseInt(docArray[i])>Integer.parseInt(docArray[j])){
						edge=docArray[i]+" "+docArray[j];
					}else{
						edge=docArray[j]+" "+docArray[i];
					}
					Integer cnt=edgeTable.get(edge);
					if(cnt==null){
						edgeTable.put(edge, 1);
					}else{
						cnt++;
						edgeTable.put(edge, cnt);
					}
				}
			}
		}
		for(Iterator it=edgeTable.entrySet().iterator();it.hasNext();){
			Map.Entry<String, Integer> map=(Entry<String, Integer>) it.next();
			String edge=map.getKey();
			Integer cnt=map.getValue();
			int doc1=Integer.parseInt(edge.split(" ")[0]);
			int doc2=Integer.parseInt(edge.split(" ")[1]);
			double weight=cnt*1.0/Math.max(auNumOfDoc[doc1], auNumOfDoc[doc1]);
			
			if(cnt>0)
			w.write(doc1+","+doc2+","+weight+"\n");
		}
		w.flush();
		w.close();
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		DBLPProcess p=new DBLPProcess();
		//p.generateCoNet();
		p.generateDocNet();
	}

}
