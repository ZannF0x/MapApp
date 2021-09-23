package com.zannardyapps.mapapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.zannardyapps.mapapp.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity :
    AppCompatActivity(),
    OnMapReadyCallback,
    //Quando o marker do mapa for clicado, ocorrerá um evento.
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState: Boolean = false

    companion object {
        private const val LOCATION_PERMISSION_CODE  = 1
        private const val REQUEST_CHECK_SETTINGS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // serviços de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Tipo do Mapa customizado
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        locationPermissionRequest()

        /*val newYork = LatLng(40.73, -73.99)
        map.addMarker(MarkerOptions().position(newYork ).title("Favorite City"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(newYork , 12.0f))*/

        // Controle de Zoom
        map.uiSettings.isZoomControlsEnabled = true

        // Quando clicar no Marker vai trazer por padrão dois comportamentos
        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

    // Funções de Menu
     override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.setMyLocalization -> {
                locationPermissionRequest()
                //recreate()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Funções de Permissão, localização de usuário.
    private fun locationPermissionRequest(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE)

        } else {
            //habilitar minha localização
            map.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Se a minha localização for encontrada com sucesso (Em raras situações é null)...

                if (location != null) {
                    // A variável vai receber minha localização solicitada
                    lastLocation = location
                    // variável que recebe a latitude e longitude da minha localização requisitada
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    placeMarkerOnMap(currentLatLng)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
                }
            }
            //Toast.makeText(this, "erro aq", Toast.LENGTH_LONG).show()
        }

    }

    // Função para add uma marker na localização do usuário
    private fun placeMarkerOnMap(location: LatLng){
        val markerOptions: MarkerOptions = MarkerOptions().position(location)

        val titleStr = getAddress(location)
        markerOptions.title(titleStr)

        map.addMarker(markerOptions)
    }

    // Mostrar Endereço do usuário
    private fun getAddress(location: LatLng): String {
        // instancia o Geocoder e retorna uma localização padrão
        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())

        // Lista de Endereços com base na location passada; maxResults = 1 (primeiro Resultado)
        val addresses: List<Address> = geocoder.getFromLocation(
            location.latitude, location.longitude, 1
        )

        val address = addresses[0].getAddressLine(0)
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode

        return address
    }

    /*
    private fun startLocationUpdate(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE)
        } else {
            //fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun createLocationRequest(){
       
    }
     */

}