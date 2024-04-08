import android.annotation.*
import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import coil.*
import coil.compose.*
import coil.decode.*
import coil.disk.*
import coil.memory.*

@Composable
fun rememberSvgPainter(
    model: Any?,
    imageLoader: ImageLoader = rememberImageLoader(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = model,
        imageLoader = imageLoader,
        transform = transform,
        onState = onState,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

@Composable
fun rememberAssetsPainter(
    path: String,
    imageLoader: ImageLoader = rememberImageLoader(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = "file:///android_asset/${path}",
        imageLoader = imageLoader,
        transform = transform,
        onState = onState,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

@Composable
fun SvgIcon(
    path: String,
    contentDescription: String?,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.size(18.dp),
    tint: Color = LocalContentColor.current,
) {
    Icon(painter = rememberAssetsPainter(path = path), contentDescription, modifier, tint)
}

@Composable
fun SvgIcon(
    painter: Painter,
    contentDescription: String?,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.size(18.dp),
    tint: Color = LocalContentColor.current,
) {
    Icon(painter = painter, contentDescription, modifier, tint)
}

@Composable
private fun rememberImageLoader(context: Context = LocalContext.current): ImageLoader {
    val loader = remember {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
    return loader

}