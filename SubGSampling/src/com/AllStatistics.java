package com;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.tseg.algorithm.global.AssortativityCoefficient;
import org.tseg.algorithm.global.BasicProperity;
import org.tseg.algorithm.global.WSClusterCoefficient;
import org.tseg.algorithm.shortestpath.asp.RandomShortestPath;
import org.tseg.graph.Graph;
import org.tseg.io.NaiveFileGraphReader;
import org.tseg.utils.GlobalEnvValue;

public class AllStatistics {

	public String run(String inPath) throws IOException {

		File dir = new File(inPath);
		String[] files = dir.list();
		
		
		int cnt=0;
	
		int m=0;
		int n =0;
		double density = 0;
		double averD=0;
		double cc=0;
		double acc=0;
		double asp=0;
		double diameter=0;
		for (String fileName : files) {
			if(fileName.contains("myExp")){
				continue;
			}
			cnt++;
			String filePath=dir.getAbsolutePath()+"/"+fileName;
			System.out.println("run"+filePath);
			Graph graph = NaiveFileGraphReader.readUndirectedGraph(filePath,
					GlobalEnvValue.LONG_VERTEX);
			
			System.out.print("graph size:"+graph.getEdgeNumber());
			BasicProperity summary = new BasicProperity(graph);
			
			m += summary.nodeNumber();
			n += summary.edgeNumber();
			density += summary.density();
			averD+=n*2/m;

			WSClusterCoefficient tCalculate = new WSClusterCoefficient(graph);
			cc += tCalculate.getAvgClusterCoefficient();
			AssortativityCoefficient ac = new AssortativityCoefficient(graph);
			acc += ac.getCoefficient();
			RandomShortestPath randASP = new RandomShortestPath(graph, 0.01);
			asp += randASP.getAllAvgShortestPath();
			diameter += randASP.getDiameter();
			String output=m+"\t"+n+"\t"+density+"\t"+averD+"\t"+cc+"\t"+acc+"\t"+asp+"\t"+diameter;
			System.out.println(output);
			
		}
		m=m/cnt;n=n/cnt; density=density/cnt; averD=averD/cnt;
		cc=cc/cnt; acc=acc/cnt; asp=asp/cnt; diameter=diameter/cnt;
		
		String output=m+"\t"+n+"\t"+density+"\t"+averD+"\t"+cc+"\t"+acc+"\t"+asp+"\t"+diameter;
		return output;
	

	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		AllStatistics a=new AllStatistics();
		BufferedWriter w = new BufferedWriter(new FileWriter("C:/Exp.txt"));
		w.write("|V|	|E|	density averDegree	AvgClusterCoefficient	Assortativity	AvgShortestPath	diameter\n");
		File dir = new File("C:/ke");
		String[] files = dir.list();
		String finalS="";
		for(String file:files){
		
			System.out.println(file);
			String s=a.run(dir.getAbsolutePath()+"/"+file);
			finalS+=s+"\n";
			w.write(file+":"+s+"\n");
		}	
		System.out.println(finalS);
		w.flush();
		w.close();
	}

}
