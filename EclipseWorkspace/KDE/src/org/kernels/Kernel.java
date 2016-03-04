/**
 *  Interface with the method to be implemented by all kernels
 */
package org.kernels;

/**
 * @author Rafael Kaustchr
 *
 */
public interface Kernel {	
	/**
	 * 
	 * @param u { u = ( x - mean ) / bandwidth }
	 * @return 
	 */
	public double execute( double u );
	
}
