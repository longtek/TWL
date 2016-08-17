package com.longtek.bluetooth_control;

import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogBox extends DialogFragment
{
	private DialogInterface.OnClickListener OnChooseBox = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
		{
			DialogBox.this.m_Manager.ChooseBox(paramAnonymousInt);
		}
	};
	Fac_Manager m_Manager = null;
  
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		this.m_Manager = Fac_Manager.getManager();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Select file to play on the device").setItems(this.m_Manager.getM_Data().getM_ListBox(), this.OnChooseBox);
		
		AlertDialog dialog = builder.create();		//创建对话框
		dialog.setCanceledOnTouchOutside(true);		//让对话框消失
	//	return dialog;
		return super.onCreateDialog(savedInstanceState);
	}

//	public void show(FragmentManager fragmentManager, String string) {
//		// TODO Auto-generated method stub
//		
//	}
}
  