package mobileMedical.FileManage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import mobileMedical.namespace.R;

import devFiles.FileManager.FileOperator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class ImageFilesOperator extends FileOperator {

    
	private final String filesPath = "/mobileMedical.namespace/files/images/headicons/";
	private String rootDirectory = "/data/data/mobileMedical.namespace/files/images/headicons/";
	private final String FILE_PATH = "/data/data/mobileMedical.namespace/files/images/headicons/";
	
	public ImageFilesOperator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	public Bitmap GetBitmapFromSDCardOrApp(boolean sdCard,String imageFileName)
	{
		  String filePath = null;
	        if (sdCard)
	        {
	           boolean sacardMounted = HasSDCard();
	           if(sacardMounted)
	           {
	           filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ filesPath;
	           }
	           else
	           {
	        	   return null;
	           }
	        }
	        else {
	        	filePath = rootDirectory;
			}
	        
	        File workingPath = new File(filePath);
	        //if this directory does not exists, make one.
	        if(!workingPath.exists())   
	        {   
	         return null;
	        }
	        
	        File outFile = new File(workingPath, imageFileName);   	                    
            InputStream in = null;
			try {
				in = new FileInputStream(outFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            Bitmap bitmap=BitmapFactory.decodeStream(in);
            return bitmap;

	        
	}
	
	public boolean SaveImageFilesToSDCardOrApp(boolean sdCard, Bitmap bitmapFile,String imageFileName, Bitmap.CompressFormat format)
	{
		
		
		             
	       
	        String filePath = null;
	        if (sdCard)
	        {
	           boolean sacardMounted = HasSDCard();
	           if(sacardMounted)
	           {
	           filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ filesPath;
	           }
	           else
	           {
	        	   return false;
	           }
	        }
	        else {
	        	filePath = rootDirectory;
			}
	        
	        File workingPath = new File(filePath);
	        //if this directory does not exists, make one.
	        if(!workingPath.exists())   
	        {   
	            if(!workingPath.mkdirs())   
	            {   
	                 
	            }   
	        }   
	        
	         String fileType = null;
	         String fileName =  imageFileName;
	          switch (format)
	          {
	          
	          case JPEG:
	        	  fileType = ".jpeg";
	        	  fileName += fileType;
	        	  break;
	          case PNG:
	        	  fileType = ".png";
	        	  fileName += fileType;
	        	  break;
	          default:
	        	  break;
	        	  
	          }
	          try   
	            { 
	                File outFile = new File(workingPath, fileName);   	                    
	                OutputStream out = new FileOutputStream(outFile);  
	                bitmapFile.compress(format, 100, out);  
	                out.close();
	            }
	           catch (FileNotFoundException e)   
	            {   
	                e.printStackTrace();  
	                return false;
	            }   
	            catch (IOException e)   
	            {   
	                e.printStackTrace(); 
	                return false;
	            }   
	           
	        return true;  
		}
}