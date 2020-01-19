/**
 * Représentation d'une section de voie ferrée. C'est une sous-classe de la
 * classe {@link Element}.
 *
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Section extends Element {

	private boolean inUse = false;


	public synchronized void enter(Train train) throws InterruptedException {
		while (this.inUse){
			System.out.println(train.toString()+" is waiting to use section "+this.toString());
			wait();
		}
		System.out.println(train.toString()+" has entered "+this.toString());
		this.inUse = true;
	}

	public synchronized void setInUse(boolean inUse){
		this.inUse = inUse;
	}

	public Section(String name) {
		super(name);
	}
}
