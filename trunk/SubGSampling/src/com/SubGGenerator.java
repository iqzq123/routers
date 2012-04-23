package com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.Map.Entry;

import model.MyGraph;
import model.Node;

public class SubGGenerator {

	private MyGraph graph = new MyGraph();
	private int smapleNum = 10;
	private int minDegree = 5;

	public MyGraph getGraph() {
		return graph;
	}

	public void setGraph(MyGraph graph) {
		this.graph = graph;
	}

	public int getSmapleNum() {
		return smapleNum;
	}

	public void setSmapleNum(int smapleNum) {
		this.smapleNum = smapleNum;
	}

	public int[][] getSubGDoc3() throws IOException {
		int size = this.graph.getNodeTable().size();
		Node[] array = new Node[size];
		int index = 0;
		for (Iterator it = this.graph.getNodeTable().entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<String, Node> map = (Entry<String, Node>) it.next();
			array[index] = map.getValue();
			index++;
		}

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length - i - 1; j++) {
				if (array[j].getAdjList().size() < array[j + 1].getAdjList()
						.size()) {
					Node tmp = array[j];
					array[j] = array[j + 1];
					array[j + 1] = tmp;
				}
			}
		}
		HashSet set = new HashSet();
		HashSet adjSet = new HashSet();
		for (Node node : array) {
			if (!adjSet.contains(node.getId())) {
				set.add(node.getId());
				adjSet.add(node.getId());
				System.out.println("sample:" + node.getId());
				if (set.size() > this.smapleNum * 0.4) {
					System.out.println("break");
					break;
				}
				for (String adj : node.getAdjList()) {
					adjSet.add(adj);
				}
			}
		}
		System.out.println("MCS size:" + set.size());
		// ///////////////////////////////////

		bfs(set);

		int[][] doc = new int[set.size()][];
		int dCnt = 0;
		for (Iterator it = set.iterator(); it.hasNext();) {
			String id = (String) it.next();
			if(id.equals("34")){
				int a=0;
			}
			HashSet subG = this.getSubGraphSA(id);
			doc[dCnt] = new int[subG.size()];
			int wCnt = 0;
			for (Iterator n = subG.iterator(); n.hasNext();) {
				doc[dCnt][wCnt] = Integer.parseInt((String) (n.next()));
				wCnt++;
			}
			dCnt++;

		}
		System.out.println("总数:" + this.graph.getNodeTable().size());
		System.out.println(" sampleNum" + smapleNum);
		System.out.println(set.size());
		return doc;

	}

	public int[][] getSubGDoc() throws IOException {

		int size = this.graph.getNodeTable().size();

		if (this.smapleNum > size * 0.4) {
			this.minDegree = (int) ((1 - this.smapleNum * 1.0 / size) * this.minDegree);
		}

		String startNode = graph.getRondomNode();
		Stack stack = new Stack();
		HashSet set = new HashSet();
		HashSet mark = new HashSet();
		set.add(startNode);
		stack.push(startNode);
		while (set.size() < this.smapleNum * 0.6) {
			String node = (String) stack.peek();
			double ratio = Double.MAX_VALUE;
			String nextNode = null;
			for (String adj : graph.getAdjList(node)) {
				if (mark.contains(adj) == false) {
					int degree = graph.getAdjList(adj).size();
					int edges = 0;
					for (String adj2 : graph.getAdjList(adj)) {
						if (set.contains(adj2)) {
							edges++;
						}
					}
					double ratio1 = edges * 1.0 / (degree * 1.0);
					if (ratio1 < ratio) {

						ratio = ratio1;
						nextNode = adj;
					}
				}

			}
			if (nextNode == null) {
				stack.pop();
			} else {
				stack.push(nextNode);
				mark.add(nextNode);
				if (this.graph.getAdjList(nextNode).size() > this.minDegree) {
					set.add(nextNode);
				}
				System.out.println(nextNode);
			}
		}
		// ///////////////////////////////////

		bfs(set);

		int[][] doc = new int[set.size()][];
		int dCnt = 0;
		for (Iterator it = set.iterator(); it.hasNext();) {
			String id = (String) it.next();
			HashSet subG = this.getSubGraph(id);
			doc[dCnt] = new int[subG.size()];
			int wCnt = 0;
			for (Iterator n = subG.iterator(); n.hasNext();) {
				doc[dCnt][wCnt] = Integer.parseInt((String) (n.next()));
				wCnt++;
			}
			dCnt++;

		}
		System.out.println("总数:" + this.graph.getNodeTable().size());
		System.out.println(" sampleNum" + smapleNum);
		System.out.println(set.size());
		return doc;

	}

	public int[][] getSubGDoc2() throws IOException {

		String startNode = graph.getRondomNode();
		Stack stack = new Stack();
		HashSet set = new HashSet();
		set.add(startNode);
		stack.push(startNode);

		while (set.size() < this.smapleNum * 0.6) {
			String node = (String) stack.peek();
			double ratio = Double.MAX_VALUE;
			String nextNode = null;
			for (String adj : graph.getAdjList(node)) {
				if (set.contains(adj) == false) {
					int degree = graph.getAdjList(adj).size();
					int edges = 0;
					for (String adj2 : graph.getAdjList(adj)) {
						if (set.contains(adj2)) {
							edges++;
						}
					}
					double ratio1 = edges * 1.0 / (degree * 1.0);
					if (ratio1 < ratio) {

						ratio = ratio1;
						nextNode = adj;
					}
				}

			}
			if (nextNode == null) {
				stack.pop();
			} else {

				stack.push(nextNode);
				set.add(nextNode);
				System.out.println(nextNode);
			}
		}
		// ///////////////////////////////////

		bfs(set);

		int[][] doc = new int[set.size()][];
		int dCnt = 0;
		for (Iterator it = set.iterator(); it.hasNext();) {
			String id = (String) it.next();
			HashSet subG = this.getSubGraph(id);
			doc[dCnt] = new int[subG.size()];
			int wCnt = 0;
			for (Iterator n = subG.iterator(); n.hasNext();) {
				doc[dCnt][wCnt] = Integer.parseInt((String) (n.next()));
				wCnt++;
			}
			dCnt++;

		}
		System.out.println("总数:" + this.graph.getNodeTable().size());
		System.out.println(" sampleNum+" + smapleNum);
		System.out.println(set.size());
		return doc;

	}

	private HashSet getSubGraph(String id) {

		HashSet set = new HashSet();
		set.add(id);
		for (String adj : this.graph.getAdjList(id)) {
			set.add(adj);
			for (String adjadj : this.graph.getAdjList(adj)) {
				set.add(adjadj);
			}
		}
		return set;
		// return this.getSubGraphSA(id);
	}

	private HashSet getSubGraphSA(String id) {
		
		if(id==null){
			System.out.println("input null");
		}

		String startNode = id;
		Stack stack = new Stack();
		HashSet set = new HashSet();
		set.add(startNode);
		for (String adj : this.graph.getAdjList(startNode)) {
			set.add(adj);
		}
		stack.push(startNode);

		boolean isFinished = false;
		double T = 1.0;
		double alpha = 0.9;
		while (!isFinished) {
			double ratio = Double.MIN_VALUE;
			String nextNode = null;
			HashSet neigSet = this.getSetNeigh(set);
			for (Iterator it = neigSet.iterator(); it.hasNext();) {
				String nid = (String) it.next();
				int degree = this.graph.getAdjList(nid).size();
				
				int edges = 0;
				for (String adj : this.graph.getAdjList(nid)) {
					if (set.contains(adj)) {
						edges++;
					}
				}
				double ratio1 = edges * 1.0 / (degree * 1.0);
				if (ratio1 >ratio) {
					ratio = ratio1;
					nextNode = nid;
				}
			}
			double c1=this.getConduce(set);
			if(nextNode==null){
				break;
			}
			set.add(nextNode);
			if(set.size()>this.graph.getNodeTable().size()*0.4){
				isFinished=true;
				break;
			}
			double c2=this.getConduce(set);
			double difC=c1-c2;
			if(difC<0){
				System.out.println("zdifC:"+difC+" T:"+T+"difC/T:"+difC/T);
				if(Math.random()>Math.exp(difC/T)){
					isFinished=true;
					System.out.println("break");
					System.out.println(set.size());
				}
			}
			T=T*alpha;
			
		}
		// ///////////////////////////////////
		return set;
	}

	private HashSet getSetNeigh(HashSet set) {
		List<String> l = new ArrayList<String>();

		HashSet neighSet = new HashSet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			String id = (String) it.next();
			for (String adj : this.graph.getAdjList(id)) {
				if (!set.contains(adj)) {
					neighSet.add(adj);
				}
			}
		}
		return neighSet;
	}

	private double getConduce(HashSet set) {
		int inDegree = 0;
		int outDegree = 0;
		for (Iterator it = set.iterator(); it.hasNext();) {
			String id = (String) it.next();
			if(id==null){
				int a=0;
			}
			for (String adj : this.graph.getAdjList(id)) {
				if (set.contains(adj)) {
					inDegree++;
				} else {
					outDegree++;
				}
			}
		}
		return outDegree * 1.0 / (inDegree + outDegree);
	}

	private void bfs(HashSet sSet) {
		Queue<String> q = new LinkedList<String>();
		for (Iterator<String> it = sSet.iterator(); it.hasNext();) {
			// newSet.add(it.next());
			q.offer(it.next());
		}
		HashSet markSet = new HashSet();
		while (sSet.size() < this.smapleNum) {
			String id = q.poll();
			sSet.add(id);
			markSet.add(id);
			for (String adj : this.graph.getAdjList(id)) {
				if (!markSet.contains(adj)) {
					q.offer(adj);
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SubGGenerator s = new SubGGenerator();
		MyGraph g = new MyGraph();
		g.readGraphFile("c:/graph1.txt");
		s.setGraph(g);
		s.setSmapleNum((int) (s.graph.getNodeTable().size() * 0.3));

	}
}
