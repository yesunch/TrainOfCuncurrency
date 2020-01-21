import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Représentation d'un circuit constitué d'éléments de voie ferrée : gare ou
 * section de voie
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Railway {

	//private final Element[] elements;
	private List<Element> elements;
	private final static Logger LOGGER = Logger.getLogger(Train.class.getName());

	public Railway(Element[] elements) {
		if(elements == null)
			throw new NullPointerException();
		
		this.elements = new ArrayList<>(Arrays.asList(elements));
		for (Element e : elements)
			e.setRailway(this);
	}

	public List<Element> getElements() {
		return elements;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Element e : this.elements) {
			if (first)
				first = false;
			else
				result.append("--");
			result.append(e);
		}
		return result.toString();
	}

	/**
	 * Calculates the route in a linear railway. Route will include depart and destinations elements
	 * @param originalStation
	 * @param destination
	 * @return
	 */
	public  synchronized List<Element> calculateRoute(Station originalStation, Station destination){
		//List<Element> elementsList = new ArrayList<>(Arrays.asList(this.elements));
		List<Element> finalRoute = new ArrayList<>();
		for(int i = this.elements.indexOf(originalStation); i<= this.elements.indexOf(destination); i++)
			finalRoute.add(this.elements.get(i));
		for(int i = this.elements.indexOf(originalStation); i>= this.elements.indexOf(destination); i--)
			finalRoute.add(this.elements.get(i));
		return finalRoute;
	}

	/**
	 * Check whether a train can leave the current Station and enter the next Section.
	 *
	 * If the train can leave the current station, it will enter all of the sections connected with each other until
	 * the next station (won't enter this station). All of the sections entered will be removed from the route of the train.
	 * And return the next station.
	 *
	 * Cause all of the train who enter in this method and is permitted to enter the sections would keep running until leave all of
	 * the sections and reach the next station without being interrupted (because of the synchronized keyword), the result of rl*lr would always
	 * be 0 for the trains who enter in this method.
	 *
	 * @param currentStation
	 * @param train
	 * @return	nextStation If the train has entered and left all of the sections between two stations
	 * 			currStation If the train can't leave the currentStation which would not happen if the train can enter this method
	 * @throws InterruptedException
	 */
	public synchronized Element tryEnterSections(Station currentStation, Train train) throws InterruptedException {
		// 2. Where he wnt to go?
		Station nextStation = currentStation;
		Section nextSection = (Section) train.getRoute().get(0);
		for (Element elem : train.getRoute()) {
			if (elem instanceof Station) {
				nextStation = (Station) elem;
				break;
			}
		}
		// 3. get sections between two stations
		List<Section> sectionsList = this.calculateRoute(currentStation, nextStation).stream().filter(elem -> elem instanceof Section).map(section -> (Section)section).collect(Collectors.toList());
		int lr = (int) sectionsList.stream().filter(section -> section.checkSectionFull() && section.getUsingTrainDirection().equals(Direction.LR)).count();
		int rl = (int) sectionsList.stream().filter(section -> section.checkSectionFull() && section.getUsingTrainDirection().equals(Direction.RL)).count();

		if (train.getPos().getDirection() == Direction.LR)
			lr++;
		else rl++;
		while (lr*rl != 0) {
			LOGGER.info("for "+train.toString()+"lr is "+lr+", rl is "+rl+", it's waiting");
			return currentStation;
		}
		if (train.getPos().getDirection() == Direction.LR)
			lr--;
		else rl--;

		LOGGER.info("for "+train.toString()+"lr is "+lr+", rl is "+rl);
		currentStation.outTrain(train);
		for (Section s : sectionsList) {
			s.enter(train);
			train.getRoute().remove(0);
			s.setInUse(false);
		}
		return nextStation;

	}


}
