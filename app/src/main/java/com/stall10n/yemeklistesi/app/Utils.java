package com.stall10n.yemeklistesi.app;

import java.io.File;

/**
 * Created by onur on 11.6.2014.
 */
public class Utils
{
    // Delete only the files in current directory not sublevels
    public static void DeleteAllFilesInDirectory(File directoryPath)
    {
        if (directoryPath.isDirectory())
        {
            String[] children = directoryPath.list();
            for (int i = 0; i < children.length; i++) {
                new File(directoryPath, children[i]).delete();
            }
        }
    }

    public static String GetDayUrl(Days day)
    {
        if(day ==  Days.Pazartesi)
            return "pazartesi.php";
        else if(day == Days.Sali)
            return "sali.php";
        else if(day == Days.Carsamba)
            return "carsamba.php";
        else if(day == Days.Persembe)
            return "persembe.php";
        else if(day == Days.Cuma)
            return "cuma.php";
        else if(day == Days.Cumartesi)
            return  "cumartesi.php";
        else if(day == Days.Pazar)
            return  "pazar.php";

        return "";
    }

}
