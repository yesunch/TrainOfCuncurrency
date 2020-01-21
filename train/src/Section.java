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
	private Direction usingTrainDirection;

	public Section(String name) {
		super(name);
		this.inUse = false;
	}


	public synchronized void enter(Train train) throws InterruptedException {
		while (this.checkSectionFull() || (this.usingTrainDirection != null && this.usingTrainDirection != train.getPos().getDirection())){
			LOGGER.info("For "+train.toString()+", the direction of "+this.toString()+" is "+this.usingTrainDirection+
					" his direction is "+train.getPos().getDirection()+
					"and the section inUse is "+this.checkSectionFull());
			LOGGER.info(train.toString()+" is waiting to use section "+this.toString());
			wait();
		}
		LOGGER.info(train.toString()+" has entered "+this.toString());
		this.inUse = true;
		this.usingTrainDirection = train.getPos().getDirection();
	}

	public synchronized void setInUse(boolean inUse){

		this.inUse = inUse;
		if(!checkSectionFull()) {
			this.usingTrainDirection = null;
			this.notifyAll();
		}

	}
	public boolean checkSectionFull(){
		return this.inUse;
	}

	public Section addSection(Section section) {
		return new Section(this.toString()+" and section "+section.toString());
	}
	public Direction getUsingTrainDirection() {
		return usingTrainDirection;
	}
}
