import java.util.logging.Logger;

/**
 * Représentation d'une section de voie ferrée. C'est une sous-classe de la
 * classe {@link Element}.
 *
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Section extends Element {

	private boolean inUse = false;
	private final static Logger LOGGER = Logger.getLogger(Section.class.getName());

	public Section(String name) {
		super(name);
		this.inUse = false;
	}


	public synchronized void enter(Train train) throws InterruptedException {
		while (this.checkSectionFull()){
			LOGGER.info(train.toString()+" is waiting to use section "+this.toString());
			wait();
		}
		LOGGER.info(train.toString()+" has entered "+this.toString());
		this.inUse = true;
	}

	public synchronized void setInUse(boolean inUse){

		this.inUse = inUse;
		if(!checkSectionFull())
			this.notifyAll();

	}
	private boolean checkSectionFull(){
		return this.inUse;
	}

	public Section addSection(Section section) {
		return new Section(this.toString()+" and section "+section.toString());
	}
}
