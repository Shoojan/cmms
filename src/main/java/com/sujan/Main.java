package com.sujan;

import com.sujan.config.MyPreloader;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.sujan.config.StageManager;
import com.sujan.view.FxmlView;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class Main extends Application {

    private static final double WIDTH = 800;
    private static final double HEIGHT = 600;

    // Just a counter to create some delay while showing preloader.
    private static final int COUNT_LIMIT = 15000;

    private static int stepCount = 1;

    // Used to demonstrate step couns.
    public static String STEP() {
        return stepCount++ + ". ";
    }

    protected ConfigurableApplicationContext springContext;
    protected StageManager stageManager;

    public static void main(final String[] args) {
        LauncherImpl.launchApplication(Main.class, MyPreloader.class, args);
//        Application.launch(args);
    }

    public Main() {
        // Constructor is called after BEFORE_LOAD.
        System.out.println(Main.STEP() + "MyApplication constructor called, thread: " + Thread.currentThread().getName());
    }

    @Override
    public void init() {
        System.out.println(Main.STEP() + "MyApplication#init (doing some heavy lifting), thread: " + Thread.currentThread().getName());

        Application application = this;
        Task preloaderTask = new Task<Void>() {
            @Override
            protected Void call() {
                for (int i = 0; i < COUNT_LIMIT; i++) {
                    double progress = (double) (100 * i) / COUNT_LIMIT;
                    LauncherImpl.notifyPreloader(application, new Preloader.ProgressNotification(progress));
                }
                return null;
            }
        };
        new Thread(preloaderTask).start();

        // Perform some heavy lifting (i.e. database start, check for application updates, etc. )
        springContext = springBootApplicationContext();

    }

    @Override
    public void start(Stage stage) {
        System.out.println(Main.STEP() + "MyApplication#start (initialize and show primary application stage), thread: " + Thread.currentThread().getName());

        stageManager = springContext.getBean(StageManager.class, stage);
        displayInitialScene();
    }

    @Override
    public void stop() {
        springContext.close();
    }

    /**
     * Useful to override this method by sub-classes wishing to change the first
     * Scene to be displayed on startup. Example: Functional tests on main
     * window.
     */
    private void displayInitialScene() {
        stageManager.switchScene(FxmlView.LOGIN);
    }

    @SuppressWarnings("rawtypes")
    @Bean
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:conf/cmms_db.db");
        return dataSourceBuilder.build();
    }

    private ConfigurableApplicationContext springBootApplicationContext() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        String[] args = getParameters().getRaw().toArray(new String[0]);
        return builder.run(args);
    }

}
