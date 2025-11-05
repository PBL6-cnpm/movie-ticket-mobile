package hoang.dqm.codebase.utils

//import com.braly.analytics.event.BralyTracking
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import hoang.dqm.codebase.R
import hoang.dqm.codebase.base.application.getBaseApplication
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun ImageView.loadImage(path: String) {
    val requestBuilder: RequestBuilder<Drawable> =
        Glide.with(this).asDrawable().sizeMultiplier(0.1f)
    Glide.with(this).load(File(path)).placeholder(R.drawable.bg_place_holder)
        .thumbnail(requestBuilder)
        .into(this)
}

fun ImageView.loadImageWithUri(uri: Uri) {
    val requestBuilder: RequestBuilder<Drawable> =
        Glide.with(this).asDrawable().sizeMultiplier(0.1f)
    Glide.with(this).load(uri)
        .thumbnail(requestBuilder)
        .into(this)
}

fun ImageView.loadImage(drawable: Int?) {
    val requestBuilder: RequestBuilder<Drawable> =
        Glide.with(this).asDrawable().sizeMultiplier(0.1f)
    Glide.with(this).load(drawable).thumbnail(requestBuilder)
        .into(this)
}

fun ImageView.loadImageBitmap(bitmap: Bitmap?, onResourceReady: (Bitmap) -> Unit) {
    val mBitmap = bitmap ?: return
    Glide.with(this).asBitmap().load(mBitmap)
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Bitmap,
                model: Any,
                target: Target<Bitmap>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                onResourceReady.invoke(resource)
                return false
            }

        }
        ).into(this)
}

fun ImageView.loadImageSketch(url: String) {
    val requestBuilder: RequestBuilder<Drawable> = Glide.with(this)
        .asDrawable()
        .sizeMultiplier(0.1f)
    val fullUrl = generateGlideUrl(url)
    Glide.with(this)
        .load(fullUrl)
        .placeholder(R.drawable.thumbnail)
        .thumbnail(requestBuilder)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.loadGifSketch(url: String) {
    val fullUrl = generateGlideUrl(url)

    val thumbnailRequest = Glide.with(this)
        .asGif()
        .sizeMultiplier(0.1f)
        .load(fullUrl)

    Glide.with(this)
        .asGif()
        .load(fullUrl)
        .placeholder(R.drawable.thumbnail)
        .thumbnail(thumbnailRequest)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}


fun ImageView.loadImageFullSketch(url: String) {
    val requestBuilder: RequestBuilder<Drawable> = Glide.with(this)
        .asDrawable()
//        .sizeMultiplier(0.1f)
    val fullUrl = generateGlideUrl(url)
    Glide.with(this)
        .load(fullUrl)
        .thumbnail(requestBuilder)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
//        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun preloadImage(
    url: String,
    onResourceReady: (Drawable) -> Unit,
    isFromRemote: Boolean
) {
    Glide.with(getBaseApplication())
        .load(if (isFromRemote) generateGlideUrl(url) else url)
        .centerCrop()
        .encodeFormat(Bitmap.CompressFormat.PNG)
        .encodeQuality(100)
        .skipMemoryCache(false)
        .into(object : CustomTarget<Drawable>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                onResourceReady.invoke(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) = Unit
        })
}

fun fetchImageFromGithub(context: Context, url: String, onResult: (Bitmap?) -> Unit) {
    val fullUrl = generateGlideUrl(url)
    Glide.with(context)
        .asBitmap()
        .load(fullUrl)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                onResult.invoke(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) = Unit

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                onResult.invoke(null)
            }
        })
}

suspend fun fetchImageFromGithubSuspend(context: Context, url: String): Bitmap? {
    return suspendCoroutine { continuation ->
        val fullUrl = generateGlideUrl(url)
        Glide.with(context)
            .asBitmap()
            .load(fullUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    continuation.resume(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    continuation.resume(null)
                }
            })
    }
}

fun ImageView.loadImageUrl(
    endPointUrl: String
) {
    val startTimeLoading = System.currentTimeMillis()
    Glide.with(this)
        .load(generateGlideUrl(endPointUrl))
        .placeholder(R.drawable.bg_place_holder)
        .timeout(20000)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable?>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                if (dataSource == DataSource.REMOTE) {
                    //BralyTracking.logEvent(this@loadImageUrl.context, IMAGE_LOADING_SUCCESS)
                }
                return false
            }
        })
        .into(this)
}

fun generateGlideUrl(endpoint: String): GlideUrl {
    return GlideUrl(endpoint)
}
