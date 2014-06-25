package com.stall10n.yemeklistesi.app;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by onur on 31.5.2014.
 */
public class Parser {

    private static final String url = "http://www.medyemek.com/menuler/";
    public static ArrayList<DailyMenu> menuList = new ArrayList<DailyMenu>();

    public static String GenerateFullUrl(Days day)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append(Utils.GetDayUrl(day));

        return sb.toString();
    }

    public static DailyMenu ParseDailyMenu(Days day)
    {
        DailyMenu menu = new DailyMenu();

        try
        {
            String fullUrl =  GenerateFullUrl(day);
            Document doc = Jsoup.connect(fullUrl).get();
            Elements spans = doc.body().select("span");

            String normal = spans.get(1).text();
            String diet = spans.get(3).text();

            menu.setDay(day.toString());
            menu.setStandard_menu(normal);
            menu.setDiet_menu(diet);

            if(day == Days.Cuma)
            {
                menu.setHash(Parser.MD5(normal));
            }

        }
        catch (Exception f)
        {
            return null;
        }

        return menu;
    }


    public static void initializeList(File file, ProgressDialog pg)
    {
        ParserTask parserTask = new ParserTask(file,pg);

        try
        {
           menuList = parserTask.execute(Days.values()).get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }

    }

    public static void SerializeObjects(List<DailyMenu> menu, String filePath)
    {
        ObjectOutputStream oos = null;

        try
        {
            oos = new ObjectOutputStream(new FileOutputStream(new File(filePath),true));
            for(DailyMenu content : menu)
            {
                oos.writeObject(content);
            }
            oos.flush();
            oos.close();
        }
        catch (Exception f)
        {
            Log.v("Serialization Save Error : ", f.getMessage());
            f.printStackTrace();
        }

    }

    public static ArrayList<DailyMenu> DeserializeObjects(String filePath)
    {
        try
        {
            ArrayList<DailyMenu> menuList = new ArrayList<DailyMenu>();

            FileInputStream inputStream = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(inputStream);

            while(inputStream.available() > 0)
            {
                DailyMenu menu = (DailyMenu)ois.readObject();
                if(menu == null)
                    break;

                menuList.add(menu);
            }

            ois.close();
            return menuList;
        }
        catch (Exception ex)
        {
            Log.v("Serialization Read Error : ",ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public static String MD5(String content) {
        try
        {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

class CheckerTask extends AsyncTask<Days, Void, Boolean>
{
    public Boolean doInBackground(Days... params)
    {
        DailyMenu menu = Parser.ParseDailyMenu(params[0]);
        String actualHash = "";
        String storedHash = Parser.menuList.get(5).getHash();

        if(menu != null)
            actualHash = menu.getHash();

        return !actualHash.equals(storedHash);

    }
}

class ParserTask extends AsyncTask<Days, Void, ArrayList<DailyMenu>> {

    private Exception exception;
    private File file;
    private ProgressDialog progressDialog;

    public ParserTask(File file, ProgressDialog pg)
    {
        this.file = file;
        this.progressDialog = pg;
    }

    protected ArrayList<DailyMenu> doInBackground(Days... days)
    {
        try
        {
            ArrayList<DailyMenu> menuList = new ArrayList<DailyMenu>();

            if(file.exists())
            {
                menuList = Parser.DeserializeObjects(file.getAbsolutePath());
            }
            else
            {
                String absolutePath = file.getAbsolutePath();
                String dirPath = absolutePath.
                        substring(0,absolutePath.lastIndexOf(File.separator));

                Utils.DeleteAllFilesInDirectory(new File(dirPath));

                for (Days day : days)
                    menuList.add(Parser.ParseDailyMenu(day));

                Parser.SerializeObjects(menuList, file.getAbsolutePath());
            }
            return menuList;
        }
        catch (Exception e)
        {
            this.exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<DailyMenu> dailyMenus) {
        super.onPostExecute(dailyMenus);
        //progressDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        progressDialog.show();
    }
}

