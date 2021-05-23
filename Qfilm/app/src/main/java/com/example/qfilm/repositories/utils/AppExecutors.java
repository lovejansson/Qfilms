package com.example.qfilm.repositories.utils;

import java.util.concurrent.Executor;

/**
 *
 * Provides one Executor to do background work on and one Executor to post results on the main thread.
 * This is used in the NetworkBoundResource class.
 *
 * **/

public class AppExecutors {

    private Executor backgroundThread;

    private Executor mainThread;

    private Executor repositoryThread;

    public AppExecutors(Executor backgroundThread, Executor mainThread,
                        Executor repositoryThread) {

        this.backgroundThread = backgroundThread;

        this.mainThread = mainThread;

        this.repositoryThread = repositoryThread;
    }

    public Executor getBackgroundThread() {
        return backgroundThread;
    }


    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getRepositoryThread(){
        return repositoryThread;
    }

}
