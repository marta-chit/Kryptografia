import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

class PrimeGenerator {	
	private int d;
	private int k;
	private BigInteger[] primes;
	private GeneratorThread[] threads;
	PrimeGenerator (int d, int k) {
		this.d = d;
		this.k = k;
		primes = new BigInteger[k];
		threads = new GeneratorThread[k];
		for(int i = 0; i < k; i++)
			threads[i] = new GeneratorThread(i, d);
	}
	private class GeneratorThread extends Thread {
		private int d;
		private SecureRandom random;
		private int id;
		GeneratorThread(int id, int d) {
			this.id = id;
			this.d = d;
			random = new SecureRandom();
		}
		private boolean isPrime(BigInteger n) {
			if(n.compareTo(BigInteger.ZERO) == 0 || n.compareTo(BigInteger.ONE) == 0) {
				return false;
			}
			if(n.mod(new BigInteger("2")).compareTo(BigInteger.ZERO) == 0) {
				return false;
			}
			for(int i = 0; i < 100; i++) {
				BigInteger a = new BigInteger(d, random);	
				a = a.mod(n.subtract(BigInteger.ONE));
				while(a.compareTo(BigInteger.ONE) != 1 ) {
					a = new BigInteger(d, random);
				}
				if(a.modPow(n.subtract(BigInteger.ONE), n).compareTo(BigInteger.ONE) != 0) {
					return false;
				}
			}
			return true;
		}	
		public void run() {
			BigInteger n = new BigInteger(d, random);
			while(!isPrime(n)) {
				n = new BigInteger(d,random);
			}
			primes[id] = n;
		}	
	}
	public BigInteger[] gen() {
		for(int i = 0; i < k; i++) {
			threads[i].start();		
		}
		for(int i = 0; i < k; i++) {
			try{threads[i].join();}
			catch(Exception ex) {
				System.out.println("Threads interrupted");
				return null;
			}		
		}
		return primes;
	}
}


public class PrimeGeneratorTest {
	public static void main(String[] args) {
		int k, d;
		try{
			k = Integer.parseInt(args[0]);
			d = Integer.parseInt(args[1]);		
		}
		catch(NumberFormatException ex) {
			System.out.println("Error");
			return;
		}
		PrimeGenerator generator = new PrimeGenerator(d, k);
		BigInteger[] primes = generator.gen();		
		for(int i = 0; i < k; i++) {
			System.out.println(primes[i]);		
		}
	}
}
