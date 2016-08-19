package com.longtek.bluetooth_control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.UUID;

import org.apache.http.util.EncodingUtils;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * CanSettings类
 * 用于选择发送Ccg文件，显示控制盒回传的CAN数据（RPM、Speed、Throttle）
 * @author TWL
 * */
public class CanSettings extends Activity {

	private Button CANSettings;			//设置CAN配置文件按钮
	private TextView CanRPM;			//发动机转速
	private	TextView CanSpeed;			//车速
	private TextView CanThrottle;		//油门开度
	private TextView CcgFile_tv;
	private ToggleButton m_SpyOnOff;		//数据开关
	
	private Connect m_Connect;
	private Fac_Manager m_Manager;
	public static final int FILE_SELECT_CODE = 1000;    //选择文件 请求码
	public static final String SEND_FILE_NAME = "MG GT.ccg";
	private static final String BASEDIR = "SoundCreator";
	private String CcgFileName;
	private Connect m_connect;
	private byte[] m_CcgBytes;
	public BluetoothSocket _socket;
	private String m_CcgName;
	boolean isConnected = true;
	private String path;
	private Uri uri;
	private String CcgBuffer;	
	
	//创建CAN设置按钮点击响应事件监听器
	private View.OnClickListener OnCANSettings = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			//读取手机存储，选取需要加载的Ccg文件
			Intent intent= new Intent("android.intent.action.GET_CONTENT");
		    intent.setType("*/*");
		    intent.addCategory("android.intent.category.OPENABLE");
		    CanSettings.this.startActivityForResult(Intent.createChooser(intent, "Choose Ccg file"), FILE_SELECT_CODE);
		}
	}; 

	//创建数据显示开关点击响应事件监听器
	  private View.OnClickListener OnSpyOnOff = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(!CanSettings.this.m_Manager.IsConnected())
			{
				((MainActivity) CanSettings.this.m_Manager.getM_Connect()).PleaseDoConnection();
				CanSettings.this.m_SpyOnOff.setChecked(false);
				return ;
			}
			if (CanSettings.this.m_SpyOnOff.isChecked())
			{
				CanSettings.this.m_Manager.SpyOn();
				return ;
			}
			CanSettings.this.m_Manager.SpyOff();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cansettings);
		
		init();
		this.CANSettings.setOnClickListener(this.OnCANSettings);
//		this.m_SpyOnOff.setOnClickListener(OnSpyOnOff);         //存在异常
		this.m_Manager = Fac_Manager.getManager();
 
	}
	
	/* public static String read(String name) {
	        File sdcard = Environment.getExternalStorageDirectory();
	        String sdcardPath = sdcard.getPath();
	        File file = new File(sdcardPath + "/"+BASEDIR+"/" + name + ".ccg");
	        StringBuilder text = new StringBuilder();
	        try {
	            BufferedReader br = new BufferedReader(new FileReader(file));
	            String line;
	            while ((line = br.readLine()) != null) {
	                text.append(line);
	                text.append('\n');
	            }
	            br.close();
	            return text.toString();
	        }
	        catch (IOException e) {
	            return e.getMessage();
	        }
	    }*/
	
	//读SD中的文件  
	public String readFileSdcardFile(String fileName) throws IOException{   
	  String res="";   
	  try{   
	         FileInputStream fis = new FileInputStream(CcgFileName);   
	  
	         int length = fis.available();   
	  
	         byte [] buffer = new byte[length];   
	         fis.read(buffer);       
	  
	         res = EncodingUtils.getString(buffer, "UTF-8");   
	  
	         fis.close();       
	        }   
	  
	        catch(Exception e){   
	         e.printStackTrace();   
	        }   
	        return res;   
	}   
	
	/*public String readCcgFile(String CcgFileName){  
		try{  
			Log.d("start read file! ", path + CcgFileName);
			// 创建File对象，确定需要读取文件的信息 
//			File file = new File(path, CcgFileName);
			File file = new File(path);
//					if(!file.exists())
//					Log.d("文件不存在 ","file == null");

			// FileInputSteam 输入流的对象
			FileInputStream fis = new FileInputStream(file);  
			// 字节数组存放读取的数据  
			byte[] buffer = new byte[fis.available()];  
			// 开始进行文件的读取   
			fis.read(buffer);    
			  
			//将字节数组转换成字符串， 并转换编码的格式    
			String res = EncodingUtils.getString(buffer, "GBK");  
			//循环读取文件内容
			while((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			//关闭流     
			fis.close();
			
			
			//获取指定文件的对应输入流
			FileInputStream fis = new FileInputStream(file);
			//将指定输入流包装成BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuffer sb = new StringBuffer("");
			String line = null;
			//循环读取文件内容
			while((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			//关闭资源
			br.close();
			return sb.toString();
			 
		}catch(Exception ex){  
//			Toast.makeText(CanSettings.this, "文件读取失败！", 1000).show();  
			ex.printStackTrace();
		}
		
		
	}  
	*/
	
	
	public void CcgFlashed(boolean isConnected)
	{
		if (isConnected)
	    {
			this.CcgFile_tv.setText(this.getCcgFileName());
			Toast.makeText(this, R.string.TransferComplete, 0).show();
			return;
	    }
	    Toast.makeText(this, R.string.TransferFailed, 1).show();
	}
	
	public CharSequence getCcgFileName()
	{
		return this.getM_CcgName();
	}

	public String getM_CcgName()
	{
		return this.m_CcgName;
	}
	 
	public void CcgLoaded(boolean isConnected)
	{
	}
	
	public void SpyOnOff(boolean isConnected)
	{
		if (isConnected)
	    {
			Toast.makeText(this, "Spy On", 1).show();
			this.m_SpyOnOff.setBackgroundColor(-16711936);
			return;
	    }
	    Toast.makeText(this, "Spy Off", 1).show();
	    this.m_SpyOnOff.setBackgroundResource(17301508);
	    this.CanRPM.setText("");
	    this.CanSpeed.setText("");
	    this.CanThrottle.setText("");
	}
	
	public void UpdateRPMSpeedThrottle(int RPM, int Speed, int Throttle)
	{
		String rpm1 = String.format("%d", new Object[] { Integer.valueOf(RPM) });
	    this.CanRPM.setText(rpm1);
	    String speed1 = String.format("%d", new Object[] { Integer.valueOf(Speed) });
	    this.CanSpeed.setText(speed1);
	    String throttle1 = String.format("%d", new Object[] { Integer.valueOf(Throttle) });
	    this.CanThrottle.setText(throttle1);
	}
	
	//初始化该Activity的全部UI组件
	public void init()
	{
		this.CANSettings = (Button) findViewById(R.id.ButtonBrowseCcgCan);
		this.CanRPM = (TextView) findViewById(R.id.textViewRPMCan);
		this.CanSpeed = (TextView) findViewById(R.id.textViewSpeedCan);
		this.CanThrottle = (TextView) findViewById(R.id.textViewThrottleCan);
		this.CcgFile_tv = (TextView) findViewById(R.id.TextCcgFileCan);
		this.m_SpyOnOff = (ToggleButton) findViewById(R.id.buttonSpyOnOffCan);
		this.m_SpyOnOff.setTextOff(getResources().getString(R.string.SpyOff));
		this.m_SpyOnOff.setTextOn(getResources().getString(R.string.SpyOn));
		this.m_SpyOnOff.setChecked(false);
	}
	
	public void ActivityFinish()
	{
		finish();
	}
	
	public void GoHome()
	{
		finish();
	}
	/**
	 * 各个子菜单之间相互跳转的函数
	 * */
	public void Launch_Connection()
	{
		startActivity(new Intent(this, Connect.class));
		ActivityFinish();
	}
	
	public void Launch_CanSettings()
	{
		
	}
	public void Launch_BoxSettings()
	{
		startActivity(new Intent(this, BoxSettings.class));
		ActivityFinish();
	}
	
	public void Launch_Demo()
	{
		startActivity(new Intent(this, Demo.class));
		ActivityFinish();
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
	public boolean onOptionsItemSelected(MenuItem item) {
	 
		//通过菜单项的ID响应每个菜单
				switch (item.getItemId())
				{
				case R.id.menu_home:
					GoHome();
					break;
				case R.id.menu_connnection:
					Launch_Connection();
					break;
				case R.id.menu_demo:
					Launch_Demo();
					break;
				case R.id.menu_boxsettings:
					Launch_BoxSettings();
					break;
				case R.id.menu_cansettings:
					Launch_CanSettings();
					break;
				case R.id.menu_help:
					Launch_Help();
					break;
				case R.id.menu_about:
					Launch_About();
					break;
				case R.id.menu_logs:
					Launch_Logs();
					break;
				default:
					return super.onOptionsItemSelected(item);		//对没有处理的事件交给父类处理
				}
				return true;		//返回true表示处理完菜单项的点击事件，不需要将事件传播
				
	}
	/**
	 * Activity回调函数，用于返回选择Ccg文件的结果
	 * */
  	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
  		if(resultCode==RESULT_OK){
  			uri = data.getData();
  			Log.i("uri", uri.toString());
  			path = uri.getPath();			//获取文件的路径
  			Log.d("path", path);
  			
			if (!path.substring(path.lastIndexOf(".") + 1).equals("ccg"))
			{
				Toast toast = Toast.makeText(this, "Error: Please select a .ccg file", 1);
				toast.setGravity(17, 0, 0);
				toast.show();
			}else {
				
				CcgFileName = path.substring(path.lastIndexOf("/") + 1, path.length());
				CcgFile_tv.setText(CcgFileName);			//显示所选Ccg文件名
				
				Log.d("------------------------------", "准备读取文件....");
				readCcgFile();			//读取Ccg文件内容
				
				try {
					Log.i("----------------", "准备发送文件...");
					sendCcgFile();
//					sendDataToPairedDevice(CcgBuffer, ((Connect)this.m_connect)._device);
					Toast.makeText(this, R.string.TransferComplete, 0).show();       //提示加载成功
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(this, R.string.TransferFailed, 1).show();			//提示加载失败
				}
			}
  		}
  		
  	}
  	
	/**
  	 * 获取输出流，发送选择的Ccg文件
  	 */
  	public void sendCcgFile()  
  	{
  		 
    	Log.d("--------------------", "开始打开输出流");
				try {
//					if(!((Connect)this.m_connect).IsConnected())
//					if(!IsConnected())
//					{
//						Log.d("-----------------------", "重新连接蓝牙！");
//						PleaseDoConnection();
//					}else{
						OutputStream os = ((Connect)this.m_connect)._socket.getOutputStream();    //空指针异常
						byte[] ccg  = CcgBuffer.getBytes("utf-8");
//						byte[] ccg  = sb.toString().getBytes("utf-8");
						os.write(ccg);          //写入流
						os.flush();
						Log.d("-------------------------------", "发送文件中.....");
						//关闭输出流，关闭Socket
						os.close();
						_socket.close();
						
						Log.d("Ccg文件已发送", ccg.toString());
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//    		 while (true)
//             {
//                 BufferedReader reader = new BufferedReader(
//                         new InputStreamReader(System.in));
//  
//                 String line = reader.readLine();
//  
//                 os.write(line.getBytes());
//             }
//         }
//         catch (IOException e)
//         {
//             e.printStackTrace();
//         }
//    		
  	}
  	
  	private void sendDataToPairedDevice(String message ,BluetoothDevice device){       
  		byte[] toSend = message.getBytes();
  		try {
//  			UUID applicationUUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
  			UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  		 
  			BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(applicationUUID);
  			OutputStream mmOutStream = socket.getOutputStream();
  			mmOutStream.write(toSend);
  			// Your Data is sent to  BT connected paired device ENJOY.
  		} catch (IOException e) {
  			Log.e("SendData", "Exception during write", e);
  		}
  	}
//调用函数
//sendDataToPairedDevice("text to send" ,bluetoothDevice);
  	/**
	 * 获取SD卡文件路径，读取Ccg文件内容
	 * @return  文件内容  sb.toString() 
	 */
	public String readCcgFile()
	{
		
		try {
			//如果手机插入了SD卡，而且应用程序具有访问SD卡的权限
			if(Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED))
			{
				File sdCardDir = Environment.getExternalStorageDirectory();
				//获取SD卡对应的存储目录
				System.out.println("---------------------" + "开始读取文件内容...");
				//获取指定文件的对应输入流
//				FileInputStream fis = new FileInputStream( 
//						sdCardDir.getCanonicalPath() + SEND_FILE_NAME);
				
				FileInputStream fis = new FileInputStream(uri.getPath());			//传入文件路径创建输入流
				//将指定输入流包装成BufferedReader
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				StringBuffer sb = new StringBuffer("");
				String line = null;
				//循环读取文件内容
				while((line = br.readLine()) != null)
				{
					sb.append(line);
				}
				Log.d("-----------------------", "所选CCG文件内容为:  ");
				CcgBuffer = sb.toString();
				System.out.println(CcgBuffer);
				//关闭资源
				br.close();
				Log.d("-----------------------", "文件读取完毕！流已关闭！！！");
				return CcgBuffer;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
  	public void LoadCcg(Uri uri)
  	{
  		if(!((Connect)this.m_connect).IsConnected())
  		{
  			PleaseDoConnection();
  		}
  		 
  		  File localFile = new File(uri.getPath());
  		  setM_CcgName(uri.getLastPathSegment());
  		  byte[] ccg = ReadCcgFile(localFile);         	//将读到的CGG文件存在数组中
  		  setM_CcgBytes(ccg);				//将存放在数组中的文件转为字节码
  		  sendCcgCmd();
  		
  	}
  //对话框提示用户连接蓝牙设备
  	public void PleaseDoConnection()
  	{
  	    Toast toast = Toast.makeText(this, R.string.PleaseDoConnection, 1);
  	    toast.setGravity(17, 0, 0);
  	    toast.show();
  	}
  	
  	public void setM_CcgBytes(byte[] ArrayOfByte)
  	{
  		this.m_CcgBytes = ArrayOfByte;
  	}
  	
  	public byte[] ReadCcgFile(File file)
  	{
  		byte[] localObject2 = null;
  		return (byte[]) localObject2;
  	}
  	
  	 public void sendCcgCmd()
 	 {
 		 write("c".getBytes());
 	 }
  	 
  	public void write(byte[] ArrayOfByte)
	{
  		
	}

  	public void setM_CcgName(String string)
  	{
  		this.m_CcgName = string;
  	}
  	
  	public boolean IsConnected()
  	{
  		return (this._socket != null) && (this._socket.isConnected());
  	}
     
}
