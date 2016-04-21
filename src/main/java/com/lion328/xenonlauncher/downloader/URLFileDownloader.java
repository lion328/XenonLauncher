package com.lion328.xenonlauncher.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLFileDownloader implements FileDownloader
{

    public static final int BUFFER_SIZE = 8192;

    private final URL inputUrl;
    private final File targetFile;
    private final int bufferSize;
    private final List<FileDownloaderCallback> callbackList;

    private boolean running;
    private int percentage;
    private long size;
    private long downloaded;

    public URLFileDownloader(URL url, File file)
    {
        this(url, file, BUFFER_SIZE);
    }

    public URLFileDownloader(URL url, File file, int bufferSize)
    {
        inputUrl = url;
        targetFile = file;
        this.bufferSize = bufferSize;
        callbackList = new ArrayList<>();
        running = false;
    }

    @Override
    public synchronized void download() throws IOException
    {
        if (running)
        {
            return;
        }

        running = true;

        HttpURLConnection connection = (HttpURLConnection) inputUrl.openConnection();
        connection.setRequestMethod("GET");

        size = connection.getContentLengthLong();
        downloaded = 0;
        percentage = 0;

        OutputStream fileOut = new FileOutputStream(targetFile);

        byte[] buffer = new byte[bufferSize];
        int length;

        while (running && ((length = connection.getInputStream().read(buffer)) != -1))
        {
            fileOut.write(buffer, 0, length);
            downloaded += length;

            if ((size != -1) && (percentage < (percentage = (int) (downloaded * 100 / size))))
            {
                onPercentageChange(targetFile, percentage, size, downloaded);
            }
        }

        if (size == -1)
        {
            percentage = 100;
            downloaded = size;
            onPercentageChange(targetFile, percentage, size, size);
        }

        fileOut.close();

        running = false;
    }

    public URL getInputUrl()
    {
        return inputUrl;
    }

    @Override
    public void stop()
    {
        running = false;
    }

    @Override
    public boolean isRunning()
    {
        return running;
    }

    @Override
    public File getCurrentFile()
    {
        return targetFile;
    }

    @Override
    public int getOverallPercentage()
    {
        return percentage;
    }

    @Override
    public long getCurrentFileSize()
    {
        return size;
    }

    @Override
    public long getCurrentDownloadedSize()
    {
        return downloaded;
    }

    @Override
    public void registerCallback(FileDownloaderCallback callback)
    {
        callbackList.add(callback);
    }

    @Override
    public void removeCallback(FileDownloaderCallback callback)
    {
        callbackList.remove(callback);
    }

    private void onPercentageChange(File file, int percentage, long size, long downloaded)
    {
        for (FileDownloaderCallback callback : callbackList)
        {
            callback.onPercentageChange(file, percentage, size, downloaded);
        }
    }

    @Override
    public File getFile()
    {
        return targetFile;
    }
}