package com.example.shoppingapp.ui.view.fragment.edit_profile

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingapp.R
import com.example.shoppingapp.constants.BottomNavigationUtils
import com.example.shoppingapp.constants.StringConstants
import com.example.shoppingapp.constants.ToastUtils
import com.example.shoppingapp.constants.ViewPagerUtils
import com.example.shoppingapp.databinding.FragmentMapBinding
import com.example.shoppingapp.ui.viewmodel.edit_profile.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.Style
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: MapViewModel by viewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val viewPager2 by lazy { requireActivity().findViewById<ViewPager2>(R.id.view_pager2) }
    private val bottomNavigationView by lazy {
        requireActivity().findViewById<BottomNavigationView>(
            R.id.bottom_navigation_view
        )
    }
    private lateinit var mapView: MapView
    private lateinit var mapLibreMap: MapLibreMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var marker: Marker? = null
    private val geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }
    private val controller by lazy { findNavController() }
    private val addressDetail by lazy {
        arguments?.getString(StringConstants.ADDRESS_DETAIL)
    }
    private var address: String = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermission: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCurrentUserAddress()
        } else {
            Toast.makeText(
                requireContext(),
                "Vui lòng cấp quyền để sử dụng tính năng",
                Toast.LENGTH_SHORT
            ).show()
            controller.popBackStack()
            ViewPagerUtils.enableViewPager(viewPager2, controller)
            BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e(StringConstants.DEBUG, "onCreateView: mapfrag")
        MapLibre.getInstance(requireContext())
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.apply {

            edtSearch.setText(addressDetail)

            imgBack.setOnClickListener {
                controller.popBackStack()
                ViewPagerUtils.enableViewPager(viewPager2, controller)
                BottomNavigationUtils.showBottomNavigation(bottomNavigationView, controller)
            }

            edtSearch.setOnEditorActionListener { _, actionId, key ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (key != null && key.keyCode ==
                            KeyEvent.KEYCODE_ENTER)
                ) {
                    geocoder.getFromLocationName(edtSearch.text.toString(), 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            showAddressFromLocationName(addresses)
                        }
                    }
                }
                true
            }

            btnSave.setOnClickListener {
                controller.previousBackStackEntry?.savedStateHandle?.set(
                    StringConstants.ADDRESS_DETAIL,
                    address
                )
                controller.popBackStack()
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onMapReady(p0: MapLibreMap) {
        mapLibreMap = p0

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCurrentUserAddress()
        } else {
            requestPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    fun showCurrentUserAddress() {
        mapLibreMap.apply {
            setStyle(
                Style.Builder().fromUri(
                    "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"
                )
            ) {
                //đăng kí lấy vị trí hiện tại của user, nếu ko đăng kí thì ko hiện chấm tròn
                locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.Builder(
                        requireContext(),
                        it
                    ).useDefaultLocationEngine(true).build()
                )
                locationComponent.isLocationComponentEnabled = true
                locationComponent.cameraMode = CameraMode.TRACKING //bám theo user
                locationComponent.renderMode = RenderMode.COMPASS //xoay theo user

                if (addressDetail.isNullOrEmpty()) {
                    //lấy vị trí cuối cùng, ko cần đăng kí cái bên trên cũng được
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                        location?.let { loc ->
                            geocoder.getFromLocation(
                                loc.latitude,
                                loc.longitude,
                                1
                            ) { addresses -> //callback này chạy trong thread khác, phải withContext sang main (nhớ check thread trước khi code)
                                Log.e(
                                    StringConstants.DEBUG,
                                    "showCurrentUserAddress: $addresses và \n ${Thread.currentThread().name}"
                                )
                                viewLifecycleOwner.lifecycleScope.launch {
                                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                                        withContext(Dispatchers.Main) {
                                            Log.e(
                                                StringConstants.DEBUG,
                                                "showCurrentUserAddress: ${Thread.currentThread().name}"
                                            )
                                            showMarker(
                                                LatLng(loc.latitude, loc.longitude),
                                                addresses[0].getAddressLine(0)
                                            )
                                            moveCamera(LatLng(loc.latitude, loc.longitude))
                                        }
                                    }
                                }
                            }
                        }
                    }.addOnFailureListener {
                        ToastUtils.showToast(
                            requireContext(),
                            StringConstants.CURRENT_ADDRESS_ERROR
                        )
                    }
                } else {
                    geocoder.getFromLocationName(addressDetail!!, 1) { addresses ->
                        showAddressFromLocationName(addresses)
                    }
                }


                addOnMapClickListener { latLng ->
                    marker?.let { mapLibreMap.removeMarker(it) }
                    showMarker(latLng, "")
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            address = addresses[0].getAddressLine(0)
                            viewLifecycleOwner.lifecycleScope.launch {
                                repeatOnLifecycle(Lifecycle.State.STARTED) {
                                    withContext(Dispatchers.Main) {
                                        binding.edtSearch.setText(address)
                                    }
                                }
                            }
                        }
                        Log.e(
                            StringConstants.DEBUG,
                            "showCurrentUserAddress: ${Thread.currentThread().name}"
                        )
                    }
                    true
                }

            }

        }

    }

    fun showMarker(latLng: LatLng, detailAddress: String) {
        marker = mapLibreMap.addMarker(
            MarkerOptions().position(latLng)
                .title(StringConstants.YOUR_ADDRESS)
                .snippet(detailAddress)
        )
    }

    fun moveCamera(latLng: LatLng) {
        mapLibreMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(latLng)
                    .zoom(16.0)
                    .tilt(30.0)
                    .build()
            ), 1000
        )
    }

    fun showAddressFromLocationName(addresses: MutableList<Address>) {
        address = addresses[0].getAddressLine(0)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.Main) {
                    val lat = addresses[0].latitude
                    val log = addresses[0].longitude
                    showMarker(LatLng(lat, log), addresses[0].getAddressLine(0))
                    moveCamera(LatLng(lat, log))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}