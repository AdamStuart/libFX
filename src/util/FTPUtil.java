package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
//http://www.codejava.net/java-se/networking/ftp/how-to-upload-a-directory-to-a-ftp-server

public class FTPUtil
{
	static boolean SKIP_INVISIBLES = true;
	static boolean verbose = true;
	/**
	 * Upload a whole directory (including its nested sub directories and files)
	 * to a FTP server.
	 *
	 * @param ftpClient
	 *            an instance of org.apache.commons.net.ftp.FTPClient class.
	 * @param localParentDir
	 *            Path of the local directory being uploaded.
	 * @param remoteParentDir
	 *            Path of the parent directory of the current directory on the
	 *            server (used by recursive calls).
	 * @param remoteDirName
	 *            Path of the destination directory on the server.
	 * @throws IOException
	 *             if any network or IO error occurred.
	 */
	public static void uploadDirectory(FTPClient ftpClient, String localParentDir, String remoteParentDir, String remoteDirName)
	        throws IOException {
	 
    	if (verbose)
    		System.out.println(localParentDir + " --> " + remoteParentDir + "/" +  remoteDirName);
 	
	    File localDir = new File(localParentDir);
	    File[] subFiles = localDir.listFiles();
	    if (subFiles != null && subFiles.length > 0) 
	    {
	        for (File item : subFiles) 
	        {
               	String itemName = item.getName();
	        	if (SKIP_INVISIBLES && itemName.startsWith(".")) 		continue;			// skipping invisible files -- should be optional
	            String remoteFilePath = remoteParentDir +  "/" + remoteDirName  + "/" + itemName; 
//	            if (remoteParentDir.equals("")) 
//	                remoteFilePath = remoteParentDir + "/" + item.getName();   //??? don't understand this repetition
		 
//	 	       if (verbose)	 System.out.println("remoteFilePath: "  + remoteFilePath);
                if (item.isFile()) {
	                // upload the file
	                String localFilePath = item.getAbsolutePath();
	                if (verbose)	 System.out.println("About to upload the file: " + localFilePath);
	                boolean uploaded = uploadSingleFile(ftpClient, localFilePath, remoteFilePath);  //
            		String msg = (uploaded) ? "UPLOADED a file to: " : "COULD NOT upload the file: ";
            		String replyStr = ftpClient.getReplyString();
            		if (verbose)	 System.out.println(msg  + remoteFilePath + " " + replyStr);
	               
	            } else {
	                // create directory on the server
	            	boolean created = ftpClient.makeDirectory(remoteFilePath);
	            	if (verbose)
	            	{
	            		String msg = (created) ? "CREATED the directory: " : "COULD NOT create the directory: ";
	            		System.out.println(msg  + remoteFilePath);
	            	}

	                String remoteParent = remoteParentDir +  "/" + remoteDirName;
         	
	 
	                // upload the sub directory
//	                String parent = remoteParentDir + "/" + item.getName();
//	                if (remoteParentDir.equals("")) 
//	                    parent = item.getName();
	 
	                localParentDir = item.getAbsolutePath();
	               	if (verbose)
	               	{
	                
	               		System.out.println("localParentDir: "  + localParentDir);
	               		System.out.println("remoteParentDir: "  + remoteParent);
	               		System.out.println("remoteFilePath: "  + itemName);
	               	}
	                uploadDirectory(ftpClient, localParentDir, remoteParent, itemName);
	            }
	        }
	    }
	}
	/**
	 * Upload a single file to the FTP server.
	 *
	 * @param ftpClient
	 *            an instance of org.apache.commons.net.ftp.FTPClient class.
	 * @param localFilePath
	 *            Path of the file on local computer
	 * @param remoteFilePath
	 *            Path of the file on remote the server
	 * @return true if the file was uploaded successfully, false otherwise
	 * @throws IOException
	 *             if any network or IO error occurred.
	 */
	public static boolean uploadSingleFile(FTPClient ftpClient,
	        String localFilePath, String remoteFilePath) throws IOException {
	    File localFile = new File(localFilePath);
	 
	    InputStream inputStream = new FileInputStream(localFile);
	    try {
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        boolean ok = ftpClient.storeFile(remoteFilePath, inputStream);
	       if (verbose)
	       {
	    	   System.out.println(remoteFilePath);
		       System.out.println(ftpClient.getReplyString());
    	   	}
	        return ok;
	    } finally {
	        inputStream.close();
	    }
	}
	
//	
//	// Zip up the contents of a folder, and upload it as a single file
//	
//	public static void zipAndUploadDirectory(FTPClient ftpClient,
//			        String remoteDirPath, String localParentDir, String remoteParentDir)
//			        throws IOException {
//	    
//		File zipFile = FileUtil.compress(new File(localParentDir));
//		
//		uploadSingleFile(ftpClient, zipFile.getAbsolutePath(), remoteParentDir);
//		FileUtil.moveToTrash(zipFile);
//	}	
}
