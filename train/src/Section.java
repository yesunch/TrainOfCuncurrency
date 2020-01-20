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
	private Direction usingTrainDirection;

	private final static Logger LOGGER = Logger.getLogger(Section.class.getName());

	public Section(String name) {
		super(name);
		this.inUse = false;
	}


	public synchronized void enter(Train train) throws InterruptedException {
		while (this.checkSectionFull() ){
			LOGGER.info(train.toString()+" is waiting to use section "+this.toString());
			wait();
		}
		this.usingTrainDirection = train.getPos().getDirection();
		this.inUse = true;
		LOGGER.info(train.toString()+" has entered "+this.toString());
	}

	public synchronized void leaveAndEnterElem(Boolean inUse, Train train){
		this.inUse = inUse;
		if(!checkSectionFull()) {
			LOGGER.info(train.toString() + " has gone out of the section " + this.toString());
			this.notifyAll();
		}


	}

	public boolean checkSectionFull(){
		return this.inUse;
	}

	public Direction getUsingTrainDirection() {
		return usingTrainDirection;
	}
}
