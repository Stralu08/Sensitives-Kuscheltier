package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.io.IOException;

/**
 * MainActivity class, holds all the Fragments, handles behavior on start and stop
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private HomeFragment home;
    private VideoFragment video;
    private NewAudioFragment audio;
    private BabyfonFragment babyfon;

    private ClientSocket clientSocket;

    /**
     * sets up fragments and tries to connect to server
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        home = new HomeFragment();
        video = new VideoFragment();
        audio = new NewAudioFragment();
        babyfon = new BabyfonFragment();
        setContentView(R.layout.activity_main);
        new ConnectSocketThread("192.168.43.1", 5555).start();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    /**
     * Called on stop of the activity:
     * quits connection to server
     */
    @Override
    protected void onStop() {
        if(clientSocket!=null && clientSocket.isConnected()){
            try{
                clientSocket.destroy();
                Log.i("APP: ", "closed current connection");
            }catch (IOException e){
                Log.i("APP: ", "failed to close current connection, maybe the connection was already closed?", e);
            }
        }
        super.onStop();
    }

    /**
     * Returns the ClientSocket
     * @return clientSocket
     */
    public ClientSocket getClientSocket(){
        return clientSocket;
    }

    /**
     * A Thread for establishing a new connection
     */
    public class ConnectSocketThread extends Thread {

        private String ip;
        private int port;

        /**
         * Creates a new ConnectSocketThread instance for connection to given hostname and pprt
         * @param ip the hostname or ip as string
         * @param port the port
         */
        public ConnectSocketThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        /**
         * Tries to connect and sets the attribute of the activity
         */
        @Override
        public void run() {
            Log.i("APP: ", "trying to connect to " + ip + "@" + port);
            ClientSocket socket = new ClientSocket();
            int success = socket.connect(ip, port);
            clientSocket = socket;
            if (success == ClientSocket.CONNECTED) {
                Log.i("APP: ", "connection successfully established");
                toastOnUiThread("Verbunden!", Toast.LENGTH_SHORT);
            } else {
                Log.i("APP: ", "connection failed");
                toastOnUiThread("Verbinden fehlgeschlagen!", Toast.LENGTH_SHORT);
            }
        }

        /**
         * For posting toasts from another thread
         * @param msg message to display
         * @param length duration of display
         */
        private void toastOnUiThread(final String msg, final int length){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), msg, length).show();
                }
            });
        }
    }

    /**
     * Sets the fragment depending on the selected item in the list view of the fragment drawer.
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, home)
                        .commit();
                mTitle = getString(R.string.title_home);
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, video)
                        .commit();
                mTitle = getString(R.string.title_video);
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, audio)
                        .commit();
                mTitle = getString(R.string.title_audio);
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, babyfon)
                        .commit();
                mTitle = getString(R.string.title_babyfon);
                break;
        }
    }

    /**
     * Sets title (displayed on the action bar) depending on the selected
     * item in the list view of the fragment drawer.
     * @param number
     */
    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_home);
                break;
            case 1:
                mTitle = getString(R.string.title_video);
                break;
            case 2:
                mTitle = getString(R.string.title_audio);
                break;
            case 3:
                mTitle = getString(R.string.title_babyfon);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * poorly implemented:
     * Fragment to welcome the user:
     * has only a placeholder imageview for the logo...
     * planned: logo and a short description of the app and its function for the user, shown below
     * the logo
     */
    public static class HomeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.layout_home, container, false);
            return rootView;
        }
    }
}
