package mobileMedical.namespace;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import mobileMedical.Contacts.Manager.ContactManager;
import mobileMedical.Contacts.Manager.EmailManager;
import mobileMedical.Contacts.Manager.IMManager;
import mobileMedical.Contacts.Manager.TelManager;
import mobileMedical.Contacts.Model.Contact;
import mobileMedical.database.DBManager;

public class fileTransmission extends Activity {
    private DBManager dbManager;
    private ContactManager contactMgr;
    private EmailManager emailMgr;
    private TelManager telMgr;
    private EditText ipet;
    private Button FReceiveButton;
    private String ipstr;
    private Button FSendButton;
    private Button resuleDisplay4ECG;
    private Button resultDisplay4bloodPressure;
    private Button resultDisplay4bloodOx;

    boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_transmission);
        setTitle("上传与下载");
        //设置ipet与edittext相关联
        ipet=(EditText)findViewById(R.id.ipet);
        //此处为服务器ip
        ipet.setText("172.20.195.1");
//        ipet.setText("10.108.168.155");
        //--------------文件接收到手机-------------------//
        FReceiveButton=(Button)findViewById(R.id.FReceive);
        //添加receiveButton单击监听
        FReceiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将ipstr设置为服务器ip
                ipstr=ipet.getText().toString();
                //新线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketChannel socketChannel=null;
                        try{
                            socketChannel=SocketChannel.open();
                            //建立socket连接，服务器地址为ipstr，端口为1991
                            SocketAddress socketAddress = new InetSocketAddress(ipstr, 1900);
                            socketChannel.connect(socketAddress);
                            //手机接收文件，存储位置为data/data/com.example.practice.filetransferbetweenphonepc，
                            //文件名为123.txt
                            receiveFile(socketChannel, new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/mobileMedical.namespace/files/y0.txt"));
                        }catch (Exception ex){
                            Log.i("FReceiveERROR", null, ex);
                        }
                    }
                }).start();

                //新线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketChannel socketChannel=null;
                        try{
                            socketChannel=SocketChannel.open();
                            //建立socket连接，服务器地址为ipstr，端口为1991
                            SocketAddress socketAddress = new InetSocketAddress(ipstr, 1901);
                            socketChannel.connect(socketAddress);
                            //手机接收文件，存储位置为data/data/com.example.practice.filetransferbetweenphonepc，
                            //文件名为123.txt
                            receiveFile(socketChannel, new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/mobileMedical.namespace/files/y1.txt"));
                        }catch (Exception ex){
                            Log.i("FReceiveERROR", null, ex);
                        }
                    }
                }).start();

                //新线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketChannel socketChannel=null;
                        try{
                            socketChannel=SocketChannel.open();
                            //建立socket连接，服务器地址为ipstr，端口为1991
                            SocketAddress socketAddress = new InetSocketAddress(ipstr, 1902);
                            socketChannel.connect(socketAddress);
                            //手机接收文件，存储位置为data/data/com.example.practice.filetransferbetweenphonepc，
                            //文件名为123.txt
                            receiveFile(socketChannel, new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/mobileMedical.namespace/files/y2.txt"));
                        }catch (Exception ex){
                            Log.i("FReceiveERROR", null, ex);
                        }
                    }
                }).start();

                //新线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketChannel socketChannel=null;
                        try{
                            socketChannel=SocketChannel.open();
                            //建立socket连接，服务器地址为ipstr，端口为1991
                            SocketAddress socketAddress = new InetSocketAddress(ipstr, 1903);
                            socketChannel.connect(socketAddress);
                            //手机接收文件，存储位置为data/data/com.example.practice.filetransferbetweenphonepc，
                            //文件名为123.txt
                            receiveFile(socketChannel, new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/mobileMedical.namespace/files/y3.txt"));
                        }catch (Exception ex){
                            Log.i("FReceiveERROR", null, ex);
                        }
                    }
                }).start();
            }
        });
        //------------------文件发送到电脑---------------------//
        FSendButton=(Button)findViewById(R.id.FSend);
        //添加sendButton单击监听
        FSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将ipstr设置为服务器ip
                ipstr=ipet.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SocketChannel socketChannel=null;
                        try {
                            // send measdatainfo.txt
                            socketChannel=SocketChannel.open();
                            //建立socket连接，服务器地址为ipstr，端口为1991
                            SocketAddress socketAddress = new InetSocketAddress(ipstr, 1991);
                            socketChannel.connect(socketAddress);
                            //手机发送文件，文件位置为data/data/com.example.practice.filetransferbetweenphonepc，
                            sendFile(socketChannel, new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/mobileMedical.namespace/files/measdatainfo.txt"));

                            //send measdata.txt
                            socketChannel=SocketChannel.open();
                            //建立socket连接，服务器地址为ipstr，端口为1991
                            socketChannel.connect(socketAddress);
                            //send measdata.txt
                            sendFile(socketChannel, new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/mobileMedical.namespace/files/measdata.txt"));

                            // send patientAndDoctor.txt
                            // read patient and doctor info write to file then send to server
                            readAndWritePaientAndDoctorInfo();
                            socketChannel=SocketChannel.open();
                            //建立socket连接，服务器地址为ipstr，端口为1991
                            socketChannel.connect(socketAddress);
                            sendFile(socketChannel, new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/mobileMedical.namespace/files/patientAndDoctor.txt"));


                        }catch (Exception ex){
                            Log.i("FSendERROR",null,ex);
                        }
                    }
                }).start();
            }
        });

        resuleDisplay4ECG =(Button)findViewById(R.id.resuleDisplay4ecg);
        resuleDisplay4ECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(fileTransmission.this,aiChartsDisplay4ECG.class);
                startActivity(intent);
            }
        });

        resultDisplay4bloodPressure = (Button)findViewById(R.id.resuleDisplay4bloodPressure);
        resultDisplay4bloodPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(fileTransmission.this,aiChartsDisplay4bloodPressure.class);
                startActivity(intent);
            }
        });
        resultDisplay4bloodOx = (Button)findViewById(R.id.resuleDisplay4bloodOx);
        resultDisplay4bloodOx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(fileTransmission.this,aiChartsDisplay4bloodOx.class);
                startActivity(intent);
            }
        });
    }

    private static void sendFile(SocketChannel socketChannel, File file) throws IOException {
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int size = 0;
            while ((size = channel.read(buffer)) != -1) {
                buffer.rewind();
                buffer.limit(size);
                socketChannel.write(buffer);
                buffer.clear();
            }
            socketChannel.socket().shutdownOutput();
        } finally {
            try {
                channel.close();
            } catch(Exception ex) {}
            try {
                fis.close();
            } catch(Exception ex) {}
        }
    }

    private static void receiveFile(SocketChannel socketChannel, File file) throws IOException {
        FileOutputStream fos = null;
        FileChannel channel = null;

        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

            int size = 0;
            while ((size = socketChannel.read(buffer)) != -1) {
                buffer.flip();
                if (size > 0) {
                    buffer.limit(size);
                    channel.write(buffer);
                    buffer.clear();
                }
            }
        } finally {
            try {
                channel.close();
            } catch(Exception ex) {}
            try {
                fos.close();
            } catch(Exception ex) {}
        }
    }


//    public synchronized void onResume() {
//        super.onResume();
//        Cursor cursor = dbManager.queryDoctorInfo(MemberManage.doctorID);
//        cursor.moveToNext();
//        setTitle(cursor.getString(cursor
//                .getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME)) + "医生");
//        cursor.close();
//        dbManager.closeDB();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_transmission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void readAndWritePaientAndDoctorInfo()
    {
        String splitNoteString = ",";
        // read doctor info and write to file
        dbManager = new DBManager(fileTransmission.this);
        Cursor cursor = dbManager.queryDoctorInfo(MemberManage.doctorID);
        String username = "", doctorid = "", work = "", room = "", level = "", tel = "", email = "";

        if (cursor.moveToNext()) {
            username = cursor.getString(cursor
                    .getColumnIndex(ConstDef.DATABASE_FIELD_USERNAME));
            doctorid = cursor.getString(cursor
                    .getColumnIndex(ConstDef.DATABASE_FIELD_ID));
            work = cursor.getString(cursor
                    .getColumnIndex(ConstDef.DATABASE_FIELD_WORK));
            room = cursor.getString(cursor
                    .getColumnIndex(ConstDef.DATABASE_FIELD_ROOM));
            level = cursor.getString(cursor
                    .getColumnIndex(ConstDef.DATABASE_FIELD_LEVEL));
            tel = cursor.getString(cursor
                    .getColumnIndex(ConstDef.DATABASE_FIELD_TEL));
            email = cursor.getString(cursor
                    .getColumnIndex(ConstDef.DATABASE_FIELD_EMAIL));
        }
        cursor.close();
        dbManager.closeDB();

        // 读取病人信息
        contactMgr = new ContactManager(this);
        telMgr = new TelManager(this);
        emailMgr = new EmailManager(this);
        int patientid = contactMgr.getAllContacts().get(0)
                .getId();
        Contact contact = contactMgr.getContactById(patientid);
        String name = contact.getName();
        String birthday = contact.getBirthday();
        String address = contact.getAddress();
        String idNo = contact.getIdNo();
        String firstTelNumbertel = "";
        String firstEmailName = "";

        if (!telMgr.getTelNumbersByContactId(patientid).isEmpty()) {
            firstTelNumbertel = telMgr.getTelNumbersByContactId(patientid).get(0);
        }
        if (!emailMgr.getEmailsByContactId(patientid).isEmpty()) {
            firstEmailName = emailMgr.getEmailsByContactId(patientid).get(0)
                    .getEmailAcount();
        }
        String relationName = contact.getRelationName();
        String relation = contact.getRelation();
        String relationTele = contact.getRelationTele();
        String relationEmail = contact.getRelationEmail();


        // 写入文件
        String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/mobileMedical.namespace/files/";

        File workingPath = new File(filePath);
        // if this directory does not exists, make one.
        if (!workingPath.exists()) {
            if (!workingPath.mkdirs()) {
            }
        }
        File fileWrite = new File( filePath + "patientAndDoctor.txt");
        try {
            fileWrite.createNewFile();
            FileOutputStream fos =  new FileOutputStream(fileWrite);
            // 插入医生信息
            String doctorInfo = "1" + splitNoteString + doctorid +
                    splitNoteString + username +
                    splitNoteString + work +
                    splitNoteString + room +
                    splitNoteString + level +
                    splitNoteString + tel +
                    splitNoteString + email;
            fos.write(doctorInfo.getBytes());
            fos.write("\r\n".getBytes());

            // 插入病人信息
            String patientInfo = "2" + splitNoteString + patientid +
                    splitNoteString + name +
                    splitNoteString + birthday +
                    splitNoteString + idNo +
                    splitNoteString + address +
                    splitNoteString + firstTelNumbertel +
                    splitNoteString + firstEmailName +
                    splitNoteString + relationName +
                    splitNoteString + relation +
                    splitNoteString + relationTele+
                    splitNoteString + relationEmail;
            fos.write(patientInfo.getBytes());
            fos.write("\r\n".getBytes());
            fos.close();
        }
        catch(Exception e){
        }

    }
}
