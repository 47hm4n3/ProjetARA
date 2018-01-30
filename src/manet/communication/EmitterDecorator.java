package manet.communication;

import manet.Message;
import manet.positioning.PositionProtocol;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class EmitterDecorator extends EmitterImpl implements EDProtocol {

	private static final String PAR_EMITTERPID = "emitterprotocol";
	private static final String PAR_POSITIONPID = "positionprotocol";
	private static final String PAR_GOSSIPPID = "gossipprotocol";

	private final int my_pid;
	private final int gossip_pid;
	private final int position_pid;

	private static Integer N;

	protected int received = 0;
	protected int transmited = 0;

	public EmitterDecorator(String prefix) {
		super(prefix);
		String tmp[] = prefix.split("\\.");
		my_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		position_pid = Configuration.getPid(prefix + "." + PAR_POSITIONPID);
		gossip_pid = Configuration.getPid(prefix + "." + PAR_GOSSIPPID);
		N = 0;
	}

	public EmitterDecorator clone() {
		return (EmitterDecorator) super.clone();
	}

	@Override
	public int getLatency() {
		return super.getLatency();
	}

	@Override
	public int getScope() {
		return super.getScope();
	}

	@Override
	public void emit(Node host, Message msg) {
		super.emit(host, msg);
		N += nbNeighbors(host);
		//if(msg.getContent() != null &&  !(boolean)msg.getContent()){
			transmited++;
		//}
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (event instanceof Message) {
			Message msg = ((Message) event);
			if (msg.getTag() == MessageType.decrement) { // Reception depuis la couche applicative
				N--;
				if (msg.getContent() != null) {
					if (((boolean) msg.getContent())) {
						System.out.println("incrementer received");
						received++;
					}
				} else {
					System.out.println("FLOODING CONTENT == NULL");
				}
			} else {
				if (msg.getTag() == MessageType.flooding) {
					Message newMsg = new Message(msg.getIdSrc(), msg.getIdSrc(), msg.getTag(), null, msg.getPid());
					EDSimulator.add(0, newMsg, host, gossip_pid);
				}
				// System.out.println(host.getID() + " decremente");
			}
		} else {
			System.out.println("ERROR");
		}

	}

	private int nbNeighbors(Node host) {
		PositionProtocol hostPos = null;
		PositionProtocol nodePos = null;
		Node node = null;
		int cpt = 0;
		for (int i = 0; i < Network.size(); i++) { // For all network nodes
			node = Network.get(i);
			if (host.getID() != node.getID()) { // Except me
				hostPos = (PositionProtocol) host.getProtocol(position_pid);
				nodePos = (PositionProtocol) node.getProtocol(position_pid);
				if ((hostPos.getCurrentPosition().distance(nodePos.getCurrentPosition()) <= this.getScope())) { // In my
																												// scope
					cpt++;
				}
			}
		}
		return cpt;
	}

	public void decrementN(int n) {
		N -= n;
	}

	public int getN() {
		return this.N;
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
