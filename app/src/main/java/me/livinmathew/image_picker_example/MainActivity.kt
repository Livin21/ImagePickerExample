package me.livinmathew.image_picker_example

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    val thumbnailList = mutableListOf<String>()
    private lateinit var thumbnailsRecyclerView: RecyclerView

    private val launcher = this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            ImagePicker.getFilePath(it.data)?.run {
                thumbnailList.add(this)
                thumbnailsRecyclerView.adapter?.notifyDataSetChanged()
            }
        } else if (it.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(it.data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.selectImageButton).setOnClickListener {
            //If you want both Camera and Gallery
            ImagePicker.with(this)
                .crop()
                .createIntentFromDialog { launcher.launch(it) }
        }

        findViewById<Button>(R.id.uploadButton).setOnClickListener {
            uploadImage()
        }

        thumbnailsRecyclerView = findViewById<RecyclerView>(R.id.thumbnailsRecyclerView)
        thumbnailsRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        thumbnailsRecyclerView.adapter = ThumbnailsAdapter(this, thumbnailList){
            thumbnailList.removeAt(it)
            thumbnailsRecyclerView.adapter?.notifyDataSetChanged()
        }

    }

    private fun watermarkImage(bitmap: Bitmap): Bitmap {
        val waterMark = (ResourcesCompat.getDrawable(
            applicationContext.resources,
            R.drawable.plotmall_logo,
            null
        ) as BitmapDrawable?)!!.bitmap
        val waterM = Bitmap.createScaledBitmap(waterMark, 200, 69, false)
        val watermarkedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(watermarkedBitmap)
        val matrix = Matrix()
        matrix.postTranslate(
            (bitmap.width - 230).toFloat(),
            (bitmap.height - 99).toFloat()
        )
        canvas.drawBitmap(waterM, matrix, null)
        canvas.save()
        return watermarkedBitmap
    }


    private fun uploadImage() {

        if (thumbnailList.isEmpty()){
            return Toast.makeText(this, "Please select atleast one image", Toast.LENGTH_LONG).show()
        }

        val progressDialog = ProgressDialog(this).apply {
            setMessage("Please wait...")
            setTitle("Uploading Images")
        }
        progressDialog.show()
        thumbnailList.forEach {

            // Generate bitmap
            val image = File(it)
            val bmOptions = BitmapFactory.Options()
            val bitmap = BitmapFactory.decodeFile(image.absolutePath, bmOptions)

            // Watermark it
            val watermarkedBitmap = watermarkImage(bitmap)

            // Generate stream
            val byteArrayOutputStream = ByteArrayOutputStream()
            watermarkedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageInByte = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.encodeToString(imageInByte, Base64.DEFAULT)
            val imgName = Calendar.getInstance().timeInMillis.toString()

            // Upload
            val apiInterface = RetroClient.getRetrofit().create(ApiInterface::class.java)
            val call = apiInterface.uploadIm(imgName, encodedImage)
            call.enqueue(object : Callback<ResponsePOJO?> {
                override fun onResponse(
                    call: Call<ResponsePOJO?>,
                    response: Response<ResponsePOJO?>
                ) {
                    progressDialog.dismiss()
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@MainActivity,
                            "Successful" + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Not Successful Response",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("code Response", response.code().toString())
                        val po = response.code().toString()
                        Log.d("RESPONSE", po)
                    }
                }

                override fun onFailure(call: Call<ResponsePOJO?>, t: Throwable) {
                    progressDialog.dismiss()
                    Log.d("onFailure logs", t.message!!)
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


}