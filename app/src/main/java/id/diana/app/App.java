package id.diana.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

/**
 * Pasang penangkap crash dari titik paling awal (attachBaseContext),
 * sebelum layar apa pun sempat muncul.
 */
public class App extends Application {
    static final String CRASH_FILE = "crash.txt";

    static String path(Context ctx, String name) {
        return ctx.getApplicationInfo().dataDir + "/" + name;
    }

    static void tulis(Context ctx, String file, String isi) {
        try (FileWriter w = new FileWriter(path(ctx, file))) {
            w.write(isi);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        tulis(this, "boot.txt", "proses nyala: " + System.currentTimeMillis());
        final Thread.UncaughtExceptionHandler prev =
                Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            tulis(this, CRASH_FILE, Log.getStackTraceString(e));
            Intent i = new Intent(this, CrashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(i);
            } catch (Exception ignored) {
            }
            if (prev != null) prev.uncaughtException(t, e);
            Process.killProcess(Process.myPid());
            System.exit(1);
        });
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();
        } catch (Throwable e) {
            tulis(this, CRASH_FILE, "crash di App.onCreate:\n" + Log.getStackTraceString(e));
            throw e;
        }
    }

    /** Bersihin jejak crash lama setiap start sukses. */
    public void startBersih() {
        new File(path(this, CRASH_FILE)).delete();
    }
}
