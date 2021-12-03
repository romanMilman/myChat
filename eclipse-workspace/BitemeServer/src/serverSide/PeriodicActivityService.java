package serverSide;

import common.Logger;
import common.Logger.Level;


/**
 * PeriodicActivityService needs methods like:
 * UpdateEveryMidnight() - resets values at midnight (Business client credit)
 * 
 * */
public class PeriodicActivityService {

	public void start() {
		// log
		Logger.log(Level.DEBUG, "PeriodicActivityService : Periodic activity service started");
		System.out.println("PeriodicActivityService : Periodic activity service started");
	}
}
