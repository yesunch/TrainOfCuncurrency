# Project Les Trains 

**By Ivonne ROA RODRÍGUÉZ, Ye SUN, 01 Feb, 2020**

In this file, we will specify all the elements necessary to understand, compile and execute our work on the project Trains.

## Development environment
We have chosen **IntelliJ IDEA** as the IDE, and **Git** with **Github** as our version control and remote collaboration tool. Here is [**our repository**](https://github.com/yesunch/TrainOfCuncurrency) on Github for this project.

## Our solution
### For one train

To simulate the behavior of a train, the runnable class *Train* has two attributes *originalStation* and *destination* which are instances of *Station*. And once the Train starts to run, it requires Railway to calculate the route from its *originalStation*  to its *destination*. This route is returned as a list of *Element*. Then, for each element *elem* of the list, it tries to go to *elem* in its method *gotoNextStation()* until it reaches its destination. 

In the method *gotoNextStation*, we used two variables *oldPos* and *nextElem* to indecate the current position of train and the next *Element* it's trying to enter. The train will firstly check if it's in a station or a section and call the appropriate leaving function. Secondly, it polls the first element out of the route list as the *nextElem*, calls the appropriate enter function, and finally update its current position. Finally, it will check whether the latest current position is exactly its destination, if it's the case, it will stop.

As soon as it arrives the destination, the train will swich its destination and original station in the *run()* to continue running.

### For several trains
We have defined three variables for *Section* and *Station* to synchronize trains.
+ Section:
	+ boolean inUse; // *If inUse is true, it means that there is a train using it.*

+ Station: 
	+ int size; // *The maximum capacity of the train station to accept trains *
	+ int nbTrains; // *The actuel number of trains who are in the station, and it must be less than size*

So the safty invariances using these variables are:
+ this.inUse //* In each Section we must ensure that there is always only one train per section.*

+ this.nbTrains <= this.size  //* In the Station class, it must be ensured that the number of trains in the Station is always below the maximum authorized.*

#### Section
Each time when a train wants to enter a  section, it can only succeed when the inUse attribute of this section is false which means there's only one train permitted to run on a section. In this way, we have the Section's enter method who is in form of :
```java
public synchronized void enterSection(Train train) {
	while(this.inUse) {
		LOGGER.info(train+" is waiting to use "+this);
		wait();
	}
	LOGGER.info(train+" has entered "+this);
	this.inUse = true;
}
```

#### Station
Each time a train tries to enter a station, the actual number of trains in this station must be less than its maximum capacity-1. With the safty invariance described above, the synchronized methods enter and leave of class *Station* are in form of:
```java
public synchronized void enterStation(Train train) {
	while(this.checkStationCapacity()) {
		LOGGER.info(train+" is waiting to enter "+this+", cause the station is full now");
		wait();
	}
	LOGGER.info(train+" has entered "+this);
	this.nbTrains ++;
	notifyAll();
}
```
### Break the deadlock
To break the deadlock, we adopted the strategy of requesting all of the resources needed by a thread at once.

Since the train already has its route to its destination as soon as it departs from its original station, it's possible to calculate all of the sections it needs to enter when it tries to leave a station. So in this case, instead of just requesting only the next section, the train will ask to use all of sections between its current station and next station. In this way, we are able to avoid the deadlock situation.

And also we added a variable *direction* to *Section* to avoid the deadlock. A section's *direction* keeps same with the direction of the train running on it. And when a train requests sections between two stations, we calculate two values: *lr* and *rl*. *lr* is the number of sections which is free and has the direction Direction.LR  among the sections requested. *rl* is the number of sections which is free and has the direction Direction.RL  among the sections requested.  The condition that a train can use all of the sections requested is:

**lr * (rl+1) == 0** if the train has the direction of RL
**(lr+1) * rl == 0** if the train has the direction of LR

This condition is verified in the method *tryEnterSections* of Railway each time a train tries to leave its current station. And if the condition is satisfied, it enters the sections and next station, return the next station. Otherwise it returns the current station which means the train can't leave its current station.



## Difficulties & Limitations
### The initialization of traffic network
Currently we are only capable of creating the linear network and calculate the route on the linear network. And we have to define the id of each *Element* manually when we create the network. These id will be used to decide the direction of train(from an element with smaller id to an element with bigger id is Direction.RL). This will bring extra complexity when we create the network topology.
### Low concurrency performance
Since the method *tryEnterSection()* of Railway is synchronized, and each train which needs to leave a station will try to call this method, only one train can verify whether it can leave a station or not at one time.

## To compile and execute the project

Clone the project: `git clone git@github.com:yesunch/TrainOfCuncurrency.git`

Enter the source directory: `cd TrainOfCuncurrency/train/src`

Compile the project: `javac -d ../bin Main.java`

Execute: `java -cp ../bin Main`

### End