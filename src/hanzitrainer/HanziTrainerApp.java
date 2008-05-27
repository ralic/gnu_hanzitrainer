/*
 * HanziTrainerApp.java
 */
package hanzitrainer;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class HanziTrainerApp extends SingleFrameApplication
{
    HanziTrainerView my_view;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup()
    {
        my_view = new HanziTrainerView(this);
        show(my_view);
    }

    @Override
    protected void shutdown()
    {
        my_view.HanziTrainerViewKill();
    
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root)
    {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of HanziTrainerApp
     */
    public static HanziTrainerApp getApplication()
    {
        return Application.getInstance(HanziTrainerApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args)
    {
        launch(HanziTrainerApp.class, args);
    }
}
