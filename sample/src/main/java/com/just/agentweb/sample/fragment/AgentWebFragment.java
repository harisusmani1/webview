package com.just.agentweb.sample.fragment;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;
import com.download.library.ResourceRequest;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.gson.Gson;
import com.just.agentweb.AbsAgentWebSettings;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.DefaultDownloadImpl;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.MiddlewareWebChromeBase;
import com.just.agentweb.MiddlewareWebClientBase;
import com.just.agentweb.PermissionInterceptor;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebListenerManager;
import com.just.agentweb.filechooser.FileCompressor;
import com.just.agentweb.sample.R;
import com.just.agentweb.sample.app.App;
import com.just.agentweb.sample.client.MiddlewareChromeClient;
import com.just.agentweb.sample.client.MiddlewareWebViewClient;
import com.just.agentweb.sample.common.CommonWebChromeClient;
import com.just.agentweb.sample.common.FragmentKeyDown;
import com.just.agentweb.sample.common.UIController;
import com.just.agentweb.sample.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import top.zibin.luban.Luban;

/**
 * Created by cenxiaozhong on 2017/5/15.
 * Source code: https://github.com/Justson/AgentWeb
 */

public class AgentWebFragment extends Fragment implements FragmentKeyDown, FileCompressor.FileCompressEngine {

    private ImageView mBackImageView;
    private View mLineView;
    private ImageView mFinishImageView;
    private TextView mTitleTextView;
    protected AgentWeb mAgentWeb;
    public static final String URL_KEY = "url_key";
    private ImageView mMoreImageView;
    private PopupMenu mPopupMenu;
    /**
     * For convenient printing and testing
     */
    private Gson mGson = new Gson();
    public static final String TAG = AgentWebFragment.class.getSimpleName();
    private MiddlewareWebClientBase mMiddleWareWebClient;
    private MiddlewareWebChromeBase mMiddleWareWebChrome;

    public static AgentWebFragment getInstance(Bundle bundle) {

        AgentWebFragment mAgentWebFragment = new AgentWebFragment();
        if (bundle != null) {
            mAgentWebFragment.setArguments(bundle);
        }

        return mAgentWebFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agentweb, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (getActivity() == null) {
            Log.e(TAG, "Activity is null in onViewCreated");
            return;
        }


        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent((LinearLayout) view, -1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)) // Pass in AgentWeb's parent control.
                .useDefaultIndicator(-1, 3) // Set progress bar color and height, -1 is default value, height is 2, unit is dp.
                .setAgentWebWebSettings(getSettings()) // Set IAgentWebSettings.
                .setWebViewClient(mWebViewClient) // WebViewClient, consistent with WebView usage, but please do not get WebView to call setWebViewClient(xx) method, it will override AgentWeb DefaultWebClient, and corresponding middleware will also fail.
                .setWebChromeClient(new CommonWebChromeClient()) // WebChromeClient
                .setPermissionInterceptor(mPermissionInterceptor) // Permission interception added in 2.0.0.
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) // Strict mode, Android 4.2.2 and below will abandon injected objects, using AgentWebView has no effect.
                .setAgentWebUIController(new UIController(getActivity())) // Custom UI added in AgentWeb 3.0.0.
                .setMainFrameErrorView(com.just.agentweb.R.layout.agentweb_error_page, -1) // Parameter 1 is the error display layout, parameter 2 click refresh control ID -1 means clicking the entire layout will refresh, added in AgentWeb 3.0.0.
                .useMiddlewareWebChrome(getMiddlewareWebChrome()) // Set WebChromeClient middleware, support multiple WebChromeClient, added in AgentWeb 3.0.0.
                .additionalHttpHeader(getUrl(), "cookie", "41bc7ddf04a26b91803f6b11817a5a1c")
                .useMiddlewareWebClient(getMiddlewareWebClient()) // Set WebViewClient middleware, support multiple WebViewClient, added in AgentWeb 3.0.0.
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) // When opening other pages, popup to ask user to go to other applications, added in AgentWeb 3.0.0.
                .interceptUnkownUrl() // Intercept URLs that cannot find related pages, added in AgentWeb 3.0.0.
                .createAgentWeb() // Create AgentWeb.
                .ready() // Set WebSettings.
                .go(getUrl()); // WebView loads and displays the page at this URL address.


        AgentWebConfig.debug();

        initView(view);


        // AgentWeb does not fully cover WebView functionality, so some settings that AgentWeb does not provide, please set from WebView side.
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        // mAgentWeb.getWebCreator().getWebView() get WebView.

        // mAgentWeb.getWebCreator().getWebView().setOnLongClickListener();

//		Runtime.getInstance().setFileComparatorFactory(new FileComparator.FileComparatorFactory() {
//			@Override
//			public FileComparator newFileComparator() {
//				return new FileComparator() {
//					@Override
//					public int compare(String url, File originFile, String inputMD5, String originFileMD5) {
//						return FileComparator.COMPARE_RESULT_SUCCESSFUL;
//					}
//				};
//			}
//		});
    }


    protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

        /**
         * PermissionInterceptor 能达到 url1 允许授权， url2 拒绝授权的效果。
         * PermissionInterceptor can achieve the effect of url1 allowing authorization and url2 denying authorization.
         * @param url
         * @param permissions
         * @param action
         * @return true intercept permission requests for pages corresponding to this URL, false means no interception.
         */
        @Override
        public boolean intercept(String url, String[] permissions, String action) {
            Log.i(TAG, "mUrl:" + url + "  permission:" + mGson.toJson(permissions) + " action:" + action);
            return false;
        }
    };


    /**
     * @return IAgentWebSettings
     */
    public IAgentWebSettings getSettings() {
        return new AbsAgentWebSettings() {
            private AgentWeb mAgentWeb;

            @Override
            protected void bindAgentWebSupport(AgentWeb agentWeb) {
                this.mAgentWeb = agentWeb;
            }

            /**
             * AgentWeb 4.0.0 internally removed DownloadListener monitoring and related APIs, completely extracted the Download part into an independent library.
             * If you need to use AgentWeb Download part, please depend on compile 'com.download.library:Downloader:4.1.1',
             * If you need to listen to download results, please customize AgentWebSetting, create DefaultDownloadImpl
             * to implement progress or result listening. For example, in the following example, if you don't need to listen to progress or download results, the setDownloader example below can be ignored.
             * @param webView
             * @param downloadListener
             * @return WebListenerManager
             */
            @Override
            public WebListenerManager setDownloader(WebView webView, android.webkit.DownloadListener downloadListener) {
                return super.setDownloader(webView,
                        new DefaultDownloadImpl(getActivity(),
                                webView,
                                this.mAgentWeb.getPermissionInterceptor()) {

                            @Override
                            protected ResourceRequest createResourceRequest(String url) {
                                return DownloadImpl.getInstance(getContext())
                                        .url(url)
                                        .quickProgress()
                                        .addHeader("", "")
                                        .setEnableIndicator(true)
                                        .autoOpenIgnoreMD5()
                                        .setRetry(5)
                                        .setBlockMaxTime(100000L);
                            }

                            @Override
                            protected void taskEnqueue(ResourceRequest resourceRequest) {
                                resourceRequest.enqueue(new DownloadListenerAdapter() {
                                    @Override
                                    public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                                        super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);
                                    }

                                    @MainThread
                                    @Override
                                    public void onProgress(String url, long downloaded, long length, long usedTime) {
                                        super.onProgress(url, downloaded, length, usedTime);
                                    }

                                    @Override
                                    public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                                        return super.onResult(throwable, path, url, extra);
                                    }
                                });
                            }
                        });
            }
        };
    }

    /**
     * If page is blank, please check if scheme is added, scheme://host:port/path?query&query.
     *
     * @return URL
     */
    public String getUrl() {
        String target = "";

        if (TextUtils.isEmpty(target = this.getArguments().getString(URL_KEY))) {
            target = "http://cw.gzyunjuchuang.com/";
        }

//		return "http://ggzy.sqzwfw.gov.cn/WebBuilderDS/WebbuilderMIS/attach/downloadZtbAttach.jspx?attachGuid=af982055-3d76-4b00-b5ab-36dee1f90b11&appUrlFlag=sqztb&siteGuid=7eb5f7f1-9041-43ad-8e13-8fcb82ea831a";
        return target;
    }

    protected com.just.agentweb.WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Log.i(TAG, "onProgressChanged:" + newProgress + "  view:" + view);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mTitleTextView != null && !TextUtils.isEmpty(title)) {
                if (title.length() > 10) {
                    title = title.substring(0, 10).concat("...");
                }
            }
            mTitleTextView.setText(title);
        }
    };
    /**
     * Note: When overriding WebViewClient methods, super.xxx() must be called correctly. If super.xxx() is not called, DefaultWebClient methods cannot be executed
     * which may affect AgentWeb's built-in functionality. Try to call super.xxx() to complete the onion model
     */
    protected com.just.agentweb.WebViewClient mWebViewClient = new com.just.agentweb.WebViewClient() {

        private HashMap<String, Long> timer = new HashMap<>();
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e(TAG, "WebView error: " + errorCode + " - " + description + " for URL: " + failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e(TAG, "WebView resource error: " + error.getErrorCode() + " - " + error.getDescription());
            }
            super.onReceivedError(view, request, error);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        //
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {

            Log.i(TAG, "view:" + new Gson().toJson(view.getHitTestResult()));
            Log.i(TAG, "mWebViewClient shouldOverrideUrlLoading:" + url);
            // Youku wants to wake up its own app to play the video. If the intercepted address below returns true, it will play H5 in the app and prohibit Youku from waking up to play the video. If it returns false, DefaultWebClient will handle the address according to the intent protocol. First match whether the app exists. If it exists, wake up the app to play. If it doesn't exist, jump to the app market to download the app.
            if (url.startsWith("intent://") && url.contains("com.youku.phone")) {
                return true;
            }
			/* else if (isAlipay(view, mUrl))   // Starting from 1.2.5, you don't need to call this method anymore. Just import the Alipay SDK. DefaultWebClient will handle the corresponding URL to call Alipay by default
			    return true; */
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i(TAG, "mUrl:" + url + " onPageStarted  target:" + getUrl());
            timer.put(url, System.currentTimeMillis());
            if (url.equals(getUrl())) {
                pageNavigator(View.GONE);
            } else {
                pageNavigator(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (timer.get(url) != null) {
                long overTime = System.currentTimeMillis();
                Long startTime = timer.get(url);
                Log.i(TAG, "  page mUrl:" + url + "  used time:" + (overTime - startTime));
            }

        }
        /* Error page callback this method. If this method is overridden, the layout passed in above will not be displayed and will be implemented by the developer. Pay attention to parameter alignment. */
	   /* public void onMainFrameError(AbsAgentWebUIController agentWebUIController, WebView view, int errorCode, String description, String failingUrl) {

            Log.i(TAG, "AgentWebFragment onMainFrameError");
            agentWebUIController.onMainFrameError(view,errorCode,description,failingUrl);

        }*/

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);

//			Log.i(TAG, "onReceivedHttpError:" + 3 + "  request:" + mGson.toJson(request) + "  errorResponse:" + mGson.toJson(errorResponse));
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

//			Log.i(TAG, "onReceivedError:" + errorCode + "  description:" + description + "  errorResponse:" + failingUrl);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Starting from 2.0.0, this API is deprecated with no API replacement. Use ActionActivity to bypass this method to lower the usage threshold. This API is removed in 4.0.0.
         */
//        mAgentWeb.uploadFileResult(requestCode, resultCode, data);
    }

    private SimpleSearchView mSimpleSearchView;
    private ImageView mSearchImageView;

    protected void initView(View view) {
        mBackImageView = (ImageView) view.findViewById(R.id.iv_back);
        mLineView = view.findViewById(R.id.view_line);
        mFinishImageView = (ImageView) view.findViewById(R.id.iv_finish);
        mTitleTextView = (TextView) view.findViewById(R.id.toolbar_title);
        mBackImageView.setOnClickListener(mOnClickListener);
        mFinishImageView.setOnClickListener(mOnClickListener);
        mMoreImageView = (ImageView) view.findViewById(R.id.iv_more);
        mMoreImageView.setOnClickListener(mOnClickListener);
        mSearchImageView = view.findViewById(R.id.iv_search);
        mSearchImageView.setOnClickListener(mOnClickListener);
        mSimpleSearchView = view.findViewById(R.id.search_view);
        pageNavigator(View.GONE);
        mSimpleSearchView.setHint("Please enter URL");
        EditText editText = mSimpleSearchView.findViewById(com.ferfalk.simplesearchview.R.id.searchEditText);
        editText.setImeOptions(EditorInfo.IME_ACTION_GO);
//        mSimpleSearchView.setSearchBackground(new ColorDrawable(getColorPrimary()));
        mSimpleSearchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String completeUrl = s;
                if (!completeUrl.startsWith("http")) {
                    completeUrl = "http://" + s;
                }
                mAgentWeb.getUrlLoader().loadUrl(completeUrl);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                return false;
            }
        });
        FileCompressor.getInstance().registerFileCompressEngine(this);

    }

//    public int getColorPrimary() {
//        TypedValue typedValue = new TypedValue();
//        requireActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
//        return typedValue.data;
//    }


    private void pageNavigator(int tag) {

        mBackImageView.setVisibility(tag);
        mLineView.setVisibility(tag);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.iv_back:
                    // true means AgentWeb handled this event
                    if (!mAgentWeb.back()) {
                        AgentWebFragment.this.getActivity().finish();
                    }
                    break;
                case R.id.iv_finish:
                    AgentWebFragment.this.getActivity().finish();
                    break;
                case R.id.iv_more:
                    showPoPup(v);
                    break;
                case R.id.iv_search:
                    mSimpleSearchView.showSearch();
                    break;
                default:
                    break;

            }
        }

    };

    /**
     * Open browser
     *
     * @param targetUrl URL to open in external browser
     */
    private void openBrowser(String targetUrl) {
        if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
            Toast.makeText(this.getContext(), targetUrl + " This link cannot be opened with browser.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri mUri = Uri.parse(targetUrl);
        intent.setData(mUri);
        startActivity(intent);
    }


    /**
     * Show more menu
     *
     * @param view Menu attached below this View
     */
    private void showPoPup(View view) {
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(this.getActivity(), view);
            mPopupMenu.inflate(R.menu.toolbar_menu);
            mPopupMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);
        }
        mPopupMenu.show();
    }

    /**
     * Menu events
     */
    private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {

                case R.id.refresh:
                    if (mAgentWeb != null) {
                        mAgentWeb.getUrlLoader().reload(); // Refresh
                    }
                    return true;

                case R.id.copy:
                    if (mAgentWeb != null) {
                        toCopy(AgentWebFragment.this.getContext(), mAgentWeb.getWebCreator().getWebView().getUrl());
                    }
                    return true;
                case R.id.default_browser:
                    if (mAgentWeb != null) {
                        openBrowser(mAgentWeb.getWebCreator().getWebView().getUrl());
                    }
                    return true;
                case R.id.default_clean:
                    toCleanWebCache();
                    return true;
                case R.id.error_website:
                    loadErrorWebSite();
                    // Test DownloadingService
//			        LogUtils.i(TAG, " :" + mDownloadingService + "  " + (mDownloadingService == null ? "" : mDownloadingService.isShutdown()) + "  :" + mExtraService);
//                    if (mDownloadingService != null && !mDownloadingService.isShutdown()) {
//                        mExtraService = mDownloadingService.shutdownNow();
//                        LogUtils.i(TAG, "mExtraService::" + mExtraService);
//                        return true;
//                    }
//                    if (mExtraService != null) {
//                        mExtraService.performReDownload();
//                    }

                    return true;
                default:
                    return false;
            }

        }
    };

    /**
     * Test error page display
     */
    private void loadErrorWebSite() {
        if (mAgentWeb != null) {
            mAgentWeb.getUrlLoader().loadUrl("http://www.unkownwebsiteblog.me");
        }
    }

    /**
     * Clear WebView cache
     */
    private void toCleanWebCache() {

        if (this.mAgentWeb != null) {

            // Clear all WebView-related cache, database, history, etc.
            this.mAgentWeb.clearWebCache();
            Toast.makeText(getActivity(), "Cache cleared", Toast.LENGTH_SHORT).show();
            // Clear all AgentWeb disk cache, including WebView cache, AgentWeb downloaded images, videos, apk and other files.
//            AgentWebConfig.clearDiskCache(this.getContext());
        }

    }


    /**
     * Copy string
     *
     * @param context
     * @param text
     */
    private void toCopy(Context context, String text) {

        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));

    }


    @Override
    public void onResume() {
        mAgentWeb.getWebLifeCycle().onResume(); // Resume
        super.onResume();
    }

    @Override
    public void onPause() {

        mAgentWeb.getWebLifeCycle().onPause(); // Pause all WebView in the application. Call mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); to resume.
        super.onPause();
    }

    @Override
    public boolean onFragmentKeyDown(int keyCode, KeyEvent event) {
        if (mSimpleSearchView.onBackPressed()) {
            return true;
        }
        return mAgentWeb.handleKeyEvent(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        FileCompressor.getInstance().unregisterFileCompressEngine(this);
        super.onDestroyView();
    }

    /**
     * MiddlewareWebClientBase is a powerful feature provided by AgentWeb 3.0.0.
     * If users need to use the functionality provided by AgentWeb and don't want to override WebClientView
     * methods to cover AgentWeb's functionality, then MiddlewareWebClientBase is a
     * good choice.
     *
     * @return
     */
    protected MiddlewareWebClientBase getMiddlewareWebClient() {
        return this.mMiddleWareWebClient = new MiddlewareWebViewClient() {
            /**
             *
             * @param view
             * @param url
             * @return
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(TAG, "MiddlewareWebClientBase#shouldOverrideUrlLoading url:" + url);
				/* if (url.startsWith("agentweb")) { // Intercept url, do not execute DefaultWebClient#shouldOverrideUrlLoading
					Log.i(TAG, "agentweb scheme ~");
					return true;
				} */

                if (super.shouldOverrideUrlLoading(view, url)) { // Execute DefaultWebClient#shouldOverrideUrlLoading
                    return true;
                }
                // do you work
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.e(TAG, "MiddlewareWebClientBase#shouldOverrideUrlLoading request url:" + request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame() && error.getErrorCode() != -1) {
                    super.onReceivedError(view, request, error);
                }
            }
        };
    }

    protected MiddlewareWebChromeBase getMiddlewareWebChrome() {
        return this.mMiddleWareWebChrome = new MiddlewareChromeClient() {
        };
    }

    /**
     * Callback this method after selecting files. Here you can do file compression / or image orientation adjustment
     *
     * @param type     customize/system, customize means getting files through js method, converting files
     *                 to base64 and returning to js. This method has high compatibility, but there is a problem that when files are too large and converted to base64,
     *                 the string length is too long, causing js communication failure. So it is necessary to compress files and try to control string length within 512kb.
     *                 <p>
     *                 system method is file selection triggered by input/file tag. The disadvantage of this method is that it does not callback
     *                 fileChooser on Android 4.4, which has compatibility issues. But after upgrades, it can basically be ignored. API compatibility is getting better and better. Callback
     *                 returns in uri form, so there is no file size problem, and image preview is also fast. (Recommended method)
     * @param uri      File uri
     * @param callback
     */
    @Override
    public void compressFile(String type, final Uri[] uri, ValueCallback<Uri[]> callback) {
        Log.e(TAG, "compressFile type:" + type);
        if ("system".equals(type)) { // File selection triggered by input/file tag, this method has no performance issues, can be compressed or not, depending on your business requirements
            callback.onReceiveValue(uri);
            return;
        }
        // customize.equals(type) This method strongly recommends file compression
        if (uri == null || uri.length == 0) {
            callback.onReceiveValue(uri);
        } else {
            final String[] paths = AgentWebUtils.uriToPath(getActivity(), uri);
            if (paths == null || paths.length == 0) {
                callback.onReceiveValue(uri);
                return;
            }

            AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> {
                try {
                    Uri[] result = new Uri[paths.length];
                    for (int i = 0; i < paths.length; i++) {
                        String filePath = paths[i];
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtils.getExtensionByFilePath(filePath));
                        if (TextUtils.isEmpty(mimeType) || !mimeType.startsWith("image")) {
                            result[i] = uri[i];
                        } else {
                            File origin = new File(filePath);
                            File file = Luban.with(App.mContext).ignoreBy(100).setTargetDir(AgentWebUtils.getAgentWebFilePath(App.mContext)).get(filePath);
                            Log.e(TAG, "Original file size: " + byte2FitMemorySize(origin.length()));
                            Log.e(TAG, "Compressed file size: " + byte2FitMemorySize(file.length()));

                            Uri fileUri = AgentWebUtils.getUriFromFile(App.mContext, file);
                            result[i] = fileUri;

                        }
                    }
                    AgentWebUtils.runInUiThread(() -> callback.onReceiveValue(result));
                } catch (IOException e) {
                    e.printStackTrace();
                    AgentWebUtils.runInUiThread(() -> callback.onReceiveValue(uri));
                }
            });

        }

    }

    private static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteNum / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteNum / 1073741824);
        }
    }
}