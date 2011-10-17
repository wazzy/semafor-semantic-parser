package edu.cmu.cs.lti.ark.fn.parsing;

import java.util.Arrays;

public class OverlapSlave implements Slave {

	public double[] mObjVals;
	public int[] mIndices;
	
	public DescComparator mDesc;
	
	public OverlapSlave(double[] objVals, 
						int[] indices) {
		mObjVals = objVals;
		mIndices = indices;
	}

	public double[] makeZUpdate(double rho, double[] us, double[] lambdas,
			double[] zs) {
		Double[] as = new Double[mIndices.length];
		for (int i = 0; i < as.length; i++) {
			double a = us[mIndices[i]] + 
					   (1.0 / rho) * (mObjVals[mIndices[i]] + lambdas[mIndices[i]]);
			as[i] = a;
		}
		Arrays.sort(as, mDesc);
		double[] sums = new double[as.length];
		Arrays.fill(sums, 0);
		sums[0] = as[0];
		for (int i = 1; i < as.length; i++) {
			sums[i] = sums[i-1] + as[i];
		}
		int tempRho = 0;
		for (int i = 0; i < as.length; i++) {
			double temp = as[i] - (1.0 / (double)(i+1)) * (sums[i] - 1.0);
			if (temp <= 0) {
				break;
			}
			tempRho = i;
		}
		double tau = (1.0 / (double)(tempRho+1)) * (sums[tempRho] - 1.0);
		double[] updZs = new double[mObjVals.length];
		Arrays.fill(updZs, 0);
		for (int i = 0; i < mIndices.length; i++) {
			updZs[mIndices[i]] = Math.max(as[i] - tau, 0);
		}
		return updZs;
	}
}