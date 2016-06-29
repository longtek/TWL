package com.longtek.bluetooth_control;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Connect extends Activity{

	private TextView TextConnectedTo;
	private Button Disconnect;
	private ImageButton Refresh;
	private ProgressBar ProgressDeviceDiscovery;
	private ListView ListViewBTDevices;
	private ArrayAdapter<String> BTArrayAdapter;
	 
	BluetoothSocket btSocket = null; 	//蓝牙通信socket
	BluetoothDevice btDevice = null; 	//蓝牙设备
	boolean bRun = true;
	boolean bThread = false;
	private Fac_Manager m_Manager;
	
	private View.OnClickListener OnRefresh = new View.OnClickListener()
	{
	    public void onClick(View view)
	    {
	    	Connect.this.ProgressDeviceDiscovery.setVisibility(0);
	        Connect.this.OnRefreshFunction();
	    	
	    }
	};
	
	private AdapterView.OnItemClickListener OnChooseBTDevice = new AdapterView.OnItemClickListener()
	{
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	    {
	    	Connect.this.m_Manager.Connect(position);
	    }
	};
	
	private BluetoothDevice m_Device;
	private ArrayList<BluetoothDevice> m_listDevice;
	private BluetoothAdapter myBluetoothAdapter;
	
	public void ConnectionDone(boolean bool)
	{
	    if (bool)
	    {
	    	setResult(-1);
	    	ActivityFinish();
	    	return;
	    }
	    Toast localToast = Toast.makeText(this, R.string.ConnectionFailed, 1);
	    localToast.setGravity(17, 0, 0);
	    localToast.show();
	  }
	 
	public void HideProgressBar()
	{
		this.ProgressDeviceDiscovery.setVisibility(8);
	}
	
	public void NewDeviceDetected(BluetoothDevice BtDevice)
	{
	    appendBTArrayAdapter(DeviceToString(BtDevice));
	}
	
	public String DeviceToString(BluetoothDevice BtDevice)
	{
		return BtDevice.getName() + "\n" + BtDevice.getAddress();
	}
	
	public boolean appendBTArrayAdapter(String string)
	{
	    this.BTArrayAdapter.add(string);
	    return true;
	}
	
/*	public boolean connectclient(BluetoothDevice BluetoothDevice)
	{
	    try
	    {
	      this.m_socket = BluetoothDevice.createRfcommSocketToServiceRecord(this.my_uuid);
	      return true;
	    }
	    catch (IOException  e)
	    {
	    }
	    return false;
	}*/
 
	public BluetoothDevice getDevice(int paramInt)
	{
	    this.m_Device = ((BluetoothDevice)this.m_listDevice.get(paramInt));
	    return this.m_Device;
	}
	
	private View.OnClickListener OnDisconnect = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
 
		}
	};
	
	private void init()
	{
		 this.Refresh = (ImageButton) findViewById(R.id.RefreshConn);
		 this.Disconnect = (Button) findViewById(R.id.DisconnectConn);
		 this.TextConnectedTo = (TextView) findViewById(R.id.TextConnectedToConn);
		 this.ProgressDeviceDiscovery = (ProgressBar) findViewById(R.id.progressBarDeviceDiscoveryConn);
		 this.ListViewBTDevices = (ListView) findViewById(R.id.listViewBTDeviceConn);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection);
		
		init();
		
		this.Refresh.setOnClickListener(this.OnRefresh);
		this.Disconnect.setOnClickListener(this.OnDisconnect);
		this.ListViewBTDevices.setAdapter(this.BTArrayAdapter);
        this.ListViewBTDevices.setOnItemClickListener(this.OnChooseBTDevice);
       
     //判断设备是否连接成功，成功则显示设备的蓝牙名称
//       if (this.m_Manager.IsConnected()) {
//    	      this.TextConnectedTo.setText(getString(R.string.ConnectedTo) + this.getM_Device().getName());
//    	    }
//    	    OnRefreshFunction();
        //若蓝牙未连接成功，执行以上代码会异常退出！！！
	}
 
	public BluetoothDevice getM_Device()
	{
	    return this.m_Device;
	}

	public void OnRefreshFunction()
	{
  	    CleanBTArrayAdapter();
	    this.ScanDevice();			//扫描蓝牙设备
	    this.ListViewBTDevices.setAdapter(this.BTArrayAdapter);
	}
	
	private boolean CleanBTArrayAdapter()
	{
		this.BTArrayAdapter = new ArrayAdapter<String>(Connect.this, R.layout.activity_chooser_view_list_item);   
		//ArrayAdapter参数：Context context, int resource，注意，上下文参数应该指定为Activity.this
	    this.ListViewBTDevices.setAdapter(this.BTArrayAdapter);
	    return true;
	}
	
	public void ScanDevice()
	{
		if (this.myBluetoothAdapter.isDiscovering())
			this.myBluetoothAdapter.cancelDiscovery();
	    	ClearDevices();
	    if (!this.myBluetoothAdapter.startDiscovery())
	      Log.d("Fac_Manager", "Start discovery bluetooth devices.");
	}
	
	public void ClearDevices()
	{
	    this.m_listDevice.clear();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		//运行时，参数Menu其实就是MenuBuilder对象  
        Log.d("MainActivity", "menu--->" + menu);  
          
        /*利用反射机制调用MenuBuilder的setOptionalIconsVisible方法设置mOptionalIconsVisible为true， 
         * 给菜单设置图标时才可见 
         */  
        setIconEnable(menu, true);
          
        return super.onCreateOptionsMenu(menu);  
	}
	
	//enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效 
	private void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method method = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            method.setAccessible(true);  
              
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)  
            method.invoke(menu, enable);  
              
        } catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
    }  

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_home:
			GoHome();
			break;
		case R.id.menu_connnection:
			Launch_Connection();
			break;
		case R.id.menu_cansettings:
			Launch_CanSettings();
			break;
		case R.id.menu_help:
			Launch_Help();
			break;
		case R.id.menu_boxsettings:
			Launch_BoxSettings();
			break;
		case R.id.menu_logs:
			Launch_Logs();
			break;
		case R.id.menu_about:
			Launch_About();
			break;
		case R.id.memu_demo:
			Launch_Demo();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	public void ActivityFinish()
	{
		finish();
	}
	
	public void GoHome()
	{
		finish();
	}
	
	public void Launch_Connection()
	{
		
	}
	
	public void Launch_CanSettings()
	{
		startActivity(new Intent(this, CanSettings.class));
		ActivityFinish();
	}
	
	public void Launch_Help()
	{
		startActivity(new Intent(this, Help.class));
		ActivityFinish();
	}
	
	public void Launch_BoxSettings()
	{
		startActivity(new Intent(this, BoxSettings.class));
		ActivityFinish();
	}
	
	public void Launch_Logs()
	{
		startActivity(new Intent(this, Logs.class));
		ActivityFinish();
	}
	
	public void Launch_About()
	{
		startActivity(new Intent(this, About.class));
		ActivityFinish();
	}
	
	public void Launch_Demo()
	{
		startActivity(new Intent(this, Demo.class));
		ActivityFinish();
	}
	
	public void DisconnectionDone(boolean paramBoolean)
	{
	    this.TextConnectedTo.setText(getString(R.string.NotConnected));
	}
}
