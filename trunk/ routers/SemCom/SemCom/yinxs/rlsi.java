/**
 * @file rlsi.java
 * @brief Regularized Latent Semantic Indexing
 * @details It will extract latent topics in some documents.
 * @author yinxs, yinxusen@gmail.com
 * @date 2012-3-26
 */

package math_yinxs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.vecmath.*;

public class rlsi {
	public int M_terms;
	public int N_docs;
	public int K_topics;
	public GMatrix D_term_doc;
	public GMatrix U_term_topic;
	public GMatrix V_topic_doc;
	public GMatrix U_tmp;
	public double lamda1, lamda2;
	private boolean D_initialized;
	public static final double epsion = 0.0001;
	
	/**
	 * @brief Construct function. This is not the only one, you should
	 * call load() to initialize some other values before using the class.
	 * @param m: number of terms
	 * @param n: number of documents
	 * @param k: number of topics
	 */
	public rlsi(int m, int n, int k) {
		this.M_terms = m;
		this.N_docs = n;
		this.K_topics = k;
		this.D_term_doc = new GMatrix(M_terms, N_docs);
		this.U_term_topic = new GMatrix(M_terms, K_topics, 0.0);
		this.V_topic_doc = new GMatrix(K_topics, N_docs);
		this.U_tmp = new GMatrix(M_terms, K_topics, 0.0);
		this.D_initialized = false;
		this.lamda1 = this.lamda2 = 0.0;
	}
	
	/**
	 * @throws IOException 
	 * @brief Some things should be initialized.
	 * e.g. matrix D, lamda, m, n, k ...
	 * D_initialized will be changed if load success.
	 * You should not call other functions before load().
	 */
	public void load(String fName, double l1, double l2) throws IOException {
		FileReader fr = new FileReader(fName);
		BufferedReader br = new BufferedReader(fr);
		while(br.ready()) {
			String tmpLine = br.readLine();
			int row = Integer.parseInt(tmpLine.split(",")[0]);
			int col = Integer.parseInt(tmpLine.split(",")[1]);
			double val = Double.parseDouble(tmpLine.split(",")[2]);
			this.D_term_doc.set(val, row, col);
		}
		this.lamda1 = l1;
		this.lamda2 = l2;
		this.D_initialized = true;
	}

	/**
	 * @brief update U matrix, details in paper.
	 */
	private void updateU() {
		GMatrix s = new GMatrix(K_topics, K_topics);
		s.mulTransposeRight(V_topic_doc, V_topic_doc);
		GMatrix r = new GMatrix(M_terms, K_topics);
		r.mulTransposeRight(D_term_doc, V_topic_doc);
		
		int m, k;
		for(m=0; m < this.M_terms; ++m) {
			for(k=0; k < this.K_topics; ++k) {
				this.U_term_topic.set(0.0, m, k);
			}
			while(true) {
				U_tmp.set(U_term_topic);
				for(k=0; k < this.K_topics; ++k) {
					double tmp = 0.0;
					for(int l = 0; l < this.K_topics; ++l) {
						if(l != k) {
							tmp += 
									(s.getElement(k, l)*this.U_term_topic.getElement(m, l));
						}
					}
					double W_mk = r.getElement(m, k) - tmp;
					tmp = (Math.abs(W_mk) - 0.5*this.lamda1);
					tmp = tmp > 0 ? tmp : 0;
					this.U_term_topic.setElement(
							m, k, 
							(tmp + Math.signum(W_mk))/s.getElement(k, k)); 
				}
				if(U_tmp.epsilonEquals(U_term_topic, epsion)) {
					break;
				}
			}
		}
	}
	
	/**
	 * @brief update V matrix, details in paper.
	 */
	private void updateV() {
		GMatrix sigma = new GMatrix(K_topics, K_topics);
		GMatrix phi = new GMatrix(K_topics, N_docs);
		GMatrix tmp1 = new GMatrix(K_topics, K_topics);
		tmp1.mulTransposeLeft(U_term_topic, U_term_topic);
		GMatrix tmp2 = new GMatrix(K_topics, K_topics, this.lamda2);
		tmp1.add(tmp2);
		sigma.invert(tmp1);
		phi.mulTransposeLeft(U_term_topic, D_term_doc);
		int n;
		for(n=0; n < N_docs; ++n) {
			GVector Vn = new GVector(K_topics);
			GVector phin = new GVector(K_topics);
			phi.getColumn(n, phin);
			Vn.mul(sigma, phin);
			this.V_topic_doc.setColumn(n, Vn);
		}
	}

	/**
	 * @brief output function.
	 * It will output the U and V matrix we want to solve.
	 */
	private void output() {
		System.out.println("U is :");
		System.out.println(U_term_topic);
		System.out.println("V is :");
		System.out.println(V_topic_doc);
	}
	
	/**
	 * @brief Main function of rlsi algorithm.
	 * It will call updateU() and updateV().
	 */
	public void rLSI() {
		if(!this.D_initialized) {
			System.err.print("You should init document first.");
			return;
		}
	 for(int i = 0; i < 10; ++i) {
			this.updateU();
			this.updateV();
		}
	 this.output();
	}
	
	public static void main(String[] args) throws IOException {
		int m = 11771,n = 28569,k = 30;
		rlsi test = new rlsi(m,n,k);
		test.load("mdt.txt", 1, 1);
		test.rLSI();
	}
}
