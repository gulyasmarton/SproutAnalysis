package eu.bioimage.celltools.general.datamanaging;

import java.awt.geom.Point2D;
import java.util.List;

/******************************************************************************
 * Compilation: javac LinearRegression.java StdIn.java Execution: java
 * LinearRegression < data.txt
 * 
 * Reads in a sequence of pairs of real numbers and computes the best fit (least
 * squares) line y = ax + b through the set of points. Also computes the
 * correlation coefficient and the standard errror of the regression
 * coefficients.
 *
 * Note: the two-pass formula is preferred for stability.
 *
 ******************************************************************************/

public class LinearRegression {

	double[] x;
	double[] y;
	
	
	
	double beta1;
	double beta0;
	double  R2;
	double rss = 0.0; // residual sum of squares
	double ssr = 0.0; // regression sum of squares
	double yybar = 0.0 ;

	public LinearRegression(List<Point2D> points) {
		super();
		this.x = new double[points.size()];
		this.y = new double[points.size()];

		for (int i = 0; i < x.length; i++) {
			Point2D p = points.get(i);
			x[i] = p.getX();
			y[i] = p.getY();
		
		}

		Execute();
	}

	private void Execute() {
		int n = x.length;
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
		
		for (int i = 0; i < x.length; i++) {
			sumx += x[i];
			sumx2 += x[i] * x[i];
			sumy += y[i];
		}
		
		double xbar = sumx / n;
		double ybar = sumy / n;

		// second pass: compute summary statistics
		double xxbar = 0.0,  xybar = 0.0;
		for (int i = 0; i < n; i++) {
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			yybar += (y[i] - ybar) * (y[i] - ybar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}
		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;
		


		// analyze results
		int df = n - 2;
		rss = 0.0; // residual sum of squares
		ssr = 0.0; // regression sum of squares
		for (int i = 0; i < n; i++) {
			double fit = beta1 * x[i] + beta0;
			rss += (fit - y[i]) * (fit - y[i]);
			ssr += (fit - ybar) * (fit - ybar);
		}
		R2 = ssr / yybar;
		double svar = rss / df;
		double svar1 = svar / xxbar;
		double svar0 = svar / n + xbar * xbar * svar1;
		
		// StdOut.println("std error of beta_1 = " + Math.sqrt(svar1));
		// StdOut.println("std error of beta_0 = " + Math.sqrt(svar0));
		// svar0 = svar * sumx2 / (n * xxbar);
		// StdOut.println("std error of beta_0 = " + Math.sqrt(svar0));

		
	}

	//y  = ax + b 
	public String getLineEquation(){
		return "y   = " + beta1 + " * x + " + beta0;
	}
	
	public double getSlope(){
		return beta1;
	}
	
	public double getR2(){
		return R2;
	}
	
	public double getSSTO(){
		return yybar;
	}
	public double getSSE(){
		return rss;
	}
	public double getSSR(){
		return ssr;
	}
	
	
	
	
	public static void main(String[] args) {

		
	}
}
