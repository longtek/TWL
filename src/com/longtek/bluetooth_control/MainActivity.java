package com.longtek.bluetooth_control;

import java.lang.reflect.Method;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;

/**
 * 应用程序主类
 * @author TWL
 *
 */
public class MainActivity extends FragmentActivity
{
	private Button Connect;
	private ImageButton Search;
	private ImageButton Next;
	private ImageButton Previous;
	private TextView NBox;
	private Button Plus;
	private Button Moins;
	private ProgressBar Volume;
	private TextView TextVolume;
	private ImageButton VolumeOn;
	private TextView Debug;
	
	private Fac_Manager m_Manager;
	private ArrayAdapter<String> BTArrayAdapter;
	static final int PICKBOXFILE = 0;
	static final int PICKCCGFILE = 1;
	static final int LAUNCHCONNECT = 2;
	static final int LAUNCHDEMO = 3;
	private static final int LAUNCHCANSETTINGS = 4;
	private static final int LAUNCHBOXSETTINGS = 5;
	protected static final int BLUETOOTH_ACTIVATION = 0;
	
	private View.OnClickListener OnRefresh = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity.this.Launch_Connection();
		}
	};

	private View.OnClickListener OnDisconnect = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MainActivity.this.m_Manager.disconnect();
		}
	};
	
	private View.OnClickListener OnPrevious = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			MainActivity.this.m_Manager.Previous();
		}
	};
	
	private View.OnClickListener OnSearch = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

			if(!MainActivity.this.m_Manager.IsConnected())
			{
				MainActivity.this.PleaseDoConnection();
				return;
			}
			new DialogBox().show(MainActivity.this.getSupportFragmentManager(), "DialogBox");
		}
	};
	
	private View.OnClickListener OnNext = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity.this.m_Manager.Next();
		}
	};
	
	private View.OnClickListener OnVolPlus = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MainActivity.this.m_Manager.VolPlus();
		}
	};
	
	private View.OnClickListener OnVolMoins = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MainActivity.this.m_Manager.VolMoins();
		}
	};
	
	private View.OnClickListener OnVolumeOn = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MainActivity.this.m_Manager.SwitchVolumeMuted();
		}
	};
	
	
	//音量键操作
	public void BoxMuted(boolean paramBoolean)
	{
		if (paramBoolean)
	    {
			this.VolumeOn.setImageResource(R.drawable.volumemuted);
			this.VolumeOn.setBackgroundColor(getResources().getColor(R.color.red));
			this.Volume.setEnabled(false);
			return;
	    }
	    this.VolumeOn.setImageResource(R.drawable.volumeon);
	    this.VolumeOn.setBackgroundColor(getResources().getColor(R.color.blue));
	    this.Volume.setEnabled(true);
	}

	public void ConnectionDone(boolean paramBoolean)
	{
		if (paramBoolean)
	    {
			this.Connect.setBackgroundColor(-16711936);
			String str = getString(R.string.ConnectedTo) + this.m_Manager.getM_Data().getM_Device().getName();
			this.Connect.setText(str);
			CleanBTArrayAdapter();
			return;
	    }
	    this.Connect.setBackgroundResource(17301508);
	    this.Connect.setText(R.string.ConnectToSoundCreator);
	    Clean_NBox();
	    updateVolume(0);
	}
	
	private boolean CleanBTArrayAdapter()
	{
		this.BTArrayAdapter = new ArrayAdapter(this, 17367043);
	    return true;
	}

	public void Clean_NBox()
	{
		this.NBox.setText("");
	}
	
	public void updateNBox(int Int)
	{
		this.NBox.setText("Currently playing: " + this.m_Manager.getM_Data().getM_ListBox()[Int] + ".box");
	}
	
	public void updateVolume(int paramInt)
	{
		if (paramInt >= 0)
	    {
			this.Volume.setIndeterminate(false);
			this.Volume.setProgress((int)(Math.log10(paramInt / 100) / Math.log10(655.0D) * this.Volume.getMax()));
			String str = String.format("%d", new Object[] { Integer.valueOf((int)Math.round(20.0D * Math.log10(paramInt / 65535.0F))) }) + " dB";
			this.TextVolume.setText(str);
			return;
	    }
	    this.Volume.setIndeterminate(true);
	    this.TextVolume.setText(" ");
	}
	
	public void Debug(int paramInt)
	{
		
	}
	
	public String DeviceToString(BluetoothDevice paramBluetoothDevice)
	{
	    return paramBluetoothDevice.getName() + "\n" + paramBluetoothDevice.getAddress();
	}
	
	public void NewDeviceDetected(BluetoothDevice paramBluetoothDevice)
	{
	    appendBTArrayAdapter(DeviceToString(paramBluetoothDevice));
	}
	
	public boolean appendBTArrayAdapter(String paramString)
	{
	    this.BTArrayAdapter.add(paramString);
	    return true;
	}
	
	public void Error()
	{
		
	}
	
	//打Toast显示错误操作提示
	public void WrongMessage(String string)
	{
	    Toast toast = Toast.makeText(this, "Error: Operation failed. Please try again.", 1);
	    toast.setGravity(17, 0, 0);
	    toast.show();
	}
	
	public void BoxListAvailable(String[] ArrayOfString)
	{
	    int i = ArrayOfString.length;
	}
	
	//初始化该类下的所有UI组件
	private void init()
	{
		this.Connect = (Button) findViewById(R.id.ButtonConnect);
		this.Search = ((ImageButton)findViewById(R.id.ButtonSearch));
		this.Next = ((ImageButton)findViewById(R.id.ButtonNext));
		this.Previous = ((ImageButton)findViewById(R.id.ButtonPrevious));
		this.NBox = ((TextView)findViewById(R.id.TextNBox));
		this.Plus = ((Button)findViewById(R.id.ButtonPlus));
		this.Moins = ((Button)findViewById(R.id.ButtonMoins));
		this.Volume = ((ProgressBar)findViewById(R.id.ProgressBarVolume));
		this.TextVolume = ((TextView)findViewById(R.id.TextVolume));
		this.VolumeOn = ((ImageButton)findViewById(R.id.ButtonVolume));
		this.Debug = ((TextView)findViewById(R.id.TextDebug));
		
		this.m_Manager = new Fac_Manager(this);                      //实例Fac_Manager类，用户触发事件不会异常退出
		this.BTArrayAdapter = new ArrayAdapter(this, 17367043);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		
		this.Connect.setOnClickListener(this.OnRefresh);
 		this.Search.setOnClickListener(this.OnSearch);
 		this.Previous.setOnClickListener(this.OnPrevious);
 		this.Next.setOnClickListener(this.OnNext);
 		this.Plus.setOnClickListener(this.OnVolPlus);
 		this.Moins.setOnClickListener(this.OnVolMoins);
 		this.VolumeOn.setOnClickListener(this.OnVolumeOn);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		
		setIconEnable(menu, true);
		return true;
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
				case R.id.memu_demo:
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
	
	
	public void GoHome()
	{
		
	}
	
	public void Launch_Connection()
	{
		startActivity(new Intent(this, Connect.class));
	}
	
	public void Launch_CanSettings()
	{
		startActivity(new Intent(this, CanSettings.class));
	}
	
	public void Launch_BoxSettings()
	{
		startActivity(new Intent(this, BoxSettings.class));
	}
	
	public void Launch_Demo()
	{
		startActivity(new Intent(this, Demo.class));
	}
	
	public void Launch_Help()
	{
		startActivity(new Intent(this, Help.class));
	}
	
	public void Launch_Logs()
	{
		startActivity(new Intent(this, Logs.class));
	}
	
	public void Launch_About()
	{
		startActivity(new Intent(this, About.class));
	}
 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   
	    switch (requestCode)
	    {
	    	case 0: 	
	    		Uri uri = data.getData();
	    		if (!uri.getPath().substring(uri.getPath().lastIndexOf(".") + 1).equals("box"))
	    		{
	    			Toast toast = Toast.makeText(this, "Please select a .box", 1);
	    			toast.setGravity(17, 0, 0);
	    			toast.show();
	    		}
	    		this.m_Manager.LoadBox(uri);
	    		break;
	    		
	    	case 1: 
	    		Uri uri1 = data.getData();
	    		if (!uri1.getPath().substring(uri1.getPath().lastIndexOf(".") + 1).equals("ccg"))
	    		{
	    			Toast toast= Toast.makeText(this, "Please select a .ccg", 1);
	    			toast.setGravity(17, 0, 0);
	    			toast.show();
	    		}
	    		this.m_Manager.LoadCcg(uri1);
	    		break;
	    		
	    	default: 
	    		super.onActivityResult(requestCode, resultCode, data);
	   
	    }
	}  
	
	public void PleaseDoConnection()
	{
	    Toast toast = Toast.makeText(this, R.string.PleaseDoConnection, 1);
	    toast.setGravity(17, 0, 0);
	    toast.show();
	}
	
	public ArrayAdapter<String> getBTArrayAdapter()
	{
	    return this.BTArrayAdapter;
	}
	
	public void setBTArrayAdapter(ArrayAdapter<String> paramArrayAdapter)
	{
	    this.BTArrayAdapter = paramArrayAdapter;
	}
	
	
}
