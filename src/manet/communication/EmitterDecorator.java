package manet.communication;


import manet.Message;
import manet.positioning.PositionProtocol;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import utils.MessageType;
import utils.DataProtoProba;

public class EmitterDecorator extends EmitterImpl implements EDProtocol {
	/* 
	 * Classe utilisée à là fois comme emitter et recepteur 
	 * -utilisée par les gossip protocols
	 * -incrémente et décrémente la variable associée au nombre de message "en transit"
	 * -utilise les différentes stratégies de probabilité
	 */
	
	
	private static final String PAR_POSITIONPID = "positionprotocol";
	private static final String PAR_GOSSIPPID = "gossipprotocol";
	private static final String PAR_K = "k";
	private static final String PAR_PROBA = "proba";
	
	private final int gossip_pid;
	private final int position_pid;
	
	private final double proba;
	private final int k;
	
	private static Integer N; // Nombre de message en transit lors d'une vague de diffusion

	public EmitterDecorator(String prefix) {
		super(prefix);
		position_pid = Configuration.getPid(prefix + "." + PAR_POSITIONPID);
		gossip_pid = Configuration.getPid(prefix + "." + PAR_GOSSIPPID);
		k = Configuration.getInt(prefix + "." + PAR_K);
		proba = Configuration.getDouble(prefix + "." + PAR_PROBA, 1.0);
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
		// Ne fait que diffuser les message de la couche applicative mais aussi incrémenter le nombre de message en transit
		super.emit(host, msg);
		N += nbNeighbors(host);

	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		/* Réception des messages de diffusion des autres noeuds
		 * -Calcule de la probabilité selon le type de diffusion (devrait être à la couche applicative mais c'est moins jolie)
		 * -Délivrance du message à la couche applicative
		 * Mais aussi réception d'un message du même noeud venant de la couche applicative afin de décrémenter le nombre de message en transit
		 */
		if (event instanceof Message) {
			Message msg = ((Message) event);
			Message newMsg = null;
			switch(msg.getTag()) {

			case MessageType.decrement :
				N--;
				break;

			case MessageType.flooding_algo1_2 : 
				newMsg = new Message(msg.getIdSrc(), msg.getIdSrc(), msg.getTag(), proba, msg.getPid());
				EDSimulator.add(0, newMsg, host, gossip_pid);
				break;

			case MessageType.flooding_algo3 :
				newMsg = new Message(msg.getIdSrc(), msg.getIdSrc(), msg.getTag(), ((double)k)/((double)nbNeighbors(host)), msg.getPid());
				EDSimulator.add(0, newMsg, host, gossip_pid);
				break;

			case MessageType.flooding_algo4 :
				newMsg = new Message(msg.getIdSrc(), msg.getIdSrc(), msg.getTag(), calculateProbability(host,msg.getIdSrc()), msg.getPid());
				EDSimulator.add(0, newMsg, host, gossip_pid);
				break;
				
			case MessageType.flooding_algo3_algo8 :
				DataProtoProba d = (DataProtoProba)msg.getContent();
				d.setProba( ((double)k)/((double)nbNeighbors(host)));
				newMsg = new Message(msg.getIdSrc(), msg.getIdSrc(), msg.getTag(), d, msg.getPid());
				EDSimulator.add(0, newMsg, host, gossip_pid);
				break;
				
			case MessageType.flooding_algo4_algo8 :
				DataProtoProba d2 = (DataProtoProba)msg.getContent();
				d2.setProba(calculateProbability(host,msg.getIdSrc()) );
				newMsg = new Message(msg.getIdSrc(), msg.getIdSrc(), msg.getTag(), d2, msg.getPid());
				EDSimulator.add(0, newMsg, host, gossip_pid);;
				
				break;
			default :
				System.out.println("ERROR");
				break;
			}
			
		} else {

		}
	}

	private double calculateProbability(Node host, long idSrc) {
		// Cacule de probabilité selon la distance récepteur et émetteur, et le scope
		// Méthode utilisée pour GossipProtocolDistance et GossipProtocolQ8 avec stratégie 4
		Node node =  Network.get((int)idSrc);
		PositionProtocol hostPos = (PositionProtocol) host.getProtocol(position_pid);
		PositionProtocol nodePos = (PositionProtocol) node.getProtocol(position_pid);
		return hostPos.getCurrentPosition().distance(nodePos.getCurrentPosition())/(double)getScope();
		
	}

	private int nbNeighbors(Node host) {
		// Récupère le nombre de voisins 
		// Méthode utilisée pour GossipProtocolK et GossipProtocolQ8 avec stratégie 3
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

	public int getN() {
		return N;
	}

}