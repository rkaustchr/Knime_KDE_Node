package org.kernels;

/*
 * Epanechnikov Kernel
 * 
 * Equation: if u <= 1 : y = ( 3 / 4 ) * ( 1 - u ); 
 * 			 else      : y = 1;
 */
public class Epanechnikov implements Kernel {

	@Override
	public double execute(double u) {
		double kernel;
		
		if ( u > 1 ) {
			kernel = 1;
		} else {
			kernel =  ( 3 / (double) 4) * ( 1 - Math.pow(u, 2) );
		}
		
		return kernel;
	}

}
