package bak;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import svd.MaxK;

import Jama.Matrix;

public class CDRLSI {

	// record term t occur in doc d with a weight w
	public class TDIndex {

		int docId;

		public TDIndex(int docId, double weight) {
			super();
			this.docId = docId;
			this.weight = weight;
		}

		double weight;
	}

	// record the doc d contains term t with weight w
	public class DTIndex {
		public DTIndex(int termId, double weight) {
			super();
			this.termId = termId;
			this.weight = weight;
		}

		int termId;
		double weight;
	}

	private int termNum = 11771;// Term number
	private int docNum = 28569;// doc number
	private int demesion = 50;
	int trainNum = 50;

	private double lamda1 = 0.1;
	private double lamda2 = 0.1;
	private double alpha = 0.1;
	private double[][] termTopic = null;
	private double[][] docTopic = null;
	private double[][] S = null;
	private double[][] R = null;
	private double maxError = 0.0001;
	private List<TDIndex>[] invertedIndex = null;
	private List<DTIndex>[] doc = null;
	private String[] terms = null;
	private String[] docName = null;
	private String encoding;

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setDemesion(int demesion) {
		this.demesion = demesion;
	}

	public void setTrainNum(int trainNum) {
		this.trainNum = trainNum;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("CDRSLI");

		if (args.length < 3) {
			System.out.print("RLSI parameter error");
			CDRLSI r = new CDRLSI();
			r.setDemesion(50);
			r.setTrainNum(50);
			r.setEncoding("gbk");
			String infile="d:/download/wenku.context.100";
			r.readMatrix(infile);
			r.train();
			r.printDoc(infile+".doc");
			return;
		}
		String infile = args[0];
		CDRLSI r = new CDRLSI();
		r.setDemesion(Integer.parseInt(args[1]));
		String encoding=args[3];
		r.setEncoding(encoding);
		r.readMatrix(infile);
		r.setTrainNum(Integer.parseInt(args[2]));
		r.train();
//		File outfile = new File(infile + ".doc");
//		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));
		r.printDoc(infile + ".doc");

//		File outfile2 = new File(infile + ".term");
//		BufferedWriter w2 = new BufferedWriter(new FileWriter(outfile2));
		r.printWord(infile + ".term");


	}

	public void readMatrix(String dtFile) throws IOException {
		
		InputStreamReader read = new InputStreamReader(new FileInputStream(dtFile),encoding); 
		BufferedReader reader=new BufferedReader(read); 

		// replace term and doc Id
		String tempString = null;

		int cnt = 0;
		Hashtable<String, Integer> termDict = new Hashtable<String, Integer>();
		Hashtable<String, Integer> docDict = new Hashtable<String, Integer>();

		int docId = 0;
		int termId = 0;
		while ((tempString = reader.readLine()) != null) {
			cnt++;
			if (cnt % 100000 == 0) {
				System.out.println("read");
			}
			String[] strArray = tempString.split("\t");
			if (docDict.get(strArray[0]) == null) {
				docDict.put(strArray[0], docId);
				docId++;
			}
			if (termDict.get(strArray[1]) == null) {
				termDict.put(strArray[1], termId);
				termId++;
			}
		}
		reader.close();
		System.out.println("doc size:"+docId);
		System.out.println("term size:"+termId);
		// initialize vector
		intialVector(termId, docId, this.demesion);
		// read doc and build invertedIndex
		read = new InputStreamReader(new FileInputStream(dtFile),encoding); 
		reader=new BufferedReader(read); 
		this.docName = new String[docId];
		this.terms = new String[termId];
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
			doc[dId].add(new DTIndex(tId, weight));
			this.invertedIndex[tId].add(new TDIndex(dId, weight));
		}
		reader.close();

	}

	private void intialVector(int m, int n, int k) {
		this.termNum = m;
		this.docNum = n;
		this.demesion = k;
		termTopic = new double[m][k];
		docTopic = new double[n][k];
		R = new double[m][k];
		S = new double[k][k];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < k; j++) {
				termTopic[i][j] = Math.random() * 1;
			}
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < k; j++) {
				docTopic[i][j] = Math.random() * 1;
			}
		}
		this.invertedIndex = new ArrayList[termNum];
		for (int i = 0; i < termNum; i++) {
			this.invertedIndex[i] = new ArrayList<TDIndex>();
		}
		this.doc = new ArrayList[docNum];
		for (int i = 0; i < docNum; i++) {
			this.doc[i] = new ArrayList<DTIndex>();
		}

	}

	public double dotProduct(double[] array1, double[] array2) {
		double ret = 0.0;
		for (int i = 0; i < array1.length; i++) {
			ret += array1[i] * array2[i];
		}
		return ret;
	}

	public double getError() {
		double error = 0.0;
		int cnt = 0;
		for (int n = 0; n < this.docNum; n++) {
			for (DTIndex t : this.doc[n]) {
				double a = dotProduct(this.termTopic[t.termId],
						this.docTopic[n]);
				error += (a - t.weight) * (a - t.weight);
				cnt++;
			}
		}
		return error / cnt;
	}

	public void train() {

		for (int t = 0; t < trainNum; t++) {

			System.out.println("train");
			System.out.println("doc erro:" + this.getError());
			// update U
			// compute S
			computeS();
			// compute R
			computeR();
			for (int m = 0; m < termNum; m++) {
				// u_{m}=0;
				for (int k = 0; k < demesion; k++) {
					this.termTopic[m][k] = 0.0;
				}
				boolean convergence = false;
				int maxCnt = 0;
				while (!convergence) {
					maxCnt++;
					if (maxCnt > 100) {
						break;
					}
					double[] preWT = new double[demesion];
					for (int k = 0; k < demesion; k++) {
						preWT[k] = this.termTopic[m][k];
					}

					for (int k = 0; k < demesion; k++) {
						double product = 0.0;
						for (int l = 0; l < demesion; l++) {
							if (l != k) {
								product += S[k][l] * this.termTopic[m][l];
							}
						}

						double w_mk = R[m][k] - product;
						this.termTopic[m][k] = Math.max(0,
								(Math.abs(w_mk) - 0.5 * lamda1))
								* sign(w_mk) / S[k][k];

					}
					double error = 0.0;
					for (int k = 0; k < demesion; k++) {
						error += Math.abs(preWT[k] - this.termTopic[m][k]);
					}

					if (error < this.maxError) {
						convergence = true;
					}
				}

			}
			// update V with gradient method

			System.out.println("update V:" + t);
			updateV();
		}
		for (int i = 0; i < demesion; i++) {
			this.outputTopic(i);
		}

	}

	public void printDoc(String infile) throws IOException {
//		String fileEncode = System.getProperty("file.encoding");
//		OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(
//				infile), fileEncode);
		OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(infile),encoding); 
		BufferedWriter ow=new BufferedWriter(write); 
		for (int i = 0; i < this.docTopic.length; i++) {
			String line = this.docName[i] + "\t";
			for (int k = 0; k < this.docTopic[i].length; k++) {
				line += this.docTopic[i][k] + "\t";
			}
			line += "\n";
			ow.write(line);

		}
		ow.flush();
		ow.close();
	}

	public void printWord(String infile) throws IOException {
		OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(infile),this.encoding); 
		BufferedWriter ow=new BufferedWriter(write); 
		for (int i = 0; i < this.termTopic.length; i++) {
			String line = this.terms[i] + "\t";
			for (int k = 0; k < this.termTopic[i].length; k++) {
				line += this.termTopic[i][k] + "\t";
			}
			line += "\n";
			ow.write(line);
			
		}
		ow.flush();
		ow.close();
	}

	public void outputTopic(int k) {
		double[] array = new double[20];
		double[] wordArray = new double[termNum];
		for (int i = 0; i < termNum; i++) {
			wordArray[i] = this.termTopic[i][k];
		}
		array = MaxK.getMaxK(wordArray, 20);
		double min = array[0];
		int[] wordId = new int[20];
		String[] keywords = new String[20];
		int index = 0;
		for (int i = 0; i < termNum; i++) {
			if (wordArray[i] >= min) {
				// keywords[index]=this.words[i];
				wordId[index] = i;
				index++;
			}
		}
		// sort words
		for (int i = 0; i < 20 - 1; i++) {
			for (int j = 0; j < 20 - i - 1; j++) {
				if (this.termTopic[wordId[j]][k] < this.termTopic[wordId[j + 1]][k]) {
					int tmp = wordId[j];
					wordId[j] = wordId[j + 1];
					wordId[j + 1] = tmp;
				}
			}
		}
		System.out.println("topic: " + k);
		for (int i = 0; i < 20; i++) {
			System.out.println(this.terms[wordId[i]] + "	"
					+ this.termTopic[wordId[i]][k]);
		}
	}

	public void updateV() {

		// ////////////////update V
		double[][] II = new double[demesion][demesion];
		for (int i = 0; i < demesion; i++) {
			for (int j = 0; j < demesion; j++) {
				if (i == j) {
					II[i][j] = lamda2;
				}
			}
		}
		Matrix I = new Matrix(II);
		Matrix U = new Matrix(this.termTopic);
		Matrix UU = U.transpose().times(U);
		UU.plusEquals(I);
		Matrix Z = UU.inverse();

		double[][] O = new double[demesion][docNum];
		for (int k = 0; k < demesion; k++) {
			for (int n = 0; n < docNum; n++) {
				double product = 0.0;
				// for (int m = 0; m < M; m++) {
				// this.wordTopic[m][k]*this.
				// }
				for (DTIndex t : this.doc[n]) {
					product += t.weight * this.termTopic[t.termId][k];
				}
				O[k][n] = product;
			}
		}

		for (int n = 0; n < docNum; n++) {
			double[] OClo = new double[demesion];
			for (int k = 0; k < demesion; k++) {
				OClo[k] = O[k][n];
			}
			for (int k = 0; k < demesion; k++) {
				this.docTopic[n][k] = dotProduct(Z.getArray()[k], OClo);
			}
		}
	}

	private double sign(double val) {
		if (val > 0) {
			return 1.0;
		} else {
			return -1.0;
		}
	}

	private void computeR() {
		for (int m = 0; m < termNum; m++) {
			for (int k = 0; k < demesion; k++) {
				double dotProduct = 0.0;
				for (TDIndex tw : this.invertedIndex[m]) {
					dotProduct += this.docTopic[tw.docId][k] * tw.weight;
				}
				R[m][k] = dotProduct;
			}
		}
	}

	private void computeS() {
		for (int i = 0; i < demesion; i++) {
			for (int j = 0; j < demesion; j++) {
				double dotProduct = 0.0;
				for (int index = 0; index < docNum; index++) {
					dotProduct += docTopic[index][i] * docTopic[index][j];
				}
				S[i][j] = dotProduct;
			}
		}
	}

}
