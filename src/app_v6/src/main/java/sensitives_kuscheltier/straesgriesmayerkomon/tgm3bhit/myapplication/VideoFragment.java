package sensitives_kuscheltier.straesgriesmayerkomon.tgm3bhit.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Patrick, 29.05.2015.
 * Shows a photo, taken by the raspberry cam
 */
public class VideoFragment extends Fragment {

    private ImageButton takePhoto;
    private ImageView imageView;

    /**
     * Sets layout_video.xml as root view, adds listeners and sets attributes
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_video, container, false);
        takePhoto = (ImageButton) rootView.findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new OnClickHandler());
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        return rootView;
    }

    /**
     * From stackoverflow...
     * Gets a Bitmap of an image from a specific url
     * @param url the url as string to get the image from
     * @return the Bitmap
     */
    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("APP: ", "Error getting bitmap", e);
        }
        return bm;
    }

    /**
     * Handles clicks on the take photo button
     * BUG: camera is not working properly on raspberry pi - only after installing it inside the teddy.
     * taking a photo crashes the server because of some weird error with the picamera module.
     *
     * if someone tries to take a photo with the app, the command is sent to RPi,
     * the RPi tries to take a photo and sends back the url.
     * All photos are saved on the RPis web server as <timestamp>.jpg
     */
    private class OnClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (((MainActivity)getActivity()).getClientSocket() !=null && ((MainActivity)getActivity()).getClientSocket().isConnected()) {
                if (v == takePhoto) {
                    Log.i("APP: ", "requested photo from RPi");
                    toastOnUiThread("Cheese!", Toast.LENGTH_SHORT);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                ((MainActivity)getActivity()).getClientSocket().sendMessage("takephoto");
                                final String answer = ((MainActivity)getActivity()).getClientSocket().receiveMessage();
                                Log.i("APP: ", "pic @ http://192.168.43.1/" + answer);
                                final Bitmap image = getImageBitmap("http://192.168.43.1/" + answer);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(image);
                                    }
                                });
                            } catch (IOException e) {
                                Log.e("APP: ", "Error sending request, getting answer or displaying bitmap", e);
                                toastOnUiThread("Fehler beim Senden der Anfrage! Nicht verbunden?", Toast.LENGTH_SHORT);
                            }
                        }
                    }.start();
                }
            } else
                toastOnUiThread("Nicht verbunden!", Toast.LENGTH_SHORT);
        }

        /**
         * For posting toasts from another thread
         * @param msg message to display
         * @param length duration of display
         */
        private void toastOnUiThread(final String msg, final int length){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), msg, length).show();
                }
            });
        }

    }
}