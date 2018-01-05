package manet.algorithm.gossip;

import peersim.core.Node;

public class GossipProtocolImpl implements GossipProtocol {

	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		// TODO Auto-generated method stub

	}

	public GossipProtocolImpl clone () {
		try {
			return (GossipProtocolImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning GossipProtocolImpl Failed !");
		}
		return null;
	}
	
	
}
