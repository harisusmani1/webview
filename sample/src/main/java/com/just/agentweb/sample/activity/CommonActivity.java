package com.just.agentweb.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.just.agentweb.sample.R;
import com.just.agentweb.sample.common.FragmentKeyDown;
import com.just.agentweb.sample.fragment.AgentWebFragment;
import com.just.agentweb.sample.fragment.BounceWebFragment;
import com.just.agentweb.sample.fragment.CustomIndicatorFragment;
import com.just.agentweb.sample.fragment.CustomSettingsFragment;
import com.just.agentweb.sample.fragment.CustomWebViewFragment;
import com.just.agentweb.sample.fragment.JsAgentWebFragment;
import com.just.agentweb.sample.fragment.JsbridgeWebFragment;
import com.just.agentweb.sample.fragment.SmartRefreshWebFragment;
import com.just.agentweb.sample.fragment.VasSonicFragment;

import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_BOUNCE_EFFACT;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_CUSTOM_PROGRESSBAR;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_CUSTOM_WEBVIEW_SETTINGS;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_CUTSTOM_WEBVIEW;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_FILE_DOWNLOAD;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_INPUT_TAG_PROBLEM;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_JSBRIDGE_SAMPLE;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_JS_JAVA_COMMUNICATION;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_JS_JAVA_COMUNICATION_UPLOAD_FILE;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_LINKS;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_MAP;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_PULL_DOWN_REFRESH;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_USE_IN_FRAGMENT;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_VASSONIC_SAMPLE;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_VIDEO_FULL_SCREEN;
import static com.just.agentweb.sample.activity.MainActivity.FLAG_GUIDE_DICTIONARY_WEBRTC;
import static com.just.agentweb.sample.sonic.SonicJavaScriptInterface.PARAM_CLICK_TIME;

/**
 * Created by cenxiaozhong on 2017/5/23.
 * Source code: https://github.com/Justson/AgentWeb
 */

public class CommonActivity extends AppCompatActivity {


	private FrameLayout mFrameLayout;
	public static final String TYPE_KEY = "type_key";
	private FragmentManager mFragmentManager;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_common);

		mFrameLayout = (FrameLayout) this.findViewById(R.id.container_framelayout);
		int key = getIntent().getIntExtra(TYPE_KEY, -1);
		mFragmentManager = this.getSupportFragmentManager();
		openFragment(key);
	}


	private AgentWebFragment mAgentWebFragment;

	private void openFragment(int key) {

		FragmentTransaction ft = mFragmentManager.beginTransaction();
		Bundle mBundle = null;


		switch (key) {

            /* Fragment using AgentWeb */
			case FLAG_GUIDE_DICTIONARY_USE_IN_FRAGMENT: // Please use constants instead of 0 in projects for better code readability
				/* Download files */
			case FLAG_GUIDE_DICTIONARY_FILE_DOWNLOAD:
				ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "https://www.baidu.com/");
				break;
			/* Input tag file upload */
			case FLAG_GUIDE_DICTIONARY_INPUT_TAG_PROBLEM:
				ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "file:///android_asset/upload_file/uploadfile.html");
				break;
            /* JS file upload */
			case FLAG_GUIDE_DICTIONARY_JS_JAVA_COMUNICATION_UPLOAD_FILE:
				ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "file:///android_asset/upload_file/jsuploadfile.html");
				break;
            /* JS */
			case FLAG_GUIDE_DICTIONARY_JS_JAVA_COMMUNICATION:
				ft.add(R.id.container_framelayout, mAgentWebFragment = JsAgentWebFragment.getInstance(mBundle = new Bundle()), JsAgentWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "file:///android_asset/js_interaction/hello.html");
				break;
			/* WebRTC */
			case FLAG_GUIDE_DICTIONARY_WEBRTC:
				ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "https://webrtc.github.io/samples/");
				break;
            /* Youku fullscreen video playback */
			case FLAG_GUIDE_DICTIONARY_VIDEO_FULL_SCREEN:
				ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "https://www.youtube.com/");
//                mBundle.putString(AgentWebFragment.URL_KEY, "https://v.qq.com/x/page/i0530nu6z1a.html");
				break;
            /* Taobao custom progress bar */
			case FLAG_GUIDE_DICTIONARY_CUSTOM_PROGRESSBAR:
				ft.add(R.id.container_framelayout, mAgentWebFragment = CustomIndicatorFragment.getInstance(mBundle = new Bundle()), CustomIndicatorFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "https://www.baidu.com/");
				break;
            /* Wandoujia */
			case FLAG_GUIDE_DICTIONARY_CUSTOM_WEBVIEW_SETTINGS:
				ft.add(R.id.container_framelayout, mAgentWebFragment = CustomSettingsFragment.getInstance(mBundle = new Bundle()), CustomSettingsFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "https://www.baidu.com/");
				break;

            /* SMS */
			case FLAG_GUIDE_DICTIONARY_LINKS:
				ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "file:///android_asset/sms/sms.html");
				break;
            /* Custom WebView */
			case FLAG_GUIDE_DICTIONARY_CUTSTOM_WEBVIEW:
				ft.add(R.id.container_framelayout, mAgentWebFragment = CustomWebViewFragment.getInstance(mBundle = new Bundle()), CustomWebViewFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "https://www.baidu.com/");
				break;
            /* Bounce effect */
			case FLAG_GUIDE_DICTIONARY_BOUNCE_EFFACT:
				ft.add(R.id.container_framelayout, mAgentWebFragment = BounceWebFragment.getInstance(mBundle = new Bundle()), BounceWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "https://www.baidu.com/");
				break;

            /* JSBridge demo */
			case FLAG_GUIDE_DICTIONARY_JSBRIDGE_SAMPLE:
				ft.add(R.id.container_framelayout, mAgentWebFragment = JsbridgeWebFragment.getInstance(mBundle = new Bundle()), JsbridgeWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "file:///android_asset/jsbridge/demo.html");
				break;

            /* SmartRefresh pull-to-refresh */
			case FLAG_GUIDE_DICTIONARY_PULL_DOWN_REFRESH:
				ft.add(R.id.container_framelayout, mAgentWebFragment = SmartRefreshWebFragment.getInstance(mBundle = new Bundle()), SmartRefreshWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "https://www.baidu.com/");
				break;
                /* Map */
			case FLAG_GUIDE_DICTIONARY_MAP:
				ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
				mBundle.putString(AgentWebFragment.URL_KEY, "https://maps.google.com/");
				break;
                /* Instant first screen loading */
			case FLAG_GUIDE_DICTIONARY_VASSONIC_SAMPLE:
				ft.add(R.id.container_framelayout, mAgentWebFragment = VasSonicFragment.create(mBundle = new Bundle()), AgentWebFragment.class.getName());
				mBundle.putLong(PARAM_CLICK_TIME, getIntent().getLongExtra(PARAM_CLICK_TIME, -1L));
				mBundle.putString(AgentWebFragment.URL_KEY, "https://www.baidu.com/");
				break;
			default:
				break;

		}
		ft.commit();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        // Must ensure mAgentWebFragment callback
//		mAgentWebFragment.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		AgentWebFragment mAgentWebFragment = this.mAgentWebFragment;
		if (mAgentWebFragment != null) {
			FragmentKeyDown mFragmentKeyDown = mAgentWebFragment;
			if (mFragmentKeyDown.onFragmentKeyDown(keyCode, event)) {
				return true;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}

		return super.onKeyDown(keyCode, event);
	}




	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
