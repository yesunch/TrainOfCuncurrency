import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Représentation d'un circuit constitué d'éléments de voie ferrée : gare ou
 * section de voie
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Railway {

	private final Element[] elements;

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

}
