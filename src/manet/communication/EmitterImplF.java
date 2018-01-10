package manet.communication;

import manet.Message;
import manet.detection.NeighborProtocolImpl;
import peersim.config.Configuration;
import peersim.core.Node;

public class EmitterImplF implements Emitter {
	
	private static final String PAR_EMITTERPID = "emitterprotocol";
	private static final String PAR_NEIGHBOURPID = "neighbourprotocol";
	
	private final int emitter_pid;
	private final int neighbour_pid;
	
	private Node node;
	
	private static Integer N;
	private int Ninit;
	
	public EmitterImplF(String prefix) {
		emitter_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		neighbour_pid = Configuration.getPid(prefix + "." + PAR_NEIGHBOURPID);
		N = 0;
	}
	
	public EmitterImplF clone() {
		try {
			return (EmitterImplF) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning EmitterImplF Failed !");
		}
		return null;
	}

	@Override
	public int getLatency() {
		return ((EmitterImpl) node.getProtocol(emitter_pid)).getLatency();
	}

	@Override
	public int getScope() {
		return ((EmitterImpl) node.getProtocol(emitter_pid)).getScope();
	}
	
	@Override
	public void emit(Node host, Message msg) {
		node = host;
		((EmitterImpl)host.getProtocol(emitter_pid)).emit(host,msg);
		int neighbours = ((NeighborProtocolImpl)host.getProtocol(neighbour_pid)).getNeighbors().size();;
		N += neighbours;	
		Ninit = N;
		System.out.println(host.getID()+" N init " + N);
		System.out.println(host.getID() +" : " +((NeighborProtocolImpl)host.getProtocol(neighbour_pid)).getNeighbors().size());
	}
	
	public void decrementN (int n) {
		N -= n;
	}
	
	public int getNeighbors() {
		return ((NeighborProtocolImpl)node.getProtocol(neighbour_pid)).getNeighbors().size();	
	}

	public int  getN () {
		return this.N;
	}
	
	public int getNinit() {
		return Ninit;
	}
	
}
