package id.diana.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;

/** Menampilkan tumpukan error crash supaya bisa dibaca / di-screenshot. */
public class CrashActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(
                new FileReader(getApplicationInfo().dataDir + "/" + App.CRASH_FILE))) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append('\n');
        } catch (Exception e) {
            sb.append("Gak bisa baca log crash: ").append(e);
        }
        final String teks = sb.toString();

        TextView tv = new TextView(this);
        tv.setText("DIANA CRASH — screenshot layar ini terus kirim ke developer.\n\n"
                + "(ketuk buat salin teksnya)\n\n" + teks);
        tv.setTextColor(Color.parseColor("#3d2b1f"));
        tv.setBackgroundColor(Color.parseColor("#faf6ef"));
        tv.setTypeface(android.graphics.Typeface.MONOSPACE, 0);
        tv.setTextSize(11f);
        tv.setPadding(24, 24, 24, 24);
        tv.setTextIsSelectable(true);
        tv.setOnClickListener(v -> {
            ClipboardManager cb = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            cb.setPrimaryClip(ClipData.newPlainText("crash", teks));
            tv.setText("✓ Kesalin. Tempel di Telegram.");
        });

        ScrollView sc = new ScrollView(this);
        sc.addView(tv);
        setContentView(sc);
    }
}
