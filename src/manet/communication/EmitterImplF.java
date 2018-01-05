package manet.communication;

import manet.Message;
import peersim.core.Node;

public class EmitterImplF implements Emitter {

	private EmitterImpl myEmitter;
	private int N;
	
	public EmitterImplF() {
		N = 0;
	
	}

	@Override
	public void emit(Node host, Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLatency() {
		return myEmitter.getLatency();
	}

	@Override
	public int getScope() {
		return myEmitter.getScope();
	}
	
	
	public EmitterImplF clone() {
		try {
			return (EmitterImplF) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning EmitterImplF Failed !");
		}
		return null;
	}

}
