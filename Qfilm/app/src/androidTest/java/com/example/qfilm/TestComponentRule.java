package com.example.qfilm;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.qfilm.di.AppModule;
import com.example.qfilm.di.DaggerTestApplicationComponent;
import com.example.qfilm.di.TestApplicationComponent;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


public class TestComponentRule implements TestRule {

    private static final String TAG = "TestComponentRule";

    private final MainApplication application;

    public TestComponentRule(Application application) {

        this.application = (MainApplication) application;

    }


    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                TestApplicationComponent testApplicationComponent = DaggerTestApplicationComponent.builder()
                        .appModule(new AppModule(application))
                        .build();
                application.setApplicationComponent(testApplicationComponent);
                
                base.evaluate();

                application.setApplicationComponent(null);

            }
        };
    }
}
