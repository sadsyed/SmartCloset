package ssar.smartcloset;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Amy on 11/9/2014.
 */
public class SmartClosetFileService {
    public static final String CLASSNAME = SmartClosetFileService.class.getSimpleName();

        /* Checks if external storage is available for read and write */
        public static boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }

        /* Checks if external storage is available to at least read */
        public static boolean isExternalStorageReadable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) ||
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return true;
            }
            return false;
        }

        public static File getDataStorageDir(String dataName) {
            // Get the directory for the user's public documents directory.
            File path = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), dataName);
            if (!path.mkdirs()) {
                Log.i(CLASSNAME, "Error creating file path for data storage directory");
            }

            return path;
        }
        public static String getRealPathFromURI(Uri contentUri, Context context) {
            File tempFile = new File(context.getFilesDir().getAbsolutePath(), "temp_image");
            //Copy Uri contents into temp File.
            try {
                tempFile.createNewFile();
                copyAndClose(context.getContentResolver().openInputStream(contentUri),new FileOutputStream(tempFile));
            } catch (IOException e) {
                //Log Error
            }
            //Now fetch the new URI
            Uri newUri = Uri.fromFile(tempFile);
            return newUri.getPath();
        }

        public static void copyAndClose(InputStream is, FileOutputStream fos) {
            try {
                byte[] buffer = new byte[1024];
                int bytesRead;
                //read from is to buffer
                while((bytesRead = is.read(buffer)) !=-1){
                    fos.write(buffer, 0, bytesRead);
                }
                is.close();
                //flush OutputStream to write any buffered data to file
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

