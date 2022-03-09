package com.info.interfaces

import android.net.Uri

interface ImageDownloadResponse{
    fun onImageDownload(uri:Uri)
}