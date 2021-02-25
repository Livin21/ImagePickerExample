package me.livinmathew.image_picker_example

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        thumbnailsRecyclerView = findViewById<RecyclerView>(R.id.thumbnailsRecyclerView)
        thumbnailsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        thumbnailsRecyclerView.adapter = ThumbnailsAdapter(this, thumbnailList){
            thumbnailList.removeAt(it)
            thumbnailsRecyclerView.adapter?.notifyDataSetChanged()
        }

    }
}