package com.example.singnature.WildlifeMenu

import android.content.Context
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import java.io.File
import java.lang.Exception

class SpacesUploader(context: Context) {
    private val SPACE_NAME = "image-database"  // 替换为你的 Spaces 名称
    private val REGION = "sgp1"  // 替换为你的 DigitalOcean 服务器区域
    private val ACCESS_KEY = "DO00FRF7V7HQZG6TKY6H"  // 替换为你的 Access Key
    private val SECRET_KEY = "cefVRIpN/hYfDdiwfbDzTxaVgbJsuZccwnTES7JLuIw"  // 替换为你的 Secret Key

    private val s3Client = AmazonS3Client(BasicAWSCredentials(ACCESS_KEY,SECRET_KEY))

    init {
        val endpoint = "https://$REGION.digitaloceanspaces.com"
        s3Client.setEndpoint(endpoint)
    }

    private val transferUtility:TransferUtility = TransferUtility.builder()
        .context(context)
        .s3Client(s3Client)
        .defaultBucket(SPACE_NAME)
        .build()

    fun uploadFile(file: File, fileName:String, callback: (String?)->Unit){
        val uploadObserver = transferUtility.upload(SPACE_NAME,fileName,file)

        uploadObserver.setTransferListener(object : TransferListener{
            override fun onStateChanged(id: Int, state: TransferState?) {
                if(state == TransferState.COMPLETED){
                    val imageUrl = "https://$SPACE_NAME.$REGION.digitaloceanspaces.com/$fileName"
                    callback(imageUrl)
                }else if(state == TransferState.FAILED){
                    callback(null)
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            }

            override fun onError(id: Int, ex: Exception?) {
                callback(null)
                if (ex != null) {
                    Log.e("SpaceUploader", "Upload error: ${ex.message}")
                }
            }
        })
    }
}