package manet.algorithm.gossip;

import peersim.edsim.EDProtocol;

public abstract class GossipProtocolAbstract implements GossipProtocol, EDProtocol{
	
	protected boolean firstTime = true; 
	
	
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
}
