package manet.algorithm.gossip;

import peersim.edsim.EDProtocol;

public abstract class GossipProtocolAbstract implements GossipProtocol, EDProtocol{
	
	protected static final String PAR_EMITTERPID = "emitterdecoratorprotocol";
	
	protected boolean initiator = false;
	protected boolean alreadySent = false;
	protected boolean firstRecv = false;
	
	
	public GossipProtocolAbstract clone () {
		try {
			return (GossipProtocolAbstract) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning GossipProtocolAbstract Failed !");
		}
		return null;
	}
	
	
	public boolean getAlreadySent() {
		return alreadySent;
	}

	public void setAlreadySent(boolean alreadySent) {
		this.alreadySent = alreadySent;
	}

	public void setInitator(boolean initator) {
		this.initiator = initator;
	}

	public boolean getFirstRecv() {
		return firstRecv;
	}

	public void setFirstRecv(boolean firstRecv) {
		this.firstRecv = firstRecv;
	}
	
	

}
