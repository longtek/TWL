package com.longtek.bluetooth_control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.Menu;            //如使用菜单加入此三包
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Connect类
 * 用于启用本机蓝牙，实现蓝牙连接，接收DeviceListActivity类返回的结果
 * 之后加入发送数据和接收数据，显示Log功能
 * @author TWL
 *
 */
public class Connect extends Activity {
	
	private TextView TextConnectedTo;
	private final static int REQUEST_CONNECT_DEVICE = 1;    //请求码
	
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
	public BluetoothDevice _device;
	
	private InputStream is; 	   //输入流，用来接收蓝牙数据
	//private TextView text0; 	   //提示栏解句柄
    private EditText edit0;  	  //发送数据输入句柄
    private TextView dis;     	  //接收数据显示句柄
    private ScrollView sv;     	 //翻页句柄
    private String smsg = ""; 	 //显示用数据缓存
    private String fmsg = ""; 	 //保存用数据缓存
    private Button Disconnect;	
    private ToggleButton m_SpyOnOff;		//数据显示开关
    private TextView CanRPM; 		//发送机转速
    private TextView CanSpeed;    	//车速
    private TextView CanThrottle;	//油门开度

    private Fac_Manager m_Manager = null;
	private PC_Data m_Data = null;
	
    public String rmp;
    public String speed;
    public String throttle;
    public String filename=""; //用来保存存储的文件名
    public BluetoothSocket _socket = null;      //蓝牙通信socket
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;
	private BluetoothSocket btSocket;
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
	
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
	
	//创建数据显示开关点击响应事件监听器
	  private View.OnClickListener OnSpyOnOff = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(!Connect.this.m_Manager.IsConnected())
			{
				((MainActivity) Connect.this.m_Manager.getM_Connect()).PleaseDoConnection();
				Connect.this.m_SpyOnOff.setChecked(false);
				return ;
			}
			if (Connect.this.m_SpyOnOff.isChecked())
			{
				Connect.this.m_Manager.SpyOn();
				return ;
			}
			Connect.this.m_Manager.SpyOff();
		}
	};
	
	//初始化该Activity的全部UI组件
	public void init()
	{
		this.TextConnectedTo = (TextView) findViewById(R.id.TextConnectedToConn);
		this.Disconnect = (Button) findViewById(R.id.DisconnectConn);
		this.CanRPM = (TextView) findViewById(R.id.textViewRPMCan);
		this.CanSpeed = (TextView) findViewById(R.id.textViewSpeedCan);
		this.CanThrottle = (TextView) findViewById(R.id.textViewThrottleCan);
		this.m_SpyOnOff = (ToggleButton) findViewById(R.id.buttonSpyOnOffCan);
		this.m_SpyOnOff.setTextOff(getResources().getString(R.string.SpyOff));
		this.m_SpyOnOff.setTextOn(getResources().getString(R.string.SpyOn));
		this.m_SpyOnOff.setChecked(false);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection); 		  
       
        init();       //加载该类中的UI控件
        
        this.Disconnect.setOnClickListener(this.OnDisconnect);
//        this.m_SpyOnOff.setOnClickListener(this.OnSpyOnOff);				//存在点击异常
        
       //如果打开本地蓝牙设备不成功，提示信息，结束程序
       if (_bluetooth == null){
        	Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // 设置设备可以被搜索  
       new Thread(){
    	   public void run(){
    		   if(_bluetooth.isEnabled()==false){
        		_bluetooth.enable();
    		   }
    	   }   	   
       }.start();   
       
      
//       if (this.IsConnected()) {
//           this.TextConnectedTo.setText(getString(R.string.ConnectedTo) + this.m_Manager.getM_Data().getM_Device().getName());
//       }
    }

    public boolean IsConnected()
    {
    	return (this._socket != null) && (this._socket.isConnected());
    }
    
    public PC_Data getM_Data()
    {
  	  return this.m_Data;
    }
    
    public BluetoothDevice getM_Device()
    {
		  return this._device;
    }
    //发送按键响应
    public void onSendButtonClicked(View v){
    	int i=0;
    	int n=0;
    	try{
    		OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
    		byte[] bos = edit0.getText().toString().getBytes();
    		for(i=0;i<bos.length;i++){
    			if(bos[i]==0x0a)n++;
    		}
    		byte[] bos_new = new byte[bos.length+n];
    		n=0;
    		for(i=0;i<bos.length;i++){ //手机中换行为0a,将其改为0d 0a后再发送
    			if(bos[i]==0x0a){
    				bos_new[n]=0x0d;
    				n++;
    				bos_new[n]=0x0a;
    			}else{
    				bos_new[n]=bos[i];
    			}
    			n++;
    		}
    		
    		os.write(bos_new);	
    	}catch(IOException e){  		
    	}  	
    }
    
    //接收活动结果，响应startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode){
    	case REQUEST_CONNECT_DEVICE:     //连接结果，由DeviceListActivity设置返回
    		// 响应返回结果
            if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
                // MAC地址，由DeviceListActivity设置返回
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // 得到蓝牙设备句柄      
                _device = _bluetooth.getRemoteDevice(address);
 
                // 用服务号得到socket
                try{
                	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                }catch(IOException e){
                	Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                }
                //连接socket
            	Button btn = (Button) findViewById(R.id.DisconnectConn);
            	TextView tv = (TextView) findViewById(R.id.TextConnectedToConn);
                try{
                	_socket.connect();
                	Toast.makeText(this, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
                	btn.setText("断开");
                	tv.setText("connected to BT-06");
//                	tv.setText(getString(R.string.ConnectedTo) + this.m_Manager.getM_Data().getM_Device().getName());
                }catch(IOException e){
                	try{
                		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                		_socket.close();
                		_socket = null;
                	}catch(IOException ee){
                		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                	}
                	
                	return;
                }
                
                //接收硬件通过蓝牙上传的数据
                try{
            		is = _socket.getInputStream();   //得到蓝牙数据输入流
            		}catch(IOException e){
            			Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
            			return;
            		}
                	try {
						if(bThread == false){
							//延时
							Thread.sleep(200);
							//开启读取数据的工作线程
							ReadThread.start();
							bThread = true;
						}else{
							bRun = true;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

            		/*if(bThread==false){
            			
            			//延时
            			try {
    						ReadThread.sleep(200);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
            			//开启读取数据的工作线程
            			ReadThread.start();
//            			thread.start();
            			bThread=true;
            		}else{
            			bRun = true;
            		}*///------>>存在运行运行异常
            }
    		break;
    	default:break;
    	}
    }
   
    //接收数据线程
    Thread ReadThread=new Thread(){
    	
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
    					//接收数据缓存时，加入延时
    					Thread.sleep(200);
    					
    					num = is.read(buffer);         //读入数据
    					n=0;
     					smsg = "";
    					String s0 = new String(buffer,0,num);
     					fmsg = s0;
//    					fmsg +=s0;    //保存收到数据
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
    					smsg +=s;   //写入接收缓存
    					
    					if(is.available()==0)break;  //短时间没有数据才跳出进行显示
    				}
    				
    				System.out.println(smsg);
    				//启动一条子线程来响应蓝牙模块上传的数据	
    			 /*	new Thread()
    				{
    					@Override
    					public void run()
    					{
    						try {
    							 
								Message msg = handler.obtainMessage();
								handler.sendMessage(msg);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    					}
    				}.start();*/
    			 
            		//使用Handler发送信息,进行显示刷新
//            		try {
//            			Message message = handler.obtainMessage();
//            			handler.sendMessage(message);
//            		} catch (Exception e) {
//            			// TODO Auto-generated catch block
//            			e.printStackTrace();
//            		} ----------------------------------------->>此处存在运行异常，待解决
    				
    				//使用Handler发送信息,进行显示刷新
//    				Message message = handler.obtainMessage();
//    				handler.sendMessage(message);    
    					
    	    		}catch(IOException | InterruptedException e){
    	    			System.out.println("未收到数据");
    	    			e.printStackTrace();
    	    		}
    		}
    	}
    };
    
    //消息处理队列
    Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		 
    		//处理数据，截取转速，车速，油门开度
    		
    		rmp = smsg.substring(smsg.indexOf('R')+1, smsg.indexOf('S'));
    		CanRPM.setText(rmp); 	  	//显示转速
    			
    		speed = smsg.substring(smsg.indexOf('S')+1, smsg.indexOf('T'));
    		CanSpeed.setText(speed);	//显示车速
    		
    		throttle = smsg.substring(smsg.indexOf('T')+1, smsg.indexOf('E'));
     		CanThrottle.setText(throttle);//显示油门开度
    		
    	}
    };
    
   //新建一个线程实时的发送数据
    Thread thread = new Thread()
    {
    	public void run()
    	{
    		String [] Buffer = {"R1000S15T10E", "R1500S20T15E", "R2000S25T20E", "R2500S30T25E", "R3000S35T30E", "R3500S40T35E", "R4000S4550T40E", "R4500S60T45E", "R5000S80T50E", "R6000S100T60E"};
    		int i;
    		for(i=0; i < 10; i++)
    		{
    			smsg = Buffer[i];
    			System.out.println(smsg);
    		
    			try {
    				Thread.sleep(2000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        		//发送信息
        		Message m = handler.obtainMessage();
        		handler.sendMessage(m);
        		
    		}
    	}
    };
    
    //关闭程序掉用处理部分
    public void onDestroy(){
    	super.onDestroy();
//    	if(_socket!=null)  //关闭连接socket
//    	try{
//    		_socket.close();
//    	}catch(IOException e){}
    //	_bluetooth.disable();  //关闭蓝牙服务
    }
  
    //连接按键响应函数
    public void onConnectButtonClicked(View v){ 
    	if(_bluetooth.isEnabled()==false){  //如果蓝牙服务不可用则提示
    		Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
        //如未连接设备则打开DeviceListActivity进行设备搜索
    	Button btn = (Button) findViewById(R.id.DisconnectConn);
    	if(_socket==null){
    		Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳转程序设置
    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
    	}
    	else{
    		 //关闭连接socket
    	    try{
    	    	
    	    	is.close();
    	    	_socket.close();
    	    	_socket = null;
    	    	bRun = false;
    	    	btn.setText("连接");
    	    }catch(IOException e){}   
    	}
    	return;
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

	//点击选项菜单响应函数
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case R.id.menu_home:
			GoHome();
			break;
		case R.id.menu_connnection:
			Launch_Connection();
			break;
		case R.id.menu_cansettings:
			Launch_CanSettings();
			break;
		case R.id.menu_boxsettings:
			Launch_BoxSettings();
			break;
		case R.id.menu_help:
			Launch_Help();
			break;
		case R.id.menu_logs:
			Launch_Logs();
			break;
		case R.id.menu_about:
			Launch_About();
			break;
		case R.id.menu_demo:
			Launch_Demo();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		
		return true;
	}

	public void ActivityFinish()
	{
		this.m_Manager.deleteBoxSettings();
		finish();
	}
	
	public void GoHome()
	{
		ActivityFinish();
	}
	
	public void Launch_Connection()
	{
		startActivity(new Intent(this, Connect.class));
		ActivityFinish();
	}
	
	public void Launch_CanSettings()
	{
		startActivity(new Intent(this, CanSettings.class));
		ActivityFinish();
	}
	
	public void Launch_BoxSettings()
	{
		
	}
	
	public void Launch_Help()
	{
		startActivity(new Intent(this, Help.class));
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

	public String getRmp() {
		return rmp;
	}

	public void setRmp(String rmp) {
		this.rmp = rmp;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getThrottle() {
		return throttle;
	}

	public void setThrottle(String throttle) {
		this.throttle = throttle;
	}
	
}