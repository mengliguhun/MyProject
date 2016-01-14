package com.example.administrator.myproject.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;

import com.example.administrator.myproject.view.HaloToast;


public class BaseFragment extends Fragment {
	private ProgressDialog progressDlg;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
	}
    @Override
	public void onResume() {
		super.onResume();
//		MobclickAgent.onPageStart(this.getClass().getName());
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		MobclickAgent.onPageEnd(this.getClass().getName());
	}
	protected void init() {
		initViews();
		bindViews();

	}
    protected void initViews() {
		// TODO Auto-generated method stub

	}
    protected void bindViews() {
		// TODO Auto-generated method stub

	}
	/**
	 * Show loading dialog
	 * 
	 * @param sMsg
	 *            the message to display
	 */
	public void displayLoadingDlg(String sMsg) {
		if (getActivity().isFinishing()||!isAdded()) {
			return;
		}
		if (progressDlg != null && progressDlg.isShowing()) {
			progressDlg.setMessage(sMsg);
		} else {
			progressDlg = new ProgressDialog(getActivity());
			progressDlg.setMessage(sMsg);
			progressDlg.setIndeterminate(true);
			progressDlg.setCancelable(true);
			progressDlg.show();
		}
	}
	public void displayLoadingDlgNocancel(String sMsg) {
		if (getActivity().isFinishing()||!isAdded()) {
			return;
		}
		if (progressDlg != null && progressDlg.isShowing()) {
			progressDlg.setMessage(sMsg);
		} else {
			progressDlg = new ProgressDialog(getActivity());
			progressDlg.setMessage(sMsg);
			progressDlg.setIndeterminate(true);
			progressDlg.setCancelable(false);
			progressDlg.show();
			
		}
	}
	/**
	 * Show loading dialog
	 * 
	 * @param resId
	 *            message resId in string.xml to display
	 */
	public void displayLoadingDlg(int resId) {
		displayLoadingDlg(getString(resId));
	}
	/**
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(OnCancelListener listener){
		if (progressDlg != null ){
			progressDlg.setOnCancelListener(listener);
		}
	}
	/**
	 * 
	 * @param listener
	 */
	public void setOnKeyListener(OnKeyListener listener){
		if (progressDlg != null )
			progressDlg.setOnKeyListener(listener);
	}
	/**
	 * Dismiss the loading dialog
	 */
	public void dismissLoadingDlg() {
		if (progressDlg != null && progressDlg.isShowing()){
			progressDlg.cancel();
			progressDlg.setCanceledOnTouchOutside(false);
		}
		
	}
	public void setCanceledOnTouchOutside(boolean b) {
		if (progressDlg != null && progressDlg.isShowing()){
			progressDlg.setCanceledOnTouchOutside(false);
		}
			
	}
	public void showToast(String msg) {
		if (isAdded()) {
			HaloToast.show(getActivity(), msg);
		}
	}
	/**
     * 是否联网
     * @return
     */
    public boolean checkNetworkInfo() {
		  ConnectivityManager conMan = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		  State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		  State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		  if (mobile == State.CONNECTED || mobile == State.CONNECTING){
			  return true;
		  }
		  if (wifi == State.CONNECTED || wifi == State.CONNECTING){
			  return true;
		  }
		  return false;
	}
    
    public void dealNet( final boolean isIndex){
    	String negative;
    	if(isIndex){
    		negative = "退出";
    	}else{
    		negative = "取消";
    	}
    	if(!getActivity().isFinishing() || isAdded()){
	    	Builder builder = new Builder(this.getActivity());
			builder.setTitle("提示");
			
			builder.setMessage("设备未接入互联网");
			builder.setPositiveButton("去设置网络", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					 Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			         startActivity(intent);
				}
			});
			builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					if(isIndex){
						getActivity().finish();
						android.os.Process.killProcess(android.os.Process.myPid());
					}else{
						dialog.dismiss();
					}
				}
			});
			builder.show();
    	}
    }


}
