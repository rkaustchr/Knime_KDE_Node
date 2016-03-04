package org.kernels;

/*
 * Gaussian Kernel
 * 
 * Equation: y = ( 1 / sqrt( 2 * pi ) ) * exp( -0.5 * ( ( x - media ) ^ 2 ) );
 */
public class Gaussian implements Kernel {
	
	@Override
	public double execute(double u) {
		return  ( 1 / Math.sqrt( 2 * Math.PI ) ) * Math.exp( -0.5 * Math.pow( u , 2) );
	}
	
}
