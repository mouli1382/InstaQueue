package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.commonsware.cwac.preso.PresentationService;

import in.mobifirst.tagtree.R;

public class TokenDisplayService extends PresentationService implements
        Runnable {
    public final static String SNAP_BITMAP_VIEW = "snap_view_bitmap";
    private Handler handler = null;
    private Bitmap bitmap = null;
    private ImageView iv = null;

    @Override
    public void onCreate() {
        handler = new Handler(Looper.getMainLooper());
        super.onCreate();
    }

    @Override
    protected int getThemeId() {
        return (R.style.AppTheme);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bitmap = intent.getParcelableExtra(SNAP_BITMAP_VIEW);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected View buildPresoView(Context context, LayoutInflater inflater) {
        iv = new ImageView(context);
        run();

        return (iv);
    }

    @Override
    public void run() {
        iv.setImageBitmap(bitmap);
        handler.postDelayed(this, 1000);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(this);

        super.onDestroy();
    }
}