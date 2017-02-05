/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Administrator
 */
public class Zipping
{
    
    public String createZip(String sourceFile,String destinationFolder)
    {
        String zipFile="";

        try
        {
            File src = new File (sourceFile);

            if (src.isDirectory())
            {                
                File forDestFolder = new File(destinationFolder);

                if(forDestFolder.exists()==false)
                    forDestFolder.mkdir();

                zipFile = destinationFolder+File.separatorChar+new File(sourceFile).getName() + ".zip";
                zipFolder (sourceFile, zipFile);
            }

        }
        catch(Exception ex)
        {
            System.out.println("Error creating zip file : "+ex);
        }

        return zipFile;
    }

    private void zipFolder(String srcFolder, String destZipFile) throws Exception
    {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;


        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);


        addFileToZip("", srcFolder, zip);

        //Mihir
            //fileWriter.flush();
            //fileWriter.close();
        //Mihir
        zip.flush();
        zip.close();
   }

    public static void delete(File file) throws IOException {

        if (file.isDirectory()) {

            //directory is empty, then delete it
            if (file.list().length == 0) {

                file.delete();

            } else {

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }

        } else {
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }
    
    private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception
    {

        File folder = new File(srcFile);

        if (folder.isDirectory())
        {
            addFolderToZip(path, srcFile, zip);
        }
        else
        {
             byte[] buf = new byte[1024];
             int len;
             FileInputStream in = new FileInputStream(srcFile);
             zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));

             while ((len = in.read(buf)) > 0)
             {
                   zip.write(buf, 0, len);
             }
             //Mihir
             in.close();
             //Mihir
        }
      }

    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception
    {

           File folder = new File(srcFolder);
           String[] fileName = folder.list();

           for (int i = 0; i < fileName.length; i++)
           {

               if (path.equals(""))
               {
                  addFileToZip(folder.getName(), srcFolder + "/" + fileName[i], zip);
               }
               else
               {
                    addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName[i], zip);
               }
           }
    }


    
    
    public void unZip(String sourceFile,String destinationFolder)
    {
        try
        {
            File file = new File(sourceFile);
            FileInputStream zis = new FileInputStream(file);
            //String dir = file.getParent();
            String dir = destinationFolder;
            dir = dir.replace('\\', '/');
            storeZipStream(zis, dir);
            zis.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error at unzip : "+ex);
        }
    }

    private void storeZipStream(InputStream inputStream, String dir) throws IOException
    {

        ZipInputStream zis = new ZipInputStream(inputStream);
        ZipEntry entry = null;
        int countEntry = 0;
        if (!dir.endsWith(File.separator))
        dir += File.separator;

        // check inputStream is ZIP or not
        if ((entry = zis.getNextEntry()) != null) {
        do {
        String entryName = entry.getName();
        // Directory Entry should end with FileSeparator
        if (!entry.isDirectory()) {
        // Directory will be created while creating file with in it.
        String fileName = dir + entryName;
        createFile(zis, fileName);
        countEntry++;
        //zis.close();
        }
        } while ((entry = zis.getNextEntry()) != null);
        System.out.println("No of files Extracted : " + countEntry);

        } else {
        throw new IOException("Given file is not a Compressed one");
        }

        zis.close();

    }


    private void createFile(InputStream is, String absoluteFileName) throws IOException
    {
        File f = new File(absoluteFileName);

        if (!f.getParentFile().exists())
        f.getParentFile().mkdirs();
        OutputStream out = new FileOutputStream(absoluteFileName);
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = is.read(buf)) > 0) {
        out.write(buf, 0, len);
        }
        // Close the streams
        out.close();
    }





}
