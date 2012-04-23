package com;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.tseg.extern.Evaluation;

import model.MyGraph;

public class ComDetection {

	private String inFile = "c:/graph1.txt";
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
				phi2[k][m] = phi[k][m] * 0.5 + 0.5 * amout;

			}
		}
		phi = phi2;

	}

	public boolean run2() throws IOException {

		this.graph.readGraphFile(this.inFile);
		SubGGenerator g = new SubGGenerator();
		g.setGraph(this.graph);
		g.setSmapleNum(20);
		int[][] documents = g.getSubGDoc3();

		M = documents.length;
		int maxWId = -1;
		HashSet wordset = new HashSet();
		for (int i = 0; i < documents.length; i++) {
			for (int j = 0; j < documents[i].length; j++) {
				if (documents[i][j] > maxWId) {
					maxWId = documents[i][j];
				}
				wordset.add(documents[i][j]);
			}
		}

		System.out.println("Latent Dirichlet Allocation using Gibbs Sampling.");

		LdaGibbsSampler lda = new LdaGibbsSampler(documents, V);
		lda.configure(10000, 2000, 100, 10);
		lda.gibbs(K, alpha, beta);

		double[][] theta = lda.getTheta();
		double[][] phi = lda.getPhi();

		int[][] nw = lda.nw;

		System.out.println("Document--Topic Associations, Theta[d][k] (alpha="
				+ alpha + ")");
		// System.out.print("d\\k\t");
		for (int m = 0; m < theta[0].length; m++) {
			// System.out.print("   " + m % 10 + "    ");
		}
		System.out.println();
		for (int m = 0; m < theta.length; m++) {
			// System.out.print(m + "\t");
			for (int k = 0; k < theta[m].length; k++) {
				// System.out.print(theta[m][k] + " ");
				// System.out.print(theta[m][k] + " ");
			}
			// System.out.println();
		}
		// System.out.println();
		// System.out.println("Topic--Term Associations, Phi[k][w] (beta=" +
		// beta
		// + ")");

		// System.out.print("k\\w\t");
		// System.out.println();
		List[] cluster = new List[K];
		for (int i = 0; i < cluster.length; i++) {
			cluster[i] = new ArrayList<Integer>();
		}
		// 过滤
		this.filter(phi);
		for (int w = this.startId; w < V; w++) {

			double max1 = Double.MIN_VALUE;
			int c = 0;
			for (int k = 0; k < phi.length; k++) {
				if (phi[k][w] > max1) {
					max1 = phi[k][w];
					c = k;
				}
			}
			cluster[c].add(w);
		}

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

		Evaluation e = new Evaluation();
		e.setClusterFlie(this.outFile);
		e.setGraphFile(this.inFile);
		this.modulity = e.getModularity();
		System.out.println(".....................Modularity:" + this.modulity);
		return true;

	}

	public boolean run() throws IOException {

		this.inFile = "c:/dolphins-edges.txt";
		this.graph.readGraphFile(this.inFile);
		SubGGenerator g = new SubGGenerator();
		g.setGraph(this.graph);
		g.setSmapleNum(30);
		int[][] documents = g.getSubGDoc3();

		M = documents.length;
		this.V = 62;
		this.K = 2;
		int maxWId = -1;
		HashSet wordset = new HashSet();
		for (int i = 0; i < documents.length; i++) {
			for (int j = 0; j < documents[i].length; j++) {
				if (documents[i][j] > maxWId) {
					maxWId = documents[i][j];
				}
				wordset.add(documents[i][j]);
			}
		}

		System.out.println("Latent Dirichlet Allocation using Gibbs Sampling.");

		LdaGibbsSampler lda = new LdaGibbsSampler(documents, V);
		lda.configure(1000, 2000, 100, 10);
		lda.gibbs(K, alpha, beta);

		// double[][] theta = lda.getTheta();

		int[][] nw = lda.nw;

		System.out.println("Document--Topic Associations, Theta[d][k] (alpha="
				+ alpha + ")");

		System.out.println();

		List[] cluster = new List[K];
		for (int i = 0; i < cluster.length; i++) {
			cluster[i] = new ArrayList<Integer>();
		}
		// 过滤
		double[][] cArray = this.filter(nw);
		// output posterior
		this.outputPosterior(cArray);

		for (int w = this.startId; w < V; w++) {

			double max1 = Double.MIN_VALUE;
			int c = 0;
			for (int k = 0; k < cArray[w].length; k++) {
				if (cArray[w][k] > max1) {
					max1 = cArray[w][k];
					c = k;
				}
			}
			cluster[c].add(w);
		}

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
		Evaluation e = new Evaluation();
		e.setClusterFlie(this.outFile);
		e.setGraphFile(this.inFile);
		this.modulity = e.getModularity();
		System.out.println(".....................Modularity:" + this.modulity);
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

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ComDetection c = new ComDetection();
		double molAmount = 0.0;
		int cnt = 0;
		for (int i = 0; i < 1; i++) {
			if (c.run()) {
				molAmount += c.getModulity();
				cnt++;
			} else {
				System.out.println("error");
			}
		}
		System.out.println("cnt" + cnt);
		System.out.println("average modularity:" + molAmount / cnt);

	}

	public double getModulity() {
		return modulity;
	}

}
