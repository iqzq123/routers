package nmf;

import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private String id;
	private String data;
	private double weight;
	private List<String> adjList=new ArrayList<String>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getAdjList() {
		return adjList;
	}
	public void setAdjList(List<String> adjList) {
		this.adjList = adjList;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getData() {
		return data;
	}
	public boolean isNeighbor(String dot) {
		return this.adjList.contains(dot);
	}
	public void setWeight(double w) {
		this.weight = w;
	}
	public double getWeight() {
		return this.weight;
	}
}
