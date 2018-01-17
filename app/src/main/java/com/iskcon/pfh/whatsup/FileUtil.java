package com.iskcon.pfh.whatsup;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by i308830 on 12/25/17.
 */


public class FileUtil {
    public static void writeConfiguration(Context ctx, String s ) {
        try (FileOutputStream openFileOutput =
                     ctx.openFileOutput( "document_urls.txt", Context.MODE_APPEND|Context.MODE_PRIVATE);) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput));
            bw.write(s);
            bw.newLine();
            bw.close();
            openFileOutput.close();
        } catch (Exception e) {
            // not handled
        }
    }

    public static String[] readFileFromInternalStorage(Context ctx,String fileName) {
        String eol = System.getProperty("line.separator");
        String[] res = {" "," "," "," "," "," "," "," "," "," "};
        try (BufferedReader input = new BufferedReader(new InputStreamReader(
                ctx.openFileInput(fileName))); ){
            String line;
            int i=0;
            StringBuffer buffer = new StringBuffer();
            while ((line = input.readLine()) != null) {
                Log.d("info","FileContent:"+line);
                buffer.append(line + eol);
                res[i]=line;
                i++;
            }
        } catch (Exception e) {
            // we do not care
        }
        Log.d("info","Strings:"+res);
        return res;
    }
}