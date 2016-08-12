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

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
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
 * CanSettings�࣬����ѡ����Ccg�ļ�����ʾ���ƺлش���CAN���ݣ�RPM��Speed��Throttle��
 * @author TWL
 * */
public class CanSettings extends Activity {

	private Button CANSettings;			//����CAN�����ļ���ť
	private TextView CanRPM;			//������ת��
	private	TextView CanSpeed;			//����
	private TextView CanThrottle;		//���ſ���
	private TextView CcgFile_tv;
	private ToggleButton m_SpyOnOff;		//���ݿ���
	
	private Fac_Manager m_Manager;
	public static final int FILE_SELECT_CODE = 1000;    //ѡ���ļ� ������
	public static final String SEND_FILE_NAME = "MG GT.ccg";
	private static final String BASEDIR = "SoundCreator";
	private String CcgFileName;
	private Connect m_connect;
	private byte[] m_CcgBytes;
	private BluetoothSocket _socket;
	private String m_CcgName;
	boolean isConnected = true;
	private String path;
	private Uri uri;
	private String CcgBuffer;	
	
	//����CAN���ð�ť�����Ӧ�¼�������
	private View.OnClickListener OnCANSettings = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			//��ȡ�ֻ��洢��ѡȡ��Ҫ���ص�Ccg�ļ�
			Intent intent= new Intent("android.intent.action.GET_CONTENT");
		    intent.setType("*/*");
		    intent.addCategory("android.intent.category.OPENABLE");
		    CanSettings.this.startActivityForResult(Intent.createChooser(intent, "Choose Ccg file"), FILE_SELECT_CODE);
		}
	}; 

	//����������ʾ���ص����Ӧ�¼�������
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
//		this.m_SpyOnOff.setOnClickListener(OnSpyOnOff);         //�����쳣
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
	
	//��SD�е��ļ�  
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
			// ����File����ȷ����Ҫ��ȡ�ļ�����Ϣ 
//			File file = new File(path, CcgFileName);
			File file = new File(path);
//					if(!file.exists())
//					Log.d("�ļ������� ","file == null");

			// FileInputSteam �������Ķ���
			FileInputStream fis = new FileInputStream(file);  
			// �ֽ������Ŷ�ȡ������  
			byte[] buffer = new byte[fis.available()];  
			// ��ʼ�����ļ��Ķ�ȡ   
			fis.read(buffer);    
			  
			//���ֽ�����ת�����ַ����� ��ת������ĸ�ʽ    
			String res = EncodingUtils.getString(buffer, "GBK");  
			//ѭ����ȡ�ļ�����
			while((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			//�ر���     
			fis.close();
			
			
			//��ȡָ���ļ��Ķ�Ӧ������
			FileInputStream fis = new FileInputStream(file);
			//��ָ����������װ��BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuffer sb = new StringBuffer("");
			String line = null;
			//ѭ����ȡ�ļ�����
			while((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			//�ر���Դ
			br.close();
			return sb.toString();
			 
		}catch(Exception ex){  
//			Toast.makeText(CanSettings.this, "�ļ���ȡʧ�ܣ�", 1000).show();  
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
	//��ʼ����Activity��ȫ��UI���
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
	 * �����Ӳ˵�֮���໥��ת�ĺ���
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
	public boolean onOptionsItemSelected(MenuItem item) {
	 
		//ͨ���˵����ID��Ӧÿ���˵�
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
					return super.onOptionsItemSelected(item);		//��û�д�����¼��������ദ��
				}
				return true;		//����true��ʾ������˵���ĵ���¼�������Ҫ���¼�����
				
	}
	/**
	 * Activity�ص����������ڷ���ѡ��Ccg�ļ��Ľ��
	 * */
  	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
  		if(resultCode==RESULT_OK){
  			uri = data.getData();
  			Log.i("uri", uri.toString());
  			path = uri.getPath();			//��ȡ�ļ���·��
  			Log.d("path", path);
  			
			if (!path.substring(path.lastIndexOf(".") + 1).equals("ccg"))
			{
				Toast toast = Toast.makeText(this, "Error: Please select a .ccg file", 1);
				toast.setGravity(17, 0, 0);
				toast.show();
			}else {
				
//				readCcgFile();
				CcgFileName = path.substring(path.lastIndexOf("/") + 1, path.length());
				CcgFile_tv.setText(CcgFileName);			//��ʾ��ѡCcg�ļ���
				Toast.makeText(this, R.string.TransferComplete, 0).show();       //��ʾ���سɹ�

				readCcgFile();			//��ȡCcg�ļ�����
				
				try {
					Log.i("----------------", "��ʼ�����ļ���");
					sendCcgFile();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
//  			if (!uri.getPath().substring(uri.getPath().lastIndexOf(".") + 1).equals("ccg"))
//  			{
//  				Toast toast = Toast.makeText(this, "Error: Please select a .ccg file", 1);
//  				toast.setGravity(17, 0, 0);
//  				toast.show();
//  				return ;
//  			}
//  			try {
//  				//ȡ��ѡ����ļ���·�����ļ���
//  				path = uri.getPath();
//  				CcgFileName = path.substring(path.lastIndexOf("/") + 1, path.length());
//  				CcgFile_tv.setText(CcgFileName);
//  				Toast.makeText(this, R.string.TransferComplete, 0).show();       //��ʾ���سɹ�
////						CcgFlashed();
//  			} catch (Exception e) {	
//  				Toast.makeText(this, R.string.TransferFailed, 1).show();			//��ʾ����ʧ��
//  			}
//  			//��ȡѡ���Ccg�ļ�
//  			 readCcgFile();
  		}
  		
  	}
  	
  	/**
	 * ��ȡSD���ļ�·������ȡCcg�ļ�����
	 * @return   sb.toString() �ļ�����
	 */
	public String readCcgFile()
	{
		
		try {
			//����ֻ�������SD��������Ӧ�ó�����з���SD����Ȩ��
			if(Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED))
			{
				//��ȡSD����Ӧ�Ĵ洢Ŀ¼
				File sdCardDir = Environment.getExternalStorageDirectory();
				System.out.println("Ĭ��SD��·��" + sdCardDir);
				//��ȡָ���ļ��Ķ�Ӧ������
//				FileInputStream fis = new FileInputStream( 
//						sdCardDir.getCanonicalPath() + SEND_FILE_NAME);
				FileInputStream fis = new FileInputStream(uri.getPath());			//�����ļ�·������������
				//��ָ����������װ��BufferedReader
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				StringBuffer sb = new StringBuffer("");
				String line = null;
				//ѭ����ȡ�ļ�����
				while((line = br.readLine()) != null)
				{
					sb.append(line);
				}
				CcgBuffer = sb.toString();
				System.out.println(CcgBuffer);
				//�ر���Դ
				br.close();
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
  		  byte[] ccg = ReadCcgFile(localFile);         	//��������CGG�ļ�����������
  		  setM_CcgBytes(ccg);				//������������е��ļ�תΪ�ֽ���
  		  sendCcgCmd();
  		
  	}
  //�Ի�����ʾ�û����������豸
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
  	
  	/**
  	 * ��ȡ�����������ѡ���Ccg�ļ�
  	 */
  	public void sendCcgFile()
  	{
  		int i=0;
    	int n=0;
    	try{
    		OutputStream os = _socket.getOutputStream();   //�������������
//    		byte[] bos = edit0.getText().toString().getBytes();
//    		byte[] ccg = this.ReadCcgFile(getFilesDir());
    		byte[] ccg  = CcgBuffer.getBytes();
    		for(i=0;i<ccg.length;i++)
    		{
    			if(ccg[i]==0x0a)
    				n++;
    		}
    		byte[] ccg_new = new byte[ccg.length+n];
    		n=0;
    		for(i=0;i<ccg.length;i++){ //�ֻ��л���Ϊ0a,�����Ϊ0d 0a���ٷ���
    			if(ccg[i]==0x0a){
    				ccg_new[n]=0x0d;
    				n++;
    				ccg_new[n]=0x0a;
    			}else{
    				ccg_new[n]=ccg[i];
    			}
    			n++;
    		}
    		
    		os.write(ccg_new);	
    		
    		Log.d("Ccg�ļ��ѷ���", ccg_new.toString());
    	}catch(IOException e){  		
    	}  	
  	}
}
