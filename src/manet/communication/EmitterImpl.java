	package manet.communication;

import manet.Message;
import manet.positioning.PositionProtocol;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class EmitterImpl implements Emitter {

	private static final String PAR_POSITIONPID = "positionprotocol";
	private static final String PAR_LATENCY = "latency";
	private static final String PAR_SCOPE = "scope";

	private final int position_pid;
	private final int latency;
	private final int scope;
	
	private PositionProtocol hostPos = null;
	private PositionProtocol nodePos = null;
	private Node node = null;

	
	public EmitterImpl(String prefix) {
		position_pid = Configuration.getPid(prefix + "." + PAR_POSITIONPID);
		latency = Configuration.getInt(prefix + "." + PAR_LATENCY);
		scope = Configuration.getInt(prefix + "." + PAR_SCOPE);
	}

	@Override
	public int getLatency() {
		return this.latency;
	}

	@Override
	public int getScope() {
		return this.scope;
	}


	public EmitterImpl clone() {
		try {
			return (EmitterImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning EmitterImpl Failed !");
		}
		return null;
	}
	
	@Override
	public void emit(Node host, Message msg) {
		for (int i = 0; i < Network.size(); i++) { // For all network nodes
			node = Network.get(i);
			if (host.getID() != node.getID()) { // Except me
				hostPos = (PositionProtocol) host.getProtocol(position_pid);
				nodePos   = (PositionProtocol) node.getProtocol(position_pid);
				if ((hostPos.getCurrentPosition().distance(nodePos.getCurrentPosition()) <= this.getScope())) { // In my scope
					EDSimulator.add(this.getLatency(), msg, node, msg.getPid()); // Send()
				}
			}
		}
	}

}
