package coreClasses;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static utils.Range.range;

/**
 * this class is responsible for the scheduling of the Missions
 */

/*
    You must implement the methods called by the
    mission controller to construct the mission vehicles from components, move missions along their
    stages, check for failures, send reports, send instructions and software updates. It is important to
    represent the communications networks (queues) too
 */
    // The mission controller is a shared resource used for all missions.
public class Controller {
    // 10 is the minimum per the requirements
    public static int defaultNumberOfSimultaneousMissions = 2;

    public Controller() {}

    public void scheduleMissions(int numberOfSimultaneousMissions) {
        // might benefit from a ScheduledExecutorService or a forkJoinPool
        // note that we currently execute as much tasks as the threadPool size so there is no reuse ?
        ExecutorService executor = Executors.newFixedThreadPool(numberOfSimultaneousMissions);

        for (int threadIdx : range(numberOfSimultaneousMissions)) {
            Runnable mission = new Mission();
            executor.execute(mission);
        }
        executor.shutdown();
        // busy wait ?
        while (!executor.isTerminated()) {}

        System.out.println("All the missions have been completed!");
    }
}
