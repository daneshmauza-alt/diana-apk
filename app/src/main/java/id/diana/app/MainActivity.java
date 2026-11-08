package id.diana.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** Pembungkus WebView buat web app Diana. Nol dependency eksternal. */
public class MainActivity extends Activity {

    private static final String URL = "https://danesh.pythonanywhere.com";

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getApplication()).startBersih();
        try {
            mulai();
        } catch (Throwable e) {
            App.tulis(getApplication(), App.CRASH_FILE,
                    "crash di MainActivity.onCreate:\n"
                            + android.util.Log.getStackTraceString(e));
            throw e;
        }
    }

    private void mulai() {
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);   // React app butuh localStorage
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView v, WebResourceRequest req,
                                        WebResourceError err) {
                // Cuma tampilin halaman offline kalo main frame-nya yang gagal,
                // bukan tiap gambar/API yang nyasar.
                if (req.isForMainFrame()) {
                    v.loadDataWithBaseURL(null, OFFLINE_HTML,
                            "text/html", "UTF-8", null);
                }
            }
        });
        setContentView(web);
        web.loadUrl(URL);
    }

    /** Tombol back: muter riwayat dulu, abis itu baru keluar. */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (web != null) web.destroy();
        super.onDestroy();
    }

    private static final String OFFLINE_HTML =
            "<body style=\"font-family:sans-serif;background:#faf6ef;color:#3d2b1f;"
            + "display:flex;align-items:center;justify-content:center;"
            + "height:100vh;margin:0;text-align:center\">"
            + "<div>"
            + "<h2>Koneksi ke server Diana putus</h2>"
            + "<p style=\"color:#8a7a68\">Cek WiFi/quota, terus coba lagi.</p>"
            + "<button onclick=\"location.href='" + URL + "'\""
            + "style=\"background:#6b4a2f;color:#faf6ef;border:0;"
            + "border-radius:8px;padding:12px 28px;font-size:16px\">"
            + "Coba lagi</button>"
            + "</div></body>";
}
