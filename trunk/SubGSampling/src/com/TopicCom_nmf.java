package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.vecmath.GMatrix;

import org.tseg.graph.Graph;
import org.tseg.graph.community.VertexCommunity;
import org.tseg.graph.community.VertexCommunityImpl;
import org.tseg.graph.edge.Edge;
import org.tseg.graph.vertex.Vertex;
import org.tseg.visual.cluster.Clusters;
import org.tseg.visual.cluster.ClustersImpl;
import org.tseg.visual.control.ColorChange;

import model.MyGraph;
import model.Node;

public class TopicCom_nmf {


	private String outFile = "c:/cluster.txt";
	private MyGraph graph = new MyGraph();
	private double modulity = 0.0;
	private int startId = 0;

	// vocabulary
	int V = 128;
	int M = 30;
	// # topics
	int K = 4;
	// good values alpha = 2, beta = .5
	double alpha = 2;
	double beta = .5;

	private void filter(double[][] phi) {

		double[][] phi2 = phi.clone();
		for (int k = 0; k < phi.length; k++) {
			for (int m = 0; k < phi[k].length; m++) {
				double amout = 0.0;
				List<String> adjList = this.graph.getAdjList(String.valueOf(m));
				if (adjList == null) {
					System.out.println("no exit!!!!" + m);
					break;

				}
				for (String adj : adjList) {
					amout += phi[k][Integer.parseInt(adj)];
				}
				amout = amout / adjList.size();
				phi2[k][m] = phi[k][m] * 0.6 + 0.4 * amout;

			}
		}
		phi = phi2;

	}

	public void read(Graph g){
		g.getVertexIterator();
		for(Iterator it=g.getVertexIterator();it.hasNext();){
			Vertex v=(Vertex) it.next();
			Node n=new Node();
			n.setId(v.toString());
			v.toString();
			for(Iterator et=v.getEdges();et.hasNext();){
				Edge e=(Edge) et.next();
				String id=e.getSecondVertex().toString();
				n.getAdjList().add(id);
			}
			this.graph.getNodeTable().put(n.getId(), n);
		}
	}
	public boolean run(int samplNum, int topicNum) throws IOException {

	
		SubGGenerator g = new SubGGenerator();
		g.setGraph(this.graph);
		g.setSmapleNum(samplNum);
		int[][] documents = g.getSubGDoc3();
		int[][] weight = g.getWeight(documents);

		M = documents.length;
		this.V = this.graph.getNodeTable().size();
		this.K = topicNum;
		

		nmf2 test = new nmf2(K,M,V,3);



		test.load(documents, weight);
		test.train();
		
		GMatrix v = test.getV();
		List[] cluster = new List[K];
		for (int i = 0; i < cluster.length; i++) {
			cluster[i] = new ArrayList<Integer>();
		}
		/* 把节点根据概率划分社团 */
		for(int i=0; i < v.getNumRow(); ++i) {
			int position = 0;
			for(int j=0; j < v.getNumCol(); ++j) {
				if(v.getElement(i, j) > v.getElement(i, position)) {
					position = j;
				}
			}
			cluster[position].add(i);
		}
		
//		int maxWId = -1;
//		HashSet wordset = new HashSet();
//		for (int i = 0; i < documents.length; i++) {
//			for (int j = 0; j < documents[i].length; j++) {
//				if (documents[i][j] > maxWId) {
//					maxWId = documents[i][j];
//				}
//				wordset.add(documents[i][j]);
//			}
//		}
//
//		System.out.println("Non-negative matrix factorization.");
//		
//		
//		LdaGibbsSampler lda = new LdaGibbsSampler(documents, V);
//		lda.configure(1000, 2000, 100, 10);
//		lda.gibbs(K, alpha, beta);
//
//		// double[][] theta = lda.getTheta();
//
//		int[][] nw = lda.nw;
//
//		System.out.println("Document--Topic Associations, Theta[d][k] (alpha="
//				+ alpha + ")");
//
//		System.out.println();
//
//		List[] cluster = new List[K];
//		for (int i = 0; i < cluster.length; i++) {
//			cluster[i] = new ArrayList<Integer>();
//		}
//		// 过滤
//		double[][] cArray = this.filter(nw);
//		// output posterior
//		this.outputPosterior(cArray);
//
//		for (int w = this.startId; w < V; w++) {
//
//			double max1 = Double.MIN_VALUE;
//			int c = 0;
//			for (int k = 0; k < cArray[w].length; k++) {
//				if (cArray[w][k] > max1) {
//					max1 = cArray[w][k];
//					c = k;
//				}
//			}
//			cluster[c].add(w);
//		}

		BufferedWriter w = null;
		FileOutputStream file1 = new FileOutputStream(new File(this.outFile));
		w = new BufferedWriter(new OutputStreamWriter(file1, "UTF-8"));
		int cnt = 0;
		for (List<Integer> l : cluster) {
			// System.out.println(l.toString());
			String s = "";
			if (l.size() == 0) {
				return false;
			}
			for (Integer a : l) {
				s += a + " ";
				cnt++;
			}
			w.write(s + "\n");
			System.out.println(s);
		}

		System.out.println("cnt:" + cnt);
		w.flush();
		w.close();
		
		System.out.println(this.V);
		return true;

	}

	private void outputPosterior(double[][] cArray) throws IOException {

		BufferedWriter w = null;
		FileOutputStream file1 = new FileOutputStream(new File("c:/multi.txt"));
		w = new BufferedWriter(new OutputStreamWriter(file1, "UTF-8"));

		for (int i = this.startId; i < cArray.length; i++) {
			String s = i + " ";
			for (int k = 0; k < cArray[i].length; k++) {
				s += cArray[i][k] + " ";
			}
			w.write(s + "\n");
		}
		w.flush();
		w.close();

	}

	private double[][] filter(int[][] nw) {
		// TODO Auto-generated method stub
		double[][] phi2 = new double[nw.length][nw[0].length];
		double[][] phi1 = new double[nw.length][nw[0].length];
		// 归一化
		for (int m = 0; m < nw.length; m++) {
			int sum=0;
			for (int k = 0; k < nw[m].length; k++) {
				sum+=nw[m][k];
			}
			for (int k = 0; k < nw[m].length; k++) {
				phi1[m][k]=nw[m][k]*1.0/sum;
			}
		}

		for (int m = 0; m < phi1.length; m++) {
			for (int k = 0; k < phi1[m].length; k++) {

				double amout = 0.0;
				List<String> adjList = this.graph.getAdjList(String.valueOf(m));
				if (adjList == null) {
					System.out.println("no exit!!!!" + m);
					break;

				}
				for (String adj : adjList) {
					amout += phi1[Integer.parseInt(adj)][k];
				}
				amout = amout / adjList.size();
				phi2[m][k] = (phi1[m][k] * 0.5 + 0.5 * amout);

			}
		}
		return phi2;

	}
	
	public List<VertexCommunity> getCommunities(Graph g) throws IOException  
	{
		
		FileInputStream file = new FileInputStream(new File(this.outFile));
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(file, "UTF-8"));
		String tempString = null;
		int line = 0;
		// 一次读入一行，直到读入null为文件结束
		Clusters clusters = new ClustersImpl();
		ColorChange colorChange = new ColorChange(10);
		List<Set> nodeSetList =new ArrayList<Set>();
		while ((tempString = reader.readLine()) != null) {
			String []vertexs=tempString.split(" ");
			Set<Vertex> set=new HashSet<Vertex>();
			for(String id:vertexs){
				Vertex v=g.getVertex(Integer.parseInt(id));
				set.add(v);
			}
			nodeSetList.add(set);
			
		}
		//List nodeSetList = findCommunityNodeSetList();

	
		List communityList = new ArrayList();
		for (int i = 0; i < nodeSetList.size(); i++)
		{
			Set set = (Set)nodeSetList.get(i);
			VertexCommunity community = new VertexCommunityImpl(g, set);
			communityList.add(community);
		}
		return communityList;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub


	}

	public double getModulity() {
		return modulity;
	}

}
