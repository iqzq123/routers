package model;

public class InvertedIndex {
	public int docId;
    public double weight;
	public InvertedIndex(int docId, double weight) {
		super();
		this.docId = docId;
		this.weight = weight;
	}
}
