import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

	private Railway r;
	private Station originalStation;
	private Station destination;
	private List<Element> route;

	private final static Logger LOGGER = Logger.getLogger(Train.class.getName());

	public void setRoute(List<Element> route) {
		this.route = route;
	}

	public Train(String name,  Station originalStation, Station destination, Railway r) throws BadPositionForTrainException, InterruptedException {
		if (name == null )
			throw new NullPointerException();

		this.name = name;
		this.pos = new Position(originalStation, destination.getId()>originalStation.getId()?Direction.LR:Direction.RL);
		if (this.pos.getPos() instanceof Station)
			((Station) this.pos.getPos()).enterTrain(this);
		this.originalStation = originalStation;
		this.destination = destination;
		this.r = r;
	}


	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Train[");
		result.append(this.name);
		result.append("]");
		return result.toString();
	}

	/**
	 * Prints a statement that the train starts to move from original station to the destination
	 */
	public void depart() {
		LOGGER.info(this.toString()+" has departed from "+this.originalStation.toString()+" with destination of "+this.destination);
	}

	/**
	 * Prints a statement that The train stops at current position.
	 */
	public void stop() {
		LOGGER.info(this.toString()+" has stopped at " + this.pos.getPos());
	}

	/**
	 * Requires a route from original station to its destination using Railway
	 */
	public void getRoute() {
		List<Element> routeCalculated = this.r.calculateRoute(this.originalStation, this.destination);
		this.route = routeCalculated.subList(1,routeCalculated.size());

	}

	/**
	 *
	 * @throws InterruptedException
	 */
	public void gotoNextStation() throws InterruptedException {
		if (this.pos.getPos() != this.destination) {
			Position oldPos = this.pos;

			//if it's leaving from a station, call the outTrain method of this station
			if (oldPos.getPos() instanceof Station)
				((Station) oldPos.getPos()).outTrain(this);
			//if it's leaving from a section, make this section available to others
			if (oldPos.getPos() instanceof Section){
				((Section)oldPos.getPos()).setInUse(false);
				LOGGER.info(this.toString() + " has gone out of the section " + oldPos.getPos().toString());
			}

			Element nextElem =this.route.remove(0);
			calculNextPos(nextElem);
			LOGGER.info(this.toString()+" is moving from " + oldPos.getPos() +" "+ this.pos.getDirection() + " to " + this.pos.getPos());

			//if its nextElem is a station, try to enter it
			if (nextElem instanceof Station)
				((Station) nextElem).enterTrain(this);



			//if its nextElem is a section, try to enter it and make it not available to others
			if (nextElem instanceof Section) {
				Element afterNext = this.route.remove(0);
				List<Section> sectionsList = new ArrayList<>();
				if (!(afterNext instanceof Section)) //station
					this.route.add(0,afterNext);
				while (afterNext instanceof Section) {
					nextElem = ((Section) nextElem).addSection((Section)afterNext);
					sectionsList.add((Section) afterNext);
					afterNext = this.route.remove(0);
				}
				this.route.add(0,afterNext);
				sectionsList.forEach(section ->{
					try {
						section.enter(this);
						section.setInUse(true);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
				//((Section) nextElem).enter(this);
				calculNextPos(nextElem);
				//((Section) nextElem).setInUse(true);
			}
			//if it arrives at the destination, it will stop
			if (this.pos.getPos() == this.destination)
				this.stop();
		}
	}

	/**
	 * Method used to update the current position of train using the next Element.
	 * If the id of nextElem is greater than the current position id,
	 * @param nextElem
	 */
	private void calculNextPos(Element nextElem) {
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
		while (true) {
			this.getRoute();
			LOGGER.info(this.toString() + " has route " + this.route);
			this.depart();
			while (this.pos.getPos() != this.destination) {
				try {
					this.gotoNextStation();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			//the train has arrived its destination, now it turns around and continue running
			LOGGER.info(this.toString()+" has turned around");
			Station temp = this.originalStation;
			this.originalStation = this.destination;
			this.destination = temp;
		}

	}
}
