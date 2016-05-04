package com.eyssyapps.fypcms.activities.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.eyssyapps.fypcms.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReadNewsPostActivity extends Activity
{
    public static final String WEBVIEW_CONTENT = "webview_content";

    @Bind (R.id.newpost_webview)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news_post);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            loadHtmlWithBootstrapAssets(extras.getString(ReadNewsPostActivity.WEBVIEW_CONTENT));
        }
        else
        {
            finishActivity();
        }
    }

    @Override
    public void onBackPressed()
    {
        this.finishActivity();
    }

    private void finishActivity()
    {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        finish();
    }

    protected void loadHtmlWithBootstrapAssets(String html)
    {
        if (Build.VERSION.SDK_INT >= 19)
        {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else
        {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
            sb.append("<head>");
                sb.append("<link href=\"bootstrap.min.css\" type=\"text/css\" rel=\"stylesheet\"/>");
                sb.append("<script src=\"bootstrap.min.js\" type=\"text/javascript\" ></script>");
            sb.append("</head>");
            sb.append("<body>");
                sb.append("<div class=\"container-fluid\">");
                    sb.append("<div class=\"row\">");
                        sb.append("<div class=\"col-sm-12\">");
                            sb.append(html);
                        sb.append("</div>");
                    sb.append("</div>");
                sb.append("</div>");
            sb.append("</body>");
        sb.append("</html>");

        webView.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "UTF-8", null);
    }
}