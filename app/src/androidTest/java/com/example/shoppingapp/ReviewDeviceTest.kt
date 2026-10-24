package com.example.shoppingapp

import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.shoppingapp.ui.view.fragment.user.WriteReviewFragment
import com.example.shoppingapp.utils.StringConstants
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@RunWith(AndroidJUnit4::class)
class ReviewDeviceTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private val args get() = InstrumentationRegistry.getArguments()

    private fun host(productId: Int = 1, orderItemId: Int = 1, reviewId: Int = 0) =
        ActivityScenario.launch<ReviewTestHostActivity>(Intent(context, ReviewTestHostActivity::class.java).setAction(Intent.ACTION_MAIN)
            .putExtra("productId", productId).putExtra("orderItemId", orderItemId).putExtra("reviewId", reviewId))

    private fun sheet(activity: ReviewTestHostActivity): Fragment? = activity.supportFragmentManager
        .findFragmentByTag("review-test-parent")?.childFragmentManager?.fragments?.firstOrNull()

    private fun await(condition: () -> Boolean) {
        val end = System.currentTimeMillis() + 60000
        while (System.currentTimeMillis() < end) {
            instrumentation.waitForIdleSync()
            if (condition()) return
            Thread.sleep(100)
        }
        fail("Review UI did not reach expected state")
    }
    private fun chooseStar(fragment: Fragment, rating: Int) {
        val recycler = fragment.requireView().findViewById<RecyclerView>(R.id.recycler_view_star)
        val star = recycler.findViewHolderForAdapterPosition(rating - 1)?.itemView
        requireNotNull(star).findViewById<View>(R.id.img_star).performClick()
    }

    @Test fun draftSurvivesActivityRecreation() {
        host().use { scenario ->
            var ready = false
            await { scenario.onActivity { ready = sheet(it)?.view != null }; ready }
            scenario.onActivity { activity ->
                val fragment = requireNotNull(sheet(activity))
                fragment.requireView().findViewById<EditText>(R.id.edt_comment).setText("Draft before rotation")
                chooseStar(fragment, 4)
            }
            scenario.recreate()
            ready = false
            await { scenario.onActivity { ready = sheet(it)?.view != null }; ready }
            scenario.onActivity { activity ->
                val fragment = requireNotNull(sheet(activity))
                assertEquals("Draft before rotation",fragment.requireView().findViewById<EditText>(R.id.edt_comment).text.toString())
                assertTrue(fragment is WriteReviewFragment)
            }
        }
    }

    @Test fun realBackendCreateEditDeleteFromAndroid() {
        val token = args.getString("reviewToken").orEmpty()
        val api = args.getString("reviewApiUrl").orEmpty()
        val product = args.getString("reviewProductId")?.toIntOrNull() ?: 0
        val item = args.getString("reviewOrderItemId")?.toIntOrNull() ?: 0
        assumeTrue("Pass isolated backend fixture arguments to enable end-to-end device test",token.isNotBlank() && api.isNotBlank() && product > 0 && item > 0)
        context.getSharedPreferences("shopping_app",Context.MODE_PRIVATE).edit()
            .putString(StringConstants.TOKEN,token).commit()
        val withPhoto = args.getString("reviewWithPhoto") == "true"
        val photo = if (withPhoto) requireNotNull(com.example.shoppingapp.utils.ReviewImageUris.createCameraUri(context)) else null
        if (photo != null) {
            val bitmap = android.graphics.Bitmap.createBitmap(8,8,android.graphics.Bitmap.Config.ARGB_8888)
            val random = java.util.Random()
            for (x in 0 until 8) for (y in 0 until 8) bitmap.setPixel(x,y,android.graphics.Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256)))
            context.contentResolver.openOutputStream(photo)?.use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it) }
            bitmap.recycle()
        }
        val picker = if (photo != null) instrumentation.addMonitor(
            android.content.IntentFilter(Intent.ACTION_OPEN_DOCUMENT).apply { addDataType("*/*"); addCategory(Intent.CATEGORY_OPENABLE) },
            android.app.Instrumentation.ActivityResult(android.app.Activity.RESULT_OK,Intent().setData(photo)),true) else null
        val comment="Android review "+System.currentTimeMillis()
        try {
        host(product,item).use { scenario ->
            var ready=false
            await { scenario.onActivity { ready=sheet(it)?.view!=null };ready }
            scenario.onActivity { activity ->
                val fragment=requireNotNull(sheet(activity))
                fragment.requireView().findViewById<EditText>(R.id.edt_comment).setText(comment)
                chooseStar(fragment,4)
                if (withPhoto) fragment.requireView().findViewById<View>(R.id.img_gallery).performClick()
            }
            if (withPhoto) {
                await {
                    scenario.onActivity { activity -> ready=sheet(activity)?.view?.findViewById<RecyclerView>(R.id.recycler_view_image)?.adapter?.itemCount==1 }
                    ready
                }
            }
            scenario.recreate()
            ready=false
            await { scenario.onActivity { ready=sheet(it)?.view!=null };ready }
            scenario.onActivity { activity -> requireNotNull(sheet(activity)).requireView().findViewById<View>(R.id.btn_post_comment).performClick() }
            await { scenario.onActivity { ready=sheet(it)==null };ready }
        }
        } finally {
            if (picker != null) instrumentation.removeMonitor(picker)
            if (photo != null) com.example.shoppingapp.utils.ReviewImageUris.discardCameraUri(context, photo)
        }
        fun get(path:String): Pair<Int,String> {
            val connection=URL(api+path).openConnection() as HttpURLConnection
            connection.setRequestProperty("Authorization","Bearer "+token)
            return try { connection.responseCode to (if(connection.responseCode<400) connection.inputStream else connection.errorStream).bufferedReader().use { it.readText() } }
            finally { connection.disconnect() }
        }
        val page=JSONObject(get("reviews/product/$product?pageNumber=0").second).getJSONArray("contents")
        val created=(0 until page.length()).map { page.getJSONObject(it) }.first { it.optString("comment")==comment }
        assertEquals(4,created.getInt("rating")); val reviewId=created.getInt("reviewId")
        if (withPhoto) {
            val images=created.getJSONArray("images")
            assertEquals(1,images.length())
            val imageUrl=images.getJSONObject(0).getString("imageUrl")
            assertTrue(imageUrl.startsWith("https://res.cloudinary.com/"))
            val connection=URL(imageUrl).openConnection() as HttpURLConnection
            try { assertEquals(200,connection.responseCode) } finally { connection.disconnect() }
            java.io.File(context.cacheDir,"review-cloudinary-device.json").writeText(JSONObject().put("imageUrl",imageUrl).toString())
        }
        host(reviewId=reviewId).use { scenario ->
            var loaded=false
            await { scenario.onActivity { loaded=sheet(it)?.view?.findViewById<EditText>(R.id.edt_review)?.text?.toString()==comment };loaded }
            scenario.onActivity { activity ->
                val fragment=requireNotNull(sheet(activity))
                fragment.requireView().findViewById<EditText>(R.id.edt_review).setText(comment+" edited")
                chooseStar(fragment,2)
                fragment.requireView().findViewById<View>(R.id.btn_edit_review).performClick()
            }
            await { scenario.onActivity { loaded=sheet(it)==null };loaded }
        }
        assertEquals(2,JSONObject(get("reviews/$reviewId").second).getInt("rating"))
        host(reviewId=reviewId).use { scenario ->
            var loaded=false
            await { scenario.onActivity { loaded=sheet(it)?.view?.findViewById<EditText>(R.id.edt_review)?.text?.toString()==comment+" edited" };loaded }
            androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withId(R.id.tv_delete))
                .perform(androidx.test.espresso.action.ViewActions.click())
            androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withId(android.R.id.button1))
                .perform(androidx.test.espresso.action.ViewActions.click())
            await { scenario.onActivity { loaded=sheet(it)==null };loaded }
        }
        assertEquals(404,get("reviews/$reviewId").first)
    }
    @Test fun gallerySelectionAndCameraCancellationSurviveRecreation() {
        val uri = requireNotNull(com.example.shoppingapp.utils.ReviewImageUris.createCameraUri(context))
        val bitmap = android.graphics.Bitmap.createBitmap(2,2,android.graphics.Bitmap.Config.ARGB_8888)
        context.contentResolver.openOutputStream(uri)?.use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it) }
        bitmap.recycle()
        val filter = android.content.IntentFilter(Intent.ACTION_OPEN_DOCUMENT).apply { addDataType("*/*"); addCategory(Intent.CATEGORY_OPENABLE) }
        val gallery = instrumentation.addMonitor(filter,
            android.app.Instrumentation.ActivityResult(android.app.Activity.RESULT_OK,Intent().setData(uri)),true)
        val camera = instrumentation.addMonitor(android.content.IntentFilter(android.provider.MediaStore.ACTION_IMAGE_CAPTURE),
            android.app.Instrumentation.ActivityResult(android.app.Activity.RESULT_CANCELED,null),true)
        try {
            host().use { scenario ->
                var ready=false
                await { scenario.onActivity { ready=sheet(it)?.view!=null };ready }
                scenario.onActivity { activity ->
                    requireNotNull(sheet(activity)).requireView().findViewById<View>(R.id.img_gallery).performClick()
                }
                fun imageCount(): Int {
                    var count=0
                    scenario.onActivity { activity ->
                        count=requireNotNull(sheet(activity)).requireView().findViewById<RecyclerView>(R.id.recycler_view_image).adapter?.itemCount ?: 0
                    }
                    return count
                }
                await { imageCount()==1 }
                scenario.onActivity { activity ->
                    requireNotNull(sheet(activity)).requireView().findViewById<View>(R.id.img_take_photo).performClick()
                }
                await { camera.hits>0 }
                assertEquals(1,imageCount())
                scenario.recreate()
                await { imageCount()==1 }
                scenario.onActivity { activity ->
                    val fragment=requireNotNull(sheet(activity))
                    fragment.requireView().findViewById<EditText>(R.id.edt_comment).setText("Selected image draft")
                    chooseStar(fragment,5)
                }
                scenario.onActivity { activity ->
                    val dialog = sheet(activity) as androidx.fragment.app.DialogFragment
                    val decor = requireNotNull(dialog.dialog?.window?.decorView)
                    val screenshot = android.graphics.Bitmap.createBitmap(decor.width,decor.height,android.graphics.Bitmap.Config.ARGB_8888)
                    decor.draw(android.graphics.Canvas(screenshot))
                    java.io.File(context.cacheDir,"review-draft-device.png").outputStream().use {
                        screenshot.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)
                    }
                    screenshot.recycle()
                }
            }
        } finally {
            instrumentation.removeMonitor(gallery);instrumentation.removeMonitor(camera)
            com.example.shoppingapp.utils.ReviewImageUris.discardCameraUri(context,uri)
        }
    }

    @Test fun networkFailureKeepsDraftAndReenablesSubmit() {
        assumeTrue(args.getString("reviewOffline")=="true")
        val token=args.getString("reviewToken").orEmpty()
        context.getSharedPreferences("shopping_app",Context.MODE_PRIVATE).edit().putString(StringConstants.TOKEN,token).commit()
        host(requireNotNull(args.getString("reviewProductId")).toInt(),requireNotNull(args.getString("reviewOrderItemId")).toInt()).use { scenario ->
            var ready=false
            await { scenario.onActivity { ready=sheet(it)?.view!=null };ready }
            scenario.onActivity { activity ->
                val fragment=requireNotNull(sheet(activity))
                fragment.requireView().findViewById<EditText>(R.id.edt_comment).setText("Keep draft when offline")
                chooseStar(fragment,3)
                fragment.requireView().findViewById<View>(R.id.btn_post_comment).performClick()
            }
            await {
                scenario.onActivity { activity -> ready=sheet(activity)?.view?.findViewById<View>(R.id.btn_post_comment)?.isEnabled==true }
                ready
            }
            scenario.onActivity { activity ->
                val fragment=requireNotNull(sheet(activity))
                assertEquals("Keep draft when offline",fragment.requireView().findViewById<EditText>(R.id.edt_comment).text.toString())
            }
        }
    }

    @Test fun imageValidationRejectsCorruptAndOversizedData() {
        val uri = requireNotNull(com.example.shoppingapp.utils.ReviewImageUris.createCameraUri(context))
        val reader = com.example.shoppingapp.data.source.review.ReviewImageReader(context)
        try {
            context.contentResolver.openOutputStream(uri)?.use { it.write(byteArrayOf(1,2,3)) }
            assertThrows(IllegalArgumentException::class.java) { reader.read(uri) }
            context.contentResolver.openOutputStream(uri)?.use { it.write(ByteArray(StringConstants.REVIEW_MAX_IMAGE_BYTES + 1)) }
            assertThrows(IllegalArgumentException::class.java) { reader.read(uri) }
        } finally { com.example.shoppingapp.utils.ReviewImageUris.discardCameraUri(context,uri) }
    }

    @Test fun directPhotoUploadFailsWithinDeadlineWhenOffline() {
        assumeTrue(args.getString("reviewUploadOffline") == "true")
        val uri = requireNotNull(com.example.shoppingapp.utils.ReviewImageUris.createCameraUri(context))
        val bitmap = android.graphics.Bitmap.createBitmap(2,2,android.graphics.Bitmap.Config.ARGB_8888)
        context.contentResolver.openOutputStream(uri)?.use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it) }
        bitmap.recycle()
        try {
            val uploader = com.example.shoppingapp.data.source.cloudinary.impl.CloudinaryImageReviewUploader(context,
                com.example.shoppingapp.data.source.review.ReviewImageReader(context))
            kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    kotlinx.coroutines.withTimeout(StringConstants.REVIEW_UPLOAD_TIMEOUT_MS + 10000L) { uploader.upload(uri) }
                    fail("An offline photo upload should fail and allow an explicit retry")
                } catch (expected: java.io.IOException) {
                    assertTrue(expected.message.orEmpty().isNotBlank())
                }
            }
        } finally { com.example.shoppingapp.utils.ReviewImageUris.discardCameraUri(context,uri) }
    }

}
