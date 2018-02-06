import {MdlTextFieldComponent} from '@angular-mdl/core';
import {FormControl} from '@angular/forms';
import {Component, Input, OnInit, ViewChild, NgZone, EventEmitter, Output, ElementRef} from '@angular/core';
import {MapsAPILoader} from '@agm/core';
import {} from 'googlemaps';
import {AgmMarker} from '@agm/core/directives/marker';
import {MapOptions, MarkerOptions} from './maps.model';
import {FtTextFieldComponent} from '../override-mdl/text-field/text-field.component';

@Component({
  moduleId: module.id,
  selector: 'ft-map',
  templateUrl: 'map.component.html',
  styleUrls: ['map.component.scss']
})
export class MapComponent implements OnInit {
  @Input() config: MapOptions;
  @Input() isReadOnly: boolean;

  @Output() markerChanged: EventEmitter<MarkerOptions> = new EventEmitter();

  @ViewChild('search') searchInput: FtTextFieldComponent;
  @ViewChild('marker') marker: AgmMarker;

  private geocoder;

  constructor(private mapsAPILoader: MapsAPILoader,
              private ngZone: NgZone) {
  }

  ngOnInit(): void {
    if (!this.config) {
      this.config = {};
    }
    if (!this.isReadOnly) {
      this.mapsAPILoader.load().then(() => {
        this.geocoder = new google.maps.Geocoder();
        this.setCurrentPosition();
        const autocomplete = new google.maps.places.Autocomplete(this.searchInput.inputEl.nativeElement);
        autocomplete.addListener('place_changed', () => {
          this.ngZone.run(() => {
            const place: google.maps.places.PlaceResult = autocomplete.getPlace();
            if (place.geometry === undefined || place.geometry === null) {
              return;
            }
            this.setMarkerCoords(place.geometry.location.lat(), place.geometry.location.lng(), place.formatted_address);
            this.config.zoom = 15;
          });
        });
      });
    }
  }

  public onMapClick($event) {
    if (!this.isReadOnly) {
      this.geocodeCoordinates($event.coords.lat, $event.coords.lng);
    }
  }

  public onMarkerDragEnd($event) {
    if (!this.isReadOnly) {
      this.geocodeCoordinates($event.coords.lat, $event.coords.lng);
    }
  }

  private geocodeCoordinates(lat: number, lng: number) {
    this.geocoder.geocode({
      location: {
        lat: lat,
        lng: lng
      }
    }, (result, status) => {
      if (status === google.maps.GeocoderStatus.OK) {
        this.setMarkerCoords(lat, lng, result[0].formatted_address);
      }
    });
  }

  private setCurrentPosition() {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition((position) => {
        this.geocodeCoordinates(position.coords.latitude, position.coords.longitude);
        this.config.zoom = 12;
      });
    }
  }

  private setMarkerCoords(latitude: number, longitude: number, label?: string) {
    this.config.latitude = latitude;
    this.config.longitude = longitude;
    this.config.marker = {latitude: latitude, longitude: longitude, label: label};
    this.markerChanged.emit(this.config.marker);
  }
}