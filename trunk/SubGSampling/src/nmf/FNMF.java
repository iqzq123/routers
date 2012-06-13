package nmf;
/**
 * �����ݵĿ��� NMF�汾
 * by yinxs
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.vecmath.*;

public class FNMF {
	private FGMatrix U = null;
	private FGMatrix V = null;
	private FGMatrix X = null;
	private int M;
	private int N;
	private int K;
	private int iteratenum;
	
	public FGMatrix getV() {
		return V;
	}
	
	public FNMF() {
		this.K = 2;
		this.iteratenum = 100;
		this.M = 28701;
		this.N = 28701;
//		this.M = 30;
//		this.N = 62;
		X = new FGMatrix(M,N); /* �ô���ʼ���������������Ϊ�� */
		U = new FGMatrix(M,K);
		V = new FGMatrix(N,K);
	}
	
	public FNMF(int k, int m, int n, int it) {
		this.K = k;
		this.iteratenum = it;
		this.M = m;
		this.N = n;
		X = new FGMatrix(M,N); /* �ô���ʼ���������������Ϊ�� */
		U = new FGMatrix(M,K);
		V = new FGMatrix(N,K);
	}
	
	public void load(int[][] d, int[][] w) {
		for(int i=0; i < d.length; ++i) {
			for(int j=0; j < d[i].length; ++j) {
				X.setElement(i, d[i][j], w[i][j]);
			}
		}
	}
	
	public void load(String fName) throws IOException {
		int size=0;
		FileReader fr = new FileReader(fName);
		BufferedReader br = new BufferedReader(fr);
		while(br.ready()) {
			String tmpLine = br.readLine();
			int i = Integer.parseInt(tmpLine.split(",")[0]);
			int j = Integer.parseInt(tmpLine.split(",")[1]);
			double w = Double.parseDouble(tmpLine.split(",")[2]);
			assert((i>=0)&&(i<=M));
			assert((j>=0)&&(j<=N));
			X.setElement(i, j, w);
			if(size++ % 1000 == 0) {
				System.out.println(size+"\tload data 1000");
			}
		}
		System.out.println("Load end.");
		br.close();
		fr.close();
	}
	
	private void RandomMatrix() {
		for(int i=0; i < U.getNumRow(); ++i) {
			for(int j=0; j < U.getNumCol(); ++j) {
				U.setElement(i, j, Math.random()*1);
			}
		}
		for(int i=0; i < V.getNumRow(); ++i) {
			for(int j=0; j < V.getNumCol(); ++j) {
				V.setElement(i, j, Math.random()*1);
//				if(V.getElement(i, j) == 0.0) {
//					System.out.println("Here");
//				}
			}
		}
	}
	
	/**
	 * ���ھ��� V ʵʩ��һ��
	 */
	private void normCol() {
		for(int i=0; i < V.getNumRow(); ++i) {
			double tmp = 0.0;
			for(int j=0; j < V.getNumCol(); ++j) {
				tmp += V.getElement(i, j);
			}
			for(int j=0; j < V.getNumCol(); ++j) {
				/* ����Ӻ�֮�󶼺�С����ô���Կ���ÿһ��Ԫ�ض���С���Ǿͻ����ֲ���� */
				if(tmp < FGMatrix.EPS) {
					continue;
				}
				V.setElement(i, j, 1.0*V.getElement(i, j)/tmp);
			}
		}
	}
	
	/**
	 * ���������������ؽ������
	 * 
	 * @param l
	 * @param r
	 * @return
	 */
	public FGMatrix DotDiv(FGMatrix l, FGMatrix r) {
		assert (l.getNumRow() == r.getNumRow());
		assert (l.getNumCol() == r.getNumCol());
		double tmp = 0.0;
		FGMatrix retval = new FGMatrix(l.getNumRow(), l.getNumCol());
		for (int i = 0; i < l.getNumRow(); ++i) {
			for (int j = 0; j < l.getNumCol(); ++j) {
				if (l.getElement(i, j) == 0.0) {
					continue;
				} else {
					tmp = (r.getElement(i, j) != 0) ? r.getElement(i, j)
							: 0.0000000001 / (r.getNumCol() * r.getNumRow());
					retval.setElement(i, j, 1.0 * l.getElement(i, j) / tmp);
				}

			}
		}
		return retval;
	}
	
	/**
	 * �������ˣ������ؽ������
	 * 
	 * @param l
	 * @param r
	 * @return
	 */
	public FGMatrix DotMul(FGMatrix l, FGMatrix r) {
		assert (l.getNumRow() == r.getNumRow());
		assert (l.getNumCol() == r.getNumCol());
		FGMatrix retval = new FGMatrix(l.getNumRow(), l.getNumCol());
		for (int i = 0; i < l.getNumRow(); ++i) {
			for (int j = 0; j < l.getNumCol(); ++j) {
				retval.setElement(i, j, 1.0 * l.getElement(i, j)
						* r.getElement(i, j));
			}
		}
		return retval;
	}

	
	private double getErr(FGMatrix l, FGMatrix r) {
		double retval = 0.0;
		for(int i=0; i < l.getNumRow(); ++i) {
			for(int j=0; j < l.getNumCol(); ++j) {
				retval += (Math.abs(l.getElement(i, j)-r.getElement(i, j)));
			}
		}
		return retval;
	}
	
	public void train() {
		FGMatrix XX = new FGMatrix(M,N);
		/* ��ʼ�� U V �������� */
		this.RandomMatrix();
		/* ��һ�� V ������� */
		this.normCol();
		/* ��ʼ���� */
		for(int it=0; it < this.iteratenum; it++) {
			System.out.println("������"+it);
			/* for U */
			FGMatrix XV = new FGMatrix(M,K);
			XV.mul(X,V);
			
			
			
			FGMatrix UVV = new FGMatrix(M,K);
			FGMatrix VV = new FGMatrix(K,K);
			VV.mulTransposeLeft(V, V);
			
			
			UVV.mul(U,VV);
			
			
			
			FGMatrix tmp1 = this.DotDiv(XV, UVV);
			
			
			
//			System.out.println("for U"+tmp1+"\n");
			U = this.DotMul(U, tmp1);
//			UV.mul(U, V);
//			XX = this.DotDiv(X, UV);
//			FGMatrix UU = new FGMatrix(M,K);
//			UU.mulTransposeRight(XX, V);
//			U = this.DotMul(U, UU);
			
			/* for V */
			FGMatrix XU = new FGMatrix(N,K);
			XU.mulTransposeLeft(X, U);
			
			
			FGMatrix VUU = new FGMatrix(N,K);
			FGMatrix UU = new FGMatrix(K,K);
			UU.mulTransposeLeft(U, U);
			
			
			
			VUU.mul(V,UU);
			
			
			
			FGMatrix tmp2 = this.DotDiv(XU, VUU);
			
			
			
		    GVector v1 = new GVector(tmp2.getNumCol());
		    tmp2.getColumn(0, v1);
			V = this.DotMul(V, tmp2);
			
			/* U ��һ�� */
			
			XX.mulTransposeRight(U, V);
			
			
			
			this.normCol();
			
			
			
			System.out.println("�����ʣ�"+this.getErr(X, XX));
		}
	}
	
	public void output(String f) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write(U.toString());
		writer.write("\n");
		writer.write(V.toString());
		writer.close();
	}
	
	public static void main(String[] args) throws IOException {
		long startTime=System.currentTimeMillis();
		FNMF test = new FNMF();
		test.load("D:/data/coPaper.txt");
//		test.load("C:/doc-word.txt");
		test.train();
		test.output("D:/data/UV.txt");
		long endTime=System.currentTimeMillis();
		System.out.println("��������ʱ�䣺 "+(endTime-startTime)+"ms"); 
	}
}
