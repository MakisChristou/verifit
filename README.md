## Verifit
<img width="100" src="/metadata/logo/icon.svg">
Verifit is a simple minimalist fitness tracker that I made mainly for myself. The UI is heavily inspired by FitNotes. It's purpose is to replace the traditional paper and pencil method for tracking progressive overload. Since I made this with the primary user in mind being myself, it is not fully featured yet, but I do have future plans for adding extra features over time. This is my first attempt so suggestions and code improvements are always welcome.

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"
      alt="Download from Google Play"
      height="80">](https://play.google.com/store/apps/details?id=com.whatever.verifit)

[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png"
alt="Download from Izzy Droid"
height="80">](https://apt.izzysoft.de/fdroid/index/apk/com.whatever.verifit)

## Features
* Exercise tracking
* Create custom exercises
* Create exercise comments
* Edit, delete exercises
* Import & export from/to phone storage
* Import & export from/to webdav server
* Automatic backups to webdav server
* Basic analysis of workout data
* Visualization of workout data
* Volume, actual 1RM, estimated 1RM, and other stats tracking
* Includes simple rest timer

## Planned features
* Dark Mode
* More detailed stats (e.g. customized per exercise graphs)

## Screenshots
<img width="200" src="/metadata/screenshots/Screenshot2.jpg"> <img width="200" src="/metadata/screenshots/Screenshot1.jpg"> <img width="200" src="/metadata/screenshots/Screenshot3.jpg"> <img width="200" src="/metadata/screenshots/Screenshot4.jpg">
<img width="200" src="/metadata/screenshots/Screenshot5.jpg"> <img width="200" src="/metadata/screenshots/Screenshot6.jpg"> <img width="200" src="/metadata/screenshots/Screenshot7.jpg"> <img width="200" src="/metadata/screenshots/Screenshot8.jpg">
<img width="200" src="/metadata/screenshots/Screenshot9.jpg"> <img width="200" src="/metadata/screenshots/Screenshot10.jpg"> <img width="200" src="/metadata/screenshots/Screenshot11.jpg"> <img width="200" src="/metadata/screenshots/Screenshot12.jpg">
<img width="200" src="/metadata/screenshots/Screenshot13.jpg"> <img width="200" src="/metadata/screenshots/Screenshot14.jpg"> <img width="200" src="/metadata/screenshots/Screenshot15.jpg"> <img width="200" src="/metadata/screenshots/Screenshot16.jpg"> 

## Libraries Used
* [MPAndroid Chart](https://github.com/PhilJay/MPAndroidChart) for displaying charts
* [Sardine](https://github.com/lookfirst/sardine) for webdav requests
* [Gson](https://github.com/google/gson) for Json parsing and serialization
* [Volley](https://github.com/google/volley) for http requests

## Setting up a webdav server
```bash
sudo apt install docker
docker run -d -p 80:80 -v /data/webdav:/var/lib/dav -e USERNAME=user -e PASSWORD=password bytemark/webdav
```



## Donations
### Donate with Monero

42uCPZuxsSS3FNNx6RMDAMVmHVwYBfg3JVMuPKMwadeEfwyykFLkwAH8j4B12ziU7PBCMjLwpPbbDgBw45N4wMpsM3Dy7is

 <img width="200" src="/verifit/src/main/res/drawable/xmr.png">


### Donate with Paypal
[<img src="https://raw.githubusercontent.com/stefan-niedermann/paypal-donate-button/master/paypal-donate-button.png"
alt="Donate with paypal"
height="80">](https://www.paypal.com/donate/?hosted_button_id=YFZX88G8XDSN4)

