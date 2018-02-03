package manet.algorithm.gossip;

import peersim.edsim.EDProtocol;

public abstract class GossipProtocolAbstract implements GossipProtocol, EDProtocol{
	
	protected static final String PAR_EMITTERPID = "emitterdecoratorprotocol";
	
	protected boolean alreadySent = false;
	protected boolean firstRecv = false;
	protected int isTimerArmed=0;
	
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

	public boolean getFirstRecv() {
		return firstRecv;
	}

	public void setFirstRecv(boolean firstRecv) {
		this.firstRecv = firstRecv;
	}


	public int getTimerArmed() {
		return isTimerArmed;
	}


	public void setTimerArmed(int isTimerArmed) {
		this.isTimerArmed = isTimerArmed;
	}
}
