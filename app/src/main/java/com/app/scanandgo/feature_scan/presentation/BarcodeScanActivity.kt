/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.scanandgo.feature_scan.presentation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.app.scanandgo.R
import com.app.scanandgo.core.LogUtils
import com.app.scanandgo.core.ToastUtils
import com.app.scanandgo.core.observeOnce
import com.app.scanandgo.databinding.ActivityScannerBinding
import com.app.scanandgo.feature_cart.presentation.CartActivity
import com.app.scanandgo.feature_scan.data.ProductDto
import com.app.scanandgo.feature_scan.domain.BarcodeScanViewModel
import com.app.scanandgo.network.NetworkResult
import com.scandit.datacapture.barcode.capture.BarcodeCapture
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.barcode.data.SymbologyDescription
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlayStyle
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.core.ui.style.Brush
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BarcodeScanActivity : CameraPermissionActivity(), BarcodeCaptureListener {

    private lateinit var binding: ActivityScannerBinding
    private val viewModel: BarcodeScanViewModel by viewModels()

    companion object {
        const val SCANDIT_LICENSE_KEY =
            "ASUh+C9UDbHoDPieHDH34qkryRvAL5DU2zTghCUKh/lWXeMaeXjHMdVUUHzlTi/NuFvFLXhWJhSQWFb7zg6dXdZU2+a6b+0oNhFr9GdPGWEnRRq+bmEn/nY9XyHlJ5WPKgm6pG8nj82EZbvY600ttWprBn3ophqmk9gK7ql5ITG3v3RRLbbexova2DNNr/1sLPkgyOHDHDYWJrC51TUnpI7lYsdacbcprvCg0BsHIOJUxZN7dleHUQ3N3e0U5R9+w5f4AsKaJIzW1hYqJdFetb8JUPrGSrl23BqexWpegkdribIt9zRuEaNSdinIWB6GAv1ganFVoz52cFYrqymK1xikFJztmO94FYfc2miqe3O36YvyALDvfW5warkqjjC7mURzMYI3YW03yIvcZIb1pUFl8L1gKKnLG43x7vri+ZNvg8JZjAwNbCdTmYl+UhCg078yyY0iISdCPd95jmM8Lzlap70g+oKbMHIgmNz2xLGWnBpIN1OkP2hAlok220e8YbTqbte2g46SBvbi42G8MJ2UKoJyI4m4eVIZOwTaQXpuohrYb8KPA8jHPXKs1rAdhy0WzK6zz51u8NAwrBJ9er5MgHoJ1fkOU+eXLGg61ioROnpITK3NqDguMVg0FD6sujBXXzrba5NYsPJ6S6x2Ozqi8w8YD/aK3AVE2drZK6hssYm0vQKWpJ9JjFeJS8+YKJe82aHJ3WX3hlOo+H7wi67ibzPocQrD88RWC7AGOn14jjvCxiCiiHI10Wz84vHklGfAK/7mS9beS3hIps1+Cc07QMEF45ZfsFTMNWudOEDl40OVU/6nX8c="
    }

    private var dataCaptureContext: DataCaptureContext? = null
    private var barcodeCapture: BarcodeCapture? = null
    private var camera: Camera? = null
    private var dataCaptureView: DataCaptureView? = null
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeAndStartBarcodeScanning()
        observeViewModelData()
    }

    private fun observeViewModelData() {
        viewModel.productResult.observe(this@BarcodeScanActivity) { networkResult ->
            when(networkResult) {
                is NetworkResult.Error -> {
                    dismissScannedCodesDialog()
                    ToastUtils.showToast(applicationContext, networkResult.message ?: getString(R.string.item_found))
                    LogUtils.printError(message = networkResult.message ?: getString(R.string.error_str))
                    barcodeCapture?.isEnabled = true
                }
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Success -> {

                    lifecycleScope.launch(Dispatchers.Main) {
                        ToastUtils.showToast(applicationContext, getString(R.string.item_found))
                        val job = lifecycleScope.launch(Dispatchers.IO) {
                            val productDto = networkResult.data as ProductDto
                            val cartItem = productDto.toCartItem()
                            viewModel.addCartItem(cartItem)
                        }
                        job.join()
                        dismissScannedCodesDialog()
                        barcodeCapture?.isEnabled = true
                        val intent = Intent(this@BarcodeScanActivity, CartActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }
        }

        viewModel.itemAdded.observeOnce(this@BarcodeScanActivity) { itemAdded ->
            if(itemAdded) {
                val intent = Intent(this@BarcodeScanActivity, CartActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun initializeAndStartBarcodeScanning() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY)

        // Use the default camera with the recommended camera settings for the BarcodeCapture mode
        // and set it as the frame source of the context. The camera is off by default and must be
        // turned on to start streaming frames to the data capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
        camera = Camera.getDefaultCamera(BarcodeCapture.createRecommendedCameraSettings())
        camera?.let {
            dataCaptureContext?.setFrameSource(it)
        } ?: throw IllegalStateException("Sample depends on a camera, which failed to initialize.")

        // The barcode capturing process is configured through barcode capture settings
        // which are then applied to the barcode capture instance that manages barcode recognition.
        val barcodeCaptureSettings = BarcodeCaptureSettings()

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires as
        // every additional enabled symbology has an impact on processing times.
        val symbologies = HashSet<Symbology>()
        symbologies.add(Symbology.EAN13_UPCA)
        symbologies.add(Symbology.EAN8)
        symbologies.add(Symbology.UPCE)
        symbologies.add(Symbology.QR)
        symbologies.add(Symbology.DATA_MATRIX)
        symbologies.add(Symbology.CODE39)
        symbologies.add(Symbology.CODE128)
        symbologies.add(Symbology.INTERLEAVED_TWO_OF_FIVE)
        barcodeCaptureSettings.enableSymbologies(symbologies)

        // Some linear/1d barcode symbologies allow you to encode variable-length data.
        // By default, the Scandit Data Capture SDK only scans barcodes in a certain length range.
        // If your application requires scanning of one of these symbologies, and the length is
        // falling outside the default range, you may need to adjust the "active symbol counts"
        // for this symbology. This is shown in the following few lines of code for one of the
        // variable-length symbologies.
        val symbologySettings = barcodeCaptureSettings.getSymbologySettings(Symbology.CODE39)

        val activeSymbolCounts = setOf<Short>(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        symbologySettings.activeSymbolCounts = activeSymbolCounts

        // Create new barcode capture mode with the settings from above.
        barcodeCapture =
            BarcodeCapture.forDataCaptureContext(dataCaptureContext, barcodeCaptureSettings)

        // Register self as a listener to get informed whenever a new barcode got recognized.
        barcodeCapture!!.addListener(this)

        // To visualize the on-going barcode capturing process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext)

        // Add a barcode capture overlay to the data capture view to render the location of captured
        // barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        val overlay = BarcodeCaptureOverlay.newInstance(
            barcodeCapture!!,
            dataCaptureView,
            BarcodeCaptureOverlayStyle.FRAME
        )
        overlay.viewfinder = RectangularViewfinder(RectangularViewfinderStyle.SQUARE)

        // Adjust the overlay's barcode highlighting to match the new viewfinder styles and improve
        // the visibility of feedback. With 6.10 we will introduce this visual treatment as a new
        // style for the overlay.
        val brush = Brush(Color.TRANSPARENT, Color.WHITE, 3f)
        overlay.brush = brush
        binding.frameScanner.addView(dataCaptureView)
    }

    override fun onPause() {
        pauseFrameSource()
        super.onPause()
    }

    override fun onDestroy() {
        barcodeCapture!!.removeListener(this)
        dataCaptureContext!!.removeMode(barcodeCapture!!)
        super.onDestroy()
    }

    private fun pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode capture as well.
        barcodeCapture?.isEnabled = false
        camera!!.switchToDesiredState(FrameSourceState.OFF, null)
    }

    override fun onResume() {
        super.onResume()

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission()
    }

    override fun onCameraPermissionGranted() {
        resumeFrameSource()
    }

    private fun resumeFrameSource() {
        dismissScannedCodesDialog()

        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        barcodeCapture?.isEnabled = true
        camera!!.switchToDesiredState(FrameSourceState.ON, null)
    }

    private fun dismissScannedCodesDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    private fun showResult(result: String) {
        LogUtils.printDebug(result)
        val randomProductId = (0..20).random()
        viewModel.fetchProduct(randomProductId)
        /*val builder = AlertDialog.Builder(this)
        dialog = builder.setCancelable(false)
            .setTitle(result)
            .setPositiveButton(
                "OK"
            ) { _, _ -> barcodeCapture?.isEnabled = true }
            .create()
        dialog!!.show()*/
    }

    override fun onBarcodeScanned(
        barcodeCapture: BarcodeCapture,
        session: BarcodeCaptureSession,
        data: FrameData
    ) {
        if (session.newlyRecognizedBarcodes.isEmpty()) return
        val barcode = session.newlyRecognizedBarcodes[0]

        // Stop recognizing barcodes for as long as we are displaying the result. There won't be any
        // new results until the capture mode is enabled again. Note that disabling the capture mode
        // does not stop the camera, the camera continues to stream frames until it is turned off.
        barcodeCapture.isEnabled = false

        // If you are not disabling barcode capture here and want to continue scanning, consider
        // setting the codeDuplicateFilter when creating the barcode capture settings to around 500
        // or even -1 if you do not want codes to be scanned more than once.

        // Get the human readable name of the symbology and assemble the result to be shown.
        val symbology: String = SymbologyDescription.create(barcode.symbology).readableName
        val result = "Scanned: " + barcode.data + " (" + symbology + ")"
        runOnUiThread { showResult(result) }
    }

    override fun onSessionUpdated(barcodeCapture: BarcodeCapture, session: BarcodeCaptureSession, data: FrameData) {}
    override fun onObservationStarted(barcodeCapture: BarcodeCapture) {}
    override fun onObservationStopped(barcodeCapture: BarcodeCapture) {}
}