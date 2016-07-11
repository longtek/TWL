package com.longtek.bluetooth_control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Connect extends Activity{

	private TextView TextConnectedTo;
	private Button Disconnect;
	private ImageButton Refresh;
	private ProgressBar ProgressDeviceDiscovery;
	private ListView ListViewBTDevices;
	private ArrayAdapter<String> BTArrayAdapter;
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();		//实例化BluetoothAdapter，获取本地蓝牙适配器，即蓝牙设备
	private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄
	BluetoothSocket btSocket = null; 	//蓝牙通信socket
	private InputStream is;			//输入流，用来接收蓝牙数据
	BluetoothDevice btDevice = null; 	//蓝牙设备
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";			//SPP服务UUID号
	private UUID my_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	boolean bRun = true;
	boolean bThread = false;
	
	
	private TextView text0; 	   	//提示栏
    private EditText edit0;    		//发送数据输入框
    private TextView dis;       	//接收数据显示
    private ScrollView sv;     		//翻页
    private String smsg = "";    	//显示用数据缓存
    private String fmsg = "";    	//保存用数据缓存
    
    private Button SendMsgBtn;
    
	private View.OnClickListener OnRefresh = new View.OnClickListener()
	{
	    public void onClick(View view)
	    {
	    	Connect.this.ProgressDeviceDiscovery.setVisibility(0);
	    	//Connect.this.OnRefreshFunction();
	    	if(btAdapter.isEnabled()==false){  //如果蓝牙服务不可用则提示
	    		Toast.makeText(Connect.this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
	    		/**
	    		 * @param context 上下文对象必须为当前Activity.this
	    		 * */
	    		return;
	    	}
	    	
	    }
	};
	
	private View.OnClickListener OnDisconnect = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			//如未连接设备则打开DeviceListActivity进行设备搜索
	    	Button btn = (Button) findViewById(R.id.DisconnectConn);
	    	if(btSocket==null){
	    		Intent serverIntent = new Intent(Connect.this, DeviceListActivity.class); //跳转程序设置
	    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
	    	}
	    	else{
	    		 //关闭连接socket
	    	    try{
	    	    	
	    	    	is.close();
	    	    	btSocket.close();
	    	    	btSocket = null;
	    	    	bRun = false;
	    	    	btn.setText("连接");
	    	    }catch(IOException e){}   
	    	}
	    	return;
		}
	};
	private BluetoothSocket m_socket;
	
	//接收活动结果，响应startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	switch(requestCode)
    	{
    		case REQUEST_CONNECT_DEVICE:     //连接结果，由DeviceListActivity设置返回
    		// 响应返回结果
            if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
                // MAC地址，由DeviceListActivity设置返回
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // 得到蓝牙设备句柄      
                btDevice = btAdapter.getRemoteDevice(address);
 
                // 用服务号得到socket
                try{
                	btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                }catch(IOException e){
                	Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                }
                
                Log.d("mydebug", "mydebugmessage");
                //连接socket
            	Button btn = (Button) findViewById(R.id.DisconnectConn);
                try{
                	btSocket.connect();
                	Toast.makeText(this, "连接"+btDevice.getName()+"成功！", Toast.LENGTH_SHORT).show();
                	btn.setText("断开");
                }catch(IOException e){
                	try{
                		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                		btSocket.close();
                		btSocket = null;
                	}catch(IOException ee){
                		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                	}
                   	return;
                }
                
                //打开接收线程
                try{
            		is = btSocket.getInputStream();   //得到蓝牙数据输入流
            		}catch(IOException e){
            			Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
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
//				//请求为 "选择文件"
//				try {
//					//取得选择的文件名
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
//		 this.ProgressDeviceDiscovery = (ProgressBar) findViewById(R.id.progressBarDeviceDiscoveryConn);
//		 this.ListViewBTDevices = (ListView) findViewById(R.id.listViewBTDeviceConn);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection);
		
		 text0 = (TextView)findViewById(R.id.Text0);			
		 edit0 = (EditText)findViewById(R.id.Edit0);			
		 sv = (ScrollView)findViewById(R.id.ScrollView01);	
		 dis = (TextView) findViewById(R.id.in);      		
		 SendMsgBtn = (Button) findViewById(R.id.SendMsgBtn);
	        
		
		init();
		
		this.Refresh.setOnClickListener(this.OnRefresh);
		this.Disconnect.setOnClickListener(this.OnDisconnect);
//		this.ListViewBTDevices.setAdapter(this.BTArrayAdapter);
//      this.ListViewBTDevices.setOnItemClickListener(this.OnChooseBTDevice);
		
		//如果打开本地蓝牙设备不成功，提示信息，结束程序
        if (btAdapter == null){
        	Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // 设置设备可以被搜索  
       new Thread(){
    	   public void run(){
    		   if(btAdapter.isEnabled()==false){
    			   btAdapter.enable();
    		   }
    	   }   	   
       }.start();  
       
     //判断设备是否连接成功，成功则显示设备的蓝牙名称
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
	
	 //发送按键响应
    public void onSendButtonClicked(View v)
    {
    	
    	int i=0;
    	int n=0;
 
    		try {
				OutputStream os = btSocket.getOutputStream();    
				byte[] bos = edit0.getText().toString().getBytes();
  	
				for(i=0; i<bos.length; i++){
					if(bos[i]==0x0a)
						n++;
				}
				
				byte[] bos_new = new byte[bos.length+n];
				n=0;
				
				for(i=0; i<bos.length; i++){  
					if(bos[i]==0x0a){
						bos_new[n]=0x0d;
						n++;
						bos_new[n]=0x0a;
					}else{
						bos_new[n]=bos[i];
					}
					n++;
				}
				
				os.write(bos);
				System.out.println("发送数据：" + bos);
			} catch (IOException e) {
				e.printStackTrace();
			}	
    	 
    }
    
    //接收数据线程
    Thread ReadThread = new Thread(){
    	
    	public void run(){
    		int num = 0;
    		byte[] buffer = new byte[1024];
    		byte[] buffer_new = new byte[1024];
    		int i = 0;
    		int n = 0;
    		bRun = true;
    		//接收线程
    		while(true){
    			try{
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				while(true){
    					num = is.read(buffer);         //读入数据
    					n=0;
    					
    					String s0 = new String(buffer,0,num);
    					fmsg+=s0;    //保存收到数据
    					System.out.println("收到数据：" + s0);       //--->打印调试信息
    					
    					for(i=0;i<num;i++){
    						if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
    							buffer_new[n] = 0x0a;
    							i++;
    						}else{
    							buffer_new[n] = buffer[i];
    						}
    						n++;
    					}
    					String s = new String(buffer_new,0,n);
    					smsg+=s;   //写入接收缓存
    					if(is.available()==0)break;  //短时间没有数据才跳出进行显示
    					System.out.println("收到数据：" + smsg);          //--->打印调试信息，收到的数据
    				} 
    				//发送显示消息，进行刷新UI
    					handler.sendMessage(handler.obtainMessage());       	    		
    	    		}catch(IOException e){
    	    		}
    		}
    	}
    };
    
    //消息处理队列
    Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		dis.setText(smsg);   //显示数据 
    		sv.scrollTo(0,dis.getMeasuredHeight()); //跳至数据最后一页
    	}
    };
    
    //关闭程序调用处理部分
    public void onDestroy(){
    	super.onDestroy();
    	if(btSocket!=null)  //关闭连接socket
    	try{
    		btSocket.close();
    	}catch(IOException e){}
    //	_bluetooth.disable();  //关闭蓝牙服务
    }
}
