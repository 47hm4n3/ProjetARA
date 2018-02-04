package manet.detection;

import java.util.ArrayList;
import java.util.List;

import manet.Message;
import manet.communication.EmitterImpl;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import utils.MessageType;

public class NeighborProtocolImpl implements NeighborProtocol, EDProtocol {

	private static final String PAR_EMITTERPID = "emitterprotocol";
	private static final String PAR_PERIOD = "period";
	private static final String PAR_DELTA = "delta";

	private final int neighbour_pid;
	private final int emitter_pid;
	private final long period; // Timer pour l'envoie du heartbeat
	private final long delta; // Timer pour potentiellement retirer un noeud de ses voisins 
	private List<Long> neighbours;
	private int[] timer; //chaque noeud a pour chacun de ses voisins un timer associé
	private Message msgReceived;
	private Message msgToSend;
	
	public NeighborProtocolImpl(String prefix) {
		String tmp[] = prefix.split("\\.");
		neighbour_pid = Configuration.lookupPid(tmp[tmp.length - 1]);
		emitter_pid = Configuration.getPid(prefix + "." + PAR_EMITTERPID);
		delta = Configuration.getInt(prefix + "." + PAR_DELTA);
		period = Configuration.getInt(prefix + "." + PAR_PERIOD);
	}

	@Override
	public List<Long> getNeighbors() {
		return this.neighbours;
	}

	public long getPeriod() {
		return this.period;
	}

	public long getDelta() {
		return this.delta;
	}

	public NeighborProtocolImpl clone() {
		try {
			neighbours = new ArrayList<Long>();
			timer = new int[Network.size()];
			for(int i=0;i<Network.size();i++) {
				timer[i]=0;
			}
			return (NeighborProtocolImpl) super.clone();
		} catch (Exception e) {
			System.out.println("Cloning NeighborProtocolImpl Failed !");
		}
		return null;
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		
		if (neighbour_pid != pid) {
			throw new RuntimeException("Receive Event for wrong protocol");
		}
		
		if (event instanceof Message) {
			
			msgReceived = (Message) event;
						
			long idSrc = msgReceived.getIdSrc();
			
			switch(msgReceived.getTag()) {
				
			case MessageType.probe:
				// Réception d'un process event timer déclenchant l'envoie du heartbeat et on réarme un timer pour envoyer le prochain
				idSrc = host.getID();
				msgToSend = new Message(idSrc, -1, MessageType.heartbeat, host, neighbour_pid);
				((EmitterImpl) host.getProtocol(emitter_pid)).emit(host, msgToSend);
				EDSimulator.add(period, new Message(idSrc,-1,MessageType.probe,host,neighbour_pid), host, neighbour_pid);
				break;
				
			case MessageType.timer:
				// Réception d'un process event timer associé à un voisin, permettant de suggérer si il est toujours voisin
				int newTimer = timer[(int)idSrc]-1; 
				timer[(int)idSrc]=newTimer;
				if(newTimer == 0) 
					neighbours.remove(idSrc);
				break;
				
			case MessageType.heartbeat:
				// Réception d'un process event (heartbeat) d'un autre noeud
				if (!neighbours.contains(idSrc)) {
					neighbours.add(idSrc);
				}
				timer[(int)idSrc]++;
				msgToSend = new Message(idSrc, -1, MessageType.timer, host, neighbour_pid);
				EDSimulator.add(delta, msgToSend, host, neighbour_pid);
				break;
				
			default:
				break;
				
			}
			
		} else {
			throw new RuntimeException("Received Event of unmatching type");
		}
	}

}
