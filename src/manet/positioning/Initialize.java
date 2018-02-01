package manet.positioning;

import manet.Message;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class Initialize implements Control {

	private static final String PAR_POSITIONPID = "positionprotocol";
	private static final String PAR_NEIGHBOURPID = "neighbourprotocol";

	private final int position_pid;
	private final int neighbour_pid;
	private Node node = null;
	private PositionProtocol pos = null;

	public Initialize(String prefix) {
		position_pid = Configuration.getPid(prefix + "." + PAR_POSITIONPID);
		neighbour_pid = Configuration.getPid(prefix + "." + PAR_NEIGHBOURPID);
	}

	@Override
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++) {
			node = Network.get(i);
			pos = (PositionProtocol) node.getProtocol(position_pid);
			pos.initialiseCurrentPosition(Network.get(i));
			EDSimulator.add(0, PositionProtocolImpl.loop_event, node, position_pid);
			EDSimulator.add(0, new Message(node.getID(),-1,MessageType.probe,node,neighbour_pid), node, neighbour_pid);
		}
		return false;
	}

}
