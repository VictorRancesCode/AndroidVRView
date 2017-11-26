package com.codigopanda.androidvrview;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.bumptech.glide.Glide;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.pano.VrPanoramaView.Options;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;


public class clsPrincipal extends AppCompatActivity {
    private static final String TAG = clsPrincipal.class.getSimpleName();
    private VrPanoramaView panoWidgetView;
    private VrPanoramaView panoWidgetViewOnline;
    public boolean loadImageSuccessful;

    Button boton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fprincipal);

        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_offline);
        panoWidgetViewOnline = (VrPanoramaView) findViewById(R.id.pano_online);
        handleIntent(getIntent());
        boton=(Button) findViewById(R.id.videovr);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(clsPrincipal.this,clsVideo.class));
            }
        });
    }
    private void handleIntent(Intent intent) {
        new ImageLoaderVR().execute(new Item("ciudad.jpg", "offline", panoWidgetView));
        new ImageLoaderVR().execute(new Item("http://i0.bookcdn.com/data/BookedPanorams/OriginalPhoto/0/20/20496.JPEG", "online", panoWidgetViewOnline));

    }
    class ImageLoaderVR extends AsyncTask<Item, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Item... options) {
            Options panoOptions = null;
            InputStream istr = null;
            AssetManager assetManager = getAssets();
            panoOptions = new Options();
            panoOptions.inputType = Options.TYPE_MONO;
             // panoOptions.inputType = Options.TYPE_STEREO_OVER_UNDER;

            if (options[0].type.compareTo("offline") == 0) {
                try {
                    istr = assetManager.open(options[0].path);
                    options[0].vrview.loadImageFromBitmap(BitmapFactory.decodeStream(istr), panoOptions);
                } catch (IOException e) {
                    Log.e(TAG, "Could not decode default bitmap: " + e);
                    return false;
                }
            } else {
                Bitmap mibitmap = null;
                try {
                    mibitmap = Glide.
                            with(clsPrincipal.this).
                            load(options[0].path).
                            asBitmap().
                            into(2048, 1024). // Width and height
                            get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                options[0].vrview.loadImageFromBitmap(mibitmap, panoOptions);
            }


            return true;
        }
    }
    public class Item {
        public String path;
        public String type;
        public VrPanoramaView vrview;

        public Item(String path, String type, VrPanoramaView vrview) {
            this.path = path;
            this.type = type;
            this.vrview = vrview;
        }
    }
}
