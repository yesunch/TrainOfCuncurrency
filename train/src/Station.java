import javax.sound.midi.Soundbank;
import java.util.Map;

/**
 * Représentation d'une gare. C'est une sous-classe de la classe {@link Element}.
 * Une gare est caractérisée par un nom et un nombre de quais (donc de trains
 * qu'elle est susceptible d'accueillir à un instant donné).
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Station extends Element {
	private final int size;
	int nbTrains, nbSections;

	public Station(String name, int size) {
		super(name);
		if(name == null || size <=0)
			throw new NullPointerException();
		this.size = size;
	}

	public synchronized void enterTrain(Train train) throws InterruptedException {
		while (this.nbTrains >= this.size) {
			System.out.println("This station is full, train "+train.toString()+" is waiting to enter");
			this.wait();
		}
		this.nbTrains++;
		System.out.println("Train "+train.toString()+" has entered station "+
				this.toString()+", now this station has "+this.nbTrains+" trains");
		this.notifyAll();

	}

	public synchronized void outTrain(Train train) throws InterruptedException {
		while (this.nbTrains <= 0) {
			this.wait();
		}
		this.nbTrains--;
		System.out.println("Train "+train.toString()+" has left station "+
				this.toString()+", now this station has "+this.nbTrains+" trains");
		this.notifyAll();
	}
}
