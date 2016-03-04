package org.kernels;

public class KernelFactory {
	
	/**
	 * Returns the object for the chosen kernel
	 * 
	 * @param kernel The name of the chosen kernel
	 * @return The object for the chosen kernel ( Gaussian by default )
	 */
	public static Kernel createKernel( String kernel ) {
	
		switch ( kernel.toLowerCase() ) {
			case "gaussian" :
				return new Gaussian();
			case "epanechnikov" :
				return new Epanechnikov();
			default:
				return new Gaussian();
		}
	}

}
