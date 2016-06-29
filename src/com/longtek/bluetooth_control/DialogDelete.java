package com.longtek.bluetooth_control;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogDelete extends DialogFragment
{
	DialogDeleteListener mListener;
	
	private DialogInterface.OnClickListener OnNo = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
		{
			DialogDelete.this.mListener.onDialogNegativeClick(DialogDelete.this);
		}
	};
	
	private DialogInterface.OnClickListener OnYes = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
		{
			DialogDelete.this.mListener.onDialogPositiveClick(DialogDelete.this);
		}
	};
	
	public void onAttach(Activity paramActivity)
	{
		super.onAttach(paramActivity);
		try
		{
			this.mListener = ((DialogDeleteListener)paramActivity);
			return;
		}
		catch (ClassCastException localClassCastException)
		{
			throw new ClassCastException(paramActivity.toString() + " must implement DialogDeleteListener");
		}
	}
  
	public Dialog onCreateDialog(Bundle bundle)
	{
		Builder b = new AlertDialog.Builder(getActivity());
		b.setTitle("Do you want to delete all files on the device?").setPositiveButton("Yes", this.OnYes).setNegativeButton("No", this.OnNo);
		
		return b.create();
	}
  
  
	public static abstract interface DialogDeleteListener
	{
		public abstract void onDialogNegativeClick(DialogFragment paramDialogFragment);
    
		public abstract void onDialogPositiveClick(DialogFragment paramDialogFragment);
	}
}

