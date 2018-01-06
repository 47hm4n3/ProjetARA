package manet.algorithm.gossip;

import manet.Message;
import manet.communication.EmitterImplF;
import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import utils.MessageType;

public class GossipProtocolImpl implements GossipProtocol, EDProtocol {

	private static final String PAR_EMITTERPID = "emitterfloodingprotocol";
	
	private final int gossip_pid;
	private final int emitterflooding_pid;
	private boolean firstTime = true; 
	
	
	public GossipProtocolImpl(String prefix) {
		String tmp[] = prefix.split("\\.");
		gossip_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		emitterflooding_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
	}

	public GossipProtocolImpl clone () {
		try {
			return (GossipProtocolImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning GossipProtocolImpl Failed !");
		}
		return null;
	}

	@Override
	public void initiateGossip(Node host, int id, long id_initiator) {
		Message msg = new Message(host.getID(),-1,MessageType.flooding,null,gossip_pid); // tag == flooding
		
		((EmitterImplF)host.getProtocol(emitterflooding_pid)).emit(host, msg); // emit
		System.out.println("ALLO "+((EmitterImplF)host.getProtocol(emitterflooding_pid)).getNeighbors());
		firstTime = false;
		System.out.println("je suis "+host.getID()+ " et jinitie");
	}
	
	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (event instanceof Message) {
			if (((Message)event).getTag() == MessageType.flooding) {
				Message m = (Message)event;
				System.out.println("je suis "+host.getID());
				if (firstTime) {
					firstTime = false;
					((EmitterImplF)host.getProtocol(emitterflooding_pid)).decrementN(1);
					((EmitterImplF)host.getProtocol(emitterflooding_pid)).emit(host, m);
				} else {
					//((EmitterImplF)host.getProtocol(emitterflooding_pid)).decrementN(1);
				}
			}else {
				System.out.println("ERROR");
			}
		}
	}
}
