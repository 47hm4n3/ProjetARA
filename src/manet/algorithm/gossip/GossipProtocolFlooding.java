package manet.algorithm.gossip;

import manet.Message;
import manet.communication.EmitterDecorator;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class GossipProtocolFlooding implements GossipProtocol, EDProtocol {

	private static final String PAR_EMITTERPID = "emitterdecoratorprotocol";
	private static final String PAR_PROBA = "proba";

	private final int gossip_pid;
	private final int emitterdecorator_pid;
	private final double proba;
	private boolean initiator = false;
	private boolean alreadySent = false;
	private boolean firstRecv = false;

	public GossipProtocolFlooding(String prefix) {
		String tmp[] = prefix.split("\\.");
		gossip_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		emitterdecorator_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		proba = Configuration.getDouble(prefix + "." + PAR_PROBA, 1.0);
	}

	public GossipProtocolFlooding clone() {
		try {
			return (GossipProtocolFlooding) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning GossipProtocolImpl Failed !");
		}
		return null;
	}

	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		alreadySent = true; 
		initiator = true;
		firstRecv = true;
		System.out.println(host.getID() + " est initateur");
		Message msg = new Message(host.getID(), -1, MessageType.flooding, initiator, gossip_pid); // tag == flooding
		((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, msg); // emit
		// System.out.println(host.getID() + " initie la diffusion ");
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (event instanceof Message) {
			Message msg = new Message(host.getID(), host.getID(), MessageType.decrement, firstRecv,
					emitterdecorator_pid);
			if(!firstRecv) {
				firstRecv = true;
			}
			Message m = (Message) event;
			
			if (!alreadySent) {
				if (CommonState.r.nextDouble() < proba) {
					((EmitterDecorator) host.getProtocol(emitterdecorator_pid)).emit(host, m);
					alreadySent = true;
				}
			}
			EDSimulator.add(0, msg, host, emitterdecorator_pid); // Decremente reception
		}
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
