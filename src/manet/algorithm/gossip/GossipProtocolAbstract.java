package manet.algorithm.gossip;

import peersim.edsim.EDProtocol;

public abstract class GossipProtocolAbstract implements GossipProtocol, EDProtocol{
	
	protected boolean firstTime = true;
	protected int received = 0;
	protected int transmited = 0;
	
	
	public GossipProtocolAbstract clone () {
		try {
			return (GossipProtocolAbstract) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning GossipProtocolAbstract Failed !");
		}
		return null;
	}
	
	public boolean getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}

	public int getReceived() {
		return received;
	}

	public void setReceived(int received) {
		this.received = received;
	}

	public int getTransmited() {
		return transmited;
	}

	public void setTransmited(int transmited) {
		this.transmited = transmited;
	}
	
	
}
