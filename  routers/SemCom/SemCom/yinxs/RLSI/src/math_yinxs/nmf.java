package math_yinxs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.*;

public class nmf {
	GVector Uv[];
	GVector Vv[];
	List<MatElement> Xm;
	int M = 11771;
	int N = 28569;
	int K = 4;
	private String[] words = new String[12000];
	public nmf() {
		this.Xm = new ArrayList<MatElement>();

		this.Uv = new GVector[M];
		for (int i = 0; i < M; ++i) {
			this.Uv[i] = new GVector(K);
		}
		this.Vv = new GVector[N];
		for (int i = 0; i < N; ++i) {
			this.Vv[i] = new GVector(K);
		}
	}

    public void randomU() {
    	for(int i=0; i < M; ++i) {
    		for(int j=0; j < K; ++j) {
    			Uv[i].setElement(j, Math.random() * 1);
    		}
    	}
    }
	public void randomV() {
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < K; ++j) {
				Vv[i].setElement(j, Math.random() * 1);
			}
		}
	}

	public void load(String fName, String fTerm) throws IOException {
		FileReader fr = new FileReader(fName);
		BufferedReader br = new BufferedReader(fr);
        FileReader fr2 = new FileReader(fTerm);
        BufferedReader br2 = new BufferedReader(fr2);
		while (br.ready()) {
			String tmpLine = br.readLine();
			int docId = Integer.parseInt(tmpLine.split(",")[1]);
			int termId = Integer.parseInt(tmpLine.split(",")[0]);
			double weight = Double.parseDouble(tmpLine.split(",")[2]);
			this.Xm.add(new MatElement(docId, termId, weight));
		}
		br.close();
		fr.close();
        int i = 0;
		while(br2.ready()) {
			words[i++] = br2.readLine();
		}
        br2.close();
        fr2.close();
	}

	public void updateU(RowElement mt) {
		GVector ui = Uv[mt.i]; // 每次更新一组 u，一行u
		int j = 0;
		for (; j < this.K; ++j) {
			double uij = ui.getElement(j);
			double vj[] = new double[N];
			for (int v_cnt = 0; v_cnt < N; ++v_cnt) {
				vj[v_cnt] = Vv[v_cnt].getElement(j);
			}
			GVector gvj = new GVector(vj); // v的第j行放到向量中
			GVector ggvj = new GVector(vj);
			double up = gvj.dot(mt.xi); // 乘以这组数据，第i行，里面有 N 个数据
			double vtv[] = new double[K];
			for (int k = 0; k < K; ++k) {
				for (int v_cnt = 0; v_cnt < N; ++v_cnt) {
					vj[v_cnt] = Vv[v_cnt].getElement(k);
				}
				GVector tmp = new GVector(vj);
				vtv[k] = tmp.dot(ggvj);
			}
			GVector gvtv = new GVector(vtv);
			double down = ui.dot(gvtv);
			Uv[mt.i].setElement(j, uij * up / down);
		}
        // Just for test
        System.out.println(Uv[mt.i]);
	}

	// 这次的 mt 应该是一列数据，应该怎么算？
	// 先假设 mt中 i是列，mt的数据是一列数据，可以通过两个链表实现。
	public void updateV(ColElement mt) {
		int i = mt.j; // v的第i 行
		GVector vi = Vv[i];
		int k = 0;
		// 把v的第i行全部更新
		for (; k < this.K; ++k) {
			double vij = vi.getElement(k);
			// 取U的第k列
			double uk[] = new double[this.M];
			for (int u_cnt = 0; u_cnt < this.M; ++u_cnt) {
				uk[u_cnt] = Uv[u_cnt].getElement(k);
			}
			GVector guk = new GVector(uk);
			GVector gguk = new GVector(uk);
			double up = guk.dot(mt.xj);
			double utu[] = new double[this.K];
			for (int k_cnt = 0; k_cnt < this.K; ++k_cnt) {
				for (int u_cnt = 0; u_cnt < this.M; ++u_cnt) {
					uk[u_cnt] = Uv[u_cnt].getElement(k_cnt);
				}
				GVector tmp = new GVector(uk);
				utu[k_cnt] = tmp.dot(gguk);
			}
			GVector gutu = new GVector(utu);
			double down = vi.dot(gutu);
			Vv[mt.j].setElement(k, vij * up / down);
		}
        // Just for test
		System.out.println(Vv[mt.j]);
	}

	public void train() throws IOException {
		this.randomV();
        this.randomU();
		this.load("mdt.txt","term.txt");
        RowElement re = new RowElement();
        re.xi = new GVector(this.N);
        for(int i=0; i < this.M; ++i) {
           re.i = i;
           for(MatElement mt : Xm) {
               if(mt.getI() == i) {
            	  re.xi.setElement(mt.getJ(), mt.getVal());
               }
           }
           this.updateU(re);
           System.out.println("updateU\t"+i);
        }
        ColElement ce = new ColElement();
        ce.xj = new GVector(this.M);
        for(int j=0; j < this.N; ++j) {
        	ce.j = j;
        	for(MatElement mt : Xm) {
                if(mt.getJ() == j) {
                    if(mt.getI() >= this.M) {
                    	System.out.println("Here");
                    }
                	ce.xj.setElement(mt.getI(), mt.getVal());
                }
        	}
            this.updateV(ce);
            System.out.println("updateV\t"+j);
        }
	}
    
	public double[] getMaxK(double[] array, int k) {
		double[] topArray = new double[k];
		for (int i = 0; i < k; i++) {
			topArray[i] = array[i];
		}
		// build heap
		for (int i = k / 2 - 1; i >= 0; i--) {
			HeapAdjust(topArray, i, k - 1);
		}
		for (int i = k; i < array.length; i++) {
			if (array[i] > topArray[0]) {
				topArray[0] = array[i];
				HeapAdjust(topArray, 0, k - 1);
			}
		}
		return topArray;
	}

	private void HeapAdjust(double[] toparray, int top, int len) {
		double tmp = toparray[top];
		for (int i = top * 2; i <= len; i *= 2) {
			if (toparray[i] > toparray[i + 1] && i < len) {
				i++;
			}
			if (tmp > toparray[i]) {
				toparray[top] = toparray[i];
				top = i;
			} else {
				break;
			}
		}
	}

	public void outPutTopics() {
		for (int a = 0; a < this.K; a++) {
			double[] array = new double[this.Vv.length];
			for (int i = 0; i < this.Vv.length; i++) {
				array[i] = this.Vv[i].getElement(a);
//				if(this.VVector[i].getElement(a) == Double.NaN) {
//					System.out.println("fuck!");
//				}
			}
			double minValue = this.getMaxK(array, 20)[0];
			String[] keywords = new String[20];
			int index = 0;
			for (int i = 0; i < this.Vv.length; i++) {
				if (this.Vv[i].getElement(a) > minValue) {
					keywords[index] = this.words[i];
					index++;
					if(index == 19) {
						break;
					}
				}
			}
			System.out.println("topic" + a + ":");
			for (int i = 0; i < keywords.length; i++) {
				System.out.println(keywords[i]);
			}
		}
	}
	public static void main(String[] args) throws IOException {
		nmf test = new nmf();
		test.train();
		test.outPutTopics();
	}
}
