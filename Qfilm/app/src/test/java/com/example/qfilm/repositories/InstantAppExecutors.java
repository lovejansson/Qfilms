package com.example.qfilm.repositories;

import com.example.qfilm.repositories.utils.AppExecutors;

import java.util.concurrent.Executor;

public class InstantAppExecutors extends AppExecutors {

        private static Executor instant = command -> command.run();

        public InstantAppExecutors() {
            super(instant, instant, instant);
        }
}
