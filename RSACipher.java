import java.util.*;
import java.io.*;
import java.math.BigInteger;

class RSA {
	private int k, d;
	private PrimeGenerator generator;
	private BigInteger[] primes;
	RSA(int k, int d) {
		this.k = k;
		this.d = d;
		generator = new PrimeGenerator(d, k);	
	}
	
	public void gen() {
		primes = generator.gen();
		BigInteger N = primes[0];
		for(int i = 1; i < k; i++) {
			N = N.multiply(primes[i]);
		}
		BigInteger phi = primes[0].subtract(BigInteger.ONE);
		for(int i = 1; i < k; i++) {
			phi = phi.multiply(primes[i].subtract(BigInteger.ONE));
		}
		BigInteger e = new BigInteger("65537");
		BigInteger d = e.modInverse(phi);
		try{
			PrintWriter writer = new PrintWriter("RSA.pub", "UTF-8");
			writer.println(N.toString());
			writer.println(e.toString());
			writer.close();
			writer = new PrintWriter("RSA.priv", "UTF-8");
			writer.println(d.toString());
			writer.close();
		}
		catch(Exception ex) {
			System.out.println("Writer error.");
			return;
		}
	}
	
	public BigInteger enc(BigInteger M, BigInteger e, BigInteger N) {
		BigInteger C = M.modPow(e, N);
		return C;
	}
	
	public BigInteger dec(BigInteger C, BigInteger d, BigInteger N) {
		BigInteger M = C.modPow(d,N);
		return M;
	}
}

class RSACRT {
	private int k, d;
	private PrimeGenerator generator;
	private BigInteger[] primes;
	private BigInteger[] MArray;
	RSACRT(int k, int d) {
		this.k = k;
		this.d = d;
		MArray = new BigInteger[k];
		generator = new PrimeGenerator(d, k);	
	}

	private class DecryptThread extends Thread {
		BigInteger p, d, N, C;
		int id;
		DecryptThread(int id, BigInteger p, BigInteger d, BigInteger N, BigInteger C) {
			this.p = p;
			this.d = d;
			this.N = N;
			this.C = C;
			this.id = id;
		}
		public void run() {
			BigInteger M = C.modPow(d, p);
			BigInteger Ni = N.divide(p);
			BigInteger y = Ni.modInverse(p);
			BigInteger result = M.multiply(Ni).mod(N).multiply(y).mod(N);
			MArray[id] = result;
		}
	}

	public void gen() {
		primes = generator.gen();
		BigInteger N = primes[0];
		for(int i = 1; i < k; i++) {
			N = N.multiply(primes[i]);
		}
		BigInteger phi = primes[0].subtract(BigInteger.ONE);
		for(int i = 1; i < k; i++) {
			phi = phi.multiply(primes[i].subtract(BigInteger.ONE));
		}
		BigInteger e = new BigInteger("65537");
		BigInteger d = e.modInverse(phi);
		BigInteger[] dArray = new BigInteger[k];
		for(int i = 0; i < k; i++) {
			dArray[i] = d.mod(primes[i].subtract(BigInteger.ONE));	
		}
		try{
			PrintWriter writer = new PrintWriter("RSA.pub", "UTF-8");
			writer.println(N.toString());
			writer.println(e.toString());
			writer.close();
			writer = new PrintWriter("RSA.priv", "UTF-8");
			for(int i = 0; i < k; i++) {
				writer.println(primes[i]);
			}
			for(int i = 0; i < k; i++) {
				writer.println(dArray[i]);
			}
			writer.close();
		}
		catch(Exception ex) {
			System.out.println("Writer error.");
			return;
		}
	}
	
	public BigInteger enc(BigInteger M, BigInteger e, BigInteger N) {
		BigInteger C = M.modPow(e, N);
		return C;
	}
	
	public BigInteger dec(BigInteger C, BigInteger[] p, BigInteger[] d, BigInteger N) {
		DecryptThread[] decryptArray = new DecryptThread[k];
		for(int i = 0; i < k; i++) {
			decryptArray[i] = new DecryptThread(i, p[i], d[i], N, C);
			decryptArray[i].start();
		}
		for(int i = 0; i < k; i++) {
			try{decryptArray[i].join();}
			catch(Exception ex) {
				System.out.println("Thread interrupted.");
				return null;		
			}
		}
		BigInteger M = BigInteger.ZERO;
		for(int i = 0; i < k; i++) {
			M = M.add(MArray[i]).mod(N);
		}
		return M;
	}
}


public class RSACipher {
	public static void main(String[] args) {
		boolean CRT;
		int k, d;
		if(args[0].equals("CRT")) {
			CRT = true;
		}
		else {
			CRT = false;
		}
		String method = args[1];
		try{
			k = Integer.parseInt(args[2]);
			d = Integer.parseInt(args[3]);		
		}
		catch(NumberFormatException ex) {
			System.out.println("Error");
			return;
		}
		if(!CRT) {
			RSA cipher = new RSA(k,d);
			if(method.equals("gen")) {
				cipher.gen();
			}
			else if(method.equals("enc")) {
				try{
					Scanner in = new Scanner(new FileReader("RSA.pub"));
					BigInteger N = new BigInteger(in.next());
					BigInteger e = new BigInteger(in.next());
					in = new Scanner(new FileReader("msg"));
					BigInteger M = new BigInteger(in.next());
					PrintWriter writer = new PrintWriter("enc", "UTF-8");
					writer.println(cipher.enc(M, e, N));
					writer.close();
				}
				catch(Exception ex){
					System.out.println("Scanner error");
					return;
				}				
			}
			else if(method.equals("dec")) {
				try{
					Scanner in = new Scanner(new FileReader("RSA.pub"));
					BigInteger N = new BigInteger(in.next());
					BigInteger e = new BigInteger(in.next());
					in = new Scanner(new FileReader("RSA.priv"));
					BigInteger priv = new BigInteger(in.next());		
					in = new Scanner(new FileReader("enc"));
					BigInteger C = new BigInteger(in.next());
					System.out.println(cipher.dec(C,priv,N));
				}
				catch(Exception ex){
					System.out.println("Scanner error");
					return;
				}	
			}
			else {
				System.out.println("No such method.");
				return;		
			}
		}
		else {
			RSACRT cipher = new RSACRT(k,d);	
			if(method.equals("gen")) {
				cipher.gen();
			}
			else if(method.equals("enc")) {
				try{
					Scanner in = new Scanner(new FileReader("RSA.pub"));
					BigInteger N = new BigInteger(in.next());
					BigInteger e = new BigInteger(in.next());
					in = new Scanner(new FileReader("msg"));
					BigInteger M = new BigInteger(in.next());
					PrintWriter writer = new PrintWriter("enc", "UTF-8");
					writer.println(cipher.enc(M, e, N));
					writer.close();
				}
				catch(Exception ex){
					System.out.println("Scanner error");
					return;
				}
			}
			else if(method.equals("dec")) {
				try{
					Scanner in = new Scanner(new FileReader("RSA.pub"));
					BigInteger N = new BigInteger(in.next());
					BigInteger e = new BigInteger(in.next());		
					in = new Scanner(new FileReader("enc"));
					BigInteger C = new BigInteger(in.next());
					in = new Scanner(new FileReader("RSA.priv"));
					BigInteger[] dArray = new BigInteger[k];
					BigInteger[] primes = new BigInteger[k];
					for(int i = 0; i < k; i++) {
						primes[i] = new BigInteger(in.next());
					}	
					for(int i = 0; i < k; i++) {
						dArray[i] = new BigInteger(in.next());
					}
					System.out.println(cipher.dec(C,primes,dArray,N));
				}
				catch(Exception ex){
					System.out.println("Scanner error");
					return;
				}	
			}
			else {
				System.out.println("No such method.");
				return;		
			}	
		}	
		
	}
}
