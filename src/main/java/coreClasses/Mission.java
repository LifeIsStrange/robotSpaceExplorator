package coreClasses;

import utils.Range;
import utils.Utils;

/**
 * this class is responsible for the scheduling of a Mission
 */

public class Mission implements Runnable {
    /** time is simulated by considering months as seconds */
    static long defaultMinStageTime = 1000L;
    static long defaultMaxStageTime = 5000L;


    // A variable burst of reports and
    // commands are sent at the transition between mission stages.
    // There are a variable number of types
    // of commands and reports for each mission.
    private void transitStage() {
        System.out.println("entering interplanetary transit stage..");
        try {
            // todo failure rate 10% min
            
            Thread.sleep(Utils.getRandomNumberInRange(defaultMinStageTime, defaultMaxStageTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Time can simulated by allowing a fixed ratio of wall clock time to mission time eg 1 sec : 1
    // month.
    // When waiting a mission 'sleeps'.
    private void explorationStage() {
        System.out.println("entering exploration stage..");
        try {
            Thread.sleep(Utils.getRandomNumberInRange(defaultMinStageTime, defaultMaxStageTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // todo allocate resources
    private void scheduleStages() {
        System.out.println("entering boost stage..");
        this.transitStage();
        System.out.println("entering landing stage..");
        this.explorationStage();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Mission starting.");
        scheduleStages();
    }
}
