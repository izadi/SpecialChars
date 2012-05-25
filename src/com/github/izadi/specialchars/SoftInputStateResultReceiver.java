package com.github.izadi.specialchars;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.inputmethod.InputMethodManager;

public class SoftInputStateResultReceiver extends ResultReceiver {
	public final static int SHOWN = 0;
	public final static int HIDDEN = 1;
	
	public interface OnReceiveResultListener {
		public void onReceiveResult(int result);
	}

	private OnReceiveResultListener onReceiveResultListener;
	public SoftInputStateResultReceiver(OnReceiveResultListener onReceiveResultListener) {
		super(null);
		this.onReceiveResultListener = onReceiveResultListener;
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData)
	{
		if (onReceiveResultListener != null)
			onReceiveResultListener.onReceiveResult(resultCode == InputMethodManager.RESULT_SHOWN ||
				resultCode == InputMethodManager.RESULT_UNCHANGED_SHOWN ? SHOWN : HIDDEN);
	}
}
