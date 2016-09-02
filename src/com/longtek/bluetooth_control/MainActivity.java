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
 * ���ࣺ����չʾӦ�ó��������漰�������߼�
 * 
 * @author TWL
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
	private static final int Vol = 0;
	private static final BluetoothDevice  BTdevice = null;
	public boolean Isconnected;
	
	
	//�����������ӵ���¼�������
	private View.OnClickListener OnRefresh = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity.this.Launch_Connection();      //�������������������ӽ���
		}
	};

	//�����Ͽ���ť�����¼�������
	private View.OnClickListener OnDisconnect = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MainActivity.this.m_Manager.disconnect();
		}
	};
	//���������л��ļ�����¼�
	private View.OnClickListener OnPrevious = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			MainActivity.this.m_Manager.Previous();
		}
	};
	//BOX�ļ��������
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
	//�����л��ļ�����¼�
	private View.OnClickListener OnNext = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity.this.m_Manager.Next();
		}
	};
	//������
	private View.OnClickListener OnVolPlus = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MainActivity.this.m_Manager.VolPlus();
		}
	};
	//������
	private View.OnClickListener OnVolMoins = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MainActivity.this.m_Manager.VolMoins();
		}
	};
	//����������ť
	private View.OnClickListener OnVolumeOn = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MainActivity.this.m_Manager.SwitchVolumeMuted();
		}
	};
	
	//������������ɫ��ʾ
	public void BoxMuted(boolean Boolean)
	{
		if (Isconnected)
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
/**
 *��������״̬����
 *��ʾ��ǰ���ӵ������豸�����ɨ�赽�������б�
 *@param  boolean Isconnected
 **/
	public void ConnectionDone(boolean Boolean)
	{
		if (Isconnected)
	    {
			this.Connect.setBackgroundColor(-16711936);       //���ð�ť����ɫ����ɫֵδ����
			String str = getString(R.string.ConnectedTo) + this.m_Manager.getM_Data().getM_Device().getName();
			this.Connect.setText(str);				//���õ�ǰ���������豸����
			CleanBTArrayAdapter();
			return;
	    }
	    this.Connect.setBackgroundResource(17301508);		      
	    this.Connect.setText(R.string.ConnectToSoundCreator);
	    Clean_NBox();             //����δ���ӣ������BOX�ļ�
	    updateVolume(0);
	}
	
	//��������б�
	private boolean CleanBTArrayAdapter()
	{
		this.BTArrayAdapter = new ArrayAdapter(this, 17367043);
	    return true;
	}

	//���BOX�ļ���ʾ�Ի���
	public void Clean_NBox()
	{
		this.NBox.setText("");
	}
	
	//���µ�ǰBOX�ļ�
	public void updateNBox(int Int)
	{
		this.NBox.setText("Currently playing: " + this.m_Manager.getM_Data().getM_ListBox()[Int] + ".box");
	}
	
	//ͬ����ǰ����
	public void updateVolume(int Int)
	{
		if (Vol >= 0)
	    {
			this.Volume.setIndeterminate(false);
			this.Volume.setProgress((int)(Math.log10(Vol / 100) / Math.log10(655.0D) * this.Volume.getMax()));
			//��ʽ��������С
			String str = String.format("%d", new Object[] { Integer.valueOf((int)Math.round(20.0D * Math.log10(Vol / 65535.0F))) }) + " dB";
			this.TextVolume.setText(str);				//��ʾ�ַ�������
			return;
	    }
	    this.Volume.setIndeterminate(true);
	    this.TextVolume.setText(" ");		//�����ǰ���ھ����������ÿ�
	}

	public void Debug(int Int)
	{
		
	}
	
	/**
	 *�����豸ת�ַ�������
	 *��ʾ�豸���ƺ�moc��ַ 
	 **/
	public String DeviceToString(BluetoothDevice device)
	{
	    return BTdevice.getName() + "\n" + BTdevice.getAddress();
	}
	//��ʾ�¼�⵽���豸
	public void NewDeviceDetected(BluetoothDevice device)
	{
	    appendBTArrayAdapter(DeviceToString(BTdevice));
	}
	//׷���¼�⵽���豸
	public boolean appendBTArrayAdapter(String string)
	{
	    this.BTArrayAdapter.add(DeviceToString(BTdevice));
	    return true;
	}

	public void Error()
	{
		
	}
	
	//��Toast��ʾ���������ʾ
	public void WrongMessage(String string)
	{
	    Toast toast = Toast.makeText(this, "Error: Operation failed. Please try again.", 1);
	    toast.setGravity(17, 0, 0);
	    toast.show();
	}
	//��Ч��BOX�ļ��б�
	public void BoxListAvailable(String[] ArrayOfString)
	{
	    int i = ArrayOfString.length;			//�ַ������ȣ������õ�BOX�ļ���
	}
	
	//��ʼ�������µ�����UI���
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
		
		this.m_Manager = new Fac_Manager(this);                      //ʵ��Fac_Manager�࣬�û������¼������쳣�˳�
		this.BTArrayAdapter = new ArrayAdapter(this, 17367043);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);         //��ʾ���������沼��
		
		init();        //����UI���
		//ִ�и�����ĵ���¼�
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
//				case R.id.menu_home:
//					GoHome();
//					break;
				case R.id.menu_connnection:
					Launch_Connection();
					break;
//				case R.id.menu_demo:
//					Launch_Demo();
//					break;
//				case R.id.menu_boxsettings:
//					Launch_BoxSettings();
//					break;
//				case R.id.menu_cansettings:
//					Launch_CanSettings();
//					break;
//				case R.id.menu_help:
//					Launch_Help();
//					break;
//				case R.id.menu_about:
//					Launch_About();
//					break;
//				case R.id.menu_logs:
//					Launch_Logs();
//					break;
				default:
					return super.onOptionsItemSelected(item);		//��û�д�����¼��������ദ��
				}
				return true;		//����true��ʾ������˵���ĵ���¼�������Ҫ���¼�����
				
	}
	
	/**
	 * ��������������
	 * @since   2016.5
	 * @exception  �����๦����ת��������
	 * */
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
 
	/**
	 * ϵͳ�ص�����
	 * ������MainActivity�������������෵�صĽ���������ڴ˷����д���
	 * @param	int requestCode  ������
	 * 			int resultCode   �����
	 * 			Intent data		������ͼ
	 * */
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
	//�Ի�����ʾ�û����������豸
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
