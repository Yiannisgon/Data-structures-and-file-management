
function createMap(lat, lon, address){
     // Remove existing map if it exists
     if (map) {
        map.destroy();
    }
    // Show the map
    document.getElementById('Map').style.display = 'block';

    //Orismos Marker
    map = new OpenLayers.Map("Map");
    var mapnik         = new OpenLayers.Layer.OSM();
    map.addLayer(mapnik);

    //Orismos Thesis
    function setPosition(lat, lon){
        var fromProjection = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
        var toProjection   = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection
        var position       = new OpenLayers.LonLat(lon, lat).transform( fromProjection, toProjection);
        return position;
    }
    
    //Orismos Handler

    function handler(position, message){
        var popup = new OpenLayers.Popup.FramedCloud("Popup", 
            position, null,
            message, null,
            true // <-- true if we want a close (X) button, false otherwise
        );
        map.addPopup(popup);
    }

    //Markers    
    var markers = new OpenLayers.Layer.Markers( "Markers" );
    map.addLayer(markers);

    //New Marker    
    var position=setPosition(lat,lon);
    var mar=new OpenLayers.Marker(position);
    markers.addMarker(mar);    
    mar.events.register('mousedown', mar, function(evt) { 
        handler(position,address);}
    );

    //Orismos zoom    
    const zoom = 10;
    map.setCenter(position, zoom);
}
// Initialize the map variable
var map = null;