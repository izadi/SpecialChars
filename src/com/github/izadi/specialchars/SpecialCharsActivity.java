package com.github.izadi.specialchars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.github.izadi.specialchars.SpecialChars.XmlParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SimpleExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

public class SpecialCharsActivity extends Activity
	implements ExpandableListView.OnChildClickListener, MenuItem.OnMenuItemClickListener,
	SoftInputStateResultReceiver.OnReceiveResultListener, View.OnLayoutChangeListener {

	private SpecialChars data;

	private RelativeLayout relativeLayoutMain;
	private ExpandableListView listViewChars;
	private EditText editTextContent;
	private MenuItem menuItemActionKeyboard;
	private MenuItem menuItemActionCopy;
	private MenuItem menuItemActionPaste;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    	Locale locale = new Locale("fa", "IR");
//        Locale.setDefault(locale);
//    	Configuration config = getResources().getConfiguration();
//    	config.locale = locale;
//    	getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.main);
        
        relativeLayoutMain = (RelativeLayout) findViewById(R.id.relativeLayoutMain);
        relativeLayoutMain.addOnLayoutChangeListener(this);
        
        listViewChars = (ExpandableListView) findViewById(R.id.listViewChars);
        try {
			data = SpecialChars.load(getApplicationContext(), R.xml.chars);
	        ArrayList<HashMap<String, String>> groupData = new ArrayList<HashMap<String, String>>(data.getGroups().size());
	        ArrayList<ArrayList<HashMap<String, String>>> childData = new ArrayList<ArrayList<HashMap<String, String>>>(data.getGroups().size());
	        for (SpecialChars.Group group: data.getGroups()) {
	        	HashMap<String, String> curGroup = new HashMap<String, String>();
	        	curGroup.put("label", group.getLabel());
	        	groupData.add(curGroup);
	        	ArrayList<HashMap<String, String>> curChildren = new ArrayList<HashMap<String, String>>(group.getChars().size());
	        	for (SpecialChars.Char ch: group.getChars()) {
	        		HashMap<String, String> curChild = new HashMap<String, String>();
	        		curChild.put("label", ch.getLabel());
	        		curChildren.add(curChild);
	        	}
	        	childData.add(curChildren);
	        }
	        listViewChars.setAdapter(new SimpleExpandableListAdapter(this, groupData, android.R.layout.simple_expandable_list_item_1,
	            	new String[] { "label" }, new int[] { android.R.id.text1 }, childData, android.R.layout.simple_expandable_list_item_1,
	            	new String[] { "label" }, new int[] { android.R.id.text1 }));
	        listViewChars.setOnChildClickListener(this);
		} catch (XmlParseException e) {
			new AlertDialog.Builder(this)
				.setMessage("Error reading XML input file")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				}).create().show();
		}
        
        editTextContent = (EditText) findViewById(R.id.editTextContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.options, menu);

    	menuItemActionKeyboard = menu.findItem(R.id.actionKeyboard);
        menuItemActionKeyboard.setOnMenuItemClickListener(this);
        
        menuItemActionCopy = menu.findItem(R.id.actionCopy);
        menuItemActionCopy.setOnMenuItemClickListener(this);
        
        menuItemActionPaste = menu.findItem(R.id.actionPaste);
        menuItemActionPaste.setOnMenuItemClickListener(this);

        return true;
    }

	public boolean onMenuItemClick(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.actionKeyboard:
    		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(editTextContent,
    			0, new SoftInputStateResultReceiver(this));
    		return true;
    	case R.id.actionCopy:
    		((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(
    			ClipData.newPlainText("SpecialChars", editTextContent.getText()));
    		return true;
    	case R.id.actionPaste:
    		int start = editTextContent.getSelectionStart();
    		int end = editTextContent.getSelectionEnd();
    		editTextContent.getText().replace(Math.min(start, end), Math.max(start, end),
   				((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).getPrimaryClip().getItemAt(0).coerceToText(this));
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
	}

	public void onReceiveResult(int result) {
		//menuItemActionKeyboard.setVisible(result == SoftInputStateResultReceiver.SHOWN);
	}

	public void onLayoutChange(View v, int left, int top, int right,
			int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		//if (menuItemActionKeyboard != null)
		//	menuItemActionKeyboard.setEnabled(oldBottom < bottom);
	}

	public boolean onChildClick(ExpandableListView parent, View v,
		int groupPosition, int childPosition, long id) {
		int start = editTextContent.getSelectionStart();
		int end = editTextContent.getSelectionEnd();
		editTextContent.getText().replace(Math.min(start, end), Math.max(start, end),
			data.getGroups().get(groupPosition).getChars().get(childPosition).getValue());
		return true;
	}
}