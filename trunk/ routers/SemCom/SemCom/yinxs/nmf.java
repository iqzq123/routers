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

	public void randomV() {
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < K; ++j) {
				Vv[i].setElement(j, Math.random() * 1);
			}
		}
	}

	public void load(String fName) throws IOException {
		FileReader fr = new FileReader(fName);
		BufferedReader br = new BufferedReader(fr);
		while (br.ready()) {
			String tmpLine = br.readLine();
			int docId = Integer.parseInt(tmpLine.split(",")[1]);
			int termId = Integer.parseInt(tmpLine.split(",")[0]);
			double weight = Double.parseDouble(tmpLine.split(",")[2]);
			this.Xm.add(new MatElement(docId, termId, weight));
		}
		br.close();
		fr.close();
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
	}

	public void train() throws IOException {
		this.randomV();
		this.load("mdt.txt");
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

	public static void main(String[] args) throws IOException {
		nmf test = new nmf();
		test.train();
	}
}
