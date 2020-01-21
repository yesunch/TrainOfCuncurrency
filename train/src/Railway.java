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

	private final Element[] elements;
	private final static Logger LOGGER = Logger.getLogger(Train.class.getName());

	public Railway(Element[] elements) {
		if(elements == null)
			throw new NullPointerException();
		
		this.elements = elements;
		for (Element e : elements)
			e.setRailway(this);
	}

	public Element[] getElements() {
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
		List<Element> elementsList = new ArrayList<>(Arrays.asList(this.elements));
		List<Element> finalRoute = new ArrayList<>();
		for(int i = elementsList.indexOf(originalStation); i<= elementsList.indexOf(destination); i++)
			finalRoute.add(elementsList.get(i));
		for(int i = elementsList.indexOf(originalStation); i>= elementsList.indexOf(destination); i--)
			finalRoute.add(elementsList.get(i));
		return finalRoute;
	}

	public synchronized void canEnterSection(Station currentStation, Train train) throws InterruptedException {
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
			this.wait();
		}
		if (train.getPos().getDirection() == Direction.LR)
			lr--;
		else rl--;

		LOGGER.info("for "+train.toString()+"lr is "+lr+", rl is "+rl);
		//LOGGER.info(train.toString()+" is waiting to leave station "+currentStation.toString());
		currentStation.outTrain(train);
		nextSection.enter(train);
		train.getRoute().remove(0);
		nextSection.setInUse(false);


	}


}
