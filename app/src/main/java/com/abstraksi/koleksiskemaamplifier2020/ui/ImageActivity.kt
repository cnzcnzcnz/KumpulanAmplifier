package com.abstraksi.koleksiskemaamplifier2020.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.abstraksi.koleksiskemaamplifier2020.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageActivity : AppCompatActivity() {

    lateinit var builder: AlertDialog.Builder
    lateinit var pDialog: ProgressDialog

    private var screenWidth = 0
    private var screenHeight = 0

    private lateinit var mInterstitialAd: InterstitialAd
    lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        pDialog = ProgressDialog(this)

        val metrics = DisplayMetrics()
        builder = AlertDialog.Builder(this)

        this.windowManager.defaultDisplay.getMetrics(metrics)

        MobileAds.initialize(this){}

        mAdView = findViewById(R.id.adView)
        val testDeviceId = ArrayList<String>()
        testDeviceId.add(AdRequest.DEVICE_ID_EMULATOR)




//        if(BuildConfig.DEBUG){
//            val requestConfig = RequestConfiguration.Builder()
//                .setTestDeviceIds(testDeviceId)
//                .build()
//            MobileAds.setRequestConfiguration(requestConfig)
//        }
//        else {
//
//        }

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels

        val imageLink = intent.getStringExtra("imageLink")

        Picasso.get().load(imageLink)
//            .placeholder(R.drawable.placeholder)
//            .error(R.drawable.placeholder)
            .into(iv_image_post)

        iv_btn_share.setOnClickListener {
            //            SetWallpaper().execute(imageLink)
            shareImage(imageLink!!)
        }
        iv_btn_download.setOnClickListener {
            SaveWallpaperAsync(this).execute(imageLink)
        }
    }

    private fun shareImage(imageLink: String) {
        pDialog.setTitle("Loading")
        pDialog.setMessage("Harap tunggu")
        pDialog.show()
        Picasso.get().load(imageLink).into(object: Target{
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap!!))
                }
                startActivity(Intent.createChooser(intent, "Bagikan gambar"))
            }
        })
    }

    private fun getLocalBitmapUri(bitmap: Bitmap): Uri {
        var bmpUri: Uri? = null
        try{
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_${System.currentTimeMillis()}.jpg")
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()
            bmpUri = FileProvider.getUriForFile(this, "com.abstraksi.peranggambar2020.FileProvider", file)
        }
        catch (e: Exception){
            Log.d("exception", e.message!!)
        }
        pDialog.dismiss()
        return bmpUri!!
    }

    inner class SaveWallpaperAsync(private val context: Context) :
        AsyncTask<String?, String?, String?>() {
        private var pDialog: ProgressDialog? = null
        var ImageUrl: URL? = null
        var bmImg: Bitmap? = null
        override fun onPreExecute() { // TODO Auto-generated method stub
            super.onPreExecute()
            pDialog = ProgressDialog(context)
            pDialog!!.setMessage("Sedang mengunduh gambar...")
            pDialog!!.isIndeterminate = false
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        }

        override fun doInBackground(vararg args: String?): String? { // TODO Auto-generated method stub
            var `is`: InputStream? = null
            try {
                ImageUrl = URL(args[0])
                val conn: HttpURLConnection = ImageUrl!!
                    .openConnection() as HttpURLConnection
                conn.setDoInput(true)
                conn.connect()
                `is` = conn.getInputStream()
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                bmImg = BitmapFactory.decodeStream(`is`, null, options)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                val path = ImageUrl!!.path
                val idStr = path.substring(path.lastIndexOf('/') + 1)
                val filepath = Environment.getExternalStorageDirectory()
                val dir = File(
                    filepath.absolutePath
                        .toString() + "/Wallpapers/"
                )
                dir.mkdirs()
                val file = File(dir, idStr)
                val fos = FileOutputStream(file)
                bmImg!!.compress(Bitmap.CompressFormat.JPEG, 75, fos)
                fos.flush()
                fos.close()
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.path),
                    arrayOf("image/jpeg"),
                    null
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: java.lang.Exception) {
                    }
                }
            }
            return null
        }

        override fun onPostExecute(args: String?) { // TODO Auto-generated method stub
            if (bmImg == null) {
                Toast.makeText(
                    context, "Image still loading...",
                    Toast.LENGTH_SHORT
                ).show()
                pDialog!!.dismiss()
            } else {
                if (pDialog != null) {
                    if (pDialog!!.isShowing) {
                        pDialog!!.dismiss()
                    }
                }
                builder.setTitle("Sukses")
                builder.setMessage("Gambar sudah terunduh")
                builder.setNegativeButton("OK", null)
                builder.show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            else ->  // If we got here, the user's action was not recognized.
// Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }
}
