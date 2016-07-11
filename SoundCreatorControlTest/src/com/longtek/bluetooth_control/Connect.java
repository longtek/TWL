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
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();		//ʵ����BluetoothAdapter����ȡ�����������������������豸
	private final static int REQUEST_CONNECT_DEVICE = 1;    //�궨���ѯ�豸���
	BluetoothSocket btSocket = null; 	//����ͨ��socket
	private InputStream is;			//������������������������
	BluetoothDevice btDevice = null; 	//�����豸
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";			//SPP����UUID��
	private UUID my_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	boolean bRun = true;
	boolean bThread = false;
	
	private View.OnClickListener OnRefresh = new View.OnClickListener()
	{
	    public void onClick(View view)
	    {
	    	Connect.this.ProgressDeviceDiscovery.setVisibility(0);
	    	//Connect.this.OnRefreshFunction();
	    	if(btAdapter.isEnabled()==false){  //����������񲻿�������ʾ
	    		Toast.makeText(Connect.this, " ��������...", Toast.LENGTH_LONG).show();
	    		/**
	    		 * @param context �����Ķ������Ϊ��ǰActivity.this
	    		 * */
	    		return;
	    	}
	    	
	    }
	};
	
	private View.OnClickListener OnDisconnect = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			//��δ�����豸���DeviceListActivity�����豸����
	    	Button btn = (Button) findViewById(R.id.DisconnectConn);
	    	if(btSocket==null){
	    		Intent serverIntent = new Intent(Connect.this, DeviceListActivity.class); //��ת��������
	    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //���÷��غ궨��
	    	}
	    	else{
	    		 //�ر�����socket
	    	    try{
	    	    	
	    	    	is.close();
	    	    	btSocket.close();
	    	    	btSocket = null;
	    	    	bRun = false;
	    	    	btn.setText("����");
	    	    }catch(IOException e){}   
	    	}
	    	return;
		}
	};
	private BluetoothSocket m_socket;
	
	//���ջ�������ӦstartActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	switch(requestCode)
    	{
    		case REQUEST_CONNECT_DEVICE:     //���ӽ������DeviceListActivity���÷���
    		// ��Ӧ���ؽ��
            if (resultCode == Activity.RESULT_OK) {   //���ӳɹ�����DeviceListActivity���÷���
                // MAC��ַ����DeviceListActivity���÷���
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // �õ������豸���      
                btDevice = btAdapter.getRemoteDevice(address);
 
                // �÷���ŵõ�socket
                try{
                	btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                }catch(IOException e){
                	Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                }
                
                Log.d("mydebug", "mydebugmessage");
                //����socket
            	Button btn = (Button) findViewById(R.id.DisconnectConn);
                try{
                	btSocket.connect();
                	Toast.makeText(this, "����"+btDevice.getName()+"�ɹ���", Toast.LENGTH_SHORT).show();
                	btn.setText("�Ͽ�");
                }catch(IOException e){
                	try{
                		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                		btSocket.close();
                		btSocket = null;
                	}catch(IOException ee){
                		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                	}
                   	return;
                }
                
                //�򿪽����߳�
                try{
            		is = btSocket.getInputStream();   //�õ���������������
            		}catch(IOException e){
            			Toast.makeText(this, "��������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
            			return;
            		}
            		if(bThread==false){
//            			ReadThread.start();
            			bThread=true;
            		}else{
            			bRun = true;
            		}
            }
    		break;
//    	case REQUEST_SELECT_FILE:
//    		if(requestCode == RESULE_CODE){
//				//����Ϊ "ѡ���ļ�"
//				try {
//					//ȡ��ѡ����ļ���
//					String sendFileName = data.getStringExtra(SEND_FILE_NAME);
//					mSendFileNameTV.setText(sendFileName);
//				} catch (Exception eee) {				
//				}
//			}
//    		break;
    	default:
    		break;
    	}
    }
    
	private AdapterView.OnItemClickListener OnChooseBTDevice = new AdapterView.OnItemClickListener()
	{
	    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
	    {
	    	Connect.this.Connected(paramAnonymousInt);
	    }
	};
	private BluetoothDevice m_Device;
	private ArrayList<BluetoothDevice> m_listDevice;
	private BluetoothAdapter myBluetoothAdapter;
	private Context m_Connect;
	private Object m_Thread;
	
	public void ConnectionDone(boolean paramBoolean)
	{
	    if (paramBoolean)
	    {
	    	setResult(-1);
	    	ActivityFinish();
	    	return;
	    }
	    Toast localToast = Toast.makeText(this, R.string.ConnectionFailed, 1);
	    localToast.setGravity(17, 0, 0);
	    localToast.show();
	  }
	
	public boolean Connected(int paramInt)
	{
		BluetoothDevice localBluetoothDevice = this.getDevice(paramInt);
	    if (this.myBluetoothAdapter.isDiscovering())
	    	this.myBluetoothAdapter.cancelDiscovery();
	    while (true)
	    {
	    	if (!this.myBluetoothAdapter.isDiscovering())
	    	{
	    		boolean bool = this.connect(localBluetoothDevice);
	    		if ((this.m_Connect != null) && (!bool))
	    			ConnectionDone(false);
	        return bool;
	      }
	      try
	      {
	        Thread.sleep(100L);
	      }
	      catch (InterruptedException localInterruptedException)
	      {
	        localInterruptedException.printStackTrace();
	      }
	    }
	}
 
	public void disconnect()
	  {
	    if (this.m_Thread != null)
//	      this.m_Thread.Stop();
	    new Thread(new Runnable()
	    {
	      public void run()
	      {
	        if (IsConnected());
	        try
	        {
	          m_socket.close();
	          return;
	        }
	        catch (IOException localIOException)
	        {
	        }
	      }
	    }).start();
	  }
	 
	public boolean connectclient(BluetoothDevice BluetoothDevice)
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
	}
	
	 public boolean connect(BluetoothDevice paramBluetoothDevice)
	  {
	    disconnect();
	    connectclient(paramBluetoothDevice);
	    new Thread(new Runnable()
	    {
	      public void run()
	      {
	        try
	        {
	          m_socket.connect();
	          return;
	        }
	        catch (IOException localIOException1)
	        {
	          localIOException1.printStackTrace();
	          try
	          {
	            m_socket.close();
	            return;
	          }
	          catch (IOException localIOException2)
	          {
	          }
	        }
	      }
	    }).start();
	    
	    int i = 0;
	    while (true)
	    {
	      int j;
	      if (!this.m_socket.isConnected())
	      {
	        j = i + 1;
	        if (i < 50);
	      }
	      else
	      {
	        if (!this.m_socket.isConnected())
	          break;
//	        this.m_Thread = new proc_Server(this.m_socket, this.m_handler);
//	        this.m_Thread.start();
//	        this.m_Thread.write("a".getBytes());
	        return true;
	      }
	      try
	      {
	        Thread.sleep(100L);
	        i = j;
	      }
	      catch (InterruptedException  e)
	      {
	        e.printStackTrace();
	        i = j;
	      }
	    }
	    return false;
	  }
 
	public BluetoothDevice getDevice(int paramInt)
	{
	    this.m_Device = ((BluetoothDevice)this.m_listDevice.get(paramInt));
	    return this.m_Device;
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection);
		
		init();
		
		this.Refresh.setOnClickListener(this.OnRefresh);
		this.Disconnect.setOnClickListener(this.OnDisconnect);
		this.ListViewBTDevices.setAdapter(this.BTArrayAdapter);
        this.ListViewBTDevices.setOnItemClickListener(this.OnChooseBTDevice);
		
		//����򿪱��������豸���ɹ�����ʾ��Ϣ����������
        if (btAdapter == null){
        	Toast.makeText(this, "�޷����ֻ���������ȷ���ֻ��Ƿ����������ܣ�", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // �����豸���Ա�����  
       new Thread(){
    	   public void run(){
    		   if(btAdapter.isEnabled()==false){
    			   btAdapter.enable();
    		   }
    	   }   	   
       }.start();  
       
     //�ж��豸�Ƿ����ӳɹ����ɹ�����ʾ�豸����������
//       if (IsConnected()) {
//    	      this.TextConnectedTo.setText(getString(R.string.ConnectedTo) + this.getM_Device().getName());
//    	    }
//    	    OnRefreshFunction();
	}
 
	public BluetoothDevice getM_Device()
	{
	    return this.m_Device;
	}
	
	public boolean IsConnected()
	{
	    return (this.m_socket != null) && (this.m_socket.isConnected());
	}
	
	public void OnRefreshFunction()
	{
//	    CleanBTArrayAdapter();
	    this.ScanDevice();
	    this.ListViewBTDevices.setAdapter(this.BTArrayAdapter);
	}
	
	/*private boolean CleanBTArrayAdapter()
	{
		this.BTArrayAdapter = new ArrayAdapter(this, 17367043);
	    this.ListViewBTDevices.setAdapter(this.BTArrayAdapter);
	    return true;
	}*/
	
	public void ScanDevice()
	{
		if (this.myBluetoothAdapter.isDiscovering())
			this.myBluetoothAdapter.cancelDiscovery();
	    	ClearDevices();
	    if (!this.myBluetoothAdapter.startDiscovery())
	      Log.d("Fac_Manager", "Start scaning bluetooth device!");
	}
	
	public void ClearDevices()
	{
	    this.m_listDevice.clear();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		//����ʱ������Menu��ʵ����MenuBuilder����  
        Log.d("MainActivity", "menu--->" + menu);  
          
        /*���÷�����Ƶ���MenuBuilder��setOptionalIconsVisible��������mOptionalIconsVisibleΪtrue�� 
         * ���˵�����ͼ��ʱ�ſɼ� 
         */  
        setIconEnable(menu, true);
          
        return super.onCreateOptionsMenu(menu);  
	}
	
	//enableΪtrueʱ���˵����ͼ����Ч��enableΪfalseʱ��Ч��4.0ϵͳĬ����Ч 
	private void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method method = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            method.setAccessible(true);  
              
            //MenuBuilderʵ��Menu�ӿڣ������˵�ʱ����������menu��ʵ����MenuBuilder����(java�Ķ�̬����)  
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
