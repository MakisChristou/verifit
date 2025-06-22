## Verifit
<img width="100" src="/metadata/logo/icon.svg">
Verifit is a simple minimalist fitness tracker that I made mainly for myself. The UI is heavily inspired by FitNotes. It's purpose is to replace the traditional paper and pencil method for tracking progressive overload. Since I made this with the primary user in mind being myself, it is not fully featured yet, but I do have future plans for adding extra features over time. This is my first attempt so suggestions and code improvements are always welcome.

## Why another fitness tracker?
Verifit stands out as a fitness workout tracker app that prioritizes user freedom and simplicity above all. Unlike its competitors, Verifit is open-source, allowing for community-driven improvements and adaptability. This ad-free app also provides the convenience of both offline usage and the option to host on your own server, ensuring data privacy and control. Moreover, Verifit's online account and seamless export/import of workout data enable smooth transitions between different usage modes. At its core, Verifit is committed to simplicity, with a clean design that focuses on performing a single task exceptionally well, rather than juggling multiple features.

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
* Free online account option.
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
* ~~Free Online Accounts~~

## Screenshots
<img width="200" src="/metadata/screenshots/Screenshot2.png"> <img width="200" src="/metadata/screenshots/Screenshot1.png"> <img width="200" src="/metadata/screenshots/Screenshot3.png"> <img width="200" src="/metadata/screenshots/Screenshot4.png">
<img width="200" src="/metadata/screenshots/Screenshot5.png"> <img width="200" src="/metadata/screenshots/Screenshot6.png"> <img width="200" src="/metadata/screenshots/Screenshot7.png"> <img width="200" src="/metadata/screenshots/Screenshot8.png">

## Libraries Used
* [MPAndroid Chart](https://github.com/PhilJay/MPAndroidChart) for displaying charts
* [Sardine](https://github.com/lookfirst/sardine) for webdav requests
* [Gson](https://github.com/google/gson) for Json parsing and serialization

## Development

### Getting Started
1. Clone the repository
2. Open the project in Android Studio (latest stable version recommended)
3. Sync the project with Gradle files
4. Build and run the app on an emulator or physical device

### Contribution Guidelines
We welcome contributions! Before submitting a pull request, please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Ensure all tests pass
5. Submit a pull request with a clear description of your changes

### Improvement Plan
Check out our [Improvement Plan](Improvement%20Plan.md) for a detailed roadmap of planned enhancements, architectural improvements, and modernization efforts for Verifit. This document outlines our vision for the project's future development.

## Setting up a webdav server
```bash
sudo apt install docker
docker run -d -p 80:80 -v /data/webdav:/var/lib/dav -e USERNAME=user -e PASSWORD=password bytemark/webdav
```

## License
Verifit is open-source software licensed under the [MIT License](LICENSE).
