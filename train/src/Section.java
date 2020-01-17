/**
 * Représentation d'une section de voie ferrée. C'est une sous-classe de la
 * classe {@link Element}.
 *
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Section extends Element {

	private boolean inUse;
	private Station stationA;
	private Station stationB;
	private Train trainInSection;

	public Section(String name, boolean inUse, Station stationA, Station stationB, Train trainInSection) {
		super(name);
		this.inUse = inUse;
		this.stationA = stationA;
		this.stationB = stationB;
		this.trainInSection = trainInSection;
	}

	public synchronized boolean isInUse() throws InterruptedException {
		return inUse;
	}

	public synchronized void enter() throws InterruptedException {
		while (this.inUse){
			wait();
		}
		this.inUse = true;
	}

	public synchronized boolean usedByTrain(Train train){
		return trainInSection.equals(train);
	}

	public Section(String name) {
		super(name);
	}
}
