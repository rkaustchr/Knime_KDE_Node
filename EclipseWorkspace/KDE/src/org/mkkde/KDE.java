package org.mkkde;

import org.kernels.Kernel;
import org.kernels.KernelFactory;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;

/**
 * @author Rafael Kaustchr
 *
 *	Class with the KDE logic
 */
public class KDE {
	private Kernel kernel;
	private final ExecutionContext exec; // Variable to handle the comunication with Knime framework
	
	public KDE( String kernel, final ExecutionContext exec ) {
		this.kernel = KernelFactory.createKernel(kernel);
		this.exec = exec;
	}
	
	/**
	 * 
	 * @param testingMatrix input matrix with the testing values, where the KDE will by applied.
	 * @param trainingMatrix input matrix with the training value, where KDE will get the PFD.
	 * @param bandwidth input vector with the bandwidth value to each dimension of trainingMatrix
	 * 
	 * @return the vector p with the calculated KDE for each variable of testingMatrix
	 * 
	 * @throws CanceledExecutionException received from Knime framework, to stop the calculation
	 */
	public double[][] calculate(double[][] testingMatrix, double[][] trainingMatrixB, double[] bandwidth) throws CanceledExecutionException {
		int i, j;
		int n = testingMatrix.length;
		int m = trainingMatrixB.length;
		int d = testingMatrix[0].length;
		double[][] p = new double[n][d]; // The result
		double somatorio;
		
		for ( j=0; j < d; j++ ) {
			for ( i=0; i < n; i++ ) {
				// check if the execution monitor was canceled
	            exec.checkCanceled();
	            exec.setProgress( (i*j) / (double)(testingMatrix.length*d), "% Computing element " + i);
	            
	            somatorio = 0;
	    		for (int k=0; k < m; k++ ) {	    				    			
	    			somatorio += kernel.execute( ( testingMatrix[i][j] - trainingMatrixB[k][j] ) / (double) bandwidth[j] );
	    		}
	    		
				p[i][j] = ((double)1/(m * bandwidth[j])) * somatorio;
			}
		}
		
		return p;
	}
	
}
