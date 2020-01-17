import javafx.geometry.Pos;

import java.util.List;

/**
 * Représentation d'un train. Un train est caractérisé par deux valeurs :
 * <ol>
 *   <li>
 *     Son nom pour l'affichage.
 *   </li>
 *   <li>
 *     La position qu'il occupe dans le circuit (un élément avec une direction) : classe {@link Position}.
 *   </li>
 * </ol>
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Mayte segarra <mt.segarra@imt-atlantique.fr>
 * Test if the first element of a train is a station
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 * @version 0.3
 */
public class Train implements Runnable{
	private final String name;
	private Position pos;

	private Station originalStation;
	private Station destination;
	private List<Element> route;


	public Train(String name, Position p, Station originalStation, Station destination) throws BadPositionForTrainException {
		if (name == null || p == null)
			throw new NullPointerException();

		// A train should be first be in a station
		if (!(p.getPos() instanceof Station))
			throw new BadPositionForTrainException(name);

		this.name = name;
		this.pos = p.clone();
		this.originalStation = originalStation;
		this.destination = destination;
	}


	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Train[");
		result.append(this.name);
		result.append("]");
		result.append(" is on ");
		result.append(this.pos);
		return result.toString();
	}

	public void depart() {
		System.out.println("Train has departed from "+this.originalStation.toString()+" with destination of "+this.destination);
	}

	public void stop() {
		System.out.println("Train has stopped at" + this.pos);
	}

	public void getRoute() {
		this.route = Railway.calculateRoute(Station originalStation, Station destination);

	}

	public void gotoNextStation() throws InterruptedException {
		if (this.pos.getPos() != this.destination) {
			Position oldPos = this.pos;
			//if it's leaving from a station, call the outTrain of this station
			if (oldPos.getPos() instanceof Station)
				((Station) oldPos.getPos()).outTrain(this);
			Element nextElem =this.route.remove(0);
			//if its nextElem is a station, try to enter it
			if (nextElem instanceof Station)
				((Station) nextElem).enterTrain(this);
			this.pos = calculNextPos(nextElem);
			System.out.println("Train is moving from " + oldPos + " to " + this.pos);
			if (this.pos.getPos() == this.destination)
				this.stop();
		}
	}

	private Position calculNextPos(Element nextElem) {
		Direction direction;
		//if the nextElem id is greater, than go right
		if (nextElem.getId() > this.pos.getPos().getId())
			direction = Direction.LR;
		else
			direction = Direction.RL;
		this.pos = new Position(nextElem,direction);

	}
	@Override
	public void run() {
		this.getRoute();
		this.depart();
		while (this.pos.getPos() != this.destination) {
			try {
				this.gotoNextStation();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
