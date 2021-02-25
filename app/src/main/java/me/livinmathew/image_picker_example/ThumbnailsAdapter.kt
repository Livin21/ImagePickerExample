package me.livinmathew.image_picker_example

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ThumbnailsAdapter(
    private val context: Activity,
    private val thumbnailList: MutableList<String>,
    private val onDelete: (index: Int) -> Unit
) : RecyclerView.Adapter<ThumbnailsAdapter.ViewHolder>() {

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val closeButton = rootView.findViewById<ImageView>(R.id.closeButton)
        val thumbnailImageView = rootView.findViewById<ImageView>(R.id.thumbnailImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = LayoutInflater.from(context).inflate(R.layout.thumbnail, null)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.closeButton.setOnClickListener {
            onDelete(position)
        }
        Glide
            .with(context)
            .load(thumbnailList[position])
            .into(holder.thumbnailImageView)
    }

    override fun getItemCount(): Int = thumbnailList.size

}
