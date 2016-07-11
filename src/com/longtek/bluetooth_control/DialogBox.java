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


/*package com.longtek.bluetooth_control;

import java.io.IOException;
import java.io.OutputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

 
public class DialogBox extends DialogFragment {

	private int m_BoxCurrent = 0;
	private String[] m_ListBox;
	private Context m_context;
	private BluetoothSocket m_socket;
	private DialogBox m_Manager;
	private static DialogBox m_UniqueInstance;
	
	private Handler m_Handler;
	private OutputStream m_OutStream;
	
	private DialogInterface.OnClickListener OnChooseBox = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int paramInt) {
			// TODO Auto-generated method stub
			DialogBox.this.m_Manager.ChooseBox(paramInt);
		}
	};
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.m_Manager = DialogBox.getmanager();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Select file to play on the device").setItems(this.m_Manager.getM_ListBox(), this.OnChooseBox);
		
		
		AlertDialog dialog = builder.create();			//创建对话框
		dialog.setCanceledOnTouchOutside(true);			//让对话框消失
		
		return super.onCreateDialog(savedInstanceState);
	}

	public String[] getM_ListBox()
	{
	    return this.m_ListBox;
	}
	
	public void ChooseBox(int paramInt) {
		// TODO Auto-generated method stub
		 if (!IsConnected())
		    {
		      ((MainActivity)this.m_context).PleaseDoConnection();
		      return;
		    }
		    this.setM_BoxCurrent(paramInt);
		    this.LoadBoxCmd();
	}

	public void setM_BoxCurrent(int paramInt)
	{
	    this.m_BoxCurrent = paramInt;
	}
	
	public void LoadBoxCmd()
	{
	    this.write("n".getBytes());
	}
	
	public void write(byte[] paramArrayOfByte)
	{
		try
	    {
			String str = new String(paramArrayOfByte);
			byte[] Str = ("LOG trying to send " + paramArrayOfByte.length + " bytes: " + ((String)str).charAt(0)).getBytes();
			Message msg = new Message();
			Bundle localBundle = new Bundle();
			localBundle.putByteArray("data", (byte[])Str);
			((Message)msg).setData(localBundle);
			this.m_Handler.sendMessage((Message)msg);
			this.m_OutStream.write(paramArrayOfByte);
			paramArrayOfByte = "LOG success".getBytes();
//			localObject1 = new Message();
//			localObject2 = new Bundle();
//			((Bundle)localObject2).putByteArray("data", paramArrayOfByte);
//			((Message)localObject1).setData((Bundle)localObject2);
//			this.m_Handler.sendMessage((Message)localObject1);
//			return;
	    }
	    catch (IOException e)
	    {
	    	Log.e("procServer", "2");
	    	paramArrayOfByte = "LOG fail IOException".getBytes();
//	    	localObject1 = new Message();
//	    	localObject2 = new Bundle();
//	    	((Bundle)localObject2).putByteArray("data", paramArrayOfByte);
//	    	((Message)localObject1).setData((Bundle)localObject2);
//	    	this.m_Handler.sendMessage((Message)localObject1);
//	    	return;
	    }
	    catch (Exception ee)
	    {
	    		paramArrayOfByte = "LOG fail other Exception".getBytes();
//	    		Object localObject1 = new Message();
//	    		Object localObject2 = new Bundle();
//	    		((Bundle)localObject2).putByteArray("data", paramArrayOfByte);
//	    		((Message)localObject1).setData((Bundle)localObject2);
//	    		this.m_Handler.sendMessage((Message)localObject1);
	    }
	  }
	
	public boolean IsConnected()
	{
	    return (this.m_socket != null) && (this.m_socket.isConnected());
	}

	public static DialogBox getmanager()
	{
		return m_UniqueInstance;
	}
	
}
*/